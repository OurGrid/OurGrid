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
package org.ourgrid.peer.ui.async.client;

import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.PeerManager;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.status.LocalConsumerInfo;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;
import org.ourgrid.peer.status.PeerCompleteStatus;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.async.AsyncManagerClient;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class PeerAsyncManagerClient extends AsyncManagerClient<PeerManager> implements PeerStatusProviderClient, PeerControlClient {

	public void hereIsConfiguration(Map<String, String> configuration) {
		
	}

	public void hereIsUpTime(long uptime) {
		// TODO Auto-generated method stub

	}

	public void hereIsCompleteStatus(ServiceID statusProviderServiceID,
			PeerCompleteStatus completeStatus) {
		PeerAsyncApplicationClient applicationClient = getPeerApplicationClient();
		applicationClient.getModel().updateCompleteStatus(completeStatus);
		
	}

	public void hereIsLocalConsumersStatus(ServiceID statusProviderServiceID,
			List<LocalConsumerInfo> localConsumers) {
		// TODO Auto-generated method stub
		
	}

	public void hereIsLocalWorkersStatus(ServiceID statusProviderServiceID,
			List<WorkerInfo> localWorkers) {
		PeerAsyncApplicationClient applicationClient = getPeerApplicationClient();
		applicationClient.getModel().updateWorkersStatus(localWorkers);
	}

	public void hereIsNetworkOfFavorsStatus(ServiceID statusProviderServiceID,
			NetworkOfFavorsStatus nofStatus) {
		// TODO Auto-generated method stub
		
	}

	public void hereIsRemoteConsumersStatus(ServiceID statusProviderServiceID,
			List<ConsumerInfo> remoteConsumers) {
		// TODO Auto-generated method stub
		
	}

	public void hereIsRemoteWorkersStatus(ServiceID statusProviderServiceID,
			List<RemoteWorkerInfo> remoteWorkers) {
		// TODO Auto-generated method stub
		
	}

	public void hereIsTrustStatus(ServiceID statusProviderServiceID,
			List<TrustyCommunity> trustInfo) {
		// TODO Auto-generated method stub
		
	}

	public void hereIsUsersStatus(ServiceID statusProviderServiceID,
			List<UserInfo> usersInfo) {
		PeerAsyncApplicationClient applicationClient = getPeerApplicationClient();
		applicationClient.getModel().updateUsersStatus(usersInfo);
		
	}
	
	@RecoveryNotification
	public void controlIsUp(PeerManager control) {
		super.controlIsUp(control);
		
		PeerAsyncApplicationClient applicationClient = getPeerApplicationClient();
		
		if (applicationClient.getModel().isPeerToStartOnRecovery()) {
			applicationClient.start();
		} else {
			applicationClient.peerStarted();
		}
	}
	
	public void operationSucceed(ControlOperationResult controlOperationResult) {
		PeerAsyncApplicationClient applicationClient = getPeerApplicationClient();
		
		if (controlOperationResult.getErrorCause() != null) {
			JOptionPane.showMessageDialog(null, controlOperationResult.getErrorCause().getMessage(), 
					"Error on control operation", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (applicationClient.getModel().isPeerToStartOnRecovery()) {
			applicationClient.peerStarted();
			applicationClient.setPeerStartOnRecovery(false);
		}

		applicationClient.getCompleteStatus();
	}
	
	/**
	 * @return
	 */
	private PeerAsyncApplicationClient getPeerApplicationClient() {
		return (PeerAsyncApplicationClient) getServiceManager().getApplication();
	}
	

	@Override
	@FailureNotification
	public void controlIsDown(PeerManager control) {
		super.controlIsDown(control);
		PeerAsyncApplicationClient componentClient = getPeerApplicationClient();
		componentClient.peerStopped();
	}

	public void hereIsCompleteHistoryStatus(ServiceID statusProviderServiceID,
			PeerCompleteHistoryStatus completeStatus, long time) {
		// TODO Auto-generated method stub
		
	}
}
