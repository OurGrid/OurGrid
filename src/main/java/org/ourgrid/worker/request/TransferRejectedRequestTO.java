package org.ourgrid.worker.request;

import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class TransferRejectedRequestTO implements IRequestTO {
	
	private String REQUEST_TYPE = WorkerRequestConstants.TRANSFER_REJECTED;

	
	private OutgoingHandle handle;
	//private String consumerContainerID;
	private String senderPublicKey;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}


	public void setHandle(OutgoingHandle handle) {
		this.handle = handle;
	}

	public OutgoingHandle getHandle() {
		return handle;
	}

	/*public void setConsumerContainerID(String consumerContainerID) {
		this.consumerContainerID = consumerContainerID;
	}*/

	/*public String getConsumerContainerID() {
		return consumerContainerID;
	}*/

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}
}
