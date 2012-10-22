package org.ourgrid.acceptance.util.aggregator;

import java.util.ArrayList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.AggregatorAcceptanceUtil;
import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.aggregator.communication.receiver.CommunityStatusProviderClientReceiver;
import org.ourgrid.aggregator.communication.receiver.PeerStatusProviderClientReceiver;
import org.ourgrid.common.interfaces.CommunityStatusProvider;
import org.ourgrid.common.interfaces.CommunityStatusProviderClient;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class T_603_Util extends AggregatorAcceptanceUtil {
	
	private T_602_Util t_602_Util = new T_602_Util(context);

	public T_603_Util(ModuleContext context) {
		super(context);
	}
	
	public void hereIsStatusProviderList(AggregatorComponent component, boolean withDS) throws 
			CommuneNetworkException, ProcessorStartException, InterruptedException {

		CommunityStatusProviderClient communityStatusProviderClient = (CommunityStatusProviderClient) 
		component.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME).getObject();
		List<String> statusProviders = new ArrayList<String>();
		statusProviders.add(AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);
		statusProviders.add(AggegatorUsableAddresses.PEER_STATUS_PROVIDER_02);		
		hereIsStatusProviderListStatusNotification(communityStatusProviderClient, component, statusProviders, withDS);
	
	}
		
	public AggregatorComponent startAggregator() throws CommuneNetworkException, 
								ProcessorStartException, InterruptedException {
		return t_602_Util.startAggregator();
	}


	public void startAggregatorAgain(AggregatorComponent component) throws
						CommuneNetworkException, ProcessorStartException {
		t_602_Util.startAggregatorAgain(component);		
	}


	public void stopAggregatorAfterStart(AggregatorComponent component) throws 
				CommuneNetworkException, ProcessorStartException, InterruptedException {
		t_602_Util.stopAggregatorAfterStart(component);
		
	}


	public void CommunityStatusProviderIsDownWarning(AggregatorComponent component)
			throws CommuneNetworkException,	ProcessorStartException, InterruptedException {
		t_602_Util.CommunityStatusProviderIsDownWarning(component);
		
	}
	
	public void CommunityStatusProviderIsDownWarningAgain(
			AggregatorComponent component) throws CommuneNetworkException, 
							ProcessorStartException, InterruptedException {
		t_602_Util.CommunityStatusProviderIsDownWarningAgain(component);
		
	}
	
	public void communityStatusProviderIsUpSucessfull(AggregatorComponent component) {
		
		CommunityStatusProviderClient communityStatusProviderClient = (CommunityStatusProviderClient) 
				component.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME).getObject();
		
		CommunityStatusProviderIsUpSucessfullInfo(communityStatusProviderClient, component, true);
		
	}
	
	public void communityStatusProviderIsUpSucessfullWarning(AggregatorComponent component) {
		
		CommunityStatusProviderClient communityStatusProviderClient = (CommunityStatusProviderClient) 
				component.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME).getObject();
		
		CommunityStatusProviderIsUpSucessfullInfo(communityStatusProviderClient, component, false);
		
	}
	
	public void CommunityStatusProviderIsUpSucessfullInfo(
			CommunityStatusProviderClient communityStatusProviderClient,
			AggregatorComponent component, boolean communityIsDown) {

		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		CommunityStatusProvider commStatusProvider = EasyMock.createMock(CommunityStatusProvider.class);
		
		ObjectDeployment aggregatorOD = component.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME);
		component.setLogger(newLogger);

		if (communityIsDown) {
			newLogger.info(AggregatorControlMessages.getCommunityStatusProviderIsUpInfoMessage());
		} else {
			newLogger.warn(AggregatorControlMessages.getCommunityStatusProviderIsUpWarningMessage());
		}

		EasyMock.replay(newLogger);
		EasyMock.replay(commStatusProvider);

		AcceptanceTestUtil.publishTestObject(component,	aggregatorOD.getDeploymentID(), commStatusProvider,
				CommunityStatusProviderClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, aggregatorOD,	aggregatorOD.getDeploymentID());

		((CommunityStatusProviderClientReceiver) communityStatusProviderClient).doNotifyRecovery(commStatusProvider);

		EasyMock.verify(newLogger);
		EasyMock.verify(commStatusProvider);

		EasyMock.reset(newLogger);
		EasyMock.reset(commStatusProvider);

	}

	private void hereIsStatusProviderListStatusNotification(CommunityStatusProviderClient communityStatusProviderClient,
			AggregatorComponent component, List<String> statusProviders, boolean withDS) {
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		if (withDS) {
			newLogger.info(AggregatorControlMessages.getHereIsStatusProviderListInfoMessage());	
			
		} else {
			newLogger.warn(AggregatorControlMessages.
					getCommunityStatusProviderIsDownWarningMessage()); 
		}		
		
		EasyMock.replay(newLogger);
	
		communityStatusProviderClient.hereIsStatusProviderList(statusProviders);
		
		EasyMock.verify(newLogger);		
		EasyMock.reset(newLogger);
		
		
	}
	
	private void hereIsPeerStatusChangeHistoryStatusNotification(
			CommunityStatusProviderClient communityStatusProviderClient, AggregatorComponent component,
			List<DS_PeerStatusChange> statusChanges, boolean communityStatusIsUp) {
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		if (communityStatusIsUp) {
			newLogger.info(AggregatorControlMessages.
					getHereIsPeerStatusChangeHistoryInfoMessage());
		} else {
			newLogger.warn(AggregatorControlMessages.getCommunityStatusProviderIsDownWarningMessage());
		}		
		
		EasyMock.replay(newLogger);
		
		communityStatusProviderClient.hereIsPeerStatusChangeHistory(statusChanges, 0);
		
		EasyMock.verify(newLogger);
		EasyMock.reset(newLogger);
		
		
	}
	
	public void hereIsPeerStatusChangeHistoryCommunityIsUp(AggregatorComponent component) {
		
		CommunityStatusProviderClient communityStatusProviderClient = (CommunityStatusProviderClient) 
		component.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME).getObject();
		
		List<DS_PeerStatusChange> statusChanges = new ArrayList<DS_PeerStatusChange>();
		statusChanges.add(null);
		
		hereIsPeerStatusChangeHistoryStatusNotification(communityStatusProviderClient, component, statusChanges, true);
	}

	
	public void hereIsPeerStatusChangeHistory(AggregatorComponent component) {
		
		CommunityStatusProviderClient communityStatusProviderClient = (CommunityStatusProviderClient) 
		component.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME).getObject();
		
		List<DS_PeerStatusChange> statusChanges = new ArrayList<DS_PeerStatusChange>();
		statusChanges.add(null);
		
		hereIsPeerStatusChangeHistoryStatusNotification(communityStatusProviderClient,
														component, statusChanges, false);
	}

	public void hereIsCompleteHistoryStatus(AggregatorComponent component) {
		PeerStatusProviderClient peerStatusProviderClient = (PeerStatusProviderClient) 
		component.getObject(AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME).getObject();
				
		List<DS_PeerStatusChange> statusChanges = new ArrayList<DS_PeerStatusChange>();
		statusChanges.add(null);
		
		hereIsCompleteHistoryStatusFailureNotification(peerStatusProviderClient, component, statusChanges);
	
	}

	public void hereIsCompleteHistoryStatusFailureNotification(PeerStatusProviderClient peerStatusProviderClient,
			AggregatorComponent component, List<DS_PeerStatusChange> statusChanges) {
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		ServiceID serviceID = EasyMock.createMock(ServiceID.class);
		PeerCompleteHistoryStatus completeStatus = EasyMock.createMock(PeerCompleteHistoryStatus.class);
		
		serviceID.toString();		
		
		newLogger.warn(AggregatorControlMessages.getCommunityStatusProviderIsDownWarningMessage());		
		
		EasyMock.replay(newLogger);
		EasyMock.replay(serviceID);
		
		peerStatusProviderClient.hereIsCompleteHistoryStatus(serviceID, completeStatus, 0);
		
		EasyMock.verify(serviceID);
		EasyMock.verify(newLogger);
		
		EasyMock.reset(serviceID);
		EasyMock.reset(newLogger);
		
	}
	
	public void peerStatusProviderStatusNotification(AggregatorComponent component, boolean peerStatusProviderIsUp) {
		PeerStatusProviderClient peerStatuProviderClient = (PeerStatusProviderClient) 
		component.getObject(AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME).getObject();
		
		peerStatusProviderStatusNotification(peerStatuProviderClient, component, peerStatusProviderIsUp);
		
	}

	private void peerStatusProviderStatusNotification(PeerStatusProviderClient peerStatuProviderClient,
			AggregatorComponent component, boolean peerStatusProviderIsUp) {
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		PeerStatusProvider peerStatusProviderMock = EasyMock.createMock(PeerStatusProvider.class);
		DeploymentID providerIdMock = EasyMock.createMock(DeploymentID.class);
		ServiceID serviceIdMock = EasyMock.createMock(ServiceID.class);		
		
		EasyMock.expect(providerIdMock.getServiceID()).andReturn(serviceIdMock);
		serviceIdMock.toString();
		
		component.setLogger(newLogger);
		
		if (peerStatusProviderIsUp) {
			newLogger.warn(AggregatorControlMessages.
					getCommunityStatusProviderIsDownWarningMessage());
		} else {
			newLogger.warn(AggregatorControlMessages.
					getCommunityStatusProviderIsDownWarningMessage());
		}
		
		EasyMock.replay(newLogger);
		EasyMock.replay(peerStatusProviderMock);
		EasyMock.replay(providerIdMock);
		EasyMock.replay(serviceIdMock);
		
		((PeerStatusProviderClientReceiver) peerStatuProviderClient).peerStatusProviderIsUp(peerStatusProviderMock, providerIdMock);

		
		EasyMock.verify(peerStatusProviderMock);
		EasyMock.verify(newLogger);
		EasyMock.verify(providerIdMock);
		EasyMock.verify(serviceIdMock);
		
		EasyMock.reset(newLogger);
		EasyMock.reset(serviceIdMock);
		EasyMock.reset(providerIdMock);		
		
		
	}

}

