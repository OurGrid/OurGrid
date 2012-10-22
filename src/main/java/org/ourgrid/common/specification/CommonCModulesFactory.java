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
import org.ourgrid.common.specification.lexical.CommonLexicalAnalyzer;
import org.ourgrid.common.specification.lexical.LexicalAnalyzer;
import org.ourgrid.common.specification.lexical.LexicalException;
import org.ourgrid.common.specification.semantic.CommonSemanticAnalyzer;
import org.ourgrid.common.specification.semantic.SemanticActions;
import org.ourgrid.common.specification.semantic.SemanticAnalyzer;
import org.ourgrid.common.specification.semantic.exception.SemanticException;
import org.ourgrid.common.specification.syntactical.CommonSyntacticalAnalyzer;
import org.ourgrid.common.specification.syntactical.SyntacticalAnalyzer;
import org.ourgrid.common.specification.syntactical.SyntacticalException;

/**
 * @see org.ourgrid.common.specification.CompilerModulesFactory
 */
public class CommonCModulesFactory implements CompilerModulesFactory {

	/**
	 * The constructor.
	 */
	public CommonCModulesFactory() {

		// do nothing...
	}


	/**
	 * @see org.ourgrid.common.specification.CompilerModulesFactory#createLexicalAnalyzer(String)
	 */
	public LexicalAnalyzer createLexicalAnalyzer( String sourceFile ) throws LexicalException {

		return new CommonLexicalAnalyzer( sourceFile );
	}


	/**
	 * @see org.ourgrid.common.specification.CompilerModulesFactory#createSyntacticalAnalyzer(org.ourgrid.common.specification.lexical.LexicalAnalyzer,
	 *      org.ourgrid.common.specification.grammar.Grammar,
	 *      org.ourgrid.common.specification.semantic.SemanticAnalyzer)
	 */
	public SyntacticalAnalyzer createSyntacticalAnalyzer( LexicalAnalyzer lexicalAnalyzer, Grammar languageGrammar,
															SemanticAnalyzer semantic ) throws SyntacticalException {

		return new CommonSyntacticalAnalyzer( lexicalAnalyzer, languageGrammar, semantic );
	}


	/**
	 * @see org.ourgrid.common.specification.CompilerModulesFactory#createSemanticAnalyzer(org.ourgrid.common.specification.semantic.SemanticActions)
	 */
	public SemanticAnalyzer createSemanticAnalyzer( SemanticActions actionsContainer ) throws SemanticException {

		CommonSemanticAnalyzer semantic = new CommonSemanticAnalyzer( actionsContainer );
		return semantic;
	}

}
