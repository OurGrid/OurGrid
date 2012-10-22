package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.JobDAO;
import org.ourgrid.broker.business.dao.PeerEntry;
import org.ourgrid.broker.business.dao.WorkerDAO;
import org.ourgrid.broker.business.messages.LocalWorkerProviderClientMessages;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.HereIsWorkerRequestTO;
import org.ourgrid.broker.response.DisposeWorkerResponseTO;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.StringUtil;

public class HereIsWorkerRequester implements RequesterIF<HereIsWorkerRequestTO> {

	public List<IResponseTO> execute(HereIsWorkerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String senderPublicKey = request.getSenderPublicKey();
		String peerAddress =  request.getPeerAddress();
		
		PeerEntry peerEntry = BrokerDAOFactory.getInstance().getPeerDAO().getPeerEntry(peerAddress);
		
		String workerAddress = request.getWorkerAddress();
		String workerPublicKey = request.getWorkerPublicKey();
		
		WorkerSpecification workerSpec = request.getWorkerSpec();
		RequestSpecification requestSpec = request.getRequestSpec();
		
		if (peerEntry == null) {
			responses.add(new LoggerResponseTO(LocalWorkerProviderClientMessages.getUnknownPeerDeliveredAWorkerMessage(
					workerPublicKey, senderPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (peerEntry.isDown()) {
			responses.add(new LoggerResponseTO(LocalWorkerProviderClientMessages.getDownPeerDeliveredAWorkerMessage(
					workerPublicKey, senderPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (peerEntry.isUp()) {
			responses.add(new LoggerResponseTO(LocalWorkerProviderClientMessages.getNotLoggedPeerDeliveredAWorkerMessage(
					workerPublicKey, senderPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		WorkerDAO workerDAO = BrokerDAOFactory.getInstance().getWorkerDAO();
		workerDAO.addWorker(workerAddress, workerSpec);

		if (requestSpec == null) {
			responses.add(new LoggerResponseTO(LocalWorkerProviderClientMessages.nullRequestMessage(
					workerPublicKey, senderPublicKey), LoggerResponseTO.WARN));
			
			DisposeWorkerResponseTO disposeWorkerResponseTO = new DisposeWorkerResponseTO();
			disposeWorkerResponseTO.setPeerAddress(peerAddress);
			disposeWorkerResponseTO.setWorkerAddress(workerAddress);
			disposeWorkerResponseTO.setWorkerPublicKey(workerPublicKey);
			
			responses.add(disposeWorkerResponseTO);
			
			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(workerAddress);
			
			responses.add(releaseTO);
			
			return responses;
		}	
		
		JobDAO jobDAO = BrokerDAOFactory.getInstance().getJobDAO();
		
		int jobID = jobDAO.getJobForThisRequest(requestSpec.getRequestId());
		
		if (jobID == -1) {
			responses.add(new LoggerResponseTO(LocalWorkerProviderClientMessages.getBrokerWithoutRunningJobsReceivingWorkerMessage(
					workerPublicKey, senderPublicKey), LoggerResponseTO.WARN));
			
			DisposeWorkerResponseTO disposeWorkerResponseTO = new DisposeWorkerResponseTO();
			disposeWorkerResponseTO.setPeerAddress(peerAddress);
			disposeWorkerResponseTO.setWorkerAddress(workerAddress);
			
			responses.add(disposeWorkerResponseTO);
			
			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(workerAddress);
			
			responses.add(releaseTO);
			
			return responses;
		}
		
		jobDAO.addWorker2Job(StringUtil.addressToContainerID(workerAddress), jobID);
		
		if (jobID > -1) {
			
			SchedulerIF scheduler = jobDAO.getJobScheduler(jobID);
			scheduler.hereIsWorker(request.getWorkerID(), 
					workerPublicKey, senderPublicKey, request.getPeerAddress(),
					requestSpec, workerSpec, responses);
		
		}
		
		return responses;
	}
}
