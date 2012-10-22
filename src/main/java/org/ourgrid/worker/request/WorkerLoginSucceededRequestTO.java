package org.ourgrid.worker.request;

import org.ourgrid.common.WorkerLoginResult;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class WorkerLoginSucceededRequestTO implements IRequestTO {

	private final String REQUEST_TYPE = WorkerRequestConstants.WORKER_LOGIN_SUCCEEDED;
	private WorkerLoginResult result;
	private String senderPublicKey;
	private boolean idlenessDetectorOn;
	private long workerSpecReportTime;
	private boolean workerSpecReportPropOn;
	
	@Override
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setResult(WorkerLoginResult result) {
		this.result = result;
	}

	public WorkerLoginResult getResult() {
		return result;
	}

	public boolean isIdlenessDetectorOn() {
		return idlenessDetectorOn;
	}

	public void setIdlenessDetectorOn(boolean idlenessDetectorOn) {
		this.idlenessDetectorOn = idlenessDetectorOn;
	}

	public long getWorkerSpecReportTime() {
		return workerSpecReportTime;
	}

	public void setWorkerSpecReportTime(long workerSpecReportTime) {
		this.workerSpecReportTime = workerSpecReportTime;
	}

	public boolean isWorkerSpecReportPropOn() {
		return workerSpecReportPropOn;
	}

	public void setWorkerSpecReportPropOn(boolean workerSpecReportPropOn) {
		this.workerSpecReportPropOn = workerSpecReportPropOn;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

}
