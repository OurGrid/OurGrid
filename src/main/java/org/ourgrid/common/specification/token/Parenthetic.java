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
package org.ourgrid.common.specification.token;

import java.util.Map;

import org.ourgrid.common.specification.CodesTable;
import org.ourgrid.common.specification.io.CharReader;

/**
 * It is a Token object that is recognized as an Parenthetic at the CodesTable.
 */
public class Parenthetic extends Token {

	/**
	 * Checks if the character passed as paramether is a Parenthetic.
	 * 
	 * @param theChar - Is the character that has to be checked.
	 * @param reader - Is the reader that is able to read characters from
	 *        source.
	 * @return A Token object if the character was recognized or "null" if it
	 *         was not.
	 * @see org.ourgrid.common.specification.CodesTable
	 */
	public Token readParantizer( char theChar, CharReader reader ) {

		Map<String,Integer> table = CodesTable.getInstance().getParenthetics();
		String toTest = "" + theChar;

		if ( table.containsKey( toTest ) ) {

			int code = table.get( toTest ).intValue();
			this.setCode( code );
			this.setLine( reader.getActualLine() );
			this.setSymbol( toTest );

			return this;
		}
		return null;
	}

}
