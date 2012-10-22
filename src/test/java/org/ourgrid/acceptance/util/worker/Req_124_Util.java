package org.ourgrid.acceptance.util.worker;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.broker.communication.actions.HereIsWorkerSpecMessageHandle;
import org.ourgrid.broker.communication.actions.WorkerIsReadyMessageHandle;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.matchers.MessageHandleMatcher;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.worker.WorkerComponent;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_124_Util extends WorkerAcceptanceUtil {
	
	public Req_124_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * This method sets a known Peer for the Worker and excepts a call for updateWorkerSpec, depending
	 * on property of report workerSpec (worker.spec.report in worker.properties file). 
	 * 
	 * @param component WorkerComponet to be set 
	 * @param peerID Peer DeploymentID who is setting
	 * @param reportWorkerSpec true if we want to test the report WorkerSpec feature on Peer, false otherwise.
	 * @param reportTime time for report in seconds
	 * @return Mock for the {@link WorkerSpecListener} in Peer
	 * @throws InterruptedException 
	 */
	public WorkerManagementClient setKnownPeer(WorkerComponent component, DeploymentID peerID,
			boolean reportWorkerSpec, int reportTime, boolean activeTest) throws InterruptedException{
		
		WorkerManagementClient wmc = EasyMock.createMock(WorkerManagementClient.class);
		EasyMock.reset(wmc);
		createStub(wmc, WorkerManagementClient.class, peerID);
		
		EasyMock.reset(wmc);


		if (reportWorkerSpec){
			wmc.updateWorkerSpec((WorkerSpecification) EasyMock.notNull());
		}
		if (activeTest) {
			EasyMock.replay(wmc);
		}
		
		WorkerManagement workerManag = getWorkerManagement();
		ObjectDeployment wmOD = getWorkerManagementDeployment();

		AcceptanceTestUtil.setExecutionContext(component, wmOD, peerID);

		DeploymentID accID = new DeploymentID(new ContainerID("acc", "accServer", DiscoveryServiceConstants.MODULE_NAME, "dsPK"),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		DeploymentID workerSpecListenerID = new DeploymentID(new ContainerID("peerUser", "peerServer",
				PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME, "listenerPk"),
				DiscoveryServiceConstants.DS_OBJECT_NAME);		


//		workerManag.setPeer(wmc);
		
		if (activeTest){
			Thread.sleep((reportTime+1) * 1000);
			EasyMock.verify(wmc);
		}
		
		EasyMock.reset(wmc);
		return wmc;
	}
	
	/**
	 * 
	 * Test if the Client received a WorkerClient.sendMessage(HereIsWorkerSpec message)
	 * 
	 * @param component the WorkerComponent
	 * @param worker the Worker interface
	 * @param brokerID the broker DeploymentID
	 * @param reportWorkerSpec true case this property is activated, false otherwise.
	 * @param reportTime period of time to report WorkerSper
	 * @return Mock for the WorkerClient (the Broker)
	 * @throws InterruptedException
	 */
	public WorkerClient startWorkSuccessfully(WorkerComponent component, Worker worker,	
			DeploymentID brokerID, boolean reportWorkerSpec, int reportTime ) throws InterruptedException {
		
		WorkerClient workerClient = EasyMock.createMock(WorkerClient.class);
		AcceptanceTestUtil.publishTestObject(application, brokerID, workerClient, WorkerClient.class);
		
		FileTransferManager fileTransferManager = EasyMock.createMock(FileTransferManager.class);
		component.setFileTransferManager(fileTransferManager);
		
		workerClient.sendMessage((WorkerIsReadyMessageHandle) (MessageHandleMatcher.eqMatcher(new WorkerIsReadyMessageHandle())));
		
		if(reportWorkerSpec){
			workerClient.sendMessage(EasyMock.isA(HereIsWorkerSpecMessageHandle.class));
		}
		
		EasyMock.replay(workerClient);
		EasyMock.replay(fileTransferManager);
		
		AcceptanceTestUtil.setExecutionContext(component, getWorkerDeployment(), brokerID);
		
		worker.startWork(workerClient, 0, null);
		
		Thread.sleep(4*1000);
		
		EasyMock.verify(workerClient);
		EasyMock.verify(fileTransferManager);
		
		
		return workerClient;
	}
	
}
