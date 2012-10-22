package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.response.LoginResponseTO;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class LoginSender implements SenderIF<LoginResponseTO> {

	public void execute(LoginResponseTO response,
			ServiceManager manager) {
		ServiceID serviceID = ServiceID.parse(response.getStubAddress());
		
		LocalWorkerProviderClient lwpc = getLocalWorkerProviderClient(manager);
		
		LocalWorkerProvider lwp = (LocalWorkerProvider) manager.getStub(serviceID, LocalWorkerProvider.class);
		
		lwp.login(lwpc);
	}

	private LocalWorkerProviderClient getLocalWorkerProviderClient(ServiceManager manager) {
		ObjectDeployment objectDeployment = manager.getObjectDeployment(BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) objectDeployment.getObject();
		
		return (LocalWorkerProviderClient) lwpc;
	}
	
}
