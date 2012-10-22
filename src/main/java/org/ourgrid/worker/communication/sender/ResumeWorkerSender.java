package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.control.WorkerControl;
import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.worker.response.ResumeWorkerResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class ResumeWorkerSender extends AbstractWorkerControlOperationsSender<ResumeWorkerResponseTO> {

	public void execute(ResumeWorkerResponseTO response, ServiceManager manager) {
		
		WorkerControl workerControl = getWorkerControl(manager);
		
		WorkerControlClient dummyWorkerControlClient = getIdlenessDetectorWorkerControlClient(manager);
		
		workerControl.resume(dummyWorkerControlClient);
	}
}