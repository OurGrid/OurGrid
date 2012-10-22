package org.ourgrid.peer.request;


import java.util.List;

import org.ourgrid.common.internal.IRequestTO;

public class HereIsRemoteWorkerProvidersListRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.HERE_IS_REMOTE_WORKER_PROVIDERS_LIST;
	private List<String> providersUserAtServer;
	private String myUserAtServer;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}
	
	public void setMyUserAtServer(String myUserAtServer) {
		this.myUserAtServer = myUserAtServer;
	}

	public String getMyUserAtServer() {
		return myUserAtServer;
	}

	public void setProvidersUserAtServer(List<String> providersUserAtServer) {
		this.providersUserAtServer = providersUserAtServer;
	}

	public List<String> getProvidersUserAtServer() {
		return providersUserAtServer;
	}
}
