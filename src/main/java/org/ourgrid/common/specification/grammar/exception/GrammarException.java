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
package org.ourgrid.common.specification.grammar.exception;

/**
 * Project: Caymman(DSC/UFCG) Description: This class represents a grammar
 * exception. All the grammars exception should inherits of this class.
 * 
 * @version 1.0 Created on Sep 25, 2003 Last update: May 22, 2004
 */

public class GrammarException extends Exception {

	private static final long serialVersionUID = 33L;


	/**
	 * @see java.lang.Exception#Exception(java.lang.String)
	 */
	public GrammarException( String message ) {

		super( message );
	}

}
