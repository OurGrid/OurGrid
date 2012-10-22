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
package org.ourgrid.common.specification.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.ourgrid.common.specification.CommonCModulesFactory;
import org.ourgrid.common.specification.CompilerMessages;
import org.ourgrid.common.specification.CompilerModulesFactory;
import org.ourgrid.common.specification.grammar.CommonGrammar;
import org.ourgrid.common.specification.grammar.io.GalsGrammarReader;
import org.ourgrid.common.specification.grammar.io.MalformedGrammarException;
import org.ourgrid.common.specification.lexical.CommonLexicalAnalyzer;
import org.ourgrid.common.specification.semantic.CommonSemanticAnalyzer;
import org.ourgrid.common.specification.semantic.JDFSemanticActions;
import org.ourgrid.common.specification.semantic.SemanticActions;
import org.ourgrid.common.specification.semantic.SemanticAnalyzer;
import org.ourgrid.common.specification.syntactical.CommonSyntacticalAnalyzer;
import org.ourgrid.common.specification.syntactical.SyntacticalAnalyzer;
import org.ourgrid.common.specification.syntactical.SyntacticalException;

/**
 * @see org.ourgrid.common.specification.main.Compiler
 */
public class CommonCompiler implements Compiler {

	public static final String JDF_TYPE = "JOB";

	public static final String JDL_TYPE = "JDL";

//	public static final String GDF_TYPE = "GDF";

	public static final String SDF_TYPE = "SDF";
	
	public static enum FileType{ JDF, JDL, SDF };
	

//	private String GDF_FILE_NAME = "/resources/specs/GDFGrammar.gals";

//	private String SDF_FILE_NAME = "/resources/specs/SDFGrammar.gals";

	private String JDF_FILE_NAME = "/resources/specs/JDFGrammar.gals";

	private static File sourceFile;

	private InputStream grammarFileStream;

	private static transient final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger( CommonCompiler.class );

	
	private CompilerModulesFactory factory;

	private SemanticActions actions;

	/* The last action that will be executed has to set it */
	private List result;


	/**
	 * Initialize the object.
	 */
	public CommonCompiler() {
		factory = new CommonCModulesFactory();
	}


	/**
	 * @see org.ourgrid.common.specification.main.Compiler#compile(String, String)
	 */
	public void compile( String sourceFileName, FileType languageType ) throws CompilerException {

		this.openFiles( sourceFileName, languageType );
		CommonSemanticAnalyzer semanticAnalyzer = this.buildSemanticAnalyzer( languageType );
		CommonSyntacticalAnalyzer syntactical = (CommonSyntacticalAnalyzer) this
				.buildSyntacticalAnalyzer( semanticAnalyzer );
		run( syntactical );
		result = this.actions.getResult();
		LOG.debug( "Compilation finished successfully!" );

	}


	/**
	 * @see org.ourgrid.common.specification.main.Compiler#getResult()
	 */
	public List getResult() {

		return this.result;
	}


	/**
	 * @return Returns the parent directory of the source that is been compiled.
	 */
	public static String getSourceParentDir() {

		return CommonCompiler.sourceFile.getAbsoluteFile().getParent();
	}


	/**
	 * Starts the compilation at the given syntactical analyzer.
	 * 
	 * @param syntactical A syntactical analyzer.
	 * @throws CompilerException
	 */
	private void run( CommonSyntacticalAnalyzer syntactical ) throws CompilerException {

		try {
			LOG.debug( "Compilation started for source \"" + sourceFile.getAbsolutePath() + "\"" );
			syntactical.startCompilation();
		} catch ( SyntacticalException sex ) {
			LOG.error( "Problems at the compilation. ", sex );
			throw new CompilerException( sex.getMessage(), sex );
		}
	}


