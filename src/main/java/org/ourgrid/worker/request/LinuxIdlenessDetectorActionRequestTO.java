package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;




public class LinuxIdlenessDetectorActionRequestTO implements IRequestTO {

	
	private static String REQUEST_TYPE = WorkerRequestConstants.LINUX_IDLENESS_DETECTOR_ACTION;
	
	
	private String xIdleTimeLibPath;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}


	public String getXIdleTimeLibPath() {
		return xIdleTimeLibPath;
	}

	public void setXIdleTimeLibPath(String xIdleTimeLibPath) {
		this.xIdleTimeLibPath = xIdleTimeLibPath;
	}
}
