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
package org.ourgrid.common.ui.gui;

import java.awt.Component;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class OGFileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;
	private static String filePath = ".";
	
	public OGFileChooser( final String description, final String extension ) {

		super( new File( filePath ) );
		addChoosableFileFilter( new FileFilter() {

			@Override
			public boolean accept( File f ) {
				
				if(f.isDirectory()){
					return true;
				}
				
				String lowerCaseName = f.getName().toLowerCase();
				if ( lowerCaseName.endsWith( extension ) ) {
					filePath = f.getParent();
					return true;
				}
				return false;
			}
			
			@Override
			public String getDescription() {

				return description;
			}
		} );
	}
	
	
	public OGFileChooser(final Map<String, String> extensions) {
		
		super( new File( filePath ) );

		Set<Entry<String,String>> entrySet = extensions.entrySet();
		for ( final Entry<String,String> entry : entrySet ) {
			addChoosableFileFilter( new FileFilter() {
				@Override
				public boolean accept( File f ) {
					if(f.isDirectory()){
						return true;
					}
					String lowerCaseName = f.getName().toLowerCase();
					if ( lowerCaseName.endsWith( entry.getKey() ) ) {
						filePath = f.getParent();
						return true;
					}
					return false;
				}
				
				@Override
				public String getDescription() {
					return entry.getValue();
				}
			} );
		}
	}


	/**
	 * Return Selected file in file chooser
	 */
	public File selectFile( Component parent ) {

		int result = this.showOpenDialog( parent );
		if ( result == JFileChooser.APPROVE_OPTION )
			return this.getSelectedFile();
		return null;
	}
}
