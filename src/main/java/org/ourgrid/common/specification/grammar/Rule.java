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

import org.ourgrid.common.specification.CompilerMessages;
import org.ourgrid.common.specification.grammar.exception.InvalidRuleException;

/**
 * Project: Caymman(DSC/UFCG) Description: This class represents a rule of a
 * grammar.
 * 
 * @version 1.0 Created on Sep 25, 2003 Last update: Sep 25, 2003
 */

public class Rule implements Serializable {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 33L;

	/* The identification of the rule in the grammar. */
	private int id;

	/* The head of the rule. */
	private Symbol head;

	/* The body of the rule. */
	private Symbol[ ] body;


	/**
	 * Constructor
	 * 
	 * @param id The identification of the rule.
	 * @param head The head of the rule. A head is the leftmost side of a rule
	 *        in a grammar.
	 * @param body The body of the rule. A body is the leftmost side of a rule
	 *        in a grammar.
	 */
	public Rule( int id, Symbol head, Symbol[ ] body ) throws InvalidRuleException {

		if ( !head.isNonTerminal() ) {
			throw new InvalidRuleException( CompilerMessages.ERROR_GRAMMAR_MALFORMED_RULE( head.getValue() ) );
		}
		this.id = id;
		this.head = head;
		this.body = body;
	}


	/**
	 * Gets the body of the rule. For example, in the rule <input_cmd>::=read,
	 * the body is read.
	 * 
	 * @return The body of the rule.
	 */
	public Symbol[ ] getBody() {

		return body;
	}


	/**
	 * Gets the head of the rule. For example, in the rule <input_cmd>::=read,
	 * the head is <input_cmd>.
	 * 
	 * @return The head of the rule.
	 */
	public Symbol getHead() {

		return head;
	}


	/**
	 * Gets the identification of the rule. A identification is just a number.
	 * 
	 * @return The identification of the rule.
	 */
	public int getID() {

		return id;
	}


	/*
	 * Returns the rule in onde line, as: <input_cmd>::=read readln
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		String bodyStr = body.length == 0 ? "&" : ("" + body[0].getValue());
		for ( int i = 1; i < body.length; i++ ) {
			bodyStr += " " + body[i].getValue();
		}
		return id + "\t" + head.getValue() + " ::= " + bodyStr;
	}

}
