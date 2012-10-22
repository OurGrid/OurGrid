package org.ourgrid.acceptance.util.discoveryservice;

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.matchers.WorkerProvidersMatcher;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_506_Util extends DiscoveryServiceAcceptanceUtil {

	public Req_506_Util(ModuleContext context) {
		super(context);
	}
	
	public void leaveCommunity(DiscoveryServiceComponent component, TestStub dsClientTestStub) {
		leaveCommunity(component, dsClientTestStub, true, null, null);
	}
	
	public void leaveCommunity(DiscoveryServiceComponent component, TestStub dsClientTestStub, List<TestStub> dsTestStubList, List<String> dscID) {
		leaveCommunity(component, dsClientTestStub, true, dsTestStubList, dscID);
	}
	
	public void leaveCommunity(DiscoveryServiceComponent component, TestStub dsClientTestStub, boolean isCommunityMember, 
			List <TestStub> dsTestStubList, List<String> dscIDList ) {

		CommuneLogger logger = component.getLogger();
		
		DiscoveryService discoveryService = (DiscoveryService) getDiscoveryServiceProxy();
		
		DiscoveryServiceClient dsClient = (DiscoveryServiceClient) dsClientTestStub.getObject();
		
		if (!isCommunityMember) {
			logger.warn("The client with ID [" + dsClientTestStub.getDeploymentID().getServiceID() + "] is not joined to the community.");
			EasyMock.replay(logger);
			AcceptanceTestUtil.publishTestObject(component, dsClientTestStub.getDeploymentID(), dsClient,
					DiscoveryServiceClient.class);
			discoveryService.leaveCommunity(dsClient);
				
			EasyMock.verify(logger);
		} else {
			if (dsTestStubList != null){ 
				for (TestStub dsStub : dsTestStubList){
					DiscoveryService ds = (DiscoveryService) dsStub.getObject();
//					EasyMock.reset(ds);
					ds.hereIsRemoteWorkerProviderList(WorkerProvidersMatcher.eqMatcher(dscIDList));					
					EasyMock.replay(ds);		
				}
			}	
			
			discoveryService.leaveCommunity(dsClient);
			
			if (dsTestStubList != null){ 
				for (TestStub dsStub : dsTestStubList){
					DiscoveryService ds = (DiscoveryService) dsStub.getObject();
					EasyMock.verify(ds);
					EasyMock.reset(ds);
				}
			}
		}
		
		EasyMock.reset(logger);
	}

}
