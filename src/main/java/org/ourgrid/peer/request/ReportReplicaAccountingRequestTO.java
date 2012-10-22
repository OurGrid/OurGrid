package org.ourgrid.peer.request;


import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.internal.IRequestTO;

public class ReportReplicaAccountingRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.REPORT_REPLICA_ACCOUNTING;
	private String userPublicKey;
	private GridProcessAccounting accounting;
	
	public String getUserPublicKey() {
		return userPublicKey;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setUserPublicKey(String senderPublicKey) {
		this.userPublicKey = senderPublicKey;
	}

	public void setAccounting(GridProcessAccounting replicaAccounting) {
		this.accounting = replicaAccounting;
	}

	public GridProcessAccounting getAccounting() {
		return accounting;
	}

}
