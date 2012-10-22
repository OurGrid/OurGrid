package org.ourgrid.broker.request;

import java.util.List;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.request.AbstractStatusRequestTO;

public class GetJobStatusRequestTO extends AbstractStatusRequestTO {

	
	private final String REQUEST_TYPE = BrokerRequestConstants.GET_JOBS_STATUS;
	
	private List<Integer> jobsIds;
	private String myAddress;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setJobsIds(List<Integer> jobsIds) {
		this.jobsIds = jobsIds;
	}

	public List<Integer> getJobsIds() {
		return jobsIds;
	}

	public void setMyAddress(String myAddress) {
		this.myAddress = myAddress;
	}

	public String getMyAddress() {
		return myAddress;
	}

}