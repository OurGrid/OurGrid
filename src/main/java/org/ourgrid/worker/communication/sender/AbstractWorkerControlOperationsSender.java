package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.control.WorkerControl;
import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public abstract class AbstractWorkerControlOperationsSender <T extends IResponseTO>  implements SenderIF<T> {
	
	protected WorkerControl getWorkerControl(ServiceManager serviceManager) {
		return (WorkerControl) serviceManager.getObjectDeployment(
				Module.CONTROL_OBJECT_NAME).getObject();
	}

	protected WorkerControlClient getIdlenessDetectorWorkerControlClient(
			ServiceManager serviceManager) {
		ObjectDeployment objectDeployment = serviceManager.getObjectDeployment(
						WorkerConstants.IDLENESS_DETECTOR_WORKER_CONTROL_CLIENT);
		return (WorkerControlClient) objectDeployment.getObject();
	}
}
