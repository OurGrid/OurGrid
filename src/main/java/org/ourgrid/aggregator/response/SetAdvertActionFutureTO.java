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

import java.util.concurrent.TimeUnit;

import org.ourgrid.aggregator.business.requester.CommunityStatusProviderIsUpRequester;
import org.ourgrid.aggregator.communication.sender.AggregatorResponseConstants;
import org.ourgrid.aggregator.communication.sender.SetAdvertActionFutureSender;
import org.ourgrid.common.internal.IResponseTO;

/**
 * This class is a Transfer Object from one CommunityStatusProviderIsUpRequester {@link CommunityStatusProviderIsUpRequester}
 * 	to SetAdvertActionFutureSender {@link SetAdvertActionFutureSender}.
 *
 */
public class SetAdvertActionFutureTO  implements IResponseTO {
	
	private final String RESPONSE_TYPE = AggregatorResponseConstants.SET_ADVERTACTION_FUTURE;
	
	private String actionName;
	
	private long initialDelay;
	
	private long delay;
	
	private TimeUnit timeUnit;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getResponseType() {
		return RESPONSE_TYPE;
	}
	
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	public String getActionName() {
		return actionName;
	}
	
	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}
	public long getInitialDelay() {
		return initialDelay;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public long getDelay() {
		return delay;
	}
	
	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
	
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}
	
}
