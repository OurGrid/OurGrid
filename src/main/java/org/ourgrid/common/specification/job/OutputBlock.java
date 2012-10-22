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
import java.util.List;

/**
 * This entity handles the input entries for a JDL compliant task.
 * 
 * @see IOEntry Created on Jul 1, 2004
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public class OutputBlock extends IOBlock {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 33L;

	private String outputSandBoxBaseURI;

	private List<String> outputSandBox;

	private List<String> outputSandBoxDest;


	/**
	 * An empty constructor
	 * @param outputSandBoxDest 
	 * @param outputSandBoxBaseURI 
	 * @param outputSandBox 
	 */
	public OutputBlock(List<String> outputSandBox, String outputSandBoxBaseURI, List<String> outputSandBoxDest ) {
		super();
		this.outputSandBox = outputSandBox;
		this.outputSandBoxBaseURI = outputSandBoxBaseURI;
		this.outputSandBoxDest = outputSandBoxDest;
		buildEntries();
	}

	/**
	 * 
	 */
	private void buildEntries() {
		String path = outputSandBoxBaseURI;
		if(!(path.length() == 0)){
			path += File.separatorChar;
		}
		for ( int i = 0; i < outputSandBox.size(); i++ ) {
			putEntry( "", new IOEntry( "GET", outputSandBox.get( i ), path.concat( outputSandBoxDest.get( i ) ) ) );
		}
	}

	
	/**
	 * @return the outputSandBoxBaseURI
	 */
	public String getOutputSandBoxBaseURI() {
	
		return outputSandBoxBaseURI;
	}
}
