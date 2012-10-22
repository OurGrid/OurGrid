/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.peer.business.controller.accounting;

import java.util.List;

import org.ourgrid.common.interfaces.to.Accounting;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.statistics.beans.peer.Job;
import org.ourgrid.common.statistics.beans.status.ExecutionStatus;
import org.ourgrid.common.statistics.control.AccountingControl;
import org.ourgrid.common.statistics.control.JobControl;
import org.ourgrid.common.statistics.control.UserControl;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.peer.business.controller.messages.AccountingMessages;
import org.ourgrid.peer.business.dao.AccountingDAO;
import org.ourgrid.peer.business.dao.LocalWorkersDAO;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.UsersDAO;
import org.ourgrid.peer.communication.dao.ScheduledRequestDAO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.peer.to.PeerBalance;
import org.ourgrid.reqtrace.Req;

/**
 * Performs accounting actions
 */
@Req("REQ027")
public class AccountingController {
	
	private static final long serialVersionUID = 1L;

	private static AccountingController instance = null;
	
	public static AccountingController getInstance() {
		if (instance == null) {
			instance = new AccountingController();
		}
		return instance;
	}
	
	private AccountingController() {}
	
	/**
	 * Validates if the sender can report the replica accounting and if the 
	 * accounting data is valid. Call the DAO to store the replica accounting 
	 * data.
	 * 
	 * The sender must have its public key already saved in the local users 
	 * set.
	 * 
	 * The replica accounting must contain the request specification and a
	 * valid allocable worker.
	 * 
	 * The request must be running in this peer.
	 * 
	 * @param userPublicKey The sender public key
	 * @param replicaAccounting The replica accounting data
	 * 
	 * @see UsersDAO#getUserByPublicKey(String)
	 * @see ScheduledRequestDAO#isRunning(RequestSpecification)
	 * @see AccountingDAO#addReplicaAccounting(Accounting)
	 */
	public void reportReplicaAccounting(List<IResponseTO> responses, 
			GridProcessAccounting replicaAccounting, String userPublicKey) {
		
		UsersDAO usersDAO = PeerDAOFactory.getInstance().getUsersDAO();
		
		if (! UserControl.getInstance().userExists(responses, userPublicKey)) {
			
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getUnknownUserReplicaAccountingMessage(userPublicKey), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return;
		}
		
		if (!usersDAO.isLoggedUser(userPublicKey)) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getNotLoggerUserMessage(userPublicKey), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return;
		}
			
		String userAddress = usersDAO.getLoggedUser(userPublicKey).getWorkerProviderClientAddress();
		
		if (replicaAccounting == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getNullReplicaAccountingMessage(userAddress), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return;
		}
	
		long requestId = replicaAccounting.getRequestId();
		
		if (!validateRequestSpecification(responses, userPublicKey, userAddress, requestId)) {
			return;
		}
		
		if (replicaAccounting.getWorkerID() == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getUserWithoutWorkerMessage(userAddress), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return;
		}

		String workerAddress = replicaAccounting.getWorkerID();
		String workerPublicKey = replicaAccounting.getWorkerPublicKey();
		
		AllocableWorker allocableWorker = validateWorker(responses, userPublicKey, 
				userAddress, workerAddress, workerPublicKey);
		if (allocableWorker == null) {
			return;
		}

		PeerBalance accountings = replicaAccounting.getAccountings();
		if (!validateAccountings(responses, userAddress, workerAddress, accountings)) {
			return;
		}
		
		LoggerResponseTO loggerResponse = new LoggerResponseTO(
				AccountingMessages.getReceivedReplicaAccountingMessage(
						userAddress, requestId, workerAddress,
						accountings, replicaAccounting.getState(), allocableWorker.isWorkerLocal()), 
				LoggerResponseTO.DEBUG);
		responses.add(loggerResponse);
		
//		DeploymentID wpID = null;
//		Object workerProvider = allocableWorker.getWorkerProvider();
		
//		if (allocableWorker.isWorkerLocal()) {
//			wpID = serviceManager.getLocalDeploymentID(workerProvider);
//		} else {
//			wpID = serviceManager.getStubDeploymentID(workerProvider);
//		}
		
