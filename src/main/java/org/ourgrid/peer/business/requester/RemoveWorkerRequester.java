package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.OperationSucceedResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.peer.business.controller.messages.PeerControlMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.request.RemoveWorkerRequestTO;
import org.ourgrid.peer.response.StopWorkingResponseTO;
import org.ourgrid.peer.to.LocalAllocableWorker;
import org.ourgrid.peer.to.LocalWorker;

public class RemoveWorkerRequester implements RequesterIF<RemoveWorkerRequestTO> {

	public List<IResponseTO> execute(RemoveWorkerRequestTO request) {

		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		String senderPubKey = request.getSenderPubKey();

		if(request.canComponentBeUsed()) {

			if(!request.isThisMyPublicKey()) {
				responses.add(new LoggerResponseTO(PeerControlMessages.getUnknownSenderSettingWorkersMessage(senderPubKey), LoggerResponseTO.WARN));

				return responses;
			}

			OperationSucceedResponseTO to = new OperationSucceedResponseTO();
			to.setClientAddress(request.getClientAddress());

			try {
				removeWorker(responses, request.getWorkerUserAtServer());
			} catch (IllegalArgumentException e) {
				to.setErrorCause(e);
			}

			responses.add(to);
		}

		return responses;
	}

	public static void removeWorker(List<IResponseTO> responses, String workerUserAtServer) {

		LocalWorker oldLocalWorker = WorkerControl.getInstance().removeLocalWorker(responses, workerUserAtServer);

		if (oldLocalWorker == null) {
			throw new IllegalArgumentException("Worker with address [" + workerUserAtServer + "] does not" +
					" is not registered in the peer.");
		}

		discardWorker(responses, oldLocalWorker);

		String workerManagementAddress = oldLocalWorker.getWorkerManagementAddress();

		ReleaseResponseTO releaseTO = new ReleaseResponseTO();
		releaseTO.setStubAddress(workerManagementAddress);

		responses.add(releaseTO);
	}

	private static void discardWorker(List<IResponseTO> responses, LocalWorker oldLocalWorker) {

		LocalAllocableWorker allocableWorkerToDeallocate = 
				PeerDAOFactory.getInstance().getAllocationDAO().removeLocalAllocableWorker(
						oldLocalWorker.getPublicKey());

		if (allocableWorkerToDeallocate != null) {

			if ( (allocableWorkerToDeallocate.getStatus().isAllocated())) {
				StopWorkingResponseTO to = new StopWorkingResponseTO();
				to.setWmAddress(oldLocalWorker.getWorkerManagementAddress());

				responses.add(to);
			}			

		}
	}
}
