package org.ourgrid.acceptance.util.discoveryservice;

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.matchers.DiscoveryServicesServiceIDListMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_511_Util extends DiscoveryServiceAcceptanceUtil {

	public Req_511_Util(ModuleContext context) {
		super(context);
	}

	public void getDiscoveryServices(DiscoveryServiceComponent component, TestStub dsTestStub, List<ServiceID>
		dsList, boolean modified) {
		DiscoveryService discoveryService = (DiscoveryService) getDiscoveryServiceProxy();
		ObjectDeployment dsOD = getDiscoveryServiceControlDeployment(component);
		
		DiscoveryService ds = (DiscoveryService) dsTestStub.getObject();
		
		AcceptanceTestUtil.publishTestObject(component, dsTestStub.getDeploymentID(), ds, DiscoveryService.class);
		
		AcceptanceTestUtil.setExecutionContext(component, dsOD, dsTestStub.getDeploymentID());

		CommuneLogger logger = component.getLogger();
		
		logger.debug("The Discovery Service " +  dsTestStub.getDeploymentID().getServiceID() + " requested my network list");
		EasyMock.replay(logger);
		
		ds.hereAreDiscoveryServices(DiscoveryServicesServiceIDListMatcher.eqMatcher(dsList));
		EasyMock.replay(ds);
		
		discoveryService.getDiscoveryServices(ds);
		
		EasyMock.verify(logger);
		EasyMock.verify(ds);
		EasyMock.reset(logger);
		EasyMock.reset(ds);
	}
	
	public void getDiscoveryServices(DiscoveryServiceComponent component, List<ServiceID> dsList, TestStub dsTestStub) {
		getDiscoveryServices(component, dsTestStub, dsList, true);
	}
	
	public TestStub createDiscoveryService(ServiceID serviceID){
		DeploymentID dsID = new DeploymentID(serviceID);
		DiscoveryService rwp = EasyMock.createMock(DiscoveryService.class);
		return new TestStub(dsID, rwp);
	}
}
