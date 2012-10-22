package org.ourgrid.peer.response;

import java.util.List;

import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.internal.IResponseTO;

public class HereIsUserStatusResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.HERE_IS_USER_STATUS;
	private List<UserInfo> usersInfo;
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

	public List<UserInfo> getUsersInfo() {
		return usersInfo;
	}

	public void setUsersInfo(List<UserInfo> usersInfo) {
		this.usersInfo = usersInfo;
	}

	public String getResponseType() {
		return RESPONSE_TYPE;
	}
	
}
