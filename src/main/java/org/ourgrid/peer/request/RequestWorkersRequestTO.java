package org.ourgrid.peer.request;


import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IRequestTO;

import sun.security.provider.certpath.X509CertPath;

public class RequestWorkersRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.REQUEST_WORKERS;

	private RequestSpecification requestSpec;
	private String brokerPublicKey;
	private String myPublicKey;
	private X509CertPath myCertPath;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setBrokerPublicKey(String brokerPublicKey) {
		this.brokerPublicKey = brokerPublicKey;
	}

	public String getBrokerPublicKey() {
		return brokerPublicKey;
	}

	public void setRequestSpec(RequestSpecification requestSpec) {
		this.requestSpec = requestSpec;
	}

	public RequestSpecification getRequestSpec() {
		return requestSpec;
	}

	public void setMyPublicKey(String myPublicKey) {
		this.myPublicKey = myPublicKey;
	}

	public String getMyPublicKey() {
		return myPublicKey;
	}

	public void setMyCertPath(X509CertPath myCertPath) {
		this.myCertPath = myCertPath;
	}

	public X509CertPath getMyCertPath() {
		return myCertPath;
	}

	
}
