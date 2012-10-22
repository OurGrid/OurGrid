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

import static org.easymock.EasyMock.eq;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.matchers.ServiceIDMatcher;
import org.ourgrid.matchers.WorkerSpecMatcher;
import org.ourgrid.peer.PeerComponent;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;


public class Req_112_Util extends PeerAcceptanceUtil {

	public Req_112_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * An unknown remote worker changes its status
	 * @param workerUserName
	 * @param workerServerName
	 * @param workerAPublicKey
	 */
	public void unknownRemoteWorkerStatusChanged(String workerUserName, String workerServerName, 
			String workerAPublicKey, PeerComponent component) {

	    // Create mocks
	    Worker worker = EasyMock.createMock(Worker.class);
	
	    // Record mock behavior
		EasyMock.replay(worker);
	    
	    //Get bound object
	    RemoteWorkerManagementClient workerManagementClient = getRemoteWorkerManagementClientProxy();
	
	    //Change status of worker
		DeploymentID unknownID = new DeploymentID(new ContainerID("unknown", "unknowns", "worker", workerAPublicKey), "worker");
	    
	    AcceptanceTestUtil.publishTestObject(application, unknownID, worker, Worker.class);
		AcceptanceTestUtil.setExecutionContext(component, getRemoteWorkerManagementClientDeployment(), unknownID);
		workerManagementClient.statusChangedAllocatedForBroker(unknownID.getServiceID());
	
	    // Verify mocks behavior
		EasyMock.verify(worker);
	}

	/**
	 * An unknown remote worker changes its status
	 * @param workerSpec
	 * @param workerPublicKey
	 */
	public void unknownRemoteWorkerStatusChanged(WorkerSpecification workerSpec, String workerPublicKey, PeerComponent component) {

		String userName = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_USERNAME);
		String serverName = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
		unknownRemoteWorkerStatusChanged(userName, serverName, workerPublicKey, component);

	}

	/**
	 * Change remote worker status.
	 * @param component The peer component
	 * @param remoteWorkerOID The DeploymentID for the <code>RemoteWorkerManagement</code> 
	 * interface of this worker
	 */
	public void remoteWorkerStatusChanged(PeerComponent component, 
			TestStub remoteWorkerStub) {
		
		remoteWorkerStatusChanged(component, null, remoteWorkerStub, null, null);
	}

	/**
	 * Change remote worker status.
	 * If a consumer ID is provided, expect the worker to be given to this consumer.
	 * @param component The peer component
	 * @param workerSpec The WorkerSpec of this remote worker
	 * @param remoteWorkerOID The DeploymentID for the <code>RemoteWorkerManagement</code> 
	 * interface of this worker
	 * @param lwpcOID The DeploymentID for the <code>LocalWorkerProviderClient</code> 
	 * interface of the consumer
	 * @param requestSpec The request spec of the Request which provided this worker
	 * @return The Worker mock created
	 */
	public TestStub remoteWorkerStatusChanged(PeerComponent component, WorkerSpecification workerSpec, 
			TestStub remoteWorkerStub, DeploymentID lwpcOID, RequestSpecification requestSpec) {

		//Changes temporarily the logger mock
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		
		DeploymentID remoteWorkerOID = remoteWorkerStub.getDeploymentID(); 
		DeploymentID workerID = new DeploymentID(new ContainerID(remoteWorkerOID.getUserName(), remoteWorkerOID.getServerName(), "WORKER", 
				remoteWorkerOID.getPublicKey()), "WORKER");
		
		Worker worker = EasyMock.createMock(Worker.class);
		LocalWorkerProviderClient lwpc = null;
		
		AcceptanceTestUtil.publishTestObject(application, workerID, worker, Worker.class);
		
		// Create mock
		if (workerSpec == null) {
			newLogger.warn("The remote worker " +remoteWorkerOID.getContainerID() + " changed its status to ALLOCATED FOR BROKER, " +
			"but it did not provide a worker reference. This status change was ignored.");
			
			worker = null;
			
		} else {
			
			EasyMock.replay(worker);
			
			if (lwpcOID != null) {
				newLogger.debug("Giving the remote worker [" + 
						remoteWorkerOID.getContainerID() + "] to [" + lwpcOID.getContainerID() + "].");
				
				lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpcOID);
				EasyMock.reset(lwpc);
				
				lwpc.hereIsWorker((ServiceID) ServiceIDMatcher.eqMatcher(workerID.getServiceID()), WorkerSpecMatcher.eqMatcher(workerSpec), eq(requestSpec));
				
				EasyMock.replay(lwpc);
			}
		}

		EasyMock.replay(newLogger);
	    
	    //Get bound object
	    RemoteWorkerManagementClient workerManagementClient = getRemoteWorkerManagementClient();
	    //Change status of worker
	    
	    AcceptanceTestUtil.publishTestObject(application, remoteWorkerOID, remoteWorkerStub.getObject(),
	    		RemoteWorkerManagement.class);
	    
		AcceptanceTestUtil.setExecutionContext(component, getWorkerManagementClientDeployment(), remoteWorkerOID);
		workerManagementClient.statusChangedAllocatedForBroker(worker == null ? null : workerID.getServiceID());
	
		// Verify mocks behavior
		if (workerSpec != null) {
			EasyMock.verify(worker);
			
			if (lwpcOID != null) {
				EasyMock.verify(lwpc);
			}
		}
		EasyMock.verify(newLogger);

		component.setLogger(oldLogger);
		
		return new TestStub(workerID, worker);
	}

}