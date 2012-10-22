package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;




public class UpdateTransferProgressRequestTO implements IRequestTO {

	
	private static String REQUEST_TYPE = WorkerRequestConstants.UPDATE_TRANSFER_PROGRESS;
	
	
	private long amountWritten;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setAmountWritten(long amountWritten) {
		this.amountWritten = amountWritten;
	}

	public long getAmountWritten() {
		return amountWritten;
	}
}
