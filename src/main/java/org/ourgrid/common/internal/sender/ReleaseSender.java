package org.ourgrid.common.internal.sender;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.ReleaseResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class ReleaseSender implements SenderIF<ReleaseResponseTO>{

	public void execute(ReleaseResponseTO response,
			ServiceManager manager) {
		manager.release(ServiceID.parse(response.getStubAddress()));		
	}
	
}
