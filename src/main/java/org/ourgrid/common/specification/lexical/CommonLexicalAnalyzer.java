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
package org.ourgrid.common.specification.lexical;

import java.io.IOException;

import org.ourgrid.common.specification.CodesTable;
import org.ourgrid.common.specification.CompilerMessages;
import org.ourgrid.common.specification.SpecialCharException;
import org.ourgrid.common.specification.TokenDelimiter;
import org.ourgrid.common.specification.io.CharReader;
import org.ourgrid.common.specification.token.Operator;
import org.ourgrid.common.specification.token.Parenthetic;
import org.ourgrid.common.specification.token.Pointing;
import org.ourgrid.common.specification.token.StringToken;
import org.ourgrid.common.specification.token.Token;

/**
 * This is a Common Lexical Analyzer. It means that it implements the
 * LexicalAnalyzer interface in a simple way.
 */
public class CommonLexicalAnalyzer implements LexicalAnalyzer {

	/**
	 * Logger to store log messages
	 */
	private static transient final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger( CommonLexicalAnalyzer.class );

	private CharReader charReader;

	private boolean findEOF = false;

	private String readingSource;


	/**
	 * The CommonLexicalAnalyzer constructor.
	 * 
	 * @param sourceFile - is the String that defines where is the file with the
	 *        source to be analyzed.
	 */
	public CommonLexicalAnalyzer( String sourceFile ) throws LexicalException {

		this.readingSource = sourceFile;
		try {

			charReader = new CharReader( sourceFile );

		} catch ( IOException ioex ) {

			LOG.error( "Unable to read " + sourceFile + ". Cause: " + ioex.getMessage() );
			throw new LexicalException(
					CompilerMessages.LEXICAL_READING_SOURCE_PROBLEM( sourceFile, ioex.getMessage() ), ioex );

		}
	}


	/**
	 * @see org.ourgrid.common.specification.lexical.LexicalAnalyzer#getToken()
	 */
	public Token getToken() throws LexicalException {

		Token token = new Token();
		try {

			if ( findEOF ) {
				token = null;
			} else {
				char nextNonBlankChar = charReader.readNonBlankChar();

				while ( isComment( nextNonBlankChar ) ) {
					charReader.readLine();
					nextNonBlankChar = charReader.readNonBlankChar();
				}

				if ( nextNonBlankChar == CharReader.EOF_CHAR ) {
					findEOF = true;
					token = new Token( "\\n", 0, charReader.getActualLine() );
				} else if ( nextNonBlankChar == '\n' ) {
					token = new Token( "\\n", 0, charReader.getActualLine() );
				} else {
					/*
					 * There is no way to previous know the difference between a
					 * pure string and a special word. Thats why we here try to
					 * read a string and the string will check if it is or not a
					 * special type as: operator or a terminal symbol! More then
					 * this, here, to read a StringToken, we check if the next
					 * char is a letter, or if the character is not on the
					 * CodesTable, because if it is then it have to be treated
					 * by the other object types.
					 */
					if ( Character.isLetterOrDigit( nextNonBlankChar )
							|| CodesTable.getInstance().getCode( "" + nextNonBlankChar ) == 0 ) {
						Operator operator = new Operator();
						if ( operator.readOperator( nextNonBlankChar, charReader ) != null ) {
							token = operator;
						} else {
							nextNonBlankChar = charReader.readNonBlankChar();
							StringToken string = new StringToken();
							token = string.readStringToken( "" + nextNonBlankChar, charReader );
						}
					} else {
						Pointing pointing = new Pointing();
						if ( pointing.readPointing( nextNonBlankChar, charReader ) != null ) {
							token = pointing;
						} else {
							Parenthetic parenthetic = new Parenthetic();
							if ( parenthetic.readParantizer( nextNonBlankChar, charReader ) != null ) {
								token = parenthetic;
							} else {
								Operator operator = new Operator();
								if ( operator.readOperator( nextNonBlankChar, charReader ) != null ) {
									token = operator;
								} else {
									throw new LexicalException( CompilerMessages.LEXICAL_FATAL_TOKEN_NOT_RECOGNIZED );
								}
							}
						}
					}
				}
			}

			return token;

		} catch ( IOException ioex ) {
			throw new LexicalException( CompilerMessages.LEXICAL_READING_SOURCE_PROBLEM( this.readingSource, ioex
					.getMessage() ), ioex );
		}

	}


	/**
	 * @see org.ourgrid.common.specification.lexical.LexicalAnalyzer#getToken(TokenDelimiter)
	 */
	public Token getToken( TokenDelimiter delimiters ) throws LexicalException {

		StringToken toReturn = new StringToken();
		try {
			Token token = toReturn.readString( this.charReader, delimiters );
			return token;
		} catch ( IOException ioex ) {
			throw new LexicalException( CompilerMessages.LEXICAL_READING_SOURCE_PROBLEM( this.readingSource, ioex
					.getMessage() ), ioex );
		} catch ( SpecialCharException scex ) {
			throw new LexicalException( CompilerMessages.LEXICAL_READING_SOURCE_PROBLEM( this.readingSource, scex
					.getMessage() ), scex );
		}
	}


	/**
	 * Will test if the line is a comment line
	 * 
	 * @param nextNonBlankChar the char that could initialize a comment line.
	 * @return true if the line is a comment and false otherwise.
	 */
	private boolean isComment( char nextNonBlankChar ) {

		if ( nextNonBlankChar == '#' ) {
			return true;
		}
		return false;
	}

}
