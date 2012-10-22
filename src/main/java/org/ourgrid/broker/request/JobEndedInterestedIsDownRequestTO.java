package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;




public class JobEndedInterestedIsDownRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.JOB_ENDED_INTERESTED_IS_DOWN;
	
	
	private String interestedDeploymentID;
	private String interesdtedAddress;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}

	public void setInterestedDeploymentID(String interestedDeploymentID) {
		this.interestedDeploymentID = interestedDeploymentID;
	}

	public String getInterestedDeploymentID() {
		return interestedDeploymentID;
	}

	public void setInterestedAddress(String interedtedAddress) {
		this.interesdtedAddress = interedtedAddress;
	}

	public String getInterestedAddress() {
		return interesdtedAddress;
	}
}
