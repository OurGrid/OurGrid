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
package org.ourgrid.aggregator.business.dao;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.statistics.beans.aggregator.AG_Attribute;
import org.ourgrid.common.statistics.beans.aggregator.AG_Command;
import org.ourgrid.common.statistics.beans.aggregator.AG_GridProcess;
import org.ourgrid.common.statistics.beans.aggregator.AG_Job;
import org.ourgrid.common.statistics.beans.aggregator.AG_Login;
import org.ourgrid.common.statistics.beans.aggregator.AG_Peer;
import org.ourgrid.common.statistics.beans.aggregator.AG_Task;
import org.ourgrid.common.statistics.beans.aggregator.AG_User;
import org.ourgrid.common.statistics.beans.aggregator.AG_Worker;
import org.ourgrid.common.statistics.beans.aggregator.monitor.AG_PeerStatusChange;
import org.ourgrid.common.statistics.beans.aggregator.monitor.AG_WorkerStatusChange;
import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;
import org.ourgrid.common.statistics.beans.status.WorkerStatus;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;


/**
 * This class provide the DAO of this Module.
 *
 */
public class AggregatorDAO {

	private String cmmStatusProviderAddress;

	private long peerStatusChangeLastUpdate = 0L;

	private Hashtable<String,AggregatorPeerStatusProvider> aggregatorPeerStatusProviders;
	
	private List<String> currentProvidersAddress;
	
	/**
	 * Constructor default. Initializing the HasTable aggregatorPeerStatusProviders.
	 */
	public AggregatorDAO() {
		this.aggregatorPeerStatusProviders = new Hashtable<String,AggregatorPeerStatusProvider>();
		this.currentProvidersAddress = new ArrayList<String>();
	}
	
	/**
	 * Removes the AggregatorPeerStatusProvider, which has providerAddress it is equal to providerAddress
	 * from one {@link AggregatorPeerStatus}.
	 * This method does nothing if not exists matching. 
	 * @param providerAddress {@link String}
	 * @return AggregatorPeerStatusProvider {@link AggregatorPeerStatusProvider}
	 * corresponding if exists matching, else null.
	 */
	public AggregatorPeerStatusProvider removeProvider(String providerAddress) {
		return this.aggregatorPeerStatusProviders.remove(providerAddress);
	}
	
	/**
	 * Put the aggProvider in a hashtable with corresponding providerAddress.
	 * @param aggProvider {@link AggregatorPeerStatusProvider}
	 */
	public void putProvider(AggregatorPeerStatusProvider aggProvider) {
		aggregatorPeerStatusProviders.put(aggProvider.getProviderAddress(), aggProvider);
	}
	
	public boolean aggregatorCurrentPeerStatusProvidersIsEmpty(){
		return this.currentProvidersAddress.isEmpty();
	}
	
	public boolean containsCurrentProviderAddress(String providerAddress) {
		return this.currentProvidersAddress.contains(providerAddress);
	}
	
	public Set<String> getProvidersAddresses() {
		return aggregatorPeerStatusProviders.keySet();
	}
	
