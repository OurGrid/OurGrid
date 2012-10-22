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
package org.ourgrid.common.specification.io;

import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

/**
 * This is a util Character reader that can read and unread characters and
 * ignore lines.
 */
public class CharReader {

	public static final String UNIX_LINE_SEPARATOR = "\n";

	private final int FIRST_LINE = 1;

	public static final char EOF_CHAR = (char) -1;

	public static final int MAX_UNREAD_CHAR_NUMBER = 1000;

	private char next;

	private PushbackReader reader;

	private FileReader fReader;

	private int actualLine;


	/**
	 * Constructor of a character reader that uses the name of the file do be
	 * read.
	 * 
	 * @param file the name of the file to be read
	 * @throws IOException If the file could not be found.
	 */
	public CharReader( String file ) throws IOException {

		this.fReader = new FileReader( file );
		this.reader = new PushbackReader( fReader, MAX_UNREAD_CHAR_NUMBER );
		actualLine = FIRST_LINE;
	}


	/**
	 * Read a unique character at a time, including all the spetial structural
	 * character. Obs.: Ignores the special character '\r' to turn windows
	 * edited files compatible.
	 * 
	 * @return the read character.
	 */
	public char readChar() throws IOException {

		next = (char) reader.read();
		if ( next == '\r' ) {
			next = (char) reader.read();
		}
		if ( String.valueOf( next ).equals( UNIX_LINE_SEPARATOR ) ) {
			actualLine++;
		}

		return next;
	}


	/**
	 * Read only non blank characters ignoring all "Character.isWhitespace()"
	 * excepts EOF and this.UNIX_LINE_SEPARATOR
	 * 
	 * @return the non blank read character
	 * @throws IOException if happens any reading problem.
	 */
	public char readNonBlankChar() throws IOException {

		next = readChar();
		while ( Character.isWhitespace( next ) && (next != EOF_CHAR) && (next != UNIX_LINE_SEPARATOR.charAt( 0 )) ) {
			next = readChar();
		}

		return next;
	}


	/**
	 * Ignores from the actual position all the characters until the end of line
	 * (including it).
	 * 
	 * @throws IOException if happens any reading problem.
	 */
	public void readLine() throws IOException {

		next = (char) reader.read();
		while ( !(String.valueOf( next ).equals( UNIX_LINE_SEPARATOR ) || (next == EOF_CHAR)) ) {
			next = (char) reader.read();
		}
		actualLine++;
	}


	/**
	 * Unread a pre-defined ( this.MAX_UNREAD_CHAR_NUMBER ) number of read
	 * characters.
	 * 
	 * @param unwishedChar the temporary unwished char to be inserted back at
	 *        the reading queue.
	 * @throws IOException if this method is called more then the maximum times
	 *         permited (without any character be read)
	 */
	public void unreadChar( char unwishedChar ) throws IOException {

		reader.unread( unwishedChar );
		if ( String.valueOf( unwishedChar ).equals( UNIX_LINE_SEPARATOR ) ) {
			actualLine--;
			next = '\t'; // This character is just to be different from \n
							// (to be considered at the getActualLine() method)
		}
	}


	/**
	 * Closes the reader properlly.
	 * 
	 * @throws IOException if any problem related with the closing action
	 *         happens.
	 */
	public void closeStreams() throws IOException {

		reader.close();
		fReader.close();
	}


	/**
	 * @return returns the number of the line where the reader is now at the
	 *         file.
	 */
	public int getActualLine() {

		if ( next == '\n' ) {
			return actualLine - 1;
		}
		return actualLine;
	}

}
