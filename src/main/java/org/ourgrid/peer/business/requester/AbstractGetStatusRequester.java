package org.ourgrid.peer.business.requester;

import static org.ourgrid.common.interfaces.Constants.LINE_SEPARATOR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.status.LocalConsumerInfo;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.UserState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.statistics.control.UserControl;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.dao.ConsumerDAO;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.UsersDAO;
import org.ourgrid.peer.dao.AllocationDAO;
import org.ourgrid.peer.dao.DiscoveryServiceClientDAO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.Consumer;
import org.ourgrid.peer.to.LocalAllocableWorker;
import org.ourgrid.peer.to.LocalConsumer;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.peer.to.PeerUser;
import org.ourgrid.peer.to.RemoteAllocableWorker;
import org.ourgrid.reqtrace.Req;

public abstract class AbstractGetStatusRequester<U extends IRequestTO> implements RequesterIF<U> {
	
	private static final WorkerInfoComparator WORKER_INFO_COMPARATOR = new WorkerInfoComparator();
	
	private static class WorkerInfoComparator implements Comparator<WorkerInfo> {
		public int compare(WorkerInfo o1, WorkerInfo o2) {
			String id1 = (o1.getId() == null) ? "" : o1.getId();
			String id2 = (o2.getId() == null) ? "" : o2.getId();
			return id1.compareTo(id2);
		}
	}
	
	@Req({"REQ036", "REQ038a"})
	protected List<WorkerInfo> getLocalWorkersInfo(List<IResponseTO> responses, 
			String peerUserAtServer) {
		List<WorkerInfo> localWorkersInfos = new LinkedList<WorkerInfo>();

		localWorkersInfos.addAll( getLocalWorkersInfo( responses, peerUserAtServer, LocalWorkerState.IN_USE ) );
		localWorkersInfos.addAll( getLocalWorkersInfo( responses, peerUserAtServer,LocalWorkerState.DONATED ) );
		localWorkersInfos.addAll( getLocalWorkersInfo( responses, peerUserAtServer,LocalWorkerState.IDLE ) );
		localWorkersInfos.addAll( getLocalWorkersInfo( responses, peerUserAtServer,LocalWorkerState.OWNER ) );
		localWorkersInfos.addAll( getLocalWorkersInfo( responses, peerUserAtServer,LocalWorkerState.ERROR ) );

		return localWorkersInfos;
	}

	@Req({"REQ036", "REQ038a"})
	protected List<WorkerInfo> getLocalWorkersInfo(List<IResponseTO> responses, 
			String peerUserAtServer,LocalWorkerState status) {
		if(status.isAllocated()){
			return getAllocatedWorkersInfos(responses, peerUserAtServer,status);
		}
		return getUnallocatedWorkersInfos(responses, peerUserAtServer,status);
	}

	@Req({"REQ036", "REQ038a"})
	protected List<WorkerInfo> getAllocatedWorkersInfos(List<IResponseTO> responses,
			String peerUserAtServer, LocalWorkerState status) {
		AllocationDAO dao = PeerDAOFactory.getInstance().getAllocationDAO();
		String consumerAddress = null;
		
		LocalAllocableWorker localAllocableWorker = null;
		Consumer consumer = null;
		
		List<WorkerInfo> result = new LinkedList<WorkerInfo>();
		for (LocalWorker localWorker : WorkerControl.getInstance().getLocalWorkers(responses,
				 peerUserAtServer, status)) { 
			
			localAllocableWorker = dao.getLocalAllocableWorker(localWorker.getPublicKey());
			
			if (localAllocableWorker != null) {
				consumer = localAllocableWorker.getConsumer();
				
				if (consumer != null) {
					consumerAddress = consumer.getConsumerAddress();
				}
			}
			
			result.add( new WorkerInfo( localWorker.getWorkerSpecification(), status, consumerAddress) );
		}
		Collections.sort(result, WORKER_INFO_COMPARATOR);
		return result;
	}

	@Req({"REQ036", "REQ038a"})
	protected List<WorkerInfo> getUnallocatedWorkersInfos(List<IResponseTO> responses,
			 String peerUserAtServer, 
			LocalWorkerState status) {
		List<WorkerInfo> result = new LinkedList<WorkerInfo>();
		for (LocalWorker localWorker : WorkerControl.getInstance().getLocalWorkers(responses,
				 peerUserAtServer, status)) { 
				result.add( new WorkerInfo( localWorker.getWorkerSpecification(), status, null) );
		}
		Collections.sort(result, WORKER_INFO_COMPARATOR);
		return result;
	}
	
