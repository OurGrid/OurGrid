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

import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.aggregator.response.GetPeerStatusProviderRepeatedActionResponseTO;
import org.ourgrid.common.interfaces.CommunityStatusProvider;
import org.ourgrid.common.interfaces.CommunityStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * This class request, of a Discorvery Service, the list of Peers and the Peer's status.
 */
public class GetPeerStatusProviderRepeatedActionSender implements SenderIF<GetPeerStatusProviderRepeatedActionResponseTO>{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(GetPeerStatusProviderRepeatedActionResponseTO response,
			ServiceManager manager) {
		
		String providerAddress = response.getProviderAddress();
		ServiceID providerID = ServiceID.parse(providerAddress);	

			
		CommunityStatusProvider commStatusProvider = manager.
								getStub(providerID, CommunityStatusProvider.class);
		
		
		if (commStatusProvider != null) {
			
			CommunityStatusProviderClient communityStatusProviderClient = 
				(CommunityStatusProviderClient) manager.
				getObjectDeployment(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME).getObject();
			
			commStatusProvider.getPeerStatusProviders(communityStatusProviderClient);
			
			commStatusProvider.getPeerStatusChangeHistory(communityStatusProviderClient,
									response.getPeerStatusChangeLastUpdate());
		}
		
	}

}


