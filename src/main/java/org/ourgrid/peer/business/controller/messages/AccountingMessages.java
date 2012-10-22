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
package org.ourgrid.peer.business.controller.messages;

import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.PeerBalance;

public class AccountingMessages {


	/* ReplicaAccounting Messages*/
	
	/**
	 * @param userDeploymentID
	 * @param requestID
	 * @param workerID
	 * @param accountings
	 * @param accountingState
	 * @param isWorkerLocal
	 * @return
	 */
	public static String getReceivedReplicaAccountingMessage(String userDeploymentID,
			long requestID, String workerID, PeerBalance accountings, 
			GridProcessState accountingState, boolean isWorkerLocal) {
		
		String localWorker = isWorkerLocal ? "local " : "";
		String aborted = GridProcessState.ABORTED.equals(accountingState) ? "n aborted " : " ";
		
		return "Received a" + aborted + "replica accounting from the user [" + userDeploymentID + 
				"] referring to the " + localWorker + "worker [" + workerID + "], on " + "request " + 
				requestID + ": cpu=" + accountings.getAttribute(PeerBalance.CPU_TIME) + ", " + "data=" + 
				accountings.getAttribute(PeerBalance.DATA) + ".";
	}

	/**
	 * @param userDeploymentID
	 * @return
	 */
	public static String getNullReplicaAccountingMessage(String userDeploymentID) {
		return "Ignoring a replica accounting from the user ["+userDeploymentID+
				"], because there is not replica information.";
	}

	/**
	 * @param userPublicKey
	 * @return
	 */
	public static String getNotLoggerUserMessage(String userPublicKey) {
		return "Ignoring a replica accounting from a not logged user. Sender public key: " + userPublicKey;
	}

	/**
	 * @param userPublicKey
	 * @return
	 */
	public static String getUnknownUserReplicaAccountingMessage(String userPublicKey) {
		return "Ignoring a replica accounting from a unknown user with this public key: " + userPublicKey;
	}

	/**
	 * @param userDeploymentID
	 * @param requestID
	 * @return
	 */
	public static String getWrongRequestConsumerMessage(String userDeploymentID,
			long requestID) {
		return "Ignoring a replica accounting from the user ["+userDeploymentID+"], " +
				"because the request "+requestID+" does not belong to him.";
	}

	/**
	 * @param userDeploymentID
	 * @param requestID
	 * @return
	 */
	public static String getInexistentRequestMessage(String userDeploymentID,
			long requestID) {
		return "Ignoring a replica accounting from the user ["+userDeploymentID+"]," +
				" because the request "+requestID+" does not exists.";
	}

	/**
	 * @param userDeploymentID
	 * @return
	 */
	public static String getNullRequestMessage(String userDeploymentID) {
		return "Ignoring a replica accounting from the user ["+userDeploymentID+
				"], because there is not request information.";
	}

	/**
	 * @param userDeploymentID
	 * @return
	 */
	public static String getUserWithoutWorkerMessage(String userDeploymentID) {
		return "Ignoring a replica accounting from the user ["+userDeploymentID+"], " +
				"because there is not worker reference.";
	}

	/**
	 * @param userDeploymentID
	 * @param workerDeploymentID
	 * @param allocableWorker
	 * @return
	 */
	public static String getWorkerNotAllocatedForUserMessage(String userDeploymentID,
			String workerDeploymentID, AllocableWorker allocableWorker) {
		String isLocal = (allocableWorker != null && allocableWorker.isWorkerLocal()) ? "local " : "";
		
		return "Ignoring a replica accounting from the user ["+userDeploymentID+"], because the " +
				isLocal + "worker ["+workerDeploymentID+"] is not allocated for him.";
	}

	/**
	 * @param userDeploymentID
	 * @param workerDeploymentID
	 * @return
	 */
	public static String getNonPositiveDataReplicaAccountingMessage(String userDeploymentID,
			String workerDeploymentID) {
		return "Ignoring a replica accounting from the user ["+userDeploymentID+"] referring to worker " +
				"[" + workerDeploymentID + "], because the DATA accounting must not be negative.";
	}

	/**
	 * @param userDeploymentID
	 * @param workerDeploymentID
	 * @return
	 */
	public static String getNegativeCPUReplicaAccountingMessage(String userDeploymentID,
			String workerDeploymentID) {
		return "Ignoring a replica accounting from the user ["+userDeploymentID+"] referring to worker " +
				"[" + workerDeploymentID + "], because the CPU accounting must be positive.";
	}

	/* WorkAccounting Messages*/
	
	/**
	 * @param accounting
	 * @param accountings
	 * @param wmOID
	 * @return
	 */
	public static String getReceivedWorkAccountingMessage(WorkAccounting accounting, String wmOID) {
		return "Received a work accounting from the worker [" + 
		wmOID + "] " +
		"referring to the consumer with certificate DN: "+ accounting.getConsumerPeerDN() +". " +
		"cpu="+accounting.getAccountings().getAttribute(PeerBalance.CPU_TIME)+", " +
		"data="+accounting.getAccountings().getAttribute(PeerBalance.DATA)+".";
	}
	
	/**
	 * @param workerPublicKey
	 * @return
	 */
	public static String getUnknownWorkerMessage(String workerPublicKey) {
		return "Ignoring a work accounting from an unknown worker with this public key: "+workerPublicKey;
	}

	/**
	 * @param workerManagOID
	 * @return
	 */
	public static String getLocalPeerWorkAccountingMessage(String workerManagOID) {
		return "Ignoring a work accounting from the worker ["+workerManagOID+"] " +
		"referring to this local peer as the consumer";
	}

	/**
	 * @param workerManagOID
	 * @param consumerPeerPublicKey
	 * @return
	 */
	public static String getNonPositiveDataWorkAccountingMessage(
			String workerManagOID, String consumerPeerPublicKey) {
		return "Ignoring a work accounting from the worker ["+workerManagOID+"], " +
				"referring to the consumer with public key: "+consumerPeerPublicKey+", " +
						"because the DATA accounting must not be negative.";
	}

	/**
	 * @param workerManagOID
	 * @param consumerPeerPublicKey
	 * @return
	 */
	public static String getNegativeCPUWorkAccountingMessage(String workerManagOID,
			String consumerPeerPublicKey) {
		return "Ignoring a work accounting from the worker ["+workerManagOID+"], " +
				"referring to the consumer with public key: "+consumerPeerPublicKey+", " +
						"because the CPU accounting must be positive.";
	}

	/**
	 * @param workerManagOID
	 * @return
	 */
	public static String getNoConsumerWorkAccountingMessage(String workerManagOID) {
		return "Ignoring a work accounting with no consumer from worker: ["+workerManagOID+"]";
	}

	/* Save NOF Ranking Messages*/
	
	/**
	 * @return
	 */
	public static String getSaveRankingMessage() {
		return "Saving the Network of favours data.";
	}
	
	/**
	 * @param senderPublicKey
	 * @return
	 */
	public static String getUnknownSenderSaveRankingMessage(String senderPublicKey) {
		return "An unknown sender tried to save the Network of favors data. This message was " +
		"ignored. Sender public key: " + senderPublicKey;
	}
	
	/**
	 * @param remotePeerDN
	 * @return
	 */
	public static String getNotReceivedRemoteWorkerProviderMessage(String remotePeerDN) {
		return "The remote peer with certificate subject DN: " + remotePeerDN + " is not received.";
	}
	
	
	
	
}
