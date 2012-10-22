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
package org.ourgrid.broker.business.scheduler.workqueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.Request;
import org.ourgrid.broker.business.dao.WorkerDAO;
import org.ourgrid.broker.business.dao.WorkerEntry;
import org.ourgrid.broker.business.scheduler.IStateMachine;
import org.ourgrid.broker.business.scheduler.extensions.GenericTransferProgress;
import org.ourgrid.broker.business.scheduler.workqueue.statemachine.WorkqueueStateMachine;
import org.ourgrid.broker.response.DisposeWorkerResponseTO;
import org.ourgrid.broker.response.FinishRequestResponseTO;
import org.ourgrid.broker.response.JobEndedResponseTO;
import org.ourgrid.broker.response.LWPHereIsJobStatsResponseTO;
import org.ourgrid.broker.response.ReportReplicaAccountingResponseTO;
import org.ourgrid.broker.response.ResumeRequestResponseTO;
import org.ourgrid.broker.response.ScheduleActionToRunOnceResponseTO;
import org.ourgrid.broker.response.UnwantWorkerResponseTO;
import org.ourgrid.broker.response.to.GetOperationTO;
import org.ourgrid.broker.response.to.InitOperationTO;
import org.ourgrid.broker.response.to.PeerBalanceTO;
import org.ourgrid.broker.response.to.TransferProgressTO;
import org.ourgrid.broker.status.GridProcessStatusInfo;
import org.ourgrid.broker.status.GridProcessStatusInfoResult;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.broker.status.JobWorkerStatus;
import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.broker.status.WorkerStatusInfo;
import org.ourgrid.broker.util.RandomRequestIDGenerator;
import org.ourgrid.broker.util.RequestIDGenerator;
import org.ourgrid.broker.util.UtilConverter;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.GridProcessExecutionResult;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.GridProcessPhasesData;
import org.ourgrid.common.interfaces.to.GridProcessResultInfo;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.interfaces.to.ProcessCommand;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.job.GridProcess;
import org.ourgrid.common.job.IllegalResultException;
import org.ourgrid.common.job.Job;
import org.ourgrid.common.job.Task;
import org.ourgrid.common.replicaexecutor.SabotageCheckResult;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecificationAnnotationsComparator;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.worker.business.controller.GridProcessError;

import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProgress;

/**
 *
 */
public class WorkQueueExecutionController {

	
	
	private int maxReplicas;

	private int maxFails;
	
	private int maxBlFails;
	
	private Set<WorkerEntry> saboteurs;
	
	private IStateMachine stateMachine;
	
	private RequestIDGenerator requestIDGenerator;
	
