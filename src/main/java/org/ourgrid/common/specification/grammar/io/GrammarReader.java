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
package org.ourgrid.common.specification.grammar.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.ourgrid.common.specification.grammar.Grammar;

/**
 * This entity is responsable to know how to read a gammar description file.
 * Created on 22/05/2004
 */
public interface GrammarReader {

	/**
	 * Read a gammar description file from a determined format and returns a
	 * Grammar object after the process
	 * 
	 * @param fileName The file where the grammar is described.
	 * @param grammar The object grammar that will be filled with the
	 *        informations from the file.
	 * @return A Grammar object
	 */
	public Grammar read( File fileName, Grammar grammar ) throws MalformedGrammarException, IOException;


	/**
	 * Read a gammar description file from a determined format and returns a
	 * Grammar object after the process
	 * 
	 * @param stream The stream that contains the description of the grammar
	 * @param grammar The object grammar that will be filled with the
	 *        informations from the file.
	 * @return A Grammar object
	 */
	public Grammar read( InputStream stream, Grammar grammar ) throws MalformedGrammarException, IOException;
}
