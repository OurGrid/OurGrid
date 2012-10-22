package org.ourgrid.common.internal.sender;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

/**
 * Requirement 502
 */
public class RegisterInterestSender implements SenderIF<RegisterInterestResponseTO>{

	public void execute(RegisterInterestResponseTO response,
			ServiceManager manager) {
		
		if (response.getDetectionTime() == null || response.getHeartbeatDelay() == null) {
			manager.registerInterest(response.getMonitorName(), response.getMonitorableAddress(), 
					response.getMonitorableType());
		} else {
			manager.registerInterest(response.getMonitorName(), response.getMonitorableAddress(), 
					response.getMonitorableType(), response.getDetectionTime(), response.getHeartbeatDelay());
		}
	
	}
}
