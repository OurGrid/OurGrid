package org.ourgrid.acceptance.util.discoveryservice;

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_507_Util extends DiscoveryServiceAcceptanceUtil {

	public Req_507_Util(ModuleContext context) {
		super(context);
	}

	public void hereAreDiscoveryServices(DiscoveryServiceComponent component, TestStub dsTestStub, boolean senderIsUnknown, 
			List<ServiceID> discoveryServicesExpectedList, boolean senderIsDown){

		DiscoveryService discoveryService = (DiscoveryService) getDiscoveryServiceProxy();

		ObjectDeployment dsOD = getDiscoveryServiceControlDeployment(component);
				
		CommuneLogger logger = component.getLogger();
		
		if ( senderIsUnknown ){
			logger.debug("The DS [" + dsTestStub.getDeploymentID().getServiceID() + "] tried to send a Discovery Services List but it does not belong to my network.");
			EasyMock.replay(logger);
		}
		if ( senderIsDown ){
			logger.debug("The DS [" + dsTestStub.getDeploymentID().getServiceID() + "] is down.");
			EasyMock.replay(logger);
		}
		
		AcceptanceTestUtil.setExecutionContext(component, dsOD, dsTestStub.getDeploymentID());

		discoveryService.hereAreDiscoveryServices(discoveryServicesExpectedList);
		
		if ( senderIsUnknown || senderIsDown ){
			EasyMock.verify(logger);
			EasyMock.reset(logger);
		}
	}
	
	public void hereAreDiscoveryServices(DiscoveryServiceComponent component, TestStub dsTestStub, 
			boolean senderIsUnknown, List<ServiceID> discoveryServicesExpectedList){
		hereAreDiscoveryServices(component, dsTestStub, senderIsUnknown, discoveryServicesExpectedList, false);
	}
}