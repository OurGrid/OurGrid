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
package org.ourgrid.common.interfaces.to;

import java.io.Serializable;
import java.util.Map;

import org.ourgrid.broker.communication.operations.GetOperation;
import org.ourgrid.broker.communication.operations.InitOperation;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.replicaexecutor.SabotageCheckResult;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.common.util.TimeDataGenerator;
import org.ourgrid.worker.business.controller.GridProcessError;

/**
 * Description: This class represents the result of a replica remote phase
 * execution.
 */
public class GridProcessExecutionResult implements Serializable {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 40L;

	/** An identification of the replica associated to this result */
	private final GridProcessHandle replicaHandle;

	/**
	 * Holds the result of a replica execution (if there is any).
	 */
	private ExecutorResult executorResult;

	/**
	 * Holds the error occurred during a replica execution (if there is any).
	 */
	private GridProcessError executionError;

	/**
	 * Init phase execution time.
	 */
	private TimeDataGenerator initData;

	/**
	 * Remote phase execution time.
	 */
	private TimeDataGenerator remoteData;

	/** Final phase execution time. */
	private TimeDataGenerator finalData;

	/** Accounting information */
	private GridProcessAccounting replicaAccounting;

	/** This object will be created if there is a sabotage check phase in the replica workflow */
	private SabotageCheckResult sabotageCheckResult;
	
	private Map<InitOperation, TransferTime> initOperations;
	private Map<GetOperation, TransferTime> getOperations;
	

	/**
	 * Constructs a new instance of this type, based on the given replica
	 * handle.
	 * 
	 * @param handle the handle of the replica to which the new instance will be
	 *        associated.
	 */
	public GridProcessExecutionResult( GridProcessHandle handle ) {
		this.initOperations = CommonUtils.createSerializableMap();
		this.getOperations = CommonUtils.createSerializableMap();
		this.replicaHandle = handle;
		initData = new TimeDataGenerator( "Replica " + handle + " init phase duration: " );
		remoteData = new TimeDataGenerator( "Replica " + handle + " remote phase duration: " );
		finalData = new TimeDataGenerator( "Replica " + handle + " final phase duration: " );
	}

	public void addInitOperation(InitOperation operation) {
		this.initOperations.put(operation, new TransferTime());
	}
	
	public TransferTime getInitOperationTransferTime(InitOperation operation) {
		return this.initOperations.get(operation);
	}
	
	public void addGetOperation(GetOperation operation) {
		this.getOperations.put(operation, new TransferTime());
	}
	
	public TransferTime getGetOperationTransferTime(GetOperation operation) {
		return this.getOperations.get(operation);
	}

	/**
	 * Gets the result of a replica execution (if there is any).
	 * 
	 * @return the result of a replica execution (if there is any).
	 */
	public ExecutorResult getExecutorResult() {

		return executorResult;
	}


	/**
	 * Sets the result of a replica execution.
	 * 
	 * @param result the result of a replica execution.
	 */
	public void setExecutorResult( ExecutorResult result ) {

		executorResult = result;
	}


	/**
	 * Gets the error occurred during a replica execution (if there is any).
	 * 
	 * @return the error occurred during a replica execution (if there is any).
	 */
	public GridProcessError getExecutionError() {

		return executionError;
	}


	/**
	 * Sets the error occurred during a replica execution.
	 * 
	 * @param executionError the error occurred during a replica execution.
	 */
	public void setExecutionError( GridProcessError executionError ) {

		this.executionError = executionError;
	}
	
	/**
	 * @return the sabotageCheckResult
	 */
	public SabotageCheckResult getSabotageCheckResult() {
		return sabotageCheckResult;
	}


	/**
	 * @param sabotageCheckResult the sabotageCheckResult to set
	 */
	public void setSabotageCheckResult(SabotageCheckResult sabotageCheckResult) {
		this.sabotageCheckResult = sabotageCheckResult;
	}


