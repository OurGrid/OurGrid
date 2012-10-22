package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class StopWorkingRequestTO implements IRequestTO {
	
	
	private static String REQUEST_TYPE = WorkerRequestConstants.STOP_WORKING;

	
	private String senderPublicKey;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

}
