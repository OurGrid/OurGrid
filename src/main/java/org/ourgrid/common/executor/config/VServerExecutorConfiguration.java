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
package org.ourgrid.common.executor.config;

import java.io.File;

import org.ourgrid.worker.WorkerConstants;

public class VServerExecutorConfiguration extends AbstractExecutorConfiguration {

	
	public static enum PROPERTIES {
							VM_NAME, PREPARE_ALLOCATION_COMMAND,
							START_VM_COMMAND, STOP_VM_COMMAND, STATUS_VM_COMMAND, EXEC_COMMAND,
							STOP_PREPARING_ALLOCATION_COMMAND, COPY_FILES_COMMAND, REPLACE_VM_IMAGE_COMMAND, KILL_TAR_PROC_COMMAND,
							APP_SCRIPT, APP_STDOUT_FILE_NAME, APP_STDERROR_FILE_NAME, 
							TERMINATION_FILE_NAME,
                            VM_PLAYPEN, VM_STORAGE
							}
							
	public VServerExecutorConfiguration(File rootDir) {
		super(rootDir, parseProperties());
	}
	

	public void setDefaultProperties() {
		properties.put(WorkerConstants.PREFIX + PROPERTIES.APP_SCRIPT.toString(), "app.script.sh");
		properties.put(WorkerConstants.PREFIX + PROPERTIES.APP_STDOUT_FILE_NAME.toString(), "app.stdout");
		properties.put(WorkerConstants.PREFIX + PROPERTIES.APP_STDERROR_FILE_NAME.toString(), "app.stderr");
		properties.put(WorkerConstants.PREFIX + PROPERTIES.TERMINATION_FILE_NAME.toString(), "terminated");
	}
	
	private static String[] parseProperties(){
		
		String[] properties = new String[PROPERTIES.values().length] ;
		
		for (int i = 0; i < PROPERTIES.values().length; i++) {
			properties[i] = WorkerConstants.PREFIX + PROPERTIES.values()[i].toString();
		}
		
		return properties;
	}
}
