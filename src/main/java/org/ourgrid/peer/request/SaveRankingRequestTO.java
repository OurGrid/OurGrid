package org.ourgrid.peer.request;

import org.ourgrid.common.internal.IRequestTO;

public class SaveRankingRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.SAVE_RANKING;
	
	
	private String senderPublicKey;
	private String rankingFilePath;
	private boolean isThisMyPublicKey;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setThisMyPublicKey(boolean isThisMyPublicKey) {
		this.isThisMyPublicKey = isThisMyPublicKey;
	}

	public boolean isThisMyPublicKey() {
		return isThisMyPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setRankingFilePath(String rankingFilePath) {
		this.rankingFilePath = rankingFilePath;
	}

	public String getRankingFilePath() {
		return rankingFilePath;
	}
}
