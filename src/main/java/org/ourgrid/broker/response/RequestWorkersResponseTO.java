package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.job.JobSpecification;

public class RequestWorkersResponseTO implements IResponseTO {

	
	private static final String RESPONSE_TYPE = BrokerResponseConstants.REQUEST_WORKERS;
	
	private String peerAddress;
	private long requestID;
	private int jobID;
	private int requiredWorkers;
	private int maxFails;
	private int maxReplicas;
	private JobSpecification jobSpec;


	/**
	 * @return the requestID
	 */
	public long getRequestID() {
		return requestID;
	}


	/**
	 * @param requestID the requestID to set
	 */
	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}


	/**
	 * @return the jobID
	 */
	public int getJobID() {
		return jobID;
	}


	/**
	 * @param jobID the jobID to set
	 */
	public void setJobID(int jobID) {
		this.jobID = jobID;
	}


	/**
	 * @return the requiredWorkers
	 */
	public int getRequiredWorkers() {
		return requiredWorkers;
	}


	/**
	 * @param requiredWorkers the requiredWorkers to set
	 */
	public void setRequiredWorkers(int requiredWorkers) {
		this.requiredWorkers = requiredWorkers;
	}


	/**
	 * @return the maxFails
	 */
	public int getMaxFails() {
		return maxFails;
	}


	/**
	 * @param maxFails the maxFails to set
	 */
	public void setMaxFails(int maxFails) {
		this.maxFails = maxFails;
	}


	/**
	 * @return the maxReplicas
	 */
	public int getMaxReplicas() {
		return maxReplicas;
	}


	/**
	 * @param maxReplicas the maxReplicas to set
	 */
	public void setMaxReplicas(int maxReplicas) {
		this.maxReplicas = maxReplicas;
	}


	public String getResponseType() {
		return RESPONSE_TYPE;
	}


	/**
	 * @return the jobSpec
	 */
	public JobSpecification getJobSpec() {
		return jobSpec;
	}


	/**
	 * @param jobSpec the jobSpec to set
	 */
	public void setJobSpec(JobSpecification jobSpec) {
		this.jobSpec = jobSpec;
	}


	/**
	 * @return the peerAddress
	 */
	public String getPeerAddress() {
		return peerAddress;
	}


	/**
	 * @param peerAddress the peerAddress to set
	 */
	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}
	
	
}
