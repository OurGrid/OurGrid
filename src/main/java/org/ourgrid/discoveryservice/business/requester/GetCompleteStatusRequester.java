package org.ourgrid.discoveryservice.business.requester;

import static org.ourgrid.common.interfaces.Constants.LINE_SEPARATOR;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.request.GetCompleteStatusRequestTO;
import org.ourgrid.discoveryservice.response.HereIsCompleteStatusResponseTO;
import org.ourgrid.discoveryservice.status.DiscoveryServiceCompleteStatus;

public class GetCompleteStatusRequester implements RequesterIF<GetCompleteStatusRequestTO>{
	
	public List<IResponseTO> execute(GetCompleteStatusRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (!request.canStatusBeUsed()) {
			responses.add(new LoggerResponseTO("Received a status request from: " + request.getClientAddress() +
					", but the component is not started.", LoggerResponseTO.WARN));
			return responses;
		}

		DiscoveryServiceCompleteStatus completeStatus = getCompleteStatus(request);
		
		HereIsCompleteStatusResponseTO to = new HereIsCompleteStatusResponseTO();
		to.setClientAddress(request.getClientAddress());
		to.setDiscoveryServiceCompleteStatus(completeStatus);
		
		responses.add(to);
		
		return responses;
	}
	
	private DiscoveryServiceCompleteStatus getCompleteStatus(GetCompleteStatusRequestTO request) {
		return new DiscoveryServiceCompleteStatus( 
				getCompleteNetwork(request), 
				DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO().getAllOnlinePeers(),
				request.getUpTime(),
				getDescription(request.getPropConfDir(), request.getContextString()),
				request.getMyAddress());
	}
	
	private Map<DiscoveryServiceInfo, Set<String>> getCompleteNetwork(GetCompleteStatusRequestTO request) {
		DiscoveryServiceDAO discoveryServiceDAO = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		Map<DiscoveryServiceInfo, Set<String>> network = CommonUtils.createSerializableMap();
		network.putAll(discoveryServiceDAO.getNetwork());
		network.put(new DiscoveryServiceInfo(request.getMyAddress(), true), new LinkedHashSet<String>(discoveryServiceDAO.getMyOnlinePeers()));
		
		return network;
	}
	
	private String getDescription(String propConfDir, String contextString) {
		
		StringBuilder conf = new StringBuilder();

		conf.append( "\tVersion: " ).append( Configuration.VERSION ).append( LINE_SEPARATOR );

		conf.append( "\tConfiguration directory: " );
		conf.append( /*containerContext.getProperty(ModuleProperties.PROP_CONFDIR)*/propConfDir );
		conf.append( LINE_SEPARATOR );
		
		conf.append( /*containerContext.toString()*/contextString );

		return conf.toString();
	}
}
