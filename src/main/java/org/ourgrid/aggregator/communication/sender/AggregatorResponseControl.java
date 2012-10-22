/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
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
package org.ourgrid.aggregator.communication.sender;

import org.ourgrid.common.internal.OurGridResponseControl;

/**
 * This class  provide an map that will put these responses with a key that is an
 * AggregatorResponseConstants {@link AggregatorResponseConstants} in the map to execute them.
 *
 */
public class AggregatorResponseControl extends OurGridResponseControl {

	
	private static AggregatorResponseControl instance;
	
	
	public static AggregatorResponseControl getInstance() {
		if (instance == null) {
			instance = new AggregatorResponseControl();
		}
		
		return instance;
	}
	
	protected void addEntitySenders() {
		addSender(AggregatorResponseConstants.OPERATION_SUCCEDED, new OperationSucceedSender());
		addSender(AggregatorResponseConstants.GET_COMPLETE_STATUS, new GetCompleteStatusSender());
		addSender(AggregatorResponseConstants.CANCEL_FUTURE, new CancelRequestFutureSender());
		addSender(AggregatorResponseConstants.GET_PEER_STATUS_PROVIDER_REPEATED_ACTION, new GetPeerStatusProviderRepeatedActionSender());
		addSender(AggregatorResponseConstants.SET_ADVERTACTION_FUTURE, new SetAdvertActionFutureSender());
		addSender(AggregatorResponseConstants.HERE_IS_COMPLETE_STATUS, new HereIsCompleteStatusSender());
	}
	
}
