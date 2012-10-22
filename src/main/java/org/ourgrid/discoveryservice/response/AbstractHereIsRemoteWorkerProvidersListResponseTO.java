package org.ourgrid.discoveryservice.response;

import java.util.List;

import org.ourgrid.common.internal.IResponseTO;

public abstract class AbstractHereIsRemoteWorkerProvidersListResponseTO implements IResponseTO {

	private List<String> workerProviders;
	private String stubAddress;
	
	
	public void setWorkerProviders(List<String> workerProviders) {
		this.workerProviders = workerProviders;
	}

	public List<String> getWorkerProviders() {
		return workerProviders;
	}

	public void setStubAddress(String stubAddress) {
		this.stubAddress = stubAddress;
	}

	public String getStubAddress() {
		return stubAddress;
	}
}
