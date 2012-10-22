package org.ourgrid.peer.business.controller.actions;

import java.io.Serializable;

import org.ourgrid.common.interfaces.control.PeerControl;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;

public class UpdatePeerUptimeAction implements RepeatedAction {

	public void run(Serializable handler, ServiceManager serviceManager) {
		PeerControl peerControl = (PeerControl) serviceManager.getObjectDeployment(
				Module.CONTROL_OBJECT_NAME).getObject();
		peerControl.updatePeerUpTime();
	}

}
