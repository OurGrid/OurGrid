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
package org.ourgrid.acceptance.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.ourgrid.acceptance.util.broker.TestJob;
import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.communication.operations.GetOperation;
import org.ourgrid.broker.communication.operations.InitOperation;
import org.ourgrid.broker.communication.operations.Operation;
import org.ourgrid.broker.communication.receiver.LocalWorkerProviderClientReceiver;
import org.ourgrid.broker.util.RandomRequestIDGenerator;
import org.ourgrid.broker.util.RequestIDGenerator;
import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.control.BrokerControl;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.job.GridProcess;
import org.ourgrid.common.job.Task;
import org.ourgrid.common.specification.job.JobSpecification;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;

public class BrokerAcceptanceUtil extends AcceptanceUtil{
	
	private RequestIDGenerator requestIDGenerator;

	public BrokerAcceptanceUtil(ModuleContext context) {
		super(context);
		this.requestIDGenerator = new RandomRequestIDGenerator();
	}
	
	@Before
    public static void setUp() throws Exception {
        System.setProperty("OGROOT", ".");
        Configuration.getInstance(BrokerConfiguration.BROKER);
    }
	
	@After
    public static void tearDown() throws Exception {
		if (application != null && !application.getContainerDAO().isStopped()) {
			application.stop();
		}
    }

    public BrokerControl getBrokerControl(BrokerServerModule component) {
        ObjectDeployment deployment = getBrokerControlDeployment(component);
		return (BrokerControl) deployment.getObject();
    }

    public ObjectDeployment getBrokerControlDeployment(BrokerServerModule component) {
    	return component.getObject(Module.CONTROL_OBJECT_NAME);
    }
	
    public LocalWorkerProviderClient getLocalWorkerProviderClient(BrokerServerModule component) {
        ObjectDeployment deployment = getLocalWorkerProviderClientDeployment(component);
        
        
        if (deployment == null) {
        	return null;
        }
        return (LocalWorkerProviderClient) deployment.getObject();
    }

    public ObjectDeployment getLocalWorkerProviderClientDeployment(BrokerServerModule component) {
    	return component.getObject(BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);
    }
	
    public LocalWorkerProviderClientReceiver getPeerMonitor(BrokerServerModule component) {
        ObjectDeployment deployment = getPeerMonitorDeployment(component);
		return (LocalWorkerProviderClientReceiver) deployment.getObject();
    }

	public ObjectDeployment getPeerMonitorDeployment(BrokerServerModule component) {
		return component.getObject(BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);
	}
	
    public LocalWorkerProviderClientReceiver getWorkerMonitor(BrokerServerModule component) {
        ObjectDeployment deployment = getWorkerMonitorDeployment(component);
		return (LocalWorkerProviderClientReceiver) deployment.getObject();
    }

	public ObjectDeployment getWorkerMonitorDeployment(BrokerServerModule component) {
		return component.getObject(BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);
	}
	
    public WorkerClient getWorkerClient(BrokerServerModule component) {
        ObjectDeployment deployment = getWorkerClientDeployment(component);
		return (WorkerClient) deployment.getObject();
    }

    public ObjectDeployment getWorkerClientDeployment(BrokerServerModule component) {
    	return component.getObject(BrokerConstants.WORKER_CLIENT);
    }
    
	public Map<String, Object> createBrokerProperties() {
		
		Map<String, Object> properties = new LinkedHashMap<String, Object>();
        //properties.put(ContainerContext.PROP_PUBLIC_KEY, context.getProperty(JICContext.PROP_PUBLIC_KEY));
        
        return properties;
	}
	
	
	public OutgoingTransferHandle getOutgoingTransferHandle(TestJob testJob, String fileName) {
		Collection<Task> tasks = testJob.getJob().getTasks();
		for (Task task : tasks) {
			Collection<GridProcess> gridProcesses = task.getGridProcesses();
			
			for (GridProcess gridProcess : gridProcesses) {
				List<Operation> operations = gridProcess.getOperations().getInitPhaseOperationsList();
				
				for (Operation o : operations) {
					InitOperation initOperation = (InitOperation) o;
					if (fileName.equals(initOperation.getRemoteFilePath())) { 
						
						OutgoingHandle handle = (OutgoingHandle) initOperation.getHandle();
						
						OutgoingTransferHandle outgoing = new OutgoingTransferHandle(handle.getId(), 
								handle.getLogicalFileName(), handle.getLocalFile(), handle.getDescription(), 
								new DeploymentID(handle.getDestinationID()));
						return outgoing;
					}	
				}
			}
		}
		
		return null;
	}
	
	public IncomingTransferHandle getIncomingTransferHandle(TestJob testJob, String fileName) {
		Collection<Task> tasks = testJob.getJob().getTasks();
		for (Task task : tasks) {
			Collection<GridProcess> gridProcesses = task.getGridProcesses();
			
			for (GridProcess gridProcess : gridProcesses) {
				List<GetOperation> operations = gridProcess.getOperations().getFinalPhaseOperationsList();
				
				for (GetOperation o : operations) {
					if (fileName.equals(o.getRemoteFilePath())) {
						
						IncomingHandle handleIn = (IncomingHandle) o.getHandle();
						ContainerID idIn = ContainerID.parse(handleIn.getSenderContainerID());
						
						IncomingTransferHandle incoming = new IncomingTransferHandle(handleIn.getId(),
								handleIn.getLogicalFileName(), handleIn.getDescription(), 
								handleIn.getFileSize(), idIn.getContainerID());
						return incoming;
					}	
				}
			}
		}
		
		return null;
	}
	
	protected RequestSpecification createRequestSpec(int jobId, JobSpecification jobSpec, int maxReplicas, int maxFails) {
		String requirements = jobSpec.getRequirements();
		int numberOfTasks = jobSpec.getTaskSpecs().size();
		int numberOfWorkers = numberOfTasks * maxReplicas;
		
		long requestID = this.requestIDGenerator.nextRequestID();
		
		return new RequestSpecification(jobId, jobSpec, requestID, requirements, numberOfWorkers, maxFails, maxReplicas);
	}
	
}