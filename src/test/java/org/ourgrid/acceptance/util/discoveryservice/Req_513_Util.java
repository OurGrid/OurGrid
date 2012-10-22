package org.ourgrid.acceptance.util.discoveryservice;

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.communication.receiver.DiscoveryServiceReceiver;
import org.ourgrid.matchers.WorkerProvidersMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_513_Util extends DiscoveryServiceAcceptanceUtil {

	public Req_513_Util(ModuleContext context) {
		super(context);
	}

	public void dsClientIsUp(DiscoveryServiceComponent component, TestStub dscTestStub, List<TestStub> dsList, List<String> dscServiceIDList, boolean isLogged) {
		CommuneLogger logger = component.getLogger();
		
		ObjectDeployment dsOD = getDiscoveryServiceControlDeployment(component);
		
		DiscoveryServiceReceiver controller = (DiscoveryServiceReceiver) getDiscoveryServiceDeployment(component).getObject();
		
		DiscoveryServiceClient dsc = (DiscoveryServiceClient) dscTestStub.getObject();
		
		AcceptanceTestUtil.publishTestObject(component, dscTestStub.getDeploymentID(), dsc, DiscoveryServiceClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, dsOD, dscTestStub.getDeploymentID());
		
		if ( !isLogged ){
			logger.error("The client [" + dscTestStub.getDeploymentID().getContainerID().getUserAtServer() + "] is not logged.");
			EasyMock.replay(logger);
		}
		else if ( dsList != null ){
			for ( TestStub dsTestStub : dsList ){
				DiscoveryService remoteDS = (DiscoveryService) dsTestStub.getObject();
				remoteDS.hereIsRemoteWorkerProviderList(WorkerProvidersMatcher.eqMatcher(dscServiceIDList));
				EasyMock.replay(remoteDS);
			}
		}
		
		controller.dsClientIsUp(dsc, dscTestStub.getDeploymentID());
		
		if ( !isLogged ){
			EasyMock.verify(logger);
			EasyMock.reset(logger);
		}
		else if ( dsList != null ){
			for ( TestStub dsTestStub : dsList ){
				DiscoveryService remoteDS = (DiscoveryService) dsTestStub.getObject();
				EasyMock.verify(remoteDS);
				EasyMock.reset(remoteDS);
			}
		}
	}

	public void dsClientIsUp(DiscoveryServiceComponent component, TestStub dscTestStub, boolean isLogged) {
		dsClientIsUp(component, dscTestStub, null, null, isLogged);
	}
}