package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.controller.messages.WorkerMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.request.PreemptedWorkerRequestTO;
import org.ourgrid.peer.response.LocalPreemptedWorkerResponseTO;
import org.ourgrid.peer.to.RemoteAllocableWorker;
import org.ourgrid.peer.to.Request;

public class PreemptedWorkerRequester implements RequesterIF<PreemptedWorkerRequestTO> {
	
	public List<IResponseTO> execute(PreemptedWorkerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		String remoteWorkerPublicKey = request.getRemoteWorkerPublicKey();
		
		RemoteAllocableWorker remoteAllocableWorker = PeerDAOFactory.getInstance().getAllocationDAO().getRemoteAllocableWorker(
				remoteWorkerPublicKey);
		
		if (remoteAllocableWorker == null) {
			responses.add(new LoggerResponseTO(WorkerMessages.getUnknownOrDisposedPreemptedRemoteWorkerMessage(remoteWorkerPublicKey), 
					LoggerResponseTO.DEBUG));
			
			return responses;
		}
		
		if (remoteAllocableWorker.isDelivered()) {
			LocalPreemptedWorkerResponseTO preemptedTO = new LocalPreemptedWorkerResponseTO();
			preemptedTO.setLwpcAddress(remoteAllocableWorker.getConsumer().getConsumerAddress());
			preemptedTO.setWorkerAddress(remoteAllocableWorker.getWorkerAddress());
				
			responses.add(preemptedTO);
			
			return responses;
		}
		
		String workerAddress = remoteAllocableWorker.getWorkerAddress();
		String workerPubKey = remoteAllocableWorker.getWorkerPubKey();
		
		//Remove and release worker
		WorkerControl.getInstance().removeRemoteWorker(responses, StringUtil.addressToUserAtServer(workerAddress));
		PeerDAOFactory.getInstance().getAllocationDAO().removeRemoteAllocableWorker(workerPubKey);

		ReleaseResponseTO releaseTO = new ReleaseResponseTO();
		releaseTO.setStubAddress(workerAddress);
		
		responses.add(releaseTO);
		
		Request req = remoteAllocableWorker.getRequest();
		
		if (req != null) {
			req.removeAllocableWorker(remoteAllocableWorker);
		}
		
		return responses;
	}
}
