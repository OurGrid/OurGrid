package org.ourgrid.common.internal.response;

import java.security.InvalidParameterException;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.OurGridResponseConstants;

/**
 * Requirement 302
 */
public class LoggerResponseTO implements IResponseTO {
	
	public static final int DEBUG = 0;
	public static final int WARN = 1;
	public static final int ERROR = 2;
	public static final int INFO = 3;
	public static final int TRACE = 4;
	public static final int FATAL = 5;
	
	private final String RESPONSE_TYPE = OurGridResponseConstants.LOGGER;

	private String message;
	private int type;
	private Exception error;
	
	public LoggerResponseTO() {}
	
	public LoggerResponseTO(String message, int type) {
		if (type < 0 || type > 5) {
			throw new InvalidParameterException("Logger type must be an Integer within the range [0, 5].");
		}
		
		this.message = message;
		this.type = type;
		this.error = null;
	}
	
	public LoggerResponseTO(String message, int type, Exception error) {
		this.message = message;
		this.type = type;
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setError(Exception error) {
		this.error = error;
	}

	public Exception getError() {
		return error;
	}

}
