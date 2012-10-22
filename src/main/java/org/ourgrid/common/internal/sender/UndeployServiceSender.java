package org.ourgrid.common.internal.sender;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.UndeployServiceResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class UndeployServiceSender implements SenderIF<UndeployServiceResponseTO> {

	public void execute(UndeployServiceResponseTO response, ServiceManager manager) {
		manager.undeploy(response.getServiceName());
	}

}
