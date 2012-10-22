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
package org.ourgrid.common.interfaces;

import java.util.List;

import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;

import br.edu.ufcg.lsd.commune.api.Remote;

/**
 * Entity that represents a CommunityStatusProvider client. From this interface
 * the CommunityStatusProvider receive the requests about status.
 * 
 * @see CommunityStatusProvider
 */
@Remote
public interface CommunityStatusProviderClient {
	
	/**
	 * Informs the IDs of the StatusProviders.
	 * @param statusProviders List of IDs.
	 */
	void hereIsStatusProviderList(List<String> statusProviders);
	
	/**
	 * Informs the status changes of the Peer according to the parameter time. 
	 * @param statusChanges List of Status Changes
	 * @param since Time
	 */
	void hereIsPeerStatusChangeHistory(List<DS_PeerStatusChange> statusChanges, long since);
}
