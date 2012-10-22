package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.status.WorkerCompleteStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.status.PeerState;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.request.GetWorkerCompleteStatusRequestTO;
import org.ourgrid.worker.response.HereIsWorkerCompleteStatusResponseTO;
import org.ourgrid.worker.status.PeerStatusInfo;

public class GetWorkerCompleteStatusRequester implements RequesterIF<GetWorkerCompleteStatusRequestTO> {

	public List<IResponseTO> execute(GetWorkerCompleteStatusRequestTO to) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (to.canStatusBeUsed()) {
			HereIsWorkerCompleteStatusResponseTO responseTO = new HereIsWorkerCompleteStatusResponseTO();
			responseTO.setCompleteStatus(getCompleteStatus(to));
			responseTO.setClientAddress(to.getClientAddress());
			responses.add(responseTO);
		}
		
		
		return responses;
	}
	
	@Req("REQ095")
	private WorkerCompleteStatus getCompleteStatus(GetWorkerCompleteStatusRequestTO to) {
		
		WorkerStatusDAO statusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		PeerStatusInfo peerInfo = new PeerStatusInfo(
				statusDAO.isLogged() ? PeerState.LOGGED.toString() : PeerState.NOT_LOGGED.toString(), 
				statusDAO.getLoginError(), StringUtil.addressToUserAtServer(statusDAO.getMasterPeerAddress()));
		
		return new WorkerCompleteStatus(to.getUptime(), to.getConfiguration(), 
				statusDAO.getStatus(), peerInfo, getCurrentPlaypenDirPath(to),
				getCurrentStorageDirPath(to));
	}
	
	@Req("REQ095")
	private String getCurrentPlaypenDirPath(GetWorkerCompleteStatusRequestTO to) {
		String playpenDir = WorkerDAOFactory.getInstance().getEnvironmentDAO().getPlaypenDir();
		if (playpenDir != null) {
			return playpenDir;
		}
		return to.getContextPlaypenDir();
	}
	
	@Req("REQ095")
	private String getCurrentStorageDirPath(GetWorkerCompleteStatusRequestTO to) {
		String storageDir = WorkerDAOFactory.getInstance().getEnvironmentDAO().getStorageDir();
		if (storageDir != null) {
			return storageDir;
		}
		return to.getContextStorageDir();
	}
}
