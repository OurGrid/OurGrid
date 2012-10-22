package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.response.AcceptTransferResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;

public class AcceptTransferSender implements SenderIF<AcceptTransferResponseTO>{

	public void execute(AcceptTransferResponseTO response, ServiceManager manager) {
		IncomingHandle incomingHandle = response.getIncomingHandle();
		
		ContainerID senderContainerID = ContainerID.parse(incomingHandle.getSenderContainerID());
		senderContainerID.setPublicKey(incomingHandle.getSenderPublicKey());
		
		IncomingTransferHandle incomingTransferHandle = new IncomingTransferHandle(incomingHandle.getId(), 
				incomingHandle.getLogicalFileName(), incomingHandle.getDescription(), incomingHandle.getFileSize(), 
				senderContainerID);
		
		incomingTransferHandle.setExecutable(incomingHandle.isExecutable());
		incomingTransferHandle.setReadable(incomingHandle.isReadable());
		incomingTransferHandle.setWritable(incomingHandle.isWritable());
		
		Worker worker = (Worker) manager.getObjectDeployment(WorkerConstants.WORKER).getObject();
		
		manager.acceptTransfer(incomingTransferHandle,  worker, response.getFile());
	}

}
