package org.ourgrid.discoveryservice.request;

import java.util.List;

import org.ourgrid.common.internal.IRequestTO;

public class HereIsRemoteWorkerProviderListRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.HERE_IS_REMOTE_WORKER_PROVIDER_LIST;
	
	
	private List<String> workerProviders;
	private String senderAddress;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}


	public void setWorkerProviders(List<String> workerProviders) {
		this.workerProviders = workerProviders;
	}

	public List<String> getWorkerProviders() {
		return workerProviders;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderAddress() {
		return senderAddress;
	}
}
