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
import org.ourgrid.common.internal.IRequestTO;

/**
 * This class is a Transfer Object from one CommunityStatusProviderClient {@link CommunityStatusProviderClient}
 * to CommunityStatusProviderIsUpRequester {@link CommunityStatusProviderIsUpRequester}.
 */
public class CommunityStatusProviderIsUpRequestTO implements IRequestTO {

	private String REQUEST_TYPE = AggregatorRequestConstants.
							COMMUNITY_STATUS_PROVIDER_IS_UP.getString();
	
	private String commStatusProviderAddress;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setCommStatusProviderAddress(String commStatusProviderAddress) {
		this.commStatusProviderAddress = commStatusProviderAddress;
	}

	public String getCommStatusProviderAddress() {
		return commStatusProviderAddress;
	}

}