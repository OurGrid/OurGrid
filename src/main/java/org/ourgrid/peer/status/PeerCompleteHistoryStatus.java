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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.interfaces.to.Accounting;
import org.ourgrid.common.statistics.beans.aggregator.AG_Peer;
import org.ourgrid.common.statistics.beans.aggregator.monitor.AG_WorkerStatusChange;
import org.ourgrid.common.status.CompleteStatus;
import org.ourgrid.common.util.CommonUtils;


public class PeerCompleteHistoryStatus extends CompleteStatus {

	private static final long serialVersionUID = -8344323339484024271L;

	private List<AG_Peer> peerInfo;
	
	private List<AG_WorkerStatusChange> wscInfo;
	
	private Map<String, Accounting> workAccountings;

	//	private List<AG_WorkerStatusChange> remoteWorkerStatusChangeInfo;
	
	///???
	
	//private final List<ConsumerStatusEntry> localConsumersInfo;

	//private final List<ConsumerInfo> remoteConsumersInfo;

	//private final NetworkOfFavorsStatus networkOfFavorsStatus;

	/**
	 * @param upTime
	 * @param configuration
	 * @param peerInfo
	 * @param localWorkersInfo
	 * @param remoteWorkersInfo
	 * @param attributeInfo
	 * @param wscInfo
	 * @param usersInfo
	 * @param loginInfo
	 * @param jobInfo
	 * @param taskInfo
	 * @param executionInfo
	 * @param commandInfo
	 * @param fileTransferInfo
	 */
	public PeerCompleteHistoryStatus(long upTime, String configuration) {
		super(upTime, configuration);
		this.peerInfo = new ArrayList<AG_Peer>();
		this.wscInfo = new ArrayList<AG_WorkerStatusChange>();
		this.workAccountings = CommonUtils.createSerializableMap();
//		this.remoteWorkerStatusChangeInfo = new ArrayList<AG_WorkerStatusChange>();
	}

	public List<AG_Peer> getPeerInfo() {
		return peerInfo;
	}


	public List<AG_WorkerStatusChange> getWorkerStatusChangeInfo() {
		return wscInfo;
	}

//	public List<AG_WorkerStatusChange> getRemoteWorkerStatusChangeInfo() {
//		return remoteWorkerStatusChangeInfo;
//	}

	public void setPeerInfo(List<AG_Peer> peerInfo) {
		this.peerInfo = peerInfo;
	}

	public void setWorkerStatusChangeInfo(List<AG_WorkerStatusChange> wscInfo) {
		this.wscInfo = wscInfo;
	}

//	public void setRemoteWorkerStatusChangeInfo(
//			List<AG_WorkerStatusChange> remoteWorkerStatusChangeInfo) {
//		this.remoteWorkerStatusChangeInfo = remoteWorkerStatusChangeInfo;
//	}
	
	public Map<String, Accounting> getWorkAccountings() {
		return workAccountings;
	}

	public void setWorkAccountings(Map<String, Accounting> workAccountings) {
		this.workAccountings = workAccountings;
	}
	
	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( !(obj instanceof PeerCompleteHistoryStatus) )
			return false;
		final PeerCompleteHistoryStatus other = (PeerCompleteHistoryStatus) obj;

		if ( !super.equals( obj ) )
			return false;
		if ( !this.peerInfo.equals( other.peerInfo ) )
			return false;
		if ( !this.wscInfo.equals( other.wscInfo ) )
			return false;
//		if ( !this.remoteWorkerStatusChangeInfo.equals( other.remoteWorkerStatusChangeInfo ) )
//			return false;
		return true;
	}

}

