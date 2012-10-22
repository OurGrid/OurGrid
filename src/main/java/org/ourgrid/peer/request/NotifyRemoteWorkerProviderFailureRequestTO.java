package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class NotifyRemoteWorkerProviderFailureRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.NOTIFY_RWP_FAILURE;
	private String rwpUserAtServer;
	private String rwpPublicKey;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}
	
	public void setRwpPublicKey(String rwpPublicKey) {
		this.rwpPublicKey = rwpPublicKey;
	}

	public String getRwpPublicKey() {
		return rwpPublicKey;
	}

	public void setRwpUserAtServer(String rwpUserAtServer) {
		this.rwpUserAtServer = rwpUserAtServer;
	}

	public String getRwpUserAtServer() {
		return rwpUserAtServer;
	}
}
