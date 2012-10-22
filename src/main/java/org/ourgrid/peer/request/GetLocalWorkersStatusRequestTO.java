package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class GetLocalWorkersStatusRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.GET_LOCAL_WORKERS_STATUS;
	private String clientAddress;
	private String statusProviderServiceID;
	private boolean canStatusBeUsed;

	public String getPeerUserAtServer() {
		return peerUserAtServer;
	}

	public void setPeerUserAtServer(String peerUserAtServer) {
		this.peerUserAtServer = peerUserAtServer;
	}

	private String peerUserAtServer;
	
	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getStatusProviderServiceID() {
		return statusProviderServiceID;
	}

	public void setStatusProviderServiceID(String statusProviderServiceID) {
		this.statusProviderServiceID = statusProviderServiceID;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setCanStatusBeUsed(boolean canStatusBeUsed) {
		this.canStatusBeUsed = canStatusBeUsed;
	}

	public boolean canStatusBeUsed() {
		return canStatusBeUsed;
	}


}
