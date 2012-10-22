package org.ourgrid.broker.business.scheduler.workqueue;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.JobDAO;
import org.ourgrid.broker.business.dao.Request;
import org.ourgrid.broker.business.dao.WorkerDAO;
import org.ourgrid.broker.business.dao.WorkerEntry;
import org.ourgrid.broker.business.messages.BrokerControlMessages;
import org.ourgrid.broker.business.messages.LocalWorkerProviderClientMessages;
import org.ourgrid.broker.business.messages.SchedulerMessages;
import org.ourgrid.broker.business.messages.WorkerClientMessages;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.business.scheduler.extensions.GenericTransferProgress;
import org.ourgrid.broker.communication.operations.GridProcessOperations;
import org.ourgrid.broker.communication.operations.ReplicaParser;
import org.ourgrid.broker.response.DisposeWorkerResponseTO;
import org.ourgrid.broker.response.FinishRequestResponseTO;
import org.ourgrid.broker.response.JobEndedResponseTO;
import org.ourgrid.broker.response.PauseRequestResponseTO;
import org.ourgrid.broker.response.RequestWorkersResponseTO;
import org.ourgrid.broker.response.ScheduleActionToRunOnceResponseTO;
import org.ourgrid.broker.response.StartWorkResponseTO;
import org.ourgrid.broker.response.UnwantWorkerResponseTO;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.broker.status.JobWorkerStatus;
import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.broker.util.UtilConverter;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.OperationSucceedResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.job.GridProcess;
import org.ourgrid.common.job.Job;
import org.ourgrid.common.job.Task;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.controller.matcher.MatcherImpl;
import org.ourgrid.worker.business.controller.GridProcessError;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

@WebService()
public class WorkQueueReplication implements SchedulerIF {

	private WorkQueueExecutionController executionController;

	public WorkQueueReplication() {}

	public WorkQueueReplication(int maxReplicas, int maxFails, int maxBlFails) {
		setData(maxReplicas, maxFails, maxBlFails);
	}

