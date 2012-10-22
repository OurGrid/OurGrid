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

public class GridProcessResultInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer exitValue;
	private String errorCause;
	private String executionErrorType;
	private String stderr;
	private String stdout;
	
	public GridProcessResultInfo() {}
	
	public GridProcessResultInfo(Integer exitValue, String errorCause, String executionErrorType, String stderr, String stdout) {
		this.exitValue = exitValue;
		this.errorCause = errorCause;
		this.executionErrorType = executionErrorType;
		this.stderr = stderr;
		this.stdout = stdout;
	}
	
	public Integer getExitValue() {
		if (exitValue == null) {
			return -1;
		}
		return exitValue;
	}
	
	public void setExitValue(Integer exitValue) {
		this.exitValue = exitValue;
	}
	
	public String getErrorCause() {
		return errorCause;
	}
	
	public void setErrorCause(String errorCause) {
		this.errorCause = errorCause;
	}
	
	public String getExecutionErrorType() {
		return executionErrorType;
	}
	
	public void setExecutionErrorType(String executionErrorType) {
		this.executionErrorType = executionErrorType;
	}
	
	public String getStderr() {
		return stderr;
	}
	
	public void setStderr(String stderr) {
		this.stderr = stderr;
	}
	
	public String getStdout() {
		return stdout;
	}
	
	public void setStdout(String stdout) {
		this.stdout = stdout;
	}
	
	

}
