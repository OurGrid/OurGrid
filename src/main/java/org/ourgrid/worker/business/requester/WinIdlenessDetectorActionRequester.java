package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.business.controller.IdlenessDetectorController;
import org.ourgrid.worker.business.dao.IdlenessDetectorDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.communication.actions.idlenessdetector.WinIdlenessDetectorAction.Kernel32;
import org.ourgrid.worker.communication.actions.idlenessdetector.WinIdlenessDetectorAction.User32;
import org.ourgrid.worker.request.WinIdlenessDetectorActionRequestTO;

public class WinIdlenessDetectorActionRequester extends AbstractScheduledIdlenessDetectorActionRequester<WinIdlenessDetectorActionRequestTO> {

	public List<IResponseTO> execute(WinIdlenessDetectorActionRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (isIdle()) {
			IdlenessDetectorController.getInstance().resumeWorker(responses);
		} else {
			IdlenessDetectorController.getInstance().pauseWorker(responses);
		}
		
		return responses;
	}
	
	protected boolean isIdle() {
		IdlenessDetectorDAO idlenessDetectorDAO = WorkerDAOFactory.getInstance().getIdlenessDetectorDAO();
		
		return super.isIdle() && (!idlenessDetectorDAO.isActive() ||
				getIdleTimeMillisWin32() > idlenessDetectorDAO.getIdlenessTime());
	}
	
	/**
	 * Get the amount of milliseconds that have elapsed since the last input event
	 * (mouse or keyboard)
	 * @return idle time in milliseconds
	 */
	private static int getIdleTimeMillisWin32() {
		User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
		User32.INSTANCE.GetLastInputInfo(lastInputInfo);
		return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
	}
}
