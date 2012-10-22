package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;




public class WorkForBrokerRequestTO implements IRequestTO {

	
	private static String REQUEST_TYPE = WorkerRequestConstants.WORK_FOR_BROKER;
	
	
	private String senderPublicKey;
	private String brokerPublicKey;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}


	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setBrokerPublicKey(String brokerPublicKey) {
		this.brokerPublicKey = brokerPublicKey;
	}

	public String getBrokerPublicKey() {
		return brokerPublicKey;
	}

}
