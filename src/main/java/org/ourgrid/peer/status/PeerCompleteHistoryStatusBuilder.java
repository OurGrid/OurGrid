/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.peer.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.ourgrid.common.interfaces.to.Accounting;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.beans.aggregator.AG_Peer;
import org.ourgrid.common.statistics.beans.aggregator.monitor.AG_WorkerStatusChange;
import org.ourgrid.common.statistics.beans.pair.AGPair;
import org.ourgrid.common.statistics.beans.pair.AttributePair;
import org.ourgrid.common.statistics.beans.pair.CommandPair;
import org.ourgrid.common.statistics.beans.pair.GridProcessPair;
import org.ourgrid.common.statistics.beans.pair.JobPair;
import org.ourgrid.common.statistics.beans.pair.LoginPair;
import org.ourgrid.common.statistics.beans.pair.PeerPair;
import org.ourgrid.common.statistics.beans.pair.TaskPair;
import org.ourgrid.common.statistics.beans.pair.UserPair;
import org.ourgrid.common.statistics.beans.pair.WorkerPair;
import org.ourgrid.common.statistics.beans.pair.WorkerStatusChangePair;
import org.ourgrid.common.statistics.beans.peer.Attribute;
import org.ourgrid.common.statistics.beans.peer.Command;
import org.ourgrid.common.statistics.beans.peer.GridProcess;
import org.ourgrid.common.statistics.beans.peer.Job;
import org.ourgrid.common.statistics.beans.peer.Login;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.beans.peer.Task;
import org.ourgrid.common.statistics.beans.peer.User;
import org.ourgrid.common.statistics.beans.peer.Worker;
import org.ourgrid.common.statistics.beans.peer.monitor.WorkerStatusChange;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.status.util.PeerHistoryStatusBuilderHelper;
import org.ourgrid.peer.to.PeerBalance;

/**
 * @author alan
 *
 */
public class PeerCompleteHistoryStatusBuilder {

	private static final String TIME_OF_CHANGE = "timeOfChange";
	private static final String UNTIL = "until";
	
	public PeerCompleteHistoryStatus buildCompleteHistoryStatus(List<IResponseTO> responses, long since, long until, long uptime,
			String configuration, String myUserAtServer) {
		
		PeerCompleteHistoryStatus history = new PeerCompleteHistoryStatus(uptime, configuration);
		
		HibernateUtil.beginTransaction();
		try {
			collectPeerHistory(responses, since, until, myUserAtServer, history);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			HibernateUtil.rollbackAndCloseSession();
		}
		
		return history;
	}
	
