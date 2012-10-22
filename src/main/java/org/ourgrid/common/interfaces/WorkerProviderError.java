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
package org.ourgrid.common.interfaces;

import org.ourgrid.common.interfaces.to.RequestSpecification;

/**
 * Represents an error that occurs inside a WorkerProvider when trying to
 * respond a client request (i.e. a request for workers).
 */
public class WorkerProviderError {

	private Throwable errorCause;

	/**
	 * The <code>RequestSpec</code> to describe which request was associated
	 * to the error (if any).
	 */
	private RequestSpecification requestSpec;


	public WorkerProviderError( Throwable cause, RequestSpecification spec ) {

		super();
		errorCause = cause;
		requestSpec = spec;
	}


	public Throwable getErrorCause() {

		return errorCause;
	}


	public RequestSpecification getRequestSpec() {

		return requestSpec;
	}
}
