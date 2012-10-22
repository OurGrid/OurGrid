/*
 * Copyright (C) 2011 Universidade Federal de Campina Grande
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
package org.ourgrid.aggregator.response;

import org.ourgrid.aggregator.business.requester.GetPeerStatusProviderRepeatedActionRequester;
import org.ourgrid.aggregator.communication.sender.AggregatorResponseConstants;
import org.ourgrid.aggregator.communication.sender.GetPeerStatusProviderRepeatedActionSender;
import org.ourgrid.common.internal.IResponseTO;

/**
 *  This class is a Transfer Object from one GetPeerStatusProviderRepeatedActionRequester
 *  {@link GetPeerStatusProviderRepeatedActionRequester}
 * 	to GetPeerStatusProviderRepeatedActionSender {@link GetPeerStatusProviderRepeatedActionSender}.
 */
public class GetPeerStatusProviderRepeatedActionResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = AggregatorResponseConstants.
											GET_PEER_STATUS_PROVIDER_REPEATED_ACTION;

	private String providerAddress;
	private long peerStatusChangeLastUpdate;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setProviderAddress(String providerAddress) {
		this.providerAddress = providerAddress;
	}

	public String getProviderAddress() {
		return providerAddress;
	}

	public void setPeerStatusChangeLastUpdate(long peerStatusChangeLastUpdate) {
		this.peerStatusChangeLastUpdate = peerStatusChangeLastUpdate;
	}

	public long getPeerStatusChangeLastUpdate() {
		return peerStatusChangeLastUpdate;
	}
}

