package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.interfaces.status.CommunityInfo;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.to.DiscoveryServiceState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.control.AccountingControl;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.dao.ConsumerDAO;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.DiscoveryServiceClientDAO;
import org.ourgrid.peer.request.GetCompleteStatusRequestTO;
import org.ourgrid.peer.response.HereIsCompleteStatusResponseTO;
import org.ourgrid.peer.status.PeerCompleteStatus;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.RemoteConsumer;
import org.ourgrid.reqtrace.Req;

public class GetCompleteStatusRequester extends AbstractGetStatusRequester<GetCompleteStatusRequestTO> {


	public List<IResponseTO> execute(GetCompleteStatusRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
	
		if (request.canStatusBeUsed()) {
			HereIsCompleteStatusResponseTO to = new HereIsCompleteStatusResponseTO();
			to.setClientAddress(request.getClientAddress());
			to.setPeerAddress(request.getPeerAddress());
			to.setPeerCompleteStatus(getCompleteStatus(responses, request));
			
			responses.add(to);
		}
		
		return responses;
	}
	
	@Req("REQ038a")
	protected PeerCompleteStatus getCompleteStatus(List<IResponseTO> responses, GetCompleteStatusRequestTO request) {
		PeerCompleteStatus completeStatus = new PeerCompleteStatus( 
				getLocalWorkersInfo(responses, StringUtil.addressToUserAtServer(request.getPeerAddress())), 
				getRemoteWorkersInfo(),
				getLocalConsumersInfo(), 
				getRemoteConsumersInfo(), 
				getUsersInfo(responses), 
				getNetworkOfFavorsStatus(responses, request), 
				getCommunityInfo(), 
				request.getUpTime(), 
				request.getLabel(),
				Configuration.VERSION.toString(), 
				getDescription(request.getContextString(), request.getPropConfDir(), request.getPropLabel(), request.getPropJoinCommunity(),
						request.isJoinCommunityEnabled()));
		
		return completeStatus;
	}
	
	@Req("REQ038a")
	private List<ConsumerInfo> getRemoteConsumersInfo() {
		
		ConsumerDAO consumerDAO = PeerDAOFactory.getInstance().getConsumerDAO();
		List<RemoteConsumer> remoteConsumers = consumerDAO.getRemoteConsumers();
		
		List<ConsumerInfo> remoteConsumersInfo = new ArrayList<ConsumerInfo>();
		
		for (RemoteConsumer remoteConsumer : remoteConsumers) {
			
			if (remoteConsumer.getConsumerAddress() == null) {
				continue;
			}
			
			int noOfLocalWorkers = 0;
			
			for (AllocableWorker allWorker : remoteConsumer.getAllocableWorkers()) {
				if (allWorker.isWorkerLocal()) {
					noOfLocalWorkers++;
				} 
			}
			
			ConsumerInfo consumerInfo = new ConsumerInfo(
					noOfLocalWorkers,
					StringUtil.addressToUserAtServer(remoteConsumer.getConsumerAddress()));
			
			remoteConsumersInfo.add(consumerInfo);
		}
		
		return remoteConsumersInfo;
	}
	
	@Req("REQ038a")
	private NetworkOfFavorsStatus getNetworkOfFavorsStatus(List<IResponseTO> responses, GetCompleteStatusRequestTO request) {
		return new NetworkOfFavorsStatus(AccountingControl.getInstance().getBalances(responses, request.getMyCertSubjectDN()));
	}
	
	private CommunityInfo getCommunityInfo() {
		
		String dsAddress = null;
		DiscoveryServiceClientDAO dsDao = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
		dsAddress = dsDao.getDsAddresses().toString();
		
		List<String> connectedPeers = new ArrayList<String>();
		for (String peerAddress : dsDao.getRemoteWorkerProvidersAddress()) {
			connectedPeers.add(StringUtil.addressToUserAtServer(peerAddress));
		}
		
		return new CommunityInfo(dsAddress, 
				dsDao.isConnected() ? DiscoveryServiceState.UP : DiscoveryServiceState.CONTACTING,
				connectedPeers);
	}
}