	/**
	 * This method adds the complete history status to the database, performing merge 
	 * when necessary.
	 * @param completeStatus {@link PeerCompleteHistoryStatus}
	 * @param providerAddress {@link String}
	 */
	public void addCompleteHistoryStatus(PeerCompleteHistoryStatus completeStatus, String providerAddress, 
			List<IResponseTO> responses) {
		
		HibernateUtil.beginTransaction();
		
		try {
			List<AG_Peer> peerInfo = completeStatus.getPeerInfo();
			
			//iterate for all peers atualizing all those informations recursively
			//atualizing all workers owned of each peer.
			for (AG_Peer peer : peerInfo) {
				
				//update the worker status if the peerAddess does not match
				//to the providerAddress.
				if (!peer.getAddress().equals(providerAddress)) {
					for (AG_Worker worker : peer.getWorkers()) {
						worker.setStatus(WorkerStatus.DONATED);
						worker.setEndTime(null);
					}
				}
				
				AG_Peer existingPeer = getExistingPeer(peer.getAddress());
				List<AG_Worker> existingWorkers = new ArrayList<AG_Worker>();
				
				//save the peer if him not exists in dao. Else update him.
				if (existingPeer == null) {
					savePeer(peer);
					
				} else {
					if (peer.getAddress().equals(providerAddress)) {
						updatePeer(peer, existingPeer);
					}
					
					List<AG_Worker> workers = new ArrayList<AG_Worker>(peer.getWorkers());
					for (AG_Worker worker : workers) {
						
						AG_Worker mergedWorker = mergeWorker(worker, existingPeer);
						existingWorkers.add(mergedWorker);
					}
					
					List<AG_User> users = new ArrayList<AG_User>(peer.getUsers());
					for (AG_User user : users) {
						mergeUser(user, existingPeer, existingWorkers);
					}
				}
			}
			//Every WorkerStatusChange is new (lastModified > now)
			for (AG_WorkerStatusChange wsc : completeStatus.getWorkerStatusChangeInfo()) {
				saveWorkerStatusChange(wsc);
			}
			
			responses.add(new LoggerResponseTO(AggregatorControlMessages
					.getAddCompleteHistoryStatusSuccessfulMessage(),
					LoggerResponseTO.INFO));
			HibernateUtil.commitTransaction();
			
		} catch (Exception e) {
			responses.add(new LoggerResponseTO(AggregatorControlMessages
				.getRollbackTransactionMessage( "addCompleteHistoryStatus", e.getMessage()),
				LoggerResponseTO.WARN));
			HibernateUtil.rollbackTransaction();
		
		} finally {
			HibernateUtil.closeSession();
		}
		
		
	}
	
	public void setCommunityStatusProviderAddress(String cmmStatusProviderAddress) {
		this.cmmStatusProviderAddress = cmmStatusProviderAddress;
	}

	
	public void setPeerStatusChangeLastUpdate(long peerStatusChangeLastUpdate) {
		this.peerStatusChangeLastUpdate = peerStatusChangeLastUpdate;
	}
	
	public String getCommunityStatusProviderAddress() {
		return this.cmmStatusProviderAddress;
	}

	public void setCurrentProvidersAddress(List<String> currentProvidersAddress) {
		this.currentProvidersAddress = currentProvidersAddress;
	}

	/**
	 * Get the AggregatorPeerStatusProvider corresponding to providerAddress.
	 * @param providerAddress {@link String}
	 * @return AggregatorPeerStatusProvider {@link AggregatorPeerStatusProvider} corresponding if
	 * exists matching, else null
	 */
	public AggregatorPeerStatusProvider getProvider(String providerAddress) {
		return aggregatorPeerStatusProviders.get(providerAddress);
	}

	public long getPeerStatusChangeLastUpdate() {
		return peerStatusChangeLastUpdate;
	}

	private void saveWorkerStatusChange(AG_WorkerStatusChange wsc) {
		AG_Worker existingWorker = getExistingWorker(wsc.getWorker(), wsc.getWorker().getPeer().getAddress());
		wsc.setWorker(existingWorker);
		
		if (hasNotExistingWorkerStatusChange(existingWorker.getAddress(), wsc.getTimeOfChange())) {
			HibernateUtil.getSession().save(wsc);
		}
	}
	
	private boolean hasNotExistingWorkerStatusChange(String workerAddress, long timeOfChange) {
		Criteria criteria = HibernateUtil.getSession().createCriteria(AG_WorkerStatusChange.class);
		criteria.add(Restrictions.eq("timeOfChange", timeOfChange));
		
		Criteria workerCriteria = criteria.createCriteria("worker");
		workerCriteria.add(Restrictions.eq("address", workerAddress));
		
		return criteria.list().isEmpty();
	}

	private AG_Worker mergeWorker(AG_Worker worker, AG_Peer existingPeer) {
		AG_Worker existingWorker = getExistingWorker(worker, existingPeer.getAddress());
		if (existingWorker == null) {
			return saveWorker(worker, existingPeer);
		} 
		
		updateWorker(worker, existingWorker);
		
		for (AG_Attribute attribute : worker.getAttributes()) {
			mergeAttribute(attribute, existingWorker);
		}
		return existingWorker;
	}

