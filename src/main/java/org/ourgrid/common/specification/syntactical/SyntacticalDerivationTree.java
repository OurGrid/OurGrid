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
package org.ourgrid.common.specification.syntactical;

import java.util.Stack;

import org.ourgrid.common.specification.grammar.Rule;
import org.ourgrid.common.specification.grammar.Symbol;

/**
 * This is the entity that will control the derivation of a word from a language
 * described by a grammar. Here a word is the source that has to be compiled,
 * then; a derivation is the process of, beginning from the initial symbol of a
 * grammar, obtain the read source.
 */
public class SyntacticalDerivationTree {

	private Stack<Symbol> sdt;


	/**
	 * Initializes the stack that will control the derivation.
	 * 
	 * @param endOfSourceSymbol The symbol that defines that the source has
	 *        ended.
	 * @param initialSymbol The initial symbol of the grammar language.
	 */
	public SyntacticalDerivationTree( Symbol endOfSourceSymbol, Symbol initialSymbol ) {

		sdt = new Stack<Symbol>();
		initializeSDT( endOfSourceSymbol, initialSymbol );
	}


	/*
	 * Pushes the two symbol received as paramethers at the constructor... It is
	 * necessary to have at the end of the stack the final symbol from source
	 * and at the top (in the begging moment) the initial symbol from grammar.
	 */
	private void initializeSDT( Symbol endOfSourceSymbol, Symbol initialSymbol ) {

		sdt.push( endOfSourceSymbol );
		sdt.push( initialSymbol );
	}


	/**
	 * Returns the symbol at the top of the derivation stack.
	 * 
	 * @return The symbol at the top of the derivation stack.
	 */
	public Symbol top() {

		return sdt.peek();
	}


	/**
	 * Pops (removes) the symbol at the top of the derivation stack.
	 * 
	 * @return The symbol that was removed from the top of the derivation stack.
	 */
	public Symbol pop() {

		return sdt.pop();
	}


	/**
	 * Pushes (insert) a given rule at the top of the derivation stack. A rule
	 * is a set of symbols (terminal or not). To push a rule at the derivation
	 * stack, is necessary to insert from the end to the beggining of the rule's
	 * symbols.
	 * 
	 * @param rule The rule that have to be inserted at the derivation stack
	 *        top.
	 */
	public void pushRule( Rule rule ) {

		for ( int count = (rule.getBody().length) - 1; count >= 0; count-- ) {
			Symbol symbol = ((rule.getBody())[count]);
			sdt.push( symbol );
		}
	}

}
