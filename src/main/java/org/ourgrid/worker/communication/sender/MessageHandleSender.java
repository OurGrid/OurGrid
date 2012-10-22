package org.ourgrid.worker.communication.sender;

import org.ourgrid.broker.communication.actions.ErrorOcurredMessageHandle;
import org.ourgrid.common.interfaces.WorkerClient;
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
		WorkerClient workerClient = (WorkerClient) manager.getStub(serviceID, WorkerClient.class);
		
		if (workerClient != null) {
			workerClient.sendMessage(response.getMessageHandle());
		}
	}

}
