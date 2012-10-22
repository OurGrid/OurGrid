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
package org.ourgrid.common.job;

/**
 * A <code>JobCounter</code> implementation that always starts counting from
 * id=1.
 * 
 * Requirement 302
 */
public class SimpleJobCounter implements JobCounter {

	private static final long serialVersionUID = 40L;

	/**
	 * Current id
	 */
	private int id;


	/**
	 * Constructor, set's the initialid=1.
	 */
	public SimpleJobCounter() {

		this( 1 );
	}


	/**
	 * Constructor, set's the initial-id=id.
	 * 
	 * @param id Initial id to use.
	 */
	protected SimpleJobCounter( int id ) {

		this.id = id;
	}


	public int nextJobId() {

		return id++;
	}


	public int getJobId() {

		return id;
	}


	public void shutdown( boolean force ) {

		this.id = 1;
	}
}
