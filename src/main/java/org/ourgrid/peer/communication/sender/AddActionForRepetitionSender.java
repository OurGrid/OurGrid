package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.AddActionForRepetitionResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;

public class AddActionForRepetitionSender implements SenderIF<AddActionForRepetitionResponseTO>{

	public void execute(AddActionForRepetitionResponseTO response,
			ServiceManager manager) {
		
		Class<?> clazz = response.getActionClass();
		
		try {
			RepeatedAction repeatedAction = (RepeatedAction) clazz.getConstructor().newInstance();
			
			manager.addActionForRepetition(response.getActionName(), 
					repeatedAction);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
}
