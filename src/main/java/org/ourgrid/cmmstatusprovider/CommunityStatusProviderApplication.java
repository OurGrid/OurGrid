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
package org.ourgrid.cmmstatusprovider;

import org.ourgrid.cmmstatusprovider.controller.CommunityStatusProviderAppController;
import org.ourgrid.discoveryservice.status.DiscoveryServiceCompleteStatus;
import org.ourgrid.peer.status.PeerCompleteStatus;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.ServerModule;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 *
 */
public class CommunityStatusProviderApplication extends ServerModule {

	/**
	 * @param containerName
	 * @param context
	 * @throws CommuneNetworkException
	 * @throws ProcessorStartException
	 */
	public CommunityStatusProviderApplication(ModuleContext context) throws CommuneNetworkException,
			ProcessorStartException {
		super(CommunityStatusProviderConstants.MODULE_NAME, context);
	}
	
	public CommunityStatusProviderApplication(ModuleContext context, ConnectionListener listener) throws CommuneNetworkException, 
		ProcessorStartException {
		super(CommunityStatusProviderConstants.MODULE_NAME, context, listener);
	}
	
	@Override
	protected void connectionCreated() {
		init();
	}
	
	/**
	 * 
	 */
	private void init() {
		CommunityStatusProviderAppController controller = (
				CommunityStatusProviderAppController) getObject(Module.CONTROL_OBJECT_NAME).getObject();
		controller.start(controller);
	}

	/* (non-Javadoc)
	 * @see br.edu.ufcg.lsd.commune.ApplicationServer#createApplicationManager()
	 */
	@Override
	protected ServerModuleManager createApplicationManager() {
		return new CommunityStatusProviderAppController();
	}
	
	/**
	 * @param callback
	 * @param peerAddress
	 */
	public void getPeerCompleteStatus(CommunityStatusProviderCallback callback, String peerAddress) {
		CommunityStatusProviderAppController controller = (
				CommunityStatusProviderAppController) getObject(Module.CONTROL_OBJECT_NAME).getObject();
		controller.getPeerCompleteStatus(callback, peerAddress);
	}

	/**
	 * @param callback
	 * @param dsAddress
	 */
	public void getDiscoveryServiceCompleteStatus(CommunityStatusProviderCallback callback, String dsAddress) {
		CommunityStatusProviderAppController controller = (
				CommunityStatusProviderAppController) getObject(Module.CONTROL_OBJECT_NAME).getObject();
		controller.getDSCompleteStatus(callback, dsAddress);
	}
	
	public PeerCompleteStatus getPeerCompleteStatus(String peerAddress) {
		CommunityStatusProviderAppController controller = (
				CommunityStatusProviderAppController) getObject(Module.CONTROL_OBJECT_NAME).getObject();
		return controller.getPeerCompleteStatus(peerAddress);
	}
	
	public DiscoveryServiceCompleteStatus getDiscoveryServiceCompleteStatus(String dsAddress) {
		CommunityStatusProviderAppController controller = (
				CommunityStatusProviderAppController) getObject(Module.CONTROL_OBJECT_NAME).getObject();
		return controller.getDSCompleteStatus(dsAddress);
	}
	
	public void addDiscoveryServiceStateListener(String userAtServer, DiscoveryServiceStateListener listener) {
		CommunityStatusProviderAppController controller = (
				CommunityStatusProviderAppController) getObject(Module.CONTROL_OBJECT_NAME).getObject();
		controller.addDiscoveryServiceStateListener(userAtServer, listener);
	}
}
