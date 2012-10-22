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

/**
 * A token is a set of informations about the peaces read from the source to be
 * compiled. This is the unic structure that is understandable to the
 * syntactical analyzer.
 */
public class Token {

	private String symbol;

	private int code;

	private int line;


	/**
	 * A Empty Constructor
	 */
	public Token() {

	}


	/**
	 * A Constructor
	 * 
	 * @param symbol The token's string read from source.
	 * @param code The code of the string read. To mode informations see
	 *        CodesTable.
	 * @param line The line where the symbol was found at source.
	 */
	public Token( String symbol, int code, int line ) {

		this.symbol = symbol;
		this.code = code;
		this.line = line;
	}


	/**
	 * @return A string view of the token informations.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append( "Lexeme: \"" + getSymbol() + "\" ; " );
		sb.append( "Code: \"" + getCode() + "\" ; " );
		sb.append( "Line: \"" + getLine() + "\"" );

		return sb.toString();
	}


	/**
	 * @return the code of the token read from source.
	 */
	public int getCode() {

		return code;
	}


	/**
	 * @return the line of source where the symbol was read.
	 */
	public int getLine() {

		return line;
	}


	/**
	 * @return the token's string read from source.
	 */
	public String getSymbol() {

		return symbol;
	}


	/**
	 * @param code the code of the token read from source.
	 */
	public void setCode( int code ) {

		this.code = code;
	}


	/**
	 * @param line the line of source where the symbol was read.
	 */
	public void setLine( int line ) {

		this.line = line;
	}


	/**
	 * @param symbol the token's string read from source.
	 */
	public void setSymbol( String symbol ) {

		this.symbol = symbol;
	}

}
