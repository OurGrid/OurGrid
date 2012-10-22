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

import org.ourgrid.common.specification.grammar.Grammar;
import org.ourgrid.common.specification.lexical.LexicalAnalyzer;
import org.ourgrid.common.specification.lexical.LexicalException;
import org.ourgrid.common.specification.semantic.SemanticActions;
import org.ourgrid.common.specification.semantic.SemanticAnalyzer;
import org.ourgrid.common.specification.semantic.exception.SemanticException;
import org.ourgrid.common.specification.syntactical.SyntacticalAnalyzer;
import org.ourgrid.common.specification.syntactical.SyntacticalException;

/**
 * Is a central factory for the compiler modules.
 */
public interface CompilerModulesFactory {

	/**
	 * Makes an instance of the Lexical Module.
	 * 
	 * @param sourceFile The file constaining the source to be compiled.
	 * @return A instance of the LexicalAnalyzer entity.
	 * @throws LexicalException If happens some problem at the creations
	 *         process, for example, the file could not be found.
	 */
	public LexicalAnalyzer createLexicalAnalyzer( String sourceFile ) throws LexicalException;


	/**
	 * Makes an instance of the Syntactical Module.
	 * 
	 * @param lexicalAnalyzer The lexical module that will give tokens to this
	 *        module.
	 * @param languageGrammar The grammar entity that knows how is the language.
	 * @param semantic The semanic module that will execute with the
	 *        compilation. Can be null if there is no intention of run it.
	 * @return A instance of the SyntacticalAnalyzer entity.
	 * @throws SyntacticalException If happens some problem at the creations
	 *         process.
	 */
	public SyntacticalAnalyzer createSyntacticalAnalyzer( LexicalAnalyzer lexicalAnalyzer, Grammar languageGrammar,
															SemanticAnalyzer semantic ) throws SyntacticalException;


	/**
	 * Makes an instance of the Syntactical Module.
	 * 
	 * @param actionsContainer The class that has the actions to be executed by
	 *        introspection.
	 * @return A instance of the SemanticalAnalyzer entity.
	 * @throws SemanticException If happens some problem at the creations
	 *         process.
	 */
	public SemanticAnalyzer createSemanticAnalyzer( SemanticActions actionsContainer ) throws SemanticException;
}
