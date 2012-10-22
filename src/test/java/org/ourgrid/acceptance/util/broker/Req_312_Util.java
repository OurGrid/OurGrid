package org.ourgrid.acceptance.util.broker;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_312_Util extends BrokerAcceptanceUtil {
	
	private BrokerAcceptanceUtil brokerAcceptanceUtil = new BrokerAcceptanceUtil(context);

	public Req_312_Util(ModuleContext context) {
		super(context);
	}
	
	public TestStub receiveWorker(BrokerServerModule component, String workerPublicKey, boolean hasPeers,
			                  boolean isLogged, boolean isPeerUp, boolean hasJobs, WorkerSpecification workerSpec, 
			                  String peerPublicKey, TestStub peerStub, TestJob testJob) {
		
		CommuneLogger newLogger = component.getLogger();
		
		LocalWorkerProviderClient localProviderClient = brokerAcceptanceUtil.getLocalWorkerProviderClient(component);
		
		Worker workerMock = EasyMock.createMock(Worker.class);
		DeploymentID workerDP = createWorkerDeploymentID(workerPublicKey, 
				workerSpec.getAttribute(OurGridSpecificationConstants.ATT_USERNAME), workerSpec.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		component.createTestStub(workerMock, Worker.class, workerDP, false);
		
		EasyMock.reset(newLogger);
		
		if (hasPeers) {
			if (isLogged) {
				if (!hasJobs) {
					newLogger.warn("Request is null. Disposing worker with public key: [" + workerPublicKey +
							"] to  peer with public key: [" + peerPublicKey + "].");
				}
				
			} else {
				if (isPeerUp) {
					newLogger.warn("The broker is not logged in the peer with public key [" + 
							peerPublicKey + "]. This worker with public key [" +
							workerPublicKey + "] delivery was ignored.");
				} else {
					newLogger.warn("The peer with public key [" + peerPublicKey + "], which is down, " +
							"delivered a worker with public key: [" + workerPublicKey + "].");
				}
			}
		} else if (localProviderClient != null){
			newLogger.warn("An unknown peer delivered a worker with public key: [" + workerPublicKey + 
					"], which was ignored. Peer public key: [" + peerPublicKey + "].");
		}

		EasyMock.replay(workerMock);
		EasyMock.replay(newLogger);

		peerStub.getDeploymentID().setPublicKey(peerPublicKey);
		AcceptanceTestUtil.setExecutionContext(component, getLocalWorkerProviderClientDeployment(component), peerStub.getDeploymentID());
		
		if(!hasJobs){
			LocalWorkerProvider lwp = (LocalWorkerProvider)peerStub.getObject();
			EasyMock.reset(lwp);
			lwp.disposeWorker(workerDP.getServiceID());
			EasyMock.replay(lwp);
		}
		
		RequestSpecification requestSpec = null;
		if(testJob != null){
			requestSpec = testJob.getRequestByPeer(application, (LocalWorkerProvider) peerStub.getObject());
		}
		
		if (localProviderClient != null) {
			localProviderClient.hereIsWorker(workerDP.getServiceID(), workerSpec, requestSpec);
			//localProviderClient.hereIsWorker(workerMock, workerSpec, requestSpec);
		}	
		
		if(!hasJobs){
			EasyMock.verify((LocalWorkerProvider) peerStub.getObject());
		}
		EasyMock.verify(workerMock);
		EasyMock.verify(newLogger);

		return new TestStub(workerDP, workerMock);
	}
	
	public DeploymentID createWorkerDeploymentID(String workerPublicKey, String user, String server) {
		
		if (user == null) {
			user = "a";
		}
		
		if (server == null) {
			server = "a";
		}
		
		DeploymentID workerDeploymentID = new DeploymentID(new ContainerID(user, server, WorkerConstants.MODULE_NAME, workerPublicKey), 
				WorkerConstants.WORKER);
		
		return workerDeploymentID;
	}
}
