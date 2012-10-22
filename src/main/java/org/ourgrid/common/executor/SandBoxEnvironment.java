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
package org.ourgrid.common.executor;

import java.util.Map;

import org.ourgrid.common.executor.config.ExecutorConfiguration;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;

public interface SandBoxEnvironment {

	/**
	 * 
	 * Mount the Virtual Machine Environment.
	 * 
	 * @return Process
	 * @throws ExecutorException if occur some mounting problem. 
	 */
	public Process prepareAllocation() throws ExecutorException;
	
	/**
	 * 
	 * Execute a remote command in a specific dirName, with the environment
	 * defined by envVars
	 * 
	 * @param dirName Name of directory
	 * @param command Command to be executed
	 * @param envVars 
	 * @throws ExecutorException
	 */
	public Process executeRemoteCommand(String dirName, String command, Map<String, String> envVars)
														throws ExecutorException;

	/**
	 * 
	 * Initializes the Virtual Machine Environment.
	 * 	
	 * @param envVars
	 * @throws ExecutorException
	 */
	public void initSandboxEnvironment(Map<String, String> envVars) throws ExecutorException;
	
	/**
	 * Stop the Virtual Machine Environment.
	 * 
	 * @throws ExecutorException
	 */
	public void shutDownSandBoxEnvironment() throws ExecutorException;
	
	
	/**
	 * 
	 * Stop the prepare allocation process
	 * 
	 * @throws ExecutorException
	 */
	public void stopPrepareAllocation() throws ExecutorException;
	
	/**
	 * Blocks until the command finishes and returns its result
	 * 
	 * @return ExecutorResult StdOut, StdErr and exitValue from kill command.
	 * @throws ExecutorException when there is a problem while obtaining the result of an execution.
	 */
	public ExecutorResult getResult() throws ExecutorException;

	public CommuneLogger getLogger();
	
	/**
	 * 
	 * Checks whether the execution has been completed
	 * 
	 * @return 
	 * @throws ExecutorException
	 */
	public boolean hasExecutionFinished() throws ExecutorException;

	/**
	 * Sets the configuration to be used by this environment.
	 * 
	 * @param executorConfiguration Configuration to be used.
	 */
	public void setConfiguration(ExecutorConfiguration executorConfiguration);

	public void finishExecution() throws ExecutorException;
	
}
