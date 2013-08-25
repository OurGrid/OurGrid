package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.business.controller.IdlenessDetectorController;
import org.ourgrid.worker.business.dao.IdlenessDetectorDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.request.MacOSIdlenessDetectorActionRequestTO;

public class MacOSIdlenessDetectorActionRequester
		extends
		AbstractScheduledIdlenessDetectorActionRequester<MacOSIdlenessDetectorActionRequestTO> {

	private static final String IDLENESS_CMD_LOCATION = "/usr/share/ourgrid/idleness";
	
	private boolean checkIdle() throws Exception {
		IdlenessDetectorDAO idlenessDetectorDAO = WorkerDAOFactory
				.getInstance().getIdlenessDetectorDAO();
		Long idlenessTimeInSeconds = idlenessDetectorDAO.getIdlenessTime() / 1000;
		ProcessBuilder pb = new ProcessBuilder(IDLENESS_CMD_LOCATION);
		Process process = pb.start();
		process.waitFor();
		String idleTime = IOUtils.toString(process.getInputStream()).trim();
		
		return Long.valueOf(idleTime) > idlenessTimeInSeconds;
	}
	
	@Override
	public List<IResponseTO> execute(
			MacOSIdlenessDetectorActionRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		if (isIdleNow()) {
			IdlenessDetectorController.getInstance().resumeWorker(responses);
		} else {
			IdlenessDetectorController.getInstance().pauseWorker(responses);
		}
		return responses;
	}

	private boolean isIdleNow() {
		IdlenessDetectorDAO idlenessDetectorDAO = WorkerDAOFactory.getInstance().getIdlenessDetectorDAO();
		boolean isScheduledToBeIdle = super.isIdle();
		if (!idlenessDetectorDAO.isActive()) {
			return isScheduledToBeIdle;
		}
		try {
			return checkIdle();
		} catch (Exception e) {
			return false;
		}
	}
}
