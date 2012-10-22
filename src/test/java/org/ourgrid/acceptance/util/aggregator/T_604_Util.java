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
import org.ourgrid.common.statistics.beans.status.PeerStatus;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class T_604_Util extends AggregatorAcceptanceUtil {

	public T_604_Util(ModuleContext context) {
		super(context);
	}

	public void communityStatusProviderTestCase(AggregatorComponent component,
			boolean isUpMethod, boolean communityStatusProvideIsDown)
			throws CommuneNetworkException, ProcessorStartException,
			InterruptedException {

		CommunityStatusProviderClient communityStatusProviderClient = (CommunityStatusProviderClient) component
				.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME)
				.getObject();

		CommunityStatusProviderStatusNotification(
				communityStatusProviderClient, component, isUpMethod,
				communityStatusProvideIsDown);
	}

	public void hereIsCompleteHistoryStatus(AggregatorComponent component) {
		PeerStatusProviderClient peerStatusProviderClient = (PeerStatusProviderClient) component
				.getObject(
						AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME)
				.getObject();

		List<DS_PeerStatusChange> statusChanges = new ArrayList<DS_PeerStatusChange>();
		statusChanges.add(null);

		hereIsCompleteHistoryStatusFailureNotification(
				peerStatusProviderClient, component, statusChanges, false);
	}

	public void hereIsCompleteHistoryStatusFailureNotification(
			PeerStatusProviderClient peerStatusProviderClient,
			AggregatorComponent component,
			List<DS_PeerStatusChange> statusChanges, boolean withPeer) {

		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		ServiceID serviceID = EasyMock.createMock(ServiceID.class);
		PeerCompleteHistoryStatus completeStatus = EasyMock
				.createMock(PeerCompleteHistoryStatus.class);

		component.setLogger(newLogger);

		if (withPeer) {
			newLogger.info(AggregatorControlMessages
					.getHereIsCompleteHistoryStatusInfoMessage());
		} else {
			newLogger
					.warn(AggregatorControlMessages
							.getProviderAddressListIsEmptyMessage());
		}
		EasyMock.replay(newLogger);
		EasyMock.replay(serviceID);
		EasyMock.replay(completeStatus);

		peerStatusProviderClient.hereIsCompleteHistoryStatus(serviceID,
				completeStatus, 0);

		EasyMock.verify(serviceID);
		EasyMock.verify(newLogger);

		EasyMock.reset(serviceID);
		EasyMock.reset(newLogger);
		EasyMock.reset(completeStatus);

	}

	public void peerStatusProviderIsUp(AggregatorComponent component,
			ServiceID serviceID, boolean withPeerAddress) {

		PeerStatusProviderClient peerStatusProviderClient = (PeerStatusProviderClient) component
				.getObject(
						AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME)
				.getObject();

		peerStatusProviderIsUpFailureNotification(peerStatusProviderClient,
				component, serviceID, withPeerAddress);
	}

	public void peerStatusProviderIsUpFailureNotification(
			PeerStatusProviderClient peerStatusProviderClient,
			AggregatorComponent component, ServiceID serviceID,
			boolean withPeerAddress) {

		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		PeerStatusProvider peerStatusProviderMock = EasyMock
				.createMock(PeerStatusProvider.class);
		
		
		DeploymentID providerDeploymentId = new DeploymentID(serviceID);

		ObjectDeployment aggregatorOD = new ObjectDeployment(component,
				providerDeploymentId, peerStatusProviderMock);
		
		component.setLogger(newLogger);

		if (withPeerAddress) {
			peerStatusProviderMock.getCompleteHistoryStatus(peerStatusProviderClient, 0);
			
			newLogger.info(AggregatorControlMessages
					.getPeerStatusProviderIsUpInfoMessage());
		} else {
			newLogger
					.warn(AggregatorControlMessages
							.getAggregatorPeerStatusProviderIsNullMessage());
		}

		EasyMock.replay(newLogger);
		EasyMock.replay(peerStatusProviderMock);
		
		AcceptanceTestUtil.publishTestObject(component,
				aggregatorOD.getDeploymentID(), peerStatusProviderMock,
				PeerStatusProvider.class);
		
		AcceptanceTestUtil.setExecutionContext(component, aggregatorOD,
				aggregatorOD.getDeploymentID());
		
		((PeerStatusProviderClientReceiver) peerStatusProviderClient)
				.peerStatusProviderIsUp(peerStatusProviderMock, providerDeploymentId);

		EasyMock.verify(newLogger);
		EasyMock.verify(peerStatusProviderMock);
		
		EasyMock.reset(newLogger);
		EasyMock.reset(peerStatusProviderMock);
	}

	public void peerStatusProviderIsDown(AggregatorComponent component,
			ServiceID serviceId, boolean withPeer) {
		PeerStatusProviderClient peerStatusProviderClient = (PeerStatusProviderClient) component
				.getObject(
						AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME)
				.getObject();

		peerStatusProviderIsDownFailureNotification(peerStatusProviderClient,
				component, serviceId, withPeer);
	}

	private void peerStatusProviderIsDownFailureNotification(
			PeerStatusProviderClient peerStatusProviderClient,
			AggregatorComponent component, ServiceID serviceId, boolean withPeer) {

		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		PeerStatusProvider peerStatusProviderMock = EasyMock
				.createMock(PeerStatusProvider.class);
		DeploymentID providerIdMock = EasyMock.createMock(DeploymentID.class);

		EasyMock.expect(providerIdMock.getServiceID()).andReturn(serviceId);

		component.setLogger(newLogger);

		newLogger.warn(AggregatorControlMessages
				.getAggregatorPeerStatusProviderIsNullMessage());

		EasyMock.replay(newLogger);
		EasyMock.replay(peerStatusProviderMock);
		EasyMock.replay(providerIdMock);

		((PeerStatusProviderClientReceiver) peerStatusProviderClient)
				.peerStatusProviderIsDown(peerStatusProviderMock,
						providerIdMock);

		EasyMock.verify(peerStatusProviderMock);
		EasyMock.verify(newLogger);
		EasyMock.verify(providerIdMock);

		EasyMock.reset(newLogger);
		EasyMock.reset(providerIdMock);
		EasyMock.reset(peerStatusProviderMock);

	}

	public void CommunityStatusProviderStatusNotification(
			CommunityStatusProviderClient communityStatusProviderClient,
			AggregatorComponent component, boolean isUpMethod,
			boolean communityStatusProvideIsDown) {

		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		CommunityStatusProvider commStatusProvider = EasyMock
				.createMock(CommunityStatusProvider.class);
		ObjectDeployment aggregatorOD = component
				.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME);

		component.setLogger(newLogger);

		if (isUpMethod) {
			if (communityStatusProvideIsDown) {
				newLogger.info(AggregatorControlMessages
						.getCommunityStatusProviderIsUpInfoMessage());
			} else {
				newLogger.warn(AggregatorControlMessages
						.getCommunityStatusProviderIsUpWarningMessage());
			}
		} else {
			if (!communityStatusProvideIsDown) {
				newLogger.info(AggregatorControlMessages
						.getCommunityStatusProviderIsDownInfoMessage());
			} else {
				newLogger.warn(AggregatorControlMessages
						.getCommunityStatusProviderIsDownWarningMessage());
			}
		}

		EasyMock.replay(commStatusProvider);
		EasyMock.replay(newLogger);

		AcceptanceTestUtil.publishTestObject(component,
				aggregatorOD.getDeploymentID(), commStatusProvider,
				CommunityStatusProvider.class);
		AcceptanceTestUtil.setExecutionContext(component, aggregatorOD,
				aggregatorOD.getDeploymentID());

		if (isUpMethod) {
			((CommunityStatusProviderClientReceiver) communityStatusProviderClient)
					.doNotifyRecovery(commStatusProvider);
		} else {
			((CommunityStatusProviderClientReceiver) communityStatusProviderClient)
					.doNotifyFailure(commStatusProvider, aggregatorOD.getDeploymentID());
		}

		EasyMock.verify(newLogger);
		EasyMock.verify(commStatusProvider);

		EasyMock.reset(commStatusProvider);
		EasyMock.reset(newLogger);

	}

	public void hereIsPeerStatusChangeHistory(AggregatorComponent component) {
		CommunityStatusProviderClient communityStatusProviderClient = (CommunityStatusProviderClient) component
				.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME)
				.getObject();

		List<DS_PeerStatusChange> statusChanges = new ArrayList<DS_PeerStatusChange>();

		DS_PeerStatusChange DSPeerStatusChangeMock = createDSPeerStatusChangeMock(
				null, AggegatorUsableAddresses.userAtServerToServiceID(
							AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01).toString(), null, 0L, null, PeerStatus.UP);

		statusChanges.add(DSPeerStatusChangeMock);

		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		newLogger.info(AggregatorControlMessages
				.getHereIsPeerStatusChangeHistoryInfoMessage());

		EasyMock.replay(newLogger);

		communityStatusProviderClient.hereIsPeerStatusChangeHistory(
				statusChanges, 0);

		EasyMock.verify(newLogger);
		EasyMock.reset(newLogger);
	}

	private DS_PeerStatusChange createDSPeerStatusChangeMock(Integer id,
			String peerAddress, Long timeOfChange, Long lastModified,
			String version, PeerStatus currentStatus) {

		DS_PeerStatusChange DSPeerStatusChangeMock = EasyMock
				.createMock(DS_PeerStatusChange.class);

		EasyMock.expect(DSPeerStatusChangeMock.getPeerAddress())
				.andReturn(peerAddress).anyTimes();
		EasyMock.expect(DSPeerStatusChangeMock.getCurrentStatus())
				.andReturn(currentStatus).anyTimes();
		EasyMock.expect(DSPeerStatusChangeMock.getLastModified()).andReturn(
				lastModified);
		EasyMock.expect(DSPeerStatusChangeMock.getTimeOfChange()).andReturn(
				timeOfChange);
		EasyMock.expect(DSPeerStatusChangeMock.getVersion()).andReturn(version);

		EasyMock.replay(DSPeerStatusChangeMock);
		return DSPeerStatusChangeMock;

	}

}
