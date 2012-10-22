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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.broker.business.messages.BrokerControlMessages;
import org.ourgrid.broker.business.requester.BrokerRequestControl;
import org.ourgrid.broker.request.AddJobRequestTO;
import org.ourgrid.broker.request.CancelJobRequestTO;
import org.ourgrid.broker.request.CleanAllFinishedJobsRequestTO;
import org.ourgrid.broker.request.CleanFinishedJobRequestTO;
import org.ourgrid.broker.request.GetBrokerCompleteStatusRequestTO;
import org.ourgrid.broker.request.GetCompleteJobsStatusRequestTO;
import org.ourgrid.broker.request.GetJobStatusRequestTO;
import org.ourgrid.broker.request.GetPagedTasksRequestTO;
import org.ourgrid.broker.request.JobEndedInterestedIsDownRequestTO;
import org.ourgrid.broker.request.NotifyWhenJobIsFinishedRequestTO;
import org.ourgrid.broker.request.StartBrokerRequestTO;
import org.ourgrid.broker.request.StopBrokerRequestTO;
import org.ourgrid.common.interfaces.control.BrokerControlClient;
import org.ourgrid.common.interfaces.management.BrokerManager;
import org.ourgrid.common.interfaces.status.BrokerStatusProviderClient;
import org.ourgrid.common.interfaces.to.JobEndedInterested;
import org.ourgrid.common.internal.OurGridControlReceiver;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.internal.RequestControlIF;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.control.ModuleControlClient;
import br.edu.ufcg.lsd.commune.container.control.ModuleNotStartedException;
import br.edu.ufcg.lsd.commune.container.control.ModuleStoppedException;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.ContainerDAO;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

/**
 *
 */
public class BrokerControlReceiver extends OurGridControlReceiver implements BrokerManager {
	
