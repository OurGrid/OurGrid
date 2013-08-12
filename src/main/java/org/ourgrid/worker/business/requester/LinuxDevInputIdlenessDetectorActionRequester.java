package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.business.controller.IdlenessDetectorController;
import org.ourgrid.worker.business.dao.IdlenessDetectorDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.request.LinuxIdlenessDetectorActionRequestTO;

public class LinuxDevInputIdlenessDetectorActionRequester
		extends
		AbstractScheduledIdlenessDetectorActionRequester<LinuxIdlenessDetectorActionRequestTO> {

	private static final String IDLENESS_CMD_LOCATION = "/usr/share/ourgrid/idleness";
	private boolean isIdle = false;
	private ExecutorService threadPool;
	
	private void checkIdle() {
		IdlenessDetectorDAO idlenessDetectorDAO = WorkerDAOFactory
				.getInstance().getIdlenessDetectorDAO();
		Long idlenessTime = idlenessDetectorDAO.getIdlenessTime() / 1000;
		while (true) {
			try {
				ProcessBuilder pb = new ProcessBuilder(IDLENESS_CMD_LOCATION, 
						idlenessTime.toString());
				int exitValue = pb.start().waitFor();
				isIdle = exitValue == 1;
			} catch (Exception e) {
			} finally {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {}
			}
		}
	}
	
	@Override
	public List<IResponseTO> execute(
			LinuxIdlenessDetectorActionRequestTO request) {
		
		IdlenessDetectorDAO idlenessDetectorDAO = WorkerDAOFactory.getInstance().getIdlenessDetectorDAO();
		
		if (threadPool == null && idlenessDetectorDAO.isActive()) {
			threadPool = Executors.newFixedThreadPool(1);
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					checkIdle();
				}
			});
		}
		
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
		return isIdle;
	}
}
