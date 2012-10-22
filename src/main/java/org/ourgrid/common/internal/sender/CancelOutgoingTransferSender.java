package org.ourgrid.common.internal.sender;

import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.CancelOutgoingTransferResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;

public class CancelOutgoingTransferSender implements SenderIF<CancelOutgoingTransferResponseTO> {

	public void execute(CancelOutgoingTransferResponseTO response, ServiceManager manager) {
		OutgoingHandle outgoingHandle = response.getOutgoingHandle();
		
		OutgoingTransferHandle outgoingTransferHandle = new OutgoingTransferHandle(outgoingHandle.getId(), 
				outgoingHandle.getLocalFile().getPath(), outgoingHandle.getLocalFile(), 
				outgoingHandle.getDescription(), new DeploymentID(outgoingHandle.getDestinationID()));
		
		
		manager.cancelOutgoingTransfer(outgoingTransferHandle);
	}
}
