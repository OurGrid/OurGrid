package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.specification.job.JobSpecification;

/**
 * Transfer object for adding job messages.
 */
public class AddJobRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.ADD_JOB;
	
	
	private String senderPublicKey;
	private boolean isThisMyPublicKey;
	private boolean canComponentBeUsed;
	private String maxReplicas;
	private String maxFails;
	private JobSpecification jobSpec;
	private String brokerControlClientAddress;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public boolean isThisMyPublicKey() {
		return isThisMyPublicKey;
	}

	public void setThisMyPublicKey(boolean isThisMyPublicKey) {
		this.isThisMyPublicKey = isThisMyPublicKey;
	}

	public boolean canComponentBeUsed() {
		return canComponentBeUsed;
	}

	public void setCanComponentBeUsed(boolean canComponentBeUsed) {
		this.canComponentBeUsed = canComponentBeUsed;
	}

	public String getMaxReplicas() {
		return maxReplicas;
	}

	public void setMaxReplicas(String maxReplicas) {
		this.maxReplicas = maxReplicas;
	}

	public String getMaxFails() {
		return maxFails;
	}

	public void setMaxFails(String maxFails) {
		this.maxFails = maxFails;
	}

	public void setJobSpec(JobSpecification jobSpec) {
		this.jobSpec = jobSpec;
	}

	public JobSpecification getJobSpec() {
		return jobSpec;
	}

	public void setBrokerControlClientAddress(String brokerControlClientAddress) {
		this.brokerControlClientAddress = brokerControlClientAddress;
	}

	public String getBrokerControlClientAddress() {
		return brokerControlClientAddress;
	}
}
