package org.ourgrid.broker.request;

import java.util.List;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;



/**
 * Requirement 302
 */
public class StartBrokerRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.START_BROKER;
	
	
	private boolean isPersistentJobEnable;
	private String jobCounterFilePath;
	private String maxReplicas;
	private String maxFails;
	private String maxBlackListFails;
	private List<String> peersUserAtServer;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}

	public void setPersistentJobEnable(boolean isPersistentJobEnable) {
		this.isPersistentJobEnable = isPersistentJobEnable;
	}

	public boolean isPersistentJobEnable() {
		return isPersistentJobEnable;
	}

	public void setJobCounterFilePath(String jobCounterFilePath) {
		this.jobCounterFilePath = jobCounterFilePath;
	}

	public String getJobCounterFilePath() {
		return jobCounterFilePath;
	}

	public void setMaxReplicas(String maxReplicas) {
		this.maxReplicas = maxReplicas;
	}

	public String getMaxReplicas() {
		return maxReplicas;
	}

	public void setMaxFails(String maxFails) {
		this.maxFails = maxFails;
	}

	public String getMaxFails() {
		return maxFails;
	}

	public void setMaxBlackListFails(String maxBlackListFails) {
		this.maxBlackListFails = maxBlackListFails;
	}

	public String getMaxBlackListFails() {
		return maxBlackListFails;
	}

	public List<String> getPeersUserAtServer() {
		return peersUserAtServer;
	}

	public void setPeersUserAtServer(List<String> peersUserAtServer) {
		this.peersUserAtServer = peersUserAtServer;
	}
	
}
