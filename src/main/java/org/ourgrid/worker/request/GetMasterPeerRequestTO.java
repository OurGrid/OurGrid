package org.ourgrid.worker.request;

import org.ourgrid.common.internal.request.AbstractStatusRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class GetMasterPeerRequestTO extends AbstractStatusRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.GET_MASTER_PEER;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}
}
