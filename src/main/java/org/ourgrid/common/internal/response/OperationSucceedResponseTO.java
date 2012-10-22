package org.ourgrid.common.internal.response;

import java.io.Serializable;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.OurGridResponseConstants;

public class OperationSucceedResponseTO implements IResponseTO {
	
	private final String RESPONSE_TYPE = OurGridResponseConstants.OPERATION_SUCCEDED;

	
	private String clientAddress;
	private Exception errorCause;
	private Serializable result;

	private boolean isRemoteClient = true;
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setErrorCause(Exception errorCause) {
		this.errorCause = errorCause;
	}

	public Exception getErrorCause() {
		return errorCause;
	}

	public void setResult(Serializable result) {
		this.result = result;
	}

	public Serializable getResult() {
		return result;
	}

	public void setRemoteClient(boolean isRemoteClient) {
		this.isRemoteClient = isRemoteClient;
	}

	public boolean isRemoteClient() {
		return isRemoteClient;
	}
}
