package org.ourgrid.acceptance.util.discoveryservice;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.status.DiscoveryServiceStatusProviderClient;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.communication.receiver.DiscoveryServiceControlReceiver;
import org.ourgrid.matchers.DiscoveryServiceCompleteStatusMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_504_Util extends DiscoveryServiceAcceptanceUtil {
	
	public Req_504_Util(ModuleContext context) {
		super(context);
	}
	
	public void getEmptyCompleteStatusWithStartedDS(DiscoveryServiceComponent component, Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork) {
		getCompleteStatus(component, expectedLocalDSNetwork, true);
	}
	
	public void getEmptyCompleteStatus(DiscoveryServiceComponent component, boolean isStarted) {
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(
				getDiscoveryServiceControlDeployment(component).getDeploymentID().getServiceID().toString(), true),
				new HashSet<String>());
		
		getCompleteStatus(component, expectedLocalDSNetwork, isStarted);
	}
	
	public void getCompleteStatus(DiscoveryServiceComponent component, Map<DiscoveryServiceInfo, Set<String>> network) {
		getCompleteStatus(component, network, true);
	}
	
	public void getCompleteStatus(DiscoveryServiceComponent component, Map<DiscoveryServiceInfo, Set<String>> network, boolean isStarted) {
		DiscoveryServiceControlReceiver dscc = (DiscoveryServiceControlReceiver) getDiscoveryServiceControl(component);
		ObjectDeployment dsOD = getDiscoveryServiceControlDeployment(component);
		DiscoveryServiceStatusProviderClient statusProviderMock = EasyMock.createMock(DiscoveryServiceStatusProviderClient.class);
		
		CommuneLogger oldLogger = component.getLogger();
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		component.setLogger(newLogger);
		
		DeploymentID deploymentID = new DeploymentID(new ContainerID("dsClient", "dsServer", "peer", "peerPK"), "peer");
		AcceptanceTestUtil.publishTestObject(component, deploymentID, statusProviderMock,
				DiscoveryServiceStatusProviderClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, dsOD, deploymentID);

		if (isStarted) {
			statusProviderMock.hereIsCompleteStatus(DiscoveryServiceCompleteStatusMatcher.eqMatcher(network));
		} else {
			newLogger.warn("Received a status request from: " + deploymentID.getServiceID() + ", but the component is not started.");
		}
		
		EasyMock.replay(newLogger);
		EasyMock.replay(statusProviderMock);
		
		dscc.getCompleteStatus(statusProviderMock);
		
		EasyMock.verify(newLogger);
		EasyMock.verify(statusProviderMock);

		component.setLogger(oldLogger);
	}

	
}
