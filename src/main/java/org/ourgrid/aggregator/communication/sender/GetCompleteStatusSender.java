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

import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.aggregator.response.GetCompleteStatusResponseTO;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * This class request the complete status of a Peer.
 *
 */
public class GetCompleteStatusSender implements SenderIF<GetCompleteStatusResponseTO>{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(GetCompleteStatusResponseTO response,
			ServiceManager manager) {
		
		ServiceID serviceID = ServiceID.parse(response.getProviderAddress());
		
		PeerStatusProvider provider = manager.getStub(serviceID, PeerStatusProvider.class);
		
		provider.getCompleteHistoryStatus((PeerStatusProviderClient) 
				manager.getObjectDeployment(AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME).getObject(), 
				response.getLastUpdateTime());
	}

}
