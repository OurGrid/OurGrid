package org.ourgrid.common;

import java.io.Serializable;

import org.ourgrid.reqtrace.Req;

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

/**
 * This class represents a login result
 */
@Req("REQ108")
public class WorkerLoginResult implements Serializable {


	private static final long serialVersionUID = 1L;

	public static final String INVALID_CERT_PATH = "Invalid cert path.";

	public static final String UNISSUED_CERT_PATH = "Unissued cert path.";

	public static final String OK = "OK";

	private String resultMessage;

	public WorkerLoginResult(){
		this(null);
	}

	/**
	 * Constructor used in where a return result is needed.
	 * @param result The cause of the error that occurs during the execution
	 */
	public WorkerLoginResult(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	/**
	 * Get's the error message for this operation.
	 * 
	 * @return Error message
	 */
	@Req("REQ109")
	public String getResultMessage() {

		return resultMessage;
	}

	/**
	 * Set's the error message for this operation.
	 * 
	 * @param resultMessage Result message
	 */
	public void setResultMessage( String resultMessage ) {
		this.resultMessage = resultMessage;
	}

	/**
	 * @return True in error ocurred during the execution of the operation,
	 *         false otherwise.
	 */
	@Req("REQ109")
	public boolean hasAnResult() {
		return this.resultMessage != null;
	}

	@Override
	public String toString() {
		return resultMessage;
	}
}
