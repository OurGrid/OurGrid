package org.ourgrid.worker.business.requester;

public interface WorkerRequestConstants {
	
	public static final String START_WORKER = "START_WORKER";
	public static final String START_WORK = "START_WORK";
	public static final String RESUME_WORKER = "RESUME_WORKER";
	public static final String STOP_WORKER = "STOP_WORKER";
	public static final String GET_COMPLETE_STATUS = "GET_COMPLETE_STATUS";
	public static final String GET_MASTER_PEER = "GET_MASTER_PEER";
	public static final String GET_STATUS = "GET_STATUS";
	public static final String INCOMING_TRANSFER_FAILED = "INCOMING_TRANSFER_FAILED";
	public static final String PAUSE_WORKER = "PAUSE_WORKER";
	public static final String TRANSFER_REQUEST_RECEIVED = "TRANSFER_REQUEST_RECEIVED";
	public static final String WORK_FOR_BROKER = "WORK_FOR_BROKER";
	public static final String WORK_FOR_PEER = "WORK_FOR_PEER";
	public static final String STOP_WORKING = "STOP_WORKING";
	public static final String INCOMING_TRANSFER_COMPLETED = "INCOMING_TRANSFER_COMPLETED";
	public static final String TRANSFER_REJECTED = "TRANSFER_REJECTED";
	public static final String OUTGOING_TRANSFER_CANCELLED = "OUTGOING_TRANSFER_CANCELLED";
	public static final String OUTGOING_TRANSFER_FAILED = "OUTGOING_TRANSFER_FAILED";
	public static final String WORKER_CLIENT_IS_UP = "WORKER_CLIENT_IS_UP";
	public static final String WORKER_CLIENT_IS_DOWN = "WORKER_CLIENT_IS_DOWN";
	public static final String UPDATE_TRANSFER_PROGRESS = "UPDATE_TRANSFER_PROGRESS";
	public static final String OUTGOING_TRANSFER_COMPLETED = "OUTGOING_TRANSFER_COMPLETED";
	public static final String CONCURRENT_EXECUTIONS_ERROR = "CONCURRENT_EXECUTIONS_ERROR";
	public static final String EXECUTION_ERROR = "EXECUTION_ERROR";
	public static final String EXECUTION_RESULT = "EXECUTION_RESULT";
	public static final String EXECUTION_IS_RUNNING = "EXECUTION_IS_RUNNING";
	public static final String ALLOCATION_ERROR = "ALLOCATION_ERROR";
	public static final String READY_FOR_ALLOCATION = "READY_FOR_ALLOCATION";
	public static final String REMOTE_WORK_FOR_BROKER = "REMOTE_WORK_FOR_BROKER";
	public static final String WMC_DO_NOTIFY_FAILURE = "WMC_DO_NOTIFY_FAILURE";
	public static final String WMC_DO_NOTIFY_RECOVERY = "WMC_DO_NOTIFY_RECOVERY";
	public static final String REPORT_WORK_ACCOUNTING = "REPORT_WORK_ACCOUNTING";
	public static final String REPORT_WORKER_SPEC = "REPORT_WORKER_SPEC";
	public static final String GET_FILE_INFO_PROCESSOR = "GET_FILE_INFO_PROCESSOR";
	public static final String GET_FILES_PROCESSOR = "GET_FILES_PROCESSOR";
	public static final String REMOTE_EXECUTE_PROCESSOR = "REMOTE_EXECUTE_PROCESSOR";
	public static final String LINUX_IDLENESS_DETECTOR_ACTION = "LINUX_IDLENESS_DETECTOR_ACTION";
	public static final String WIN_IDLENESS_DETECTOR_ACTION = "WIN_IDLENESS_DETECTOR_ACTION";
	public static final String WORKER_SPEC_BASED_IDLENESS_DETECTOR_ACTION = "WORKER_SPEC_BASED_IDLENESS_DETECTOR_ACTION";
	public static final String WORKER_LOGIN_SUCCEEDED = "WORKER_LOGIN_SUCCEEDED";
	public static final String REMOTE_WORKER_MANAGEMENT_CLIENT_DO_NOTIFY_FAILURE = "REMOTE_WORKER_MANAGEMENT_CLIENT_DO_NOTIFY_FAILURE";
	public static final String REMOTE_WORKER_MANAGEMENT_CLIENT_DO_NOTIFY_RECOVERY = "REMOTE_WORKER_MANAGEMENT_CLIENT_DO_NOTIFY_RECOVERY";
}
