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

import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.common.interfaces.management.WorkerManager;
import org.ourgrid.common.internal.OurGridControlReceiver;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.internal.RequestControlIF;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.messages.ControlMessages;
import org.ourgrid.worker.business.requester.WorkerRequestControl;
import org.ourgrid.worker.request.GetMasterPeerRequestTO;
import org.ourgrid.worker.request.GetStatusRequestTO;
import org.ourgrid.worker.request.GetWorkerCompleteStatusRequestTO;
import org.ourgrid.worker.request.PauseWorkerRequestTO;
import org.ourgrid.worker.request.ResumeWorkerRequestTO;
import org.ourgrid.worker.request.StartWorkerRequestTO;
import org.ourgrid.worker.request.StopWorkerRequestTO;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.control.ModuleControlClient;
import br.edu.ufcg.lsd.commune.container.control.ModuleNotStartedException;
import br.edu.ufcg.lsd.commune.container.control.ModuleStoppedException;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.ContainerDAO;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * Perform the Worker component control actions.
 */
@Req("REQ010")
public class WorkerComponentReceiver extends OurGridControlReceiver implements WorkerManager {


	public String getComponentName() {
		return "Worker";
	}
	
	protected void startComponent() throws Exception {
		
		StartWorkerRequestTO to = new StartWorkerRequestTO();
		to.setPropertiesCollectorOn(isPropertiesCollectorOn());
		to.setIdlenessSchedule(useIdlenessSchedule());
		to.setIdlenessScheduleTime(
				getServiceManager().getContainerContext().getProperty(
						WorkerConfiguration.PROP_IDLENESS_SCHEDULE_TIME));
		to.setExecutionClientDeployed(isExecutionClientDeployed());
			
		boolean idlenessDetectorOn = isIdlenessDetectorOn();
		
		to.setIdlenessDetectorOn(idlenessDetectorOn);
		
		if (idlenessDetectorOn) {
			to.setIdlenessTime(
					getServiceManager().getContainerContext().parseLongProperty(WorkerConfiguration.PROP_IDLENESS_TIME)*1000);
		}
		
		String masterPeerUserAtServer = getServiceManager().getContainerContext().getProperty(
						WorkerConfiguration.PROP_PEER_ADDRESS);
		int userAtServerSeparator = masterPeerUserAtServer.indexOf("@");
		
		String user = masterPeerUserAtServer.substring(0, userAtServerSeparator);
		String server = masterPeerUserAtServer.substring(userAtServerSeparator + 1);
		
		ContainerID masterPeerContainerID = new ContainerID(user, server, PeerConstants.MODULE_NAME);
		ServiceID masterPeerServiceID = new ServiceID(masterPeerContainerID,
				PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
		to.setMasterPeerAddress(masterPeerServiceID.toString());
	
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}


	public void stop(boolean callExit, boolean force, 
			@MonitoredBy(Module.CONTROL_OBJECT_NAME) ModuleControlClient client) {

		String senderPublicKey = getServiceManager().getSenderPublicKey();
		
		StopWorkerRequestTO stopWorkerRequestTO = new StopWorkerRequestTO();
		stopWorkerRequestTO.setComponentBeUsed(getComponentBeUsedError((client)) == null);
		stopWorkerRequestTO.setStopSenderPublicKeyValid(validateStopSenderPublicKey(client, senderPublicKey));
		stopWorkerRequestTO.setStopSenderPublicKey(senderPublicKey);
		
		OurGridRequestControl.getInstance().execute(stopWorkerRequestTO, getServiceManager());
		
		super.stop(callExit, force, client);
	}

	@Req("REQ087")
	public void pause(@MonitoredBy(Module.CONTROL_OBJECT_NAME) WorkerControlClient client) {
		
		PauseWorkerRequestTO to = new PauseWorkerRequestTO();
		Exception componentBeUsedError = getComponentBeUsedError((client));
		to.setComponentBeUsed(componentBeUsedError==null);
		to.setErrorCause(componentBeUsedError);
		String senderPublicKey = getServiceManager().getSenderPublicKey();
		to.setSenderPublicKey(senderPublicKey);
		
		String clientAddress = null;
		
		DeploymentID clientDeploymentID = getServiceManager().getStubDeploymentID(client);
		if (clientDeploymentID != null) {
			clientAddress = clientDeploymentID.getServiceID().toString();
		} else {
			clientAddress = getServiceManager().getLocalDeploymentID(client).getServiceID().toString();
			to.setRemoteClient(false);
		}
		
		to.setClientAddress(clientAddress);
		to.setThisMyPublicKey(getServiceManager().isThisMyPublicKey(senderPublicKey));

		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Req("REQ088")
	public void resume(@MonitoredBy(Module.CONTROL_OBJECT_NAME) WorkerControlClient client) {

		ResumeWorkerRequestTO to = new ResumeWorkerRequestTO();
		Exception componentBeUsedError = getComponentBeUsedError((client));
		to.setComponentBeUsed(componentBeUsedError==null);
		to.setErrorCause(componentBeUsedError);
		String senderPublicKey = getServiceManager().getSenderPublicKey();
		to.setSenderPublicKey(senderPublicKey);
		
		String clientAddress = null;
		
		DeploymentID clientDeploymentID = getServiceManager().getStubDeploymentID(client);
		if (clientDeploymentID != null) {
			clientAddress = clientDeploymentID.getServiceID().toString();
		} else {
			clientAddress = getServiceManager().getLocalDeploymentID(client).getServiceID().toString();
			to.setRemoteClient(false);
		}
		
		to.setClientAddress(clientAddress);
		to.setThisMyPublicKey(getServiceManager().isThisMyPublicKey(senderPublicKey));
		to.setExecutionClientDeployed(isExecutionClientDeployed());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Override
	protected boolean validateStartSenderPublicKey(ModuleControlClient client, String senderPublicKey) {
		
		if(!getServiceManager().isThisMyPublicKey(senderPublicKey)) {
			getServiceManager().getLog().warn(ControlMessages.getUnknownEntityTryingToStartWorkerMessage(senderPublicKey));
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean validateStopSenderPublicKey(ModuleControlClient client, String senderPublicKey) {
		
		if(!getServiceManager().isThisMyPublicKey(senderPublicKey)) {
			return false;
		}
		return true;
	}

	/**
	 * Retrieves to the callback client complete info on this worker
	 * @param client The client that requested info
	 */
	@Req("REQ095")
	public void getCompleteStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) WorkerControlClient client) {
		GetWorkerCompleteStatusRequestTO to = new GetWorkerCompleteStatusRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setUptime(getServiceManager().getContainerDAO().getUpTime());
		to.setClientAddress(getServiceManager().getStubDeploymentID(client).getServiceID().toString());
		
		ModuleContext context = getServiceManager().getContainerContext();
		
		to.setConfiguration(WorkerConfiguration.toString(context));
		to.setContextPlaypenDir(context.getProperty(WorkerConfiguration.PROP_PLAYPEN_ROOT));
		to.setContextStorageDir(context.getProperty(WorkerConfiguration.PROP_STORAGE_DIR));
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Req("REQ094")
	public void getStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) WorkerControlClient client) {
		GetStatusRequestTO to = new GetStatusRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(client).getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Req("REQ093")
	public void getMasterPeer(@MonitoredBy(Module.CONTROL_OBJECT_NAME) WorkerControlClient client) {
		GetMasterPeerRequestTO to = new GetMasterPeerRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(client).getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	private boolean isIdlenessDetectorOn() {
		String propIdlenessDetector = 
			(String) getServiceManager().getContainerContext().getProperty(WorkerConfiguration.PROP_IDLENESS_DETECTOR);
		
		return (propIdlenessDetector == null) ? 
			false :
			!propIdlenessDetector.equals(WorkerConfiguration.DEF_PROP_IDLENESS_DETECTOR);
	}
	
	private boolean useIdlenessSchedule() {
		String propUseIdlenessSchedule = 
			(String) getServiceManager().getContainerContext().getProperty(WorkerConfiguration.PROP_USE_IDLENESS_SCHEDULE);
		
		return (propUseIdlenessSchedule == null) ? 
				false :
				!propUseIdlenessSchedule.equals(WorkerConfiguration.DEF_PROP_USE_IDLENESS_SCHEDULE);
	}

	private boolean isPropertiesCollectorOn() {
		String propCollector = 
			(String) getServiceManager().getContainerContext().getProperty(WorkerConfiguration.PROP_WORKER_SPEC_REPORT);
		
		return (propCollector == null) ? false: 
			!propCollector.equals(WorkerConfiguration.DEF_WORKER_SPEC_REPORT);
		
	}
	
	@RecoveryNotification
	public void controlClientIsUp(WorkerControlClient client) {}
	
	@FailureNotification
	public void controlClientIsDown(WorkerControlClient client) {}
	
	public Exception getComponentBeUsedError(ModuleControlClient client) {
		ContainerDAO dao = getServiceManager().getContainerDAO();
		
		ControlOperationResult result = null;
		
		if(dao.isStopped()){
			result = new ControlOperationResult(new ModuleStoppedException(getComponentName() + " is stopped"));
		}
		else if (!dao.isStarted()) {
			result = new ControlOperationResult(new ModuleNotStartedException(getComponentName()));
		}
		
		return (result != null) ? result.getErrorCause() : null;
	}
	
	private boolean isExecutionClientDeployed() {
		return getServiceManager().getObjectDeployment(WorkerConstants.WORKER_EXECUTION_CLIENT) != null;
	}

	@Override
	protected RequestControlIF createRequestControl() {
		return new WorkerRequestControl();
	}
		
}