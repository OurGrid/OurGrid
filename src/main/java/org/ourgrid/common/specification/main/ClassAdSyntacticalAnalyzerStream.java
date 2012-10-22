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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This class redirects the standard error stream in order to check
 * the occurrence of syntactical errors during JDL or ClassAd compilations
 * @author David Candeia Medeiros Maia
 *
 */
public class ClassAdSyntacticalAnalyzerStream {

	private static PrintStream systemErr;
	private static PrintStream previousStream;
	protected static StringBuilder errBuilder = new StringBuilder();
	
	/**
	 * This method redirects the standard error stream to a temporarily
	 * created stream
	 */
	public static void changeSystemErrToStream() {
		previousStream = System.err;
		systemErr = new PrintStream( new OutputStream() {
			
			@Override
			public void write( int b ) throws IOException {
		
				errBuilder.append( (char)b );
				
			}
		});
		
		System.setErr(systemErr);
	}
	
	/**
	 * This method resets the error stream to its standard value
	 */
	public static void resetSystemErr() {
		System.setErr(previousStream);
		errBuilder = new StringBuilder();
	}
	
	/**
	 * This method retrieves any occurred compilation error
	 * @return A string containing the error
	 */
	public static String getErrorMessage(){
		return errBuilder.toString();
	}
}
