package org.ourgrid.peer.response;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;

public class RemoteWorkerProviderRequestWorkersResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.REMOTE_WORKER_PROVIDER_REQUEST_WORKERS;
	private String remoteWorkerProviderAddress;
	private RequestSpecification requestSpec;
	
	public String getRemoteWorkerProviderAddress() {
		return remoteWorkerProviderAddress;
	}

	public void setRemoteWorkerProviderAddress(String remoteWorkerProviderAddress) {
		this.remoteWorkerProviderAddress = remoteWorkerProviderAddress;
	}

	public RequestSpecification getRequestSpec() {
		return requestSpec;
	}

	public void setRequestSpec(RequestSpecification requestSpec) {
		this.requestSpec = requestSpec;
	}

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

}
