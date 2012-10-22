package org.ourgrid.broker.response;

import java.util.List;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.common.internal.response.AbstractStatusResponseTO;



public class HereIsPagedTasksResponseTO extends AbstractStatusResponseTO {
	
	private final String RESPONSE_TYPE = BrokerResponseConstants.HERE_IS_PAGED_TASKS;

	private Integer jobId;
	private Integer offset;
	private List<TaskStatusInfo> pagedTasks;
	private String myAddress;
	
	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public List<TaskStatusInfo> getPagedTasks() {
		return pagedTasks;
	}

	public void setPagedTasks(List<TaskStatusInfo> pagedTasks) {
		this.pagedTasks = pagedTasks;
	}

	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setMyAddress(String myAddress) {
		this.myAddress = myAddress;
	}

	public String getMyAddress() {
		return myAddress;
	}
}
