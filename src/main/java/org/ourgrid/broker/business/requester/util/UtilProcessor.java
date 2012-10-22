package org.ourgrid.broker.business.requester.util;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.JobDAO;
import org.ourgrid.broker.business.scheduler.SchedulerIF;

public class UtilProcessor {
	
	public static SchedulerIF getScheduler(String senderContainerId) {
		
		JobDAO jobDAO = BrokerDAOFactory.getInstance().getJobDAO();
		
		Integer jobID = jobDAO.getJobFromWorker(senderContainerId);
		
		SchedulerIF scheduler = null;
		if (jobID != null) {
			scheduler = jobDAO.getJobScheduler(jobID);
		} else {
			//TODO log
			return null;
		}
		
		if (scheduler == null) {
			//TODO log
		}
		
		return scheduler;
	}
}
