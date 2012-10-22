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
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.aggregator.request.HereIsPeerStatusChangeHistoryRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;

/**
 * This class provide a list of {@link IResponseTO} that will be executed.
 * Responsible for the execute a action that save the changes history that
 * was given by the Community Status Provider.
 */
public class HereIsPeerStatusChangeHistoryRequester implements
							RequesterIF<HereIsPeerStatusChangeHistoryRequestTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IResponseTO> execute(
			HereIsPeerStatusChangeHistoryRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		AggregatorDAO dao = AggregatorDAOFactory.getInstance().getAggregatorDAO();
		if(dao.getCommunityStatusProviderAddress() == null){
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getCommunityStatusProviderIsDownWarningMessage(),
									LoggerResponseTO.WARN));
			return responses;
		}
		
		dao.savePeersStatusChanges(request.getStatusChanges(), responses);
		dao.setPeerStatusChangeLastUpdate(request.getSince());
		responses.add(new LoggerResponseTO(AggregatorControlMessages
				.getHereIsPeerStatusChangeHistoryInfoMessage(),
				LoggerResponseTO.INFO));
		return responses;
	}

}
