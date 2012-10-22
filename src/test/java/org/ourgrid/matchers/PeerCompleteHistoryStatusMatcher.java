package org.ourgrid.matchers;
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
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.ourgrid.common.statistics.beans.aggregator.AG_Attribute;
import org.ourgrid.common.statistics.beans.aggregator.AG_Login;
import org.ourgrid.common.statistics.beans.aggregator.AG_Peer;
import org.ourgrid.common.statistics.beans.aggregator.AG_User;
import org.ourgrid.common.statistics.beans.aggregator.AG_Worker;
import org.ourgrid.common.statistics.beans.aggregator.monitor.AG_WorkerStatusChange;
import org.ourgrid.common.status.CompleteStatus;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;

public class PeerCompleteHistoryStatusMatcher implements IArgumentMatcher {
	private PeerCompleteHistoryStatus completeStatus;

	/**
	 * @param remoteExecuteMessageHandle
	 */
	public PeerCompleteHistoryStatusMatcher(PeerCompleteHistoryStatus completeStatus) {
		this.completeStatus = completeStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.easymock.IArgumentMatcher#appendTo(java.lang.StringBuffer)
	 */
	public void appendTo(StringBuffer arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.easymock.IArgumentMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object arg0) {

		if (!(CompleteStatus.class.isInstance(arg0))) {
			return false;
		}

		if (arg0 == null) {
			return false;
		}

		PeerCompleteHistoryStatus other = (PeerCompleteHistoryStatus) arg0;

		if (!this.completeStatus.getPeerInfo().isEmpty()) {
			List<AG_Peer> peerInfo = this.completeStatus.getPeerInfo();
			List<AG_Peer> otherPeerInfo = other.getPeerInfo();
			
			//Special treatment to peer empty
			if (peerInfo.get(0).getAddress() == null) {
				return true;
			}
			
			if (peerInfo.size() != otherPeerInfo.size())
				return false;

			for (int i = 0; i < peerInfo.size(); i++) {
				AG_Peer peer = peerInfo.get(i);
				AG_Peer otherPeer = otherPeerInfo.get(i);
				
				if (!peer.getAddress().equals(otherPeer.getAddress())) 
					return false;
			}
		}
		
		if (this.completeStatus.getPeerInfo() != null && !getWorkersList(this.completeStatus.getPeerInfo()).isEmpty()) {
			List<AG_Worker> workersInfo = getWorkersList(this.completeStatus.getPeerInfo());
			List<AG_Worker> otherWorkersInfo = getWorkersList(other.getPeerInfo());
			
			if (workersInfo.size() != otherWorkersInfo.size()) 
				return false;

			for (int i = 0; i < workersInfo.size(); i++) {
				AG_Worker workerInfo = workersInfo.get(i);
				AG_Worker otherWorkerInfo = otherWorkersInfo.get(i);
				
				if (!workerInfo.getAddress().equals(otherWorkerInfo.getAddress()))
					return false;

				if (workerInfo.getPeer() != null) {
					if (otherWorkerInfo.getPeer() == null) {
						return false;
					}
					
					if (!workerInfo.getPeer().getAddress().equals(otherWorkerInfo.getPeer().getAddress()))
						return false;
				}

				for (AG_Attribute attribute : workerInfo.getAttributes()) {
					boolean hasProperty = false;

					for (AG_Attribute otherAttribute : otherWorkerInfo.getAttributes()) {
						if (attribute.getProperty().equals(otherAttribute.getProperty())) {
							hasProperty = true;
							if (!attribute.getValue().equals(otherAttribute.getValue())) 
								return false;
							else
								break;
						}
					}

					if (!hasProperty) 
						return false;
				}
			}
		}
		
		if (!this.completeStatus.getWorkerStatusChangeInfo().isEmpty()) {
			List<AG_WorkerStatusChange> workerStatusChangeInfo = this.completeStatus.getWorkerStatusChangeInfo();
			List<AG_WorkerStatusChange> otherWorkerStatusChangeInfo = other.getWorkerStatusChangeInfo();
			
			if (workerStatusChangeInfo.size() != otherWorkerStatusChangeInfo.size()) 
				return false;
			
			for (int i = 0; i < workerStatusChangeInfo.size(); i++) {
				AG_WorkerStatusChange statusChange = workerStatusChangeInfo.get(i);
				AG_WorkerStatusChange otherStatusChange = otherWorkerStatusChangeInfo.get(i);

				if (!statusChange.getStatus().equals(otherStatusChange.getStatus())) 
					return false;
				
				if (!statusChange.getWorker().getAddress().equals(otherStatusChange.getWorker().getAddress()))
					return false;
			}
		}
		
		if (this.completeStatus.getPeerInfo() != null && !getUsersList(this.completeStatus.getPeerInfo()).isEmpty()) {
			List<AG_User> usersInfo = getUsersList(this.completeStatus.getPeerInfo());
			List<AG_User> otherUsersInfo = getUsersList(other.getPeerInfo()); 
			
			if (usersInfo.size() != otherUsersInfo.size()) 
				return false;
			
			for (int i = 0; i < usersInfo.size(); i++) {
				AG_User user = usersInfo.get(i);
				AG_User otherUser = otherUsersInfo.get(i);

				if (!user.getAddress().equals(otherUser.getAddress()))
					return false;

				if (!user.getPeer().getAddress().equals(otherUser.getPeer().getAddress()))
					return false;

				if (!user.getPublicKey().equals(otherUser.getPublicKey()))
					return false;
			}
		}

		if (this.completeStatus.getPeerInfo() != null && !getLoginList(this.completeStatus.getPeerInfo()).isEmpty()) {
			List<AG_Login> loginInfo = getLoginList(this.completeStatus.getPeerInfo());
			List<AG_Login> otherLoginInfo = getLoginList(other.getPeerInfo());
			
			if (loginInfo.size() != otherLoginInfo.size())
				return false;
			
			for (int i = 0; i < loginInfo.size(); i++) {
				AG_Login login = loginInfo.get(i);
				AG_Login otherLogin = otherLoginInfo.get(i);
				
				if (!login.getLoginResult().equals(otherLogin.getLoginResult()))
					return false;

				if (login.getUser().getId() != otherLogin.getUser().getId())
					return false;
			}
		}

		return true;
	}

	private List<AG_Worker> getWorkersList(List<AG_Peer> peerInfo) {
		
		List<AG_Worker> workers = new ArrayList<AG_Worker>();
		
		for (AG_Peer peer : peerInfo) {
			workers.addAll(peer.getWorkers());
		}
		
		return workers;
	}

	private List<AG_Login> getLoginList(List<AG_Peer> peerInfo) {
		
		List<AG_User> users = getUsersList(peerInfo);
		List<AG_Login> logins = new ArrayList<AG_Login>();
		
		for (AG_User user : users) {
			logins.addAll(user.getLogins());
		}
		
		return logins;
	}

	private List<AG_User> getUsersList(List<AG_Peer> peerInfo) {
		List<AG_User> users = new ArrayList<AG_User>();
		
		if (peerInfo != null) {
			for (AG_Peer peer : peerInfo) {
				users.addAll(peer.getUsers());
			}
		}	
		
		return users;
	}

	public static PeerCompleteHistoryStatus eqMatcher(PeerCompleteHistoryStatus completeStatus) {
		EasyMock.reportMatcher(new PeerCompleteHistoryStatusMatcher(completeStatus));
		return null;
	}
}
