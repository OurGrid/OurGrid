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

import org.ourgrid.aggregator.business.requester.AggregatorRequestConstants;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;

/**
 * This class is a Transfer Object from one 
 * PeerStatusProviderClient {@link PeerStatusProviderClient}
 * to HereIsCompleteHistoryStatusRequester {@link HereIsCompleteHistoryStatusRequester}.
 */
public class HereIsCompleteHistoryStatusRequestTO implements IRequestTO {

	private String REQUEST_TYPE = AggregatorRequestConstants.
								HERE_IS_COMPLETE_HISTORY_STATUS.getString();
	
	private String providerAddress;
	private PeerCompleteHistoryStatus completeStatus;
	private long time;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public String getProviderAddress() {
		return providerAddress;
	}

	public void setProviderAddress(String providerAddress) {
		this.providerAddress = providerAddress;
	}

	public PeerCompleteHistoryStatus getCompleteStatus() {
		return completeStatus;
	}

	public void setCompleteStatus(PeerCompleteHistoryStatus completeStatus) {
		this.completeStatus = completeStatus;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
