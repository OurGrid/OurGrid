package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.job.JobSpecification;

public class UnwantWorkerResponseTO implements IResponseTO {
	
	
	private static final String RESPONSE_TYPE = BrokerResponseConstants.UNWANT_WORKER;
	
	
	private String peerAddress;
	private String workerAddress;
	private String workerPublicKey;
	private JobSpecification jobSpec;
	private long requestID;
	private int jobID;
	private int requiredWorkers;
	private int maxFails;
	private int maxReplicas;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public long getRequestID() {
		return requestID;
	}

	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}

	public int getJobID() {
		return jobID;
	}

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

	public int getRequiredWorkers() {
		return requiredWorkers;
	}

	public void setRequiredWorkers(int requiredWorkers) {
		this.requiredWorkers = requiredWorkers;
	}

	public int getMaxFails() {
		return maxFails;
	}

	public void setMaxFails(int maxFails) {
		this.maxFails = maxFails;
	}

	public int getMaxReplicas() {
		return maxReplicas;
	}

	public void setMaxReplicas(int maxReplicas) {
		this.maxReplicas = maxReplicas;
	}

	public void setJobSpec(JobSpecification jobSpec) {
		this.jobSpec = jobSpec;
	}

	public JobSpecification getJobSpec() {
		return jobSpec;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}

}