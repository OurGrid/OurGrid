package org.ourgrid.broker.business.requester;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.JobDAO;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.broker.status.JobWorkerStatus;
import org.ourgrid.broker.status.WorkerStatusInfo;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.util.CommonUtils;

public abstract class AbstractBrokerStatusRequester<U extends IRequestTO> implements RequesterIF<U> {
	
	protected Map<Integer, JobStatusInfo> getAllJobsInfo() {
		JobWorkerStatus jwStatus = null;
		Map<Integer, JobStatusInfo> allJobs = CommonUtils.createSerializableMap();
		
		JobDAO jobDAO = BrokerDAOFactory.getInstance().getJobDAO();
		
		for(SchedulerIF scheduler: jobDAO.getSchedulers()) {
			jwStatus = scheduler.getCompleteStatus();
			allJobs.putAll(jwStatus.getJobs());
		}
		return allJobs;
	}
	
	protected Map<Integer, Set<WorkerStatusInfo>> convertWorkerStatus(Map<Integer, WorkerStatusInfo[]> statusMap) {
		
		Map<Integer, Set<WorkerStatusInfo>> newMap = CommonUtils.createSerializableMap();
		
		if (statusMap != null) {
			for (Entry<Integer, WorkerStatusInfo[]> status : statusMap.entrySet()) {
				newMap.put(status.getKey(), getSet(status.getValue()));
			}
		}
		
		return newMap;
	}
	
	private Set<WorkerStatusInfo> getSet(WorkerStatusInfo[] array) {
		
		Set<WorkerStatusInfo> set = new TreeSet<WorkerStatusInfo>();
		
		if (array != null) {
			for (WorkerStatusInfo workerStatusInfo : array) {
				set.add(workerStatusInfo);
			}
		}
		
		return set;
	}
}