	@Req({"REQ038a", "REQ037"})
	protected List<RemoteWorkerInfo> getRemoteWorkersInfo() {
		List<RemoteWorkerInfo> remoteWorkersInfo = new LinkedList<RemoteWorkerInfo>();

		List<RemoteAllocableWorker> remoteAllocables = PeerDAOFactory.getInstance().getAllocationDAO().getRemoteAllocableWorkers();
		
		if (remoteAllocables != null) {
			for (RemoteAllocableWorker remoteAllocable : remoteAllocables) {
				
				Consumer consumer = remoteAllocable.getConsumer();

				String consumerAddress = null;
				if (consumer != null) {
					consumerAddress = consumer.getConsumerAddress();
				}
				
				remoteWorkersInfo.add(
						new RemoteWorkerInfo(remoteAllocable.getWorkerSpecification(), remoteAllocable.getProviderAddress(), consumerAddress));
			}
			Collections.sort(remoteWorkersInfo, WORKER_INFO_COMPARATOR);
		}
		
		return remoteWorkersInfo;
		
	}
	
	protected List<UserInfo> getUsersInfo(List<IResponseTO> responses) {
		List<UserInfo> consumingUsersResult = new LinkedList<UserInfo>();
		List<UserInfo> loggedUsersResult = new LinkedList<UserInfo>();
		List<UserInfo> offlineUsersResult = new LinkedList<UserInfo>();
		List<UserInfo> neverLoggedUsersResult = new LinkedList<UserInfo>();
		
		UsersDAO dao = PeerDAOFactory.getInstance().getUsersDAO();
		
		for (PeerUser user : UserControl.getInstance().getUsers(responses).values()) {
			
			//If the user uses authentication, and has no public key registered, 
			//then its status is NEVER_LOGGED
			if(user.getPublicKey() == null || user.getPublicKey().equals("")){
				neverLoggedUsersResult.add(new UserInfo(user.getUsername(), 
						user.getXMPPServer(), user.getPublicKey(), UserState.NEVER_LOGGED));
			}
			
			//If the user is not logged and does not use authentication or has a public key registered, 
			//then its status is OFFLINE
			if(user.getPublicKey() != null && !user.getPublicKey().equals("") && isOffline(user)){
				offlineUsersResult.add(new UserInfo(user.getUsername(), 
						user.getXMPPServer(), user.getPublicKey(), UserState.OFFLINE));
			}
			
			//If the user is online and consuming, its status is CONSUMING, otherwise its LOGGED
			if(dao.isLoggedUser(user.getPublicKey())){
				if(isUserConsuming(user)){
					consumingUsersResult.add(new UserInfo(user.getUsername(), user.getXMPPServer(), user.getPublicKey(), UserState.CONSUMING));
				} else {
					loggedUsersResult.add(new UserInfo(user.getUsername(), user.getXMPPServer(), user.getPublicKey(), UserState.LOGGED));
				}
			}
		}
		
		Collections.sort(consumingUsersResult);
		Collections.sort(loggedUsersResult);
		Collections.sort(offlineUsersResult);
		Collections.sort(neverLoggedUsersResult);
		
		List<UserInfo> completeOrderedUsersInfo = new LinkedList<UserInfo>();
		completeOrderedUsersInfo.addAll(consumingUsersResult);
		completeOrderedUsersInfo.addAll(loggedUsersResult);
		completeOrderedUsersInfo.addAll(offlineUsersResult);
		completeOrderedUsersInfo.addAll(neverLoggedUsersResult);
		
		return completeOrderedUsersInfo;
	}
	
	@Req({"REQ038a", "REQ106"})
	private boolean isUserConsuming(PeerUser user) {
		return PeerDAOFactory.getInstance().getConsumerDAO().isUserConsuming(user.getPublicKey());
	}
	
	private boolean isOffline(PeerUser user) {
		return !(PeerDAOFactory.getInstance().getUsersDAO().isLoggedUser(user.getPublicKey()));
	}
	
