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
package org.ourgrid.broker.communication.operations;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.internal.IResponseTO;

/**
 * This class holds the status of each operation in a replica.
 */
public class GridProcessOperations {

	/** The operations to be executed or being executed in the final phase. */
	private Map<GenericTransferHandle,GetOperation> finalPhaseOperations;

	/** The operations to be executed or being executed in the init phase. */
	private Map<GenericTransferHandle,InitOperation> initPhaseOperations;

	/** The operation to be executed or being executed in the remote phase. */
	private RemoteOperation remotePhaseOperation;

	/** The operation to be executed or being executed in the sabotage check phase. */
	private SabotageCheckOperation sabotageCheckOperation;

	/**
	 * Constructs a new replica status with the given list of phase operations
	 * 
	 * @param initPhaseOperations the list of operations in the init phase.
	 * @param remotePhaseOperations the operation in the remote phase.
	 * @param finalPhaseOperation the list of operations in the final phase.
	 * @param sabotageCheckOperation the operation in the sabotage check phase.
	 */
	public GridProcessOperations( Map<GenericTransferHandle,InitOperation> initPhaseOperations,
								RemoteOperation remotePhaseOperations,
								Map<GenericTransferHandle, GetOperation> finalPhaseOperation,
								SabotageCheckOperation sabotageCheckOperation ) {

		this.initPhaseOperations = initPhaseOperations;
		this.remotePhaseOperation = remotePhaseOperations;
		this.finalPhaseOperations = finalPhaseOperation;
		this.sabotageCheckOperation = sabotageCheckOperation;
	}

	/**
	 * Checks whether all final phase operations have already been executed.
	 * 
	 * @return <b>true</b> if all final phase operations have already been
	 *         executed. <b>false</b> otherwise.
	 */
	public boolean areAllFinalPhaseOperationsFinished() {

		return finalPhaseOperations == null || finalPhaseOperations.isEmpty();
	}


	/**
	 * Checks whether all init phase operations have already been executed.
	 * 
	 * @return <b>true</b> if all init phase operations have already been
	 *         executed. <b>false</b> otherwise.
	 */
	public boolean areAllInitPhaseOperationsFinished() {

		return initPhaseOperations.isEmpty();
	}


	/**
	 * Shutdowns and removes all operations for the replica associated with this
	 * <code>ReplicaOperations</code>.
	 */
	public void cancelOperations(List<IResponseTO> responses) {

		for ( InitOperation operation : initPhaseOperations.values() ) {
			operation.cancelFileTransfer(responses);
		}

		if (finalPhaseOperations != null) {
			for ( GetOperation operation : finalPhaseOperations.values() ) {
				operation.cancelFileTransfer(responses);
			}
			
		}

		initPhaseOperations.clear();
		remotePhaseOperation = null;
		finalPhaseOperations = null;
		sabotageCheckOperation = null;
	}


	/**
	 * Gets all unfinished final phase operations in a list.
	 * 
	 * @return all unfinished final phase operations in a list.
	 */
	public Map<GenericTransferHandle, GetOperation> getFinalPhaseOperations() {

		return finalPhaseOperations;
	}

	/**
	 * Gets all unfinished final phase operations in a list.
	 * 
	 * @return all unfinished final phase operations in a list.
	 */
	public List<GetOperation> getFinalPhaseOperationsList() {

		return new LinkedList<GetOperation>(finalPhaseOperations.values());
	}

	
	public InitOperation getInitPhaseOperation( GenericTransferHandle handle ) {

		return initPhaseOperations.get( handle );
	}


	public RemoteOperation getRemotePhaseOperation() {

		return remotePhaseOperation;
	}

	public SabotageCheckOperation getSabotageCheckOperation() {
		
		return sabotageCheckOperation;
	}


	/**
	 * Gets all unfinished init phase operations in a list.
	 * 
	 * @return all unfinished init phase operations in a list.
	 */
	public Map<GenericTransferHandle,InitOperation> getInitPhaseOperations() {

		return initPhaseOperations;
	}



	public List<Operation> getInitPhaseOperationsList() {

		return new LinkedList<Operation>( initPhaseOperations.values() );
	}


	/**
	 * Checks whether all remote phase operations have already been executed.
	 * 
	 * @return <b>true</b> if all remote phase operations have already been
	 *         executed. <b>false</b> otherwis.
	 */
	public boolean isRemotePhaseOperationFinished() {

		return getRemotePhaseOperation() == null;
	}

	/**
	 * Checks whether all sabotage check phase operations have already been executed.
	 * 
	 * @return <b>true</b> if all sabotage check phase operations have already been
	 *         executed. <b>false</b> otherwise.
	 */
	public boolean isSabotageCheckOperationFinished() {
		
		return getSabotageCheckOperation() == null;
	}

	public InitOperation removeInitPhaseOperation( GenericTransferHandle operationHandle ) {

		return initPhaseOperations.remove( operationHandle );
	}
	
	public InitOperation removeInitPhaseOperation(long handlerId) {
		for (Entry<GenericTransferHandle, InitOperation> initOperationEntry : initPhaseOperations.entrySet()) {
			if (initOperationEntry.getKey().getId() == handlerId) {
				return this.initPhaseOperations.remove(initOperationEntry.getKey());
			}
		}
		return null;
	}
	
	public GetOperation removeFinalPhaseOperation( GenericTransferHandle operationHandle ) {

		if (finalPhaseOperations != null) {
			return finalPhaseOperations.remove( operationHandle );
		}
		
		return null;
	}


	public void removeRemotePhaseOperation() {

		remotePhaseOperation = null;
	}

	public void removeSabotageCheckOperation() {
		
		sabotageCheckOperation = null;
	}

	public boolean areAllOperationsFinished() {

		return areAllInitPhaseOperationsFinished() && isRemotePhaseOperationFinished()
			&& areAllFinalPhaseOperationsFinished()
			&& isSabotageCheckOperationFinished();
	}

	public InitOperation getPutOperation(GenericTransferHandle operationHandle) {
		return initPhaseOperations.get(operationHandle);
	}
	
	public InitOperation getPutOperation(long handlerId) {
		for (Entry<GenericTransferHandle, InitOperation> initOperationEntry : initPhaseOperations.entrySet()) {
			if (initOperationEntry.getKey().getId() == handlerId) {
				return initOperationEntry.getValue();
			}
		}
		return null;
	}
	
	public GetOperation getFinalPhaseOperation(GenericTransferHandle handle) {
		return this.finalPhaseOperations.get(handle);
		
	}
}
