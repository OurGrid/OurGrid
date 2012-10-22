package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.request.GetPagedTasksRequestTO;
import org.ourgrid.broker.response.HereIsPagedTasksResponseTO;
import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.common.internal.IResponseTO;

public class GetPagedTasksRequester extends AbstractBrokerStatusRequester<GetPagedTasksRequestTO> {

	public List<IResponseTO> execute(GetPagedTasksRequestTO to) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (to.canStatusBeUsed()) {
			
			int jobId = to.getJobId();
			int pageSize = to.getPageSize();
			int offset = to.getOffset();
			
			List<TaskStatusInfo> taskStatusInfo = getAllJobsInfo().get(jobId).getTasks();
			List<TaskStatusInfo> pagedTasks = new ArrayList<TaskStatusInfo>(pageSize);
			
			for (int i = offset; i < pageSize; i++) {
				
				if (i >= taskStatusInfo.size()) {
					break;
				}
				
				pagedTasks.add(taskStatusInfo.get(i));
			}
			
			HereIsPagedTasksResponseTO responseTO = new HereIsPagedTasksResponseTO();
			
			responseTO.setJobId(jobId);
			responseTO.setOffset(offset);
			responseTO.setPagedTasks(pagedTasks);
			responseTO.setClientAddress(to.getClientAddress());
			responseTO.setMyAddress(to.getMyAddress());

			responses.add(responseTO);
		}

		return responses;
	}
	
}
