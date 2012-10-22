package org.ourgrid.common.internal.sender;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.CreateRepeatedActionResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;

/**
 * Requirement 302
 */
public class CreateRepeatedActionSender implements SenderIF<CreateRepeatedActionResponseTO> {

	public void execute(CreateRepeatedActionResponseTO response, ServiceManager manager) {
		
		manager.addActionForRepetition(response.getActionName(), (RepeatedAction) response.getRepeatedAction());
	}

}
