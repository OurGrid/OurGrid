package org.ourgrid.peer.response;

import java.util.List;

import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.internal.IResponseTO;

public class HereIsLocalWorkersStatusResponseTO implements IResponseTO {
	
	private static final String RESPONSE_TYPE = PeerResponseConstants.HERE_IS_LOCAL_WORKERS_STATUS;
	private List<WorkerInfo> localWorkersInfos;
	private String ClientAddress;
	

	public String getClientAddress() {
		return ClientAddress;
	}


	public void setClientAddress(String clientAddress) {
		ClientAddress = clientAddress;
	}


	public List<WorkerInfo> getLocalWorkersInfos() {
		return localWorkersInfos;
	}


	public void setLocalWorkersInfos(List<WorkerInfo> localWorkersInfos) {
		this.localWorkersInfos = localWorkersInfos;
	}


	public String getResponseType() {
		return RESPONSE_TYPE;
	}


	
}
