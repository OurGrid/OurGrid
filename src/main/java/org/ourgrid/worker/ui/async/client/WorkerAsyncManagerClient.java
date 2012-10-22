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
package org.ourgrid.worker.ui.async.client;

import java.util.Map;

import javax.swing.JOptionPane;

import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.common.interfaces.management.WorkerManager;
import org.ourgrid.common.interfaces.status.WorkerCompleteStatus;
import org.ourgrid.common.interfaces.status.WorkerStatusProviderClient;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.worker.ui.async.model.WorkerAsyncUIModel;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.async.AsyncManagerClient;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class WorkerAsyncManagerClient extends AsyncManagerClient<WorkerManager> implements WorkerStatusProviderClient, 
	WorkerControlClient{

	public void hereIsCompleteStatus(WorkerCompleteStatus completeStatus) {
		getWorkerApplicationClient().getModel().updateCompleteStatus(completeStatus);
		
	}

	public void hereIsMasterPeer(ServiceID masterPeer) {
		
	}

	public void hereIsStatus(WorkerStatus workerStatus) {
		// TODO Auto-generated method stub
		
	}

	public void hereIsConfiguration(Map<String, String> configuration) {
		// TODO Auto-generated method stub
		
	}

	public void hereIsUpTime(long uptime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RecoveryNotification
	public void controlIsUp(WorkerManager control) {
		super.controlIsUp(control);
		
		if (getWorkerApplicationClient().getModel().isWorkerToStartOnRecovery()) {
			getWorkerApplicationClient().start();
		} else {
			getWorkerApplicationClient().workerStarted();
		}
	}
	
	@Override
	@FailureNotification
	public void controlIsDown(WorkerManager control) {
		super.controlIsDown(control);
		getWorkerApplicationClient().workerStopped();
	}
	
	public void operationSucceed(ControlOperationResult controlOperationResult) {
		WorkerAsyncUIModel model = getWorkerApplicationClient().getModel();
		
		if (controlOperationResult.getErrorCause() != null) {
			JOptionPane.showMessageDialog(null, controlOperationResult.getErrorCause(), 
					"Error on control operation", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (model.isWorkerToStartOnRecovery()) {
			getWorkerApplicationClient().workerStarted();
			model.setWorkerStartOnRecovery(false);
		}
		
	}

	/**
	 * @return
	 */
	private WorkerAsyncComponentClient getWorkerApplicationClient() {
		//return (WorkerAsyncComponentClient) getServiceManager().getApplication();
		return WorkerAsyncInitializer.getInstance().getComponentClient();
	}

	@Override
	public void hereIsMasterPeer(DeploymentID masterPeer) {
		// TODO Auto-generated method stub
		
	}
	
}
