package org.ourgrid.acceptance.util.discoveryservice;

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.matchers.DiscoveryServicesListSizeMatcher;
import org.ourgrid.matchers.DiscoveryServicesStringListMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_512_Util extends DiscoveryServiceAcceptanceUtil {

	public Req_512_Util(ModuleContext context) {
		super(context);
	}

	public void getDiscoveryServices(DiscoveryServiceComponent component, TestStub dscTestStub, List<String>
		dsList, int size, int maxSize) {
		DiscoveryService discoveryService = (DiscoveryService) getDiscoveryServiceProxy();
		ObjectDeployment dsOD = getDiscoveryServiceControlDeployment(component);
		
		DiscoveryServiceClient dsc = (DiscoveryServiceClient) dscTestStub.getObject();
		
		AcceptanceTestUtil.publishTestObject(component, dscTestStub.getDeploymentID(), dsc, DiscoveryServiceClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, dsOD, dscTestStub.getDeploymentID());
		
		if (size < maxSize) {
			dsc.hereAreDiscoveryServices(DiscoveryServicesStringListMatcher.eqMatcher(dsList));
		} else {
			dsc.hereAreDiscoveryServices(DiscoveryServicesListSizeMatcher.eqMatcher(dsList, maxSize));
		}
			
		EasyMock.replay(dsc);
		
		discoveryService.getDiscoveryServices(dsc);
		
		EasyMock.verify(dsc);
		EasyMock.reset(dsc);
	}
	
	public void getDiscoveryServices(DiscoveryServiceComponent component, List<String> dsList, TestStub dsTestStub, int size) {
		getDiscoveryServices(component, dsTestStub, dsList, size, 10);
	}
}
