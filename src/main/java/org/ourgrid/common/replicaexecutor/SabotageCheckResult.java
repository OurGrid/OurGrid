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
package org.ourgrid.common.replicaexecutor;

import java.io.Serializable;

import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.worker.business.controller.GridProcessError;

/**
 * This object has the information about the sabotage check command execution.
 * A error in the check command could be occur so the sabotage detection is undecidable, in
 * this case the replica is considered finished and the check command error should be in
 * this object 
 * 
 */
public class SabotageCheckResult implements Serializable{

	private static final long serialVersionUID = 1L;

	private final boolean wasSabotaged;

	//Holds the result of a sabotage check execution (if there is any).
	private final ExecutorResult executorResult;

	//Holds the error occurred during a sabotage check execution (if there is any).
	private final GridProcessError executionError;
	
	/**
	 * @param wasSabotaged
	 * @param executorResult
	 * @param executionError
	 */
	public SabotageCheckResult(boolean wasSabotaged, ExecutorResult executorResult, 
			GridProcessError executionError) {

		this.wasSabotaged = wasSabotaged;
		this.executorResult = executorResult;
		this.executionError = executionError;
	}

	public boolean wasSabotaged() {
		return wasSabotaged;
	}
	
	public ExecutorResult getSabotageCheckExecutionResult() {
		return executorResult;
	}
	
	public GridProcessError getSabotageCheckExecutionError() {
		return executionError;
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
		result = prime * result + (wasSabotaged ? 1231 : 1237);
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
		final SabotageCheckResult other = (SabotageCheckResult) obj;
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
		if (wasSabotaged != other.wasSabotaged)
			return false;
		return true;
	}
	
	public String toString() {
		String result = wasSabotaged ? "SABOTAGED" : "NOT SABOTAGED";
		result += executorResult != null ? executorResult.toString() : "";
		result += executionError != null ? executionError.toString() : ""; 
		
		return result;
	}
}
