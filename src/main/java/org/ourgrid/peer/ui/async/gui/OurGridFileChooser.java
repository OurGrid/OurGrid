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
package org.ourgrid.peer.ui.async.gui;

import java.io.File;

/**
 * OurGridFileChooser provides a simple mechanism for the user to choose a file. 
 * For information about using JFileChooser, see <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/components/filechooser.html">How to Use File Choosers</a>, 
 * a section in The Java Tutorial.
 */
public class OurGridFileChooser extends javax.swing.JFileChooser{
	
	public OurGridFileChooser(String arquiveDescription, String... arquiveExtensions){
		this();
		super.setFileFilter(new FileNameExtensionFilter(arquiveDescription, arquiveExtensions));
	}
	
	public OurGridFileChooser() {
		this(null);
	}
	
	public OurGridFileChooser(File lastOpenedFile){
		super(lastOpenedFile);
		super.setAcceptAllFileFilterUsed(false);
    	super.setMultiSelectionEnabled(false);
	}
	
	/**
	 * Return the file selected by the user.
	 * @return The file selected by the user.
	 */
	public File getFile(){
		File sdfFile = null;
		int returnOption = this.showOpenDialog(null);
    			
    	if (returnOption == this.APPROVE_OPTION) {
    		sdfFile = this.getSelectedFile();
    	}
    	return sdfFile;
	}
}
