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
package org.ourgrid.aggregator.communication.sender;

import java.util.concurrent.Future;

import org.ourgrid.aggregator.communication.dao.AdvertActionFutureDAO;
import org.ourgrid.aggregator.response.SetAdvertActionFutureTO;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

/**
 * This class create a Future {@link Future} and set him in your 
 * data base {@link AdvertActionFutureDAO}.
 */
public class SetAdvertActionFutureSender implements SenderIF<SetAdvertActionFutureTO>{
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(SetAdvertActionFutureTO response,
			ServiceManager manager) {
		Future<?> advertActionFuture = manager.scheduleActionWithFixedDelay(
				response.getActionName(), 
				response.getInitialDelay(), response.getDelay(), 
				response.getTimeUnit());

		AdvertActionFutureDAO.getInstance().setAdvertActionFuture(advertActionFuture);
	}

}
