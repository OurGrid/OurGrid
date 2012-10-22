package org.ourgrid.broker.communication.sender;

import org.ourgrid.common.internal.OurGridResponseConstants;


public interface BrokerResponseConstants extends OurGridResponseConstants {

	public static final String HERE_IS_COMPLETE_STATUS = "HERE_IS_COMPLETE_STATUS";
	public static final String HERE_IS_COMPLETE_JOBS_STATUS = "HERE_IS_COMPLETE_JOBS_STATUS";
	public static final String HERE_IS_JOBS_STATUS = "HERE_IS_JOBS_STATUS";
	public static final String HERE_IS_PAGED_TASKS = "HERE_IS_PAGED_TASKS";
	
	public static final String ADD_ACCOUNTING_AGGREGATOR = "ADD_ACCOUNTING_AGGREGATOR";
	public static final String REMOVE_ACCOUNTING_AGGREGATOR = "REMOVE_ACCOUNTING_AGGREGATOR";
	public static final String DISPOSE_WORKER = "DISPOSE_WORKER";
	public static final String LOGIN = "LOGIN";
	public static final String ACCEPT_TRANSFER = "ACCEPT_TRANSFER";
	public static final String REQUEST_WORKERS = "REQUEST_WORKERS";
	public static final String JOB_ENDED = "JOB_ENDED";
	public static final String RESUME_REQUEST = "RESUME_REQUEST";
	public static final String SCHEDULED_ACTION_TO_RUN_ONCE = "SCHEDULED_ACTION_TO_RUN_ONCE";
	public static final String PAUSE_REQUEST = "PAUSE_REQUEST";
	public static final String START_WORK = "START_WORK";
	public static final String START_TRANSFER = "START_TRANSFER";
	public static final String FINISH_REQUEST = "FINISH_REQUEST";
	public static final String UNWANT_WORKER = "UNWANT_WORKER";
	public static final String BROKER_MESSAGE_PROCESSOR = "BROKER_MESSAGE_PROCESSOR";
	public static final String MESSAGE_HANDLE = "MESSAGE_HANDLE";
	public static final String OPERATION_SUCCEDED = "OPERATION_SUCCEDED";
	public static final String REPORT_REPLICA_ACCOUNTING = "REPORT_REPLICA_ACCOUNTING";
	public static final String LWP_HERE_IS_JOB_STATS = "LWP_HERE_IS_JOB_STATS";
}
