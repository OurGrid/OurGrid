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
package org.ourgrid.aggregator.communication.receiver;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.aggregator.request.CommunityStatusProviderIsDownRequestTO;
import org.ourgrid.aggregator.request.CommunityStatusProviderIsUpRequestTO;
import org.ourgrid.aggregator.request.HereIsPeerStatusChangeHistoryRequestTO;
import org.ourgrid.aggregator.request.HereIsStatusProviderListRequestTO;
import org.ourgrid.common.interfaces.CommunityStatusProvider;
import org.ourgrid.common.interfaces.CommunityStatusProviderClient;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;
import org.ourgrid.peer.PeerConstants;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * This class implements the interface CommunityStatusProviderClient and serves to
 * notify the Community Status.
 *
 */
public class CommunityStatusProviderClientReceiver implements CommunityStatusProviderClient {

	private ServiceManager serviceManager;
	
	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsStatusProviderList(List<String> statusProviders) {
		HereIsStatusProviderListRequestTO request = new HereIsStatusProviderListRequestTO();
		
		List<String> newStatusProviderAddress = new ArrayList<String>();
		
		
		for (int i=0; i < statusProviders.size(); i++) {
			newStatusProviderAddress.add(serviceIDProvider(statusProviders.get(i)).toString());			
		}
		
		request.setStatusProviders(newStatusProviderAddress);
		OurGridRequestControl.getInstance().execute(request, serviceManager);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsPeerStatusChangeHistory(
			List<DS_PeerStatusChange> statusChanges, long since) {
		HereIsPeerStatusChangeHistoryRequestTO request = new HereIsPeerStatusChangeHistoryRequestTO();
		
		request.setStatusChanges(statusChanges);
		request.setSince(since);
		
		OurGridRequestControl.getInstance().execute(request, serviceManager);
		
	}
	
	/**
	 * Notifies the Community Status Provider failure and send a request.
	 * @param monitorable {@link CommunityStatusProvider}
	 */
	@FailureNotification
	public void doNotifyFailure(CommunityStatusProvider monitorable, DeploymentID monitorableID) {
		
		CommunityStatusProviderIsDownRequestTO request = new CommunityStatusProviderIsDownRequestTO();
		request.setProviderAddress(monitorableID.getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(request, serviceManager);
	}
	
	/**
	 * Notifies the Community Status Provider recovery and send a request.
	 * @param commStatusProvider {@link CommunityStatusProvider}
	 */
	@RecoveryNotification
	public void doNotifyRecovery(CommunityStatusProvider commStatusProvider) {
	
		CommunityStatusProviderIsUpRequestTO request = new CommunityStatusProviderIsUpRequestTO();
		request.setCommStatusProviderAddress(serviceManager.getStubDeploymentID(commStatusProvider)
				.getServiceID().toString());
			
		OurGridRequestControl.getInstance().execute(request, serviceManager);
		
	}	
	
	private ServiceID serviceIDProvider(String providerAddress) {
		String[] addresses = providerAddress.split("@");
			
		return new ServiceID(addresses[0], addresses[1], PeerConstants.MODULE_NAME, Module.CONTROL_OBJECT_NAME);
	}

}
