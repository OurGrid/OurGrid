package org.ourgrid.worker.request;

import java.security.cert.X509Certificate;
import java.util.List;

import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;




public class WorkForPeerRequestTO implements IRequestTO {

	
	private static String REQUEST_TYPE = WorkerRequestConstants.WORK_FOR_PEER;
	
	private String senderPublicKey;
	private String remotePeerPublicKey;
	private String clientAddress;
	private List<String> usersDN;
	private List<X509Certificate> caCertificates;

	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public List<String> getUsersDN() {
		return usersDN;
	}

	public void setUsersDN(List<String> usersDN) {
		this.usersDN = usersDN;
	}

	public List<X509Certificate> getCaCertificates() {
		return caCertificates;
	}

	public void setCaCertificates(List<X509Certificate> caCertificates) {
		this.caCertificates = caCertificates;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setRemotePeerPublicKey(String remotePeerPublicKey) {
		this.remotePeerPublicKey = remotePeerPublicKey;
	}

	public String getRemotePeerPublicKey() {
		return remotePeerPublicKey;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

}
