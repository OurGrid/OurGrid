package org.ourgrid.worker.request;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;




public class WorkerManagementClientDoNotifyFailureRequestTO implements IRequestTO {

	
	private static String REQUEST_TYPE = WorkerRequestConstants.WMC_DO_NOTIFY_FAILURE;
	

	private String monitorablePublicKey;
	private String monitorableID;
	private String monitorableAddress;
	

	public String getMonitorablePublicKey() {
		return monitorablePublicKey;
	}

	public void setMonitorablePublicKey(String monitorablePublicKey) {
		this.monitorablePublicKey = monitorablePublicKey;
	}

	public String getMonitorableID() {
		return monitorableID;
	}

	public void setMonitorableID(String monitorableID) {
		this.monitorableID = monitorableID;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setMonitorableAddress(String monitorableAddress) {
		this.monitorableAddress = monitorableAddress;
	}

	public String getMonitorableAddress() {
		return monitorableAddress;
	}

}
