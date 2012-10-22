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

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.ourgrid.common.executor.config.ExecutorConfiguration;

/**
 * This interface defines the contract between platform dependent layer and the
 * upper layers. The idea is to provide a clean interface that is a minimum set
 * of features to perform native operations.
 */
public interface Executor extends Serializable {

	/**
	 * Serial identification of the class. It needs to be changed only if the
	 * class interface is changed.
	 */
	static final long serialVersionUID = 33L;


	/**
	 * Mounts the Virtual Machine environment.
	 * 
	 * @throws ExecutorException if occur some mounting problem.   
	 */
	public void prepareAllocation() throws ExecutorException;
	
	/**
	 * Execute a local command in a specific dirName, with the environment
	 * defined by envVars.
	 * 
	 * @param dirName directory to execute the script
	 * @param command command to be executed
	 * @param envVars environment variables used by this command
	 * @return a handle that identifies this execution
	 * @throws ExecutorException when there is a problem in the execution.
	 */
	public ExecutorHandle execute( String dirName, String command, Map<String, String> envVars ) throws ExecutorException;


	/**
	 * Execute a local command in a specific dirName. This method doesn't set
	 * any environment variables.
	 * 
	 * @param dirName directory to execute the script
	 * @param command command to be executed
	 * @return a handle that identifies this execution
	 * @throws ExecutorException when there is a problem in the execution.
	 */
	public ExecutorHandle execute( String dirName, String command ) throws ExecutorException;


	/**
	 * Changes the permissions for the indicated file. Permissions are described
	 * using the "rwx" Linux convention. The permissions are changed in a way
	 * that further access from this JVM will see the new permissions.
	 * 
	 * @param file the File object
	 * @param perm the new permission in "rwx" format
	 * @exception ExecutorException error changing the permissions
	 * 
	 * @FIXME Verify the necessity of this method.
	 * This method is not implemented by the implementors yet.
	 */
	public void chmod( File file, String perm ) throws ExecutorException;


	/**
	 * Kills command that was issued via an execute method.
	 * 
	 * @param handle the command handle
	 * @throws ExecutorException
	 */
	public void killCommand( ExecutorHandle handle ) throws ExecutorException;
	
	/**
	 * Kills processing for preparing allocation via an execute method.
	 * 
	 * @throws ExecutorException
	 */
	public void killPreparingAllocation() throws ExecutorException;
	
	/**
	 * Blocks until the command finishes and returns its result.
	 * 
	 * @param handle the command handle
	 * @return ExecutorResult StdOut, StdErr and exitValue from kill command.
	 * @throws ExecutorException when there is a problem while obtaining the
	 *         result of an execution.
	 */
	public ExecutorResult getResult( ExecutorHandle handle ) throws ExecutorException;
	
	/**
	 * Sets the configurations that will be used by this executor.
	 * 
	 * @param executorConfiguratrion The configuration to be used.
	 */
	public void setConfiguration(ExecutorConfiguration executorConfiguratrion);

	/**
	 * What was intended for this method is uncertain. The fact is that
	 * this method currently does anything at all, although it is used by other methods.
	 * In other words,there is no current implementation for this method signature.
	 * @param handle the command handle
	 * @throws ExecutorException when there is a problem while finishing the execution of a command.
	 */
	public void finishCommandExecution( ExecutorHandle handle ) throws ExecutorException;

	/**
	 * Finalizes the preparing allocation processing, removing it from the Command Handle Entries map.
	 * @throws ExecutorException when there is a problem while finishing the
	 *         preparing allocation procedure.
	 */
	public void finishPrepareAllocation() throws ExecutorException;

	public void shutdown() throws ExecutorException;
	
}
