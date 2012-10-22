package org.ourgrid.discoveryservice.business.requester;

import org.ourgrid.common.internal.OurGridRequestConstants;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.internal.ResponseControlIF;
import org.ourgrid.common.internal.requester.QueryRequester;
import org.ourgrid.discoveryservice.communication.sender.DiscoveryServiceResponseControl;
import org.ourgrid.discoveryservice.request.DiscoveryServiceRequestConstants;

public class DiscoveryServiceRequestControl extends OurGridRequestControl {

	@Override
	protected ResponseControlIF createResponseControl() {
		return new DiscoveryServiceResponseControl();
	}

	@Override
	protected void fillMap() {
		addRequester(OurGridRequestConstants.QUERY, new QueryRequester());
		addRequester(DiscoveryServiceRequestConstants.START_DISCOVERY_SERVICE, new StartDiscoveryServiceRequester());
		addRequester(DiscoveryServiceRequestConstants.GET_COMPLETE_STATUS, new GetCompleteStatusRequester());
		addRequester(DiscoveryServiceRequestConstants.LEAVE_COMMUNITY, new LeaveCommunityRequester());
		addRequester(DiscoveryServiceRequestConstants.GET_REMOTE_WORKER_PROVIDERS, new GetRemoteWorkerProvidersRequester());
		addRequester(DiscoveryServiceRequestConstants.DS_CLIENT_IS_UP, new DSClientIsUpRequester());
		addRequester(DiscoveryServiceRequestConstants.DS_CLIENT_IS_DOWN, new DSClientIsDownRequester());
		addRequester(DiscoveryServiceRequestConstants.DS_GET_DISCOVERY_SERVICES, new DSGetDiscoveryServicesRequester());
		addRequester(DiscoveryServiceRequestConstants.HERE_ARE_DISCOVERY_SERVICES, new HereAreDiscoveryServicesRequester());
		addRequester(DiscoveryServiceRequestConstants.DS_CLIENT_GET_DISCOVERY_SERVICES, new DSClientGetDiscoveryServicesRequester());
		addRequester(DiscoveryServiceRequestConstants.HERE_IS_REMOTE_WORKER_PROVIDER_LIST, new HereIsRemoteWorkerProviderListRequester());
		addRequester(DiscoveryServiceRequestConstants.DS_IS_DOWN, new DSIsDownRequester());
		addRequester(DiscoveryServiceRequestConstants.DS_IS_UP, new DSIsUpRequester());
		addRequester(DiscoveryServiceRequestConstants.GET_PEER_STATUS_PROVIDERS, new GetPeerStatusProvidersRequester());
		addRequester(DiscoveryServiceRequestConstants.GET_PEER_STATUS_CHANGE_HISTORY, new GetPeerStatusChangeHistoryRequester());
	}

}
