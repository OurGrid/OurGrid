package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;




public class StartWorkerRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = WorkerRequestConstants.START_WORKER;
	
	
	private boolean propertiesCollectorOn;
	private boolean idlenessDetectorOn;
	private boolean idlenessSchedule;
	private String idlenessScheduleTime;
	private long idlenessTime;
	private String masterPeerAddress;


	private boolean isExecutionClientDeployed;


	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public boolean isPropertiesCollectorOn() {
		return propertiesCollectorOn;
	}

	public void setPropertiesCollectorOn(boolean propertiesCollectorOn) {
		this.propertiesCollectorOn = propertiesCollectorOn;
	}

	public boolean isIdlenessDetectorOn() {
		return idlenessDetectorOn;
	}

	public void setIdlenessDetectorOn(boolean idlenessDetectorOn) {
		this.idlenessDetectorOn = idlenessDetectorOn;
	}

	public boolean useIdlenessSchedule() {
		return idlenessSchedule;
	}

	public void setIdlenessSchedule(boolean idlenessSchedule) {
		this.idlenessSchedule = idlenessSchedule;
	}

	public String getIdlenessScheduleTime() {
		return idlenessScheduleTime;
	}

	public void setIdlenessScheduleTime(String idlenessScheduleTime) {
		this.idlenessScheduleTime = idlenessScheduleTime;
	}

	public long getIdlenessTime() {
		return idlenessTime;
	}

	public void setIdlenessTime(long idlenessTime) {
		this.idlenessTime = idlenessTime;
	}

	public boolean isExecutionClientDeployed() {
		return isExecutionClientDeployed;
	}

	public void setExecutionClientDeployed(boolean isExecutionClientDeployed) {
		this.isExecutionClientDeployed = isExecutionClientDeployed;
	}

	public void setMasterPeerAddress(String masterPeerAddress) {
		this.masterPeerAddress = masterPeerAddress;
	}

	public String getMasterPeerAddress() {
		return masterPeerAddress;
	}
}
