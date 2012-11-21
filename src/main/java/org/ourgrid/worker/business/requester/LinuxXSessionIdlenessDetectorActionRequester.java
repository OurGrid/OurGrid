package org.ourgrid.worker.business.requester;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.business.controller.IdlenessDetectorController;
import org.ourgrid.worker.business.dao.IdlenessDetectorDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.request.LinuxIdlenessDetectorActionRequestTO;

public class LinuxXSessionIdlenessDetectorActionRequester
		extends
		AbstractScheduledIdlenessDetectorActionRequester<LinuxIdlenessDetectorActionRequestTO> {

	private Date lastModification;
	private String xSessionIdlenessFile;

	public List<IResponseTO> execute(
			LinuxIdlenessDetectorActionRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		xSessionIdlenessFile = request.getXIdleTimeLibPath();
		
		if (isIdle(responses)) {
			IdlenessDetectorController.getInstance().resumeWorker(responses);
		} else {
			IdlenessDetectorController.getInstance().pauseWorker(responses);
		}

		return responses;
	}

	@SuppressWarnings("unchecked")
	private Date getLastInput() {
		File idlenessDetectorFile = new File(xSessionIdlenessFile);
		Date lastInput = null;

		if (!idlenessDetectorFile.exists()) {
			return getLastModification();
		}
		List<String> idlenessFileContent = null;
		try {
			idlenessFileContent = IOUtils.readLines(new FileInputStream(
					idlenessDetectorFile));
		} catch (IOException e) {
			return getLastModification();
		}

		if (idlenessFileContent.isEmpty()) {
			return getLastModification();
		}

		String idlenessDataStr = idlenessFileContent.get(0);
		String[] idlenessData = idlenessDataStr.split(";");
		lastModification = new Date(Long.parseLong(idlenessData[0]) * 1000);
		Long idleTime = Long.parseLong(idlenessData[1]);
		lastInput = new Date(lastModification.getTime() - idleTime);

		return lastInput;
	}

	public Date getLastModification() {
		if (lastModification == null) {
			lastModification = new Date();
		}
		return lastModification;
	}

	private boolean isIdle(List<IResponseTO> responses) {
		IdlenessDetectorDAO idlenessDetectorDAO = WorkerDAOFactory
				.getInstance().getIdlenessDetectorDAO();
		Date now = new Date();
		long idlenessTime = idlenessDetectorDAO.getIdlenessTime();
		long idleTime = now.getTime() - getLastInput().getTime();
		return (idleTime >= idlenessTime);
	}

}
