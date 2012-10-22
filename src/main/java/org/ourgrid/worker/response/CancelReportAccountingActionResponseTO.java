package org.ourgrid.worker.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;




public class CancelReportAccountingActionResponseTO implements IResponseTO {
	
	private final static String RESPONSE_TYPE = WorkerResponseConstants.CANCEL_REPORT_ACCOUNTING_ACTION;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}
}
