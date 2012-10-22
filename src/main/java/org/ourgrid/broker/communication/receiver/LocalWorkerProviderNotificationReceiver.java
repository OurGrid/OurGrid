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
package org.ourgrid.broker.communication.receiver;

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.request.LWPDoNotifyFailureRequestTO;
import org.ourgrid.broker.request.LWPDoNotifyRecoveryRequestTO;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

/**
 * The Peer Monitor. Realizes an action when the Peer which the Broker is logged has failed.
 */
public class LocalWorkerProviderNotificationReceiver {

	private static LocalWorkerProviderNotificationReceiver instance;
	
	public static LocalWorkerProviderNotificationReceiver getInstance() {
		if (instance == null) {
			instance = new LocalWorkerProviderNotificationReceiver();
		}
		return instance;
	}
	
	private LocalWorkerProviderNotificationReceiver() {}
	
	/**
	 * Notifies a Peer failure. The Broker will not be able to request workers to this Peer.
	 * @param monitorable The Peer that is being monitored.
	 * @param failedLocalWorkerProviderID The ID of the peer that had its failure notified.
	 */
	@Req("REQ328")
	public void doNotifyFailure(ServiceManager serviceManager, LocalWorkerProvider monitorable, DeploymentID failedLocalWorkerProviderID) {
		
		LWPDoNotifyFailureRequestTO to = new LWPDoNotifyFailureRequestTO();
		to.setPeerAddress(failedLocalWorkerProviderID.getServiceID().toString());
		to.setPeerID(failedLocalWorkerProviderID.toString());
		to.setPeerPublicKey(failedLocalWorkerProviderID.getPublicKey());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	/**
	 * Notifies a Peer recovery. The Broker will try to log in the Peer again.
	 * @param monitorableStub The Peer that is being monitored.
	 * @param recoveredLocalWorkerProviderID The ID of the peer that had its recovery notified.
	 */
	@Req("REQ327")
	public void doNotifyRecovery(ServiceManager serviceManager, LocalWorkerProvider monitorableStub, DeploymentID recoveredLocalWorkerProviderID) {
		
		LWPDoNotifyRecoveryRequestTO to = new LWPDoNotifyRecoveryRequestTO();
		
		to.setPeerAddress(recoveredLocalWorkerProviderID.getServiceID().toString());
		to.setPeerID(recoveredLocalWorkerProviderID.toString());
		to.setClientDeployed(isClientDeployed(serviceManager));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	private boolean isClientDeployed(ServiceManager serviceManager) {
		ObjectDeployment objectDeployment = serviceManager.getObjectDeployment(BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);
		
		if (objectDeployment == null) {
			return false;
		}
		
		return objectDeployment.getObject() != null;
		
		//return (objectDeployment == null) ? false : objectDeployment.getObject() != null;
	}

}
