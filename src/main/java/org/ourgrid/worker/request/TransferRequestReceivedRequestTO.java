package org.ourgrid.worker.request;

import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class TransferRequestReceivedRequestTO implements IRequestTO {
	
	private String REQUEST_TYPE = WorkerRequestConstants.TRANSFER_REQUEST_RECEIVED;

	
	private IncomingHandle handle;
	//private String consumerContainerID;
	private String consumerPublicKey;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}


	public void setHandle(IncomingHandle handle) {
		this.handle = handle;
	}

	public IncomingHandle getHandle() {
		return handle;
	}

	/*public void setConsumerContainerID(String consumerContainerID) {
		this.consumerContainerID = consumerContainerID;
	}*/

	/*public String getConsumerContainerID() {
		return consumerContainerID;
	}*/

	public void setConsumerPublicKey(String consumerPublicKey) {
		this.consumerPublicKey = consumerPublicKey;
	}

	public String getConsumerPublicKey() {
		return consumerPublicKey;
	}
}
