package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.GetJobStatusRequestTO;
import org.ourgrid.broker.response.HereIsJobsStatusResponseTO;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.common.interfaces.to.JobsPackage;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.util.CommonUtils;

public class GetJobStatusRequester extends AbstractBrokerStatusRequester<GetJobStatusRequestTO> {

	public List<IResponseTO> execute(GetJobStatusRequestTO to) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (to.canStatusBeUsed()) {
			
			Map<Integer, JobStatusInfo> allJobs = CommonUtils.createSerializableMap();
			Set<SchedulerIF> schedulers = BrokerDAOFactory.getInstance().getJobDAO().getSchedulers();
			
			for(SchedulerIF scheduler : schedulers) {
				allJobs.putAll(scheduler.getJobsDescription());
			}
			
			HereIsJobsStatusResponseTO responseTO = new HereIsJobsStatusResponseTO();
			responseTO.setJobPackage(new JobsPackage(allJobs));
			responseTO.setClientAddress(to.getClientAddress());
			responseTO.setMyAddress(to.getMyAddress());
			responses.add(responseTO);
		}

		return responses;
	}
	
}
