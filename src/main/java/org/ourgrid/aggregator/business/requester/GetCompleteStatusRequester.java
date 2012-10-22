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

import static org.ourgrid.common.interfaces.Constants.LINE_SEPARATOR;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.aggregator.business.dao.AggregatorDAOFactory;
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.aggregator.request.GetCompleteStatusRequestTO;
import org.ourgrid.aggregator.response.HereIsCompleteStatusResponseTO;
import org.ourgrid.aggregator.status.AggregatorCompleteStatus;
import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.util.StringUtil;

/**
 * This class provide a list of {@link IResponseTO} that will be executed.
 * Responsible for the execute a methods that shows some informations
 * of this Component in default exit.
 *
 */
public class GetCompleteStatusRequester implements RequesterIF<GetCompleteStatusRequestTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IResponseTO> execute(GetCompleteStatusRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (!request.getCanStatusBeUsed()) {
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.getCompleteStatusWarnMessage(request.getClientAddress()),
							LoggerResponseTO.WARN));
			return responses;
		}
		
		String description = getDescription(request); 
		
		String cmmStatusProviderAddress = AggregatorDAOFactory.getInstance().
							getAggregatorDAO().getCommunityStatusProviderAddress();
		
		if (cmmStatusProviderAddress == null) {
			responses.add(
					new LoggerResponseTO(AggregatorControlMessages.
							getCommunityStatusProviderIsWrongOrDownMessage(),
							LoggerResponseTO.WARN));
		}
		
		String dsUserAtSever = getDsUserAtSever(cmmStatusProviderAddress);
		
		AggregatorCompleteStatus completeStatus = new AggregatorCompleteStatus(
				dsUserAtSever, request.getUpTime(), description);
		
		HereIsCompleteStatusResponseTO to = new HereIsCompleteStatusResponseTO();
		to.setClientAddress(request.getClientAddress());
		to.setAggregatorCompleteStatus(completeStatus);
		
		responses.add(to);
		
		responses.add(
				new LoggerResponseTO(AggregatorControlMessages.getCompleteStatusInfoMessage(request.getClientAddress()),
						LoggerResponseTO.INFO));
		
		return responses;
	}
	
	private String getDsUserAtSever(String cmmStatusProviderAddress) {
		String dsUserAtSever = null;
		if (cmmStatusProviderAddress != null) {
			dsUserAtSever = StringUtil.addressToUserAtServer(cmmStatusProviderAddress);
		}
		
		return dsUserAtSever;
	}

	private String getDescription(GetCompleteStatusRequestTO request) {
		
		StringBuilder conf = new StringBuilder();

		conf.append( "\tVersion: " ).append( Configuration.VERSION ).append( LINE_SEPARATOR );

		conf.append( "\tConfiguration directory: " );
		conf.append( request.getPropConfDir());
		conf.append( LINE_SEPARATOR );
		
		conf.append( request.getContainerContext() );

		return conf.toString();
	}

}