	private void mergeAttribute(AG_Attribute attribute, AG_Worker existingWorker) {
		AG_Attribute existingAttribute = getExistingAttribute(attribute, existingWorker);
		if (existingAttribute == null) {
			saveAttribute(attribute, existingWorker);
		} else {
			updateAttribute(attribute, existingAttribute);
		}
	}

	private void mergeUser(AG_User user, AG_Peer existingPeer, List<AG_Worker> existingWorkers) {
		AG_User existingUser = getExistingUser(user, existingPeer.getAddress());
		if (existingUser == null) {
			saveUser(user, existingPeer, existingWorkers);
		} else {
			updateUser(user, existingUser);
			List<AG_Login> logins = new ArrayList<AG_Login>(user.getLogins());
			for (AG_Login login : logins) {
				mergeLogin(login, existingUser, existingWorkers);
			}
		}
	}

	private void mergeLogin(AG_Login login, AG_User existingUser, List<AG_Worker> existingWorkers) {
		AG_Login existingLogin = getExistingLogin(login, existingUser);
		if (existingLogin == null) {
			saveLogin(login, existingUser, existingWorkers);
		} else {
			updateLogin(login, existingLogin);
			List<AG_Job> jobs = new ArrayList<AG_Job>(login.getJobs());
			for (AG_Job job : jobs) {
				mergeJob(job, existingLogin, existingWorkers);
			}
		}
	}

	private void mergeJob(AG_Job job, AG_Login existingLogin, List<AG_Worker> existingWorkers) {
		AG_Job existingJob = getExistingJob(job, existingLogin);
		if (existingJob == null) {
			saveJob(job, existingLogin, existingWorkers);
		} else {
			updateJob(job, existingJob);
			List<AG_Task> tasks = new ArrayList<AG_Task>(job.getTasks());
			for (AG_Task task : tasks) {
				mergeTask(task, existingJob, existingWorkers);
			}
		}
	}

	private void mergeTask(AG_Task task, AG_Job existingJob, List<AG_Worker> existingWorkers) {
		AG_Task existingTask = getExistingTask(task, existingJob);
		if (existingTask == null) {
			saveTask(task, existingJob, existingWorkers);
		} else {
			updateTask(task, existingTask);
			List<AG_GridProcess> processes = new ArrayList<AG_GridProcess>(task.getProcesses());
			for (AG_GridProcess process : processes) {
				mergeProcess(process, existingTask, existingWorkers);
			}
		}
		
	}

	private void mergeProcess(AG_GridProcess process, AG_Task existingTask, List<AG_Worker> existingWorkers) {
		AG_GridProcess existingProcess = getExistingProcess(process, existingTask);
		if (existingProcess == null) {
			saveProcess(process, existingTask, existingWorkers);
		} else {
			updateProcess(process, existingProcess);
			List<AG_Command> commands = new ArrayList<AG_Command>(process.getCommands());
			for (AG_Command command : commands) {
				mergeCommand(command, existingProcess);
			}
		}
	}

	private void mergeCommand(AG_Command command, AG_GridProcess existingProcess) {
		AG_Command existingCommand = getExistingCommand(command, existingProcess);
		if (existingCommand == null) {
			saveCommand(command, existingProcess);
		} else {
			updateCommand(command, existingCommand);
		}
	}

	private void updateAttribute(AG_Attribute attribute,
			AG_Attribute existingAttribute) {
		existingAttribute.setBeginTime(attribute.getBeginTime());
		existingAttribute.setEndTime(attribute.getEndTime());
		existingAttribute.setLastModified(attribute.getLastModified());
		existingAttribute.setProperty(attribute.getProperty());
		existingAttribute.setValue(attribute.getValue());
		
		HibernateUtil.getSession().update(existingAttribute);
	}

	private void saveAttribute(AG_Attribute attribute, AG_Worker existingWorker) {
		existingWorker.getAttributes().add(attribute);
		attribute.setWorker(existingWorker);
		HibernateUtil.getSession().save(attribute);
		HibernateUtil.getSession().update(existingWorker);
		
	}

