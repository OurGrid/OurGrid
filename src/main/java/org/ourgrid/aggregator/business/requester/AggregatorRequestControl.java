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
package org.ourgrid.aggregator.business.requester;

import org.ourgrid.aggregator.communication.sender.AggregatorResponseControl;
import org.ourgrid.common.internal.OurGridRequestConstants;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.internal.ResponseControlIF;
import org.ourgrid.common.internal.requester.QueryRequester;

/**
 * This class  provide an fillmap that will put these requesters with a key that is an
 * AggregatorRequestConstants {@link AggregatorRequestConstants} in the map to execute them.
 *
 */
public class AggregatorRequestControl extends OurGridRequestControl {
	
	
	protected void fillMap() {
		addRequester(OurGridRequestConstants.QUERY,
				new QueryRequester());
		
		addRequester(AggregatorRequestConstants.START_AGGREGATOR.getString(),
				new StartAggregatorRequester());
		
		addRequester(AggregatorRequestConstants.COMMUNITY_STATUS_PROVIDER_IS_DOWN.getString(),
				new CommunityStatusProviderIsDownRequester());
		
		addRequester(AggregatorRequestConstants.COMMUNITY_STATUS_PROVIDER_IS_UP.getString(),
				new CommunityStatusProviderIsUpRequester());
		
		addRequester(AggregatorRequestConstants.HERE_IS_STATUS_PROVIDER_LIST.getString(),
				new HereIsStatusProviderListRequester());
		
		addRequester(AggregatorRequestConstants.HERE_IS_COMPLETE_HISTORY_STATUS.getString(),
				new HereIsCompleteHistoryStatusRequester());
		
		addRequester(AggregatorRequestConstants.HERE_IS_PEER_STATUS_CHANGE_HISTORY.getString(),
				new HereIsPeerStatusChangeHistoryRequester());
		
		addRequester(AggregatorRequestConstants.PEER_STATUS_PROVIDER_IS_DOWN.getString(), 
				new PeerStatusProviderIsDownRequester());
		
		addRequester(AggregatorRequestConstants.PEER_STATUS_PROVIDER_IS_UP.getString(), 
				new PeerStatusProviderIsUpRequester());
		
		addRequester(AggregatorRequestConstants.GET_PEER_STATUS_PROVIDER.getString(),
				new GetPeerStatusProviderRepeatedActionRequester());
		
		addRequester(AggregatorRequestConstants.GET_COMPLETE_STATUS.getString(),
				new GetCompleteStatusRequester());
	}

	
	@Override
	protected ResponseControlIF createResponseControl() {
		return AggregatorResponseControl.getInstance();
	}
}