	@SuppressWarnings("unchecked")
	private void collectPeerHistory(List<IResponseTO> responses, long time, long until, String myUserAtServer,
			PeerCompleteHistoryStatus status) {
		
		//Commands
		Query commandsQuery = HibernateUtil.getSession().getNamedQuery("getCommandsSince");
		commandsQuery.setCacheable(false);
		commandsQuery.setLong(TIME_OF_CHANGE, time);
		commandsQuery.setLong(UNTIL, until);
		
		List<Command> commands = commandsQuery.list();
		List<CommandPair> commandsPair = new ArrayList<CommandPair>();
		for (Command command : commands) {
			commandsPair.add(new CommandPair(command, PeerHistoryStatusBuilderHelper.convertCommand(command)));
			HibernateUtil.getSession().evict(command);
		}
		
		//Grid Process
		Query processQuery = HibernateUtil.getSession().getNamedQuery("getProcessSince");
		processQuery.setCacheable(false);
		processQuery.setLong(TIME_OF_CHANGE, time);
		processQuery.setLong(UNTIL, until);
		
		List<GridProcess> processes = processQuery.list();
		List<GridProcessPair> processesPair = new ArrayList<GridProcessPair>();
		for (GridProcess gridProcess : processes) {
			processesPair.add(new GridProcessPair(gridProcess, PeerHistoryStatusBuilderHelper.convertProcess(gridProcess)));
			HibernateUtil.getSession().evict(gridProcess);
		}
		
		collectHierarchy(commandsPair, processesPair);
		
		//Task
		Query taskQuery = HibernateUtil.getSession().getNamedQuery("getTasksSince");
		taskQuery.setCacheable(false);
		taskQuery.setLong(TIME_OF_CHANGE, time);
		taskQuery.setLong(UNTIL, until);
		
		List<Task> tasks = taskQuery.list();
		List<TaskPair> tasksPair = new ArrayList<TaskPair>();
		for (Task task : tasks) {
			tasksPair.add(new TaskPair(task, PeerHistoryStatusBuilderHelper.convertTask(task)));
			HibernateUtil.getSession().evict(task);
		}
		
		collectHierarchy(processesPair, tasksPair);
		
		//Job
		Query jobsQuery = HibernateUtil.getSession().getNamedQuery("getJobsSince");
		jobsQuery.setCacheable(false);
		jobsQuery.setLong(TIME_OF_CHANGE, time);
		jobsQuery.setLong(UNTIL, until);
		
		List<Job> jobs = jobsQuery.list();
		List<JobPair> jobsPair = new ArrayList<JobPair>();
		for (Job job : jobs) {
			jobsPair.add(new JobPair(job, PeerHistoryStatusBuilderHelper.convertJob(job)));
			HibernateUtil.getSession().evict(job);
		}
		
		collectHierarchy(tasksPair, jobsPair);
		
		//Login
		Query loginsQuery = HibernateUtil.getSession().getNamedQuery("getLoginsSince");
		loginsQuery.setCacheable(false);
		loginsQuery.setLong(TIME_OF_CHANGE, time);
		loginsQuery.setLong(UNTIL, until);
		
		List<Login> logins = loginsQuery.list();
		List<LoginPair> loginsPair = new ArrayList<LoginPair>();
		for (Login login : logins) {
			loginsPair.add(new LoginPair(login, PeerHistoryStatusBuilderHelper.convertLogin(login)));
			HibernateUtil.getSession().evict(login);
		}
		
		collectHierarchy(jobsPair, loginsPair);
		
		//User
		Query usersQuery = HibernateUtil.getSession().getNamedQuery("getUsersSince");
		usersQuery.setCacheable(false);
		usersQuery.setLong(TIME_OF_CHANGE, time);
		usersQuery.setLong(UNTIL, until);
		
		List<User> users = usersQuery.list();
		List<UserPair> usersPair = new ArrayList<UserPair>();
		for (User user : users) {
			usersPair.add(new UserPair(user, PeerHistoryStatusBuilderHelper.convertUser(user)));
			HibernateUtil.getSession().evict(user);
		}
	
		collectHierarchy(loginsPair, usersPair);
		
		//Peer
		Query peersQuery = HibernateUtil.getSession().getNamedQuery("getPeersSince");
		peersQuery.setCacheable(false);
		peersQuery.setLong(TIME_OF_CHANGE, time);
		peersQuery.setLong(UNTIL, until);
		
		List<Peer> peers = peersQuery.list();
		List<PeerPair> peersPair = new ArrayList<PeerPair>();
		for (Peer peer : peers) {
			peersPair.add(new PeerPair(peer, PeerHistoryStatusBuilderHelper.convertPeer(peer)));
			HibernateUtil.getSession().evict(peer);
		}
	
		collectHierarchy(usersPair, peersPair);
		
		//Attribute
		Query attsQuery = HibernateUtil.getSession().getNamedQuery("getAttributesSince");
		attsQuery.setCacheable(false);
		attsQuery.setLong(TIME_OF_CHANGE, time);
		attsQuery.setLong(UNTIL, until);
		
		List<Attribute> attributes = attsQuery.list();
		List<AttributePair> attsPair = new ArrayList<AttributePair>();
		for (Attribute attribute : attributes) {
			attsPair.add(new AttributePair(attribute, PeerHistoryStatusBuilderHelper.convertAttribute(attribute)));
			HibernateUtil.getSession().evict(attribute);
		}
		
		//Worker
		Query workersQuery = HibernateUtil.getSession().getNamedQuery("getWorkersSince");
		workersQuery.setCacheable(false);
		workersQuery.setLong(TIME_OF_CHANGE, time);
		workersQuery.setLong(UNTIL, until);
		
		List<Worker> workers = workersQuery.list();
		List<WorkerPair> workersPair = new ArrayList<WorkerPair>();
		for (Worker worker : workers) {
			workersPair.add(new WorkerPair(worker, PeerHistoryStatusBuilderHelper.convertWorker(worker)));
			HibernateUtil.getSession().evict(worker);
		}
		
		collectHierarchy(attsPair, workersPair);
		
		//Process x Worker
		collectWorkerProcessHierarchy(processesPair, workersPair);
		
		//StatusChange x Worker 
		Query statusQuery = HibernateUtil.getSession().getNamedQuery("collectWorkerStatusChangeInfo");
		statusQuery.setCacheable(false);
		statusQuery.setLong(TIME_OF_CHANGE, time);
		statusQuery.setLong(UNTIL, until);
		
		List<WorkerStatusChange> changes = statusQuery.list();
		List<WorkerStatusChangePair> changesPair = new ArrayList<WorkerStatusChangePair>();
		
		for (WorkerStatusChange workerStatusChange : changes) {
			changesPair.add(new WorkerStatusChangePair(workerStatusChange, 
					PeerHistoryStatusBuilderHelper.convertWorkerStatusChange(workerStatusChange)));
			HibernateUtil.getSession().evict(workerStatusChange);
		}
		
		collectWorkerStatusChangeHierarchy(changesPair, workersPair);
		
		//Worker x Peer
		collectPeerWorkerHierarchy(workersPair, peersPair);
		
		List<AG_Peer> peerInfo = new ArrayList<AG_Peer>();
		for (PeerPair peerPair : peersPair) {
			peerInfo.add(peerPair.getAGObject());
		}
		
		List<AG_WorkerStatusChange> statusChangeInfo = new ArrayList<AG_WorkerStatusChange>();
		for (WorkerStatusChangePair workerStatusChangePair : changesPair) {
			statusChangeInfo.add(workerStatusChangePair.getAGObject());
		}
		
		status.setPeerInfo(peerInfo);
		status.setWorkerStatusChangeInfo(statusChangeInfo);
		status.setWorkAccountings(collectWorkAccountings(responses, myUserAtServer));
	}

