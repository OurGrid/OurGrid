package org.ourgrid.peer.request;

import java.util.List;
import java.util.Map;

import org.ourgrid.common.internal.IRequestTO;

public class StartPeerRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.START_PEER;
	
	
	private String myUserAtServer;
	private String description;
	private String email;
	private String label;
	private String latitude;
	private String longitude;
	private String myCertSubjectDN;
	private String filePath;
	private String networkStr;
	private boolean shouldJoinCommunity;
	private List<String> dsAddress;
	
	private String requestingCACertificatePath;
	private String receivingCACertificatePath;


	private Map<String, String> properties;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public boolean shouldJoinCommunity() {
		return shouldJoinCommunity;
	}

	public void setShouldJoinCommunity(boolean shouldJoinCommunity) {
		this.shouldJoinCommunity = shouldJoinCommunity;
	}

	public List<String> getDsAddress() {
		return dsAddress;
	}

	public void setDsAddress(List<String> dsAddress) {
		this.dsAddress = dsAddress;
	}

	public void setMyUserAtServer(String myUserAtServer) {
		this.myUserAtServer = myUserAtServer;
	}

	public String getMyUserAtServer() {
		return myUserAtServer;
	}

	public void setMyCertSubjectDN(String myCertSubjectDN) {
		this.myCertSubjectDN = myCertSubjectDN;
	}

	public String getMyCertSubjectDN() {
		return myCertSubjectDN;
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

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setNetworkStr(String networkStr) {
		this.networkStr = networkStr;
	}

	public String getNetworkStr() {
		return networkStr;
	}

	public void setRequestingCACertificatePath(
			String requestingCACertificatePath) {
		this.requestingCACertificatePath = requestingCACertificatePath;
	}

	public String getRequestingCACertificatePath() {
		return requestingCACertificatePath;
	}

	public void setReceivingCACertificatePath(String receivingCACertificatePath) {
		this.receivingCACertificatePath = receivingCACertificatePath;
	}

	public String getReceivingCACertificatePath() {
		return receivingCACertificatePath;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public Map<String, String> getProperties() {
		return properties;
	}
}
