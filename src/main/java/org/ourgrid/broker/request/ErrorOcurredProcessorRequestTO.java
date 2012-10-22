package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.controller.GridProcessError;

public class ErrorOcurredProcessorRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = BrokerRequestConstants.ERROR_OCURRED_PROCESSOR;
	
	
	private String workerAddress;
	private String workerContainerID;
	private GridProcessError gridProcessError;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setGridProcessError(GridProcessError gridProcessError) {
		this.gridProcessError = gridProcessError;
	}

	public GridProcessError getGridProcessError() {
		return gridProcessError;
	}

	public void setWorkerContainerID(String workerContainerID) {
		this.workerContainerID = workerContainerID;
	}

	public String getWorkerContainerID() {
		return workerContainerID;
	}
}