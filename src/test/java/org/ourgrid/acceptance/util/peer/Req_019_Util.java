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
package org.ourgrid.acceptance.util.peer;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.communication.receiver.WorkerManagementClientReceiver;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_019_Util extends PeerAcceptanceUtil {

	public Req_019_Util(ModuleContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DeploymentID createAndPublishWorkerManagement(PeerComponent component, WorkerSpecification workerSpec, String workerPublicKey) {
	
		// Mock worker
	    WorkerManagement workerMock = EasyMock.createMock(WorkerManagement.class);
	    		
		DeploymentID workerDeploymentID = createWorkerManagementDeploymentID(workerPublicKey, workerSpec);
		
	    // Record mock behavior
	    EasyMock.reset(workerMock);
	    
	    AcceptanceTestUtil.publishTestObject(component, workerDeploymentID, workerMock, WorkerManagement.class, false);
	    ObjectDeployment peerControlDeployment = getPeerControlDeployment();
	    AcceptanceTestUtil.setExecutionContext(component, peerControlDeployment, workerPublicKey);
	    
	    return workerDeploymentID;
	}

	
	/**
	 * Creates a WorkerManagement interface mock, publishes it and notifies its recovery
	 * 
	 * @param component The peer Component
	 * @param workerSpec The workerSpec containing the worker attributes
	 * @param workerPublicKey The worker public key
	 * @param message The message to logger 
	 * @return Returns the WorkerManagement OID 
	 */
	public DeploymentID notifyWorkerRecoveryForTestMessage(PeerComponent component, WorkerSpecification workerSpec, String workerPublicKey, String message) {
	
		//Mock logger
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
	    
		// Mock worker
	    WorkerManagement workerMock = EasyMock.createMock(WorkerManagement.class);
		DeploymentID workerDeploymentID = createWorkerManagementDeploymentID(workerPublicKey, workerSpec);
		
		AcceptanceTestUtil.publishTestObject(component, workerDeploymentID, workerMock, WorkerManagement.class);

	    // Get peer bound object
		WorkerManagementClientReceiver workerMonitor = getWorkerMonitor();
	
	    EasyMock.replay(newLogger);

	    // Notify recovery of Worker
	    if (workerMock != null && workerMonitor != null){
	    	workerMonitor.doNotifyRecovery(workerMock, workerDeploymentID);
	    }
	
	    // Verify mock behavior
	    EasyMock.verify(newLogger);
	    
	    component.setLogger(oldLogger);
	    
	    return workerDeploymentID;
	}
	
	
	public DeploymentID createWorkerManagementDeploymentID(String workerPublicKey, WorkerSpecification workerSpec) {
		
		String workerName = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_USERNAME);
		String workerServer = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
		DeploymentID workerID = new DeploymentID(new ContainerID(workerName, workerServer, WorkerConstants.MODULE_NAME), 
				WorkerConstants.LOCAL_WORKER_MANAGEMENT);
		workerID.setPublicKey(workerPublicKey);
		
		return workerID; 
	}

	/**
	 * Creates a WorkerManagement mock and notifies its failure
	 * @param workerDeploymentID The objectID assigned to the WorkerManagement mock
	 */
	public void notifyWorkerFailure(DeploymentID workerDeploymentID, PeerComponent component) {
	
	    WorkerManagement workerMock = EasyMock.createMock(WorkerManagement.class);
	    EasyMock.replay(workerMock);
	    
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		newLogger.info("Worker <" + workerDeploymentID.getServiceID() + "> is now DOWN");
		EasyMock.replay(newLogger);
	
	    WorkerManagementClientReceiver workerMonitor = getWorkerMonitor();
	    workerMonitor.doNotifyFailure(workerMock, workerDeploymentID);
	    component.setStubDown(workerMock);
	
	    EasyMock.verify(workerMock);
	    EasyMock.verify(newLogger);

	    component.setLogger(oldLogger);
	}
	
	
	public void notifyWorkerFailureForTestMessages(PeerComponent component, DeploymentID workerDeploymentID, String message) {
	
		//Mock logger
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
	    WorkerManagement workerMock = EasyMock.createMock(WorkerManagement.class);
	    
	    EasyMock.replay(workerMock);
	    
	    newLogger.error(message + workerDeploymentID.getServiceID());
	    EasyMock.replay(newLogger);
	    
	    // Get peer bound object
	    WorkerManagementClientReceiver workerMonitor = getWorkerMonitor();
	    workerMonitor.doNotifyFailure(workerMock, workerDeploymentID);
	    
	    // Verify mock behavior
	    EasyMock.verify(newLogger);

	    component.setLogger(oldLogger);
	}
	

	
}