package org.ourgrid.acceptance.util.discoveryservice;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.control.DiscoveryServiceControl;
import org.ourgrid.common.interfaces.control.DiscoveryServiceControlClient;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.matchers.ControlOperationResultMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleAlreadyStartedException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_502_Util extends DiscoveryServiceAcceptanceUtil {
	
	public Req_502_Util(ModuleContext context) {
		super(context);
	}
	
	public DiscoveryServiceComponent startDiscoveryService() throws Exception {
		DiscoveryServiceComponent component = createDiscoveryServiceComponent();
		return startDiscoveryService(component, null, false);
	}

	public DiscoveryServiceComponent startDiscoveryServiceWithWrongPublicKey(String senderPublicKey) throws Exception {
		DiscoveryServiceComponent component = createDiscoveryServiceComponent();
		return startDiscoveryService(component, senderPublicKey, false);
	}

	public DiscoveryServiceComponent startDiscoveryServiceAgain(DiscoveryServiceComponent component) throws Exception {
		return startDiscoveryService(component, null, true);
	}
	
	private DiscoveryServiceComponent startDiscoveryService(DiscoveryServiceComponent component, String senderPublicKey, boolean isDSAlreadyStarted) {
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		DiscoveryServiceControl dsControl = getDiscoveryServiceControl(component);
		ObjectDeployment dsOD = getDiscoveryServiceControlDeployment(component);
		
		DiscoveryServiceControlClient dsClientMock = EasyMock.createMock(DiscoveryServiceControlClient.class);
		
		if (senderPublicKey == null) {
			senderPublicKey = dsOD.getDeploymentID().getPublicKey();
		}
		
		if (isDSAlreadyStarted) {
			dsClientMock.operationSucceed(ControlOperationResultMatcher.eqType(ModuleAlreadyStartedException.class));
		} else {
			if (dsOD.getDeploymentID().getPublicKey().equals(senderPublicKey)) {
				dsClientMock.operationSucceed(ControlOperationResultMatcher.noError());
				newLogger.info("Discovery Service has been successfully started.");
			} else {
				newLogger.warn("An unknown entity tried to start the Discovery Service. " +
						"Only the local modules can perform this operation. Unknown entity public key: [" + senderPublicKey + "].");
			}
		}
		
		EasyMock.replay(newLogger);
		EasyMock.replay(dsClientMock);
		
		AcceptanceTestUtil.setExecutionContext(component, dsOD, senderPublicKey);
		dsControl.start(dsClientMock);
		
		EasyMock.verify(dsClientMock);
		EasyMock.verify(newLogger);
		
		EasyMock.reset(newLogger);
		
		return component;
	}

}