	private AG_Attribute getExistingAttribute(AG_Attribute attribute,
			AG_Worker existingWorker) {
		Criteria criteria = HibernateUtil.getSession().createCriteria(AG_Attribute.class);
		criteria.add(Restrictions.eq("property", attribute.getProperty()));
		criteria.add(Restrictions.eq("beginTime", attribute.getBeginTime()));
		
		Criteria worker = criteria.createCriteria("worker");
		worker.add(Restrictions.eq("address", existingWorker.getAddress()));
		worker.add(Restrictions.eq("beginTime", existingWorker.getBeginTime()));
		
		Criteria peer = worker.createCriteria("peer");
		peer.add(Restrictions.eq("address", existingWorker.getPeer().getAddress()));
		
		AG_Attribute dbAttribute = (AG_Attribute) criteria.uniqueResult();
		return dbAttribute;
	}

	private void updateWorker(AG_Worker worker, AG_Worker existingWorker) {
		existingWorker.setAddress(worker.getAddress());
		existingWorker.setBeginTime(worker.getBeginTime());
		existingWorker.setEndTime(worker.getEndTime());
		existingWorker.setLastModified(worker.getLastModified());
		existingWorker.setStatus(worker.getStatus());
		existingWorker.setAllocatedFor(worker.getAllocatedFor());
		
		HibernateUtil.getSession().update(existingWorker);
	}
	
	private AG_Worker saveWorker(AG_Worker worker, AG_Peer existingPeer) {
		
		existingPeer.getWorkers().add(worker);
		worker.setPeer(existingPeer);
		HibernateUtil.getSession().save(worker);
		HibernateUtil.getSession().update(existingPeer);
		
		List<AG_Attribute> attributes = new ArrayList<AG_Attribute>(worker.getAttributes());
		worker.setAttributes(new ArrayList<AG_Attribute>());
		
		attachToExistentProcess(worker);
		
		for (AG_Attribute attribute : attributes) {
			saveAttribute(attribute, worker);
		}
		
		return worker;
	}

	private void attachToExistentProcess(AG_Worker worker) {
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(AG_GridProcess.class);
		criteria.add(Restrictions.eq("workerAddress", worker.getAddress()));
		criteria.add(Restrictions.isNull("worker"));
		
		List<AG_GridProcess> processes = criteria.list();
		
		for (AG_GridProcess process : processes) {
			process.setWorker(worker);
			HibernateUtil.getSession().saveOrUpdate(process);
		}
	}

	private AG_Worker getExistingWorker(AG_Worker worker, String peerAddress) {
		Criteria criteria = HibernateUtil.getSession().createCriteria(AG_Worker.class);
		criteria.add(Restrictions.eq("address", worker.getAddress()));
		
		Criteria peerCriteria = criteria.createCriteria("peer");
		peerCriteria.add(Restrictions.eq("address", peerAddress));
		
		return (AG_Worker) criteria.uniqueResult();
	}
	
	private void updateCommand(AG_Command command, AG_Command existingCommand) {
		
		existingCommand.setDestination(command.getDestination());
		existingCommand.setFileName(command.getFileName());
		existingCommand.setFileSize(command.getFileSize());
		existingCommand.setLastModified(command.getLastModified());
		existingCommand.setName(command.getName());
		existingCommand.setProgress(command.getProgress());
		existingCommand.setSource(command.getSource());
		existingCommand.setStatus(command.getStatus());
		existingCommand.setTransferBegin(command.getTransferBegin());
		existingCommand.setTransferEnd(command.getTransferEnd());
		existingCommand.setTransferRate(command.getTransferRate());
	
		HibernateUtil.getSession().update(existingCommand);
	}

	private void saveCommand(AG_Command command, AG_GridProcess existingProcess) {
		command.setProcess(existingProcess);
		existingProcess.getCommands().add(command);
		HibernateUtil.getSession().save(command);
		HibernateUtil.getSession().update(existingProcess);
	}

