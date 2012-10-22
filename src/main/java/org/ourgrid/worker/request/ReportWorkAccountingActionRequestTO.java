package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;


public class ReportWorkAccountingActionRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.REPORT_WORK_ACCOUNTING;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}
}
