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
package org.ourgrid.common.executor;

import org.ourgrid.common.exception.OurgridException;

/**
 * This exception must be used to signalize an execution problem, found by the
 * implementors of org.ourgrid.common.executor.Executor interface
 */
public class ExecutorException extends OurgridException {

	private static final long serialVersionUID = 33L;


	/**
	 * Constructor without paramethers
	 */
	public ExecutorException( ) {

		super();
	}


	/**
	 * @param command The command in which execution the Executor failed. It
	 *        will be used to compose better this getMessage method.
	 */
	public ExecutorException( String command ) {

		super( "Command: " + command );

	}


	/**
	 * @param command The command in which execution the Executor failed. It
	 *        will be used to compose better this getMessage method.
	 * @param detail an object that specifies the exception details
	 */
	public ExecutorException( String command, Throwable detail ) {

		super( "Command: " + command, detail );

	}


	/**
	 * @param detail an object that specifies the exception details
	 */
	public ExecutorException( Throwable detail ) {

		super( detail );
	}


	/**
	 * Returns the message of this exception
	 * 
	 * @return The message
	 */
	public String getMessage( ) {
		return super.getMessage();
	}
}