	private AG_Command getExistingCommand(AG_Command command,
			AG_GridProcess existingProcess) {
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(AG_Command.class);
		criteria.add(Restrictions.eq("transferBegin", command.getTransferBegin()));
		
		Criteria process = criteria.createCriteria("process");
		process.add(Restrictions.eq("creationTime", existingProcess.getCreationTime()));
		
		Criteria task = process.createCriteria("task");
		task.add(Restrictions.eq("sequenceNumber", existingProcess.getTask().getSequenceNumber()));
		
		Criteria job = task.createCriteria("job");
		job.add(Restrictions.eq("jobId", existingProcess.getTask().getJob().getJobId()));
		
		Criteria login = job.createCriteria("login");
		login.add(Restrictions.eq("beginTime", existingProcess.getTask().getJob().getLogin().getBeginTime()));
		
		Criteria user = login.createCriteria("user");
		user.add(Restrictions.eq("address", existingProcess.getTask().getJob().getLogin().getUser().getAddress()));
		user.add(Restrictions.eq("creationDate", existingProcess.getTask().getJob().getLogin().getUser().getCreationDate()));
		
		Criteria peer = user.createCriteria("peer");
		peer.add(Restrictions.eq("address", existingProcess.getTask().getJob().getLogin().getUser().getPeer().getAddress()));

		AG_Command dbCommand = (AG_Command) criteria.uniqueResult();
		return dbCommand;
	}

	private void updateProcess(AG_GridProcess process, AG_GridProcess existingProcess) {
		existingProcess.setCreationTime(process.getCreationTime());
		existingProcess.setErrorCause(process.getErrorCause());
		existingProcess.setExecutionErrorType(process.getExecutionErrorType());
		existingProcess.setExitValue(process.getExitValue());
		existingProcess.setFinalBeginning(process.getFinalBeginning());
		existingProcess.setFinalEnd(process.getFinalEnd());
		existingProcess.setInitBeginning(process.getInitBeginning());
		existingProcess.setInitEnd(process.getInitEnd());
		existingProcess.setLastModified(process.getLastModified());
		existingProcess.setLatestPhase(process.getLatestPhase());
		existingProcess.setRemoteBeginning(process.getRemoteBeginning());
		existingProcess.setRemoteEnd(process.getRemoteEnd());
		existingProcess.setSabotageCheck(process.getSabotageCheck());
		existingProcess.setStatus(process.getStatus());
		existingProcess.setSequenceNumber(process.getSequenceNumber());
		existingProcess.setWorkerAddress(process.getWorkerAddress());
		existingProcess.setStderr(process.getStderr());
		existingProcess.setStdout(process.getStdout());
		
		HibernateUtil.getSession().update(existingProcess);
	}

	private void saveProcess(AG_GridProcess process, AG_Task existingTask, List<AG_Worker> existingWorkers) {
		process.setTask(existingTask);
		existingTask.getProcesses().add(process);
		
		AG_Worker existingWorker = null;
		
		for (AG_Worker worker : existingWorkers) {
			if (worker.getAddress().equals(process.getWorkerAddress()))  {
				existingWorker = worker;
			}
		}
		
		process.setWorker(existingWorker);
		
		HibernateUtil.getSession().save(process);
		HibernateUtil.getSession().update(existingTask);
		
		List<AG_Command> commands = new ArrayList<AG_Command>(process.getCommands());
		process.setCommands(new ArrayList<AG_Command>());
		
		for (AG_Command command : commands) {
			saveCommand(command, process);
		}
	}

	private AG_GridProcess getExistingProcess(AG_GridProcess process,
			AG_Task existingTask) {
		Criteria criteria = HibernateUtil.getSession().createCriteria(AG_GridProcess.class, "process");
		criteria.add(Restrictions.eq("sequenceNumber",process.getSequenceNumber()));
		
		Criteria task = criteria.createCriteria("task");
		task.add(Restrictions.eq("sequenceNumber", existingTask.getSequenceNumber()));
		
		Criteria job = task.createCriteria("job");
		job.add(Restrictions.eq("jobId", existingTask.getJob().getJobId()));
		
		Criteria login = job.createCriteria("login");
		login.add(Restrictions.eq("beginTime", existingTask.getJob().getLogin().getBeginTime()));
		
		Criteria user = login.createCriteria("user");
		user.add(Restrictions.eq("address", existingTask.getJob().getLogin().getUser().getAddress()));
		user.add(Restrictions.eq("creationDate", existingTask.getJob().getLogin().getUser().getCreationDate()));
		
		Criteria peer = user.createCriteria("peer");
		peer.add(Restrictions.eq("address", existingTask.getJob().getLogin().getUser().getPeer().getAddress()));
		
		AG_GridProcess dbProcess = (AG_GridProcess) criteria.uniqueResult();
		return dbProcess;
	}

