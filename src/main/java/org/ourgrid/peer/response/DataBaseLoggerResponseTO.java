package org.ourgrid.peer.response;

import org.ourgrid.common.internal.response.LoggerResponseTO;

public class DataBaseLoggerResponseTO extends LoggerResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.DATA_BASE_LOGGER;
	
	public DataBaseLoggerResponseTO() {}
	
	public DataBaseLoggerResponseTO(String message, int type) {
		super(message, type);
	}
	
	public DataBaseLoggerResponseTO(String message, int type, Exception error) {
		super(message, type, error);
	}
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}
}
