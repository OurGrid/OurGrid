package org.ourgrid.common.internal.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.OurGridResponseConstants;

/**
 * Requirement 502
 */
public class RegisterInterestResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = OurGridResponseConstants.REGISTER_INTEREST;
	
	
	private String monitorName;
	private Class<?> monitorableType;
	private String monitorableAddress;
	
	private Integer detectionTime = null;
	private Integer heartbeatDelay = null;
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}
	
	public Integer getDetectionTime() {
		return detectionTime;
	}

	public void setDetectionTime(Integer detectionTime) {
		this.detectionTime = detectionTime;
	}

	public Integer getHeartbeatDelay() {
		return heartbeatDelay;
	}

	public void setHeartbeatDelay(Integer heartbeatDelay) {
		this.heartbeatDelay = heartbeatDelay;
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
