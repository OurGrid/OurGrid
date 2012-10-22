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

import org.ourgrid.reqtrace.Req;

/**
 * This class implements the interface ExecutorHandle and is used to identify
 * executions dispatched to intances of a native Executor class.
 */
public class IntegerExecutorHandle implements ExecutorHandle {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 33L;

	private Integer value;


	/**
	 * Constructs a new handle based on an instance of Integer class.
	 * 
	 * @param value An instance of Integer class.
	 */
	public IntegerExecutorHandle( Integer value ) {
		this.value = value;
	}


	/**
	 * Constructs a new handle by using an int value.
	 * 
	 * @param valueInt A integer value to init the handle.
	 */
    @Req("REQ004")
	public IntegerExecutorHandle( int valueInt ) {
		this.value = new Integer( valueInt );
	}


	/**
	 * This method provides the value of this handle.
	 * 
	 * @return An instance of Integer class that represents the handle value.
	 */
	public Integer getValue( ) {
		return this.value;
	}


	/**
	 * This method provides a representation of this handle like an primitive
	 * int value.
	 * 
	 * @return A primitive int value representation for this handle
	 */
	public int getIntValue( ) {
		return this.value.intValue();
	}


	/**
	 * This method provides a String representation of this handle.
	 * 
	 * @return A string representation for this handle.
	 */
	public String toString( ) {
		return this.getValue().toString();
	}


	public boolean equals( Object o ) {
		if ( o == null || !( o instanceof IntegerExecutorHandle ) ) {
			return false;
		}
		IntegerExecutorHandle handle = ( IntegerExecutorHandle ) o;
		return this.getIntValue() == ( handle.getIntValue() );
	}


	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo( Object otherHandle ) throws ClassCastException {

		if ( this.getIntValue() < ( ( IntegerExecutorHandle ) otherHandle ).getIntValue() ) {
			return -1;
		} else if ( this.getIntValue() > ( ( IntegerExecutorHandle ) otherHandle ).getIntValue() ) {
			return 1;
		} else {
			return 0;
		}

	}

}
