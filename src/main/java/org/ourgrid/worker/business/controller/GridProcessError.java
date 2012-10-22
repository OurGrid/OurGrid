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
package org.ourgrid.worker.business.controller;

import java.io.Serializable;

import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Exception raised when an error occurs during an execution.
 * 
 * @since 4.0
 */
public class GridProcessError implements Serializable {

	private static final long serialVersionUID = 40L;

	@XStreamAlias("type")
	private GridProcessErrorTypes type;

	@XStreamAlias("errorCause")
	private Throwable errorCause;


	/**
	 * Constructor
	 * 
	 * @param errorCause The exception that cause the error
	 * @param type The category of the error
	 */
	public GridProcessError( Throwable errorCause, GridProcessErrorTypes type ) {

		this.type = type;
		this.errorCause = errorCause;
	}


	/**
	 * Creates an <code>ExectionException</code> with no error cause
	 * 
	 * @param type The category of the error
	 */
	public GridProcessError( GridProcessErrorTypes type ) {

		this( null, type );
	}


	/**
	 * @return The error cause
	 */
	public Throwable getErrorCause() {

		return errorCause;
	}


	/**
	 * @return The error type
	 */
	public GridProcessErrorTypes getType() {

		return type;
	}


	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.type == null) ? 0 : this.type.hashCode());
		return result;
	}


	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( !(obj instanceof GridProcessError) )
			return false;
		final GridProcessError other = (GridProcessError) obj;

		if ( !(this.type == null ? other.type == null : type.equals( other.type )) )
			return false;
		return true;
	}


	@Override
	public String toString() {

		return "Execution Error. Type: " + type + ". Cause: " + (errorCause == null ? "-" : errorCause.getMessage());
	}

}
