package org.ourgrid.broker.status;

import java.io.Serializable;

import org.ourgrid.common.executor.ExecutorResult;

public class GridProcessStatusInfoResult implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2062461228474647408L;
	public static final int APPLICATION_ERROR = 10;
	public static final int EXECUTION_ERROR = 11;
	public static final int IO_ERROR = 12;
	public static final int FILE_TRANSFER_ERROR = 13;
	public static final int INVALID_SESSION = 14;
	public static final int MACHINE_FAILURE = 15;
	public static final int MACHINE_NOT_IDLE = 16;
	public static final int CONCURRENT_RUNNING = 17;
	public static final int BROKER_ERROR = 18;
	public static final int SABOTAGE_ERROR = 19;
	
	private String executionError;
	private String executionErrorCause;
	private long initDataTime;
	private long remoteDataTime;
	private long finalDataTime;
	private ExecutorResult executorResult;
	private String sabotageCheck;
	
	public GridProcessStatusInfoResult() {}
	
	public GridProcessStatusInfoResult(String executionError, String executionErrorCause, long initDataTime,
			long remoteDataTime, long finalDataTime, ExecutorResult executorResult) {
		
		this.executionError = executionError;
		this.executionErrorCause = executionErrorCause;
		this.initDataTime = initDataTime;
		this.remoteDataTime = remoteDataTime;
		this.finalDataTime = finalDataTime;
		this.executorResult = executorResult;
	}
	
	
	public String getExecutionError() {
		return executionError;
	}
	
	public String getExecutionErrorCause() {
		return executionErrorCause;
	}

	public long getInitDataTime() {
		return initDataTime;
	}

	public long getRemoteDataTime() {
		return remoteDataTime;
	}

	public long getFinalDataTime() {
		return finalDataTime;
	}

	public ExecutorResult getExecutorResult() {
		return executorResult;
	}

	public void setExecutionError(String executionError) {
		this.executionError = executionError;
	}

	public void setExecutionErrorCause(String executionErrorCause) {
		this.executionErrorCause = executionErrorCause;
	}

	public void setInitDataTime(long initDataTime) {
		this.initDataTime = initDataTime;
	}

	public void setRemoteDataTime(long remoteDataTime) {
		this.remoteDataTime = remoteDataTime;
	}

	public void setFinalDataTime(long finalDataTime) {
		this.finalDataTime = finalDataTime;
	}

	public void setExecutorResult(ExecutorResult executorResult) {
		this.executorResult = executorResult;
	}

	public void setSabotageCheck(String sabotageCheck) {
		this.sabotageCheck = sabotageCheck;
	}

	public String getSabotageCheck() {
		return sabotageCheck;
	}
}
