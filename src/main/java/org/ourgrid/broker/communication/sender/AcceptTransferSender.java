package org.ourgrid.broker.communication.sender;

import java.io.File;

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.response.AcceptTransferResponseTO;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferReceiver;

public class AcceptTransferSender implements SenderIF<AcceptTransferResponseTO> {
	
	public void execute(AcceptTransferResponseTO response,
			ServiceManager manager) {
		
		IncomingTransferHandle tHandle = new IncomingTransferHandle(response.getId(), response.getLogicalFileName(), 
				response.getDescription(), response.getFileSize(), 
				ContainerID.parse(response.getSenderContainerID()));
		
		tHandle.setExecutable(response.isExecutable());
		tHandle.setReadable(response.isReadable());
		tHandle.setWritable(response.isWritable());
		
		TransferReceiver receiver = (TransferReceiver) manager.getObjectDeployment(
				BrokerConstants.WORKER_CLIENT).getObject();
		
		manager.acceptTransfer(tHandle, receiver, new File(response.getLocalFilePath()));
		
	}
}
