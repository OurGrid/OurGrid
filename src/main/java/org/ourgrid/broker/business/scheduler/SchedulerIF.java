package org.ourgrid.broker.business.scheduler;

import java.util.List;
import java.util.Map;

import org.ourgrid.broker.business.scheduler.extensions.GenericTransferProgress;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.broker.status.JobWorkerStatus;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;

/**
 * Scheduler methods.
 */
public interface SchedulerIF {
	
	public void start();
	
	public void stop();
	
	public void schedule(List<IResponseTO> responses);
	
	public void addJob(JobSpecification jobSpec, int jobID, List<IResponseTO> responses);
	
	public void cancelJob(int jobID, List<IResponseTO> responses);

	public void cleanAllFinishedJobs(List<IResponseTO> responses);

	public void cleanFinishedJob(int jobID, List<IResponseTO> responses);
	
	public void finishRequests(List<IResponseTO> responses);
	
	public void finishPeerRequests(String peerServiceID, List<IResponseTO> responses);

	public void notifyWhenJobIsFinished(int jobID, String deploymentID, List<IResponseTO> responses);
	
	public void setPeers(String[] peersID);
	
	public void loginSucceed(String peerID, List<IResponseTO> responses);
	
	public void hereIsWorker(String workerId, String workerPK, String senderPublicKey, 
			String peerAddress, RequestSpecification requestSpec, WorkerSpecification workerSpec, List<IResponseTO> responses);
	
	public void localWorkerProviderFailure(String peerID);
	
	public void workerFailure(String workerContainerID, List<IResponseTO> responses);
	
	public void workerRecovery(String workerPublicKey, String workerDeploymentID, List<IResponseTO> responses);
	
	public void jobEndedInterestedIsDown(String interestedID);
	
	public void outgoingTransferCancelled(OutgoingHandle handle, long amountWritten, List<IResponseTO> responses);
	
	public void outgoingTransferCompleted(OutgoingHandle handle, long amountWritten, List<IResponseTO> responses);
	
	public void outgoingTransferFailed(OutgoingHandle handle, String failCause, 
			long amountWritten, List<IResponseTO> responses);
	
	public void incomingTransferCompleted(IncomingHandle handle, long amountWritten, List<IResponseTO> responses);
	
	public void incomingTransferFailed(IncomingHandle handle, String failCause, 
			long amountWritten, List<IResponseTO> responses);
	
	public void transferRejected(OutgoingHandle handle, List<IResponseTO> responses);
	
	public void updateTransferProgress(GenericTransferProgress transferProgress, List<IResponseTO> responses);
	
	public void transferRequestReceived(IncomingHandle handle, List<IResponseTO> responses);
	
	public void errorOcurred(String workerContainerID, String errorCause, 
			String gridProcessErrorType, List<IResponseTO> responses);
	
	public void hereIsFileInfo(String workerContainerID, long transferHandle, FileInfo fileInfo, List<IResponseTO> responses);
	
	public void hereIsGridProcessResult(String workerContainerID, ExecutorResult result, List<IResponseTO> responses);
	
	public void workerIsReady(String workerContainerID, List<IResponseTO> responses);
	
	public JobWorkerStatus getCompleteStatus();
	
	public Map<Integer, JobStatusInfo> getJobsDescription();
	
	public void sendMessage(MessageHandle handle);
	
}
