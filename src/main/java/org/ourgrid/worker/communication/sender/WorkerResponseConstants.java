package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.internal.OurGridResponseConstants;


public interface WorkerResponseConstants extends OurGridResponseConstants {
	
	public static final String ACCEPT_TRANSFER = "ACCEPT_TRANSFER";
	public static final String CREATE_EXECUTOR = "CREATE_EXECUTOR";
	public static final String EXECUTOR_KILL_COMMAND = "EXECUTOR_KILL_COMMAND";
	public static final String EXECUTOR_KILL_PREPARING_ALLOCATION = "EXECUTOR_KILL_PREPARING_ALLOCATION";
	public static final String HERE_IS_COMPLETE_STATUS = "HERE_IS_COMPLETE_STATUS";
	public static final String HERE_IS_MASTER_PEER = "HERE_IS_MASTER_PEER";
	public static final String HERE_IS_STATUS = "HERE_IS_STATUS";
	public static final String REJECT_TRANSFER = "REJECT_TRANSFER";
	public static final String START_TRANSFER = "START_TRANSFER";
	public static final String STATUS_CHANGED = "STATUS_CHANGED";
	public static final String STATUS_CHANGED_ALLOCATED_FOR_PEER = "STATUS_CHANGED_ALLOCATED_FOR_PEER";
	public static final String PAUSE_WORKER = "PAUSE_WORKER";
	public static final String RESUME_WORKER = "RESUME_WORKER";
	public static final String REMOTE_PEER_STATUS_CHANGED_ALLOCATED_FOR_BROKER = "REMOTE_PEER_STATUS_CHANGED_ALLOCATED_FOR_BROKER";
	public static final String MASTER_PEER_STATUS_CHANGED_ALLOCATED_FOR_BROKER = "MASTER_PEER_STATUS_CHANGED_ALLOCATED_FOR_BROKER";
	public static final String SUBMIT_PREPARE_ALLOCATION_ACTION = "SUBMIT_PREPARE_ALLOCATION_ACTION";
	public static final String SUBMIT_EXECUTION_ACTION = "SUBMIT_EXECUTION_ACTION";
	public static final String CANCEL_BEGIN_ALLOCATION_ACTION = "CANCEL_BEGIN_ALLOCATION_ACTION";
	public static final String CANCEL_EXECUTION_ACTION = "CANCEL_EXECUTION_ACTION";
	public static final String CANCEL_REPORT_ACCOUNTING_ACTION = "CANCEL_REPORT_ACCOUNTING_ACTION";
	public static final String REPORT_WORK_ACCOUNTING = "REPORT_WORK_ACCOUNTING";
	public static final String UPDATE_WORKER_SPEC_LISTENER = "UPDATE_WORKER_SPEC_LISTENER";
	public static final String LOGIN_AT_PEER = "LOGIN_AT_PEER";
	public static final String EXECUTOR_SHUTDOWN_COMMAND = "EXECUTOR_SHUTDOWN";
	
}
