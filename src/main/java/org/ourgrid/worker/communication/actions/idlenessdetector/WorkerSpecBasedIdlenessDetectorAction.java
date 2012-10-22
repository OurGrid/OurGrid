package org.ourgrid.worker.communication.actions.idlenessdetector;

import java.io.Serializable;

import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.worker.request.WorkerSpecBasedIdlenessDetectorActionRequestTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;

public class WorkerSpecBasedIdlenessDetectorAction implements RepeatedAction {

	public static final double MAX_CPU_USAGE_AVERAGE = 10.0;// 10%
	
	
	public void run(Serializable handler, ServiceManager serviceManager) {
		WorkerSpecBasedIdlenessDetectorActionRequestTO to = new WorkerSpecBasedIdlenessDetectorActionRequestTO();
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

}
