package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.request.AbstractStatusRequestTO;

public class GetPagedTasksRequestTO extends AbstractStatusRequestTO {

	
	private final String REQUEST_TYPE = BrokerRequestConstants.GET_PAGED_TASKS;
	
	private Integer jobId;
	private Integer offset;
	private Integer pageSize;
	
	private String myAddress;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setMyAddress(String myAddress) {
		this.myAddress = myAddress;
	}

	public String getMyAddress() {
		return myAddress;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageSize() {
		return pageSize;
	}
}