	private Map<String, Accounting> collectWorkAccountings(List<IResponseTO> responses, String myUserAtServer) {
		
		Map<String, Accounting> accountings = CommonUtils.createSerializableMap();
		Collection<Worker> activeWorkers = PeerDAOFactory.getInstance().getWorkerDAO().findAllActiveWorkers(
				responses, myUserAtServer);
		
		if (activeWorkers  == null) {
			return accountings;
		}
		
		for (Worker worker : activeWorkers) {
			accountings.put(worker.getAddress(), new Accounting(new PeerBalance(worker.getCpuTime(), worker.getDataStored())));
		}
		return accountings;
	}
	
	private void collectPeerWorkerHierarchy(List<WorkerPair> workersPair,
			List<PeerPair> peersPair) {
		for (WorkerPair workerPair : workersPair) {
			boolean hasParent = false;
			
			Peer peer = workerPair.getParent();
			for (PeerPair peerPair : peersPair) {
				if (peer.equals(peerPair.getObject())) {
					workerPair.setAGParent(peerPair.getAGObject());
					peerPair.getAGObject().getWorkers().add(workerPair.getAGObject());
					hasParent = true;
					break;
				}
			}
			
			if (!hasParent) {
				PeerPair parentPair = workerPair.createParentPair();
				
				workerPair.setAGParent(parentPair.getAGObject());
				parentPair.getAGObject().getWorkers().add(workerPair.getAGObject());
				
				peersPair.add(parentPair);
			}
		}
	}
	
	private void collectWorkerStatusChangeHierarchy(
			List<WorkerStatusChangePair> changesPair, List<WorkerPair> workersPair) {
		
		for (WorkerStatusChangePair wscPair : changesPair) {
			boolean hasParent = false;
			
			Worker worker = wscPair.getObject().getWorker();
			for (WorkerPair workerPair : workersPair) {
				if (worker.equals(workerPair.getObject())) {
					wscPair.setAGParent(workerPair.getAGObject());
					hasParent = true;
					break;
				}
			}
			
			if (!hasParent) {
				WorkerPair workerPair = new WorkerPair(worker, PeerHistoryStatusBuilderHelper.convertWorker(worker));
				wscPair.setAGParent(workerPair.getAGObject());
				workersPair.add(workerPair);
			}
		}
		
	}

	private void collectWorkerProcessHierarchy(
			List<GridProcessPair> processesPair, List<WorkerPair> workersPair) {
		
		for (GridProcessPair processPair : processesPair) {
			boolean hasParent = false;
			
			Worker worker = processPair.getObject().getWorker();
			for (WorkerPair workerPair : workersPair) {
				if (worker.equals(workerPair.getObject())) {
					processPair.getAGObject().setWorker(workerPair.getAGObject());
					hasParent = true;
					break;
				}
			}
			
			if (!hasParent) {
				WorkerPair workerPair = new WorkerPair(worker, PeerHistoryStatusBuilderHelper.convertWorker(worker));
				processPair.getAGObject().setWorker(workerPair.getAGObject());
				workersPair.add(workerPair);
			}
		}
		
	}


	@SuppressWarnings("unchecked")
	private <C extends AGPair, T extends AGPair> void collectHierarchy(List<C> childrenPair, List<T> parentsPair) {
		
		for (AGPair childPair : childrenPair) {
			boolean hasParent = false;
			
			Object parent = childPair.getParent();
			for (AGPair parentPair : parentsPair) {
				if (parent.equals(parentPair.getObject())) {
					childPair.setAGParent(parentPair.getAGObject());
					parentPair.addAGChildren(childPair.getAGObject());
					hasParent = true;
					break;
				}
			}
			
			if (!hasParent) {
				T parentPair = (T) childPair.createParentPair();
				
				childPair.setAGParent(parentPair.getAGObject());
				parentPair.addAGChildren(childPair.getAGObject());
				
				parentsPair.add(parentPair);
			}
		}
		
	}
	

}
