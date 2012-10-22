package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.interfaces.to.JobsPackage;
import org.ourgrid.common.internal.response.AbstractStatusResponseTO;



public class HereIsJobsStatusResponseTO extends AbstractStatusResponseTO {
	
	private final String RESPONSE_TYPE = BrokerResponseConstants.HERE_IS_JOBS_STATUS;
	
	private JobsPackage jobPackage;
	private String myAddress;
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setJobPackage(JobsPackage jobPackage) {
		this.jobPackage = jobPackage;
	}

	public JobsPackage getJobPackage() {
		return jobPackage;
	}

	public void setMyAddress(String myAddress) {
		this.myAddress = myAddress;
	}

	public String getMyAddress() {
		return myAddress;
	}
}
