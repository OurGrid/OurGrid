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
package org.ourgrid.peer.business.controller.ds;

import java.util.List;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.DiscoveryServiceClientDAO;
import org.ourgrid.peer.response.CancelDiscoveryServiceAdvertResponseTO;
import org.ourgrid.peer.response.GetRemoteWorkerProvidersResponseTO;
import org.ourgrid.peer.response.ScheduleDSActionResponseTO;

/**
 * Implements Peer actions when a discovery service recover or fail.
 */
public class DiscoveryServiceNotificationController  {

	private static DiscoveryServiceNotificationController instance;

	public static DiscoveryServiceNotificationController getInstance() {
		if (instance == null) {
			instance = new DiscoveryServiceNotificationController();
		}
		return instance;
	}

	private DiscoveryServiceNotificationController() {}
	
	/**
	 * Notifies the Discovery Service fail.
	 * @param monitorable The DiscoveryService that has failed.
	 * @param deploymentID The DeploymentID of the DiscoveryService that has failed.
	 */
	public void doNotifyFailure(List<IResponseTO> responses, String dsServiceID) {
		removeAliveDiscoveryService(responses, dsServiceID);
		registerInterestInAllDiscoveryServices(responses);
	}

	private void registerInterestInAllDiscoveryServices(
			List<IResponseTO> responses) {
		
		DiscoveryServiceClientDAO dao = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
		
		for (String dsId : dao.getDsAddresses()) {
			RegisterInterestResponseTO registerInterestResponse = new RegisterInterestResponseTO();
			registerInterestResponse.setMonitorableAddress(dsId);
			registerInterestResponse.setMonitorableType(DiscoveryService.class);
			registerInterestResponse.setMonitorName(PeerConstants.DS_CLIENT);
			
			responses.add(registerInterestResponse);
		}
	}

	public void removeAliveDiscoveryService(List<IResponseTO> responses, String dsAddress) {
		
		DiscoveryServiceClientDAO dao = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
		dao.removeAliveDiscoveryService(dsAddress);
		
		if (dao.isAliveDsListEmpty()) {
			CancelDiscoveryServiceAdvertResponseTO advertResponse = new CancelDiscoveryServiceAdvertResponseTO();
			responses.add(advertResponse);
		}
		
		ReleaseResponseTO releaseTO = new ReleaseResponseTO();
		releaseTO.setStubAddress(dsAddress);
		
		responses.add(releaseTO);
	}
	
	/**
	 * Notifies the Discovery Service recovery.
	 * @param dsActionDelay 
	 */
	public void doNotifyRecovery(List<IResponseTO> responses, String dsServiceID, int dsRequestSize) {

		DiscoveryServiceClientDAO dao = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
		
		boolean listWasEmpty = dao.isAliveDsListEmpty();
		
		boolean listIsFull = dao.addAliveDiscoveryServiceAddress(dsServiceID);
		
		GetRemoteWorkerProvidersResponseTO getRWPResponseTO = new GetRemoteWorkerProvidersResponseTO();
		getRWPResponseTO.setDsClientObjectName(PeerConstants.DS_CLIENT);
		getRWPResponseTO.setDsAddress(dsServiceID);
		getRWPResponseTO.setDsRequestSize(dsRequestSize);
		
		responses.add(getRWPResponseTO);
		
		if (listWasEmpty) {
			ScheduleDSActionResponseTO schedulerActionResponse = new ScheduleDSActionResponseTO();
			responses.add(schedulerActionResponse);
		}
		
		if (listIsFull) {
			for (String dsId : dao.getDsAddresses()) {
				if (!dsId.equals(dsServiceID) && !dao.isDsAlive(dsId)) {
					ReleaseResponseTO releaseTO = new ReleaseResponseTO();
					releaseTO.setStubAddress(dsId);
					
					responses.add(releaseTO);
				}
			}
		}
		
	}
	
}
