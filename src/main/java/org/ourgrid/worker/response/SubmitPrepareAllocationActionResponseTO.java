package org.ourgrid.worker.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;




public class SubmitPrepareAllocationActionResponseTO implements IResponseTO {
	
	private final static String RESPONSE_TYPE = WorkerResponseConstants.SUBMIT_PREPARE_ALLOCATION_ACTION;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}
}
