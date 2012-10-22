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

import java.io.Serializable;

import org.ourgrid.common.interfaces.Constants;


/**
 * This class represents the result of an execution.
 */
public class ExecutorResult implements Serializable {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 40L;

	/**
	 * The exit value of an execution.
	 */
	private int exitValue;

	/**
	 * The standard output of an execution.
	 */
	private String stdout;

	/**
	 * The standard error output.
	 */
	private String stderr;


	public ExecutorResult() {

	}


	public ExecutorResult( int exitValue, String stdout, String stderr ) {

		super();
		this.exitValue = exitValue;
		this.stdout = stdout;
		this.stderr = stderr;
	}


	/**
	 * Returns a textual representation of an <code>ExecutorResult</code>.
	 */
	@Override
	public String toString() {

		StringBuilder str = new StringBuilder();
		str.append( "ExitValue: " );
		str.append( exitValue );
		str.append( Constants.LINE_SEPARATOR );
		str.append( " StdOut: " );
		str.append( stdout == null ? "-" : stdout );
		str.append( Constants.LINE_SEPARATOR );
		str.append( " StdErr: " );
		str.append( stderr == null ? "-" : stderr );
		str.append( Constants.LINE_SEPARATOR );
		return str.toString();
	}


	/**
	 * Returns the exit value of the process
	 * 
	 * @return The exit value
	 */
	public int getExitValue() {

		return exitValue;
	}


	/**
	 * Returns the error of the process
	 * 
	 * @return The error
	 */
	public String getStderr() {

		return stderr;
	}


	/**
	 * Returns the standard output of the process
	 * 
	 * @return The standard output
	 */
	public String getStdout() {

		return stdout;
	}


	/**
	 * Sets the exit value of the process.
	 * 
	 * @param exitValue The exit value of the process
	 */
	public void setExitValue( int exitValue ) {

		this.exitValue = exitValue;
	}


	/**
	 * Set the error of the process.
	 * 
	 * @param stderr The standard err of the process
	 */
	public void setStderr( String stderr ) {

		this.stderr = stderr;
	}


	/**
	 * Set the standard output of the process.
	 * 
	 * @param stdout The standard output of the process
	 */
	public void setStdout( String stdout ) {

		this.stdout = stdout;
	}


	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + this.exitValue;
		result = PRIME * result + (this.stderr == null ? 0 : this.stderr.hashCode());
		result = PRIME * result + (this.stdout == null ? 0 : this.stdout.hashCode());
		return result;
	}


	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( !(obj instanceof ExecutorResult) )
			return false;
		final ExecutorResult other = (ExecutorResult) obj;
		if ( this.exitValue != other.exitValue )
			return false;
		if ( !(this.stderr == null ? other.stderr == null : this.stderr.equals( other.stderr )) )
			return false;
		if ( !(this.stdout == null ? other.stdout == null : this.stdout.equals( other.stdout )) )
			return false;
		return true;
	}
}
