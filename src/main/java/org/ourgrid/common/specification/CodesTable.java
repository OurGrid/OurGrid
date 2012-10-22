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

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.ourgrid.common.specification.io.CharReader;

/**
 * This entity is used to manage the correct use of the common.token package
 * entities. More specifically it will manage the type of each "word" (better
 * saying token) read from source.
 */
public class CodesTable {

	// Language types
	public static final String STRING = "string";

	// Symbol types
	public static final int RESERVED_WORD = 1;

	public static final int OPERATOR = 2;

	public static final int PARENTHERIC = 3;

	public static final int POINTING = 4;

	public static final int LANGUAGETYPES = 5;

	private Map<String,Integer> reservedWords, operators, parenthetic, pointing, languageTypes;

	private static CodesTable instance;


	/**
	 * Gets an instace of the CodesTable entity.
	 * 
	 * @return The codesTable object.
	 */
	public static CodesTable getInstance() {

		if ( instance == null ) {
			instance = new CodesTable();
		}
		return instance;
	}


	/**
	 * A collection with all the Reserved words.
	 * 
	 * @return A Map with the table reserved words.
	 */
	public Map<String,Integer> getReservedWords() {

		return reservedWords;
	}


	/**
	 * A collection with all the Operator symbols.
	 * 
	 * @return A Map with the table operator symbols.
	 */
	public Map<String,Integer> getOperators() {

		return operators;
	}


	/**
	 * A collection with all the Parenthetic symbols.
	 * 
	 * @return A Map with the table parenthetic symbols.
	 */
	public Map<String,Integer> getParenthetics() {

		return parenthetic;
	}


	/**
	 * A collection with language types.
	 * 
	 * @return A Map with the table language type symbols.
	 */
	public Map<String,Integer> getLanguageTypes() {

		return languageTypes;
	}


	/**
	 * A collection with all the Punctuation symbols.
	 * 
	 * @return A Map with the table punctuation symbols.
	 */
	public Map<String,Integer> getPointing() {

		return pointing;
	}


	/**
	 * Executes a search at the table by the given symbol key.
	 * 
	 * @param key The symbol where the code is wanted.
	 * @return The code of the symbol (key) or 0 if it was not found.
	 */
	public int getCode( String key ) {

		int code = 0;
		if ( reservedWords.get( key ) != null ) {
			code = reservedWords.get( key ).intValue();

		} else if ( operators.get( key ) != null ) {
			code = operators.get( key ).intValue();

		} else if ( parenthetic.get( key ) != null ) {
			code = parenthetic.get( key ).intValue();

		} else if ( pointing.get( key ) != null ) {
			code = pointing.get( key ).intValue();

		} else if ( languageTypes.get( key ) != null ) {
			code = languageTypes.get( key ).intValue();
		}

		return code;
	}


	/**
	 * Returns the type of the symbol received.
	 * 
	 * @return An int representing a type specified in this class from
	 *         RESERVED_WORD, OPERATOR, PARENTHERIC, POINTING, SPECIALS. If the
	 *         key is not found it returns 0 (zero).
	 */
	public int getType( String key ) {

		int type = 0;
		if ( reservedWords.get( key ) != null ) {
			type = RESERVED_WORD;

		} else if ( operators.get( key ) != null ) {
			type = OPERATOR;

		} else if ( parenthetic.get( key ) != null ) {
			type = PARENTHERIC;

		} else if ( pointing.get( key ) != null ) {
			type = POINTING;

		} else if ( languageTypes.get( key ) != null ) {
			type = LANGUAGETYPES;
		}

		return type;
	}


	// ///////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Initialize the table of codes
	 */

	private CodesTable() {

		initialize();
		populateTable();
	}


	private void initialize() {

		reservedWords = new TreeMap<String,Integer>( new IgnoreCaseComparator() );
		operators = new TreeMap<String,Integer>( new IgnoreCaseComparator() );
		parenthetic = new TreeMap<String,Integer>( new IgnoreCaseComparator() );
		pointing = new TreeMap<String,Integer>( new PointingComparator() );
		languageTypes = new TreeMap<String,Integer>( new IgnoreCaseComparator() );
	}


	/*
	 * Populates the Maps informations.
	 */
	private void populateTable() {

		// RESERVED WORDS !!!
		reservedWords.put( "JOB", new Integer( 1 ) );
		reservedWords.put( "TASK", new Integer( 2 ) );
		reservedWords.put( "REQUIREMENTS", new Integer( 3 ) );
		reservedWords.put( "INIT", new Integer( 4 ) );
		reservedWords.put( "REMOTE", new Integer( 6 ) );
		reservedWords.put( "FINAL", new Integer( 8 ) );
		reservedWords.put( "IF", new Integer( 9 ) );
		reservedWords.put( "THEN", new Integer( 10 ) );
		reservedWords.put( "ELSE", new Integer( 11 ) );
		reservedWords.put( "ENDIF", new Integer( 18 ) );
		reservedWords.put( "PUT", new Integer( 12 ) );
		reservedWords.put( "STORE", new Integer( 13 ) );
		reservedWords.put( "GET", new Integer( 14 ) );
		reservedWords.put( "PEER", new Integer( 15 ) );
		reservedWords.put( "WORKER", new Integer( 16 ) );
		reservedWords.put( "WORKERDEFAULTS", new Integer( 17 ) );
		reservedWords.put( "CHECK", new Integer( 18 ) );

		// OPERATORS
		operators.put( "||", new Integer( 30 ) );
		operators.put( "&&", new Integer( 31 ) );
		operators.put( "!", new Integer( 32 ) );
		operators.put( "=", new Integer( 33 ) );
		operators.put( "==", new Integer( 34 ) );
		operators.put( "!=", new Integer( 35 ) );
		operators.put( ">", new Integer( 36 ) );
		operators.put( "<", new Integer( 37 ) );
		operators.put( ">=", new Integer( 38 ) );
		operators.put( "<=", new Integer( 39 ) );
		operators.put( "IN_PATH", new Integer( 40 ) );
		operators.put( "IN_CLASSPATH", new Integer( 41 ) );
		operators.put( "VERSION", new Integer( 42 ) );

		// POINTING
		pointing.put( ";", new Integer( 60 ) );
		pointing.put( CharReader.UNIX_LINE_SEPARATOR, new Integer( 61 ) );
		pointing.put( "\\n", new Integer( 61 ) );
		pointing.put( ":", new Integer( 62 ) );

		// PARENTHERIC
		parenthetic.put( "{", new Integer( 70 ) );
		parenthetic.put( "}", new Integer( 71 ) );
		parenthetic.put( "(", new Integer( 72 ) );
		parenthetic.put( ")", new Integer( 73 ) );
		parenthetic.put( "\"", new Integer( 74 ) );

		// LanguageTypes
		languageTypes.put( CodesTable.STRING, new Integer( 83 ) );
	}

	class PointingComparator implements Comparator<String> {

		public int compare( String o1, String o2 ) {

			return o1.compareTo( o2 );
		}
	}
}
