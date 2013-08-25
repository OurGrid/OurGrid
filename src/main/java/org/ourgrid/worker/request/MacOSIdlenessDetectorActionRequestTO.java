package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class MacOSIdlenessDetectorActionRequestTO implements IRequestTO {
	
	private static String REQUEST_TYPE = WorkerRequestConstants.MACOS_IDLENESS_DETECTOR_ACTION;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}
}
