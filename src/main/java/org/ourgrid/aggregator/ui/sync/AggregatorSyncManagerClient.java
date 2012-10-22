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
package org.ourgrid.aggregator.ui.sync;

import org.ourgrid.aggregator.status.AggregatorCompleteStatus;
import org.ourgrid.common.interfaces.control.AggregatorControlClient;
import org.ourgrid.common.interfaces.management.AggregatorManager;
import org.ourgrid.common.interfaces.status.AggregatorStatusProviderClient;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncContainerUtil;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncManagerClient;


public class AggregatorSyncManagerClient extends SyncManagerClient<AggregatorManager> implements AggregatorStatusProviderClient, 
	AggregatorControlClient {

	public void hereIsCompleteStatus(
			AggregatorCompleteStatus completeStatus) {
		SyncContainerUtil.putResponseObject(getQueue(), completeStatus);
	}

	@Override
	@RecoveryNotification
	public void controlIsUp(AggregatorManager control) {
		super.controlIsUp(control);
	}
	
	@Override
	@FailureNotification
	public void controlIsDown(AggregatorManager control) {
		super.controlIsDown(control);
	}
}
