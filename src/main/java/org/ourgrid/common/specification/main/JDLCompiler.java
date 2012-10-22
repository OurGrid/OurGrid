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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.specification.CompilerMessages;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.main.CommonCompiler.FileType;

/**
 * A compiler specialised in file with ClassAd-like sintax.
 *   
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 *
 * @see org.ourgrid.common.specification.main.Compiler
 */
public class JDLCompiler implements Compiler {
	
	private List result;

	protected PrintStream builder;

	private static transient final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
	.getLogger( JDLCompiler.class );
	
	/**
	 * Default empty constructor
	 */
	public JDLCompiler() {
		result = new ArrayList();
	}

	/**
	 * {@inheritDoc}
	 */
	public void compile( String sourceFileName, FileType languageType ) throws CompilerException {
		
		assert sourceFileName != null: "UI should have guaranteeded that source file name is not null.";
		assert languageType != null: "Cannot be null.";

		switch ( languageType ) {
			case JDL:
				result = new ArrayList<JobSpecification>();
				result.addAll( JDLSemanticAnalyzer.compileJDL( sourceFileName ) );
				break;
			case SDF:
				result.addAll( SDFClassAdsSemanticAnalyzer.compile( sourceFileName ) );
				break;
			default:
				throw new CompilerException( CompilerMessages.BAD_LANGUAGE_TYPE );
		}
		LOG.debug( "Compilation finished successfully!" );
	}

	/**
	 * {@inheritDoc}
	 */
	public List getResult() {

		return result;
	}

}
