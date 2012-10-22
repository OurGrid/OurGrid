/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.peer.business.requester;

import org.ourgrid.common.internal.OurGridRequestConstants;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.internal.ResponseControlIF;
import org.ourgrid.common.internal.requester.QueryRequester;
import org.ourgrid.peer.communication.sender.PeerResponseControl;
import org.ourgrid.peer.request.PeerRequestConstants;

public class PeerRequestControl extends OurGridRequestControl {
	
	protected void fillMap() {
		addRequester(OurGridRequestConstants.QUERY, new QueryRequester());
		addRequester(PeerRequestConstants.ADD_USER, new AddUserRequester());
		addRequester(PeerRequestConstants.LOGIN, new LoginRequester());
		addRequester(PeerRequestConstants.SAVE_RANKING, new SaveRankingRequester());
		addRequester(PeerRequestConstants.START_PEER, new StartPeerRequester());
		addRequester(PeerRequestConstants.STOP_PEER, new StopPeerRequester());
		addRequester(PeerRequestConstants.NOTIFY_WORKER_MANAGEMENT_FAILURE, new NotifyWorkerManagementFailureRequester());
		addRequester(PeerRequestConstants.NOTIFY_WORKER_MANAGEMENT_RECOVERY, new NofityWorkerManagementRecoveryRequester());
		addRequester(PeerRequestConstants.STATUS_CHANGED, new StatusChangedRequester());
		addRequester(PeerRequestConstants.GET_USER_STATUS, new GetUserStatusRequester());
		addRequester(PeerRequestConstants.NOTIFY_LWPC_FAILURE, new NotifyLocalWorkerProviderClientFailureRequester());
		addRequester(PeerRequestConstants.GET_TRUST_STATUS, new GetTrustStatusRequester());
		addRequester(PeerRequestConstants.NOTIFY_DS_RECOVERY, new NotifyDiscoveryServiceRecoveryRequester());
		addRequester(PeerRequestConstants.NOTIFY_DS_FAILURE, new NotifyDiscoveryServiceFailureRequester());
		addRequester(PeerRequestConstants.REMOTE_WORKER_PROVIDER_REQUEST_WORKERS, new RemoteWorkerProviderRequestWorkersRequester());
		addRequester(PeerRequestConstants.GET_LOCAL_WORKERS_STATUS, new GetLocalWorkersStatusRequester());
		addRequester(PeerRequestConstants.STATUS_CHANGED_ALLOCATED_FOR_PEER, new StatusChangedAllocatedForPeerRequester());
		addRequester(PeerRequestConstants.NOTIFY_RWP_RECOVERY, new NotifyRemoteWorkerProviderRecoveryRequester());
		addRequester(PeerRequestConstants.NOTIFY_RWP_FAILURE, new NotifyRemoteWorkerProviderFailureRequester());
		addRequester(PeerRequestConstants.HERE_IS_WORKER, new HereIsWorkerRequester());
		addRequester(PeerRequestConstants.HERE_IS_REMOTE_WORKER_PROVIDERS_LIST, new HereIsRemoteWorkerProvidersListRequester());
		addRequester(PeerRequestConstants.REQUEST_WORKERS, new RequestWorkersRequester());
		addRequester(PeerRequestConstants.REMOTE_STATUS_CHANGED_ALLOCATED_FOR_BROKER, new RemoteStatusChangedAllocatedForBrokerRequester());
		addRequester(PeerRequestConstants.STATUS_CHANGED_ALLOCATED_FOR_BROKER, new StatusChangedAllocatedForBrokerRequester());
		addRequester(PeerRequestConstants.FINISH_REQUEST, new FinishRequestRequester());
		addRequester(PeerRequestConstants.UNWANTED_WORKER, new UnwantedWorkerRequester());
		addRequester(PeerRequestConstants.PAUSE_REQUEST, new PauseRequestRequester());
		addRequester(PeerRequestConstants.DISPOSE_WORKER, new DisposeWorkerRequester());
		addRequester(PeerRequestConstants.GET_REMOTE_WORKERS_STATUS, new GetRemoteWorkersStatusRequester());
		addRequester(PeerRequestConstants.REMOTE_DISPOSE_WORKER, new RemoteDisposeWorkerRequester());
		addRequester(PeerRequestConstants.REMOVE_USER, new RemoveUserRequester());
		addRequester(PeerRequestConstants.REPORT_REPLICA_ACCOUNTING, new ReportReplicaAccountingRequester());
		addRequester(PeerRequestConstants.REPORT_WORK_ACCOUNTING, new ReportWorkAccountingRequester());
		addRequester(PeerRequestConstants.UPDATE_REQUEST, new UpdateRequestRequester());
		addRequester(PeerRequestConstants.RESUME_REQUEST, new ResumeRequestRequester());
		addRequester(PeerRequestConstants.GET_REMOTE_CONSUMERS_STATUS, new GetRemoteConsumersStatusRequester());
		addRequester(PeerRequestConstants.GET_COMPLETE_STATUS, new GetCompleteStatusRequester());
		addRequester(PeerRequestConstants.GET_NETWORK_OF_FAVORS_STATUS, new GetNetworkOfFavorsStatusRequester());
		addRequester(PeerRequestConstants.NOTIFY_RWM_FAILURE, new NotifyRemoteWorkerManagementFailureRequester());
		addRequester(PeerRequestConstants.NOTIFY_RWM_RECOVERY, new NotifyRemoteWorkerManagementRecoveryRequester());
		addRequester(PeerRequestConstants.UPDATE_WORKER_SPEC, new UpdateWorkerSpecRequester());
		addRequester(PeerRequestConstants.GET_COMPLETE_HISTORY_STATUS, new GetCompleteHistoryStatusRequester());
		addRequester(PeerRequestConstants.GET_LOCAL_CONSUMERS_STATUS, new GetLocalConsumersStatusRequester());
		addRequester(PeerRequestConstants.REMOVE_WORKER, new RemoveWorkerRequester());
		addRequester(PeerRequestConstants.UPDATE_PEER_UP_TIME, new UpdatePeerUpTimeRequester());
		addRequester(PeerRequestConstants.HERE_ARE_DISCOVERY_SERVICES, new HereAreDiscoveryServicesRequester());
		addRequester(PeerRequestConstants.HERE_IS_JOB_STATS, new HereIsJobStatsRequester());
		addRequester(PeerRequestConstants.WORKER_LOGIN, new WorkerLoginRequester());
		addRequester(PeerRequestConstants.PREEMPTED_WORKER, new PreemptedWorkerRequester());
		addRequester(PeerRequestConstants.DS_IS_OVERLOADED, new DSIsOverloadedRequester());
	}

	@Override
	protected ResponseControlIF createResponseControl() {
		return PeerResponseControl.getInstance();
	}
}
