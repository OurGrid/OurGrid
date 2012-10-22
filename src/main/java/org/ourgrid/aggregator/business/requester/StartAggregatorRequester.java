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

import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.aggregator.communication.actions.ds.GetPeerStatusProviderRepeatedAction;
import org.ourgrid.aggregator.communication.receiver.CommunityStatusProviderClientReceiver;
import org.ourgrid.aggregator.communication.receiver.PeerStatusProviderClientReceiver;
import org.ourgrid.aggregator.request.StartAggregatorRequestTO;
import org.ourgrid.common.interfaces.CommunityStatusProvider;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.CreateRepeatedActionResponseTO;
import org.ourgrid.common.internal.response.DeployServiceResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;

/**
 * This class provide a list of {@link IResponseTO} that will be executed.
 * Responsible for the start this component.
 */
public class StartAggregatorRequester implements RequesterIF<StartAggregatorRequestTO > {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IResponseTO> execute(StartAggregatorRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		HibernateUtil.setUp(request.getHibernateConfXmlPath());
		
		createServices(responses);
		
		responses.add(RegisterInterestResponseTOFactory(request));		
		responses.add(repeatedActionTOfactory());
		responses.add(new LoggerResponseTO(AggregatorControlMessages.
				getSuccessfullyStartedAggregatorMessage(), LoggerResponseTO.INFO));
		
		return responses;
	}
	
	private void createServices(List<IResponseTO> responses) {
		DeployServiceResponseTO to = new DeployServiceResponseTO();
		to.setServiceClass(CommunityStatusProviderClientReceiver.class);
		to.setServiceName(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME);
		responses.add(to);
		
		to = new DeployServiceResponseTO();
		to.setServiceClass(PeerStatusProviderClientReceiver.class);
		to.setServiceName(AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME);
		responses.add(to);
		
	}
	
	
	private CreateRepeatedActionResponseTO repeatedActionTOfactory() {
		CreateRepeatedActionResponseTO repeatedActionTO = new CreateRepeatedActionResponseTO();
		repeatedActionTO.setActionName(AggregatorConstants.GET_PEER_STATUS_PROVIDER_ACTION_NAME);
		repeatedActionTO.setRepeatedAction(new GetPeerStatusProviderRepeatedAction());
		return repeatedActionTO;
	}
	
	private RegisterInterestResponseTO RegisterInterestResponseTOFactory(StartAggregatorRequestTO request) {
		RegisterInterestResponseTO interestTO = new RegisterInterestResponseTO();
		interestTO.setMonitorName(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME);
		interestTO.setMonitorableAddress(request.getDsAddress());
		interestTO.setMonitorableType(CommunityStatusProvider.class);
		return interestTO;
	}
}
