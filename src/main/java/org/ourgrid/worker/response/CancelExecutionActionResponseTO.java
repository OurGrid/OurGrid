package org.ourgrid.worker.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;




public class CancelExecutionActionResponseTO implements IResponseTO {
	
	private final static String RESPONSE_TYPE = WorkerResponseConstants.CANCEL_EXECUTION_ACTION;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}
}
