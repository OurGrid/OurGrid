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
package org.ourgrid.common.specification.token;

import java.io.IOException;

import org.ourgrid.common.specification.CodesTable;
import org.ourgrid.common.specification.CompilerMessages;
import org.ourgrid.common.specification.SpecialCharException;
import org.ourgrid.common.specification.TokenDelimiter;
import org.ourgrid.common.specification.io.CharReader;

/**
 * It is a Token object that is recognized as a string at the CodesTable.
 * 
 * @see org.ourgrid.common.specification.CodesTable
 */
public class StringToken extends Token {

	private CodesTable codesTable = CodesTable.getInstance();

	/** Store all the special chars that are readed only after a "\" symbol. */
	private TokenDelimiter specialChar;


	public StringToken() {

		specialChar = new TokenDelimiter();
		specialChar.addDelimiter( '\"' );
		specialChar.addDelimiter( '\\' );
	}


	/**
	 * Check if the character passed as paramether is part of a string. This
	 * method of reading string considers many symbols as end-of-word. Check
	 * symbol at method "this.isEndOfWord()"
	 * 
	 * @param firstPart - Is the String that probably begins the StringToken
	 *        symbol
	 * @param reader - Is the reader that controls the character reading process
	 *        from source.
	 * @return A Token object if a string was recognized beggining with
	 *         "firstPart"; or "null" if it was not.
	 * @see org.ourgrid.common.specification.CodesTable
	 */
	public Token readStringToken( String firstPart, CharReader reader ) throws IOException {

		StringBuffer buffer = new StringBuffer();
		buffer.append( firstPart );
		char next = reader.readChar();
		while ( !isEndOfWord( next, reader ) && next != CharReader.EOF_CHAR ) {
			buffer.append( next );
			next = reader.readChar();
		}

		String tokenSymbol = buffer.toString();
		Token theToken = getToken( tokenSymbol, reader );

		return this.checkOtherTypes( theToken );

	}


	/**
	 * Generalizes the token reading process when considering the token
	 * delimiters desired.
	 * 
	 * @param reader the reader able to get the characters from source.
	 * @param delimiters the delimiter's set
	 * @return A token where the token's symbol is the "string read"
	 * @throws IOException
	 * @throws SpecialCharException If a back slash is found a no special char
	 *         was recognized.
	 */
	public Token readString( CharReader reader, TokenDelimiter delimiters ) throws SpecialCharException, IOException {

		StringBuffer buffer = new StringBuffer();
		char next = reader.readNonBlankChar();

		if ( next == '\"' ) {
			delimiters = new TokenDelimiter();
			delimiters.addDelimiter( '\"' );
			next = reader.readNonBlankChar();
		}

		// The End-Of-File char is always a delimiter
		delimiters.addDelimiter( CharReader.EOF_CHAR );
		while ( !delimiters.contains( next ) ) {
			if ( next == '\\' ) {
				next = this.checkSpecialChar( reader );
			}
			buffer.append( next );
			next = reader.readChar();
		}
		// Will unread a delimiter if it is equals to "\n" and only when it was
		// used as delimiter
		// It means that a \n will not be unread if it appears alone.
		if ( next == '\n' && buffer.length() != 0 )
			reader.unreadChar( next );
		this.setCode( codesTable.getCode( CodesTable.STRING ) );
		this.setLine( reader.getActualLine() );
		this.setSymbol( buffer.toString().trim() );
		return this;
	}


	/**
	 * Check if the next character readed form reader is a valid special one. It
	 * is used after read a '\' (back slash) symbol.
	 * 
	 * @param reader the reader from source.
	 * @return the character to be inserted as next.
	 * @throws IOException If an error occurs at reader.
	 * @throws SpecialCharException
	 */
	private char checkSpecialChar( CharReader reader ) throws IOException, SpecialCharException {

		char nextNext = reader.readChar();
		if ( specialChar.contains( nextNext ) ) {
			// It is the treatment for the actual special character " and \, but
			// it probably
			// have to change to another special characters.
			return nextNext;
		}
		throw new SpecialCharException( CompilerMessages.BAD_SPECIAL_CHAR( reader.getActualLine() ) );
	}


	/**
	 * @param theToken token read as StringToken
	 * @return The same StringToken if it is not other "special type", the
	 *         special type operator or a special terminal symbol.
	 */
	private Token checkOtherTypes( Token theToken ) {

		String symbol = theToken.getSymbol();
		int line = theToken.getLine();
		int code = codesTable.getCode( symbol );

		if ( code != 0 ) {
			int type = codesTable.getType( symbol );
			if ( type == CodesTable.OPERATOR ) {
				theToken = new Operator();
			} else if ( type == CodesTable.RESERVED_WORD ) {
				theToken = new Token();
			}
			theToken.setSymbol( symbol );
			theToken.setLine( line );
			theToken.setCode( code );
		}
		return theToken;
	}


	/**
	 * Builds this object to be returned using the informations passed as
	 * paramether.
	 * 
	 * @param tokenSymbol the peace of source read
	 * @param reader the reader able to read source
	 * @return Token The token that could be build using the tokenSymbol
	 */
	private Token getToken( String tokenSymbol, CharReader reader ) {

		CodesTable codesTable = CodesTable.getInstance();
		int code = codesTable.getCode( CodesTable.STRING );

		// Setting the Token object
		this.setSymbol( tokenSymbol );
		this.setCode( code );
		this.setLine( reader.getActualLine() );

		return this;
	}


	/**
	 * Tells if the next chararacter is a symbol that defines the end of a
	 * string for the commom lexical compiler. OBS.: If the character is found
	 * as a End-Of-Word then it will be unread!
	 * 
	 * @param theChar The character to be analyzed.
	 * @param reader The reader used to read the source.
	 * @return Will return true if the next char is any of this symbols: blanck
	 *         space, tab, end of line, '\t', ';', '{', '}', '(', ')', ':'; that
	 *         are symbols that represents a end of a string.
	 * @throws IOException Thrown if could not unread a char from the "reader".
	 */
	public static boolean isEndOfWord( char theChar, CharReader reader ) throws IOException {

		if ( theChar == ' ' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == '\t' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == '\n' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == ';' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == '{' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == '}' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == '(' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == ')' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == ':' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == '=' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == '>' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == '<' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == '&' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == '|' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == '!' ) {
			reader.unreadChar( theChar );
			return true;
		}
		if ( theChar == '\"' ) {
			reader.unreadChar( theChar );
			return true;
		}
		return false;
	}

}
