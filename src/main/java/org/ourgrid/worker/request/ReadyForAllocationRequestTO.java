package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class ReadyForAllocationRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.READY_FOR_ALLOCATION;
	
	private boolean isWorkerDeployed;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}
	
	public void setWorkerDeployed(boolean isWorkerDeployed) {
		this.isWorkerDeployed = isWorkerDeployed;
	}

	public boolean isWorkerDeployed() {
		return isWorkerDeployed;
	}

}
