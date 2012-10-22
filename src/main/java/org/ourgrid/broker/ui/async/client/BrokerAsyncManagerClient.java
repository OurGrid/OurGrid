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
package org.ourgrid.broker.ui.async.client;

import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.common.interfaces.control.BrokerControlClient;
import org.ourgrid.common.interfaces.management.BrokerManager;
import org.ourgrid.common.interfaces.status.BrokerStatusProviderClient;
import org.ourgrid.common.interfaces.to.BrokerCompleteStatus;
import org.ourgrid.common.interfaces.to.JobsPackage;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.async.AsyncManagerClient;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class BrokerAsyncManagerClient extends AsyncManagerClient<BrokerManager> implements BrokerStatusProviderClient, BrokerControlClient {

	public void hereIsCompleteStatus(ServiceID statusProviderServiceID,
			BrokerCompleteStatus status) {
		getBrokerApplicationClient().getModel().hereIsCompleteStatus(status);
		
	}

	public void hereIsConfiguration(Map<String, String> configuration) {
		// TODO Auto-generated method stub
		
	}

	public void hereIsUpTime(long uptime) {
		// TODO Auto-generated method stub
		
	}

	public void operationSucceed(ControlOperationResult controlOperationResult) {
		BrokerAsyncApplicationClient applicationClient = getBrokerApplicationClient();
		
		if (controlOperationResult.getErrorCause() != null) {
			JOptionPane.showMessageDialog(null, controlOperationResult.getErrorCause(), 
					"Error on control operation", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (applicationClient.getModel().isBrokerToStartOnRecovery()) {
			
			applicationClient.brokerStarted();
			applicationClient.setBrokerStartOnRecovery(false);
		}
		
	}
	
	/**
	 * @return
	 */
	private BrokerAsyncApplicationClient getBrokerApplicationClient() {
		return (BrokerAsyncApplicationClient) getServiceManager().getApplication();
	}
	
	@Override
	@RecoveryNotification
	public void controlIsUp(BrokerManager control) {
		super.controlIsUp(control);

		BrokerAsyncApplicationClient applicationClient = getBrokerApplicationClient();
		
		if (applicationClient.getModel().isBrokerToStartOnRecovery()) {
			applicationClient.start();
		} else {
			applicationClient.brokerStarted();
		}
	}

	
	@Override
	@FailureNotification
	public void controlIsDown(BrokerManager control) {
		super.controlIsDown(control);
		
		BrokerAsyncApplicationClient componentClient = getBrokerApplicationClient();
		componentClient.brokerStopped();
	}

	public void hereIsJobsStatus(ServiceID statusProviderServiceID,
			JobsPackage jobsStatus) {
		// TODO Auto-generated method stub
		
	}

	public void hereIsCompleteJobsStatus(ServiceID statusProviderServiceID,
			JobsPackage jobsStatus) {
		// TODO Auto-generated method stub
		
	}

	public void hereIsPagedTasks(ServiceID serviceID, Integer jobId,
			Integer offset, List<TaskStatusInfo> pagedTasks) {
		// TODO Auto-generated method stub
		
	}
}
