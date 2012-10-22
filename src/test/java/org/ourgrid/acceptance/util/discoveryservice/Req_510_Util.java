package org.ourgrid.acceptance.util.discoveryservice;

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.CommunityStatusProvider;
import org.ourgrid.common.interfaces.CommunityStatusProviderClient;
import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.matchers.DS_PeerStatusChangeHistoryMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_510_Util extends DiscoveryServiceAcceptanceUtil{

	public Req_510_Util(ModuleContext context) {
		super(context);
	}

	public void getPeerStatusChangeHistory(DiscoveryServiceComponent component, 
			List<DS_PeerStatusChange> historyList) {
		
		CommunityStatusProvider communityStatusProvider = getCommunityStatusProviders(component);
		ObjectDeployment cspObjectDeployment = getCommunityStatusProvidersObjectDeployment(component);
		
		CommunityStatusProviderClient cspClientMock = EasyMock.createMock(CommunityStatusProviderClient.class);
		
		//adding behavior
		cspClientMock.hereIsPeerStatusChangeHistory(DS_PeerStatusChangeHistoryMatcher.eqMatcher((historyList)), 
				EasyMock.gt(0L));

		EasyMock.replay(cspClientMock);
		
		DeploymentID deploymentID = new DeploymentID(new ContainerID("dsClient", "dsServer", "peer", "dsClientPK"), "peer");
		AcceptanceTestUtil.publishTestObject(component, deploymentID, cspClientMock,
				CommunityStatusProviderClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, cspObjectDeployment, deploymentID);
		
		communityStatusProvider.getPeerStatusChangeHistory(cspClientMock, 0L);
		
		EasyMock.verify(cspClientMock);
	}
}
