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
import java.util.concurrent.TimeUnit;

import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.aggregator.business.dao.AggregatorDAOFactory;
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.aggregator.request.CommunityStatusProviderIsUpRequestTO;
import org.ourgrid.aggregator.response.SetAdvertActionFutureTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;

/**
 * This class provide a list of {@link IResponseTO} that will be executed.
 * Responsible for the execute a notification that changes the states of the Community Status
 * Provider to Up.
 */
public class CommunityStatusProviderIsUpRequester implements RequesterIF<CommunityStatusProviderIsUpRequestTO> {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IResponseTO> execute(CommunityStatusProviderIsUpRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		AggregatorDAOFactory daoFactory = AggregatorDAOFactory.getInstance();
		
		if(!(daoFactory.getAggregatorDAO().getCommunityStatusProviderAddress() == null)){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getCommunityStatusProviderIsUpWarningMessage(),LoggerResponseTO.WARN));
			return responses;
		}
		
		daoFactory.getAggregatorDAO().setCommunityStatusProviderAddress(request.getCommStatusProviderAddress());
			
		createFutureResponse(responses);
		responses.add(
				new LoggerResponseTO(AggregatorControlMessages.
						getCommunityStatusProviderIsUpInfoMessage(),LoggerResponseTO.INFO));
				
		return responses;
	}
	
	private void createFutureResponse(List<IResponseTO> responses){
		SetAdvertActionFutureTO response = new SetAdvertActionFutureTO();
		response.setActionName(AggregatorConstants.GET_PEER_STATUS_PROVIDER_ACTION_NAME);
		response.setInitialDelay(0);
		response.setDelay(AggregatorConstants.GET_PEER_STATUS_PROVIDER_ACTION_DELAY);
		response.setTimeUnit(TimeUnit.SECONDS);
		
		responses.add(response);		
	}
	

}
