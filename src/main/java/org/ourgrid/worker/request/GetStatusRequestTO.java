package org.ourgrid.worker.request;

import org.ourgrid.common.internal.request.AbstractStatusRequestTO;
import org.ourgrid.worker.business.requester.WorkerRequestConstants;

public class GetStatusRequestTO extends AbstractStatusRequestTO {

	
	private final String REQUEST_TYPE = WorkerRequestConstants.GET_STATUS;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}
}
