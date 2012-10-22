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
package org.ourgrid.broker.business.requester;

import org.ourgrid.broker.communication.sender.BrokerResponseControl;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.internal.ResponseControlIF;

/**
 * Requirement 301
 */
public class BrokerRequestControl extends OurGridRequestControl {
	
	protected void fillMap() {
		addRequester(BrokerRequestConstants.ADD_JOB, new AddJobRequester());
		addRequester(BrokerRequestConstants.CANCEL_JOB, new CancelJobRequester());
		addRequester(BrokerRequestConstants.CLEAN_ALL_FINISHED_JOBS, new CleanAllFinishedJobsRequester());
		addRequester(BrokerRequestConstants.CLEAN_FINISHED_JOB, new CleanFinishedJobRequester());
		addRequester(BrokerRequestConstants.ERROR_OCURRED_PROCESSOR, new ErrorOcurredProcessorRequester());
		
		addRequester(BrokerRequestConstants.GET_COMPLETE_STATUS, new GetBrokerCompleteStatusRequester());
		addRequester(BrokerRequestConstants.GET_COMPLETE_JOBS_STATUS, new GetCompleteJobsStatusRequester());
		addRequester(BrokerRequestConstants.GET_JOBS_STATUS, new GetJobStatusRequester());
		addRequester(BrokerRequestConstants.GET_PAGED_TASKS, new GetPagedTasksRequester());
		
		
		addRequester(BrokerRequestConstants.HERE_IS_FILE_INFO_PROCESSOR, new HereIsFileInfoProcessorRequester());
		addRequester(BrokerRequestConstants.HERE_IS_GRID_PROCESS_RESULT, new HereIsGridProcessResultRequester());
		addRequester(BrokerRequestConstants.HERE_IS_WORKER, new HereIsWorkerRequester());
		addRequester(BrokerRequestConstants.HERE_IS_WORKER_SPEC, new HereIsWorkerSpecRequester());
		addRequester(BrokerRequestConstants.INCOMING_TRANSFER_COMPLETED, new IncomingTransferCompletedRequester());
		addRequester(BrokerRequestConstants.INCOMING_TRANSFER_FAILED, new IncomingTransferFailedRequester());
		addRequester(BrokerRequestConstants.JOB_ENDED_INTERESTED_IS_DOWN, new JobEndedInterestedIsDownRequester());
		addRequester(BrokerRequestConstants.LOGIN_SUCCEDED, new LoginSucceedRequester());
		addRequester(BrokerRequestConstants.LWP_DO_NOTIFY_FAILURE, new LWPDoNotifyFailureRequester());
		addRequester(BrokerRequestConstants.LWP_DO_NOTIFY_RECOVERY, new LWPDoNotifyRecoveryRequester());
		addRequester(BrokerRequestConstants.NOTIFY_WHEN_JOB_IS_FINISHED, new NotifyWhenJobIsFinishedRequester());
		addRequester(BrokerRequestConstants.OUTGOING_TRANSFER_CANCELLED, new OutgoingTransferCancelledRequester());
		addRequester(BrokerRequestConstants.OUTGOING_TRANSFER_COMPLETED, new OutgoingTransferCompletedRequester());
		addRequester(BrokerRequestConstants.OUTGOING_TRANSFER_FAILED, new OutgoingTransferFailedRequester());
		addRequester(BrokerRequestConstants.SCHEDULER_ACTION, new SchedulerActionRequester());
		addRequester(BrokerRequestConstants.START_BROKER, new StartBrokerRequester());
		addRequester(BrokerRequestConstants.STOP_BROKER, new StopBrokerRequester());
		addRequester(BrokerRequestConstants.TRANSFER_REJECTED, new TransferRejectedRequester());
		addRequester(BrokerRequestConstants.TRANSFER_REQUEST_RECEIVED, new TransferRequestReceivedRequester());
		addRequester(BrokerRequestConstants.UPDATE_TRANSFER_PROGRESS, new UpdateTransferProgressRequester());
		addRequester(BrokerRequestConstants.WCR_SEND_MESSAGE, new WCRSendMessageRequester());
		addRequester(BrokerRequestConstants.WORKER_DO_NOTIFY_FAILURE, new WorkerDoNotifyFailureRequester());
		addRequester(BrokerRequestConstants.WORKER_DO_NOTIFY_RECOVERY, new WorkerDoNotifyRecoveryRequester());
		addRequester(BrokerRequestConstants.WORKER_IS_READY_PROCESSOR, new WorkerIsReadyProcessorRequester());
		addRequester(BrokerRequestConstants.PREEMPTED_WORKER, new PreemptedWorkerRequester());
	}

	@Override
	protected ResponseControlIF createResponseControl() {
		return BrokerResponseControl.getInstance();
	}
}
