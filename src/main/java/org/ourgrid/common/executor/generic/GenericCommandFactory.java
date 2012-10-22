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
package org.ourgrid.common.executor.generic;

import java.util.LinkedList;
import java.util.List;

import org.ourgrid.common.executor.config.ExecutorConfiguration;
import org.ourgrid.common.executor.config.GenericExecutorConfiguration;
import org.ourgrid.worker.WorkerConstants;

public class GenericCommandFactory {
	
	// scripts
	private String startvmScript;
	private String listvmCmdScript;
	private String stopvmScript;
	private String createSharedFolderScript;
	private String waitForExecutionToFinish;
	private String createRestoreVMScript;
	private String createInitialSnapshotScript;
	
	public GenericCommandFactory(ExecutorConfiguration executorConfiguration){
		init(executorConfiguration);
	}
	
	public  void init(ExecutorConfiguration executorConfiguration){

//		this.startvmScript = executorConfiguration
//				.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.START_VM_COMMAND.toString());
//		this.listvmCmdScript = executorConfiguration
//		.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.LIST_VM_COMMAND.toString());
//		this.stopvmScript = executorConfiguration
//				.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.STOP_VM_COMMAND.toString());
//		this.createSharedFolderScript = executorConfiguration
//				.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.CREATE_SHARED_FOLDER_VM_COMMAND.toString());
//
//		this.waitForExecutionToFinish = executorConfiguration.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.CREATE_EXECUTION_DAEMON_COMMAND.toString());
//		this.createRestoreVMScript = executorConfiguration.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.CREATE_RESTORE_VM_COMMAND.toString());
//		this.createInitialSnapshotScript = executorConfiguration.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.CREATE_INITIAL_SNAPSHOT_COMMAND.toString());

	}

	public List<String> createInitCommand(String domainName, String sharedFolderPath, String startedFileName){
		List<String> initCommand = new LinkedList<String>();
		
		initCommand.add(startvmScript);
		initCommand.add(domainName);
		initCommand.add(sharedFolderPath);
		initCommand.add(startedFileName);
		return initCommand;
	}
	
	public List<String> createSharedFolderCommand(String domainName, String sharedFolderPath) { 
		List<String> createSharedDir = new LinkedList<String>();
		
		createSharedDir.add(createSharedFolderScript);
		//TODO Add shared folder command arguments for all hypervisors
		createSharedDir.add(domainName);
		createSharedDir.add(sharedFolderPath);
		return createSharedDir;
	}
	
	public List<String> waitForExecutionToFinish(String flagsPath, String execName, String execResultName) {
		
		List<String> waitForExecutionCommand = new LinkedList<String>();
		
		waitForExecutionCommand.add(waitForExecutionToFinish);
		waitForExecutionCommand.add(flagsPath);
		waitForExecutionCommand.add(execName);
		waitForExecutionCommand.add(execResultName);
		return waitForExecutionCommand;
	}
	
	public List<String> createRestoreVMCommand(String domainName, String initialSnapshotName){
		List<String> createRestoreVMCommand = new LinkedList<String>();
		
		createRestoreVMCommand.add(createRestoreVMScript);
		createRestoreVMCommand.add(domainName);
		createRestoreVMCommand.add(initialSnapshotName);
		return createRestoreVMCommand;
	}
	
	public List<String> createStopCommand(String domainName) {
		List<String> stopCommand = new LinkedList<String>();

		stopCommand.add(stopvmScript);
		stopCommand.add(domainName);
		return stopCommand;
	}
 
	public List<String> createVerifyCommand() {
		List<String> listCommand = new LinkedList<String>();
		
		listCommand.add(listvmCmdScript);
		return listCommand;
	}
	
	public List<String> createInitialSnapshotCommand(String domainName, String snapshotPath, String xmlSourceName) {
		List<String> snapshotCommand = new LinkedList<String>();
		
		snapshotCommand.add(createInitialSnapshotScript);
		snapshotCommand.add(domainName);
		snapshotCommand.add(snapshotPath);
		snapshotCommand.add(xmlSourceName);
		return snapshotCommand;
	}
	
}
