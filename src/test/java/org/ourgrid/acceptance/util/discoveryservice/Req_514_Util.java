package org.ourgrid.acceptance.util.discoveryservice;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.communication.receiver.DiscoveryServiceNotificationReceiver;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;


public class Req_514_Util extends DiscoveryServiceAcceptanceUtil {

	public Req_514_Util(ModuleContext context) {
		super(context);
	}

	public void dsIsUp(DiscoveryServiceComponent component, TestStub dsTestStub){
		
		CommuneLogger logger = component.getLogger();
		
		DiscoveryService localDiscoveryService = (DiscoveryService) getDiscoveryServiceProxy();
		
		DiscoveryServiceNotificationReceiver discoveryService = getDiscoveryServiceNotificationReceiver();
		ObjectDeployment dsOD = getDiscoveryServiceControlDeployment(component);
				
		DiscoveryService ds = (DiscoveryService) dsTestStub.getObject();
		
		AcceptanceTestUtil.publishTestObject(component, dsTestStub.getDeploymentID(), ds, DiscoveryService.class);
		
		AcceptanceTestUtil.setExecutionContext(component, dsOD, dsTestStub.getDeploymentID());
		
		logger.info("The DS [" + dsTestStub.getDeploymentID().getServiceID().toString() + "] is up.");
		EasyMock.replay(logger);
			
		discoveryService.dsIsUp(localDiscoveryService, dsTestStub.getDeploymentID());
		
		EasyMock.verify(logger);
		EasyMock.reset(logger);
		
	}
	
	public void notMemberOfNetworkDSIsUp(DiscoveryServiceComponent component, TestStub dsTestStub){
		
		CommuneLogger logger = component.getLogger();

		DiscoveryService localDiscoveryService = (DiscoveryService) getDiscoveryServiceProxy();
		DiscoveryServiceNotificationReceiver discoveryService = getDiscoveryServiceNotificationReceiver();
		
		logger.warn("The DS [" + dsTestStub.getDeploymentID().getServiceID().toString() + "] does not belong to my network.");
		EasyMock.replay(logger);
		
		discoveryService.dsIsDown(localDiscoveryService, dsTestStub.getDeploymentID());
		
		EasyMock.verify(logger);
		EasyMock.reset(logger);
	}
}
