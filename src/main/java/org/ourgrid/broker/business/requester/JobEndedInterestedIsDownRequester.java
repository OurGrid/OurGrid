package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.JobDAO;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.JobEndedInterestedIsDownRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.ReleaseResponseTO;

public class JobEndedInterestedIsDownRequester implements RequesterIF<JobEndedInterestedIsDownRequestTO> {

	public List<IResponseTO> execute(JobEndedInterestedIsDownRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		JobDAO jobDAO = BrokerDAOFactory.getInstance().getJobDAO();
		
		for(SchedulerIF scheduler: jobDAO.getSchedulers()) {
			scheduler.jobEndedInterestedIsDown(request.getInterestedDeploymentID());
		}
		
		ReleaseResponseTO releaseTO = new ReleaseResponseTO();
		releaseTO.setStubAddress(request.getInterestedAddress());
		
		responses.add(releaseTO);
		
		return responses;
	}
}