	private void updateTask(AG_Task task, AG_Task existingTask) {
		existingTask.setActualFails(task.getActualFails());
		existingTask.setLastModified(task.getLastModified());
		existingTask.setRemoteExec(task.getRemoteExec());
		existingTask.setSabotageCheck(task.getSabotageCheck());
		existingTask.setStatus(task.getStatus());
		existingTask.setSequenceNumber(task.getSequenceNumber());
		
		HibernateUtil.getSession().update(existingTask);
	}

	private void saveTask(AG_Task task, AG_Job existingJob, List<AG_Worker> existingWorkers) {
		task.setJob(existingJob);
		existingJob.getTasks().add(task);
		
		HibernateUtil.getSession().save(task);
		HibernateUtil.getSession().update(existingJob);
		
		List<AG_GridProcess> processes = new ArrayList<AG_GridProcess>(task.getProcesses());
		task.setProcesses(new ArrayList<AG_GridProcess>());
		
		for (AG_GridProcess process : processes) {
			saveProcess(process, task, existingWorkers);
		}
	}

	private AG_Task getExistingTask(AG_Task task, AG_Job existingJob) {
		Criteria criteria = HibernateUtil.getSession().createCriteria(AG_Task.class);
		criteria.add(Restrictions.eq("sequenceNumber", task.getSequenceNumber()));
		
		Criteria job = criteria.createCriteria("job");
		job.add(Restrictions.eq("jobId", existingJob.getJobId()));
		job.add(Restrictions.eq("creationTime", existingJob.getCreationTime()));
		
		Criteria login = job.createCriteria("login");
		login.add(Restrictions.eq("beginTime", existingJob.getLogin().getBeginTime()));
		
		Criteria user = login.createCriteria("user");
		user.add(Restrictions.eq("address", existingJob.getLogin().getUser().getAddress()));
		user.add(Restrictions.eq("creationDate", existingJob.getLogin().getUser().getCreationDate()));
		
		Criteria peer = user.createCriteria("peer");
		peer.add(Restrictions.eq("address", existingJob.getLogin().getUser().getPeer().getAddress()));
		
		AG_Task dbTask = (AG_Task) criteria.uniqueResult();
		return dbTask;
	}

	private void updateJob(AG_Job job, AG_Job existingJob) {
		existingJob.setCreationTime(job.getCreationTime());
		existingJob.setFinishTime(job.getFinishTime());
		existingJob.setJobId(job.getJobId());
		existingJob.setLabel(job.getLabel());
		existingJob.setLastModified(job.getLastModified());
		existingJob.setMaxFails(job.getMaxFails());
		existingJob.setMaxReplicas(job.getMaxReplicas());
		existingJob.setRequestId(job.getRequestId());
		existingJob.setRequirements(job.getRequirements());
		existingJob.setStatus(job.getStatus());
		
		HibernateUtil.getSession().update(existingJob);
	}

	private void saveJob(AG_Job job, AG_Login existingLogin, List<AG_Worker> existingWorkers) {
		job.setLogin(existingLogin);
		existingLogin.getJobs().add(job);
		
		HibernateUtil.getSession().save(job);
		HibernateUtil.getSession().update(existingLogin);
		
		List<AG_Task> tasks = new ArrayList<AG_Task>(job.getTasks());
		job.setTasks(new ArrayList<AG_Task>());
		for (AG_Task task : tasks) {
			saveTask(task, job, existingWorkers);
		}
	}

