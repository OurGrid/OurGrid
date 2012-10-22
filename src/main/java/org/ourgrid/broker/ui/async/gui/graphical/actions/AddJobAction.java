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
package org.ourgrid.broker.ui.async.gui.graphical.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.ourgrid.broker.ui.async.client.BrokerAsyncInitializer;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.ui.gui.OGFileChooser;
import org.ourgrid.common.util.CommonUtils;

public class AddJobAction extends AbstractBrokerAction {

	private static final long serialVersionUID = 1L;

	public static final String JDF_FILE_EXT = ".jdf";
	public static final String JDF_FILE_DESCR = "Job Description File (*.jdf)";
	
	public static final String JDL_FILE_EXT = ".jdl";
	public static final String JDL_FILE_DESCR = "JDL File (*.jdl)";
	
	private static final String ACTION_NAME = "Add job";

	private final String jdf;

	private Component view;


	public AddJobAction( Component view ) {
		this( view, null, null );
	}


	public AddJobAction( Component view, String jobLabel, String jdfPath ) {
		this(getActionName(jobLabel, jdfPath), jobLabel, jdfPath);
		this.view = view;
	}
	
	public AddJobAction(String title, String jobLabel, String jdfPath) {
		super(title);
		this.jdf = jdfPath;
	}


	private static String getActionName(String jobLabel, String jdfPath) {
		if (jobLabel == null) {
			if (jdfPath == null) {
				return ACTION_NAME;
			}
			return ACTION_NAME + ": " + new File(jdfPath).getName();
		}
		return ACTION_NAME + ": " + jobLabel + " - " + new File(jdfPath).getName();
	}


	public void actionPerformed( ActionEvent e ) {
		
		File jdfFile;
		String job = null;
		boolean addToHistory = true;
		if (jdf == null) {
			Map<String, String> map = CommonUtils.createSerializableMap();
			map.put( JDF_FILE_EXT, JDF_FILE_DESCR );
			map.put( JDL_FILE_EXT, JDL_FILE_DESCR );
			
			OGFileChooser ogFileChooser = new OGFileChooser( map );
			for (FileFilter fileFilter : ogFileChooser.getChoosableFileFilters()) {
				if (fileFilter.getDescription().equals(JDF_FILE_DESCR)) {
					ogFileChooser.setFileFilter(fileFilter);
					break;
				}
			}
			
			jdfFile = ogFileChooser.selectFile( this.view );
			if (jdfFile == null) {
				return;
			}
			job = jdfFile.getPath();
			
		} else {
			job = jdf;
			addToHistory = false;
		}
		
		try {
			if(job.endsWith( ".jdl" )){
				BrokerAsyncInitializer.getInstance().getComponentClient().addJDLJob(job, addToHistory);
			}else{
				BrokerAsyncInitializer.getInstance().getComponentClient().addJob(job, addToHistory);
			}
		} catch (CompilerException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Error on jdf compilation", 
					JOptionPane.ERROR_MESSAGE);
		}
	}


	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.jdf == null) ? 0 : this.jdf.hashCode());
		return result;
	}


	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		final AddJobAction other = (AddJobAction) obj;
		if ( this.jdf == null ) {
			if ( other.jdf != null )
				return false;
		} else if ( !this.jdf.equals( other.jdf ) )
			return false;
		return true;
	}

}
