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
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import condor.classad.ClassAdParser;
import condor.classad.RecordExpr;

/**
 * This class is responsible for doing the syntactical analysis of SDF files in classad format
 * @author David Candeia Medeiros Maia
 */
public class SDFClassAdsSyntacticalAnalyzer extends ClassAdSyntacticalAnalyzerStream{
	
	public static final String LIST_SYMBOL = "{";
	public static final String RECORD_SYMBOL = "[";
	
	/**
	 * This method requests the syntactical analysis of the SDF file to condor ClassAdParser 
	 * @param sourceFileName The SDF file in classad format
	 * @return A record expression containing the parsed file attributes
	 * @throws CompilerException Exception thrown if an error occurs during syntactical analysis
	 */
	public static RecordExpr compile(String sourceFileName) throws CompilerException{
		changeSystemErrToStream();
		
		try {
			ClassAdParser parser = new ClassAdParser(new FileInputStream(new File(sourceFileName)));
		
			RecordExpr result = (RecordExpr) parser.parse();
			
			String error = errBuilder.toString();
			resetSystemErr();
			if(error.length() > 0){
				throw new CompilerException(error);
			}
			
			return result;
		} catch (FileNotFoundException e) {
			throw new CompilerException(e.getMessage());
		}
	}
}
