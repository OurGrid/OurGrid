package org.ourgrid.acceptance.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.statistics.beans.aggregator.AG_Attribute;
import org.ourgrid.common.statistics.beans.aggregator.AG_Login;
import org.ourgrid.common.statistics.beans.aggregator.AG_Peer;
import org.ourgrid.common.statistics.beans.aggregator.AG_User;
import org.ourgrid.common.statistics.beans.aggregator.AG_Worker;
import org.ourgrid.common.statistics.beans.aggregator.monitor.AG_WorkerStatusChange;
import org.ourgrid.matchers.PeerCompleteHistoryStatusMatcher;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.communication.receiver.PeerComponentReceiver;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class PeerDBTestUtil {
	
	private PeerAcceptanceUtil util;
	private boolean active;
	
	public PeerDBTestUtil(ModuleContext context) {
		this(context, true);
	}
	
	public PeerDBTestUtil(ModuleContext context, boolean active) {
		util = new PeerAcceptanceUtil(context);
		this.active = active;
	}

	public void verifyPeerAdrees(PeerComponent component, long time, List<String> expectedAddresses) {
		
		if (!active) {
			return;
		}
		
		PeerStatusProviderClient client = EasyMock.createMock(PeerStatusProviderClient.class);
		
		PeerCompleteHistoryStatus completeStatus = new PeerCompleteHistoryStatus(0, "");
		List<AG_Peer> peers = new LinkedList<AG_Peer>();
		for (String expectedAddress : expectedAddresses) {
			AG_Peer peerInfo = new AG_Peer();
			peerInfo.setAddress(expectedAddress);
			peers.add(peerInfo);
		}
		
		completeStatus.setPeerInfo(peers);
		
		client.hereIsCompleteHistoryStatus((ServiceID) EasyMock.notNull(), PeerCompleteHistoryStatusMatcher.eqMatcher(completeStatus), EasyMock.anyLong());
		
		EasyMock.replay(client);
		
		PeerComponentReceiver pcc = (PeerComponentReceiver) util.getPeerControl();
		
		pcc.getCompleteHistoryStatus(client, time);
		
		EasyMock.verify(client);
	}
	
	public void verifyWorkers(PeerComponent component, long time, List<AG_Worker> workers) {
		
		if (!active) {
			return;
		}
		
		PeerStatusProviderClient client = EasyMock.createMock(PeerStatusProviderClient.class);
		
		PeerCompleteHistoryStatus completeStatus = new PeerCompleteHistoryStatus(0, "");
		AG_Peer peerTemp = new AG_Peer();
		peerTemp.setWorkers(workers);
		
		List<AG_Peer> peers = new ArrayList<AG_Peer>();
		peers.add(peerTemp);
		completeStatus.setPeerInfo(peers);
		
		client.hereIsCompleteHistoryStatus((ServiceID) EasyMock.notNull(), PeerCompleteHistoryStatusMatcher.eqMatcher(completeStatus), EasyMock.anyLong());
		
		EasyMock.replay(client);
		
		PeerComponentReceiver pcc = (PeerComponentReceiver) util.getPeerControl();
		
		pcc.getCompleteHistoryStatus(client, time);
		
		EasyMock.verify(client);
	}
	
	public void verifyUsers(PeerComponent component, long time, List<AG_User> users) {
		
		if (!active) {
			return;
		}
		
		PeerStatusProviderClient client = EasyMock.createMock(PeerStatusProviderClient.class);
		
		PeerCompleteHistoryStatus completeStatus = new PeerCompleteHistoryStatus(0, "");
		AG_Peer peerTemp = new AG_Peer();
		peerTemp.setUsers(users);
		
		List<AG_Peer> peers = new ArrayList<AG_Peer>();
		peers.add(peerTemp);
		
		completeStatus.setPeerInfo(peers);
		
		client.hereIsCompleteHistoryStatus((ServiceID) EasyMock.notNull(), PeerCompleteHistoryStatusMatcher.eqMatcher(completeStatus), EasyMock.anyLong());
		
		EasyMock.replay(client);
		
		PeerComponentReceiver pcc = (PeerComponentReceiver) util.getPeerControl();
		
		pcc.getCompleteHistoryStatus(client, time);
		
		EasyMock.verify(client);
	}
	
	public void verifyLogin(PeerComponent component, long time, List<AG_Login> loginInfo) {
		
		if (!active) {
			return;
		}
		
		PeerStatusProviderClient client = EasyMock.createMock(PeerStatusProviderClient.class);
		
		PeerCompleteHistoryStatus completeStatus = new PeerCompleteHistoryStatus(0, "");
		
		AG_Peer peerTemp = new AG_Peer();
		AG_User userTemp = new AG_User();
		userTemp.setLogins(loginInfo);
		
		List<AG_User> users = new ArrayList<AG_User>();
		users.add(userTemp);
		
		peerTemp.setUsers(users);
		
		List<AG_Peer> peers = new ArrayList<AG_Peer>();
		peers.add(peerTemp);
		completeStatus.setPeerInfo(peers);
		
		client.hereIsCompleteHistoryStatus((ServiceID) EasyMock.notNull(), PeerCompleteHistoryStatusMatcher.eqMatcher(completeStatus), EasyMock.anyLong());
		
		EasyMock.replay(client);
		
		PeerComponentReceiver pcc = (PeerComponentReceiver) util.getPeerControl();
		
		pcc.getCompleteHistoryStatus(client, time);
		
		EasyMock.verify(client);
	}
	
	public void verifyWorkerStatusChange(PeerComponent component, long time, List<AG_WorkerStatusChange> statusChange) {
		
		if (!active) {
			return;
		}
		
		PeerStatusProviderClient client = EasyMock.createMock(PeerStatusProviderClient.class);
		
		PeerCompleteHistoryStatus completeStatus = new PeerCompleteHistoryStatus(0, "");
		completeStatus.setWorkerStatusChangeInfo(statusChange);
		
		client.hereIsCompleteHistoryStatus((ServiceID) EasyMock.notNull(), PeerCompleteHistoryStatusMatcher.eqMatcher(completeStatus), EasyMock.anyLong());
		
		EasyMock.replay(client);
		
		PeerComponentReceiver pcc = (PeerComponentReceiver) util.getPeerControl();
		
		pcc.getCompleteHistoryStatus(client, time);
		
		EasyMock.verify(client);		
	}
	
	public static AG_Worker createWorkerInfo(String userName, String serverName, String mem, String peerAddress) {
		AG_Worker workerInfo = new AG_Worker();
		List<AG_Attribute> attributes = new ArrayList<AG_Attribute>();
		
		workerInfo.setAddress(userName + "@" + serverName);
		
		AG_Attribute workerServerName = new AG_Attribute();
		workerServerName.setProperty("servername");
		workerServerName.setValue(serverName);
		attributes.add(workerServerName);
		
		AG_Attribute workerUserName = new AG_Attribute();
		workerUserName.setProperty("username");
		workerUserName.setValue(userName);
		attributes.add(workerUserName);
		
		AG_Attribute workerMem = new AG_Attribute();
		workerMem.setProperty("mem");
		workerMem.setValue(mem);
		attributes.add(workerMem);
		
		AG_Peer peer = new AG_Peer();
		peer.setAddress(peerAddress);
		
		workerInfo.setPeer(peer);
		
		workerInfo.setAttributes(attributes);
		
		return workerInfo;
	}
	
	public static AG_Worker createClassAdWorkerInfo(String userName, String serverName, String mem, String peerAddress) {
		AG_Worker workerInfo = new AG_Worker();
		List<AG_Attribute> attributes = new ArrayList<AG_Attribute>();
		
		workerInfo.setAddress(userName + "@" + serverName);
		
		AG_Attribute workerServerName = new AG_Attribute();
		workerServerName.setProperty("servername");
		workerServerName.setValue(serverName);
		attributes.add(workerServerName);
		
		AG_Attribute workerUserName = new AG_Attribute();
		workerUserName.setProperty("username");
		workerUserName.setValue(userName);
		attributes.add(workerUserName);
		
		AG_Attribute workerMem = new AG_Attribute();
		workerMem.setProperty("mainMemory");
		workerMem.setValue(mem);
		attributes.add(workerMem);
		
		AG_Peer peer = new AG_Peer();
		peer.setAddress(peerAddress);
		
		workerInfo.setPeer(peer);
		
		workerInfo.setAttributes(attributes);
		
		return workerInfo;
	}
	
	public static AG_User createUserInfo(String address, String peer, String password, String publicKey) {
		AG_User userInfo = new AG_User();
		
		userInfo.setAddress(address);
		AG_Peer peerInfo = new AG_Peer();
		peerInfo.setAddress(peer);
		userInfo.setPeer(peerInfo);
		userInfo.setPublicKey(publicKey);
		
		return userInfo;
	}
}
