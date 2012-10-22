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

import java.io.Serializable;

import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.interfaces.to.GridProcessHandle;

/**
 * This type implements common methods for <code>Operation</code> types.
 */
public abstract class AbstractOperation implements Operation, Serializable, Comparable<AbstractOperation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The worker that will receive the file. */
	private String workerID;

	/** The ID of the request that generated this operation. */
	private long requestID;

	/** Operation type */
	private OperationType type;

	private final GridProcessHandle replicaHandle;


	/**
	 * Constructs a operation will all information that is common to all
	 * operations
	 * 
	 * @param replicaHandle
	 * @param requestID
	 * @param worker
	 */
	public AbstractOperation( GridProcessHandle replicaHandle, long requestID, String workerID) {

		this.replicaHandle = replicaHandle;
		this.workerID = workerID;
		this.requestID = requestID;
		
	}


	public String getWorkerID() {

		return workerID;
	}


	public long getRequestID() {

		return requestID;
	}


	public abstract GenericTransferHandle getHandle();


	public final OperationType getType() {

		return type;
	}


	public GridProcessHandle getGridProcessHandle() {

		return replicaHandle;
	}


	protected void setType( OperationType newType ) {

		this.type = newType;
	}


	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + getGridProcessHandle().hashCode();
		result = PRIME * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( !(obj instanceof AbstractOperation) )
			return false;
		final AbstractOperation other = (AbstractOperation) obj;

		if ( !(getGridProcessHandle() == null ? other.getGridProcessHandle() == null : getGridProcessHandle().equals( other.getGridProcessHandle() )) )
			return false;
		if ( !(type == null ? other.type == null : type.equals( other.type )) )
			return false;
		return true;
	}


	@Override
	public String toString() {

		return "[" + this.getClass().getSimpleName() + "]. " + this.getGridProcessHandle();
	}
	
	public int compareTo(AbstractOperation o) {
		
		if (replicaHandle.compareTo(replicaHandle) > 0) {
			return 1;
		} 
		
		if (replicaHandle.compareTo(replicaHandle) < 0) {
			return -1;
		} 
		
		return type.compareTo(o.type);
	}
}
