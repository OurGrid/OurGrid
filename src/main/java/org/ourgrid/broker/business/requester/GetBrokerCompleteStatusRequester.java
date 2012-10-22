package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.JobDAO;
import org.ourgrid.broker.business.dao.PeerDAO;
import org.ourgrid.broker.business.dao.PeerEntry;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.GetBrokerCompleteStatusRequestTO;
import org.ourgrid.broker.response.HereIsBrokerCompleteStatusResponseTO;
import org.ourgrid.broker.status.JobWorkerStatus;
import org.ourgrid.broker.status.PeerStatusInfo;
import org.ourgrid.broker.status.WorkerStatusInfo;
import org.ourgrid.common.interfaces.to.BrokerCompleteStatus;
import org.ourgrid.common.interfaces.to.JobsPackage;
import org.ourgrid.common.interfaces.to.PeersPackage;
import org.ourgrid.common.interfaces.to.WorkersPackage;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.util.CommonUtils;

public class GetBrokerCompleteStatusRequester extends AbstractBrokerStatusRequester<GetBrokerCompleteStatusRequestTO> {

	public List<IResponseTO> execute(GetBrokerCompleteStatusRequestTO to) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (to.canStatusBeUsed()) {
			HereIsBrokerCompleteStatusResponseTO responseTO = new HereIsBrokerCompleteStatusResponseTO();
			responseTO.setCompleteStatus(getCompleteStatus(to));
			responseTO.setClientAddress(to.getClientAddress());
			responseTO.setMyAddress(to.getMyAddress());
			responses.add(responseTO);
		}
		
		
		return responses;
	}
	
	/**
	 * Broker Complete status request
	 * @param client
	 */
	private BrokerCompleteStatus getCompleteStatus(GetBrokerCompleteStatusRequestTO to) {
		
		BrokerCompleteStatus status = new BrokerCompleteStatus(
				new JobsPackage(getAllJobsInfo()), 
				new PeersPackage(getAllPeersInfo()), 
				new WorkersPackage(getAllWorkersInfo()), 
				to.getUptime(), 
				to.getConfiguration());
		
		return status;
	}
	
	private List<PeerStatusInfo> getAllPeersInfo() {
		PeerDAO peerDAO = BrokerDAOFactory.getInstance().getPeerDAO();
		
		List<PeerEntry> peers = new ArrayList<PeerEntry>(peerDAO.getPeers());
		List<PeerStatusInfo> peerInfoList = new ArrayList<PeerStatusInfo>();
		
		for (PeerEntry peerEntry : peers) {
			peerInfoList.add(new PeerStatusInfo(peerEntry.getState().toString(), peerEntry.getPeerSpec(), peerEntry.getLoginError()));
		}
		return peerInfoList;
	}
	
	private Map<Integer, Set<WorkerStatusInfo>> getAllWorkersInfo() {
		Map<Integer, Set<WorkerStatusInfo>> allWorkers = CommonUtils.createSerializableMap();
		JobWorkerStatus jwStatus = null;
		
		JobDAO jobDAO = BrokerDAOFactory.getInstance().getJobDAO();
		
		for(SchedulerIF scheduler: jobDAO.getSchedulers()) {
			jwStatus = scheduler.getCompleteStatus();
			allWorkers.putAll(convertWorkerStatus(jwStatus.getWorkers()));
		}
		return allWorkers;
	}
}
