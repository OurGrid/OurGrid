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
package org.ourgrid.common;

import java.io.Serializable;

import org.ourgrid.reqtrace.Req;

/**
 * This class represents a login result
 */
@Req("REQ108")
public class BrokerLoginResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String UNKNOWN_USER = "Unknown user.";

	public static final String WRONG_PUBLIC_KEY = "Wrong public key.";

	public static final String INTERNAL_ERROR = "Could not save Broker public key due to I/O Error.";

	public static final String ALREADY_LOGGED = "Already logged.";
	
	public static final String DUPLICATED_PUBLIC_KEY = "Duplicated public key.";

	public static final String OK = "OK";
	
	private String errorMessage;
	
	public BrokerLoginResult(){
		this(null);
	}
	
	/**
	 * Constructor used in where a return result is needed.
	 * @param result The cause of the error that occurs during the execution
	 */
	public BrokerLoginResult(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/**
	 * Get's the error message for this operation.
	 * 
	 * @return Error message
	 */
	@Req("REQ109")
	public String getErrorMessage() {
		
		return errorMessage;
	}
	
	/**
	 * Set's the error message for this operation.
	 * 
	 * @param errorMessage Error mesage
	 */
	public void setErrorMessage( String errorMessage ) {
		this.errorMessage = errorMessage;
	}
	
	/**
	 * @return True in error ocurred during the execution of the operation,
	 *         false otherwise.
	 */
	@Req("REQ109")
	public boolean hasAnErrorOcurred() {
		return this.errorMessage != null;
	}
	
	@Override
	public String toString() {
		return errorMessage;
	}
}