	public WorkQueueExecutionController(int maxReplicas, int maxFails, int maxBlFails) {
		this.maxReplicas = maxReplicas;
		this.maxFails = maxFails;
		this.maxBlFails = maxBlFails;
		this.requestIDGenerator = new RandomRequestIDGenerator();
		this.stateMachine = new WorkqueueStateMachine(this);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.heuristic.Heuristic#schedule(org.ourgrid.common.job.Job, java.util.Collection)
	 */
	public void schedule(Job job) {
		boolean scheduling = true;
		
		while (scheduling) {
			
			scheduling = false;
			//Collection<Task> tasksToScheduleQueue = job.getAvailableTasks();
			
			Collection<Task> tasks = job.getTasks();
			
			for (Task task : tasks) {
				
				if (canSchedule(task)) {
					WorkerEntry chosenWorker = null; 
														
					//TODO it should have a much better design... It's time and space consuming.
					Collection< WorkerEntry > availableWorkers = 
						job.getAvailableWorkers( new WorkerEntryComparator(job.getSpec().getAnnotations()) );
                    
					if (!availableWorkers.isEmpty()) {
						chosenWorker = availableWorkers.iterator().next();
						if (createAndAllocateExecution(job, task, chosenWorker)) {
							scheduling = true;
						}
					}
				}
			}
		}
	}

	private boolean createAndAllocateExecution(Job job, Task task, WorkerEntry chosenWorker) {
		
		GridProcess replica = null;
		if (canReplicate(task)) {
			replica = job.createAndAllocateExecution(task.getTaskid(), chosenWorker);
			replica.setRunningState(stateMachine.getInitialState());
		}	

		if (replica != null) {
			chosenWorker.allocate(replica);
			
			WorkerEntry worker = 
				WorkerInfo.getInstance().getWorker(chosenWorker.getServiceID().getContainerID().toString());
			worker.allocate(replica);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifies if new <code>Replica</code>s can be scheduled for this
	 * 
	 * @return <code>true</code> if it can.
	 */
	public boolean canSchedule(Task task) {
		
		if (!verifyTaskCanBeProcessed(task)) {
			return false;
		}
		
		if (!task.hasRunnigGridProcess()) {
			return true;
		}
		
		if (task.getState().equals(GridProcessState.RUNNING) &&  task.getJob().hasUnallocatedTasks()) {
			return false;
		}

		return verifyRunningProccess(task);
	}
	
	/**
	 * Verifies if new <code>Replica</code>s can be created for this
	 * <code>Task</code>. One can be created if the task is still in the
	 * <code>RUNNING</code> state and the current number of running
	 * <code>Replica</code>s is lesser than the <code>maxreplicas</code>
	 * defined for this <code>Task</code>.
	 * 
	 * @return <code>true</code> if it can.
	 */
	public boolean canReplicate(Task task) {
		
		if (!verifyTaskCanBeProcessed(task)) {
			return false;
		}

		return verifyRunningProccess(task);
	}
	
	private boolean verifyRunningProccess(Task task) {
		
		int running = 0;
		
		for (GridProcess replica : task.getGridProcesses()) {
			GridProcessState replicaState = replica.getState();

			if ( GridProcessState.RUNNING.equals( replicaState ) || GridProcessState.UNSTARTED.equals( replicaState ) ) {
				running++;
			}
		}
		
		return running < maxReplicas
			&& (GridProcessState.RUNNING.equals( task.getState() ) || GridProcessState.UNSTARTED.equals( task.getState() ));
		
	}
	
	private boolean verifyTaskCanBeProcessed(Task task) {
		
		if ( task.getActualFails() >= maxFails || 
				task.hasFinishedReplica() || GridProcessState.CANCELLED.equals( task.getState() ) ) {
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.heuristic.Heuristic#createRequestSpec(long, org.ourgrid.common.spec.job.JobSpec)
	 */
	public RequestSpecification createRequestSpec(int jobId, JobSpecification jobSpec) {
		String requirements = jobSpec.getRequirements();
		int numberOfTasks = jobSpec.getTaskSpecs().size();
		int numberOfWorkers = numberOfTasks * this.maxReplicas;
		
		long requestID = this.requestIDGenerator.nextRequestID();
		
		return new RequestSpecification(jobId, jobSpec, requestID, requirements, numberOfWorkers, maxFails, maxReplicas);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.heuristic.Heuristic#verifyFailure(org.ourgrid.common.job.Task)
	 */
	public boolean verifyFailure(Task task, GridProcessState state) {
		
		int actualFails = task.getActualFails();
		
		if (state.equals(GridProcessState.FAILED)) {
			actualFails++;
		}
		
		return actualFails >= maxFails && !task.hasGridProcessInExecution();
	}

	public void executionFinished(GridProcess execution, List<IResponseTO> responses) {
		
		reportReplicaAccounting(execution, responses);
		
		if ( !hasAnySisterGridProcessFinished( execution ) ) {
			executionEnded(execution, GridProcessState.FINISHED, responses);
		} else {
			executionAborted(execution, responses);
		}
	}

	private void reportReplicaAccounting(GridProcess process, List<IResponseTO> responses) {
		
		GridProcessAccounting accounting = setAccountingFields(process);
		accounting.setTransfersProgress(convertTransfer(process.getTransfersProgress()));
		String peerID = process.getWorkerProviderID();
		
		String peerAddress = StringUtil.deploymentIDToAddress(peerID);
		
		ReportReplicaAccountingResponseTO to = new ReportReplicaAccountingResponseTO();
		to.setCreationTime(accounting.getCreationTime());
		to.setErrorCause(accounting.getErrorCause());
		to.setExecutionErrorType(accounting.getExecutionErrorType());
		to.setExitValue(accounting.getExitValue());
		to.setFinalBeginning(accounting.getFinalBeginning());
		to.setFinalEnd(accounting.getFinalEnd());
		to.setInitBeginning(accounting.getInitBeginning());
		to.setInitEnd(accounting.getInitEnd());
		to.setJobID(process.getJobId());
		
		to.setLatestPhase(accounting.getLatestPhase());
		to.setMaxFails(accounting.getMaxFails());
		to.setMaxReplicas(accounting.getMaxReplicas());
		to.setPeerAddress(peerAddress);
		to.setRemoteBeginning(accounting.getRemoteBeginning());
		to.setRemoteEnd(accounting.getRemoteEnd());
		to.setRequestID(accounting.getRequestId());
		to.setRequiredWorkers(accounting.getRequiredWorkers());
		to.setSabotageCheck(accounting.getSabotageCheck());
		to.setState(accounting.getState().name());
		to.setStderr(accounting.getStderr());
		to.setStdout(accounting.getStdout());
		to.setTaskSequenceNumber(accounting.getTaskSequenceNumber());
		to.setGridProcessSequenceNumber(accounting.getGridProcessSequenceNumber());
		to.setWorkerID(accounting.getWorkerID());
		to.setWorkerPK(accounting.getWorkerPublicKey());
		
		String workerAddress = StringUtil.deploymentIDToAddress(accounting.getWorkerID());
		WorkerSpecification workerSpec = BrokerDAOFactory.getInstance().getWorkerDAO().getWorkerSpec(workerAddress);
		to.setWorkerSpec(workerSpec);
		
		to.setGetOperationsList(fillFinalGetOperations(accounting.getFinalCommands(), process.getTask(), process.getId(), process.getWorkerEntry().getWorkerID(), accounting.getRequestId()));
		to.setInitOperationsList(fillInitGetOperations(accounting.getInitCommands(), process.getTask(), process.getId(), process.getWorkerEntry().getWorkerID(), accounting.getRequestId()));
		to.setPeerBalancesList(fillPeerBalances(accounting.getAccountings().getBalances()));
		to.setTransferProgressList(fillTransferProgress(accounting.getTransfersProgress(), "" + process.getId()));
		
		responses.add(to);
	}
	
	
	public void executionAborted(GridProcess execution, List<IResponseTO> responses) {
	
		executionEnded( execution, GridProcessState.ABORTED, responses);
	}

	public void executionCancelled(GridProcess execution, List<IResponseTO> responses) {
		reportReplicaAccounting(execution, responses);
		
		updateExecution( execution, GridProcessState.CANCELLED, responses);
	}
	
	private void executionEnded(GridProcess execution, GridProcessState state, List<IResponseTO> responses) {

		updateExecution(execution, state, responses);
		
		if (hasJobEnded(execution.getJob())) {
			finishJob(execution.getJob(), responses);
		}
		
		updateScheduler(responses);
	}

	private void updateExecution(GridProcess execution, GridProcessState state, List<IResponseTO> responses) {
		try {
			execution.getJob().newReplicaResult(execution.getResult(), state, verifyFailure(execution.getTask(),
					state), canReplicate(execution.getTask()));
		} catch ( IllegalResultException e ) {
			
			responses.add(new LoggerResponseTO("Illegal result on replicaEnded: " + e.getMessage(), LoggerResponseTO.ERROR));

		}
		
		if ( state.equals( GridProcessState.FINISHED ) ) {
			abortReplicaSisters(execution, responses);
		}
		
		WorkerEntry workerEntry = execution.getWorkerEntry();
		workerEntry.deallocate();
		
		if (!isWorkerNeeded(workerEntry, execution)) {
			disposeWorker(workerEntry, responses);
		}
	}

	public void finishJob(Job job, List<IResponseTO> responses) {
		sendStats(job,responses);
		
		for (WorkerEntry workerEntry : job.getDeallocatedWorkerEntries()) {
			disposeWorker(workerEntry, responses);
		}
		
		notifyJobEnding(job, responses);
		finishRequests(job, responses);
	}

	private void finishRequests(Job job, List<IResponseTO> responses) {
		
		RequestSpecification spec = null;
		for (Request request : job.getRequests()) {
			spec = request.getSpecification();
			String peerAddress = StringUtil.deploymentIDToAddress(request.getPeerID());
			
			FinishRequestResponseTO to = new FinishRequestResponseTO();
			to.setJobID(Integer.parseInt(""+spec.getJobId()));
			to.setJobSpec(spec.getJobSpecification());
			to.setMaxFails(spec.getMaxFails());
			to.setMaxReplicas(spec.getMaxReplicas());
			to.setPeerAddress(peerAddress);
			to.setRequestID(spec.getRequestId());
			to.setRequiredWorkers(spec.getRequiredWorkers());
			
			responses.add(to);
		}
		
		job.finishRequests();
	}

	private void notifyJobEnding(Job job, List<IResponseTO> responses) {

		int jobId = job.getJobId();
		
		Set<String> interestedSet = JobInfo.getInstance().getInterested(jobId);
		
		if (interestedSet != null) {
			
			for (String interestedID : interestedSet) {
				int state = UtilConverter.getJobState(job.getState());
				JobEndedResponseTO to = new JobEndedResponseTO();
				to.setInterestedID(interestedID);
				to.setJobID(jobId);
				to.setState(state);
				responses.add(to);
			}
		}
	}

	public JobWorkerStatus getCompleteStatus() {
		
		Map<Integer, Job> jobsMap = JobInfo.getInstance().getJobs();
		Map<Integer, Set<WorkerEntry>> workersByJob = CommonUtils.createMap(); 
		
		JobStatusInfo jobInfo = null;
		List<TaskStatusInfo> tasksList = null;
		Map<Integer, JobStatusInfo> jobs = CommonUtils.createSerializableMap();
		
		//Jobs
		for (Job job : jobsMap.values()) {
				Set<WorkerEntry> workers = new LinkedHashSet<WorkerEntry>();
				tasksList = new ArrayList<TaskStatusInfo>();
	
				for (Task task : job.getTasks()) {
					tasksList.add(fillTask(task));
					for (GridProcess gridProcess : task.getGridProcesses()) {
						if (gridProcess.getState() == GridProcessState.RUNNING)
							workers.add(gridProcess.getWorkerEntry());
					}
				}
				
				jobInfo = new JobStatusInfo(job.getJobId(), job.getSpec(), UtilConverter.getJobState(job.getState()),
						tasksList, job.getCreationTime(), job.getFinalizationTime());
				
				jobs.put(jobInfo.getJobId(), jobInfo);
				
				if(job.isRunning()){
					workersByJob.put(job.getJobId(), workers);
				}
		}
		
		Map<Integer, WorkerStatusInfo[]> workers = CommonUtils.createSerializableMap();
		
		WorkerStatusInfo[] workerList = null;
		for (Entry<Integer, Set<WorkerEntry>> entry : workersByJob.entrySet()) {
			
			workerList = workers.get(entry.getKey());
			if (workerList == null) {
				workerList = new WorkerStatusInfo[entry.getValue().size()];
				workers.put(entry.getKey(), workerList);
			}
			
			int i = 0;
			for (WorkerEntry workerEntry : entry.getValue()) {
				
				GridProcessHandle handle = null;
				String state = null;
				
				if (workerEntry.getGridProcess() != null) {
					handle = workerEntry.getGridProcess().getHandle();
					state = workerEntry.getGridProcess().getState().toString();
				}
				
				workerList[i] = new WorkerStatusInfo(workerEntry.getWorkerSpecification(), 
						handle, workerEntry.getWorkerID(), state);
				i++;
			}
		}
		
		JobWorkerStatus status = new JobWorkerStatus(jobs, workers);
		
		return status;
	}
	
	private TaskStatusInfo fillTask(Task task) {
		
		List<GridProcessStatusInfo> processesList = new ArrayList<GridProcessStatusInfo>();
		
		for (GridProcess process : task.getGridProcesses()) {
			processesList.add(fillProcess(process));
		}
		
		TaskStatusInfo taskInfo = new TaskStatusInfo(task.getTaskid(), task.getJobId(), 
				task.getState().toString(),
				task.getActualFails(), task.getSpec(), processesList, task.getCreationTime(),
				task.getFinalizationTime());
		
		return taskInfo;
	}
	
	
	private GridProcessStatusInfo fillProcess(GridProcess process) {
		
		WorkerStatusInfo workerInfo = 
			new WorkerStatusInfo(process.getWorkerEntry().getWorkerSpecification(), process.getHandle(),
					process.getWorkerEntry().getWorkerID(), process.getState().toString());
		
		GridProcessStatusInfoResult result = null;
		
		if (process.getResult() != null) {
			
			String error = "";
			String errorCause = null;
			
			GridProcessError executionError = process.getResult().getExecutionError();
			if (executionError != null) {
				error = executionError.getType().getName();
				
				if (executionError.getErrorCause() != null) {
					errorCause = executionError.getErrorCause().getMessage();
				}
			}
			
			result = new GridProcessStatusInfoResult(error, errorCause,
					process.getResult().getInitData().getElapsedTimeInMillis(),
					process.getResult().getRemoteData().getElapsedTimeInMillis(),
					process.getResult().getFinalData().getElapsedTimeInMillis(),
					process.getResult().getExecutorResult());
			
			SabotageCheckResult sabotageCheckResult = process.getResult().getSabotageCheckResult();
			if (sabotageCheckResult != null) {
				result.setSabotageCheck(sabotageCheckResult.toString());
			}
		}
		
		GridProcessStatusInfo info =  new GridProcessStatusInfo(process.getId(), process.getTaskId(), 
				process.getJobId(),
				process.getState().toString(), process.getCurrentPhase().toString(),
				workerInfo, result, process.getHandle());
		
		info.setCreationTime(process.getCreationTime());
		info.setFinalizationTime(process.getFinalizationTime());
		
		return info;
	}
	
	private void sendStats(Job job, List<IResponseTO> responses) {
		List<TaskStatusInfo> tasksList = new ArrayList<TaskStatusInfo>();
		
		for (Task task : job.getTasks()) {
			tasksList.add(fillTask(task));
		}
		
		JobStatusInfo jobInfo = new JobStatusInfo(
				job.getJobId(), job.getSpec(), UtilConverter.getJobState(job.getState()),
				tasksList, job.getCreationTime(), job.getFinalizationTime());
		
		Map<String, Long> peersIdToReport = new TreeMap<String, Long>();
		
		for (Request request : job.getRequests()) {
			peersIdToReport.put(request.getPeerID(), request.getSpecification().getRequestId());
		}
		
		jobInfo.setPeersToRequests(peersIdToReport);
		
		LWPHereIsJobStatsResponseTO response = new LWPHereIsJobStatsResponseTO();
		response.setJobStatusInfo(jobInfo);
		
		responses.add(response);

		
	}

	private boolean hasJobEnded(Job job) {
		GridProcessState state = job.getState();
		
		return state.equals(GridProcessState.FAILED) || 
			state.equals(GridProcessState.CANCELLED) || state.equals(GridProcessState.FINISHED);
	}

	private boolean isWorkerNeeded(WorkerEntry workerEntry, GridProcess execution) {
		Job job = execution.getJob();
		
		return isWorkerBlacklistedForEntireJob(workerEntry, job)
			|| isJobSatisfied(job) ? false : true;
	}
	
	private void abortReplicaSisters(GridProcess execution, List<IResponseTO> responses) {

		for (GridProcess sisterGridProcess : execution.getTask().getGridProcesses()) {
			if (!sisterGridProcess.equals(execution) && sisterGridProcess.getState().isRunnable()) {
				abort(sisterGridProcess, responses);
			}
		}
	}

	private boolean hasAnySisterGridProcessFinished(GridProcess execution) {
		for (GridProcess sisterGridProcess : execution.getTask().getGridProcesses()) {
			if (!sisterGridProcess.equals(execution) && sisterGridProcess.getState().equals( GridProcessState.FINISHED )) {
				return true;
			}
		}
		return false;
	}

	public void executionFailed(GridProcess execution, List<IResponseTO> responses) {
		
		reportReplicaAccounting(execution, responses);
		
		Job job = execution.getJob();
		GridProcessExecutionResult executionResult = execution.getResult();
		
		try {
			job.newReplicaResult( executionResult, GridProcessState.FAILED, verifyFailure(execution.getTask(),
					GridProcessState.FAILED), canReplicate(execution.getTask()) );
		} catch ( IllegalResultException e ) {
			
			responses.add(new LoggerResponseTO("Illegal result on replica " + execution.getState() + " : " + e.getMessage(), LoggerResponseTO.ERROR));
		}
		
		GridProcessHandle handle = executionResult.getReplicaHandle();
		
		WorkerEntry workerEntry = execution.getWorkerEntry();
		workerEntry.deallocate();
		
		GridProcessErrorTypes type = null;
		
		if (executionResult != null && executionResult.getExecutionError() != null) {
			type = executionResult.getExecutionError().getType();
		} 
		
		boolean enteredTaskBlacklist = executionFailedOnWorker( workerEntry, 
				type, execution, responses );
		
		if ( enteredTaskBlacklist ) {
			if ( !isWorkerNeeded(workerEntry, execution) ) {
				unwantWorker(job, workerEntry, responses);
			}
		} else {
			disposeWorker(workerEntry, responses);
		}
		
		boolean hasJobEnded = hasJobEnded( job );
		
		String executorMsg = "";
		
		if (executionResult != null && executionResult.getExecutionError() != null
				&& executionResult.getExecutionError().getErrorCause() != null) {
			executorMsg = executionResult.getExecutionError().getErrorCause().toString();
		}
		
		responses.add(new LoggerResponseTO("Grid process " + execution.getState() + " " + handle + ". Job ended: " + hasJobEnded
				+ " " + executorMsg + ".", LoggerResponseTO.DEBUG));
		
		if ( hasJobEnded ) {
			finishJob(execution.getJob(), responses);
		}
		
		if ( !isJobSatisfied(job) && !hasJobEnded ) {
			
			Request request = execution.getJob().getRequest(workerEntry.getRequestID());
			if (request != null) {
				request.setPaused(false);
			}
			
			ResumeRequestResponseTO to = new ResumeRequestResponseTO();
			
			to.setPeerAddress(StringUtil.deploymentIDToAddress(workerEntry.getPeerID()));
			to.setRequestID(workerEntry.getRequestID());
			
			responses.add(to);
		}
		
		updateScheduler(responses);
	}

	public void updateScheduler(List<IResponseTO> responses) {
		
		responses.add(new ScheduleActionToRunOnceResponseTO());
	}
	
	public void disposeWorker(WorkerEntry workerEntry, List<IResponseTO> responses) {
		ServiceID serviceID = workerEntry.getServiceID();
		
		responses.add(new LoggerResponseTO("Worker dispose: " + serviceID, LoggerResponseTO.DEBUG));

		workerEntry.dispose();
		
		WorkerDAO workerDAO = BrokerDAOFactory.getInstance().getWorkerDAO();
		String wAddress = StringUtil.deploymentIDToAddress(workerEntry.getWorkerID());
		
		DisposeWorkerResponseTO disposeTO = new DisposeWorkerResponseTO();
		disposeTO.setPeerAddress(StringUtil.deploymentIDToAddress(workerEntry.getPeerID()));
		disposeTO.setWorkerAddress(wAddress);
		disposeTO.setWorkerPublicKey(workerDAO.getWorkerPublicKey(wAddress));
		responses.add(disposeTO);
		
		responses.add(new LoggerResponseTO("Stub to be released: " + wAddress, LoggerResponseTO.INFO));
		
		ReleaseResponseTO releaseTO = new ReleaseResponseTO();
		releaseTO.setStubAddress(wAddress);
		responses.add(releaseTO);
		
		workerDAO.removeWorker(wAddress);
		WorkerInfo.getInstance().removeWorker(workerEntry.getServiceID().getContainerID().toString());
	}

	/**
	 * @param job
	 * @param workerEntry
	 */
	private void unwantWorker(Job job, WorkerEntry workerEntry, List<IResponseTO> responses) {
		
		
		responses.add(new LoggerResponseTO("Worker unwanted: " + workerEntry.getWorkerID(), LoggerResponseTO.DEBUG));
		
		job.unwantWorker(workerEntry);
		workerEntry.unwant();
		
		RequestSpecification spec = workerEntry.getRequestSpecification();
		
		String peerAddress = StringUtil.deploymentIDToAddress(workerEntry.getPeerID());
		String workerAddress = StringUtil.deploymentIDToAddress(workerEntry.getWorkerID());
		Integer jobId = Integer.parseInt("" + spec.getJobId());
		
		JobSpecification jobSpec = BrokerDAOFactory.getInstance().getJobDAO().getJobSpec(jobId);

		UnwantWorkerResponseTO unwantWorkerTO = new UnwantWorkerResponseTO();
		unwantWorkerTO.setJobID(jobId);
		unwantWorkerTO.setJobSpec(jobSpec);
		unwantWorkerTO.setMaxFails(spec.getMaxFails());
		unwantWorkerTO.setMaxReplicas(spec.getMaxReplicas());
		unwantWorkerTO.setPeerAddress(peerAddress);
		unwantWorkerTO.setRequestID(spec.getRequestId());
		unwantWorkerTO.setRequiredWorkers(spec.getRequiredWorkers());
		
		unwantWorkerTO.setWorkerAddress(workerAddress);
		
		WorkerDAO workerDAO = BrokerDAOFactory.getInstance().getWorkerDAO();
		
		String workerPublicKey = workerDAO.getWorkerPublicKey(workerAddress);
		unwantWorkerTO.setWorkerPublicKey(workerPublicKey);
		
		responses.add(unwantWorkerTO);
		
		ReleaseResponseTO releaseTO = new ReleaseResponseTO();
		releaseTO.setStubAddress(workerAddress);
		
		responses.add(releaseTO);
		
		workerDAO.removeWorker(workerAddress);
		
		WorkerInfo.getInstance().removeWorker(workerEntry.getServiceID().getContainerID().toString());
		
	}

	public boolean executionFailedOnWorker(WorkerEntry workerEntry, GridProcessErrorTypes type, GridProcess execution, List<IResponseTO> responses) {
		
		if (workerEntry != null && type != null) {
			if (type.blackListError()) {
				
				int taskid = execution.getTaskId();

				responses.add(new LoggerResponseTO("Adding to blacklist. Task: " + taskid + 
						", Worker: " + workerEntry.getWorkerID(), LoggerResponseTO.DEBUG));
				
				workerEntry.addBlacklistedTask(taskid);
				
				//a sabotage error causes a immediately job blacklist entry
				if(type.equals(GridProcessErrorTypes.SABOTAGE_ERROR)) {
					saboteurs.add(workerEntry);
				}
				return true;
			}
		}
		return false;
	}

	private void abort(GridProcess gridProcess, List<IResponseTO> responses) {
		if ( gridProcess.getState().isRunnable() ) {
			gridProcess.setGridProcessState(GridProcessState.ABORTED);
			gridProcess.getReplicaAccounting().setState(GridProcessState.ABORTED);

			GridProcessAccounting accounting = setAccountingFields(gridProcess);
			accounting.setTransfersProgress(convertTransfer(gridProcess.getTransfersProgress()));
			
			reportReplicaAccounting(gridProcess, responses);
			
			gridProcess.getOperations().cancelOperations(responses);
			
			executionEnded(gridProcess, GridProcessState.ABORTED, responses);
		}
	}
	
	private List<TransferProgressTO> fillTransferProgress(Map<TransferHandle, TransferProgress> map, String id) {
		List<TransferProgressTO> transferProgressList = new ArrayList<TransferProgressTO>();
		
		for (TransferHandle handle : map.keySet()) {
			
			TransferProgress progress = map.get(handle);

			TransferProgressTO to = new TransferProgressTO();
			to.setAmountWritten(progress.getAmountWritten());
			to.setDescription(handle.getDescription());
			to.setFileSize(handle.getFileSize());
			to.setHandleID(handle.getId());
			to.setId(id);
			to.setLocalFileName(progress.getFileName());
			to.setNewStatus(progress.getNewStatus().name());
			to.setOutgoing(progress.isOutgoing());
			to.setProgress(progress.getProgress());
			to.setTransferRate(progress.getTransferRate());
			
			transferProgressList.add(to);
		}
		
		return transferProgressList;
	}

	private List<PeerBalanceTO> fillPeerBalances(Map<String, Double> balances) {
		List<PeerBalanceTO> peerBalancesList = new ArrayList<PeerBalanceTO>();
		Iterator<String> balanceIterator = balances.keySet().iterator();
		while (balanceIterator.hasNext()) {
			String property = balanceIterator.next();

			PeerBalanceTO to = new PeerBalanceTO();
			to.setProperty(property);
			to.setValue(balances.get(property));
			
			peerBalancesList.add(to);
		}
		
		return peerBalancesList;
	}

	private List<InitOperationTO> fillInitGetOperations(List<ProcessCommand> list, Task task, int processId, String workerID, long requestId) {
		List<InitOperationTO> getOperationsList = new ArrayList<InitOperationTO>();
		
		for(ProcessCommand process : list) {

			InitOperationTO to = new InitOperationTO();
			to.setEndTime(process.getTransferEnd());
			to.setInitTime(process.getTransferBegin());
			to.setJobID(task.getJobId());
			to.setLocalFilePath(process.getFileName());
			to.setProcessID(processId);
			to.setRemoteFilePath(process.getDestination());
			to.setRequestID2(requestId);
			to.setTaskID(task.getTaskid());
			to.setTransferDescription(process.getHandle().getDescription());
			to.setWorkerID2(workerID);
			to.setFileSize(process.getFileSize());
			
			getOperationsList.add(to);
		}
		
		return getOperationsList;
	}
	
	private List<GetOperationTO> fillFinalGetOperations(List<ProcessCommand> list, Task task, int processId, String workerID, long requestId) {
		List<GetOperationTO> getOperationsList = new ArrayList<GetOperationTO>();
		
		for(ProcessCommand process : list) {

			GetOperationTO to = new GetOperationTO();
			to.setEndTime(process.getTransferEnd());
			to.setInitTime(process.getTransferBegin());
			to.setJobID(task.getJobId());
			to.setLocalFilePath(process.getFileName());
			to.setProcessID(processId);
			to.setRemoteFilePath(process.getDestination());
			to.setRequestID2(requestId);
			to.setTaskID(task.getTaskid());
			to.setTransferDescription(process.getHandle().getDescription());
			to.setWorkerID2(workerID);
			to.setFileSize(process.getFileSize());
			
			getOperationsList.add(to);
		}
		
		return getOperationsList;
	}
	
	private GridProcessAccounting setAccountingFields(GridProcess process) {
		
		GridProcessAccounting accounting = process.getReplicaAccounting();
		GridProcessExecutionResult result = process.getResult();
		
		GridProcessPhasesData phasesData = new GridProcessPhasesData();
		phasesData.setInitBeginning(result.getInitData().getStartTime());
		phasesData.setInitEnd(result.getInitData().getEndTime());
		phasesData.setRemoteBeginning(result.getRemoteData().getStartTime());
		phasesData.setRemoteEnd(result.getRemoteData().getEndTime());
		phasesData.setFinalBeginning(result.getFinalData().getStartTime());
		phasesData.setFinalEnd(result.getFinalData().getEndTime());
		phasesData.setInitOperations(result.getInitOperations());
		phasesData.setGetOperations(result.getGetOperations());
		
		accounting.setPhasesData(phasesData);
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo();
		
		GridProcessError error = result.getExecutionError();
		if (error != null && error.getErrorCause() != null) {
			resultInfo.setErrorCause(error.getErrorCause().getMessage());
			resultInfo.setExecutionErrorType(error.getType().getName());
		}
		
		ExecutorResult executorResult = result.getExecutorResult();
		if (executorResult != null) {
			resultInfo.setExitValue(executorResult.getExitValue());
			resultInfo.setStderr(executorResult.getStderr());
			resultInfo.setStdout(executorResult.getStdout());
		}
		accounting.setResultInfo(resultInfo);
		
		accounting.setCreationTime(process.getCreationTime());
		accounting.setLatestPhase(process.getState().toString());
		
		SabotageCheckResult sabotageCheckResult = result.getSabotageCheckResult();
		String sabotageCheck = sabotageCheckResult == null ? null : sabotageCheckResult.toString();
		accounting.setSabotageCheck(sabotageCheck);
		
		accounting.setTaskSequenceNumber(process.getSpec().getTaskSequenceNumber()); 
		accounting.setGridProcessSequenceNumber(process.getId());
		
		accounting.setState(process.getState());
		
		return accounting;
	}

	public boolean isWorkerBlacklistedForEntireJob(WorkerEntry workerEntry, Job job) {
		return (getRemainingBlacklistFails(workerEntry, job) == 0);
	}
	
	protected int getRemainingBlacklistFails(WorkerEntry workerEntry, Job job) {
		
		if (isASaboteurWorker(workerEntry.getServiceID())) {
			return 0;
		}
		
		if (workerEntry != null) {
			/*int jobSize = Math.min(job.getSpec().getTaskSpecs().size(), this.maxBlFails);
			return Math.max( 0, jobSize - workerEntry.getNumberOfBlacklistedTasks() );*/
			
			return Math.max( 0, this.maxBlFails - workerEntry.getNumberOfBlacklistedTasks() );
		}
		
		return -1;
	}
	
	public boolean isASaboteurWorker(ServiceID workerServiceID) {
		
		if (this.saboteurs != null) {
			for (WorkerEntry entry : this.saboteurs) {
				if (entry.getServiceID().equals(workerServiceID)) {
					return true;
				}
			}
		}	
		return false;
	}

	public boolean isWorkerUnwanted(Job job, String workerID) {
		
		DeploymentID workerDeploymentID = new DeploymentID(workerID);
		ServiceID workerServiceID = workerDeploymentID.getServiceID();
		
		for (WorkerEntry entry : job.getBlackListedWorkers()) {
			if (entry.getServiceID().equals(workerServiceID)) {
				return true;
			}
		}
		
		return isASaboteurWorker(workerServiceID);
	}

	public boolean isJobSatisfied(Job job) {
		
		if (job.getTasks() == null || job.getTasks().isEmpty()) {
			return true;
		}
		
		List<Task> unsatisfiedTasks = new ArrayList<Task>();
		
		for (Task task : job.getTasks()) {
			if ( canSchedule(task) ) {
				unsatisfiedTasks.add( task );
			}
		}
		
		return unsatisfiedTasks.isEmpty();
	}
	
	private Map<TransferHandle, TransferProgress> convertTransfer(
			Map<GenericTransferHandle, GenericTransferProgress> transferMap) {
		
		Map<TransferHandle, TransferProgress> map = CommonUtils.createSerializableMap();
		
		TransferHandle handle = null;
		TransferProgress progress = null;
		GenericTransferHandle genericHandle = null;
		GenericTransferProgress genericProgress = null;
		
		
		for (Entry<GenericTransferHandle, GenericTransferProgress> entry : transferMap.entrySet()) {
			
			genericHandle = entry.getKey();
			genericProgress = entry.getValue();
			
			if (genericProgress.isOutgoing()) {
				OutgoingHandle outgoing = (OutgoingHandle) genericHandle;
				handle = new OutgoingTransferHandle(outgoing.getId(), outgoing.getLogicalFileName(),
						outgoing.getLocalFile(), outgoing.getDescription(), 
						new DeploymentID(outgoing.getOppositeID())); 
			} else {
				IncomingHandle incoming = (IncomingHandle) genericHandle;
				ContainerID senderID = ContainerID.parse(incoming.getSenderContainerID());
				
				handle = new IncomingTransferHandle(incoming.getId(), incoming.getLogicalFileName(),
						incoming.getDescription(), incoming.getFileSize(), 
						senderID.getContainerID());
			}
			
			progress = new TransferProgress(handle, genericProgress.getFileName(), genericProgress.getFileSize(),
					getStatus(genericProgress.getNewStatus()), genericProgress.getAmountWritten(),
					genericProgress.getProgress(), genericProgress.getTransferRate(), 
					genericProgress.isOutgoing());
			
			map.put(handle, progress);
		}
		
		return map;
	}
	
	private Status getStatus(String status) {
		
		Status value = null;
		
		if (status.startsWith("cancelled")) {
			value = Status.cancelled;
		} else if (status.startsWith("complete")) {
			value = Status.complete;
		} else if (status.startsWith("error")) {
			value = Status.error;
		} else if (status.startsWith("in_progress")) {
			value = Status.in_progress;
		} else if (status.startsWith("initial")) {
			value = Status.initial;
		} else if (status.startsWith("negotiated")) {
			value = Status.negotiated;
		} else if (status.startsWith("negotiating_stream")) {
			value = Status.negotiating_stream;
		} else if (status.startsWith("negotiating_transfer")) {
			value = Status.negotiating_transfer;
		} else if (status.startsWith("refused")) {
			value = Status.refused;
		}
		
		return value;
	}

	//TODO we should have a much better design
	class WorkerEntryComparator implements Comparator< WorkerEntry > {

        private WorkerSpecificationAnnotationsComparator workerSpecAnnotationsComparator;


        public WorkerEntryComparator( Map<String, String> jobAnnotations ) {
                this.workerSpecAnnotationsComparator = new WorkerSpecificationAnnotationsComparator( jobAnnotations );
        }


        public void setAnnotations( Map<String, String> jobAnnotations) {
                this.workerSpecAnnotationsComparator.setTags( jobAnnotations );
        }


        public int compare( WorkerEntry o1, WorkerEntry o2 ) {
                return workerSpecAnnotationsComparator.compare( o1.getWorkerSpecification(), o2.getWorkerSpecification() );
        }

	}

}