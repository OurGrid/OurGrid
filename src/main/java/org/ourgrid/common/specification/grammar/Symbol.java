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
package org.ourgrid.common.specification.grammar;

import java.io.Serializable;

/**
 * Project: Caymman(DSC/UFCG) Description: This class represents a symbol formed
 * by (code + value + type), where type can be Symbol.TERMINAL, <br>
 * Symbol.TERMINAL or Symbol.SEMANTIC_ACTION.
 * 
 * @version 1.0 Created on Jun 17, 2003 Last update: Jun 19, 2003
 */

public class Symbol implements Serializable {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 33L;

	public static final int TERMINAL = 0;

	public static final int NON_TERMINAL = 1;

	public static final int SEMANTIC_ACTION = 2;

	public static final Symbol EMPTY = new Symbol( -1, "&", TERMINAL );

	public static final Symbol EOF = new Symbol( 0, "EOF", TERMINAL );

	// Attributes

	/* The value ( eg. EOF ) that represents the symbol. */
	private String value;

	/* The code of the symbol. */
	private int code;

	/*
	 * The type of the symbol( TERMINAL , NON_ TERMINAL or SEMANTIC_ACTION,
	 * CODE_ACTION )
	 */
	private int type;


	/**
	 * Constructor
	 * 
	 * @param code The code of the symbol
	 * @param value The symbol identifier
	 * @param type The code of the type of the symbol
	 */
	public Symbol( int code, String value, int type ) {

		this.code = code;
		this.value = value;
		this.type = type;
	}


	/**
	 * An empty constructor
	 */
	public Symbol() {

		this( 0, "", TERMINAL );
	}


	/**
	 * Gets the code of the symbol
	 * 
	 * @return The code of the symbol
	 */
	public int getCode() {

		return code;
	}


	/**
	 * Gets the symbol
	 * 
	 * @return The symbol
	 */
	public String getValue() {

		return value;
	}


	/**
	 * Returns true if the symbol is a terminal
	 * 
	 * @return True if the symbol is a terminal, false otherwise.
	 */
	public boolean isTerminal() {

		return type == TERMINAL;
	}


	/**
	 * Returns true if the symbol is a non terminal
	 * 
	 * @return True if the symbol is a non terminal, false otherwise.
	 */
	public boolean isNonTerminal() {

		return type == NON_TERMINAL;
	}


	/**
	 * Returns true if the symbol is a semantic action
	 * 
	 * @return True if the symbol is a semantic action, false otherwise.
	 */
	public boolean isSemanticAction() {

		return type == SEMANTIC_ACTION;
	}


	/**
	 * Returns true if the object obj is a Symbol and the code = obj.getCode().
	 * 
	 * @param obj the object to be compared.
	 */
	public boolean equals( Object obj ) {

		if ( obj instanceof Symbol ) {
			return (code == ((Symbol) obj).getCode()) && (type == ((Symbol) obj).type);
		}
		return false;
	}


	/**
	 * @return A string representation of the object.
	 */
	public String toString() {

		return ("Code: " + this.code + " - Value: " + this.value + " - Type: " + this.type);
	}

}
