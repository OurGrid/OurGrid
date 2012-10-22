package org.ourgrid.acceptance.util.discoveryservice;

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.communication.receiver.DiscoveryServiceReceiver;
import org.ourgrid.matchers.WorkerProvidersMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_505_Util extends DiscoveryServiceAcceptanceUtil {

	public Req_505_Util(ModuleContext context) {
		super(context);
	}

	public void getRemoteWorkerProviders(DiscoveryServiceComponent component, List<String> allPeersServiceIds,
			TestStub dscTestStub, List<TestStub> dsTestStubs, boolean isLogged, List<String> localPeersServiceIds) {
		DiscoveryService discoveryService = (DiscoveryService) getDiscoveryServiceProxy();
		ObjectDeployment dsOD = getDiscoveryServiceControlDeployment(component);

		DiscoveryServiceClient dsc = (DiscoveryServiceClient) dscTestStub.getObject();
		dsc.hereIsRemoteWorkerProviderList(WorkerProvidersMatcher.eqMatcher(allPeersServiceIds));

		EasyMock.replay(dsc);
		
		
		if (dsTestStubs != null) {
			for (TestStub testStub : dsTestStubs) {
				DiscoveryService ds = (DiscoveryService) testStub.getObject();
				ds.hereIsRemoteWorkerProviderList(WorkerProvidersMatcher.eqMatcher(localPeersServiceIds));

				EasyMock.replay(ds);
			}
		}

		AcceptanceTestUtil.publishTestObject(component, dscTestStub.getDeploymentID(), dsc, DiscoveryServiceClient.class);

		AcceptanceTestUtil.setExecutionContext(component, dsOD, dscTestStub.getDeploymentID());

		CommuneLogger logger = component.getLogger();

		if (!isLogged) {
			logger.debug("The client [" + dscTestStub.getDeploymentID().getContainerID().getUserAtServer() + "] is not logged.");
			EasyMock.replay(logger);
		}

		discoveryService.getRemoteWorkerProviders(dsc, 1);

		if (!isLogged) {
			EasyMock.verify(logger);
			EasyMock.reset(logger);
		}

		
		EasyMock.verify(dsc);
		EasyMock.reset(dsc);

		if (dsTestStubs != null) {
			for (TestStub testStub : dsTestStubs) {
				DiscoveryService ds = (DiscoveryService) testStub.getObject();
				EasyMock.verify(ds);
				EasyMock.reset(ds);
			}
		}
	}

	public void getRemoteWorkerProviders(DiscoveryServiceComponent component, List<String> serviceIds, TestStub dscTestStub) {
		getRemoteWorkerProviders(component, serviceIds, dscTestStub, null, false, serviceIds);
	}

	public void getRemoteWorkerProviders(DiscoveryServiceComponent component, List<String> serviceIds, TestStub dscTestStub, boolean isLogged) {
		getRemoteWorkerProviders(component, serviceIds, dscTestStub, null, isLogged, serviceIds);
	}

	public void getRemoteWorkerProviders(DiscoveryServiceComponent component, List<String> serviceIds, TestStub dscTestStub,
			List<TestStub> dsTestStubs) {
		getRemoteWorkerProviders(component, serviceIds, dscTestStub, dsTestStubs, false, serviceIds);
	}

	public void notifyDiscoveryServiceClientFailure(DiscoveryServiceComponent component, TestStub dscTestStub) {
		component.setStubDown(dscTestStub.getObject());
		DiscoveryServiceReceiver controller = (DiscoveryServiceReceiver) getDiscoveryServiceDeployment(component).getObject();
		controller.dsClientIsDown((DiscoveryServiceClient) dscTestStub.getObject(), dscTestStub.getDeploymentID());
	}

	public TestStub createRemoteWorkerProvider(ServiceID serviceID){
		DeploymentID rwpID = new DeploymentID(serviceID);
		RemoteWorkerProvider rwp = EasyMock.createMock(RemoteWorkerProvider.class);
		return new TestStub(rwpID, rwp);
	}

	public TestStub createDiscoveryServiceClient(ServiceID serviceID){
		DeploymentID dscID = new DeploymentID(serviceID);
		DiscoveryServiceClient rwp = EasyMock.createMock(DiscoveryServiceClient.class);
		return new TestStub(dscID, rwp);
	}

	public TestStub createPeerStatusProvider(ServiceID serviceID){
		DeploymentID peerStatusProviderID = new DeploymentID(serviceID);
		PeerStatusProvider peerStatusProvider = EasyMock.createMock(PeerStatusProvider.class);
		return new TestStub(peerStatusProviderID, peerStatusProvider);
	}

	public void hereIsRemoteWorkerProvidersList(DiscoveryServiceComponent component, List<String> dscServiceIDList,
			TestStub dsTestStub, boolean rwpListIsDifferent, List<ServiceID> dsServiceIDList) {

		CommuneLogger logger = component.getLogger();
		
		if (! dsServiceIDList.contains(dsTestStub.getDeploymentID().getServiceID())) {
			logger.debug("The DS [" + dsTestStub.getDeploymentID().getServiceID() + "] tried to send a Remote Worker Providers List but it does not belong to my network.");
			EasyMock.replay(logger);
		}
		
		DiscoveryService discoveryService = (DiscoveryService) getDiscoveryServiceProxy();

		DiscoveryService ds = (DiscoveryService) dsTestStub.getObject();

		if (rwpListIsDifferent) {
			EasyMock.reset(ds);
			ds.hereIsRemoteWorkerProviderList(WorkerProvidersMatcher.eqMatcher(dscServiceIDList));
			EasyMock.replay(ds);
		}
		
		ObjectDeployment dsOD = getDiscoveryServiceControlDeployment(component);
		AcceptanceTestUtil.setExecutionContext(component, dsOD, dsTestStub.getDeploymentID());

		discoveryService.hereIsRemoteWorkerProviderList(dscServiceIDList);

		if (! dsServiceIDList.contains(dsTestStub.getDeploymentID().getServiceID())) {
			EasyMock.verify(logger);
			EasyMock.reset(logger);
		}
		
		if (rwpListIsDifferent) {
			EasyMock.verify(ds);
		}

	}
}
