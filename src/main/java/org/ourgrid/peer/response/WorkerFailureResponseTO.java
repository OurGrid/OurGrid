package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class WorkerFailureResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.WORKER_FAILURE;
	
	
	private String monitorName;
	private Class<?> monitorableType;
	private String monitorableAddress;
	private String wmAddress;
	private String serviceName;
	
	
	/**
	 * @return the wmAddress
	 */
	public String getWmAddress() {
		return wmAddress;
	}


	/**
	 * @param wmAddress the wmAddress to set
	 */
	public void setWmAddress(String wmAddress) {
		this.wmAddress = wmAddress;
	}


	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}


	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	public String getResponseType() {
		return RESPONSE_TYPE;
	}


	public String getMonitorName() {
		return monitorName;
	}

	public void setMonitorName(String monitorName) {
		this.monitorName = monitorName;
	}

	public Class<?> getMonitorableType() {
		return monitorableType;
	}

	public void setMonitorableType(Class<?> monitorableType) {
		this.monitorableType = monitorableType;
	}

	public String getMonitorableAddress() {
		return monitorableAddress;
	}

	public void setMonitorableAddress(String monitorableAddress) {
		this.monitorableAddress = monitorableAddress;
	}
}
