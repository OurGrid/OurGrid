/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of Commune. 
 *
 * Commune is free software: you can redistribute it and/or modify it under the
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
package org.ourgrid.cmmstatusprovider.controller;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.ourgrid.cmmstatusprovider.CommunityStatusProviderCallback;
import org.ourgrid.cmmstatusprovider.CommunityStatusProviderConstants;
import org.ourgrid.cmmstatusprovider.DiscoveryServiceStateListener;
import org.ourgrid.cmmstatusprovider.dao.CommunityStatusProviderDAO;
import org.ourgrid.common.interfaces.status.DiscoveryServiceStatusProvider;
import org.ourgrid.common.interfaces.status.DiscoveryServiceStatusProviderClient;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.status.DiscoveryServiceCompleteStatus;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.status.PeerCompleteStatus;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.control.ModuleControlClient;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleController;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncContainerUtil;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class CommunityStatusProviderAppController extends
		ServerModuleController implements ModuleControlClient {


	private static final int POLLING_TIMEOUT = 60;
	private final BlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<Object>(1);
	private static final int HEARTBEAT_DELAY = 30;
	private static final int DETECTION_TIME = 120;
	
	/* (non-Javadoc)
	 * @see br.edu.ufcg.lsd.commune.container.control.ApplicationServerController#createServices()
	 */
	@Override
	protected void createServices() {
		getServiceManager().deploy(CommunityStatusProviderConstants.ASYNC_CMM_CLIENT, 
				new AsyncCommunityStatusProviderClientController());
		
		getServiceManager().deploy(CommunityStatusProviderConstants.SYNC_CMM_CLIENT, 
				new SyncCommunityStatusProviderClientController(blockingQueue));
	}
	
	public void addDiscoveryServiceStateListener(String userAtServer, DiscoveryServiceStateListener listener) {
		getServiceManager().getDAO(CommunityStatusProviderDAO.class).addDiscoveryServiceStateListener(userAtServer, listener);
	}
	
	/* (non-Javadoc)
	 * @see br.edu.ufcg.lsd.commune.container.control.ApplicationServerController#createDAOs()
	 */
	@Override
	protected void createDAOs() {
		getServiceManager().createDAO(CommunityStatusProviderDAO.class);
	}
	
	public void getPeerCompleteStatus(CommunityStatusProviderCallback callback, String peerAddress) {
		String[] splitAddress = peerAddress.split("@");
		ServiceID serviceID = new ServiceID(splitAddress[0], splitAddress[1], 
				PeerConstants.MODULE_NAME, Module.CONTROL_OBJECT_NAME);
		
		CommunityStatusProviderDAO dao = getServiceManager().getDAO(CommunityStatusProviderDAO.class);
		dao.addPeerCallback(serviceID, callback);
		
		PeerStatusProvider peerStatusProvider = dao.getPeerStatusProvider(serviceID);
		
		if (peerStatusProvider == null) {
			getServiceManager().registerInterest(CommunityStatusProviderConstants.ASYNC_CMM_CLIENT, 
					serviceID.toString(), PeerStatusProvider.class, DETECTION_TIME, HEARTBEAT_DELAY);
		} else {
			PeerStatusProviderClient statusProviderClient = (PeerStatusProviderClient) getServiceManager().getObjectDeployment(
					CommunityStatusProviderConstants.ASYNC_CMM_CLIENT).getObject();
			peerStatusProvider.getCompleteStatus(statusProviderClient);
		}
		
	}

	/**
	 * @param callback
	 * @param dsAddress
	 */
	public void getDSCompleteStatus(CommunityStatusProviderCallback callback,
			String dsAddress) {
		String[] splitAddress = dsAddress.split("@");
		ServiceID serviceID = new ServiceID(splitAddress[0], splitAddress[1], 
				DiscoveryServiceConstants.MODULE_NAME, Module.CONTROL_OBJECT_NAME);
		
		CommunityStatusProviderDAO dao = getServiceManager().getDAO(CommunityStatusProviderDAO.class);
		dao.addDsCallback(serviceID, callback);
		
		DiscoveryServiceStatusProvider dsStatusProvider = dao.getDSStatusProvider(serviceID);
		
		if (dsStatusProvider == null) {
			getServiceManager().registerInterest(CommunityStatusProviderConstants.ASYNC_CMM_CLIENT, 
					serviceID.toString(), DiscoveryServiceStatusProvider.class, DETECTION_TIME, HEARTBEAT_DELAY);
		} else {
			DiscoveryServiceStatusProviderClient statusProviderClient = (DiscoveryServiceStatusProviderClient) 
					getServiceManager().getObjectDeployment(CommunityStatusProviderConstants.ASYNC_CMM_CLIENT).getObject();
			dsStatusProvider.getCompleteStatus(statusProviderClient);
		}
		
	}

	/**
	 * @param peerAddress
	 * @return
	 */
	public PeerCompleteStatus getPeerCompleteStatus(String peerAddress) {
		String[] splitAddress = peerAddress.split("@");
		
		ServiceID serviceID = new ServiceID(splitAddress[0], splitAddress[1], 
				PeerConstants.MODULE_NAME, Module.CONTROL_OBJECT_NAME);
		
		CommunityStatusProviderDAO dao = getServiceManager().getDAO(CommunityStatusProviderDAO.class);
		PeerStatusProvider peerStatusProvider = dao.getPeerStatusProvider(serviceID);
		
		if (peerStatusProvider == null) {
			getServiceManager().registerInterest(CommunityStatusProviderConstants.SYNC_CMM_CLIENT, 
					serviceID.toString(), PeerStatusProvider.class, DETECTION_TIME, HEARTBEAT_DELAY);
			
			peerStatusProvider = SyncContainerUtil.waitForResponseObject(
					blockingQueue, PeerStatusProvider.class, POLLING_TIMEOUT);
		}
		
		peerStatusProvider.getCompleteStatus((PeerStatusProviderClient) getServiceManager().getObjectDeployment(
				CommunityStatusProviderConstants.SYNC_CMM_CLIENT).getObject());
		
		return SyncContainerUtil.waitForResponseObject(blockingQueue, PeerCompleteStatus.class, POLLING_TIMEOUT);
	}

	/**
	 * @param dsAddress
	 * @return
	 */
	public DiscoveryServiceCompleteStatus getDSCompleteStatus(String dsAddress) {
		String[] splitAddress = dsAddress.split("@");
		ServiceID serviceID = new ServiceID(splitAddress[0], splitAddress[1], 
				DiscoveryServiceConstants.MODULE_NAME, Module.CONTROL_OBJECT_NAME);
		
		CommunityStatusProviderDAO dao = getServiceManager().getDAO(CommunityStatusProviderDAO.class);
		DiscoveryServiceStatusProvider dsStatusProvider = dao.getDSStatusProvider(serviceID);
		
		if (dsStatusProvider == null) {
			getServiceManager().registerInterest(CommunityStatusProviderConstants.SYNC_CMM_CLIENT, 
					serviceID.toString(), DiscoveryServiceStatusProvider.class, DETECTION_TIME, HEARTBEAT_DELAY);
			
			dsStatusProvider = SyncContainerUtil.waitForResponseObject(
					blockingQueue, DiscoveryServiceStatusProvider.class, POLLING_TIMEOUT);
		}
		
		dsStatusProvider.getCompleteStatus((DiscoveryServiceStatusProviderClient) 
				getServiceManager().getObjectDeployment(
						CommunityStatusProviderConstants.SYNC_CMM_CLIENT).getObject());
		
		return SyncContainerUtil.waitForResponseObject(blockingQueue, DiscoveryServiceCompleteStatus.class, POLLING_TIMEOUT);
	}

	/* (non-Javadoc)
	 * @see br.edu.ufcg.lsd.commune.container.control.ApplicationControlClient#operationSucceed(br.edu.ufcg.lsd.commune.container.control.ControlOperationResult)
	 */
	public void operationSucceed(ControlOperationResult controlOperationResult) {
		// TODO Auto-generated method stub
	}
	
}
