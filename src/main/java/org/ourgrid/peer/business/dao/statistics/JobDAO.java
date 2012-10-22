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
package org.ourgrid.peer.business.dao.statistics;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.ourgrid.broker.status.GridProcessStatusInfo;
import org.ourgrid.broker.status.GridProcessStatusInfoResult;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.ProcessCommand;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.statistics.beans.peer.Command;
import org.ourgrid.common.statistics.beans.peer.GridProcess;
import org.ourgrid.common.statistics.beans.peer.Job;
import org.ourgrid.common.statistics.beans.peer.Login;
import org.ourgrid.common.statistics.beans.peer.Task;
import org.ourgrid.common.statistics.beans.peer.User;
import org.ourgrid.common.statistics.beans.peer.Worker;
import org.ourgrid.common.statistics.beans.status.ExecutionStatus;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.UsersDAO;
import org.ourgrid.peer.business.util.LoggerUtil;
import org.ourgrid.peer.to.Request;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProgress;

/**
 * 
 */
public class JobDAO extends EntityDAO {
	
	public void insert(List<IResponseTO> responses, Job job) {
		responses.add(LoggerUtil.enter());
		
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(job);
		session.flush();
		
		responses.add(LoggerUtil.leave());
	}
	
	public void insert(List<IResponseTO> responses, GridProcess process) {
		responses.add(LoggerUtil.enter());
		
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(process);
		session.flush();
		
		responses.add(LoggerUtil.leave());
	}

	public void insert(List<IResponseTO> responses, Task task) {
		responses.add(LoggerUtil.enter());
		
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(task);
		session.flush();
		
		responses.add(LoggerUtil.leave());
	}

	public Task findByRequestAndSequence(List<IResponseTO> responses, long requestId, int taskSequenceNumber) {
		responses.add(LoggerUtil.enter());
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(Task.class);
		criteria.createCriteria("job").add(Restrictions.eq("requestId", requestId));
		criteria.add(Restrictions.eq("sequenceNumber", taskSequenceNumber));
		
		Task task = (Task) criteria.uniqueResult();
		
		responses.add(LoggerUtil.leave());
		return task;
	}

	public void insert(List<IResponseTO> responses, Command command) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		session.save(command);
		session.flush();
		
