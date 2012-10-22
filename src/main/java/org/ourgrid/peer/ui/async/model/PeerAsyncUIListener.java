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
package org.ourgrid.peer.ui.async.model;

import java.util.List;

import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.peer.status.PeerCompleteStatus;

/**
 * A tagging interface that receives peer events.
 */
public interface PeerAsyncUIListener {

	/**
	 * It permites update the workers status.
	 * @param localWorkers The informations about the workers status.
	 */
	public void updateWorkersStatus(List<WorkerInfo> localWorkers);

	/**
	 * It permites update the users status.
	 * @param usersInfo The informations about the users status.
	 */
	public void updateUsersStatus(List<UserInfo> usersInfo);

	/**
	 * It permites update the peer complete status.
	 * @param completeStatus The informations about the peer complete status.
	 */
	public void updateCompleteStatus(PeerCompleteStatus completeStatus);

	/**
	 * It permites start the peer.
	 */
	public void peerStarted();
	
	/**
	 * It permites stop the peer.
	 */
	public void peerStopped();
	
	/**
	 * It permites init the peer.
	 */
	public void peerInited();

	/**
	 * It permites reinit the peer.
	 */
	public void peerInitedFailed();

	/**
	 * It permites restart the peer.
	 */
	public void peerRestarted();

	/**
	 * It permites edit the peer.
	 */
	public void peerEditing();

}
