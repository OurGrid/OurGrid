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
import org.ourgrid.aggregator.request.PeerStatusProviderIsDownRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;

/**
 * This class provide a list of {@link IResponseTO} that will be executed.
 * Responsible for the execute a notification that changes the states of the Peer Status
 * Provider to Down.
 */
public class PeerStatusProviderIsDownRequester implements RequesterIF<PeerStatusProviderIsDownRequestTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IResponseTO> execute(PeerStatusProviderIsDownRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		AggregatorDAO dao = AggregatorDAOFactory.getInstance().getAggregatorDAO();
		
		if( verifyThrowsWarnning(responses, dao, request.getProviderId())){
			return responses;
		}
		
//		dao.setProviderStatus(request.getProviderId(), false);
		AggregatorPeerStatusProvider aggProvider = dao.getProvider(request
				.getProviderId());
		if (aggProvider != null) {
			ReleaseResponseTO response = new ReleaseResponseTO();
			response.setStubAddress(aggProvider.getProviderAddress());
			responses.add(response);
		}
		
		responses.add(new LoggerResponseTO(AggregatorControlMessages
				.getPeerStatusProviderIsDownInfoMessage(request.getProviderId()),
				LoggerResponseTO.INFO));

		return responses;
	}

	/**
	 * Verify that meets the condition will throws warnning.
	 * Case true adds the warn to responses.
	 * @param responses {@link List} {@link IResponseTO}
	 * @param dao {@link AggregatorDAO}
	 * @param aggPeerStatusProvider {@link AggregatorPeerStatusProvider}
	 * @return boolean
	 */
	private boolean verifyThrowsWarnning(List<IResponseTO> responses,
			AggregatorDAO dao,
			String providerAddress) {
		if(dao.getCommunityStatusProviderAddress() == null && dao.aggregatorCurrentPeerStatusProvidersIsEmpty()){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getCommunityStatusProviderIsDownWarningMessage(),LoggerResponseTO.WARN));
			return true;
		}
		
		if(!dao.containsCurrentProviderAddress(providerAddress)){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getAggregatorPeerStatusProviderIsNullMessage(),LoggerResponseTO.WARN));
			return true;
		}
		
		if(dao.getProvider(providerAddress) == null){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getPeerStatusProviderIsDownMessage(),LoggerResponseTO.WARN));
			return true;
		}
		
		return false;
	}
}


