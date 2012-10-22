package org.ourgrid.acceptance.util.broker;

import java.util.HashMap;
import java.util.Map;

import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.peer.PeerConstants;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class Req_309_Util extends BrokerAcceptanceUtil {

	public Req_309_Util(ModuleContext context) {
		super(context);
	}
	
	public ServiceID createServiceID(PeerSpecification peer, String publicKey, String serviceName) {
		String userName = peer.getAttribute(OurGridSpecificationConstants.ATT_USERNAME);
		String serverName = peer.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
		
		return new ServiceID(new ContainerID(userName, serverName, PeerConstants.MODULE_NAME, publicKey), serviceName);
	}
	
	public DeploymentID createPeerDeploymentID(String peerPublicKey, PeerSpecification peerSpec) {
		String peerName = peerSpec.getAttribute(OurGridSpecificationConstants.ATT_USERNAME);
		String peerServer = peerSpec.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
		
		DeploymentID peerDeploymentID = new DeploymentID(new ContainerID(peerName, peerServer, PeerConstants.MODULE_NAME, peerPublicKey), 
				PeerConstants.LOCAL_WORKER_PROVIDER);
		
		return peerDeploymentID;
	}

	public PeerSpecification createPeerSpec(String userName, String serverName) {
		Map<String,String> attributes = new HashMap<String, String>();
		attributes.put(OurGridSpecificationConstants.ATT_USERNAME, userName);
		attributes.put(OurGridSpecificationConstants.ATT_SERVERNAME, serverName);
		PeerSpecification peerSpec = new PeerSpecification(attributes);
		return peerSpec;	
	}
	
}
