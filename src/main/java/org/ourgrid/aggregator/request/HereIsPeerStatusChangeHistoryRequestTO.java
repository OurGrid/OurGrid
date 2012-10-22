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
package org.ourgrid.aggregator.request;

import java.util.List;

import org.ourgrid.aggregator.business.requester.AggregatorRequestConstants;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;

/**
 * This class is a Transfer Object from one 
 * PeerStatusProviderClient {@link PeerStatusProviderClient}
 * to HereIsPeerStatusChangeHistoryRequester {@link HereIsPeerStatusChangeHistoryRequester}.
 */
public class HereIsPeerStatusChangeHistoryRequestTO implements IRequestTO {

	private String REQUEST_TYPE = AggregatorRequestConstants.
								HERE_IS_PEER_STATUS_CHANGE_HISTORY.getString();
	
	private List<DS_PeerStatusChange> statusChanges;
	
	private long since;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setStatusChanges(List<DS_PeerStatusChange> statusChanges) {
		this.statusChanges = statusChanges;
	}

	public List<DS_PeerStatusChange> getStatusChanges() {
		return statusChanges;
	}

	public void setSince(long since) {
		this.since = since;
	}

	public long getSince() {
		return since;
	}

}