		responses.add(LoggerUtil.leave());
	}

	public Job findByRequestId(List<IResponseTO> responses, long requestId) {
		responses.add(LoggerUtil.enter());
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(Job.class);
		criteria.add(Restrictions.eq("requestId", requestId));
		Job result = (Job) criteria.uniqueResult();
		
		responses.add(LoggerUtil.leave());
		return result;
	}

	public void update(List<IResponseTO> responses, Job job) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		session.update(job);
		session.flush();

		responses.add(LoggerUtil.leave());
	}
	
	public void hereIsJobStats(List<IResponseTO> responses, Long requestId, JobStatusInfo jobStatusInfo) {
		Job job = findByRequestId(responses, requestId);
		
		job.setCreationTime(job.getCreationTime());
		job.setFinishTime(jobStatusInfo.getFinalizationTime());
		job.setStatus(ExecutionStatus.valueOf(JobStatusInfo.getState(jobStatusInfo.getState())));
		
		for (Task task : job.getTasks()) {
			TaskStatusInfo taskInfo = jobStatusInfo.getTaskByID(task.getSequenceNumber());
			task.setActualFails(taskInfo.getActualFails());
			task.setLastModified(taskInfo.getFinalizationTime());
			task.setStatus(ExecutionStatus.valueOf(taskInfo.getState()));
			
			List<GridProcessStatusInfo> nonInsertedProcesses = new ArrayList<GridProcessStatusInfo>(
					taskInfo.getGridProcesses());
			
			for (GridProcess gridProcess : task.getProcesses()) {
				GridProcessStatusInfo gridProcessStatusInfo = taskInfo.getReplicaByID(gridProcess.getSequenceNumber());
				gridProcess.setStatus(ExecutionStatus.valueOf(gridProcessStatusInfo.getState()));
				gridProcess.setWorkerAddress(StringUtil.deploymentIDToUserAtServer(
						gridProcessStatusInfo.getWorkerInfo().getWorkerID()));
				
				insert(responses, gridProcess);
				
				nonInsertedProcesses.remove(gridProcessStatusInfo);
			}
			
			for (GridProcessStatusInfo gridProcessInfo : nonInsertedProcesses) {
				GridProcess process = fillProcess(gridProcessInfo, task);
				insert(responses, process);
			}
			
			insert(responses, task);
		}
		
		insert(responses, job);
	}
	
	private GridProcess fillProcess(GridProcessStatusInfo gridProcessInfo, Task task) {
		GridProcess process = new GridProcess();
		process.setCreationTime(gridProcessInfo.getCreationTime());
		
		long initBeginning = gridProcessInfo.getCreationTime();
		process.setInitBeginning(initBeginning);

		GridProcessStatusInfoResult replicaResult = gridProcessInfo.getReplicaResult();
		
		if (replicaResult != null) {
			ExecutorResult executorResult = replicaResult.getExecutorResult();
			
			if (executorResult != null) {
				process.setExitValue(executorResult.getExitValue());
				process.setStderr(executorResult.getStderr());
				process.setStdout(executorResult.getStdout());
			}
			
			if (process.getExitValue() != null && process.getExitValue() != 0) {
				process.setErrorCause(replicaResult.getExecutionErrorCause());
				process.setExecutionErrorType(replicaResult.getExecutionError());
			}
			
			long initEnd = initBeginning + replicaResult.getInitDataTime();
			process.setInitEnd(initEnd);
			
			long remoteBeginning = initEnd;
			process.setRemoteBeginning(remoteBeginning);
			
			long remoteEnd = remoteBeginning + replicaResult.getRemoteDataTime();
			process.setRemoteEnd(remoteEnd);
			
			long finalBeginning = remoteEnd;
			process.setFinalBeginning(finalBeginning);
			
			long finalEnd = finalBeginning + replicaResult.getFinalDataTime();
			process.setFinalEnd(finalEnd);

			process.setSabotageCheck(replicaResult.getSabotageCheck());
		}
		
		process.setLastModified(now());
		process.setLatestPhase(gridProcessInfo.getPhase());
		
		process.setStatus(ExecutionStatus.valueOf(gridProcessInfo.getState()));
		
		process.setSequenceNumber(gridProcessInfo.getTaskId());
		process.setTask(task);
		
		process.setWorkerAddress(StringUtil.deploymentIDToUserAtServer(
				gridProcessInfo.getWorkerInfo().getWorkerID()));
		
		return process;
	}

	public void addRequest(List<IResponseTO> responses, Request request) {
		
		String userServiceID = request.getConsumer().getConsumerAddress();
		String userLogin = StringUtil.addressToUserAtServer(userServiceID);
		
		UsersDAO userDAO = PeerDAOFactory.getInstance().getUsersDAO();
		User userObj = userDAO.findByUserAtServer(responses, userLogin);
		
		if (userObj == null) {
			throw new CommuneRuntimeException("The user is not added: " + userLogin);
		}

		LoginDAO loginDAO = PeerDAOFactory.getInstance().getLoginDAO();
		Login login = loginDAO.findCurrentLogin(responses, userObj);
		
		if (login == null) {
			throw new CommuneRuntimeException("The user is not logged: " + userLogin);
		}
		
		RequestSpecification requestSpec = request.getSpecification();
		JobSpecification jobSpec = requestSpec.getJobSpecification();

		Job job = new Job();
		job.setCreationTime(now());
		job.setJobId(requestSpec.getJobId());
		job.setRequestId(requestSpec.getRequestId());
		job.setLabel(jobSpec.getLabel());
		job.setLastModified(now());
		job.setLogin(login);
		//login.getJobs().add(job);
		job.setMaxFails(requestSpec.getMaxFails());
		job.setMaxReplicas(requestSpec.getMaxReplicas());
		job.setRequirements(requestSpec.getRequirements());
		job.setStatus(ExecutionStatus.UNSTARTED);
		
		insert(responses, job);
		
		List<TaskSpecification> taskSpecs = jobSpec.getTaskSpecs();
		
		int i = 1;
		for (TaskSpecification taskSpec : taskSpecs) {
			
			Task task = new Task();
			task.setSequenceNumber(i++);				
			task.setActualFails(0);
			task.setJob(job);
			//job.getTasks().add(task);
			task.setLastModified(now());
			task.setRemoteExec(taskSpec.getRemoteExec());
			task.setSabotageCheck(taskSpec.getSabotageCheck());
			task.setStatus(ExecutionStatus.UNSTARTED);

			insert(responses, task);
		}
		
	}
	
	public void addProcessAccounting(List<IResponseTO> responses, GridProcessAccounting replicaAccounting) {

		long requestId = replicaAccounting.getRequestId();
		int taskSequenceNumber = replicaAccounting.getTaskSequenceNumber();
		
		WorkerDAO workerDAO = PeerDAOFactory.getInstance().getWorkerDAO();
		
		Task task = findByRequestAndSequence(responses, requestId, taskSequenceNumber);
		if (task == null) {
			throw new CommuneRuntimeException("Task not found. Request id: " + requestId + ", Task number: " +
					taskSequenceNumber);
		}
		
		String workerServiceID = replicaAccounting.getWorkerID();
		
		Worker worker = workerDAO.findActiveWorker(responses, StringUtil.addressToUserAtServer(workerServiceID));
		
		if (task.getJob().getStatus().equals(ExecutionStatus.UNSTARTED)) {
			task.getJob().setStatus(ExecutionStatus.RUNNING);
		}

		GridProcess process = new GridProcess();
		process.setCreationTime(replicaAccounting.getCreationTime());

		process.setExitValue(replicaAccounting.getExitValue());
		if (process.getExitValue() != null && process.getExitValue() != 0) {
			process.setErrorCause(replicaAccounting.getErrorCause());
			process.setExecutionErrorType(replicaAccounting.getExecutionErrorType());
		}

		process.setInitBeginning(replicaAccounting.getInitBeginning());
		process.setInitEnd(replicaAccounting.getInitEnd());
		process.setRemoteBeginning(replicaAccounting.getRemoteBeginning());
		process.setRemoteEnd(replicaAccounting.getRemoteEnd());
		process.setFinalBeginning(replicaAccounting.getFinalBeginning());
		process.setFinalEnd(replicaAccounting.getFinalEnd());
		
		process.setLastModified(now());
		process.setLatestPhase(replicaAccounting.getLatestPhase());
		process.setSabotageCheck(replicaAccounting.getSabotageCheck());
		
		GridProcessState state = replicaAccounting.getState();
		if (GridProcessState.ABORTED.equals(state)) {
			process.setStatus(ExecutionStatus.ABORTED);
		} else if (GridProcessState.CANCELLED.equals(state)) {
			process.setStatus(ExecutionStatus.CANCELLED);
		} else if (GridProcessState.FAILED.equals(state)) {
			process.setStatus(ExecutionStatus.FAILED);
		} else if (GridProcessState.FINISHED.equals(state)) {
			process.setStatus(ExecutionStatus.FINISHED);
		} else if (GridProcessState.RUNNING.equals(state)) {
			process.setStatus(ExecutionStatus.RUNNING);
		} else if (GridProcessState.SABOTAGED.equals(state)) {
			process.setStatus(ExecutionStatus.SABOTAGED);
		} else if (GridProcessState.UNSTARTED.equals(state)) {
			process.setStatus(ExecutionStatus.UNSTARTED);
		}
		
		process.setSequenceNumber(replicaAccounting.getGridProcessSequenceNumber());
		process.setStderr(replicaAccounting.getStderr());
		process.setStdout(replicaAccounting.getStdout());
		process.setTask(task);
		//task.getProcesses().add(process);
		
		process.setWorkerAddress(replicaAccounting.getWorkerSpec().getUserAndServer());
		process.setWorker(worker);
		
		insert(responses, process);
		
		
		List<ProcessCommand> initCommands = replicaAccounting.getInitCommands();
		
		if (initCommands != null) {
			
			for (ProcessCommand pCommand : initCommands) {
				insertCommand(responses, replicaAccounting, pCommand, process);
			}
		}
		
		
		List<ProcessCommand> finalCommands = replicaAccounting.getFinalCommands();
		
		if (finalCommands != null) {
			
			for (ProcessCommand pCommand : finalCommands) {
				insertCommand(responses, replicaAccounting, pCommand, process);
			}
		}
	}
	
	private void insertCommand(List<IResponseTO> responses, GridProcessAccounting replicaAccounting, ProcessCommand pCommand, GridProcess process) {
		
		TransferProgress progress = replicaAccounting.getTransferProgress(pCommand.getHandle());
		
		if (pCommand != null) {
			Command command = new Command();
			command.setDestination(pCommand.getDestination());
			
			command.setFileName(pCommand.getFileName());
			command.setFileSize(pCommand.getFileSize());
			
			if (progress != null) {
				command.setProgress(progress.getProgress());
				command.setStatus(progress.getNewStatus().toString());
				double tRate = progress.getAmountWritten() / (pCommand.getTransferEnd() - pCommand.getTransferBegin());
				command.setTransferRate(tRate);
			}
			
			command.setTransferBegin(pCommand.getTransferBegin());
			command.setTransferEnd(pCommand.getTransferEnd());
			command.setProcess(process);
			command.setLastModified(now());
			command.setName(pCommand.getName());
			command.setSource(pCommand.getSource());
			
			insert(responses, command);
		}	
	}
	
	public void finishRequest(List<IResponseTO> responses, Request request, boolean clientFailure) {
		
		
		Job job = findByRequestId(responses, request.getSpecification().getRequestId());
		
		job.setLastModified(now());
		
		if (clientFailure) {
			job.setStatus(ExecutionStatus.FAILED);
		
		} else {
			job.setStatus(ExecutionStatus.FINISHED);
			job.setFinishTime(now());
		}
		
		update(responses, job);
		
	}

}
