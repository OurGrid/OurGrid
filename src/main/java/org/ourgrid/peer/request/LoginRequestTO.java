package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class LoginRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.LOGIN;
	private String senderPublicKey;
	private String workerProviderClientAddress;
	private String userName;
	private String serverName;
	private String myUserAtServer;
	private String description;
	private String email;
	private String label;
	private String latitude;
	private String longitude;
	private String myCertSubjectDN;
	private String filePath;
	
	private boolean isOnDemandPeer;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	/**
	 * @return the senderPublicKey
	 */
	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	/**
	 * @param senderPublicKey the senderPublicKey to set
	 */
	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public void setWorkerProviderClientAddress(
			String workerProviderClientAddress) {
		this.workerProviderClientAddress = workerProviderClientAddress;
	}

	public String getWorkerProviderClientAddress() {
		return workerProviderClientAddress;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerName() {
		return serverName;
	}

	public String getLogin() {
		return this.userName + "@" + this.serverName;
	}

	public String getMyUserAtServer() {
		return myUserAtServer;
	}

	public void setMyUserAtServer(String myUserAtServer) {
		this.myUserAtServer = myUserAtServer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getMyCertSubjectDN() {
		return myCertSubjectDN;
	}

	public void setMyCertSubjectDN(String myCertSubjectDN) {
		this.myCertSubjectDN = myCertSubjectDN;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isOnDemandPeer() {
		return isOnDemandPeer;
	}

	public void setOnDemandPeer(boolean isOnDemandPeer) {
		this.isOnDemandPeer = isOnDemandPeer;
	}

}