	private AG_Job getExistingJob(AG_Job job, AG_Login existingLogin) {
		Criteria criteria = HibernateUtil.getSession().createCriteria(AG_Job.class);
		criteria.add(Restrictions.eq("jobId", job.getJobId()));
		criteria.add(Restrictions.eq("creationTime", job.getCreationTime()));
		
		Criteria login = criteria.createCriteria("login");
		login.add(Restrictions.eq("beginTime", existingLogin.getBeginTime()));
		
		Criteria user = login.createCriteria("user");
		user.add(Restrictions.eq("address", existingLogin.getUser().getAddress()));
		user.add(Restrictions.eq("creationDate", existingLogin.getUser().getCreationDate()));
		
		Criteria peer = user.createCriteria("peer");
		peer.add(Restrictions.eq("address", existingLogin.getUser().getPeer().getAddress()));
		
		AG_Job dbJob = (AG_Job) criteria.uniqueResult();
		return dbJob;
	}

	private void updateLogin(AG_Login login, AG_Login existingLogin) {
		existingLogin.setBeginTime(login.getBeginTime());
		existingLogin.setEndTime(login.getEndTime());
		existingLogin.setLastModified(login.getLastModified());
		existingLogin.setLoginResult(login.getLoginResult());
		
		HibernateUtil.getSession().update(existingLogin);
	}

	private void saveLogin(AG_Login login, AG_User existingUser, List<AG_Worker> existingWorkers) {
		login.setUser(existingUser);
		existingUser.getLogins().add(login);
		
		HibernateUtil.getSession().save(login);
		HibernateUtil.getSession().update(existingUser);
		
		List<AG_Job> jobs = new ArrayList<AG_Job>(login.getJobs());
		login.setJobs(new ArrayList<AG_Job>());
		for (AG_Job job : jobs) {
			saveJob(job, login, existingWorkers);
		}
	}

	private AG_Login getExistingLogin(AG_Login login, AG_User existingUser) {
		Criteria criteria = HibernateUtil.getSession().createCriteria(AG_Login.class);
		criteria.add(Restrictions.eq("beginTime", login.getBeginTime()));
		
		Criteria user = criteria.createCriteria("user");
		user.add(Restrictions.eq("address", existingUser.getAddress()));
		user.add(Restrictions.eq("creationDate", existingUser.getCreationDate()));
		
		Criteria peer = user.createCriteria("peer");
		peer.add(Restrictions.eq("address", existingUser.getPeer().getAddress()));
		
		AG_Login dbLogin = (AG_Login) criteria.uniqueResult();
		return dbLogin;
	}

	private void updateUser(AG_User user, AG_User existingUser) {
		existingUser.setCreationDate(user.getCreationDate());
		existingUser.setDeletionDate(user.getDeletionDate());
		existingUser.setLastModified(user.getLastModified());
		existingUser.setPublicKey(user.getPublicKey());
		
		HibernateUtil.getSession().update(existingUser);
	}

	private void updatePeer(AG_Peer peer, AG_Peer existingPeer) {
		setAttributes(peer, existingPeer);
	}

	private void savePeer(AG_Peer peer) {
		List<AG_Worker> workers = new ArrayList<AG_Worker>(peer.getWorkers());
		peer.setWorkers(new ArrayList<AG_Worker>());
		
		HibernateUtil.getSession().save(peer);
		
		List<AG_Worker> existingWorkers = new ArrayList<AG_Worker>();
		
		for (AG_Worker worker : workers) {
			existingWorkers.add(saveWorker(worker, peer));
		}
		
		List<AG_User> users = new ArrayList<AG_User>(peer.getUsers());
		peer.setUsers(new ArrayList<AG_User>());
		
		for (AG_User user : users) {
			saveUser(user, peer, existingWorkers);
		}
	}
	

	private void saveUser(AG_User user, AG_Peer existingPeer, List<AG_Worker> existingWorkers) {
		user.setPeer(existingPeer);
		existingPeer.getUsers().add(user);
		HibernateUtil.getSession().save(user);
		HibernateUtil.getSession().update(existingPeer);
		
		List<AG_Login> logins = new ArrayList<AG_Login>(user.getLogins());
		user.setLogins(new ArrayList<AG_Login>());
		for (AG_Login login : logins) {
			saveLogin(login, user, existingWorkers);
		}
		
	}

