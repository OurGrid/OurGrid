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
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.glite.jdl.Ad;
import org.glite.jdl.AdParser;
import org.glite.jdl.JobAdException;
import org.ourgrid.common.specification.CompilerMessages;
import org.ourgrid.common.specification.syntactical.SyntacticalException;

import condor.classad.Expr;

/**
 * A JDL semantic check compiler. It encapsulates the default JDL compiler adding 
 * some specific OurGrid syntax requirements.
 * 
 * @see AdParser
 * 
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public class JDLSyntacticalAnalyzer extends ClassAdSyntacticalAnalyzerStream{
	
	private static transient final Logger LOG = Logger
	.getLogger( JDLSyntacticalAnalyzer.class );
	
	/**
	 * Compiles a JDL building a valid {@link Ad} object.
	 * @param sourceFileName The JDL file source Path
	 * @return The equivalent compiled {@link Ad} 
	 * @throws CompilerException If there is no source file, or any syntactical mistake.
	 */
	public static Ad compileJDL( String sourceFileName ) throws CompilerException {

		File sourceFile = new File( sourceFileName );
		if ( (!sourceFile.exists()) || (!sourceFile.canRead()) ) {			
			IOException ioex = new IOException( CompilerMessages.BAD_SOURCE_FILE( sourceFile.getAbsolutePath() ) );
			throw new CompilerException( ioex.getMessage(), ioex );
		}
		
		LOG.debug( "Compilation started for source \"" + sourceFile.getAbsolutePath() + "\"" );

		try {
			Ad ad = (Ad) AdParser.parseJdl( readJDLExpression( sourceFile ) );
			validateAttributes(ad);
			return ad;
		} catch ( FileNotFoundException e ) {
			throw new CompilerException( e.getMessage(), e );
		} catch ( JobAdException e ) {
			throw new CompilerException( errBuilder.toString().concat( e.getMessage() ), e );
		}finally{
			resetSystemErr();
		}
	}

	/**
	 * Open an JDL file and read the whole expression.
	 * @param sourceFile A JDL source file path
	 * @return A {@link String} containing the read expression. 
	 * @throws FileNotFoundException When there is no file to read.
	 */
	private static String readJDLExpression( File sourceFile ) throws FileNotFoundException {

		StringBuilder builder = new StringBuilder();
		Scanner input = new Scanner(sourceFile);
		while ( input.hasNextLine() ) {
			builder.append( input.nextLine() );
		}
		changeSystemErrToStream();
		return builder.toString();
	}

	/**
	 * Check if any OurGrid additional required attributes (described above) are available in the generated 
	 * {@link Ad}. Here is the list of these attributes:
	 * 
	 * <ol>
	 * <li>Name : Job label</li>
	 * </ol>
	 * 
	 * @param ad The generated {@link Ad}
	 * @throws SyntacticalException When an attribute is missing.
	 */
	private static void validateAttributes( Ad ad ) throws SyntacticalException {

		Expr value = ad.lookup( JdlOGExtension.NAME );
		if(value == null){
			throw new SyntacticalException( CompilerMessages.MISSING_ATTRIBUTE(JdlOGExtension.NAME) );
		}
	}
	
}
