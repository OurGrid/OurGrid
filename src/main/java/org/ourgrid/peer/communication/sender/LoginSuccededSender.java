package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.response.LoginSuccededResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class LoginSuccededSender implements SenderIF<LoginSuccededResponseTO>{

	public void execute(LoginSuccededResponseTO response,
			ServiceManager manager) {
		
		LocalWorkerProviderClient workerProviderClient = (LocalWorkerProviderClient) manager.getStub(
				ServiceID.parse(response.getWorkerProviderClientAddress()), LocalWorkerProviderClient.class);
		
		LocalWorkerProvider localWorkerProvider = (LocalWorkerProvider) manager.getObjectDeployment(
				PeerConstants.LOCAL_WORKER_PROVIDER).getObject();
		
		workerProviderClient.loginSucceed(localWorkerProvider, response.getLoginResult());
	}

	
}
