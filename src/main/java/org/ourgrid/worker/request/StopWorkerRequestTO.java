package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;




public class StopWorkerRequestTO implements IRequestTO {

	
	private static String REQUEST_TYPE = WorkerRequestConstants.STOP_WORKER;
	
	
	private boolean componentBeUsed;
	private boolean stopSenderPublicKeyValid;
	private String stopSenderPublicKey;

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setComponentBeUsed(boolean componentBeUsed) {
		this.componentBeUsed = componentBeUsed;
	}

	public boolean canComponentBeUsed() {
		return componentBeUsed;
	}

	public void setStopSenderPublicKeyValid(boolean stopSenderPublicKeyValid) {
		this.stopSenderPublicKeyValid = stopSenderPublicKeyValid;
	}

	public boolean isStopSenderPublicKeyValid() {
		return stopSenderPublicKeyValid;
	}

	public void setStopSenderPublicKey(String stopSenderPublicKey) {
		this.stopSenderPublicKey = stopSenderPublicKey;
	}

	public String getStopSenderPublicKey() {
		return stopSenderPublicKey;
	}
}
