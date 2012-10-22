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

import java.util.List;

import org.ourgrid.common.specification.main.CommonCompiler.FileType;

/**
 * This is the compiler entity. It receives a file source path, compiles the content and 
 * produces a compilation result by managing other internal entities to make the work 
 * properly. Created on 16/06/2004
 */
public interface Compiler {

	/**
	 * Launch the compiling process for a given source file. It will check the
	 * source syntactical formation and build an answer to the source according 
	 * to the language type. 
	 * 
	 * @param sourceFileName The file that contains the source to be compiled.
	 * @param languageType A member of {@link FileType} enum.
	 * @throws CompilerException Thrown when happens any problem. It generally
	 *         packs a more specific exception.
	 */
	public void compile( String sourceFileName, FileType languageType ) throws CompilerException;


	/**
	 * Its a general form of returning an answer of a compilation.
	 * 
	 * @return A List object that have to contains the answer object(s). A null
	 *         return can be returned to indicate a not necessary answer.
	 */
	public List getResult();

}
