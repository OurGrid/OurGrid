package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;




public class WorkerSpecBasedIdlenessDetectorActionRequestTO implements IRequestTO {

	
	private static String REQUEST_TYPE = WorkerRequestConstants.WORKER_SPEC_BASED_IDLENESS_DETECTOR_ACTION;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}
}
