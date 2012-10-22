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

/**
 * Represents an exception thrown when has the XML is not well-formed.
 */

public class XMLParserException extends OurgridException {

	private static final long serialVersionUID = 33L;


	public XMLParserException() {

		super();
	}


	public XMLParserException( Throwable cause ) {

		super( cause );
	}


	public XMLParserException( String message, Throwable cause ) {

		super( message, cause );
	}

}
