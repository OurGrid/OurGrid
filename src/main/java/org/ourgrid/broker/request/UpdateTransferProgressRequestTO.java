package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.broker.business.scheduler.extensions.GenericTransferProgress;
import org.ourgrid.common.internal.IRequestTO;




public class UpdateTransferProgressRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.UPDATE_TRANSFER_PROGRESS;
	
	private String senderAddress;
	private GenericTransferProgress transferProgress;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setTransferProgress(GenericTransferProgress transferProgress) {
		this.transferProgress = transferProgress;
	}

	public GenericTransferProgress getTransferProgress() {
		return transferProgress;
	}
}
