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
package org.ourgrid.common.specification.exception;

/**
 * This class represents a problem when creating a new JobSpec. In general, the
 * problems are related to the list of Tasks Specs passed by parameter when
 * creating a JobSpec or when setting the list of Tasks Specs of a JobSpec.
 */
public class JobSpecificationException extends Exception {

	private static final long serialVersionUID = 33L;


	/**
	 * @see java.lang.Exception#Exception(java.lang.String)
	 */
	public JobSpecificationException( String argumentsInConflict ) {

		super( argumentsInConflict );
	}

}
