package org.ourgrid.broker.communication.sender;

import java.io.File;

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.response.StartTransferResponseTO;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferSender;

public class StartTransferSender implements SenderIF<StartTransferResponseTO> {

	public void execute(StartTransferResponseTO response, ServiceManager manager) {
		
		OutgoingTransferHandle tHandle = new OutgoingTransferHandle(response.getHandleId(), response.getLocalFileName(), 
				new File(response.getLocalFileName()), response.getDescription(), new DeploymentID(response.getId()));
		
		TransferSender sender = (TransferSender) manager.getObjectDeployment(
				BrokerConstants.WORKER_CLIENT).getObject();
		
		manager.startTransfer(tHandle, sender);
		
	}
}
