package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.control.WorkerControl;
import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.worker.response.PauseWorkerResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class PauseWorkerSender extends AbstractWorkerControlOperationsSender<PauseWorkerResponseTO> {

	public void execute(PauseWorkerResponseTO response, ServiceManager manager) {
		
		WorkerControl workerControl = getWorkerControl(manager);
		
		WorkerControlClient dummyWorkerControlClient = getIdlenessDetectorWorkerControlClient(manager);
		
		workerControl.pause(dummyWorkerControlClient);
	}
}