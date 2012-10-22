package org.ourgrid.worker.request;

import org.ourgrid.common.internal.request.AbstractStatusRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class GetWorkerCompleteStatusRequestTO extends AbstractStatusRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.GET_COMPLETE_STATUS;
	
	
	private long uptime;
	private String configuration;
	private String contextPlaypenDir;
	private String contextStorageDir;
	
	
	public long getUptime() {
		return uptime;
	}

	public void setUptime(long uptime) {
		this.uptime = uptime;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getContextPlaypenDir() {
		return contextPlaypenDir;
	}

	public void setContextPlaypenDir(String contextPlaypenDir) {
		this.contextPlaypenDir = contextPlaypenDir;
	}

	public String getContextStorageDir() {
		return contextStorageDir;
	}

	public void setContextStorageDir(String contextStorageDir) {
		this.contextStorageDir = contextStorageDir;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}
}