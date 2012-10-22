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
package org.ourgrid.common.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.ourgrid.common.util.ArrayUtil;

/**
 * This class is the Mother-Exception of the Broker. The idea is to use its
 * subclasses to signal that some problem with Broker-semantic has occurred. For
 * instance, problems when creating Playpen or Storage, since both are concepts
 * defined by Broker.
 */
public class OurgridException extends Exception {

	static final long serialVersionUID = 40L;

	/** Logger (log4j) object to store log events */
	private static transient final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger( OurgridException.class );


	/**
	 * Constructs a new <code>OurgridException</code> object
	 */
	public OurgridException() {

		super();
	}


	/**
	 * Constructs a new <code>OurgridException</code> object
	 * 
	 * @param newDetail an object that specifies the exception details
	 */
	public OurgridException( Throwable newDetail ) {

		super( newDetail );

	}


	/**
	 * Constructs a new <code>OurgridException</code> object
	 * 
	 * @param message a string representing the exception message
	 */
	public OurgridException( String message ) {

		super( message );

	}


	/**
	 * Constructs a new <code>OurgridException</code> object
	 * 
	 * @param message a string representing the exception message
	 * @param newDetail an object that specifies the exception details
	 */
	public OurgridException( String message, Throwable newDetail ) {

		super( message, newDetail );
	}

	/**
	 * 
	 */
	public StackTraceElement[ ] getStackTrace() {

		Throwable detail = getCause();

		if ( detail != null ) {
			StackTraceElement[ ] stackTrace = new StackTraceElement[ detail.getStackTrace().length
					+ super.getStackTrace().length ];
			ArrayUtil.concat( detail.getStackTrace(), super.getStackTrace(), stackTrace );
			return stackTrace;
		}

		return super.getStackTrace();
	}


	public void printStackTrace() {

		printStackTrace( System.out );
	}


	public void printStackTrace( PrintWriter pw ) {

		Throwable detail = getCause();

		if ( detail != null ) {
			detail.printStackTrace( pw );
		}
		super.printStackTrace( pw );
	}


	public void printStackTrace( PrintStream ps ) {

		Throwable detail = getCause();

		if ( detail != null ) {
			detail.printStackTrace( ps );
		}
		super.printStackTrace( ps );
	}


}
