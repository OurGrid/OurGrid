package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.BrokerLoginResult;
import org.ourgrid.common.internal.IRequestTO;




public class LoginSucceedRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.LOGIN_SUCCEDED;
	
	
	private String senderPublicKey;
	private String peerDeploymentID;
	private String peerAddress;
	private String peerPublicKey;
	private BrokerLoginResult result;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getPeerDeploymentID() {
		return peerDeploymentID;
	}

	public void setPeerDeploymentID(String peerDeploymentID) {
		this.peerDeploymentID = peerDeploymentID;
	}

	public void setPeerPublicKey(String peerPublicKey) {
		this.peerPublicKey = peerPublicKey;
	}

	public String getPeerPublicKey() {
		return peerPublicKey;
	}

	public void setResult(BrokerLoginResult result) {
		this.result = result;
	}

	public BrokerLoginResult getResult() {
		return result;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public String getPeerAddress() {
		return peerAddress;
	}
}
