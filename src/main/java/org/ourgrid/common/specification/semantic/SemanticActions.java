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
package org.ourgrid.common.specification.semantic;

import java.util.List;

import org.ourgrid.common.specification.semantic.exception.SemanticException;
import org.ourgrid.common.specification.token.Token;

/**
 * This entity has to be used as a set of semantic actions to a determined
 * language type. The Compiler structure built here suggests that this set of
 * actions will be used by the SemanticAnalyzer only if this entity wants to
 * deal with more then one language. In the other case, the set of actions can
 * be into the semantic analyzer's implementation. Created on Jul 2, 2004
 */
public interface SemanticActions {

	/**
	 * @see org.ourgrid.common.specification.semantic.SemanticAnalyzer#performAction(String,
	 *      Token)
	 */
	public void performAction( String action, Token token ) throws SemanticException;


	/**
	 * Used to return the result of the compilation process, after execute all
	 * the semantic actions necessary and defined by a grammar definition.
	 * 
	 * @return A list with all the objects constructed as answer of the
	 *         compilation This is a generic way of giving a answer because each
	 *         language needs a different answer structure.
	 */
	public List getResult();


	/**
	 * @see org.ourgrid.common.specification.semantic.SemanticAnalyzer#getOperationMode()
	 */
	public int getOperationalMode();

}
