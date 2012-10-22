package org.ourgrid.acceptance.util.broker;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.communication.receiver.LocalWorkerProviderClientReceiver;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.specification.peer.PeerSpecification;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_327_Util extends BrokerAcceptanceUtil {
	
	private BrokerAcceptanceUtil brokerAcceptanceUtil = new BrokerAcceptanceUtil(context);
	
	public Req_327_Util(ModuleContext context) {
		super(context);
	}

	public TestStub notifyPeerRecovery(PeerSpecification peerSpec, DeploymentID deploymentID,  
			BrokerServerModule component) {
		//Mock logger
		CommuneLogger newLogger = component.getLogger();
		
		EasyMock.reset(newLogger);
		
		ObjectDeployment bcOD = brokerAcceptanceUtil.getBrokerControlDeployment(component);
		
		LocalWorkerProvider lwpMock = EasyMock.createMock(LocalWorkerProvider.class);
		AcceptanceTestUtil.publishTestObject(component, deploymentID, lwpMock, LocalWorkerProvider.class, false);

	    // Get peer bound object
		LocalWorkerProviderClientReceiver peerMonitor = getPeerMonitor(component);
		ObjectDeployment pmOD = getPeerMonitorDeployment(component);
		
		AcceptanceTestUtil.setExecutionContext(component, pmOD, bcOD.getDeploymentID().getPublicKey());
	    
	    LocalWorkerProviderClient localWorkerProviderClient = getLocalWorkerProviderClient(component);
		
		lwpMock.login(localWorkerProviderClient);
		
		newLogger.debug("Peer with deployment id: [" + deploymentID + "] is UP.");
		
	    EasyMock.replay(lwpMock);
	    EasyMock.replay(newLogger);
	    
	    peerMonitor.doNotifyRecovery(lwpMock, deploymentID);
	    EasyMock.verify(lwpMock);
	    EasyMock.verify(newLogger);
	    
	    TestStub testStub = new TestStub(deploymentID, lwpMock);
	    
	    return testStub;
	}
}
