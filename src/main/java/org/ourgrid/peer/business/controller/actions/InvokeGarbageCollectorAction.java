package org.ourgrid.peer.business.controller.actions;

import java.io.Serializable;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;

public class InvokeGarbageCollectorAction implements RepeatedAction {

	public void run(Serializable handler, ServiceManager serviceManager) {
		System.gc();
	}

}
