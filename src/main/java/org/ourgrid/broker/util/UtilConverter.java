package org.ourgrid.broker.util;

import org.ourgrid.broker.status.GridProcessStatusInfo;
import org.ourgrid.broker.status.GridProcessStatusInfoResult;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.GridProcessPhase;
import org.ourgrid.common.interfaces.to.GridProcessState;

public class UtilConverter {
	
	public static int getJobState(GridProcessState state) {
		
		int value = -1;
		if (state == null) {
			return value;
		}
		
		if (state.toString().equals("FAILED")) {
			value = JobStatusInfo.FAILED;
		} else if (state.toString().equals("FINISHED")) {
			value = JobStatusInfo.FINISHED;
		} else if (state.toString().equals("ABORTED")) {
			value = JobStatusInfo.ABORTED;
		} else if (state.toString().equals("CANCELLED")) {
			value = JobStatusInfo.CANCELLED;
		} else if (state.toString().equals("UNSTARTED")) {
			value = JobStatusInfo.UNSTARTED;
		} else if (state.toString().equals("RUNNING")) {
			value = JobStatusInfo.RUNNING;
		} else if (state.toString().equals("SABOTAGED")) {
			value = JobStatusInfo.SABOTAGED;
		}
		
		return value;
	}
	
	public static int getPhase(GridProcessPhase phase) {
		int value = -1;
		if (phase == null) {
			return value;
		}
		
		if (phase.toString().equals("INIT")) {
			value = GridProcessStatusInfo.INIT;
		} else if (phase.toString().equals("REMOTE")) {
			value = GridProcessStatusInfo.REMOTE;
		} else if (phase.toString().equals("FINAL")) {
			value = GridProcessStatusInfo.FINAL;
		} else if (phase.toString().equals("FINISHED")) {
			value = GridProcessStatusInfo.FINISHED;
		}	
		
		return value;
	}
	
	public static int getProcessError(GridProcessErrorTypes type) {
		int value = -1;
		if (type == null) {
			return value;
		}
		
		if (type.toString().equals("APPLICATION_ERROR")) {
			value = GridProcessStatusInfoResult.APPLICATION_ERROR;
		} else if (type.toString().equals("EXECUTION_ERROR")) {
			value = GridProcessStatusInfoResult.EXECUTION_ERROR;
		} else if (type.toString().equals("IO_ERROR")) {
			value = GridProcessStatusInfoResult.IO_ERROR;
		} else if (type.toString().equals("FILE_TRANSFER_ERROR")) {
			value = GridProcessStatusInfoResult.FILE_TRANSFER_ERROR;
		} else if (type.toString().equals("INVALID_SESSION")) {
			value = GridProcessStatusInfoResult.INVALID_SESSION;
		} else if (type.toString().equals("MACHINE_FAILURE")) {
			value = GridProcessStatusInfoResult.MACHINE_FAILURE;
		} else if (type.toString().equals("MACHINE_NOT_IDLE")) {
			value = GridProcessStatusInfoResult.MACHINE_NOT_IDLE;
		} else if (type.toString().equals("CONCURRENT_RUNNING")) {
			value = GridProcessStatusInfoResult.CONCURRENT_RUNNING;
		} else if (type.toString().equals("BROKER_ERROR")) {
			value = GridProcessStatusInfoResult.BROKER_ERROR;
		} else if (type.toString().equals("SABOTAGE_ERROR")) {
			value = GridProcessStatusInfoResult.SABOTAGE_ERROR;
		}	
		
		return value;
	}
	
	public static GridProcessState getJobState(int state) {
		
		GridProcessState value = null;
		
		if (state == JobStatusInfo.FAILED) {
			value = GridProcessState.FAILED;
		} else if (state == JobStatusInfo.FINISHED) {
			value = GridProcessState.FINISHED;
		} else if (state == JobStatusInfo.ABORTED) {
			value = GridProcessState.ABORTED;
		} else if (state == JobStatusInfo.CANCELLED) {
			value = GridProcessState.CANCELLED;
		} else if (state == JobStatusInfo.UNSTARTED) {
			value = GridProcessState.UNSTARTED;
		} else if (state == JobStatusInfo.RUNNING) {
			value = GridProcessState.RUNNING;
		} else if (state == JobStatusInfo.SABOTAGED) {
			value = GridProcessState.SABOTAGED;
		}
		
		return value;
	}
}
