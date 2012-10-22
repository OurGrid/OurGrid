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

/**
 * This interface has to be implemented for every new language that will be
 * compiled. This will have to use the knowledge of a grammar to derivate the
 * sequences that can be accepted by it. This will have to use the services of
 * the Lexical module to ask it for Tokens.
 */
public interface SyntacticalAnalyzer {

	/**
	 * This is the "main" method then it will start the compilation process.
	 * Here will be made the syntactical compilation of source that is: given a
	 * source, will be checked if it is or not valid according to the rules of
	 * the grammar's language.
	 */
	public void startCompilation() throws SyntacticalException;

}
