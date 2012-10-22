package org.ourgrid.common.internal.sender;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.DeployServiceResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

/**
 * Requirement 302
 */
public class DeployServiceSender implements SenderIF<DeployServiceResponseTO> {

	public void execute(DeployServiceResponseTO response, ServiceManager manager) {
		
		Class<?> clazz = response.getServiceClass();
		
		try {
			Object instance = clazz.getConstructor().newInstance();
			manager.deploy(response.getServiceName(), instance);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
