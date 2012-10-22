package org.ourgrid.broker.communication.sender;

import org.ourgrid.common.internal.OurGridResponseControl;

/**
 * Requirement 301
 */
public class BrokerResponseControl extends OurGridResponseControl {

	
	private static BrokerResponseControl instance;
	
	
	public static BrokerResponseControl getInstance() {
		if (instance == null) {
			instance = new BrokerResponseControl();
		}
		return instance;
	}
	
	protected void addEntitySenders() {
		addSender(BrokerResponseConstants.ACCEPT_TRANSFER, new AcceptTransferSender());
		addSender(BrokerResponseConstants.BROKER_MESSAGE_PROCESSOR, new BrokerMessageProcessorSender());
		addSender(BrokerResponseConstants.DISPOSE_WORKER, new DisposeWorkerSender());
		addSender(BrokerResponseConstants.FINISH_REQUEST, new FinishRequestSender());
		
		addSender(BrokerResponseConstants.HERE_IS_COMPLETE_STATUS, new HereIsBrokerCompleteStatusSender());
		addSender(BrokerResponseConstants.HERE_IS_COMPLETE_JOBS_STATUS, new HereIsCompleteJobsStatusSender());
		addSender(BrokerResponseConstants.HERE_IS_JOBS_STATUS, new HereIsJobsStatusSender());
		addSender(BrokerResponseConstants.HERE_IS_PAGED_TASKS, new HereIsPagedTasksSender());
		
		addSender(BrokerResponseConstants.JOB_ENDED, new JobEndedSender());
		addSender(BrokerResponseConstants.LOGIN, new LoginSender());
		addSender(BrokerResponseConstants.PAUSE_REQUEST, new PauseRequestSender());
		addSender(BrokerResponseConstants.REQUEST_WORKERS, new RequestWorkersSender());
		addSender(BrokerResponseConstants.RESUME_REQUEST, new ResumeRequestSender());
		addSender(BrokerResponseConstants.SCHEDULED_ACTION_TO_RUN_ONCE, new ScheduleActionToRunOnceSender());
		addSender(BrokerResponseConstants.START_TRANSFER, new StartTransferSender());
		addSender(BrokerResponseConstants.START_WORK, new StartWorkSender());
		addSender(BrokerResponseConstants.UNWANT_WORKER, new UnwantWorkerSender());
		addSender(BrokerResponseConstants.MESSAGE_HANDLE, new MessageHandleSender());
		addSender(BrokerResponseConstants.OPERATION_SUCCEDED, new OperationSucceedSender());
		addSender(BrokerResponseConstants.REPORT_REPLICA_ACCOUNTING, new ReportReplicaAccountingSender());
		addSender(BrokerResponseConstants.LWP_HERE_IS_JOB_STATS, new LWPHereIsJobStatsSender());
		addSender(BrokerResponseConstants.CREATE_MESSAGE_PROCESSORS, new CreateMessageProcessorsSender());
	}
	
}
