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
package org.ourgrid.common.specification;

import org.ourgrid.common.specification.main.CompilerException;

/**
 * This entity indicates that a back slash was found a no special char was
 * recognized.
 */
public class SpecialCharException extends CompilerException {

	private static final long serialVersionUID = 33L;


	/**
	 * @param arg0
	 */
	public SpecialCharException( String arg0 ) {

		super( arg0 );
	}


	/**
	 * @param arg0
	 * @param arg1
	 */
	public SpecialCharException( String arg0, Throwable arg1 ) {

		super( arg0, arg1 );
	}


	/**
	 * @param arg0
	 */
	public SpecialCharException( Throwable arg0 ) {

		super( arg0 );
	}

}
