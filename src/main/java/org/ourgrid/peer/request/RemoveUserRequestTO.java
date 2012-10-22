package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class RemoveUserRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.REMOVE_USER;
	private String login;
	private String clientAddress;

	/**
	 * @return the clientAddress
	 */
	public String getClientAddress() {
		return clientAddress;
	}

	/**
	 * @param clientAddress the clientAddress to set
	 */
	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	
}
