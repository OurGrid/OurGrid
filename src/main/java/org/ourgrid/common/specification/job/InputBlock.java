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
package org.ourgrid.common.specification.job;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This entity handles the input entries for a JDL compliant task.
 * 
 * @see IOEntry Created on Jul 1, 2004
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public class InputBlock extends IOBlock {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 33L;

	private String inputSandBoxBaseURI;

	private List<String> inputSandBox;


	/**
	 * An empty constructor
	 */
	public InputBlock( String inputSandBoxBaseURI, List<String> inputSandBox ) {
		super();
		this.inputSandBoxBaseURI = inputSandBoxBaseURI;
		this.inputSandBox = inputSandBox;
		buildEntries();
	}


	/**
	 * 
	 */
	private void buildEntries() {
		
		String path = inputSandBoxBaseURI;
		if( !(path.length() == 0) ){
			path += File.separatorChar;
		}
		
		for ( String inputFile : inputSandBox ) {
			File iFile = new File(inputFile);
			if ( !iFile.isAbsolute() ){
				try {
					inputFile = new File(path + inputFile).getCanonicalPath();
				} catch (IOException e) {}
			}
			putEntry( "", new IOEntry( "PUT", inputFile, new File( inputFile ).getName()) );
		}
	}
}