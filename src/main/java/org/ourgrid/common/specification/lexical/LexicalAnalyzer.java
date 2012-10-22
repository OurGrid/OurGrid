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
package org.ourgrid.common.specification.lexical;

import org.ourgrid.common.specification.TokenDelimiter;
import org.ourgrid.common.specification.token.Token;

public interface LexicalAnalyzer {

	/**
	 * This is the main function of the lexical analyzer. It will read the next
	 * string from source and return the token it represents ( the string, the
	 * code it has and the line it was found ).
	 * 
	 * @return The next valid token object and null if the source finished
	 */
	public Token getToken() throws LexicalException;


	/**
	 * It will read the next string from source and return the token it
	 * represents. This method uses the given set of delimiters to know where
	 * the token stops.
	 * 
	 * @param delimiters the set of delimiters that determines the end of token.
	 * @return A token where the token's symbol is the string read.
	 * @throws LexicalException if a I/O problem ocurres at the reader
	 */
	public Token getToken( TokenDelimiter delimiters ) throws LexicalException;
}
