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
package org.ourgrid.peer.ui.async.util;

import org.ourgrid.common.interfaces.DiscoveryService;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;


public class DiscoveryServiceRecoveryInterested {

	private ServiceManager serviceManager;

	@SuppressWarnings("unused")
	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	private boolean hasRecovered = false;
	
	@RecoveryNotification
	public void notifyRecovery(DiscoveryService monitorable) {
		this.hasRecovered = true;
		this.serviceManager.release(monitorable);
	}
	
	@FailureNotification
	public void notifyFailure(DiscoveryService monitorable) {
		this.hasRecovered = false;
	}
	
	/**
	 * Verifies if the DS has been recovered.
	 * @return <code>true</code> if the Discovery Service has been recovered. 
	 */
	public boolean hasBeenRecovered() {
		return this.hasRecovered;
	}

}
