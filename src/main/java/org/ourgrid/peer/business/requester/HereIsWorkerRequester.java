package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.controller.messages.WorkerMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.AllocationDAO;
import org.ourgrid.peer.request.HereIsWorkerRequestTO;
import org.ourgrid.peer.to.RemoteAllocableWorker;


public class HereIsWorkerRequester implements RequesterIF<HereIsWorkerRequestTO> {

	public List<IResponseTO> execute(HereIsWorkerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String senderPublicKey = request.getSenderPublicKey();
		String providerAddress = request.getProviderAddress();
		String workerAddress = request.getWorkerAddress();
		WorkerSpecification workerSpec = request.getWorkerSpec();
		
		if (providerAddress == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getReceivingNullRemoteProviderMessage(senderPublicKey), LoggerResponseTO.WARN));
			return responses;
		}
		
		if (workerAddress == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getReceivingNullRemoteWorkerMessage(
					StringUtil.addressToContainerID(providerAddress)), LoggerResponseTO.WARN));
			
			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(providerAddress);
			
			responses.add(releaseTO);
			
			return responses;
		}
		
		if (workerSpec == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getReceivingNullWorkerSpecRemoteWorkerMessage(
					StringUtil.addressToContainerID(providerAddress)), LoggerResponseTO.WARN));
			
			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(providerAddress);
			
			responses.add(releaseTO);
			
			ReleaseResponseTO releaseTO2 = new ReleaseResponseTO();
			releaseTO2.setStubAddress(workerAddress);
			
			responses.add(releaseTO2);
			
			return responses;
		}
		
		AllocationDAO allocationDAO = PeerDAOFactory.getInstance().getAllocationDAO();
		
		RemoteAllocableWorker rAlloc = new RemoteAllocableWorker(workerAddress, request.getWorkerClientAddress(), providerAddress, 
				request.getProviderCertSubjectDN(), workerSpec);
		
		allocationDAO.addNotRecoveredRemoteAllocableWorker(workerAddress, rAlloc);
		
		RegisterInterestResponseTO registerInterestResponse = new RegisterInterestResponseTO();
		registerInterestResponse.setMonitorableAddress(workerAddress);
		registerInterestResponse.setMonitorableType(RemoteWorkerManagement.class);
		registerInterestResponse.setMonitorName(PeerConstants.REMOTE_WORKER_MANAGEMENT_CLIENT);
		responses.add(registerInterestResponse);
		
		return responses;
	}
}
