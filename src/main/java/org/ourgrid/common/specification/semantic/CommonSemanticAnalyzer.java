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
 * This implementation of the semantic analyzer uses more then one language to
 * compile. Thats why uses SemanticActions as sets to each different language
 * actions.
 */
public class CommonSemanticAnalyzer implements SemanticAnalyzer {

	SemanticActions actions;


	/**
	 * The constructor.
	 * 
	 * @param actions The set of the actions that will be used for a determined
	 *        language.
	 */
	public CommonSemanticAnalyzer( SemanticActions actions ) {

		this.actions = actions;

	}


	/**
	 * @see org.ourgrid.common.specification.semantic.SemanticAnalyzer#performAction(java.lang.String,
	 *      org.ourgrid.common.specification.token.Token)
	 */
	public void performAction( String action, Token token ) throws SemanticException {

		StringBuffer sb = new StringBuffer( action );
		String actionNumber = sb.substring( 1 ); // the format is #n where n
													// is the number of the
													// action.
		actions.performAction( "action" + actionNumber, token );
	}


	/**
	 * @see org.ourgrid.common.specification.semantic.SemanticAnalyzer#getOperationMode()
	 */
	public int getOperationMode() {

		return actions.getOperationalMode();
	}

}
