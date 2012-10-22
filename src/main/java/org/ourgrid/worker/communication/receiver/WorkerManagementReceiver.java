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

import java.io.FileInputStream;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Properties;

import org.ourgrid.common.WorkerLoginResult;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecificationConstants;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.request.StopWorkingRequestTO;
import org.ourgrid.worker.request.WorkForBrokerRequestTO;
import org.ourgrid.worker.request.WorkForPeerRequestTO;
import org.ourgrid.worker.request.WorkerLoginSucceededRequestTO;
import org.ourgrid.worker.request.WorkerManagementClientDoNotifyFailureRequestTO;
import org.ourgrid.worker.request.WorkerManagementClientDoNotifyRecoveryRequestTO;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

/**
 * WorkerManagementReceiver, called by the management peer.
 *  Its callback is WorkerManagementClient.
 * @see org.ourgrid.common.interfaces.management.WorkerManagement
 */
@Req("REQ004")
public class WorkerManagementReceiver implements WorkerManagement {

	private ServiceManager serviceManager;

	@Req("REQ090")
	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	/**
	 * @return the serviceManager
	 */
	protected ServiceManager getServiceManager() {
		return serviceManager;
	}

	private boolean isIdlenessDetectorOn() {
		String propIdlenessDetector = 
				(String) getServiceManager().getContainerContext().
				getProperty(WorkerConfiguration.PROP_IDLENESS_DETECTOR);

		return (propIdlenessDetector == null) ? 
				false : !propIdlenessDetector.equals(WorkerConfiguration.DEF_PROP_IDLENESS_DETECTOR);
	}

	private boolean isWorkerSpecReportPropOn() {
		String propWorkerSpecReport = 
				(String) getServiceManager().getContainerContext().
				getProperty(WorkerConfiguration.PROP_WORKER_SPEC_REPORT);

		return (propWorkerSpecReport == null) ? false: 
			!propWorkerSpecReport.equals(WorkerConfiguration.DEF_WORKER_SPEC_REPORT);
	}

	@Req({"REQ006", "REQ092"})
	public void workForBroker(DeploymentID brokerID) {
		WorkForBrokerRequestTO to = new WorkForBrokerRequestTO();
		to.setSenderPublicKey(getServiceManager().getSenderPublicKey());
		to.setBrokerPublicKey(brokerID.getPublicKey());

		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	public void workForPeer(String remotePeerPublicKey, List<String> usersDN,
			List<X509Certificate> caCertificates) {

		WorkForPeerRequestTO to = new WorkForPeerRequestTO();
		to.setSenderPublicKey(getServiceManager().getSenderPublicKey());
		to.setRemotePeerPublicKey(remotePeerPublicKey);
		to.setUsersDN(usersDN);
		to.setCaCertificates(caCertificates);
		to.setClientAddress(getServiceManager().getSenderServiceID().toString());

		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Req("REQ006")
	public void workForPeer(String remotePeerPublicKey) {
		WorkForPeerRequestTO to = new WorkForPeerRequestTO();
		to.setSenderPublicKey(getServiceManager().getSenderPublicKey());
		to.setRemotePeerPublicKey(remotePeerPublicKey);
		to.setClientAddress(getServiceManager().getSenderServiceID().toString());

		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Req("REQ091")
	public void stopWorking() {
		StopWorkingRequestTO to = new StopWorkingRequestTO();
		to.setSenderPublicKey(getServiceManager().getSenderPublicKey());

		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Override
	public void loginSucceeded(@MonitoredBy (WorkerConstants.LOCAL_WORKER_MANAGEMENT)
	WorkerManagementClient workerManagementClient, WorkerLoginResult loginResult) {

		WorkerLoginSucceededRequestTO to = new WorkerLoginSucceededRequestTO();
		to.setResult(loginResult);
		to.setSenderPublicKey(getServiceManager().getSenderPublicKey());
		to.setIdlenessDetectorOn(isIdlenessDetectorOn());
		to.setWorkerSpecReportPropOn(isWorkerSpecReportPropOn());
		to.setWorkerSpecReportTime(getServiceManager().getContainerContext().parseLongProperty(
				WorkerConfiguration.PROP_WORKER_SPEC_REPORT_TIME) * 1000);


		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	/**
	 * Informs that the master peer failed.
	 * @param monitorable 
	 * @param monitorableID
	 */
	@FailureNotification
	public void doNotifyFailure(WorkerManagementClient monitorable, DeploymentID monitorableID) {

		WorkerManagementClientDoNotifyFailureRequestTO to = 
				new WorkerManagementClientDoNotifyFailureRequestTO();
		to.setMonitorableAddress(monitorableID.getServiceID().toString());
		to.setMonitorableID(monitorableID.toString());
		to.setMonitorablePublicKey(monitorableID.getPublicKey());

		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	@RecoveryNotification
	public void doNotifyRecovery(WorkerManagementClient monitorable, DeploymentID monitorableID) {
		WorkerManagementClientDoNotifyRecoveryRequestTO to = 
				new WorkerManagementClientDoNotifyRecoveryRequestTO();
		to.setWorkerManagementClientAddress(monitorable == null ?
				null : monitorableID.getServiceID().toString());

		DeploymentID myDeploymentID = serviceManager.getMyDeploymentID();
		
		WorkerSpecification workerSpecification = new WorkerSpecification();
		workerSpecification.putAttribute(OurGridSpecificationConstants.USERNAME, 
				myDeploymentID.getUserName());
		workerSpecification.putAttribute(OurGridSpecificationConstants.SERVERNAME, 
				myDeploymentID.getServerName());
		workerSpecification.putAttribute(WorkerSpecificationConstants.OS, System.getProperty("os.name"));
		
		//load user defined specifications
		Properties specFile = new Properties();
		try {
			//load spec file, if it exists
			String specFilePath = getServiceManager().getContainerContext().getProperty(WorkerConfiguration.PROP_SPEC_FILENAME);
			
			if (specFile != null) {
				specFile.load(new FileInputStream(specFilePath));
				
				if (!specFile.isEmpty()) {
					for (Object key : specFile.keySet()) {
						String keyString = (String) key;
						workerSpecification.putAttribute(keyString, 
								specFile.getProperty(keyString));
					}
				}
			}
			
		} catch (Exception e) {
			//do nothing
		}
		

		to.setWorkerSpecification(workerSpecification);

		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
}