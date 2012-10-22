package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class GetCompleteStatusRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.GET_COMPLETE_STATUS;
	
	
	private boolean canStatusBeUsed;
	private String peerAddress;
	private String clientAddress;
	private String label;
	private String contextString;
	private String propConfDir;
	private String propLabel;
	private String propJoinCommunity;
	private String myCertSubjectDN; 
	boolean isJoinCommunityEnabled;
	private long upTime;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setCanStatusBeUsed(boolean canStatusBeUsed) {
		this.canStatusBeUsed = canStatusBeUsed;
	}

	public boolean canStatusBeUsed() {
		return canStatusBeUsed;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public long getUpTime() {
		return upTime;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String getContextString() {
		return contextString;
	}

	public void setContextString(String contextString) {
		this.contextString = contextString;
	}

	public String getPropConfDir() {
		return propConfDir;
	}

	public void setPropConfDir(String propConfDir) {
		this.propConfDir = propConfDir;
	}

	public String getPropLabel() {
		return propLabel;
	}

	public void setPropLabel(String propLabel) {
		this.propLabel = propLabel;
	}

	public String getPropJoinCommunity() {
		return propJoinCommunity;
	}

	public void setPropJoinCommunity(String propJoinCommunity) {
		this.propJoinCommunity = propJoinCommunity;
	}

	public boolean isJoinCommunityEnabled() {
		return isJoinCommunityEnabled;
	}

	public void setJoinCommunityEnabled(boolean isJoinCommunityEnabled) {
		this.isJoinCommunityEnabled = isJoinCommunityEnabled;
	}

	public void setMyCertSubjectDN(String myCertSubjectDN) {
		this.myCertSubjectDN = myCertSubjectDN;
	}

	public String getMyCertSubjectDN() {
		return myCertSubjectDN;
	}
}
