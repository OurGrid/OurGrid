package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;


public class ReportWorkerSpecActionRequestTO implements IRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.REPORT_WORKER_SPEC;
	
	public ReportWorkerSpecActionRequestTO() {}
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}
}
