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

import org.ourgrid.common.specification.semantic.exception.SemanticException;
import org.ourgrid.common.specification.token.Token;

/**
 * This entity is responsable for building a answer using the semantic actions
 * defined at the grammar. The structure of this interface sugests the use of
 * Introspection to call the method defined by the name of the action.
 */
public interface SemanticAnalyzer {

	/**
	 * This is the interface method for the semantic actions. It will try to
	 * execute the action defined by the paramether.
	 * 
	 * @param action The name of the action. It is generally a integer number.
	 * @param token The token that will be used at action
	 * @throws SemanticException If the action does not exists or any other
	 *         problem happens.
	 */
	public void performAction( String action, Token token ) throws SemanticException;


	/**
	 * Returns a integer code that can be used to signalize some functional
	 * modes. It is used basically to export some chages that can be used by the
	 * other modules, principally the SyntacticalAnalyzer.
	 * 
	 * @return The integer code
	 */
	public int getOperationMode();
}
