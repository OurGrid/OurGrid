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
package org.ourgrid.aggregator.request;

import org.ourgrid.aggregator.business.requester.AggregatorRequestConstants;
import org.ourgrid.common.interfaces.CommunityStatusProvider;
import org.ourgrid.common.internal.IRequestTO;

import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * This class is a Transfer Object from one 
 * GetPeerStatusProviderRepeatedAction {@link GetPeerStatusProviderRepeatedAction}
 * to GetPeerStatusProviderRepeatedActionRequester {@link GetPeerStatusProviderRepeatedActionRequester}
 */
public class GetPeerStatusProviderRepeatedActionRequestTO implements IRequestTO {

	private String REQUEST_TYPE = AggregatorRequestConstants.
											GET_PEER_STATUS_PROVIDER.getString();
	
	private ServiceID providerID;
	private CommunityStatusProvider commStatusProvider;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestType() {
		return this.REQUEST_TYPE;	
	}

	public void setProviderID(ServiceID providerID) {
		this.providerID = providerID;
	}

	public ServiceID getProviderID() {
		return providerID;
	}

	public void setCommStatusProvider(CommunityStatusProvider commStatusProvider) {
		this.commStatusProvider = commStatusProvider;
	}

	public CommunityStatusProvider getCommStatusProvider() {
		return commStatusProvider;
	}	

}
