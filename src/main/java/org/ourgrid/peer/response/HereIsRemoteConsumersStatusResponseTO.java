package org.ourgrid.peer.response;

import java.util.List;

import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.internal.IResponseTO;

public class HereIsRemoteConsumersStatusResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.HERE_IS_REMOTE_CONSUMERS_STATUS;
	private List<ConsumerInfo> remoteConsumersInfo;
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

	public void setRemoteConsumersInfo(List<ConsumerInfo> remoteConsumersInfo) {
		this.remoteConsumersInfo = remoteConsumersInfo;
	}

	public List<ConsumerInfo> getRemoteConsumersInfo() {
		return remoteConsumersInfo;
	}

}
