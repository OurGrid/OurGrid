package org.ourgrid.peer.response;

import org.ourgrid.common.BrokerLoginResult;
import org.ourgrid.common.internal.IResponseTO;

public class LoginSuccededResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.LOGIN_SUCCEDED;
	
	
	private BrokerLoginResult loginResult;
	private String workerProviderClientAddress;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public BrokerLoginResult getLoginResult() {
		return loginResult;
	}

	public void setLoginResult(BrokerLoginResult loginResult) {
		this.loginResult = loginResult;
	}

	public void setWorkerProviderClientAddress(
			String workerProviderClientAddress) {
		this.workerProviderClientAddress = workerProviderClientAddress;
	}

	public String getWorkerProviderClientAddress() {
		return workerProviderClientAddress;
	}
}
