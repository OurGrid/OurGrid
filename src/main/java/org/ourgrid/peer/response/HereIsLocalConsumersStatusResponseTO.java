package org.ourgrid.peer.response;

import java.util.List;

import org.ourgrid.common.interfaces.status.LocalConsumerInfo;
import org.ourgrid.common.internal.IResponseTO;

public class HereIsLocalConsumersStatusResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.HERE_IS_LOCAL_CONSUMERS_STATUS;
	
	
	private List<LocalConsumerInfo> localConsumersInfo;
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

	public void setLocalConsumersInfo(List<LocalConsumerInfo> localConsumersInfo) {
		this.localConsumersInfo = localConsumersInfo;
	}

	public List<LocalConsumerInfo> getLocalConsumersInfo() {
		return localConsumersInfo;
	}


}
