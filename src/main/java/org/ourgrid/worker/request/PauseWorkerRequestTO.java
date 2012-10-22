package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;




public class PauseWorkerRequestTO implements IRequestTO {

	
	private static String REQUEST_TYPE = WorkerRequestConstants.PAUSE_WORKER;
	
	
	private boolean componentBeUsed;
	private boolean isThisMyPublicKey;
//	private boolean cancelBeginAllocationAction;
	private String senderPublicKey;
	private String clientAddress;
	private Exception errorCause;
	
	private boolean isRemoteClient = true;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setComponentBeUsed(boolean componentBeUsed) {
		this.componentBeUsed = componentBeUsed;
	}

	public boolean canComponentBeUsed() {
		return componentBeUsed;
	}

	public void setThisMyPublicKey(boolean isThisMyPublicKey) {
		this.isThisMyPublicKey = isThisMyPublicKey;
	}

	public boolean isThisMyPublicKey() {
		return isThisMyPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

//	public void setCancelBeginAllocationAction(boolean cancelBeginAllocationAction) {
//		this.cancelBeginAllocationAction = cancelBeginAllocationAction;
//	}
//
//	public boolean canCancelBeginAllocationAction() {
//		return cancelBeginAllocationAction;
//	}

	public void setErrorCause(Exception errorCause) {
		this.errorCause = errorCause;
	}

	public Exception getErrorCause() {
		return errorCause;
	}

	public void setRemoteClient(boolean isRemoteClient) {
		this.isRemoteClient = isRemoteClient;
	}

	public boolean isRemoteClient() {
		return isRemoteClient;
	}

}
