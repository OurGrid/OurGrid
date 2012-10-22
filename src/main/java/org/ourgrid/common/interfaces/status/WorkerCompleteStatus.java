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
package org.ourgrid.common.interfaces.status;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.status.CompleteStatus;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.status.PeerStatusInfo;

public class WorkerCompleteStatus extends CompleteStatus {

	private static final long serialVersionUID = 1L;

	private WorkerStatus status;
	
	private PeerStatusInfo peerInfo;
	
	private final String currentPlaypenDirPath;

	private final String currentStorageDirPath;


	/**
	 * @param upTime
	 * @param configuration
	 * @param masterPeer
	 * @param status
	 * @param currentPlaypenDirPath
	 */
	@Req("REQ095")
	public WorkerCompleteStatus( long upTime, String configuration, WorkerStatus status, 
			PeerStatusInfo peerInfo, String currentPlaypenDirPath) {
		this(upTime, configuration, status, peerInfo, currentPlaypenDirPath, null);
	}
	
	public WorkerCompleteStatus( long upTime, String configuration, WorkerStatus status, 
			PeerStatusInfo peerInfo, String currentPlaypenDirPath, String currentStorageDirPath) {

		super( upTime, configuration );
		this.status = status;
		this.setPeerInfo(peerInfo);
		this.currentPlaypenDirPath = currentPlaypenDirPath;
		this.currentStorageDirPath = currentStorageDirPath;
	}

	public WorkerStatus getStatus() {

		return status;
	}
	
	public String getCurrentPlaypenDirPath() {

		return this.currentPlaypenDirPath;
	}
	
	public String getCurrentStorageDirPath() {

		return this.currentStorageDirPath;
	}


	/* (non-Javadoc)
	 * @see org.ourgrid.common.status.CompleteStatus#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( Object obj ) {

		if ( (obj == null) || (obj.getClass() != this.getClass()) )
			return false;
		WorkerCompleteStatus other = (WorkerCompleteStatus) obj;
		return other.getPeerInfo().getPeerUserAtServer().equals( this.getPeerInfo().getPeerUserAtServer()) 
				&& other.status.equals( this.status );
	}

	public PeerStatusInfo getPeerInfo() {
		return peerInfo;
	}

	public void setPeerInfo(PeerStatusInfo peerInfo) {
		this.peerInfo = peerInfo;
	}
}
