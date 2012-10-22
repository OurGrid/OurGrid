package org.ourgrid.peer.request;


import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IRequestTO;

public class UpdateRequestRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.UPDATE_REQUEST;
	
	private RequestSpecification requestSpec;
	private String brokerPublicKey;
	private String myCertPathDN;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setRequestSpec(RequestSpecification requestSpec) {
		this.requestSpec = requestSpec;
	}

	public RequestSpecification getRequestSpec() {
		return requestSpec;
	}

	public void setBrokerPublicKey(String brokerPublicKey) {
		this.brokerPublicKey = brokerPublicKey;
	}

	public String getBrokerPublicKey() {
		return brokerPublicKey;
	}

	public void setMyCertPathDN(String myCertPathDN) {
		this.myCertPathDN = myCertPathDN;
	}

	public String getMyCertPathDN() {
		return myCertPathDN;
	}

}
