package org.ourgrid.peer.business.requester;

import java.util.List;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.to.LocalAllocableWorker;
import org.ourgrid.peer.to.LocalWorker;

public abstract class AbstractRegisterWorkerRequester<U extends IRequestTO> implements RequesterIF<U> {
	
	protected void registerNewWorker(List<IResponseTO> responses, WorkerSpecification newWorkerSpec, 
			String workerPublicKey, String myUserAtServer) {
		
		newWorkerSpec.removeAttribute(OurGridSpecificationConstants.ATT_PASSWORD);
		newWorkerSpec.putAttribute(OurGridSpecificationConstants.ATT_PROVIDER_PEER, myUserAtServer);
		
		String workerAddress = newWorkerSpec.getServiceID().toString();
		String workerUserAtServer = StringUtil.addressToUserAtServer(workerAddress);
		
		PeerDAOFactory.getInstance().getLocalWorkersDAO().workerIsUp(workerUserAtServer, workerPublicKey);

		if (WorkerControl.getInstance().isNewWorker(responses, StringUtil.addressToUserAtServer(workerAddress))) {
			LocalWorker localWorker = new LocalWorker(newWorkerSpec, workerUserAtServer, workerPublicKey);
			WorkerControl.getInstance().addLocalWorker(responses, localWorker, myUserAtServer);
			
		} else {
			LocalWorker localWorker = WorkerControl.getInstance().getLocalWorker(responses, workerUserAtServer);
			localWorker.setWorkerSpecification(newWorkerSpec);
			localWorker.setStatus(LocalWorkerState.OWNER);
			
			WorkerControl.getInstance().updateWorker(responses, workerUserAtServer, newWorkerSpec.getAttributes(), 
					newWorkerSpec.getAnnotations());
			
			LocalAllocableWorker allocableWorker = PeerDAOFactory.getInstance().getAllocationDAO().getLocalAllocableWorker(localWorker.getPublicKey());
			
			if (allocableWorker != null) {
				allocableWorker.getLocalWorker().setWorkerSpecification(newWorkerSpec);
			}
		}
	}
	
}
