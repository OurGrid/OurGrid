package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

import sun.security.provider.certpath.X509CertPath;

public class NotifyRemoteWorkerProviderRecoveryRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.NOTIFY_RWP_RECOVERY;
	private X509CertPath rwpCertPath;
	private X509CertPath myCertPath;
	private String rwpAddress;
	private String rwpUserAtServer;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}
	
	public X509CertPath getMyCertPath() {
		return myCertPath;
	}

	public void setRwpCertPath(X509CertPath rwpCertPath) {
		this.rwpCertPath = rwpCertPath;
	}

	public X509CertPath getRwpCertPath() {
		return rwpCertPath;
	}

	public void setMyCertPath(X509CertPath myCertPath) {
		this.myCertPath = myCertPath;
	}

	public void setRwpAdress(String rwpAddress) {
		this.rwpAddress = rwpAddress;
	}

	public String getRwpAddress() {
		return rwpAddress;
	}

	public void setRwpUserAtServer(String rwpUserAtServer) {
		this.rwpUserAtServer = rwpUserAtServer;
	}
	
	public String getRwpUserAtServer() {
		return rwpUserAtServer;
	}
}
