package org.ourgrid.discoveryservice.communication.receiver;

import org.ourgrid.common.interfaces.CommunityStatusProvider;
import org.ourgrid.common.interfaces.CommunityStatusProviderClient;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.request.GetPeerStatusChangeHistoryRequestTO;
import org.ourgrid.discoveryservice.request.GetPeerStatusProvidersRequestTO;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class CommunityStatusProviderReceiver implements
		CommunityStatusProvider {

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	

	public void getPeerStatusProviders(@MonitoredBy(DiscoveryServiceConstants.COMMUNITY_STATUS_PROVIDER) 
			CommunityStatusProviderClient client) {
		GetPeerStatusProvidersRequestTO to = new GetPeerStatusProvidersRequestTO();
		to.setClientAddress(serviceManager.getStubDeploymentID(client).getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	public void getPeerStatusChangeHistory(@MonitoredBy(DiscoveryServiceConstants.COMMUNITY_STATUS_PROVIDER) 
			CommunityStatusProviderClient client, long since) {
		GetPeerStatusChangeHistoryRequestTO to = new GetPeerStatusChangeHistoryRequestTO();
		to.setClientAddress(serviceManager.getStubDeploymentID(client).getServiceID().toString());
		to.setSince(since);
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	@RecoveryNotification
	public void communityStatusProviderClientIsUp(CommunityStatusProviderClient client) {
		
	}
	
	@FailureNotification
	public void communityStatusProviderClientIsDown(CommunityStatusProviderClient client) {
		
	}
}
