package org.ourgrid.peer.request;

import java.util.List;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IRequestTO;

import sun.security.provider.certpath.X509CertPath;

public class RemoteWorkerProviderRequestWorkersRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.REMOTE_WORKER_PROVIDER_REQUEST_WORKERS;
	
	
	private boolean useVomsAuth;
	//private boolean isCertificateValid;
	//private boolean isCertPathIssuedByCA;
	private String senderPublicKey;
	private String remoteWorkerProviderClientAddress;
	private String remoteWorkerProviderClientContainerID;
	private String remoteWorkerProviderClientPublicKey;
	//private String providerDN;
	
	private X509CertPath myCertPath;
	private String consumerDN;
	private List<String> vomsURLList;
	private RequestSpecification requestSpec;


	private String myPrivateKey;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public RequestSpecification getRequestSpec() {
		return requestSpec;
	}

	public void setRequestSpec(RequestSpecification requestSpec) {
		this.requestSpec = requestSpec;
	}

	public String getRemoteWorkerProviderClientAddress() {
		return remoteWorkerProviderClientAddress;
	}

	public void setRemoteWorkerProviderClientAddress(
			String remoteWorkerProviderClientAddress) {
		this.remoteWorkerProviderClientAddress = remoteWorkerProviderClientAddress;
	}

	public void setRemoteWorkerProviderClientContainerID(
			String remoteWorkerProviderClientContainerID) {
		this.remoteWorkerProviderClientContainerID = remoteWorkerProviderClientContainerID;
	}

	public String getRemoteWorkerProviderClientContainerID() {
		return remoteWorkerProviderClientContainerID;
	}

	public void setUseVomsAuth(boolean useVomsAuth) {
		this.useVomsAuth = useVomsAuth;
	}

	public boolean useVomsAuth() {
		return useVomsAuth;
	}

	public void setVomsURLList(List<String> vomsURLList) {
		this.vomsURLList = vomsURLList;
	}

	public List<String> getVomsURLList() {
		return vomsURLList;
	}

//	public void setProviderDN(String providerDN) {
//		this.providerDN = providerDN;
//	}
//
//	public String getProviderDN() {
//		return providerDN;
//	}
//
//	public void setCertificateValid(boolean isCertificateValid) {
//		this.isCertificateValid = isCertificateValid;
//	}
//
//	public boolean isCertificateValid() {
//		return isCertificateValid;
//	}
//
//	public void setCertPathIssuedByCA(boolean isCertPathIssuedByCA) {
//		this.isCertPathIssuedByCA = isCertPathIssuedByCA;
//	}
//
//	public boolean isCertPathIssuedByCA() {
//		return isCertPathIssuedByCA;
//	}

	public void setMyCertPath(X509CertPath myCertPath) {
		this.myCertPath = myCertPath;
	}

	public X509CertPath getMyCertPath() {
		return myCertPath;
	}

	public void setConsumerDN(String consumerDN) {
		this.consumerDN = consumerDN;
	}

	public String getConsumerDN() {
		return consumerDN;
	}

	public void setRemoteWorkerProviderClientPublicKey(
			String remoteWorkerProviderClientPublicKey) {
		this.remoteWorkerProviderClientPublicKey = remoteWorkerProviderClientPublicKey;
	}

	public String getRemoteWorkerProviderClientPublicKey() {
		return remoteWorkerProviderClientPublicKey;
	}

	public void setMyPrivateKey(String myPrivateKey) {
		this.myPrivateKey = myPrivateKey;
	}

	public String getMyPrivateKey() {
		return myPrivateKey;
	}

}
