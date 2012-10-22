package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;




public class WorkerClientIsDownRequestTO implements IRequestTO {

	
	private static String REQUEST_TYPE = WorkerRequestConstants.WORKER_CLIENT_IS_DOWN;
	
	
	private String clientAddress;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}
}