	/**
	 * Sets the init phase start time of a replica execution
	 */
	public void setInitPhaseStartTime() {

		initData.setStartTime();
	}


	/**
	 * Sets the init phase end time of a replica execution
	 */
	public void setInitPhaseEndTime() {

		initData.setEndTime();
	}


	/**
	 * Sets the remote phase start time of a replica execution
	 */
	public void setRemotePhaseStartTime() {

		remoteData.setStartTime();
	}


	/**
	 * Sets the remote phase end time of a replica execution
	 */
	public void setRemotePhaseEndTime() {

		remoteData.setEndTime();
	}


	/**
	 * Sets the final phase start time of a replica execution
	 */
	public void setFinalPhaseStartTime() {

		finalData.setStartTime();
	}


	/**
	 * Sets the final phase end time of a replica execution
	 */
	public void setFinalPhaseEndTime() {

		finalData.setEndTime();
	}


	/**
	 * Returns an identification of the replica associated to this result
	 */
	public GridProcessHandle getReplicaHandle() {

		return replicaHandle;
	}


	public TimeDataGenerator getInitData() {

		return initData;
	}


	public TimeDataGenerator getRemoteData() {

		return remoteData;
	}


	public TimeDataGenerator getFinalData() {

		return finalData;
	}


	public GridProcessAccounting getReplicaAccountingInfo() {

		return replicaAccounting;
	}


	public void setReplicaAccountingInfo( GridProcessAccounting replicaAccounting ) {

		this.replicaAccounting = replicaAccounting;
	}

	public boolean wasSabotaged() {
		if (sabotageCheckResult == null) {
			return false;
		}
		
		return sabotageCheckResult.wasSabotaged();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((executionError == null) ? 0 : executionError.hashCode());
		result = prime * result
				+ ((executorResult == null) ? 0 : executorResult.hashCode());
		result = prime * result
				+ ((finalData == null) ? 0 : finalData.hashCode());
		result = prime * result
				+ ((initData == null) ? 0 : initData.hashCode());
		result = prime * result
				+ ((remoteData == null) ? 0 : remoteData.hashCode());
		result = prime
				* result
				+ ((replicaAccounting == null) ? 0 : replicaAccounting
						.hashCode());
		result = prime * result
				+ ((replicaHandle == null) ? 0 : replicaHandle.hashCode());
		result = prime
				* result
				+ ((sabotageCheckResult == null) ? 0 : sabotageCheckResult
						.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GridProcessExecutionResult other = (GridProcessExecutionResult) obj;
		if (executionError == null) {
			if (other.executionError != null)
				return false;
		} else if (!executionError.equals(other.executionError))
			return false;
		if (executorResult == null) {
			if (other.executorResult != null)
				return false;
		} else if (!executorResult.equals(other.executorResult))
			return false;
		if (finalData == null) {
			if (other.finalData != null)
				return false;
		} else if (!finalData.equals(other.finalData))
			return false;
		if (initData == null) {
			if (other.initData != null)
				return false;
		} else if (!initData.equals(other.initData))
			return false;
		if (remoteData == null) {
			if (other.remoteData != null)
				return false;
		} else if (!remoteData.equals(other.remoteData))
			return false;
		if (replicaAccounting == null) {
			if (other.replicaAccounting != null)
				return false;
		} else if (!replicaAccounting.equals(other.replicaAccounting))
			return false;
		if (replicaHandle == null) {
			if (other.replicaHandle != null)
				return false;
		} else if (!replicaHandle.equals(other.replicaHandle))
			return false;
		if (sabotageCheckResult == null) {
			if (other.sabotageCheckResult != null)
				return false;
		} else if (!sabotageCheckResult.equals(other.sabotageCheckResult))
			return false;
		return true;
	}
	
	public Map<InitOperation, TransferTime> getInitOperations() {
		return initOperations;
	}

	public Map<GetOperation, TransferTime> getGetOperations() {
		return getOperations;
	}

}
