package org.ourgrid.peer.response;

import java.util.Set;

import org.ourgrid.common.internal.IResponseTO;

public class PersistDiscoveryServiceNetworkResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.PERSIST_DS_NETWORK;
	
	private Set<String> usersAtServers;
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setUsersAtServers(Set<String> usersAtServers) {
		this.usersAtServers = usersAtServers;
	}

	public Set<String> getUsersAtServer() {
		return usersAtServers;
	}

	
}
