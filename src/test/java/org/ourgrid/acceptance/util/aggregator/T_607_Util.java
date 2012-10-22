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
import org.ourgrid.common.statistics.beans.aggregator.AG_Attribute;
import org.ourgrid.common.statistics.beans.aggregator.AG_Peer;
import org.ourgrid.common.statistics.beans.aggregator.AG_User;
import org.ourgrid.common.statistics.beans.aggregator.AG_Worker;
import org.ourgrid.common.statistics.beans.aggregator.monitor.AG_WorkerStatusChange;
import org.ourgrid.common.statistics.beans.status.PeerStatus;
import org.ourgrid.common.statistics.beans.status.WorkerStatus;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class T_607_Util extends AggregatorAcceptanceUtil {

	public T_607_Util(ModuleContext context) {
		super(context);
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
				providerDeploymentId, peerStatusProviderMock);component.setLogger(newLogger);

		if (withPeerAddress) {
			peerStatusProviderMock.getCompleteHistoryStatus(peerStatusProviderClient, 0);
			newLogger.info(AggregatorControlMessages
					.getPeerStatusProviderIsUpInfoMessage());
		} else {
			newLogger.warn(AggregatorControlMessages
					.getPeerStatusProviderIsUpAlreadyUpMessage());
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

		EasyMock.verify(peerStatusProviderMock);
		EasyMock.verify(newLogger);

		EasyMock.reset(newLogger);
		EasyMock.reset(peerStatusProviderMock);
	}

	public void hereIsCompleteHistoryStatus(AggregatorComponent component) {

		PeerStatusProviderClient peerStatusProviderClient = (PeerStatusProviderClient) component
				.getObject(
						AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME)
				.getObject();

		ServiceID serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);

		hereIsCompleteHistoryStatusNotification(peerStatusProviderClient,
				component, serviceID);
	}

	public void hereIsCompleteHistoryStatusNotification(
			PeerStatusProviderClient peerStatusProviderClient,
			AggregatorComponent component, ServiceID serviceID) {

		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		PeerCompleteHistoryStatus completeStatus = EasyMock
				.createMock(PeerCompleteHistoryStatus.class);
		// Todos esses objetos estao sendo passados como null pq o teste so vai
		// usar, deste AG_Peer, o endereco do peer.
		AG_Peer agPeer0Mock = createAGPeerObjectMock(AggegatorUsableAddresses.
					userAtServerToServiceID(
							AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01).
								toString(),
				null, null, null, null, null, null, null, null, null, null,
				null);

		AG_Peer agPeer01Mock = createAGPeerObjectMock(AggegatorUsableAddresses.
					userAtServerToServiceID(
							AggegatorUsableAddresses.PEER_STATUS_PROVIDER_02).
								toString(),
				null, null, null, null, null, null, null, null, null, null,
				null);

		// ----- Cria os AG_WORKER do AG_Peer ----
		AG_Worker agWorker1Mock = createAGWorkerObjectMock(
				"worker1@test/Case1/WORKER", 0L, 0L, null, agPeer0Mock, null,
				0L, WorkerStatus.DONATED, null);

		AG_Worker agWorker2Mock = createAGWorkerObjectMock(
				"worker2@test/Case2/WORKER", 0L, 0L, null, agPeer01Mock, null,
				0L, WorkerStatus.DONATED, null);

		List<AG_Worker> listAgWorker1 = new ArrayList<AG_Worker>();
		List<AG_Worker> listAgWorker2 = new ArrayList<AG_Worker>();

		listAgWorker1.add(agWorker1Mock);
		listAgWorker2.add(agWorker2Mock);
		// -----------------------------------------

		// ------ Cria os AG_User do AG_Peer. -------
		AG_User agUser1Mock1 = createAGUserObjectMock();
		AG_User agUser1Mock2 = createAGUserObjectMock();

		List<AG_User> listAGUser1 = new ArrayList<AG_User>();
		List<AG_User> listAGUser2 = new ArrayList<AG_User>();

		listAGUser1.add(agUser1Mock1);
		listAGUser2.add(agUser1Mock2);
		// ----------------------------------

		AG_Peer agPeer1Mock = createAGPeerObjectMock(AggegatorUsableAddresses.
					userAtServerToServiceID(
							AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01).
								toString(),
				null, null, null, null, null, null, null, null, listAgWorker1,
				listAGUser1, null);

		AG_Peer agPeer2Mock = createAGPeerObjectMock(AggegatorUsableAddresses.
					userAtServerToServiceID(
							AggegatorUsableAddresses.PEER_STATUS_PROVIDER_02).
								toString(),
				null, null, null, null, null, null, null, null, listAgWorker2,
				listAGUser2, null);

		List<AG_Peer> agPeerList = new ArrayList<AG_Peer>();
		agPeerList.add(agPeer1Mock);
		agPeerList.add(agPeer2Mock);

		AG_WorkerStatusChange agWorkerStatusChangeMock1 = createAGWorkerStatusChange(
				null, agWorker1Mock, null, null, null);
		AG_WorkerStatusChange agWorkerStatusChangeMock2 = createAGWorkerStatusChange(
				null, agWorker2Mock, null, null, null);

		List<AG_WorkerStatusChange> agWorkerStatusChangeList1 = new ArrayList<AG_WorkerStatusChange>();

		agWorkerStatusChangeList1.add(agWorkerStatusChangeMock1);
		agWorkerStatusChangeList1.add(agWorkerStatusChangeMock2);

		EasyMock.expect(completeStatus.getPeerInfo()).andReturn(agPeerList);

		newLogger.info(AggregatorControlMessages
				.getHereIsCompleteHistoryStatusInfoMessage());

		EasyMock.replay(newLogger);
		EasyMock.replay(completeStatus);

		peerStatusProviderClient.hereIsCompleteHistoryStatus(serviceID,
				completeStatus, 0);

		EasyMock.verify(completeStatus);
		EasyMock.verify(newLogger);
		EasyMock.reset(newLogger);
		EasyMock.reset(completeStatus);

	}

	private AG_WorkerStatusChange createAGWorkerStatusChange(
			Integer workerStatusChangeId, AG_Worker worker, Long timeOfChange,
			Long lastModified, WorkerStatus status) {

		AG_WorkerStatusChange agWSC = EasyMock
				.createMock(AG_WorkerStatusChange.class);

		agWSC.setWorker(worker);

		EasyMock.expect(agWSC.getId()).andReturn(workerStatusChangeId);
		EasyMock.expect(agWSC.getWorker()).andReturn(worker);
		EasyMock.expect(agWSC.getTimeOfChange()).andReturn(timeOfChange);
		EasyMock.expect(agWSC.getLastModified()).andReturn(lastModified);
		EasyMock.expect(agWSC.getStatus()).andReturn(status);

		EasyMock.replay(agWSC);
		return agWSC;
	}

	private AG_User createAGUserObjectMock() {
		AG_User agUser = EasyMock.createMock(AG_User.class);

		EasyMock.replay(agUser);
		return agUser;
	}

	private AG_Worker createAGWorkerObjectMock(String agWorkerAddress,
			Long lastModified, Long beginTime, Long endTime, AG_Peer agPeer,
			List<AG_Attribute> attributes, Long id, WorkerStatus status,
			String allocatedFor) {

		AG_Worker agWorker = EasyMock.createMock(AG_Worker.class);

		agWorker.setStatus(status);
		agWorker.setEndTime(endTime);
		agWorker.setPeer((AG_Peer) EasyMock.anyObject());

		EasyMock.expect(agWorker.getAddress()).andReturn(agWorkerAddress);

		EasyMock.replay(agWorker);
		return agWorker;
	}

	@SuppressWarnings("unchecked")
	private AG_Peer createAGPeerObjectMock(String peerAddress, String label,
			String description, String email, String latitude,
			String longitude, String version, String timezone,
			Long lastModified, List<AG_Worker> workers, List<AG_User> users,
			PeerStatus status) {

		AG_Peer agPeer = EasyMock.createMock(AG_Peer.class);

		agPeer.setWorkers((List<AG_Worker>) EasyMock.anyObject());

		EasyMock.expect(agPeer.getAddress()).andReturn(peerAddress).anyTimes();
		EasyMock.expect(agPeer.getLabel()).andReturn(label);
		EasyMock.expect(agPeer.getDescription()).andReturn(description);
		EasyMock.expect(agPeer.getEmail()).andReturn(email);
		EasyMock.expect(agPeer.getLatitude()).andReturn(latitude);
		EasyMock.expect(agPeer.getLongitude()).andReturn(longitude);
		EasyMock.expect(agPeer.getVersion()).andReturn(version);
		EasyMock.expect(agPeer.getTimezone()).andReturn(timezone);
		EasyMock.expect(agPeer.getLastModified()).andReturn(lastModified);
		EasyMock.expect(agPeer.getWorkers()).andReturn(workers).anyTimes();
		EasyMock.expect(agPeer.getUsers()).andReturn(users);
		EasyMock.expect(agPeer.getStatus()).andReturn(status);

		EasyMock.replay(agPeer);
		return agPeer;
	}

	public void hereIsStatusProviderList(AggregatorComponent component,
			boolean withDS) throws CommuneNetworkException,
			ProcessorStartException, InterruptedException {

		CommunityStatusProviderClient communityStatusProviderClient = (CommunityStatusProviderClient) component
				.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME)
				.getObject();
		List<String> statusProviders = new ArrayList<String>();
		statusProviders.add(AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);
		hereIsStatusProviderListStatusNotification(
				communityStatusProviderClient, component, statusProviders,
				withDS);

	}

	private void hereIsStatusProviderListStatusNotification(
			CommunityStatusProviderClient communityStatusProviderClient,
			AggregatorComponent component, List<String> statusProviders,
			boolean withDS) {

		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		PeerStatusProvider peerStatusProviderMock = EasyMock
				.createMock(PeerStatusProvider.class);

		component.setLogger(newLogger);
		ServiceID serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);

		component.createTestStub(peerStatusProviderMock,
				PeerStatusProvider.class, new DeploymentID(serviceID), true);

		ObjectDeployment aggregatorOD = new ObjectDeployment(component,
				new DeploymentID(serviceID), peerStatusProviderMock);

		peerStatusProviderMock.getCompleteHistoryStatus(
				(PeerStatusProviderClient) EasyMock.anyObject(),
				EasyMock.anyLong());

		if (withDS) {
			newLogger.info(AggregatorControlMessages
					.getHereIsStatusProviderListInfoMessage());

		} else {
			newLogger.warn(AggregatorControlMessages
					.getCommunityStatusProviderIsDownWarningMessage());
		}

		EasyMock.replay(newLogger);
		EasyMock.replay(peerStatusProviderMock);

		AcceptanceTestUtil.publishTestObject(component,
				aggregatorOD.getDeploymentID(), peerStatusProviderMock,
				PeerStatusProvider.class);
		AcceptanceTestUtil.setExecutionContext(component, aggregatorOD,
				aggregatorOD.getDeploymentID());

		communityStatusProviderClient.hereIsStatusProviderList(statusProviders);

		EasyMock.verify(newLogger);
		EasyMock.reset(newLogger);

	}

}
