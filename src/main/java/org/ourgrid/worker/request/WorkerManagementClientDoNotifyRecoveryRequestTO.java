package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class WorkerManagementClientDoNotifyRecoveryRequestTO implements IRequestTO {
	
	private static String REQUEST_TYPE = WorkerRequestConstants.WMC_DO_NOTIFY_RECOVERY;

	private String workerManagementClientAddress;
	private WorkerSpecification workerSpecification;
	
	public void setWorkerManagementClientAddress(String workerManagementClientAddress) {
		this.workerManagementClientAddress = workerManagementClientAddress;
	}

	public String getWorkerManagementClientAddress() {
		return workerManagementClientAddress;
	}
	
	@Override
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public WorkerSpecification getWorkerSpecification() {
		return workerSpecification;
	}

	public void setWorkerSpecification(WorkerSpecification workerSpecification) {
		this.workerSpecification = workerSpecification;
	}
}
