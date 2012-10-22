package org.ourgrid.peer.request;

import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.common.internal.IRequestTO;

public class HereIsJobStatsRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.HERE_IS_JOB_STATS;
	
	private JobStatusInfo jobStatusInfo;
	private String myId;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setJobStatusInfo(JobStatusInfo jobStatusInfo) {
		this.jobStatusInfo = jobStatusInfo;
	}

	public JobStatusInfo getJobStatusInfo() {
		return jobStatusInfo;
	}

	public void setMyId(String myId) {
		this.myId = myId;
	}

	public String getMyId() {
		return myId;
	}

}
