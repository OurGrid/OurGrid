package org.ourgrid.discoveryservice.communication.sender;

import org.ourgrid.common.internal.OurGridResponseControl;
import org.ourgrid.discoveryservice.response.DiscoveryServiceResponseConstants;
import org.ourgrid.peer.communication.sender.DataBaseLoggerSender;

public class DiscoveryServiceResponseControl extends OurGridResponseControl {

	@Override
	protected void addEntitySenders() {
		addSender(DiscoveryServiceResponseConstants.DATA_BASE_LOGGER, new DataBaseLoggerSender());
		addSender(DiscoveryServiceResponseConstants.HERE_IS_COMPLETE_STATUS, new HereIsCompleteStatusSender());
		addSender(DiscoveryServiceResponseConstants.DS_CLIENT_HERE_IS_REMOTE_WORKER_PROVIDERS_LIST, new DSClientHereIsRemoteWorkerProvidersListSender());
		addSender(DiscoveryServiceResponseConstants.DS_HERE_IS_REMOTE_WORKER_PROVIDERS_LIST, new DSHereIsRemoteWorkerProvidersListSender());
		addSender(DiscoveryServiceResponseConstants.DS_HERE_ARE_DISCOVERY_SERVICES, new DSHereAreDiscoveryServicesSender());
		addSender(DiscoveryServiceResponseConstants.PERSIST_NETWORK, new PersistNetworkSender());
		addSender(DiscoveryServiceResponseConstants.DS_CLIENT_HERE_ARE_DISCOVERY_SERVICES, new DSClientHereAreDiscoveryServicesSender());
		addSender(DiscoveryServiceResponseConstants.DS_GET_DISCOVERY_SERVICES, new DSGetDiscoveryServicesSender());
		addSender(DiscoveryServiceResponseConstants.HERE_IS_PEER_STATUS_PROVIDERS, new HereIsPeerStatusProvidersSender());
		addSender(DiscoveryServiceResponseConstants.HERE_IS_PEER_STATUS_CHANGE_HISTORY, new HereIsPeerStatusChangeHistorySender());
		addSender(DiscoveryServiceResponseConstants.OPERATION_SUCCEDED, new OperationSucceedSender());
		addSender(DiscoveryServiceResponseConstants.DS_IS_OVERLOADED, new DSIsOverloadedSender());
	}

}
