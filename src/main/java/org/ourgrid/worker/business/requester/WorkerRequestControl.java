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
package org.ourgrid.worker.business.requester;

import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.internal.ResponseControlIF;
import org.ourgrid.worker.communication.sender.WorkerResponseControl;

public class WorkerRequestControl extends OurGridRequestControl {
	
	protected void fillMap() {
		addRequester(WorkerRequestConstants.ALLOCATION_ERROR, new AllocationErrorRequester());
		addRequester(WorkerRequestConstants.EXECUTION_ERROR, new ExecutionErrorRequester());
		addRequester(WorkerRequestConstants.EXECUTION_IS_RUNNING, new ExecutionIsRunningRequester());
		addRequester(WorkerRequestConstants.EXECUTION_RESULT, new ExecutionResultRequester());
		addRequester(WorkerRequestConstants.GET_COMPLETE_STATUS, new GetWorkerCompleteStatusRequester());
		addRequester(WorkerRequestConstants.GET_FILE_INFO_PROCESSOR, new GetFileInfoProcessorRequester());
		addRequester(WorkerRequestConstants.GET_FILES_PROCESSOR, new GetFilesProcessorRequester());
		addRequester(WorkerRequestConstants.GET_MASTER_PEER, new GetMasterPeerRequester());
		addRequester(WorkerRequestConstants.GET_STATUS, new GetStatusRequester());
		addRequester(WorkerRequestConstants.INCOMING_TRANSFER_COMPLETED, new IncomingTransferCompletedRequester());
		addRequester(WorkerRequestConstants.INCOMING_TRANSFER_FAILED, new IncomingTransferFailedRequester());
		addRequester(WorkerRequestConstants.OUTGOING_TRANSFER_CANCELLED, new OutgoingTransferCancelledRequester());
		addRequester(WorkerRequestConstants.OUTGOING_TRANSFER_COMPLETED, new OutgoingTransferCompletedRequester());
		addRequester(WorkerRequestConstants.OUTGOING_TRANSFER_FAILED, new OutgoingTransferFailedRequester());
		addRequester(WorkerRequestConstants.PAUSE_WORKER, new PauseWorkerRequester());
		addRequester(WorkerRequestConstants.READY_FOR_ALLOCATION, new ReadyForAllocationRequester());
		addRequester(WorkerRequestConstants.REMOTE_WORK_FOR_BROKER, new RemoteWorkForBrokerRequester());
		addRequester(WorkerRequestConstants.REMOTE_EXECUTE_PROCESSOR, new RemoteExecuteProcessorRequester());
		addRequester(WorkerRequestConstants.REPORT_WORKER_SPEC, new ReportWorkerSpecActionRequester());
		addRequester(WorkerRequestConstants.REPORT_WORK_ACCOUNTING, new ReportWorkAccountingActionRequester());
		addRequester(WorkerRequestConstants.RESUME_WORKER, new ResumeWorkerRequester());
		addRequester(WorkerRequestConstants.START_WORK, new StartWorkRequester());
		addRequester(WorkerRequestConstants.START_WORKER, new StartWorkerRequester());
		addRequester(WorkerRequestConstants.STOP_WORKER, new StopWorkerRequester());
		addRequester(WorkerRequestConstants.STOP_WORKING, new StopWorkingRequester());
		addRequester(WorkerRequestConstants.TRANSFER_REJECTED, new TransferRejectedRequester());
		addRequester(WorkerRequestConstants.TRANSFER_REQUEST_RECEIVED, new TransferRequestReceivedRequester());
		addRequester(WorkerRequestConstants.UPDATE_TRANSFER_PROGRESS, new UpdateTransferProgressRequester());
		addRequester(WorkerRequestConstants.WMC_DO_NOTIFY_FAILURE, new WorkerManagementClientDoNotifyFailureRequester());
		addRequester(WorkerRequestConstants.WMC_DO_NOTIFY_RECOVERY, new WorkerManagementClientDoNotifyRecoveryRequester());
		addRequester(WorkerRequestConstants.WORK_FOR_BROKER, new WorkForBrokerRequester());
		addRequester(WorkerRequestConstants.WORK_FOR_PEER, new WorkForPeerRequester());
		addRequester(WorkerRequestConstants.WORKER_CLIENT_IS_DOWN, new WorkerClientIsDownRequester());
		addRequester(WorkerRequestConstants.WORKER_CLIENT_IS_UP, new WorkerClientIsUpRequester());
		addRequester(WorkerRequestConstants.WORKER_SPEC_BASED_IDLENESS_DETECTOR_ACTION, new WorkerSpecBasedIdlenessDetectorActionRequester());
		addRequester(WorkerRequestConstants.LINUX_IDLENESS_DETECTOR_ACTION, new LinuxDevInputIdlenessDetectorActionRequester());
		addRequester(WorkerRequestConstants.WIN_IDLENESS_DETECTOR_ACTION, new WinIdlenessDetectorActionRequester());
		addRequester(WorkerRequestConstants.MACOS_IDLENESS_DETECTOR_ACTION, new MacOSIdlenessDetectorActionRequester());
		addRequester(WorkerRequestConstants.WORKER_LOGIN_SUCCEEDED, new WorkerLoginSucceededRequester());
		addRequester(WorkerRequestConstants.REMOTE_WORKER_MANAGEMENT_CLIENT_DO_NOTIFY_FAILURE, new RemoteWorkerManagementClientDoNotifyFailureRequester());
	}

	@Override
	protected ResponseControlIF createResponseControl() {
		return WorkerResponseControl.getInstance();
	}
}
