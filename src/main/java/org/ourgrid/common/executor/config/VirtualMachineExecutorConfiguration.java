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

import org.ourgrid.common.util.OS;
import org.ourgrid.worker.WorkerConstants;


public class VirtualMachineExecutorConfiguration extends AbstractExecutorConfiguration {

	public static final String PROPERTY_START_VM_COMMAND = "vbox_startcmd";
	public static final String PROPERTY_STOP_VM_COMMAND = "vbox_stopcmd";
	public static final String PROPERTY_WAIT_FOR_VM_COMMAND = "vbox_waitforcmd";
	public static final String PROPERTY_MACHINE_NAME = WorkerConstants.PREFIX + "vbox_machinename";
	public static final String PROPERTY_VBOX_LOCATION = WorkerConstants.PREFIX + "vbox_location";
	
	public VirtualMachineExecutorConfiguration(File rootDir) {
		super(rootDir, PROPERTY_START_VM_COMMAND, PROPERTY_STOP_VM_COMMAND, PROPERTY_WAIT_FOR_VM_COMMAND, PROPERTY_MACHINE_NAME, PROPERTY_VBOX_LOCATION);
	}
	
	@Override
	public void setDefaultProperties() {
		String startvmCmd = "";
		String stopvmCmd = "";
		String waitExecCmd = "";
		
		String path = getRootDir().getAbsolutePath() + File.separator + "sandbox_scripts" + File.separator + 
							"vbox_scripts" + File.separator + "host";
		
		if (OS.isFamilyWin9x() || OS.isFamilyWindows()) {
			startvmCmd = path + File.separator + "win" + File.separator + "init_vboxenv.bat";
			stopvmCmd = path + File.separator + "win" + File.separator + "kill_vboxenv.bat";
			waitExecCmd = path + File.separator + "win" + File.separator + "wait_for_execution.bat";
		}
		
		this.properties.put(PROPERTY_START_VM_COMMAND, startvmCmd);
		this.properties.put(PROPERTY_STOP_VM_COMMAND, stopvmCmd);
		this.properties.put(PROPERTY_WAIT_FOR_VM_COMMAND,  waitExecCmd);
	}

}