	@Req("REQ038a")
	protected List<LocalConsumerInfo> getLocalConsumersInfo() {
		
		ConsumerDAO consumerDAO = PeerDAOFactory.getInstance().getConsumerDAO();
		List<LocalConsumer> localConsumers = consumerDAO.getLocalConsumers();
		
		List<LocalConsumerInfo> localConsumersInfo = new ArrayList<LocalConsumerInfo>();
		
		for (LocalConsumer localConsumer : localConsumers) {
			
			if (localConsumer.getConsumerAddress() == null) {
				continue;
			}
			
			int noOfLocalWorkers = 0;
			int noOfRemoteWorkers = 0;
			
			for (AllocableWorker allWorker : localConsumer.getAllocableWorkers()) {
				if (allWorker.isWorkerLocal()) {
					noOfLocalWorkers++;
				} else {
					noOfRemoteWorkers++;
				}
			}
			
			LocalConsumerInfo consumerInfo = new LocalConsumerInfo(
					noOfLocalWorkers,
					StringUtil.addressToUserAtServer(localConsumer.getConsumerAddress()), 
					noOfRemoteWorkers);
			
			localConsumersInfo.add(consumerInfo);
		}
		
		return localConsumersInfo;
	}
	
	/**
	 * Returns a list containing all the remote consumers' status.
	 * @return a list containing all the remote consumers' status.
	 */
	@Req("REQ034")
	protected ArrayList<ConsumerInfo> getRemoteConsumersStatus() {
		Map<String,ConsumerInfo> consumersMap = CommonUtils.createSerializableMap();

		List<AllocableWorker> allocableWorkers = PeerDAOFactory.getInstance().getAllocationDAO().getLocalAllocableWorkers();
		
		for (AllocableWorker allocableWorker : allocableWorkers) {

			Consumer consumer = allocableWorker.getConsumer();
			if (consumer == null || consumer.isLocal()) {
				continue;
			}

			String consumerAddress = consumer.getConsumerAddress();
			if (consumerAddress == null) {
				continue;
			}
			
			ConsumerInfo consumerInfo = consumersMap.get(consumerAddress);
			
			if (consumerInfo == null) {
				consumerInfo = new ConsumerInfo(0, consumerAddress);
				consumersMap.put(consumerAddress, consumerInfo);
			}
			
			consumerInfo.setNumberOfLocalWorkers(consumerInfo.getNumberOfLocalWorkers() + 1);
		}
		
		
		ArrayList<ConsumerInfo> result = new ArrayList<ConsumerInfo>(consumersMap.values());
		Collections.sort(result, new ConsumerInfoComparator());
		return result;
	}
	
	/**
	 * This class compares two consumers info through the method <code>compare</code> regarding each consumer's id.
	 */
	public class ConsumerInfoComparator implements Comparator<ConsumerInfo> {

		/**
		 * Compares two consumer infos through their ids.
		 * @param o1 first consumer info
		 * @param o2 second consumer info
		 * @return the first consumer info id less the second consumer info id
		 */
		public int compare(ConsumerInfo o1, ConsumerInfo o2) {
			return o1.getConsumerIdentification().compareTo(o2.getConsumerIdentification());
		}
		
	}
	
	protected String getDescription(String contextString, String propConfDir, String propLabel, String propJoinCommunity, 
			boolean isJoinCommunityEnabled) {
		
		StringBuilder conf = new StringBuilder();

		conf.append( "\tVersion: " ).append( Configuration.VERSION ).append( LINE_SEPARATOR );

		conf.append( "\tConfiguration directory: " );
		conf.append( /*containerContext.getProperty(ModuleProperties.PROP_CONFDIR)*/propConfDir );
		conf.append( LINE_SEPARATOR );
		
		conf.append( /*containerContext.toString()*/contextString );

		conf.append( "\tLabel: " );
		conf.append( /*containerContext.getProperty( PeerConfiguration.PROP_LABEL )*/propLabel );
		conf.append( LINE_SEPARATOR );

		conf.append( "\tJoin community: " );
		conf.append( /*containerContext.getProperty( PeerConfiguration.PROP_JOIN_COMMUNITY )*/propJoinCommunity );
		conf.append( LINE_SEPARATOR );
		
		if (/*containerContext.isEnabled(PeerConfiguration.PROP_JOIN_COMMUNITY)*/isJoinCommunityEnabled) {
			conf.append( "\tDiscovery Service List: " );
			conf.append( LINE_SEPARATOR );
			
			DiscoveryServiceClientDAO dao = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
			for (String dsAddress : dao.getDsAddresses()) {
				conf.append("\t\t" + StringUtil.addressToUserAtServer(dsAddress));
				conf.append( LINE_SEPARATOR );
			}
			
			if (dao.isConnected()) {
				conf.append("\tConnected to: " + StringUtil.addressToUserAtServer(dao.getAliveDiscoveryServiceAddress()));
				conf.append( LINE_SEPARATOR );
			}
			
		}
		
		return conf.toString();
	}
}
