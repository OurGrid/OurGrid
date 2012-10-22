package org.ourgrid.acceptance.util.discoveryservice;

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.DiscoveryServiceAcceptanceUtil;
import org.ourgrid.common.interfaces.CommunityStatusProvider;
import org.ourgrid.common.interfaces.CommunityStatusProviderClient;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_509_Util extends DiscoveryServiceAcceptanceUtil {

	public Req_509_Util(ModuleContext context) {
		super(context);
	}
	
	public void getPeerStatusProviders(DiscoveryServiceComponent component, List<String> psps) {
		
		CommunityStatusProvider communityStatusProvider = getCommunityStatusProviders(component);
		ObjectDeployment dsObjectDeployment = getCommunityStatusProvidersObjectDeployment(component);
		
		CommunityStatusProviderClient client = EasyMock.createMock(CommunityStatusProviderClient.class);
		
		DeploymentID clientID = new DeploymentID(new ServiceID(new ContainerID("clientUser", "clientServer", "clientModule"), 
				"CmmStatusProviderClient"));
		
		AcceptanceTestUtil.publishTestObject(component, clientID, client, CommunityStatusProviderClient.class);
		AcceptanceTestUtil.setExecutionContext(component, dsObjectDeployment, clientID);
		
		//recording mock behavior
		client.hereIsStatusProviderList(psps);
		
		EasyMock.replay(client);

		communityStatusProvider.getPeerStatusProviders(client);
		
		EasyMock.verify(client);
	}
}
