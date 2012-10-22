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

/**
 * This interface is one of the principal parts of the syntactical module of a
 * compiler. It handles every operation about the language and its formation
 * rules.
 */
public interface Grammar {

	/**
	 * At a syntactical compilation ( considering a predictor LL(n) grammar)
	 * this is one of the principal operations, that is: at one certain moment,
	 * where the next symbol from source and the stack top are non-terminals, it
	 * is necessary to know what rule from grammar has to be used to continue
	 * with the process.
	 * 
	 * @param stackTop The symbol at the stack top.
	 * @param nextSymbol The symbol just read from source.
	 * @return The rule object to be used at the case - null if any.
	 */
	public Rule getRule( Symbol stackTop, Symbol nextSymbol );


	/**
	 * Returns the rule defined by a given number.
	 * 
	 * @param ruleNumber The rule number.
	 * @return A object Rule that is the rule with ruleNumber as code.
	 */
	public Rule getRule( int ruleNumber );


	/**
	 * Adds a new rule to the grammar.
	 * 
	 * @param newRule The Rule to be inserted at the grammar.
	 */
	public void addRule( Rule newRule );


	/**
	 * Adds a symbol in the grammar.
	 * 
	 * @param symbol The symbol to be added.
	 */
	public void addSymbol( Symbol symbol );


	/**
	 * Gets the symbol represented by the symbolName
	 * 
	 * @param symbolName The symbolName in the grammar that represents the
	 *        required symbol.
	 * @return The symbol represented by the symbolName.
	 */
	public Symbol getSymbol( String symbolName );


	/**
	 * Returns the initial symbol of the grammar.
	 * 
	 * @return The initial symbol of the grammar.
	 */
	public Symbol getInitialSymbol();


	/**
	 * Defines the last symbol from source. It means that if this symbol is read
	 * then the source has finished.
	 * 
	 * @return The symbol that represents the end of the source.
	 */
	public Symbol getEndOfSourceSymbol();

}
