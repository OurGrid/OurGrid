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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.aggregator.business.dao.AggregatorDAO;
import org.ourgrid.aggregator.business.dao.AggregatorDAOFactory;
import org.ourgrid.aggregator.business.dao.AggregatorPeerStatusProvider;
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.aggregator.request.HereIsStatusProviderListRequestTO;
import org.ourgrid.aggregator.response.GetCompleteStatusResponseTO;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;

/**
 * This class provide a list of {@link IResponseTO} that will be executed.
 * Responsible for the execute a action that save the list that
 * was given by the Community Status Provider and remove those who aren't
 * in the list's.
 */
public class HereIsStatusProviderListRequester implements RequesterIF<HereIsStatusProviderListRequestTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IResponseTO> execute(HereIsStatusProviderListRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		AggregatorDAO dao = AggregatorDAOFactory.getInstance().getAggregatorDAO();
		if(dao.getCommunityStatusProviderAddress() == null){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getCommunityStatusProviderIsDownWarningMessage(),LoggerResponseTO.WARN));
			return responses;
		}
		
		Set<String> oldProviders = new LinkedHashSet<String>(
				dao.getProvidersAddresses());
		
		for (String providerAddress : request.getStatusProviders()) {
			
			boolean isCurrent = oldProviders.remove(providerAddress);
			
			if (isCurrent) {
				requestForHistoryStatus(responses, providerAddress, 
						dao.getProvider(providerAddress));
			} else {
				
				RegisterInterestResponseTO to = new RegisterInterestResponseTO();
				
				to.setMonitorName(AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME);
				to.setMonitorableAddress(providerAddress);
				to.setMonitorableType(PeerStatusProvider.class);
				responses.add(to);
			}
			
		}
		
		for (String oldProviderAddress : oldProviders) {
			removeProvider(responses, oldProviderAddress);
		}
		
		dao.setCurrentProvidersAddress(request.getStatusProviders());
		
		responses.add(new LoggerResponseTO(AggregatorControlMessages
				.getHereIsStatusProviderListInfoMessage(),
				LoggerResponseTO.INFO));

		return responses;
	}
		
	private void removeProvider(List<IResponseTO> responses, String providerAddress) {
		
		AggregatorPeerStatusProvider aggProvider = AggregatorDAOFactory.getInstance().
				getAggregatorDAO().removeProvider(providerAddress);
		
		if (aggProvider != null) {
			ReleaseResponseTO to = new ReleaseResponseTO();
			to.setStubAddress(providerAddress);
			
			responses.add(to);
		}
	}
	
	private void requestForHistoryStatus(List<IResponseTO> responses, String providerAddress,
			AggregatorPeerStatusProvider aggProvider) {
		
		if (aggProvider.mustUpdate()) {
			GetCompleteStatusResponseTO to = new GetCompleteStatusResponseTO();
			to.setProviderAddress(providerAddress);
			to.setLastUpdateTime(aggProvider.getLastUpdateTime());
			
			responses.add(to);
			
			aggProvider.setMustUpdate(false);
		
		}
	}

}
