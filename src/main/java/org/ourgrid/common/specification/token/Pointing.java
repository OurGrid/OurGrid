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
 * This is a token entity that recognize punctuation symbols.
 * 
 * @see org.ourgrid.common.specification.CodesTable
 */
public class Pointing extends Token {

	/**
	 * The constructor.
	 */
	public Pointing() {

	}


	/**
	 * Tries to recognize a punctuation symbol and return a token using this
	 * symbol.
	 * 
	 * @param theChar The symbol to be analyzed.
	 * @param reader The reader able to read characters from source.
	 * @return this Pointing token object if theChar was recognized as
	 *         punctuation symbol and null otherwise.
	 */
	public Token readPointing( char theChar, CharReader reader ) {

		Map<String,Integer> table = CodesTable.getInstance().getPointing();
		String character = Character.toString( theChar );
		if ( table.containsKey( character ) ) {
			int code = table.get( character ).intValue();
			this.setCode( code );
			this.setLine( reader.getActualLine() );
			if ( character.equals( CharReader.UNIX_LINE_SEPARATOR ) ) {
				this.setSymbol( "\\n" );
			} else {
				this.setSymbol( character );
			}

			return this;
		}
		return null;
	}

}
