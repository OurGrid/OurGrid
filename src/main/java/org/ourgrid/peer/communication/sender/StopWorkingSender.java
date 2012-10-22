package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.StopWorkingResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class StopWorkingSender implements SenderIF<StopWorkingResponseTO>{

	public void execute(StopWorkingResponseTO response, ServiceManager manager) {
		ServiceID wmServiceId = ServiceID.parse(response.getWmAddress());
		WorkerManagement wm = (WorkerManagement) manager.getStub(wmServiceId, WorkerManagement.class);
		
		if (wm == null) {
			manager.getLog().error("WorkerManagement stub [" + response.getWmAddress() + "] is NULL during " +
				"StopWorking message sending.");
			return;
		}
		
		wm.stopWorking();
	}


	
}