		JobControl.getInstance().addProcessAccounting(responses, replicaAccounting);
		
		AccountingControl.getInstance().addReplicaAccounting(responses, replicaAccounting, 
				allocableWorker.getProviderCertificateDN());
		
	}

	private boolean validateRequestSpecification(List<IResponseTO> responses, String userPublicKey, String userDeploymentID, long requestId) {

//		if (requestSpec == null) {
//			LoggerResponseTO loggerResponse = new LoggerResponseTO(
//					AccountingMessages.getNullRequestMessage(userDeploymentID), 
//					LoggerResponseTO.WARN);
//			responses.add(loggerResponse);
//			
//			return false;
//		}

		Job job = JobControl.getInstance().findByRequestId(responses, requestId);
		
		if (job == null || (job.getStatus() != null && job.getStatus().equals(ExecutionStatus.FINISHED))) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getInexistentRequestMessage(userDeploymentID, requestId), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return false;
		}
		
		if (!job.getLogin().getUser().getPublicKey().equals(userPublicKey)) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getWrongRequestConsumerMessage(userDeploymentID, requestId), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return false;
		}
		
		return true;
	}

	private AllocableWorker validateWorker(List<IResponseTO> responses, String userPublicKey, 
			String userAddress, String workerAddress, String workerPublicKey) {
		if (workerAddress == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getUserWithoutWorkerMessage(userAddress), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return null;
		}
		
		AllocableWorker allocableWorker = PeerDAOFactory.getInstance().getAllocationDAO().getAllocableWorker(workerPublicKey);
		
		String publicKey = "";
		if (allocableWorker != null) {
			publicKey = allocableWorker.getConsumerPublicKey();
		}
		
		if (allocableWorker == null || !userPublicKey.equals(publicKey) || !allocableWorker.isDelivered()) {
			
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getWorkerNotAllocatedForUserMessage(userAddress, workerAddress,
							allocableWorker), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return null;
		}
		return allocableWorker;
	}

	private boolean validateAccountings(List<IResponseTO> responses, String userAddress, 
			String workerAddress, PeerBalance accountings) {
		
		if (accountings.getAttribute(PeerBalance.CPU_TIME) < 0) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getNegativeCPUReplicaAccountingMessage(userAddress, workerAddress), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return false;
		}
		
		if (accountings.getAttribute(PeerBalance.DATA) < 0) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getNonPositiveDataReplicaAccountingMessage(userAddress, workerAddress), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return false;
		}
		
		return true;
	}


	/**
	 * Validate if the sender can report the work accountings, calculate the
	 * new balances for the remote peers that consumed the worker, and call the
	 * DAO to update the remote peers balance.
	 * 
	 * The sender must have its public key already saved in the local workers
	 * set.
	 * 
	 * The AccountingEvaluator is responsible for calculating the balance from
	 * the accounting data.
	 * 
	 * @param workerPublicKey The sender public key
	 * @param consumersAccountings A list of accountings for the remote peers 
	 * 			that consumed the worker
	 * 
	 * @see LocalWorkersDAO#getRecoveredWorker(String)
	 * @see AccountingDAO#getRemotePeerBalance(String)
	 * @see AccountingDAO#setRemotePeerBalance(String, Double)
	 */
	public void reportWorkAccounting(List<IResponseTO> responses, List<WorkAccounting> consumersAccountings, 
			String workerPublicKey, String workerAddress, String workerUserAtServer, 
			String myPublicKey, String myCertSubjectDN) {
		
		LocalWorker localWorker = WorkerControl.getInstance().getLocalWorker(
				responses, workerUserAtServer);
				
		if (!validateWorker(responses, workerPublicKey, localWorker)) {
			return;
		}
		
		double accumCpuTime = 0;
		double accumDataStored = 0;
		
		for (WorkAccounting accounting : consumersAccountings) {

			String wmAddress = localWorker.getWorkerManagementAddress();
			
			if (!validateAccounting(responses, accounting, wmAddress, myCertSubjectDN)) {
				continue;
			}
			
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getReceivedWorkAccountingMessage(accounting, wmAddress), 
					LoggerResponseTO.DEBUG);
			responses.add(loggerResponse);
			
			commitReceivedFavour(responses, accounting, myCertSubjectDN);
			
			accumCpuTime += accounting.getAccountings().getCPUTime();
			accumDataStored += accounting.getAccountings().getData();
		}
		
		WorkerControl.getInstance().updateWorkAccounting(responses, localWorker.getWorkerUserAtServer(), 
				accumCpuTime, accumDataStored);
	}

	private boolean validateAccounting(List<IResponseTO> responses, WorkAccounting accounting, 
			String workerManagAdress, String myCertSubjectDN) {
		String consumerPeerDN = accounting.getConsumerPeerDN();
		
		if (consumerPeerDN == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getNoConsumerWorkAccountingMessage(workerManagAdress), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return false;
		}
		
		if (accounting.getAccountings().getAttribute(PeerBalance.CPU_TIME) <= 0) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getNegativeCPUWorkAccountingMessage(workerManagAdress,
							consumerPeerDN), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return false;
		}
		
		if (accounting.getAccountings().getAttribute(PeerBalance.DATA) < 0) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getNonPositiveDataWorkAccountingMessage(workerManagAdress,
							consumerPeerDN), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return false;
		}
		
		if (myCertSubjectDN.equals(consumerPeerDN)) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getLocalPeerWorkAccountingMessage(workerManagAdress), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			
			return false;
		}
		
		return true;
	}

	private boolean validateWorker(List<IResponseTO> responses, String workerPublicKey, LocalWorker localWorker) {
		if (localWorker == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getUnknownWorkerMessage(workerPublicKey), 
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			return false;
		}
		
		return true;
	}

	private void commitReceivedFavour(List<IResponseTO> responses, WorkAccounting accounting, String myCertSubjectDN) {
		String remoteDNData = accounting.getConsumerPeerDN();
		
		PeerBalance oldBalance = null;
		if (remoteDNData != null) {
			oldBalance = AccountingControl.getInstance().getRemotePeerBalance(
					responses, myCertSubjectDN, remoteDNData);
		}	
		
		if (oldBalance == null) {
			oldBalance = new PeerBalance(0., 0.);
		}
		
		double cpu = Math.max(oldBalance.getCPUTime() - accounting.getAccountings().getCPUTime(), 0);
		double data = Math.max(oldBalance.getData() - accounting.getAccountings().getData(), 0);
		AccountingControl.getInstance().setRemotePeerBalance(
				responses, myCertSubjectDN, remoteDNData, 
				new PeerBalance(cpu, data));
	}

	/**
	 * Validates if the sender can save the balance and call the DAO to save it.
	 * 
	 * The sender (timer or component) must have the save key pair of the
	 * current module.
	 * 
	 * @param senderPublicKey The sender public key
	 */
	public void saveRanking(List<IResponseTO> responses, boolean isThisMyPublicKey, String senderPublicKey, String rankingFilePath) {
		if (!isThisMyPublicKey) {
			responses.add(new LoggerResponseTO(AccountingMessages.getUnknownSenderSaveRankingMessage(
					senderPublicKey), LoggerResponseTO.WARN));
			
			return;
		}
		
		responses.add(new LoggerResponseTO(AccountingMessages.getSaveRankingMessage(), LoggerResponseTO.DEBUG));

		PeerDAOFactory.getInstance().getAccountingDAO().saveBalancesRanking(rankingFilePath);
	}
}