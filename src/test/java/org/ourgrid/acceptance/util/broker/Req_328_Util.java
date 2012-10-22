package org.ourgrid.acceptance.util.broker;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.communication.receiver.LocalWorkerProviderClientReceiver;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.peer.PeerConstants;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_328_Util extends BrokerAcceptanceUtil {
	
	public Req_328_Util(ModuleContext context) {
		super(context);
	}
	
	public DeploymentID createPeerDeploymentID(String peerPublicKey, PeerSpecification peerSpec) {
		String peerName = peerSpec.getAttribute(OurGridSpecificationConstants.ATT_USERNAME);
		String peerServer = peerSpec.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
		
		DeploymentID peerDeploymentID = new DeploymentID(new ContainerID(peerName, peerServer, PeerConstants.MODULE_NAME, peerPublicKey), 
				PeerConstants.LOCAL_WORKER_PROVIDER);
		
		return peerDeploymentID;
	}
	
	/**
	 * @param peerSpec
	 * @param deploymentID
	 * @param peerPublicKey
	 * @param component
	 * @param isAlreadyDown
	 */
	public void notifyPeerFailure(PeerSpecification peerSpec, DeploymentID deploymentID, BrokerServerModule component, 
			boolean isAlreadyDown) {
		//Mock logger
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		ObjectDeployment bcOD = getBrokerControlDeployment(component);
		
		LocalWorkerProvider lwpMock = EasyMock.createMock(LocalWorkerProvider.class);
	    
	    // Get peer bound object
		LocalWorkerProviderClientReceiver peerMonitor = getPeerMonitor(component);
		ObjectDeployment pmOD = getPeerMonitorDeployment(component);
		AcceptanceTestUtil.setExecutionContext(component, pmOD, bcOD.getDeploymentID().getPublicKey());
		
		if (!isAlreadyDown) {
			newLogger.debug("Peer with deployment id: [" + deploymentID + "] is DOWN.");
		} else {
			newLogger.warn("The Peer Entry with deployment id: [" + deploymentID + "] is already down.");
		}
	    
	    EasyMock.replay(lwpMock);
	    EasyMock.replay(newLogger);
	    
	    peerMonitor.doNotifyFailure(lwpMock, deploymentID);
	    
	    EasyMock.verify(lwpMock);
	    EasyMock.verify(newLogger);
	    
	    component.setLogger(oldLogger);
	}
}
