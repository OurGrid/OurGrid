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

import org.ourgrid.aggregator.business.requester.GetCompleteStatusRequester;
import org.ourgrid.aggregator.communication.sender.AggregatorResponseConstants;
import org.ourgrid.aggregator.communication.sender.HereIsCompleteStatusSender;
import org.ourgrid.aggregator.status.AggregatorCompleteStatus;
import org.ourgrid.common.internal.IResponseTO;

/**	This class is a Transfer Object from one GetCompleteStatusRequester
 *  {@link GetCompleteStatusRequester}
 * 	to HereIsCompleteStatusSender {@link HereIsCompleteStatusSender}.
 */
public class HereIsCompleteStatusResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = AggregatorResponseConstants.HERE_IS_COMPLETE_STATUS;
	private AggregatorCompleteStatus aggregatorCompleteStatus;
	private String clientAddress;
	
	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	@Override
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setAggregatorCompleteStatus(AggregatorCompleteStatus aggregatorCompleteStatus) {
		this.aggregatorCompleteStatus = aggregatorCompleteStatus;
	}

	public AggregatorCompleteStatus getAggregatorCompleteStatus() {
		return aggregatorCompleteStatus;
	}

}