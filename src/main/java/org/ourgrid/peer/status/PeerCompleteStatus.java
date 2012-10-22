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
package org.ourgrid.peer.status;

import java.util.Collection;
import java.util.List;

import org.ourgrid.common.interfaces.status.CommunityInfo;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.status.LocalConsumerInfo;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.status.CompleteStatus;

public class PeerCompleteStatus extends CompleteStatus {

	private static final long serialVersionUID = 40L;

	private final List<WorkerInfo> localWorkersInfo;

	private final List<RemoteWorkerInfo> remoteWorkersInfo;
	
	private final List<LocalConsumerInfo> localConsumersInfo;

	private final List<ConsumerInfo> remoteConsumersInfo;
	
	private final List<UserInfo> usersInfo;

	private final NetworkOfFavorsStatus networkOfFavorsStatus;

	private final CommunityInfo cmmInfo;

	private final String label;
	
	private final String version;
	
	/**
	 * @param localWorkersInfo
	 * @param remoteWorkersInfo
	 * @param localConsumersInfo
	 * @param remoteConsumersInfo
	 * @param usersInfo 
	 * @param networkOfFavorsStatus
	 * @param up
	 * @param configuration
	 */
	public PeerCompleteStatus( List<WorkerInfo> localWorkersInfo,
			List<RemoteWorkerInfo> remoteWorkersInfo,
			List<LocalConsumerInfo> localConsumersInfo,
			List<ConsumerInfo> remoteConsumersInfo,
			List<UserInfo> usersInfo,
			NetworkOfFavorsStatus networkOfFavorsStatus,
			CommunityInfo communityInfo, long up, String label, String version, String configuration ) {

		super( up, configuration );
		this.localWorkersInfo = localWorkersInfo;
		this.remoteWorkersInfo = remoteWorkersInfo;
		this.localConsumersInfo = localConsumersInfo;
		this.remoteConsumersInfo = remoteConsumersInfo;
		this.usersInfo = usersInfo;
		this.networkOfFavorsStatus = networkOfFavorsStatus;
		this.cmmInfo = communityInfo;
		this.label = label;
		this.version = version;
	}


	public Collection<LocalConsumerInfo> getLocalConsumersInfo() {

		return this.localConsumersInfo;
	}


	public List<WorkerInfo> getLocalWorkersInfo() {

		return this.localWorkersInfo;
	}


	public NetworkOfFavorsStatus getNetworkOfFavorsStatus() {

		return this.networkOfFavorsStatus;
	}


	public Collection<ConsumerInfo> getRemoteConsumersInfo() {

		return this.remoteConsumersInfo;
	}


	public Collection<RemoteWorkerInfo> getRemoteWorkersInfo() {

		return this.remoteWorkersInfo;
	}

	public Collection<UserInfo> getUsersInfo() {
		return usersInfo;
	}

	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( !(obj instanceof PeerCompleteStatus) )
			return false;
		final PeerCompleteStatus other = (PeerCompleteStatus) obj;

		if ( !super.equals( obj ) )
			return false;
		if ( !this.localConsumersInfo.equals( other.localConsumersInfo ) )
			return false;
		if ( !this.localWorkersInfo.equals( other.localWorkersInfo ) )
			return false;
		if ( !this.networkOfFavorsStatus.equals( other.networkOfFavorsStatus ) )
			return false;
		if ( !this.remoteConsumersInfo.equals( other.remoteConsumersInfo ) )
			return false;
		if ( !this.remoteWorkersInfo.equals( other.remoteWorkersInfo ) )
			return false;
		return true;
	}


	public CommunityInfo getCommunityInfo() {
		return cmmInfo;
	}


	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}


	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

}
