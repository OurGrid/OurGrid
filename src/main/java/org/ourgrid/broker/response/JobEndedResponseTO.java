package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.internal.IResponseTO;

public class JobEndedResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = BrokerResponseConstants.JOB_ENDED;
	
	
	private int jobID;
	private int state;
	private String interestedID;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}


	public int getJobID() {
		return jobID;
	}

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getInterestedID() {
		return interestedID;
	}

	public void setInterestedID(String interestedID) {
		this.interestedID = interestedID;
	}
}
