package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class AddUserRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.ADD_USER;
	private String login;
	private String email;
	private String label;
	private String latitude;
	private String longitude;
	private String description;
	private String myUserAtServer;
	private String myCertSubjectDN;
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
	 * @return the myCertSubjectDN
	 */
	public String getMyCertSubjectDN() {
		return myCertSubjectDN;
	}


	/**
	 * @param myCertSubjectDN the myCertSubjectDN to set
	 */
	public void setMyCertSubjectDN(String myCertSubjectDN) {
		this.myCertSubjectDN = myCertSubjectDN;
	}


	/**
	 * @return the myUserAtServer
	 */
	public String getMyUserAtServer() {
		return myUserAtServer;
	}


	/**
	 * @param myUserAtServer the myUserAtServer to set
	 */
	public void setMyUserAtServer(String myUserAtServer) {
		this.myUserAtServer = myUserAtServer;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	public String getRequestType() {
		return REQUEST_TYPE;
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


	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}


	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}


	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}


	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}


	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}


	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}


	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}


	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
}
