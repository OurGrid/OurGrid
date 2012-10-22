package org.ourgrid.peer.business.dao;

public class PeerPropertiesDAO {

	private Integer requestRepeatDelayInSeconds = null;

	public void setRequestRepeatDelayInSeconds(Integer requestRepeatDelayInSeconds) {
		this.requestRepeatDelayInSeconds = requestRepeatDelayInSeconds;
	}

	public Integer getRequestRepeatDelayInSeconds() {
		return requestRepeatDelayInSeconds;
	}
	
}
