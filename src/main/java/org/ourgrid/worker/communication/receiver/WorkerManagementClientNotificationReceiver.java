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
package org.ourgrid.worker.communication.receiver;

import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

/**
 * Implement Worker actions when the manager Peer fails.
 */
public class WorkerManagementClientNotificationReceiver  {

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	

//	/**
//	 * Informs that the master peer failed.
//	 * @param monitorable 
//	 * @param monitorableID
//	 */
//	@Req("REQ122")
//	@FailureNotification
//	public void doNotifyFailure(WorkerManagementClient monitorable, DeploymentID monitorableID) {
//		
//		WorkerManagementClientDoNotifyFailureRequestTO to = new WorkerManagementClientDoNotifyFailureRequestTO();
//		to.setMasterPeerPublicKey(getMasterPeerPubKey());
//		to.setMonitorableAddress(monitorableID.getServiceID().toString());
//		to.setMonitorableID(monitorableID.toString());
//		to.setMonitorablePublicKey(monitorableID.getPublicKey());
//		
//		OurGridRequestControl.getInstance().execute(to, serviceManager);
//	}
//	
//	@RecoveryNotification
//	public void doNotifyRecovery(WorkerManagementClient monitorable, DeploymentID monitorableID) {
//		
//		WorkerManagementClientDoNotifyRecoveryRequestTO to = new WorkerManagementClientDoNotifyRecoveryRequestTO();
//		to.setWorkerManagementClientAddress(monitorable == null ? null : monitorableID.getServiceID().toString());
//		to.setRecoveredPeerUserAtServer(monitorableID.getUserName() + "@" + monitorableID.getServerName());
//		to.setRecoveredPeerDeploymentID(monitorableID.toString());
//		to.setRecoveredPeerPublicKey(monitorableID.getPublicKey());
//		String validationString = "VALIDATION_STRING"; //FIXME Change the value of this string to a real validation string from deploy helper.
//		to.setValidationString(validationString);
//		
//		OurGridRequestControl.getInstance().execute(to, serviceManager);
//	}
//	
//	private String getMasterPeerPubKey() {
//		return serviceManager.getContainerContext().getProperty(WorkerConfiguration.PROP_PEER_PUBLIC_KEY);
//	}
	
}