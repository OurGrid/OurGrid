package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.internal.OurGridResponseControl;
import org.ourgrid.common.internal.sender.ScheduleActionWithFixedDelaySender;

public class WorkerResponseControl extends OurGridResponseControl {

	
	private static WorkerResponseControl instance;
	
	
	public static WorkerResponseControl getInstance() {
		if (instance == null) {
			instance = new WorkerResponseControl();
		}
		
		return instance;
	}
	
	protected void addEntitySenders() {
		addSender(WorkerResponseConstants.ACCEPT_TRANSFER, new AcceptTransferSender());
		addSender(WorkerResponseConstants.CANCEL_BEGIN_ALLOCATION_ACTION, new CancelBeginAllocationActionSender());
		addSender(WorkerResponseConstants.CANCEL_EXECUTION_ACTION, new CancelExecutionActionSender());
		addSender(WorkerResponseConstants.CANCEL_REPORT_ACCOUNTING_ACTION, new CancelReportAccountingActionSender());
		addSender(WorkerResponseConstants.CREATE_EXECUTOR, new CreateExecutorSender());
		addSender(WorkerResponseConstants.EXECUTOR_SHUTDOWN_COMMAND, new ExecutorShutdownSender());
		addSender(WorkerResponseConstants.EXECUTOR_KILL_COMMAND, new ExecutorKillCommandSender());
		addSender(WorkerResponseConstants.EXECUTOR_KILL_PREPARING_ALLOCATION, new ExecutorKillPreparingAllocationSender());
		
		addSender(WorkerResponseConstants.HERE_IS_COMPLETE_STATUS, new HereIsWorkerCompleteStatusSender());
		addSender(WorkerResponseConstants.HERE_IS_MASTER_PEER, new HereIsMasterPeerSender());
		addSender(WorkerResponseConstants.HERE_IS_STATUS, new HereIsStatusSender());
		addSender(WorkerResponseConstants.LOGIN_AT_PEER, new WorkerLoginSender());
		addSender(WorkerResponseConstants.MASTER_PEER_STATUS_CHANGED_ALLOCATED_FOR_BROKER, new MasterPeerStatusChangedAllocatedForBrokerSender());
		addSender(WorkerResponseConstants.MESSAGE_HANDLE, new MessageHandleSender());
		addSender(WorkerResponseConstants.OPERATION_SUCCEDED, new OperationSucceedSender());
		addSender(WorkerResponseConstants.PAUSE_WORKER, new PauseWorkerSender());
		addSender(WorkerResponseConstants.REJECT_TRANSFER, new RejectTransferSender());
		addSender(WorkerResponseConstants.REMOTE_PEER_STATUS_CHANGED_ALLOCATED_FOR_BROKER, new RemotePeerStatusChangedAllocatedForBrokerSender());
		addSender(WorkerResponseConstants.REPORT_WORK_ACCOUNTING, new ReportWorkAccoutingActionSender());
		addSender(WorkerResponseConstants.RESUME_WORKER, new ResumeWorkerSender());
		addSender(WorkerResponseConstants.SCHEDULED_ACTION_WITH_FIXED_DELAY, new ScheduleActionWithFixedDelaySender());
		addSender(WorkerResponseConstants.START_TRANSFER, new StartTransferSender());
		addSender(WorkerResponseConstants.STATUS_CHANGED, new StatusChangedSender());
		addSender(WorkerResponseConstants.STATUS_CHANGED_ALLOCATED_FOR_PEER, new StatusChangedAllocatedForPeerSender());
		addSender(WorkerResponseConstants.SUBMIT_PREPARE_ALLOCATION_ACTION, new SubmitPrepareAllocationActionSender());
		addSender(WorkerResponseConstants.SUBMIT_EXECUTION_ACTION, new SubmitExecutionActionSender());
		addSender(WorkerResponseConstants.UPDATE_WORKER_SPEC_LISTENER, new UpdateWorkerSpecListenerSender());
		addSender(WorkerResponseConstants.CREATE_MESSAGE_PROCESSORS, new CreateMessageProcessorsSender());
	}
	
}
