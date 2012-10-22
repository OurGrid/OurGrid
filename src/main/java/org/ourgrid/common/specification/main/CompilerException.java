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
package org.ourgrid.common.specification.main;

import org.ourgrid.common.exception.OurgridException;

/**
 * This Exception wraps the various types of exceptions that can happens into
 * the compilation process.
 */
public class CompilerException extends OurgridException {

	private static final long serialVersionUID = 33L;


	/**
	 * @see java.lang.Exception#Exception(java.lang.String)
	 */
	public CompilerException( String arg0 ) {

		super( arg0 );
	}


	/**
	 * @see java.lang.Exception#Exception(java.lang.String, java.lang.Throwable)
	 */
	public CompilerException( String arg0, Throwable arg1 ) {

		super( arg0, arg1 );
	}


	/**
	 * @see java.lang.Exception#Exception(java.lang.Throwable)
	 */
	public CompilerException( Throwable arg0 ) {

		super( arg0 );
	}

}
