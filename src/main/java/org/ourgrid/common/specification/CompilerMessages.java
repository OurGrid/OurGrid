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

import org.ourgrid.common.specification.token.Token;

/**
 * Description: A class with all messages that are showed to the user at the
 * compilation process. Some messages are static attributes but other are
 * dynamic methods that can return a message pattern based on parameters.
 */
public class CompilerMessages {

	// /////////////////////

	public static final String BAD_LANGUAGE_TYPE = "The type specified is not valid.";


	public static String BAD_SOURCE_FILE( String sourcePath ) {

		return "The file \"" + sourcePath + "\" could not be read or does not exist.";
	}


	public static String BAD_SPECIAL_CHAR( int lineNumber ) {

		return "Back slash found at line " + lineNumber + ", but no special char was recognized.";
	}

	// //////////////////// GRAMMAR //////////////////////////

	public static final String BAD_GRAMMAR_FILE_IOPROBLEMS = "The grammar file could not be read or does not exist.";

	public static final String BAD_GRAMMAR_FILE_STRUCTURE = "The grammar could not be loaded because of some error in its structure.";

	public static final String BAD_GRAMMAR_FILE_NOT_FOUND = "The grammar could not be loaded because the file specified could not be found.";

	public static final String ERROR_GRAMMAR_IO = "The grammar could not be loaded because of some I/O exception.";


	public static String ERROR_GRAMMAR_MALFORMED_RULE( String rulesHeadSymbol ) {

		return "Malformed grammar - the head of a rule should be a non terminal. Check rule with head [ "
				+ rulesHeadSymbol + " ] ";
	}

	// //////////////////// SPECIFICATIONS //////////////////////////////////

	public static final String BAD_PEER_DEFINITION_USERNAME_OR_SERVER_MISSING = "The peer 'username' and 'server' attributes are either missing or invalid.";

	public static final String BAD_TASK_SPEC_REMEXEC_MISSING = "Necessary remote execution information does not exist!";


	public static String BAD_WORKER_DEFINITION( String workerURL ) {

		return "Bad Worker address definition: " + workerURL
				+ ". Check if it has a valid address and the type attribute is defined";
	}


	public static String BAD_TASK_DEFINITION( int taskPositionAtJDF, String theCauseMessage ) {

		return "Task number " + taskPositionAtJDF + " is not valid. \n Error: " + theCauseMessage;
	}

	// //////////////////// SEMANTIC ACTIONS /////////////////////////////

	public static final String SEMANTIC_ACTION_NOT_FOUND = "Could not find the action method! Probably the Grammar and the semantic actions set are not compatible.\n Please report this as a bug.";

	public static final String SEMANTIC_CLASS_NOT_FOUND = "Good God! It could not find the semantic actions class.\n Please report this as a bug.";


	public static String SEMANTIC_FATAL_ERROR() {

		return ("Problems in file processing. \n Please verify the entered file or if it does not have problems, report this as a bug.");
	}

	public static final String SEMANTIC_FATAL_ILLEGAL_ACCESS = "Could not access the environment into Semantic Analyzer. \n Please report this as a bug.";

	public static final String SEMANTIC_MALFORMED_IO_COMMAND = "One I/O command (put, store, get) is bad specified. Use OurGrid manual to see what is wrong.";


	public static String SEMANTIC_EMPTY_ATTRIBUTE_VALUE( String attName, int line ) {

		return ("Attribute named " + attName + " cannot have an empty value. Check line " + line + ".");
	}


	// ////////////////// SYNTACTICAL MODULE //////////////////////////

	public static String SYNTACTICAL_COMPILATION_PROBLEM( Token actualToken, String nextSymbolExpected ) {

		return "There's a syntax problem at the source: check line " + actualToken.getLine() + " near word "
				+ actualToken.getSymbol() + ".\n Maybe the symbol \"" + nextSymbolExpected
				+ "\" is missing at the source!";
	}


	// ////////////////// LEXICAL MODULE //////////////////////////

	public static String LEXICAL_READING_SOURCE_PROBLEM( String sourcePath, String moreSpecificMessage ) {

		return "Unable to read the source at " + sourcePath + ". \n Cause: " + moreSpecificMessage;
	}

	public static final String LEXICAL_FATAL_TOKEN_NOT_RECOGNIZED = "Not expected problem! Could not identify a token. \n Please report this as a bug.";

	public static final String DESCRIPTION_FILE_IS_EMPTY = "Description file is empty";

	
	/*
	 * JDL MESSAGES
	 */
	
	public static final String FILE_IS_EMPTY = " file is empty";
	
	public static final String JDL_UNSUPPORTED_TYPE ( String type ){
		return  "Ourgrid does not support execution of ".concat( type ).concat( " type." );
	}

	public static final String JDL_UNSUPPORTED_JOB_TYPE ( String jobType ){
		return  "Ourgrid does not support execution of ".concat( jobType ).concat( " jobs." );
	}

	public static String MISSING_ATTRIBUTE( String name ) {

		return  "Missing attribute \"".concat( name ).concat( "\"." );
	}
	
	/*
	 * SDF MESSAGES
	 */
				
	public static final String SEMANTIC_MISSING_ATTRIBUTE(String attrName){
		return "Attribute named "+attrName+" must be defined.";
	}


	public static String INVALID_TYPE(String attributeName, String type) {
		return "Attribute named "+attributeName+" type must be "+type;
	}
}
