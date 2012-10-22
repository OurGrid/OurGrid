package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.communication.receiver.LocalWorkerProviderClientReceiver;
import org.ourgrid.broker.response.StartWorkResponseTO;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class StartWorkSender implements SenderIF<StartWorkResponseTO> {

	public void execute(StartWorkResponseTO response, ServiceManager manager) {
		
		Worker worker = (Worker) manager.getStub(ServiceID.parse(response.getWorkerAddress()), Worker.class);
		
		worker.startWork(getWorkerClient(manager), response.getRequestID(), 
				new GridProcessHandle(response.getJobID(), response.getTaskID(), response.getProcessID()));
	}
	
	private WorkerClient getWorkerClient(ServiceManager manager) {
		WorkerClient workerClient = 
			(WorkerClient) manager.getObjectDeployment(BrokerConstants.WORKER_CLIENT).getObject();
		
		if (workerClient == null) {
			manager.deploy(BrokerConstants.WORKER_CLIENT, LocalWorkerProviderClientReceiver.class);
			workerClient = (WorkerClient) manager.getObjectDeployment(BrokerConstants.WORKER_CLIENT).getObject();
		}
		
		return (WorkerClient) workerClient;
	}
}
