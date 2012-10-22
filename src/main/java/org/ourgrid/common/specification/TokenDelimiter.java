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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Data structure used to encapsulate the token's delimiters when reading a
 * StringToken. This structure is generally used at the
 * <code>StringToken.getToken(reader, delimiter) </code> that was made to
 * generalize the act of read tokens.
 */
public class TokenDelimiter {

	private Set<Character> delimiters = new LinkedHashSet<Character>();


	/**
	 * Adds a new character delimiter.
	 * 
	 * @param delimiter The character that will be consider as a delimiter.
	 */
	public void addDelimiter( char delimiter ) {

		Character delimiterObject = new Character( delimiter );
		delimiters.add( delimiterObject );
	}


	/**
	 * Checks if the given character is a delimiter for this set.
	 * 
	 * @param delimiter The delimiter to be checked.
	 * @return <code>true</code> if the delimiter is part of the set and
	 *         <code>false</code> otherwise.
	 */
	public boolean contains( char delimiter ) {

		return delimiters.contains( new Character( delimiter ) );
	}
}