	@WebMethod
	public void setData(int maxReplicas, int maxFails, int maxBlFails) {
		this.executionController = new WorkQueueExecutionController(maxReplicas, maxFails, maxBlFails);
	}


	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#start()
	 */
	@WebMethod
	public void start() {
		JobInfo.reset();
		WorkerInfo.reset();
		PeerInfo.reset();

		return;
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#stop()
	 */
	@WebMethod
	public void stop() {
		return;
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#schedule()
	 */
	@WebMethod
	public void schedule(List<IResponseTO> responses) {

		for (Job job : getJobInfo().getJobsList()) {

			this.executionController.schedule(job);
			execute(job, responses);
			cleanJob(job, responses);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#sendMessage(org.ourgrid.common.interfaces.to.MessageHandle)
	 */
	@WebMethod
	public void sendMessage(MessageHandle messageHandle) {

	}


	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#addJob(org.ourgrid.common.interfaces.control.BrokerControlClient, org.ourgrid.common.spec.job.JobSpec)
	 */
	@WebMethod
	public void addJob(JobSpecification jobSpec, int jobID, List<IResponseTO> responses) {

		Job job = getJobInfo().addJob(jobSpec, jobID);

		for (String peerID : getPeerInfo().getLoggedPeersIDs()) {
			RequestSpecification requestSpec = this.executionController.createRequestSpec(job.getJobId(), job.getSpec());
			job.addRequest(requestSpec, peerID);

			JobDAO jobDAO = BrokerDAOFactory.getInstance().getJobDAO();
			String peerAddress = StringUtil.deploymentIDToAddress(peerID);

			RequestWorkersResponseTO to = new RequestWorkersResponseTO();
			to.setJobID(jobID);
			to.setMaxFails(requestSpec.getMaxFails());
			to.setMaxReplicas(requestSpec.getMaxReplicas());
			to.setRequestID(requestSpec.getRequestId());
			to.setRequiredWorkers(requestSpec.getRequiredWorkers());
			to.setPeerAddress(peerAddress);
			to.setJobSpec(jobSpec);

			responses.add(to);

			jobDAO.addJobRequest(jobID, requestSpec.getRequestId());
		}

		responses.add(new LoggerResponseTO(BrokerControlMessages.getJobAddedMessage(jobSpec, job.getJobId()), LoggerResponseTO.DEBUG));

		JobDAO jobDAO = BrokerDAOFactory.getInstance().getJobDAO();

		String clientAddress = jobDAO.getBrokerControlClientAddress();

		jobDAO.addJob(jobID);

		responses.add(new LoggerResponseTO("Operation add job succeed.", LoggerResponseTO.INFO));
		
		OperationSucceedResponseTO to = new OperationSucceedResponseTO();
		to.setClientAddress(clientAddress);

		to.setResult(jobID);
		
		responses.add(to);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#cancelJob(org.ourgrid.common.interfaces.control.BrokerControlClient, org.ourgrid.common.job.Job)
	 */
	@WebMethod
	public void cancelJob(int jobID, List<IResponseTO> responses) {

		Job job = getJobInfo().getJob(jobID);
		String clientAddress = BrokerDAOFactory.getInstance().getJobDAO().getBrokerControlClientAddress();

		if  (job == null || job.getState().equals(GridProcessState.CANCELLED)){

			OperationSucceedResponseTO to = new OperationSucceedResponseTO();

			to.setClientAddress(clientAddress);
			String errorMessage = BrokerControlMessages.getNoSuchJobToCancelMessage(jobID);
			if (errorMessage != null && !errorMessage.equals("")) {
				to.setErrorCause(new Exception(errorMessage));
			} 

			responses.add(to);

			responses.add(new LoggerResponseTO(BrokerControlMessages.getNoSuchJobToCancelMessage(jobID), LoggerResponseTO.WARN));
			return;

		}


		cancelJob(job, responses);

		OperationSucceedResponseTO to = new OperationSucceedResponseTO();
		to.setClientAddress(clientAddress);
		responses.add(new LoggerResponseTO("Operation cancel job succeed.", LoggerResponseTO.INFO));

		responses.add(to);

		responses.add(new LoggerResponseTO(BrokerControlMessages.getJobCancelledMessage(jobID), LoggerResponseTO.DEBUG));

		responses.add(new LoggerResponseTO("Exiting application.", LoggerResponseTO.INFO));
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#cleanAllFinishedJobs(org.ourgrid.common.interfaces.control.BrokerControlClient)
	 */
	@WebMethod
	public void cleanAllFinishedJobs(List<IResponseTO> responses) {
		getJobInfo().cleanAllFinishedJobs();
		String clientAddress = BrokerDAOFactory.getInstance().getJobDAO().getBrokerControlClientAddress();

		responses.add(new LoggerResponseTO("Operation succeed: clean all finished jobs.", LoggerResponseTO.INFO));
		
		OperationSucceedResponseTO to = new OperationSucceedResponseTO();
		to.setClientAddress(clientAddress);

		to.setResult(null);

		responses.add(to);

	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#cleanFinishedJob(org.ourgrid.common.interfaces.control.BrokerControlClient, int)
	 */
	@WebMethod
	public void cleanFinishedJob(int jobID, List<IResponseTO> responses) {

		Job job = getJobInfo().getJob(jobID);

		if (job.isRunning()) {

			new LoggerResponseTO(BrokerControlMessages.getJobStillRunningMessage(jobID), LoggerResponseTO.ERROR);

		}

		getJobInfo().cleanFinishedJob(jobID);

		responses.add(new LoggerResponseTO("Operation succeed.", LoggerResponseTO.INFO));
		OperationSucceedResponseTO to = new OperationSucceedResponseTO();
		to.setClientAddress(null);

		to.setResult(null);

	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#loginSucceed(java.lang.String)
	 */
	@WebMethod
	public void loginSucceed(String peerID, List<IResponseTO> responses) {

		RequestSpecification spec = null;

		for (Job job : getJobInfo().getJobsList()) {
			if (!this.executionController.isJobSatisfied(job)) {

				int jobID = job.getJobId();

				spec = this.executionController.createRequestSpec(jobID, job.getSpec());

				if (peerID != null) {
					JobDAO jobDAO = BrokerDAOFactory.getInstance().getJobDAO();
					JobSpecification jobSpec = jobDAO.getJobSpec(jobID);
					String peerAddress = StringUtil.deploymentIDToAddress(peerID);

					RequestWorkersResponseTO to = new RequestWorkersResponseTO();
					to.setJobID(jobID);
					to.setMaxFails(spec.getMaxFails());
					to.setMaxReplicas(spec.getMaxReplicas());
					to.setRequestID(spec.getRequestId());
					to.setRequiredWorkers(spec.getRequiredWorkers());
					to.setPeerAddress(peerAddress);
					to.setJobSpec(jobSpec);

					responses.add(to);

					jobDAO.addJobRequest(jobID, spec.getRequestId());
				}
				job.addRequest(spec, peerID);
			}
		}

		getPeerInfo().addPeerLogged(peerID);


	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#setPeers(java.lang.String[])
	 */
	@WebMethod
	public void setPeers(String[] peersID) {
		getPeerInfo().removePeersLogged();
		return;
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#hereIsWorker(java.lang.String, java.lang.String, java.lang.String, java.lang.String, long)
	 */
	@WebMethod
	public void hereIsWorker(String workerId, String workerPK, String senderPublicKey, 
			String peerAddress, RequestSpecification requestSpec, WorkerSpecification workerSpec, List<IResponseTO> responses) {

		if (!getJobInfo().hasRunningJobs()) {

			new LoggerResponseTO(LocalWorkerProviderClientMessages.getBrokerWithoutRunningJobsReceivingWorkerMessage(workerPK, senderPublicKey), LoggerResponseTO.WARN);

			String wAddress = StringUtil.deploymentIDToAddress(workerId);

			DisposeWorkerResponseTO disposeTO = new DisposeWorkerResponseTO();
			disposeTO.setPeerAddress(peerAddress);

			WorkerDAO workerDAO = BrokerDAOFactory.getInstance().getWorkerDAO();

			disposeTO.setWorkerAddress(wAddress);
			disposeTO.setWorkerPublicKey(workerDAO.getWorkerPublicKey(wAddress));

			responses.add(disposeTO);

			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(wAddress);

			responses.add(releaseTO);

			workerDAO.removeWorker(wAddress);

			ReleaseResponseTO responseTO = new ReleaseResponseTO();
			responseTO.setStubAddress(wAddress);
			responses.add(responseTO);

		}

		long requestID = requestSpec.getRequestId();
		Job job = getJobInfo().getJobForThisRequest(requestID);
		String workerAddress = StringUtil.deploymentIDToAddress(workerId);

		if (job != null) {
			Request request = job.getRequest(requestID);

			if (!request.isPaused()) {

				if (!this.executionController.isWorkerUnwanted(job, workerId)) {
					addJob(workerSpec, requestID, job, workerId, responses);
				} else {

					int jobID = Integer.parseInt("" + requestSpec.getJobId());
					JobSpecification jobSpec = BrokerDAOFactory.getInstance().getJobDAO().getJobSpec(jobID);


					UnwantWorkerResponseTO unwantWorkerTO = new UnwantWorkerResponseTO();
					unwantWorkerTO.setJobID(jobID);
					unwantWorkerTO.setJobSpec(jobSpec);
					unwantWorkerTO.setMaxFails(requestSpec.getMaxFails());
					unwantWorkerTO.setMaxReplicas(requestSpec.getMaxReplicas());
					unwantWorkerTO.setPeerAddress(peerAddress);
					unwantWorkerTO.setRequestID(requestID);
					unwantWorkerTO.setRequiredWorkers(requestSpec.getRequiredWorkers());
					unwantWorkerTO.setWorkerPublicKey(workerPK);

					unwantWorkerTO.setWorkerAddress(workerAddress);

					WorkerDAO workerDAO = BrokerDAOFactory.getInstance().getWorkerDAO();

					responses.add(unwantWorkerTO);

					ReleaseResponseTO releaseTO = new ReleaseResponseTO();
					releaseTO.setStubAddress(workerAddress);

					responses.add(releaseTO);

					workerDAO.removeWorker(workerAddress);
				}
			} else {

				PauseRequestResponseTO to = new PauseRequestResponseTO();

				to.setPeerAddress(peerAddress);
				to.setRequestID(requestID);

				responses.add(to);

				DisposeWorkerResponseTO disposeTO = new DisposeWorkerResponseTO();
				disposeTO.setPeerAddress(peerAddress);

				WorkerDAO workerDAO = BrokerDAOFactory.getInstance().getWorkerDAO();

				disposeTO.setWorkerAddress(workerAddress);
				disposeTO.setWorkerPublicKey(workerDAO.getWorkerPublicKey(workerAddress));

				responses.add(disposeTO);

				ReleaseResponseTO releaseTO = new ReleaseResponseTO();
				releaseTO.setStubAddress(workerAddress);

				responses.add(releaseTO);

				workerDAO.removeWorker(workerAddress);
			}

		} else {
			DisposeWorkerResponseTO disposeTO = new DisposeWorkerResponseTO();
			disposeTO.setPeerAddress(peerAddress);

			WorkerDAO workerDAO = BrokerDAOFactory.getInstance().getWorkerDAO();

			disposeTO.setWorkerAddress(workerAddress);
			disposeTO.setWorkerPublicKey(workerDAO.getWorkerPublicKey(workerAddress));

			responses.add(disposeTO);

			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(workerAddress);

			responses.add(releaseTO);

			workerDAO.removeWorker(workerAddress);			
		}

	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#localWorkerProviderFailure(java.lang.String)
	 */
	@WebMethod
	public void localWorkerProviderFailure(String peerID) {

		for (Job job :getJobInfo().getJobsList()) {
			job.removeRequests(peerID);

		}

		getPeerInfo().removePeerLogged(peerID);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#incomingTransferCompleted(org.ourgrid.broker.scheduler.transferhandle.IncomingHandle, long)
	 */
	@WebMethod
	public void incomingTransferCompleted(IncomingHandle handle, long amountWritten, List<IResponseTO> responses ) {

		WorkerEntry workerEntry = getWorkerEntry(handle.getSenderContainerID().toString());	

		if (workerEntry == null) {
			logInvalidWorkerEntry(handle.getSenderContainerID().toString(), responses);
			return;
		}

		if(!continueExecution(workerEntry)) {
			return;
		}

		GridProcess execution = workerEntry.getGridProcess();
		execution.getRunningState().incomingTransferCompleted(handle, amountWritten, execution, responses);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#incomingTransferFailed(org.ourgrid.broker.scheduler.transferhandle.IncomingHandle, java.lang.String, long)
	 */
	@WebMethod
	public void incomingTransferFailed(IncomingHandle handle, String failCause, long amountWritten, List<IResponseTO> responses ) {

		WorkerEntry workerEntry = getWorkerEntry(handle.getSenderContainerID().toString());	

		if (workerEntry == null) {
			logInvalidWorkerEntry(handle.getSenderContainerID().toString(),responses);
			return;
		}

		if(!continueExecution(workerEntry)) {
			return;
		}

		GridProcess execution = workerEntry.getGridProcess();
		execution.getRunningState().incomingTransferFailed(handle, new Exception(failCause), amountWritten, 
				execution, responses);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#outgoingTransferCancelled(br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle, long)
	 */
	@WebMethod
	public void outgoingTransferCancelled(OutgoingHandle handle, long amountWritten, List<IResponseTO> responses) {

		String containerID = StringUtil.deploymentIDToContainerID(handle.getDestinationID());
		WorkerEntry workerEntry = getWorkerEntry(containerID);	

		if (workerEntry == null) {
			logInvalidWorkerEntry(containerID, responses);
			return;
		}

		if(!continueExecution(workerEntry)) {
			return;
		}

		GridProcess execution = workerEntry.getGridProcess();
		execution.getRunningState().outgoingTransferCancelled(handle, amountWritten, execution, responses);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#outgoingTransferCompleted(br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle, long)
	 */
	@WebMethod
	public void outgoingTransferCompleted(OutgoingHandle handle, long amountWritten, List<IResponseTO> responses) {

		String containerID = StringUtil.deploymentIDToContainerID(handle.getDestinationID());
		WorkerEntry workerEntry = getWorkerEntry(containerID);	

		if (workerEntry == null) {
			logInvalidWorkerEntry(containerID, responses);
			return;
		}

		if(!continueExecution(workerEntry)) {
			return;
		}

		GridProcess execution = workerEntry.getGridProcess();
		execution.getRunningState().outgoingTransferCompleted(handle, amountWritten, execution, responses);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#outgoingTransferFailed(br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle, java.lang.Exception, long)
	 */
	@WebMethod
	public void outgoingTransferFailed(OutgoingHandle handle, String failCause, long amountWritten, List<IResponseTO> responses) {

		String containerID = StringUtil.deploymentIDToContainerID(handle.getDestinationID());
		WorkerEntry workerEntry = getWorkerEntry(containerID);	

		if (workerEntry == null) {
			logInvalidWorkerEntry(containerID, responses);
			return;
		}

		if(!continueExecution(workerEntry)) {
			return;
		}

		GridProcess execution = workerEntry.getGridProcess();
		execution.getRunningState().outgoingTransferFailed(handle, failCause, amountWritten, execution, responses);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#transferRejected(br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle)
	 */
	@WebMethod
	public void transferRejected(OutgoingHandle handle, List<IResponseTO> responses) {

		String containerID = StringUtil.deploymentIDToContainerID(handle.getDestinationID());
		WorkerEntry workerEntry = getWorkerEntry(containerID);	

		if (workerEntry == null) {
			logInvalidWorkerEntry(containerID, responses);
			return;
		}

		if(!continueExecution(workerEntry)) {
			return ;
		}

		GridProcess execution = workerEntry.getGridProcess();
		execution.getRunningState().fileRejected(handle, execution, responses);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#transferRequestReceived(br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle)
	 */
	@WebMethod
	public void transferRequestReceived(IncomingHandle handle, List<IResponseTO> responses) {

		WorkerEntry workerEntry = getWorkerEntry(handle.getSenderContainerID().toString());	

		if (workerEntry == null) {
			logInvalidWorkerEntry(handle.getSenderContainerID().toString(), responses);
			return;
		}

		if(!continueExecution(workerEntry)) {
			return;
		}

		GridProcess execution = workerEntry.getGridProcess();
		execution.getRunningState().fileTransferRequestReceived(handle, execution, responses);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#errorOcurred(java.lang.String, java.lang.String, org.ourgrid.common.interfaces.to.GridProcessErrorTypes)
	 */
	@WebMethod
	public void errorOcurred(String workerContainerID, String errorCause, 
			String gridProcessErrorType, List<IResponseTO> responses) {

		WorkerEntry workerEntry = getWorkerEntry(workerContainerID);	

		if (workerEntry == null) {
			logInvalidWorkerEntry(workerContainerID, responses);
			return;
		}

		if(!continueExecution(workerEntry)) {
			return;
		}

		GridProcess gridProcess = workerEntry.getGridProcess();
		GridProcessError error = new GridProcessError(new Throwable(errorCause), 
				GridProcessErrorTypes.getType(gridProcessErrorType)); 
		gridProcess.getRunningState().errorOcurred(error, gridProcess, responses);

	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#hereIsFileInfo(java.lang.String, br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle, org.ourgrid.common.filemanager.FileInfo)
	 */
	@WebMethod
	public void hereIsFileInfo(String workerContainerID, long handlerID,	FileInfo fileInfo, List<IResponseTO> responses) {

		WorkerEntry workerEntry = getWorkerEntry(workerContainerID);	

		if (workerEntry == null) {
			logInvalidWorkerEntry(workerContainerID,responses);
			return;
		}

		if(!continueExecution(workerEntry)) {
			return ;
		}

		GridProcess gridProcess = workerEntry.getGridProcess();
		gridProcess.getRunningState().hereIsFileInfo(handlerID, fileInfo, gridProcess, responses);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#hereIsGridProcessResult(java.lang.String, org.ourgrid.common.executor.ExecutorResult)
	 */
	@WebMethod
	public void hereIsGridProcessResult(String workerContainerID, ExecutorResult result, List<IResponseTO> responses) {

		WorkerEntry workerEntry = getWorkerEntry(workerContainerID);	

		if (workerEntry == null) {
			logInvalidWorkerEntry(workerContainerID, responses);
			return;
		}

		if(!continueExecution(workerEntry)) {
			return ;
		}

		GridProcess gridProcess = workerEntry.getGridProcess();
		gridProcess.getRunningState().hereIsExecutionResult(result, gridProcess, responses);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#workerIsReady(java.lang.String)
	 */
	@WebMethod
	public void workerIsReady(String workerContainerID, List<IResponseTO> responses) {

		WorkerEntry workerEntry = getWorkerEntry(workerContainerID);	

		if (workerEntry == null) {
			logInvalidWorkerEntry(workerContainerID, responses);
			return;
		}

		if(!continueExecution(workerEntry)) {
			return;
		}

		GridProcess gridProcess = workerEntry.getGridProcess();
		gridProcess.getRunningState().workerIsReady(gridProcess, responses);
	}


	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#updateTransferProgress(br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProgress)
	 */
	@WebMethod
	public void updateTransferProgress(GenericTransferProgress transferProgress, List<IResponseTO> responses) {
		WorkerEntry workerEntry = getWorkerEntry(transferProgress.getHandle().getOppositeID().toString());	

		if (workerEntry == null) {
			logInvalidWorkerEntry(transferProgress.getHandle().getOppositeID().toString(), responses);
			return;
		}

		if(!continueExecution(workerEntry)) {
			return;
		}

		GridProcess execution = workerEntry.getGridProcess();
		execution.getRunningState().updateTransferProgress(transferProgress, execution, responses);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#workerFailure(java.lang.String)
	 */
	@WebMethod
	public void workerFailure(String workerContainerID, List<IResponseTO> responses) {

		WorkerEntry workerEntry =  getWorkerInfo().removeWorker(workerContainerID);
		
		if (workerEntry != null) {

			String workerAddress = StringUtil.deploymentIDToAddress(workerEntry.getWorkerID());
			GridProcess gridProcess = workerEntry.getGridProcess();
			if (gridProcess != null) {
				gridProcess.getResult().setExecutionError(
						new GridProcessError(GridProcessErrorTypes.MACHINE_FAILURE));

				gridProcess.setGridProcessResult(GridProcessState.FAILED);

				GridProcessOperations operations = gridProcess.getOperations();
				if (operations != null) {
					operations.cancelOperations(responses);
				}

				this.executionController.executionFailed(gridProcess, responses);
				workerEntry.dispose();
				
			} else {
				this.executionController.disposeWorker(workerEntry, responses);
			}
			

			ReleaseResponseTO responseTO = new ReleaseResponseTO();
			responseTO.setStubAddress(workerAddress);
			responses.add(responseTO);

		} else {

			responses.add(new LoggerResponseTO(WorkerClientMessages.getPeerSendAWorkerFailureMessage(workerContainerID),LoggerResponseTO.WARN));
		}

	}

	@WebMethod
	public void workerRecovery(String workerPublicKey, String workerDeploymentID, List<IResponseTO> responses) {

		WorkerEntry workerEntry = getWorkerInfo().getWorker(StringUtil.deploymentIDToContainerID(workerDeploymentID));
		workerEntry.setUp(true);
		workerEntry.setWorkerID(workerDeploymentID);
		workerEntry.setWorkerPublicKey(workerPublicKey);

		WorkerDAO workerDAO = BrokerDAOFactory.getInstance().getWorkerDAO();
		workerDAO.setWorkerPublicKey(StringUtil.deploymentIDToAddress(workerDeploymentID), workerPublicKey);

		responses.add(new ScheduleActionToRunOnceResponseTO());		
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#notifyWhenJobIsFinished(int, java.lang.String)
	 */
	@WebMethod
	public void notifyWhenJobIsFinished(int jobID, String deploymentID, List<IResponseTO> responses) {

		Job job = getJobInfo().getJob(jobID);

		if (job != null && !job.getState().isRunnable()) {
			int jobState = UtilConverter.getJobState(job.getState());

			JobEndedResponseTO to = new JobEndedResponseTO();
			to.setInterestedID(deploymentID);
			to.setJobID(jobID);
			to.setState(jobState);

			responses.add(to);
		}

		getJobInfo().notifyWhenJobIsFinished(jobID, deploymentID);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#finishRequests()
	 */
	@WebMethod
	public void finishRequests(List<IResponseTO> responses) {

		RequestSpecification spec = null;
		for (Job job : getJobInfo().getJobsList()) {
			for (Request request : job.getRequests()) {
				spec = request.getSpecification();

				int jobID = Integer.valueOf(""+spec.getJobId());
				JobSpecification jobSpec = BrokerDAOFactory.getInstance().getJobDAO().getJobSpec(jobID);

				FinishRequestResponseTO to = new FinishRequestResponseTO();
				to.setJobID(jobID);
				to.setJobSpec(jobSpec);
				to.setMaxFails(spec.getMaxFails());
				to.setMaxReplicas(spec.getMaxReplicas());
				to.setPeerAddress( StringUtil.deploymentIDToAddress(request.getPeerID()));
				to.setRequestID(spec.getRequestId());
				to.setRequiredWorkers(spec.getRequiredWorkers());

				responses.add(to);

			}
			job.finishRequests();
		}

	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#finishRequests(java.lang.String)
	 */
	@WebMethod
	public void finishPeerRequests(String peerServiceID, List<IResponseTO> responses) {

		DeploymentID peerID = null;
		RequestSpecification spec = null;
		for (Job job : getJobInfo().getJobsList()) {
			for (Request request : job.getRequests()) {
				spec = request.getSpecification();

				peerID = new DeploymentID(request.getPeerID());
				if (peerID.getServiceID().toString().equals(peerServiceID)) {

					int jobID = Integer.valueOf(""+spec.getJobId());
					JobSpecification jobSpec = BrokerDAOFactory.getInstance().getJobDAO().getJobSpec(jobID);

					FinishRequestResponseTO to = new FinishRequestResponseTO();
					to.setJobID(jobID);
					to.setJobSpec(jobSpec);
					to.setMaxFails(spec.getMaxFails());
					to.setMaxReplicas(spec.getMaxReplicas());
					to.setPeerAddress( StringUtil.deploymentIDToAddress(request.getPeerID()));
					to.setRequestID(spec.getRequestId());
					to.setRequiredWorkers(spec.getRequiredWorkers());

					responses.add(to);

					job.removeRequest(request.getSpecification().getRequestId());
				}
			}
		}
	}


	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#jobEndedInterestedIsDown(java.lang.String)
	 */
	@WebMethod
	public void jobEndedInterestedIsDown(String interestedID) {
		getJobInfo().removeJobEndInterested(interestedID);
		return;
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.SchedulerIF#getCompleteStatus()
	 */
	@WebMethod
	public JobWorkerStatus getCompleteStatus() {
		return executionController.getCompleteStatus();
	}

	private void addJob(WorkerSpecification workerSpec, long requestID, Job job, String workerID, List<IResponseTO> responses) {
		cancelPreviousWorkerExecution(workerSpec.getServiceID().getContainerID().toString(), responses);

		WorkerEntry workerEntry = job.addWorker(requestID, workerSpec, workerID);
		DeploymentID workerDeploymentID = new DeploymentID(workerID);

		getWorkerInfo().addWorkerEntry(workerDeploymentID.getContainerID().toString(), workerEntry);

		int detectionTime = 120;
		int heartbeatDelay = 10;

		RegisterInterestResponseTO registerInterestResponse = new RegisterInterestResponseTO();
		registerInterestResponse.setMonitorableAddress(workerID);
		registerInterestResponse.setMonitorableType(Worker.class);
		registerInterestResponse.setMonitorName(BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);
		registerInterestResponse.setDetectionTime(detectionTime);
		registerInterestResponse.setHeartbeatDelay(heartbeatDelay);

		responses.add(registerInterestResponse);

	}

	private void cancelPreviousWorkerExecution(String containerID, List<IResponseTO> responses) {
		WorkerEntry previousEntry = getWorkerInfo().getWorker(containerID);

		if (previousEntry != null) {

			GridProcess gridProcess = previousEntry.getGridProcess();
			if (gridProcess != null) {

				gridProcess.setGridProcessResult(GridProcessState.ABORTED);

				GridProcessOperations operations = gridProcess.getOperations();
				if (operations != null) {
					operations.cancelOperations(responses);
				}

				this.executionController.executionAborted(gridProcess, responses);			
			}
		}
	}

	private WorkerEntry getWorkerEntry(String workerContainerID) {
		WorkerEntry workerEntry = getWorkerInfo().getWorker(workerContainerID);	
		return workerEntry;
	}

	private void logInvalidWorkerEntry(String workerContainerID, List<IResponseTO> responses) {

		responses.add(new LoggerResponseTO(WorkerClientMessages.getNotAvaliableWorker(workerContainerID),LoggerResponseTO.WARN));

	}

	private boolean continueExecution(WorkerEntry workerEntry) {

		if (workerEntry == null) {
			return false;
		}

		GridProcess execution = workerEntry.getGridProcess();

		if (execution == null) {
			return false;
		} else {
			return true;
		}
	}

	private JobInfo getJobInfo() {
		return JobInfo.getInstance();
	}

	private PeerInfo getPeerInfo() {
		return PeerInfo.getInstance();
	}

	private WorkerInfo getWorkerInfo() {
		return WorkerInfo.getInstance();
	}

	/**
	 * Executes the grid process of this job that are ready to run.
	 * @param job
	 * @param heuristic 
	 */
	private void execute(Job job, List<IResponseTO> responses) {

		for (Task task : job.getTasks()) {
			for (GridProcess gridProcess : task.getReadyToRunGridProcesses()) {

				ReplicaParser replicaParser = new ReplicaParser(gridProcess, new MatcherImpl());

				replicaParser.parse();

				GridProcessOperations executionOperations = replicaParser.getExecutionOperations();

				responses.add(new LoggerResponseTO(SchedulerMessages.getExecutingReplicaMessage(
						gridProcess.getHandle(), gridProcess.getWorkerEntry().getWorkerID()), 
						LoggerResponseTO.DEBUG));

				gridProcess.setOperations(executionOperations);
				gridProcess.setAsRunning();

				String workerAddress = StringUtil.deploymentIDToAddress(gridProcess.getWorkerEntry().getWorkerID());

				StartWorkResponseTO to = new StartWorkResponseTO();
				to.setJobID(gridProcess.getHandle().getJobID());
				to.setProcessID(gridProcess.getHandle().getReplicaID());
				to.setRequestID(gridProcess.getWorkerEntry().getRequestID());
				to.setTaskID(gridProcess.getHandle().getTaskID());
				to.setWorkerAddress(workerAddress);

				responses.add(to);
			}
		}
	}

	private void cleanJob(Job job, List<IResponseTO> responses) {

		if (this.executionController.isJobSatisfied(job)) {

			for (Request request : job.getRequests()) {
				if (!request.isPaused()) {

					PauseRequestResponseTO to = new PauseRequestResponseTO();

					to.setPeerAddress(StringUtil.deploymentIDToAddress(request.getPeerID()));
					to.setRequestID(request.getSpecification().getRequestId());

					responses.add(to);

					request.setPaused(true);
				}
			}

			// Dispose some workers that were received but were not needed

			for (WorkerEntry workerEntry : job.getDeallocatedWorkerEntries()) {
				getWorkerInfo().removeWorker(workerEntry.getServiceID().getContainerID().toString());

				responses.add(new LoggerResponseTO(SchedulerMessages.getDisposingWorkerMessage(workerEntry.getServiceID()),LoggerResponseTO.WARN));


				String wAddress = StringUtil.deploymentIDToAddress(workerEntry.getWorkerID());
				String pAddress = StringUtil.deploymentIDToAddress(workerEntry.getPeerID());

				DisposeWorkerResponseTO disposeTO = new DisposeWorkerResponseTO();
				disposeTO.setPeerAddress(pAddress);

				WorkerDAO workerDAO = BrokerDAOFactory.getInstance().getWorkerDAO();

				disposeTO.setWorkerAddress(wAddress);
				disposeTO.setWorkerPublicKey(workerDAO.getWorkerPublicKey(wAddress));

				responses.add(disposeTO);

				ReleaseResponseTO releaseTO = new ReleaseResponseTO();
				releaseTO.setStubAddress(wAddress);

				responses.add(releaseTO);

				workerDAO.removeWorker(wAddress);

				workerEntry.dispose();

				ReleaseResponseTO responseTO = new ReleaseResponseTO();
				responseTO.setStubAddress(wAddress);
				responses.add(responseTO);

			}
		}
	}

	private void cancelJob(Job job, List<IResponseTO> responses) {

		job.setAsCanceled();

		if (job.getTasks() != null) {

			for ( Task task : job.getTasks() ) {

				task.setAsCancelled();

				if (task.getGridProcesses() != null) {

					for ( GridProcess replica : task.getGridProcesses() ) {
						replica.getOperations().cancelOperations(responses);
						if ( replica.getState() == GridProcessState.RUNNING || replica.getState() == GridProcessState.UNSTARTED ) {
							executionController.executionCancelled(replica, responses);
						}
					}
				}	
			}
		}

		executionController.finishJob(job, responses);
		executionController.updateScheduler(responses);
	}

	public Map<Integer, JobStatusInfo> getJobsDescription() {
		Map<Integer, Job> jobsMap = getJobInfo().getJobs();
		Map<Integer, JobStatusInfo> jobs = CommonUtils.createSerializableMap();

		for (Job job : jobsMap.values()) {
			JobStatusInfo jobInfo = new JobStatusInfo(job.getJobId(), job.getSpec(), UtilConverter.getJobState(job.getState()),
					new LinkedList<TaskStatusInfo>(), job.getCreationTime(), job.getFinalizationTime());
			jobs.put(jobInfo.getJobId(), jobInfo);
		}

		return jobs;
	}

}
