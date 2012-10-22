package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class GetCompleteHistoryStatusRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.GET_COMPLETE_HISTORY_STATUS;
	
	
	private String peerAddress;
	private String clientAddress;
	private boolean canStatusBeUsed;
	private long time;
	private String label;
	private String contextString;
	private String propConfDir;
	private String propLabel;
	private String propJoinCommunity;
	boolean isJoinCommunityEnabled;
	private long upTime;
	
	
	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setCanStatusBeUsed(boolean canStatusBeUsed) {
		this.canStatusBeUsed = canStatusBeUsed;
	}

	public boolean canStatusBeUsed() {
		return canStatusBeUsed;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public long getUpTime() {
		return upTime;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	
}
