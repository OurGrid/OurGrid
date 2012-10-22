package org.ourgrid.broker.business.requester;

public interface BrokerRequestConstants {

	public static final String START_BROKER = "START_BROKER";
	public static final String STOP_BROKER = "STOP_BROKER";
	public static final String ADD_JOB = "ADD_JOB";
	public static final String CANCEL_JOB = "CANCEL_JOB";
	public static final String CLEAN_ALL_FINISHED_JOBS = "CLEAN_ALL_FINISHED_JOBS";
	public static final String CLEAN_FINISHED_JOB = "CLEAN_FINISHED_JOB";
	public static final String NOTIFY_WHEN_JOB_IS_FINISHED = "NOTIFY_WHEN_JOB_IS_FINISHED";
	
	public static final String GET_COMPLETE_STATUS = "GET_COMPLETE_STATUS";
	public static final String GET_JOBS_STATUS = "GET_JOBS_STATUS";
	public static final String GET_COMPLETE_JOBS_STATUS = "GET_COMPLETE_JOBS_STATUS";
	public static final String GET_PAGED_TASKS = "GET_PAGED_TASKS";
	
	public static final String JOB_ENDED_INTERESTED_IS_DOWN = "JOB_ENDED_INTERESTED_IS_DOWN";
	public static final String HERE_IS_WORKER = "HERE_IS_WORKER";
	public static final String OUTGOING_TRANSFER_CANCELLED = "OUTGOING_TRANSFER_CANCELLED";
	public static final String OUTGOING_TRANSFER_COMPLETED = "OUTGOING_TRANSFER_COMPLETED";
	public static final String OUTGOING_TRANSFER_FAILED = "OUTGOING_TRANSFER_FAILED";
	public static final String INCOMING_TRANSFER_COMPLETED = "INCOMING_TRANSFER_COMPLETED";
	public static final String INCOMING_TRANSFER_FAILED = "INCOMING_TRANSFER_FAILED";
	public static final String TRANSFER_REJECTED ="TRANSFER_REJECTED";
	public static final String UPDATE_TRANSFER_PROGRESS = "UPDATE_TRANSFER_PROGRESS";
	public static final String TRANSFER_REQUEST_RECEIVED = "TRANSFER_REQUEST_RECEIVED";
	public static final String WORKER_DO_NOTIFY_FAILURE = "WORKER_DO_NOTIFY_FAILURE";
	public static final String LWP_DO_NOTIFY_FAILURE = "LWP_DO_NOTIFY_FAILURE";
	public static final String LWP_DO_NOTIFY_RECOVERY = "LWP_DO_NOTIFY_RECOVERY";
	public static final String ERROR_OCURRED_PROCESSOR = "ERROR_OCURRED_PROCESSOR";
	public static final String HERE_IS_FILE_INFO_PROCESSOR = "HERE_IS_FILE_INFO_PROCESSOR";
	public static final String HERE_IS_GRID_PROCESS_RESULT = "HERE_IS_GRID_PROCESS_RESULT";
	public static final String HERE_IS_WORKER_SPEC = "HERE_IS_WORKER_SPEC";
	public static final String WORKER_IS_READY_PROCESSOR = "WORKER_IS_READY";
	public static final String WORKER_IS_UNAVAILABLE_PROCESSOR = "WORKER_IS_UNAVAILABLE";
	public static final String SCHEDULER_ACTION = "SCHEDULER_ACTION";
	public static final String WCR_SEND_MESSAGE = "WCR_SEND_MESSAGE";
	public static final String LOGIN_SUCCEDED = "LOGIN_SUCCEDED";
	public static final String WORKER_DO_NOTIFY_RECOVERY = "WORKER_DO_NOTIFY_RECOVERY";
	public static final String PREEMPTED_WORKER = "PREEMPTED_WORKER";

}