	/**
	 * Builds all the entities necessaries to build a syntactiacal analyzer and
	 * return it.
	 * 
	 * @param semantic The SemanticAnalyzer
	 * @return A SyntacticalAnalyzer
	 * @throws CompilerException
	 */
	private SyntacticalAnalyzer buildSyntacticalAnalyzer( SemanticAnalyzer semantic ) throws CompilerException {

		GalsGrammarReader reader = new GalsGrammarReader();
		CommonGrammar grammar = new CommonGrammar();
		buildGrammar( reader, grammar );
		CommonLexicalAnalyzer lexical = (CommonLexicalAnalyzer) factory.createLexicalAnalyzer( sourceFile
				.getAbsolutePath() );
		CommonSyntacticalAnalyzer syntactical;
		syntactical = (CommonSyntacticalAnalyzer) factory.createSyntacticalAnalyzer( lexical, grammar, semantic );

		return syntactical;
	}


	/*
	 * Will read the right grammar file, create the grammar object and prepare
	 * it for use. @param reader The specific grammar reader. @param grammar The
	 * grammar object that will be filled up with the informations read from
	 * grammar file source.
	 */
	private void buildGrammar( GalsGrammarReader reader, CommonGrammar grammar ) throws CompilerException {

		try {
			reader.read( grammarFileStream, grammar );
			grammar.loadSyntacticTable();
		} catch ( MalformedGrammarException mfex ) {
			LOG.error( "The grammar could not be loaded because of some error in its structure." );
			throw new CompilerException( CompilerMessages.BAD_GRAMMAR_FILE_STRUCTURE, mfex );
		} catch ( FileNotFoundException fnfex ) {
			LOG.error( "The grammar could not be loaded because the file specified could not be found." );
			throw new CompilerException( CompilerMessages.BAD_GRAMMAR_FILE_NOT_FOUND, fnfex );
		} catch ( IOException ioex ) {
			LOG.error( "The grammar could not be loaded because of some I/O exception." );
			throw new CompilerException( CompilerMessages.ERROR_GRAMMAR_IO, ioex );
		}
	}


	/**
	 * Will open and check the "good health" of the specified objects: the
	 * source file and the grammar file related to languageType.
	 * 
	 * @param source the source file to be compiled.
	 * @param languageType the language type of the file to be compiled.
	 * @throws CompilerException If any of the files could not be found or read.
	 */
	private void openFiles( String source, FileType languageType ) throws CompilerException {

		// Validating the files
		CommonCompiler.sourceFile = new File( source );
		if ( (!sourceFile.exists()) || (!sourceFile.canRead()) ) {			
			IOException ioex = new IOException( CompilerMessages.BAD_SOURCE_FILE( sourceFile.getAbsolutePath() ) );
			throw new CompilerException( ioex.getMessage(), ioex );
		}

		String resourceURL = null;
		switch ( languageType ) {
			case JDF:
				resourceURL = JDF_FILE_NAME;
				break;
//			case GDF:
//				resourceURL = GDF_FILE_NAME;
//				break;
//			case SDF:
//				resourceURL = SDF_FILE_NAME;
//				break;
			case JDL:
				return;
			default:
				throw new CompilerException( CompilerMessages.BAD_LANGUAGE_TYPE );
		}
		this.grammarFileStream = this.getClass().getResourceAsStream(resourceURL);
	}


	/**
	 * Will build a semantic analyzer to the specific given language type.
	 * 
	 * @param languageType the language type of the source that will be
	 *        compiled.
	 * @return the SemanticAnalyzer for the language type.
	 * @throws CompilerException If the semantic creation fails.
	 */
	private CommonSemanticAnalyzer buildSemanticAnalyzer( FileType languageType ) throws CompilerException {

		CommonSemanticAnalyzer analyzer;
		switch ( languageType ) {
			case JDF:
				this.actions = new JDFSemanticActions();
				analyzer = (CommonSemanticAnalyzer) this.factory.createSemanticAnalyzer( actions );
				break;
//			case GDF:
//				this.actions = new GDFSemanticActions();
//				analyzer = (CommonSemanticAnalyzer) this.factory.createSemanticAnalyzer( actions );
//				break;
//			case SDF:
//				this.actions = new SDFSemanticActions();
//				analyzer = (CommonSemanticAnalyzer) this.factory.createSemanticAnalyzer( actions );
//				break;
			default:
				throw new CompilerException( CompilerMessages.BAD_LANGUAGE_TYPE );
		}
		return analyzer;
	}

}
