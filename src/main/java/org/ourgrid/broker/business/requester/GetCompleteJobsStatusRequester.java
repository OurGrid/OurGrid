package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ourgrid.broker.request.GetCompleteJobsStatusRequestTO;
import org.ourgrid.broker.response.HereIsCompleteJobsStatusResponseTO;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.common.interfaces.to.JobsPackage;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.util.CommonUtils;

public class GetCompleteJobsStatusRequester extends AbstractBrokerStatusRequester<GetCompleteJobsStatusRequestTO> {

	public List<IResponseTO> execute(GetCompleteJobsStatusRequestTO to) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (to.canStatusBeUsed()) {
			HereIsCompleteJobsStatusResponseTO responseTO = new HereIsCompleteJobsStatusResponseTO();
			responseTO.setJobPackage(getJobsStatus(to.getJobsIds()));
			responseTO.setClientAddress(to.getClientAddress());
			responseTO.setMyAddress(to.getMyAddress());
			responses.add(responseTO);
		}

		return responses;
	}
	
	private JobsPackage getJobsStatus(List<Integer> jobsIds) {
		Map<Integer, JobStatusInfo> allJobsInfo = getAllJobsInfo();
		Map<Integer, JobStatusInfo> selectedJobsInfo = CommonUtils.createSerializableMap();
		
		for (Integer jobId : jobsIds) {
			selectedJobsInfo.put(jobId, allJobsInfo.get(jobId));
		}
		
		return new JobsPackage(selectedJobsInfo);
	}	
}
