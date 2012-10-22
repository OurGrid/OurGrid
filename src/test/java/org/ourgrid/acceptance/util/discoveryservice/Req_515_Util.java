package org.ourgrid.acceptance.util.discoveryservice;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.communication.receiver.DiscoveryServiceNotificationReceiver;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;


public class Req_515_Util extends DiscoveryServiceAcceptanceUtil {

	public Req_515_Util(ModuleContext context) {
		super(context);
	}

	public void dsIsDown(DiscoveryServiceComponent component, TestStub dsTestStub){
		
		DiscoveryService localDiscoveryService = (DiscoveryService) getDiscoveryServiceProxy();
		
		DiscoveryServiceNotificationReceiver discoveryService = getDiscoveryServiceNotificationReceiver();
		ObjectDeployment dsOD = getDiscoveryServiceControlDeployment(component);
		
		DiscoveryService ds = (DiscoveryService) dsTestStub.getObject();
		
		AcceptanceTestUtil.publishTestObject(component, dsTestStub.getDeploymentID(), ds, DiscoveryService.class);
		
		AcceptanceTestUtil.setExecutionContext(component, dsOD, dsTestStub.getDeploymentID());

		CommuneLogger logger = component.getLogger();
		
		logger.info("The DS [" + dsTestStub.getDeploymentID().getServiceID().toString() + "] has failed.");
		EasyMock.replay(logger);
		
		discoveryService.dsIsDown(localDiscoveryService, dsTestStub.getDeploymentID());
		
		EasyMock.verify(logger);
		EasyMock.reset(logger);
		
	}
	
	public void dsNotMemberOfNetworkIsDown(DiscoveryServiceComponent component, ServiceID dsServiceID, boolean hasAlreadyFailed) {
		TestStub dsTestStub = new TestStub(new DeploymentID(dsServiceID), null);
		
		dsNotMemberOfNetworkIsDown(component, dsTestStub, hasAlreadyFailed);
	}
	
	
	public void dsNotMemberOfNetworkIsDown(DiscoveryServiceComponent component, TestStub dsTestStub, boolean hasAlreadyFailed){
		CommuneLogger logger = component.getLogger();

		DiscoveryService localDiscoveryService = (DiscoveryService) getDiscoveryServiceProxy();
		DiscoveryServiceNotificationReceiver discoveryService = getDiscoveryServiceNotificationReceiver();
		
		if (dsTestStub.getObject() == null) {
			if (hasAlreadyFailed) {
				logger.warn("The DS [" + dsTestStub.getDeploymentID().getServiceID().toString() + "] has already failed.");
			} else{	
				logger.warn("The DS [" + dsTestStub.getDeploymentID().getServiceID().toString() + "] does not belong to my network.");
			}
			
		}	else {
		
			
			ObjectDeployment dsOD = getDiscoveryServiceControlDeployment(component);
			
			DiscoveryService ds = (DiscoveryService) dsTestStub.getObject();
			
			AcceptanceTestUtil.publishTestObject(component, dsTestStub.getDeploymentID(), ds, DiscoveryService.class);
			
			AcceptanceTestUtil.setExecutionContext(component, dsOD, dsTestStub.getDeploymentID());
			
			
			logger.warn("The DS [" + dsTestStub.getDeploymentID().getServiceID().toString() + "] does not belong to my network.");
		}
		EasyMock.replay(logger);
		
		discoveryService.dsIsDown(localDiscoveryService, dsTestStub.getDeploymentID());
		
		EasyMock.verify(logger);
		EasyMock.reset(logger);
	}
}
