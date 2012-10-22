package org.ourgrid.peer.request;


import java.util.List;

import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.common.internal.IRequestTO;

public class ReportWorkAccountingRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.REPORT_WORK_ACCOUNTING;
	
	private String workerPublicKey;
	private String workerAddress; 
	private List<WorkAccounting> accountings;
	private String workerUserAtServer; 
	private String myPublicKey;
	private String myCertSubjectDN;
	
	public String getWorkerUserAtServer() {
		return workerUserAtServer;
	}

	public void setWorkerUserAtServer(String workerUserAtServer) {
		this.workerUserAtServer = workerUserAtServer;
	}

	public String getMyPublicKey() {
		return myPublicKey;
	}

	public void setMyPublicKey(String myPublicKey) {
		this.myPublicKey = myPublicKey;
	}

	public String getMyCertSubjectDN() {
		return myCertSubjectDN;
	}

	public void setMyCertSubjectDN(String myCertSubjectDN) {
		this.myCertSubjectDN = myCertSubjectDN;
	}

	public String getWorkerPublicKey() {
		return workerPublicKey;
	}

	public void setWorkerPublicKey(String workerPublicKey) {
		this.workerPublicKey = workerPublicKey;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public List<WorkAccounting> getAccountings() {
		return accountings;
	}

	public void setAccountings(List<WorkAccounting> consumersBalances) {
		this.accountings = consumersBalances;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}


}
