package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.OperationSucceedResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.request.AddAnnotationsWorkersRequestTO;
import org.ourgrid.peer.to.LocalAllocableWorker;
import org.ourgrid.peer.to.LocalWorker;

public class AddAnnotationsWorkersRequester implements RequesterIF<AddAnnotationsWorkersRequestTO> {


	public List<IResponseTO> execute(AddAnnotationsWorkersRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if ( request.canComponentBeUsed()) {

			// TODO Not sure if we can use like this...
			// In this way, I would like to allow anyone to update the peer's
			// work.
			// if(!getServiceManager().isThisMyPublicKey(senderPubKey)) {
			// getServiceManager().getLog().warn(PeerControlMessages.getUnknownSenderSettingWorkersMessage(senderPubKey));
			// return;
			// }

			updateWorkersWithAnnotations( responses, request.getNewWorkersAnnotations() );
			OperationSucceedResponseTO to = new OperationSucceedResponseTO();
			to.setClientAddress(request.getClientAddress());
		}
		
		return responses;
	}
	
	private void updateWorkersWithAnnotations(List<IResponseTO> responses, Collection< WorkerSpecification > workersWithTags ) {

		for ( WorkerSpecification workerSpec : workersWithTags ) {
			updateWorkerWithAnnotations( responses, workerSpec );
		}

	}
	
	private void updateWorkerWithAnnotations( List<IResponseTO> responses, WorkerSpecification workerSpecWithNewAnnotations ) {

		// TODO The name of these methods are bad. Actually, this method checks
		// if a worker does not exist. It should be changed to doesWorkerExist
		// and then every call for isNewWorker be replaced to !doesWorkExist
		
		String workerUserAtServer = workerSpecWithNewAnnotations.getUserAndServer();
		
		if ( WorkerControl.getInstance().isNewWorker( responses, workerUserAtServer)) {
			return;
			// TODO Should We send some warn to up methods?
		}
	
		LocalWorker localWorker = WorkerControl.getInstance()
												.getLocalWorker(  responses, workerUserAtServer );
		WorkerSpecification newWorkerSpec = localWorker.getWorkerSpecification();

		newWorkerSpec.addAllAnnotations( workerSpecWithNewAnnotations.getAnnotations() );

		WorkerControl.getInstance().updateWorker( responses, workerUserAtServer, newWorkerSpec.getAttributes(),
				newWorkerSpec.getAnnotations());
		
		LocalAllocableWorker allocableWorker = PeerDAOFactory.getInstance().getAllocationDAO().getLocalAllocableWorker(localWorker.getPublicKey());
		
		// If there was no request, no allocable worker was created
		if ( allocableWorker != null ) {
			allocableWorker.getLocalWorker().setWorkerSpecification( newWorkerSpec );
		}
		// TODO Should We avail the alocableWorker issues?
		
	}


}