	private void setAttributes(AG_Peer peer, AG_Peer existingPeer) {
		existingPeer.setDescription(peer.getDescription());
		existingPeer.setEmail(peer.getEmail());
		existingPeer.setLabel(peer.getLabel());
		existingPeer.setLastModified(peer.getLastModified());
		existingPeer.setLatitude(peer.getLatitude());
		existingPeer.setLongitude(peer.getLongitude());
		existingPeer.setTimezone(peer.getTimezone());
		existingPeer.setVersion(peer.getVersion());
		
		HibernateUtil.getSession().update(existingPeer);
	}

	private AG_Peer getExistingPeer(String peerAddress) {
		Criteria criteria = HibernateUtil.getSession().createCriteria(AG_Peer.class);
		criteria.add(Restrictions.eq("address", peerAddress));
		return (AG_Peer) criteria.uniqueResult();
	}
	
	private AG_User getExistingUser(AG_User user, String peerAddress) {
		Criteria criteria = HibernateUtil.getSession().createCriteria(AG_User.class);
		criteria.add(Restrictions.eq("address", user.getAddress()));
		criteria.add(Restrictions.eq("creationDate", user.getCreationDate()));
		
		Criteria peerCriteria = criteria.createCriteria("peer");
		peerCriteria.add(Restrictions.eq("address", peerAddress));
		
		return (AG_User) criteria.uniqueResult();
	}
	
	private boolean hasNotExistingPeerStatusChange(String peerAddress, long timeOfChange) {
		Criteria criteria = HibernateUtil.getSession().createCriteria(AG_PeerStatusChange.class);
		criteria.add(Restrictions.eq("timeOfChange", timeOfChange));
		
		Criteria peerCriteria = criteria.createCriteria("peer");
		peerCriteria.add(Restrictions.eq("address", peerAddress));
		
		return criteria.list().isEmpty();
	}
	

	/**
	 * This method save the Peers Status Changes to the database.
	 * @param statusChanges {@link List<DS_PeerStatusChange>}
	 */
	public void savePeersStatusChanges(List<DS_PeerStatusChange> statusChanges,
			List<IResponseTO> responses ) {
		
		HibernateUtil.beginTransaction();

		try {
			
			for (DS_PeerStatusChange peerStatusChange : statusChanges) {
				AG_Peer peer = getExistingPeer(peerStatusChange.getPeerAddress()); 
				AG_PeerStatusChange peerStatusChangeAG = convertPeerStatusChange(peerStatusChange);
				
				if (peer == null) {
					peer = new AG_Peer();
					peer.setAddress(peerStatusChange.getPeerAddress());
					HibernateUtil.getSession().save(peer);
				}
				
				peer.setStatus(peerStatusChange.getCurrentStatus());
				HibernateUtil.getSession().update(peer);
				peerStatusChangeAG.setPeer(peer);
				if(hasNotExistingPeerStatusChange(peerStatusChange.getPeerAddress(), 
						peerStatusChangeAG.getTimeOfChange())) {
					HibernateUtil.getSession().save(peerStatusChangeAG);
				}
			}		
			
			responses.add(new LoggerResponseTO(AggregatorControlMessages
					.getSavePeersStatusChangesSuccessfulMessage(),
					LoggerResponseTO.INFO));
			HibernateUtil.commitTransaction();
			
		} catch (Exception e) {
			responses.add(new LoggerResponseTO(AggregatorControlMessages
					.getRollbackTransactionMessage( " savePeersStatusChanges", e.getMessage()),
					LoggerResponseTO.INFO));
			HibernateUtil.rollbackTransaction();
		
		} finally {
			HibernateUtil.closeSession();
		}
		
	}
	
	private static AG_PeerStatusChange convertPeerStatusChange(DS_PeerStatusChange psc) {
		AG_PeerStatusChange agPsc = new AG_PeerStatusChange();
		
		agPsc.setCurrentStatus(psc.getCurrentStatus());
		agPsc.setLastModified(psc.getLastModified());
		agPsc.setTimeOfChange(psc.getTimeOfChange());
		agPsc.setVersion(psc.getVersion());
		
		return agPsc;
		
	}

	
	
}
