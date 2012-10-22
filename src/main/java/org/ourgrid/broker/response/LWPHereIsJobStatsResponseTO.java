package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.common.internal.IResponseTO;

public class LWPHereIsJobStatsResponseTO implements IResponseTO {
	
	private static final String RESPONSE_TYPE = BrokerResponseConstants.LWP_HERE_IS_JOB_STATS;
	
	private JobStatusInfo jobStatusInfo;

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setJobStatusInfo(JobStatusInfo jobStatusInfo) {
		this.jobStatusInfo = jobStatusInfo;
	}

	public JobStatusInfo getJobStatusInfo() {
		return jobStatusInfo;
	}

}
