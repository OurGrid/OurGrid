package org.ourgrid.acceptance.util.discoveryservice;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.control.DiscoveryServiceControl;
import org.ourgrid.common.interfaces.control.DiscoveryServiceControlClient;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.matchers.ControlOperationResultMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleNotStartedException;
import br.edu.ufcg.lsd.commune.container.control.ModuleStoppedException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_503_Util extends DiscoveryServiceAcceptanceUtil {

	public Req_503_Util(ModuleContext context) {
		super(context);
	}

	public void stopUnstartedDiscoveryService(DiscoveryServiceComponent component) {
		stopDiscoveryService(component, null, false);
	}
	
	public void stopDiscoveryService(DiscoveryServiceComponent component) {
		stopDiscoveryService(component, null, true);
	}
	
	public void stopDiscoveryService(DiscoveryServiceComponent component, String senderPublicKey) {
		stopDiscoveryService(component, senderPublicKey, true);
	}
	
	public void stopDiscoveryService(DiscoveryServiceComponent component, boolean isDSStarted) {
		stopDiscoveryService(component, null, isDSStarted);
	}
	
	public void stopDiscoveryService(DiscoveryServiceComponent component, String senderPublicKey, 
			boolean isDSStarted) {
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		DiscoveryServiceControl dsControl = getDiscoveryServiceControl(component);
		ObjectDeployment dsOD = getDiscoveryServiceControlDeployment(component);
		
		DiscoveryServiceControlClient dscMock = EasyMock.createMock(DiscoveryServiceControlClient.class);
			
		if (!isDSStarted) {
			if(dsControl == null) {
				dscMock.operationSucceed(
						ControlOperationResultMatcher.eqType(ModuleStoppedException.class));
			} else {
				if (senderPublicKey == null) {
					senderPublicKey = dsOD.getDeploymentID().getPublicKey();
				}	
				dscMock.operationSucceed(
						ControlOperationResultMatcher.eqCauseType("DiscoveryService control was not started", ModuleNotStartedException.class));
			}
		}  else {
			if (senderPublicKey == null) {
				senderPublicKey = dsOD.getDeploymentID().getPublicKey();
				dscMock.operationSucceed(ControlOperationResultMatcher.noError());
			} else if(!dsOD.getDeploymentID().getPublicKey().equals(senderPublicKey)) {
				newLogger.warn("An unknown entity tried to stop the Discovery Service. Only the local modules can perform this operation." +
						" Unknown entity public key: [" + senderPublicKey + "].");
			}
		}
		
		
		EasyMock.replay(newLogger);
		EasyMock.replay(dscMock);
	
		AcceptanceTestUtil.setExecutionContext(component, dsOD, senderPublicKey);
		dsControl.stop(false, false, dscMock);
		
		EasyMock.verify(dscMock);
		EasyMock.verify(newLogger);
		EasyMock.reset(newLogger);
	}
}
