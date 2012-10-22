package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.internal.OurGridResponseControl;
import org.ourgrid.peer.response.PeerResponseConstants;

public class PeerResponseControl extends OurGridResponseControl {

	
	private static PeerResponseControl instance;
	
	
	public static PeerResponseControl getInstance() {
		if (instance == null) {
			instance = new PeerResponseControl();
		}
		return instance;
	}
	
	protected void addEntitySenders() {
		addSender(PeerResponseConstants.ADD_ACTION_FOR_REPETITION, new AddActionForRepetitionSender());
		addSender(PeerResponseConstants.DATA_BASE_LOGGER, new DataBaseLoggerSender());
		addSender(PeerResponseConstants.LOGIN_SUCCEDED, new LoginSuccededSender());
		addSender(PeerResponseConstants.OPERATION_SUCCEDED, new OperationSucceedSender());
		addSender(PeerResponseConstants.SAVE_RANKING, new SaveRankingSender());
		addSender(PeerResponseConstants.SCHEDULED_ACTION_WITH_FIXED_DELAY, new PeerScheduleActionWithFixedDelaySender());
		addSender(PeerResponseConstants.WORKER_MANAGEMENT_STOP_WORKING, new WorkerManagementStopWorkingSender());
		addSender(PeerResponseConstants.CANCEL_REQUEST_FUTURE, new CancelRequestFutureSender());
		addSender(PeerResponseConstants.HERE_IS_USER_STATUS, new HereIsUserStatusSender());
		addSender(PeerResponseConstants.HERE_IS_LOCAL_WORKERS_STATUS, new HereIsLocalWorkersStatusSender());
		addSender(PeerResponseConstants.HERE_IS_TRUST_STATUS, new HereIsTrustStatusSender());
		addSender(PeerResponseConstants.GET_REMOTE_WORKER_PROVIDERS, new GetRemoteWorkerProvidersSender());
		addSender(PeerResponseConstants.SCHEDULE_DS_ACTION, new ScheduleDSActionSender());
		addSender(PeerResponseConstants.CANCEL_DISCOVERY_SERVICE_ADVERT, new CancelDiscoveryServiceAdvertSender());
		addSender(PeerResponseConstants.WORK_FOR_PEER, new WorkForPeerSender());
		addSender(PeerResponseConstants.REMOTE_HERE_IS_WORKER, new RemoteHereIsWorkerSender());
		addSender(PeerResponseConstants.WORK_FOR_BROKER, new WorkForBrokerSender());
		addSender(PeerResponseConstants.REMOTE_WORKER_PROVIDER_REQUEST_WORKERS, new RemoteWorkerProviderRequestWorkersSender());
		addSender(PeerResponseConstants.SCHEDULE_REQUEST, new ScheduleRequestSender());
		addSender(PeerResponseConstants.LOCAL_HERE_IS_WORKER, new LocalHereIsWorkerSender());
		addSender(PeerResponseConstants.REMOTE_WORK_FOR_BROKER, new RemoteWorkForBrokerSender());
		addSender(PeerResponseConstants.STOP_WORKING, new StopWorkingSender());
		addSender(PeerResponseConstants.HERE_IS_REMOTE_WORKERS_STATUS, new HereIsRemoteWorkersStatusSender());
		addSender(PeerResponseConstants.HERE_IS_REMOTE_CONSUMERS_STATUS, new HereIsRemoteConsumersStatusSender());
		addSender(PeerResponseConstants.HERE_IS_COMPLETE_STATUS, new HereIsCompleteStatusSender());
		addSender(PeerResponseConstants.HERE_IS_NETWORK_OF_FAVORS_STATUS, new HereIsNetworkOfFavorsStatusSender());
		addSender(PeerResponseConstants.HERE_IS_COMPLETE_HISTORY_STATUS, new HereIsCompleteHistoryStatusSender());
		addSender(PeerResponseConstants.HERE_IS_LOCAL_CONSUMERS_STATUS, new HereIsLocalConsumersStatusSender());
		addSender(PeerResponseConstants.PERSIST_DS_NETWORK, new PersistDiscoveryServiceNetworkSender());
		addSender(PeerResponseConstants.DISPOSE_REMOTE_WORKER, new DisposeRemoteWorkerSender());
		addSender(PeerResponseConstants.WORKER_LOGIN_SUCCEEDED, new WorkerLoginSucceededSender());
		addSender(PeerResponseConstants.REMOTE_PREEMPTED_WORKER, new RemotePreemptedWorkerSender());
		addSender(PeerResponseConstants.LOCAL_PREEMPTED_WORKER, new LocalPreemptedWorkerSender());
		addSender(PeerResponseConstants.SCHEDULE_DELAYED_INTEREST_ON_DS_ACTION, new ScheduleDelayedInterestOnDSActionSender());
	}
}
