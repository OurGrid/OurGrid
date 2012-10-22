package org.ourgrid.peer.response;

import java.util.List;

import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.internal.IResponseTO;

public class HereIsRemoteWorkersStatusResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.HERE_IS_REMOTE_WORKERS_STATUS;
	private List<RemoteWorkerInfo> remoteWorkersInfo;
	private String clientAddress;
	private String peerAddress;
	
	
	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setRemoteWorkersInfo(List<RemoteWorkerInfo> remoteWorkersInfo) {
		this.remoteWorkersInfo = remoteWorkersInfo;
	}

	public List<RemoteWorkerInfo> getRemoteWorkersInfo() {
		return remoteWorkersInfo;
	}
	
}
