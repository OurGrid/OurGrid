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

import org.ourgrid.aggregator.business.dao.AggregatorDAO;
import org.ourgrid.aggregator.business.dao.AggregatorDAOFactory;
import org.ourgrid.aggregator.business.dao.AggregatorPeerStatusProvider;
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.aggregator.request.HereIsCompleteHistoryStatusRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;

/**
 * This class provide a list of {@link IResponseTO} that will be executed.
 * Responsible for the execute a action that save the changes history that
 * was given by the Peer Status Provider.
 *
 */
public class HereIsCompleteHistoryStatusRequester implements RequesterIF<HereIsCompleteHistoryStatusRequestTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IResponseTO> execute(HereIsCompleteHistoryStatusRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		AggregatorDAO dao = AggregatorDAOFactory.getInstance().getAggregatorDAO();
		
		if(verifyThrowsWarnning(request, responses, dao)){
			return responses;
		}
			
		AggregatorPeerStatusProvider provider = dao.getProvider(request
				.getProviderAddress());

		if (provider != null) {
			provider.setMustUpdate(true);
		}

		dao.addCompleteHistoryStatus(request.getCompleteStatus(),
				request.getProviderAddress(), responses);

		if (provider != null) {
			provider.setLastUpdateTime(request.getTime());
		}

		responses.add(new LoggerResponseTO(AggregatorControlMessages
				.getHereIsCompleteHistoryStatusInfoMessage(),
				LoggerResponseTO.INFO));

		return responses;
	}

	/**
	 * Verify that meets the condition will throws warnning.
	 * Case true adds the warn to responses.
	 * @param request {@link HereIsCompleteHistoryStatusRequestTO}
	 * @param responses {@link List} {@link IResponseTO}
	 * @param dao {@link AggregatorDAO}
	 * @return boolean
	 */
	private boolean verifyThrowsWarnning(
			HereIsCompleteHistoryStatusRequestTO request,
			List<IResponseTO> responses, AggregatorDAO dao) {
		
		if(dao.getCommunityStatusProviderAddress() == null && dao.aggregatorCurrentPeerStatusProvidersIsEmpty()){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getCommunityStatusProviderIsDownWarningMessage(),LoggerResponseTO.WARN));
			return true;
		}
		
		if(dao.aggregatorCurrentPeerStatusProvidersIsEmpty()){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getProviderAddressListIsEmptyMessage(),LoggerResponseTO.WARN));
			return true;
		}
		
		if(!dao.containsCurrentProviderAddress(request.getProviderAddress())){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getAggregatorPeerStatusProviderIsNullMessage(),LoggerResponseTO.WARN));
			return true;
		}
		
		if(dao.getProvider(request.getProviderAddress()) == null){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getPeerStatusProviderIsDownMessage(),LoggerResponseTO.WARN));
			return true;
		}
		
		return false;
	}

}