	/**
	 * Requirement 302
	 */
	@Override
	protected void startComponent() throws Exception {
		StartBrokerRequestTO to = new StartBrokerRequestTO();
		to.setJobCounterFilePath(
				getServiceManager().getContainerContext().getProperty(BrokerConfiguration.PROP_JOBCOUNTERFILEPATH));
		to.setMaxBlackListFails(getServiceManager().getContainerContext().getProperty(
				BrokerConfiguration.PROP_MAX_BL_FAILS));
		to.setMaxFails(getServiceManager().getContainerContext().getProperty(
				BrokerConfiguration.PROP_MAX_FAILS));
		to.setMaxReplicas(getServiceManager().getContainerContext().getProperty(
				BrokerConfiguration.PROP_MAX_REPLICAS));
		to.setPersistentJobEnable(getServiceManager().getContainerContext().isEnabled(BrokerConfiguration.PROP_PERSISTJOBID));
		
		String peerUserAtServer = getServiceManager().getContainerContext().getProperty(
				BrokerConfiguration.PROP_PEER_USER_AT_SERVER);
		List<String> peerAddresses = new LinkedList<String>(
				Arrays.asList(StringUtil.passToArrayStr(peerUserAtServer)));
		
		to.setPeersUserAtServer(peerAddresses);
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	@Override
	protected boolean validateStartSenderPublicKey(ModuleControlClient client, String senderPublicKey) {
		
		if(!getServiceManager().isThisMyPublicKey(senderPublicKey)) {
			getServiceManager().getLog().warn(BrokerControlMessages.getUnknownSenderControllingBrokerMessage(senderPublicKey));
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean validateStopSenderPublicKey(ModuleControlClient client, String senderPublicKey) {
		
		if(!getServiceManager().isThisMyPublicKey(senderPublicKey)) {
			//getServiceManager().getLog().warn(BrokerControlMessages.getUnknownSenderControllingBrokerMessage(senderPublicKey));
			return false;
		}
		return true;
	}
	
	@Override
	public void stop(boolean callExit, boolean force, 
			@MonitoredBy(Module.CONTROL_OBJECT_NAME) ModuleControlClient client) {
		
		StopBrokerRequestTO to = new StopBrokerRequestTO();
		//to.setCanComponentBeUsed(canComponentBeUsed(client));
		
		to.setCanComponentBeUsed(getComponentBeUsedError(client) == null);
		
		String senderPublicKey = getServiceManager().getSenderPublicKey();
		
		to.setSenderPublicKey(senderPublicKey);
		to.setThisMyPublicKey(getServiceManager().isThisMyPublicKey(senderPublicKey));
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
		
		super.stop(callExit, force, client);
		
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Req("REQ304")
	public void addJob(@MonitoredBy(Module.CONTROL_OBJECT_NAME) BrokerControlClient brokerControlClient, JobSpecification jobSpec) {
		AddJobRequestTO to = new AddJobRequestTO();
		to.setCanComponentBeUsed(canComponentBeUsed(brokerControlClient));
		
		String senderPublicKey = getServiceManager().getSenderPublicKey();
		
		to.setSenderPublicKey(senderPublicKey);
		to.setJobSpec(jobSpec);
		to.setThisMyPublicKey(getServiceManager().isThisMyPublicKey(senderPublicKey));
		to.setMaxFails(getServiceManager().getContainerContext().getProperty(
				BrokerConfiguration.PROP_MAX_FAILS));
		to.setMaxReplicas(getServiceManager().getContainerContext().getProperty(
				BrokerConfiguration.PROP_MAX_REPLICAS));
		to.setBrokerControlClientAddress(getServiceManager().getSenderServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Req("REQ305")
	public void cancelJob(@MonitoredBy(Module.CONTROL_OBJECT_NAME)BrokerControlClient callback, int jobID) {
		CancelJobRequestTO to = new CancelJobRequestTO();
		to.setBrokerControlClientAddress(getServiceManager().getSenderServiceID().toString());
		to.setCanComponentBeUsed(canComponentBeUsed(callback));
		
		String senderPublicKey = getServiceManager().getSenderPublicKey();
		
		to.setSenderPublicKey(senderPublicKey);
		to.setThisMyPublicKey(getServiceManager().isThisMyPublicKey(senderPublicKey));
		to.setJobID(jobID);
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Req("REQ306")
	public void cleanAllFinishedJobs(@MonitoredBy(Module.CONTROL_OBJECT_NAME)BrokerControlClient callback) {
		
		CleanAllFinishedJobsRequestTO to = new CleanAllFinishedJobsRequestTO();
		to.setBrokerControlClientAddress(getServiceManager().getSenderServiceID().toString());
		to.setCanComponentBeUsed(canComponentBeUsed(callback));

		String senderPublicKey = getServiceManager().getSenderPublicKey();
	
		to.setSenderPublicKey(senderPublicKey);
		to.setThisMyPublicKey(getServiceManager().isThisMyPublicKey(senderPublicKey));
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Req("REQ307")
	public void cleanFinishedJob(@MonitoredBy(Module.CONTROL_OBJECT_NAME)BrokerControlClient callback, int jobID) {
		
		CleanFinishedJobRequestTO to = new CleanFinishedJobRequestTO();
		to.setBrokerControlClientAddress(getServiceManager().getSenderServiceID().toString());
		to.setCanComponentBeUsed(canComponentBeUsed(callback));
		to.setJobID(jobID);
		
		String senderPublicKey = getServiceManager().getSenderPublicKey();
		
		to.setSenderPublicKey(senderPublicKey);
		to.setThisMyPublicKey(getServiceManager().isThisMyPublicKey(senderPublicKey));
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	@Req("REQ308")
	public void notifyWhenJobIsFinished(@MonitoredBy(Module.CONTROL_OBJECT_NAME)BrokerControlClient callback,
			@MonitoredBy(Module.CONTROL_OBJECT_NAME) JobEndedInterested interested, int jobID) {
		
		NotifyWhenJobIsFinishedRequestTO to = new NotifyWhenJobIsFinishedRequestTO();
		to.setBrokerControlClientAddress(getServiceManager().getSenderServiceID().toString());
		to.setCanComponentBeUsed(canComponentBeUsed(callback));
		to.setJobID(jobID);
		
		String senderPublicKey = getServiceManager().getSenderPublicKey();
		
		to.setSenderPublicKey(senderPublicKey);
		to.setThisMyPublicKey(getServiceManager().isThisMyPublicKey(senderPublicKey));
		to.setInterestedDeploymentID(getServiceManager().getStubDeploymentID(interested).toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Override
	public String getComponentName() {
		return "Broker";
	}
	
	/**
	 * Retrieves to the callback client complete info on this Broker
	 * @param client The client that requested info
	 * @param entityID The entityID of this Broker status provider
	 */
	public void getCompleteStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) BrokerStatusProviderClient client) {
		GetBrokerCompleteStatusRequestTO to = new GetBrokerCompleteStatusRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(client).getServiceID().toString());
		to.setConfiguration(getServiceManager().getContainerContext().toString());
		to.setUptime(getServiceManager().getContainerDAO().getUpTime());
		to.setMyAddress(getServiceManager().getMyDeploymentID().getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	/**
	 * Retrieves to the callback client complete info on this Broker
	 * @param client The client that requested info
	 * @param entityID The entityID of this Broker status provider
	 */
	public void getCompleteJobsStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) BrokerStatusProviderClient client, List<Integer> jobsIds) {
		GetCompleteJobsStatusRequestTO to = new GetCompleteJobsStatusRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(client).getServiceID().toString());
		to.setJobsIds(jobsIds);
		to.setMyAddress(getServiceManager().getMyDeploymentID().getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	
	public void registerAsListener(@MonitoredBy(Module.CONTROL_OBJECT_NAME) BrokerStatusProviderClient client) {
		// TODO Auto-generated method stub
	}
	
	@RecoveryNotification
	public void statusProviderClientIsUp(BrokerStatusProviderClient statusProviderClient) {}
	
	@FailureNotification
	public void statusProviderClientIsDown(BrokerStatusProviderClient statusProviderClient) {}
	
	@RecoveryNotification
	public void controlClientIsUp(BrokerControlClient client) {}
	
	@FailureNotification
	public void controlClientIsDown(BrokerControlClient client) {}
	
	@RecoveryNotification
	public void jobEndedInterestedIsUp(JobEndedInterested interested, DeploymentID interestedID){}
	
	@FailureNotification
	public void jobEndedInterestedIsDown(JobEndedInterested interested, DeploymentID interestedID){
		JobEndedInterestedIsDownRequestTO to = new JobEndedInterestedIsDownRequestTO();
		to.setInterestedAddress(interestedID.getServiceID().toString());
		to.setInterestedAddress(interestedID.toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	@Override
	protected RequestControlIF createRequestControl() {
		return new BrokerRequestControl();
	}
	
	private Exception getComponentBeUsedError(ModuleControlClient client) {
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

	public void getJobsStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) BrokerStatusProviderClient client,
			List<Integer> jobsIds) {
		GetJobStatusRequestTO to = new GetJobStatusRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(client).getServiceID().toString());
		to.setJobsIds(jobsIds);
		to.setMyAddress(getServiceManager().getMyDeploymentID().getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	public void getPagedTasks(@MonitoredBy(Module.CONTROL_OBJECT_NAME) BrokerStatusProviderClient client, Integer jobId,
			Integer offset, Integer pageSize) {
		
		GetPagedTasksRequestTO to = new GetPagedTasksRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(client).getServiceID().toString());
		to.setJobId(jobId);
		to.setOffset(offset);
		to.setPageSize(pageSize);
		to.setMyAddress(getServiceManager().getMyDeploymentID().getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
}
