package org.ourgrid.discoveryservice.business.requester;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.DeployServiceResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.PeerStatusChangeUtil;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.business.messages.DiscoveryServiceControlMessages;
import org.ourgrid.discoveryservice.communication.receiver.CommunityStatusProviderReceiver;
import org.ourgrid.discoveryservice.communication.receiver.DiscoveryServiceNotificationReceiver;
import org.ourgrid.discoveryservice.communication.receiver.DiscoveryServiceReceiver;
import org.ourgrid.discoveryservice.request.StartDiscoveryServiceRequestTO;

/**
 * Requirement 502
 */
public class StartDiscoveryServiceRequester implements RequesterIF<StartDiscoveryServiceRequestTO>{
	
	private static final String CONF_XML_PATH = "ds-hibernate.cfg.xml";
	

	public List<IResponseTO> execute(StartDiscoveryServiceRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		DiscoveryServiceDAOFactory.getInstance().reset();
		
		HibernateUtil.setUp(CONF_XML_PATH);
		
		createServices(responses);
		
		responses.add(new LoggerResponseTO(DiscoveryServiceControlMessages
				.getSuccessfullyStartedDiscoveryServiceMessage(),
				LoggerResponseTO.INFO));
		
		killAllActivePeers(responses);
		
		registerInterestOnNetwork(responses, request.getNetworkAddresses());
		
		return responses;
	}
	
	private static void createServices(List<IResponseTO> responses) {
		DeployServiceResponseTO to = new DeployServiceResponseTO();
		to.setServiceClass(DiscoveryServiceNotificationReceiver.class);
		to.setServiceName(DiscoveryServiceConstants.DS_MONITOR);
		responses.add(to);
		
		to = new DeployServiceResponseTO();
		to.setServiceClass(DiscoveryServiceReceiver.class);
		to.setServiceName(DiscoveryServiceConstants.DS_OBJECT_NAME);
		responses.add(to);
		
		to = new DeployServiceResponseTO();
		to.setServiceClass(CommunityStatusProviderReceiver.class);
		to.setServiceName(DiscoveryServiceConstants.COMMUNITY_STATUS_PROVIDER);
		responses.add(to);
	}
	
	private static void killAllActivePeers(List<IResponseTO> responses) {
		PeerStatusChangeUtil.killAllActivePeers(
				System.currentTimeMillis(), responses);
	}
	
	private void registerInterestOnNetwork(List<IResponseTO> responses, List<String> networkAddress) {
		
		for (String address : networkAddress) {
			RegisterInterestResponseTO to = new RegisterInterestResponseTO();
			to.setMonitorableAddress(address);
			to.setMonitorableType(DiscoveryService.class);
			to.setMonitorName(DiscoveryServiceConstants.DS_MONITOR);
			
			responses.add(to);
			
			DiscoveryServiceDAO dsDAO = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
			
			dsDAO.addDiscoveryService(new DiscoveryServiceInfo(address), new LinkedHashSet<String>());
		}
		
	}

}
