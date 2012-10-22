package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.communication.actions.ErrorOcurredMessageHandle;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.MessageHandleResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class MessageHandleSender implements SenderIF<MessageHandleResponseTO>{

	public void execute(MessageHandleResponseTO response,
			ServiceManager manager) {
		
		MessageHandle messageHandle = response.getMessageHandle();
		
		if (response.isErrorMessage()) {
			ErrorOcurredMessageHandle errorMessageHandle = (ErrorOcurredMessageHandle) messageHandle;
			manager.getLog().error("Error ocurred: " + errorMessageHandle.getGridProcessError().getType().getDescription(), 
					errorMessageHandle.getGridProcessError().getErrorCause());
		}
		
		ServiceID serviceID = ServiceID.parse(response.getClientAddress());
		Worker worker = (Worker) manager.getStub(serviceID, Worker.class);
		
		if (worker != null)
			worker.sendMessage(response.getMessageHandle());
	}

}
