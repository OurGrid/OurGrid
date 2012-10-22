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
package org.ourgrid.aggregator.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.aggregator.business.dao.AggregatorDAOFactory;
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.aggregator.request.CommunityStatusProviderIsDownRequestTO;
import org.ourgrid.aggregator.response.CancelRequestFutureResponseTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;

/**
 * This class provide a list of {@link IResponseTO} that will be executed.
 * Responsible for the execute a notification that changes the states of the Community Status
 * Provider to Down.
 */
public class CommunityStatusProviderIsDownRequester implements RequesterIF<CommunityStatusProviderIsDownRequestTO> {
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public List<IResponseTO> execute(CommunityStatusProviderIsDownRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		AggregatorDAOFactory daoFactory = AggregatorDAOFactory.getInstance();
		String communityStatusProviderAddress = daoFactory.getAggregatorDAO().getCommunityStatusProviderAddress();
		
		if(verifyThrowsWarnning(request, responses, communityStatusProviderAddress)){
			return responses;
		}
		
		
		daoFactory.getAggregatorDAO().setCommunityStatusProviderAddress(null);
			
		responses.add(new CancelRequestFutureResponseTO());
		
		responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getCommunityStatusProviderIsDownInfoMessage(),LoggerResponseTO.INFO));
		
		return responses;
	}

	/**
	 * Verify that meets the condition will throws warnning.
	 * Case true adds the warn to responses.
	 * @param request {@link CommunityStatusProviderIsDownRequestTO}
	 * @param responses {@link List} {@link IResponseTO}
	 * @param communityStatusProviderAddress {@link String}
	 * @return boolean
	 */
	private boolean verifyThrowsWarnning(
			CommunityStatusProviderIsDownRequestTO request,
			List<IResponseTO> responses, String communityStatusProviderAddress) {
		
		if( communityStatusProviderAddress == null){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getCommunityStatusProviderIsDownWarningMessage(),LoggerResponseTO.WARN));
			return true;
		}
		
		if( request.getProviderAddress() == null){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getWrongCommunityStatusProviderAddressWarningMessage(),LoggerResponseTO.WARN));
			return true;
		}
		
		if(!request.getProviderAddress().equals(communityStatusProviderAddress)){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getWrongCommunityStatusProviderAddressWarningMessage(),LoggerResponseTO.WARN));
			return true;
		}
		
		return false;
	}

}
