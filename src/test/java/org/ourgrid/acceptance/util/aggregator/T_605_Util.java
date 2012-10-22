package org.ourgrid.acceptance.util.aggregator;

import java.util.ArrayList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.AggregatorAcceptanceUtil;
import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.aggregator.communication.receiver.PeerStatusProviderClientReceiver;
import org.ourgrid.common.interfaces.CommunityStatusProviderClient;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class T_605_Util extends AggregatorAcceptanceUtil {

	private T_604_Util t_604_util = new T_604_Util(context);
	
	public T_605_Util(ModuleContext context) {
		super(context);
	}
	

	public void peerStatusProviderIsUp(AggregatorComponent component, ServiceID serviceID) {
		
		PeerStatusProviderClient peerStatusProviderClient = (PeerStatusProviderClient) 
		component.getObject(AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME).getObject();
		
		t_604_util.peerStatusProviderIsUpFailureNotification(
								peerStatusProviderClient, component, serviceID, true);	
	}
	
	
	public void hereIsCompleteHistoryStatus(AggregatorComponent component,
									boolean withPeerAddress, boolean withProvider) {
		
		PeerStatusProviderClient peerStatusProviderClient = (PeerStatusProviderClient) 
		component.getObject(AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME).getObject();
		ServiceID serviceID;
		
		if(withProvider){		
			serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
					AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);
		}else{
			serviceID = AggegatorUsableAddresses.userAtServerToServiceID("pia@impl");
		}
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		PeerCompleteHistoryStatus peerCompletHistoryStatus = EasyMock.createMock(PeerCompleteHistoryStatus.class);
		
		if (withPeerAddress) {
			if(withProvider){
				newLogger.warn(AggregatorControlMessages.getPeerStatusProviderIsDownMessage());
			}else{
				newLogger.warn(AggregatorControlMessages.getAggregatorPeerStatusProviderIsNullMessage());
			}		
		} else {
			newLogger.info(AggregatorControlMessages.getHereIsCompleteHistoryStatusInfoMessage());
		}
		
		
		EasyMock.replay(newLogger);
		EasyMock.replay(peerCompletHistoryStatus);
		
		
		peerStatusProviderClient.hereIsCompleteHistoryStatus(serviceID, peerCompletHistoryStatus, 0);
		
		EasyMock.verify(newLogger);
		EasyMock.verify(peerCompletHistoryStatus);
		
		EasyMock.reset(newLogger);
		EasyMock.reset(peerCompletHistoryStatus);
	}
	

	public void peerStatusProviderIsDown(AggregatorComponent component, 
			ServiceID serviceID, boolean withPeer) {
		
		PeerStatusProviderClient peerStatusProviderClient = (PeerStatusProviderClient) 
		component.getObject(AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME).getObject();
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		PeerStatusProvider peerStatusProviderMock = EasyMock.createMock(PeerStatusProvider.class);
		
		DeploymentID providerDeploymentId = new DeploymentID(serviceID);

		ObjectDeployment aggregatorOD = new ObjectDeployment(component,
				providerDeploymentId, peerStatusProviderMock);component.setLogger(newLogger);

		component.setLogger(newLogger);		
		
		if(withPeer){
			newLogger.info(AggregatorControlMessages.
					getPeerStatusProviderIsDownInfoMessage((String) EasyMock.anyObject()));
		}else{
			newLogger.warn(AggregatorControlMessages.
					getPeerStatusProviderIsDownMessage());
		}
				
		EasyMock.replay(newLogger);
		EasyMock.replay(peerStatusProviderMock);
		

		AcceptanceTestUtil.publishTestObject(component,
				aggregatorOD.getDeploymentID(), peerStatusProviderMock,
				PeerStatusProvider.class);
		
		AcceptanceTestUtil.setExecutionContext(component, aggregatorOD,
				aggregatorOD.getDeploymentID());
		
		((PeerStatusProviderClientReceiver) peerStatusProviderClient).
								peerStatusProviderIsDown(peerStatusProviderMock, providerDeploymentId);

		EasyMock.verify(peerStatusProviderMock);
		EasyMock.verify(newLogger);
		
		EasyMock.reset(newLogger);
		EasyMock.reset(peerStatusProviderMock);
	}
	
	
	public void hereIsStatusProviderList(AggregatorComponent component,	boolean withDS)
			throws CommuneNetworkException,	ProcessorStartException, InterruptedException {

		CommunityStatusProviderClient communityStatusProviderClient = (CommunityStatusProviderClient) component
				.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME).getObject();
		
		List<String> statusProviders = new ArrayList<String>();
		statusProviders.add(AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);
		statusProviders.add(AggegatorUsableAddresses.PEER_STATUS_PROVIDER_02);	
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		PeerStatusProvider provider = EasyMock.createMock(PeerStatusProvider.class);
		
		ObjectDeployment aggregatorOD = component
		.getObject(AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME);
		
		component.setLogger(newLogger);
		
		if (withDS) {
			newLogger.info(AggregatorControlMessages.getHereIsStatusProviderListInfoMessage());	
			
		} else {
			newLogger.warn(AggregatorControlMessages.
					getCommunityStatusProviderIsDownWarningMessage()); 
		}		
		
		EasyMock.replay(newLogger);
		EasyMock.replay(provider);
		
		AcceptanceTestUtil.publishTestObject(component, aggregatorOD.getDeploymentID(),
				component.getObject(AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME)
						.getObject(), PeerStatusProviderClient.class);
		
		AcceptanceTestUtil
				.setExecutionContext(component, aggregatorOD, aggregatorOD.getDeploymentID());
		
		communityStatusProviderClient.hereIsStatusProviderList(statusProviders);
		
		EasyMock.verify(provider);
		EasyMock.verify(newLogger);		
		EasyMock.reset(newLogger);

	}
	
}
