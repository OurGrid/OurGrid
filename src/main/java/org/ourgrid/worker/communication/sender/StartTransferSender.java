package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.response.StartTransferResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;

public class StartTransferSender implements SenderIF<StartTransferResponseTO> {

	public void execute(StartTransferResponseTO response, ServiceManager manager) {
		OutgoingHandle outgoingHandle = response.getOutgoingHandle();

		OutgoingTransferHandle outgoingTransferHandle = new OutgoingTransferHandle(outgoingHandle.getId(), 
				outgoingHandle.getLogicalFileName(), outgoingHandle.getLocalFile(), 
				outgoingHandle.getDescription(), new DeploymentID(outgoingHandle.getDestinationID()));
		
		Worker worker = (Worker) manager.getObjectDeployment(
				WorkerConstants.WORKER).getObject();
		
		manager.startTransfer(outgoingTransferHandle, worker);
	}
}
