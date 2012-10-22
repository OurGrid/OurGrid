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
import java.util.TreeMap;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;


/**
 * This class defines an abstraction for the operating system dependent part of
 * executing commands. The concrete implementors of this class must provide the
 * correct behavior for executing a command, killing a process and changing the
 * permissions of files.
 */
public abstract class AbstractExecutor implements Executor {

	private static final long serialVersionUID = 33L;
	
	/* The processes which results were not collected yet. */
	private Map< ExecutorHandle, HandleEntry > commandHandleEntries;
	
	/* The current handle of the preparing allocation */
	private ExecutorHandle preparingAllocationHandle;
	
	/* Logger to store log messages */
	private transient final CommuneLogger logger;
	
	/* This is the next handle that can be issued. */
	private int nextHandle = 0;
	
	public AbstractExecutor(CommuneLogger logger) {
		this.commandHandleEntries = new TreeMap< ExecutorHandle, HandleEntry >();
		this.logger = logger;
	}
	
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.Executor#prepareAllocation()
	 */
	public abstract void prepareAllocation() throws ExecutorException;
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.Executor#finishCommandExecution(org.ourgrid.common.executor.ExecutorHandle)
	 */
	public abstract void finishCommandExecution(ExecutorHandle handle) throws ExecutorException; 
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.Executor#finishPrepareAllocation()
	 */
	public void finishPrepareAllocation() {
		ExecutorHandle handle = getPreparingAllocatioHandle();
		
		if (handle != null) {
			removeProcess(handle);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.Executor#killCommand(org.ourgrid.common.executor.ExecutorHandle)
	 */
	public void killCommand(ExecutorHandle handle) throws ExecutorException {
		
		/* Process to be killed */
		Process processToKill;

		/* Get the process from the initiated execution */
		
		if (handle != null) {
			processToKill = getProcess( handle );
	
			/* call the method kill for each SO */
			if( processToKill != null ) {
				processToKill.destroy();
			} else{
				logger.debug( "Command kill for handle " + handle.toString()
						+ " is not necessary because this process is already finished." );
			}
			
			removeProcess(handle);
		}
		
	}

	/**
	 * Provides a synchronized access to the Map containing the
	 * Processes.
	 * 
	 * @param handle An identificator of the the Process in the Map.
	 * @return An instance of Process identified by the Map.
	 */
	protected synchronized Process getProcess(ExecutorHandle handle) {
		
		Process process = null;
		
		HandleEntry handleEntry = commandHandleEntries.get(handle);
		if (handleEntry != null) {
			process = handleEntry.getProcess();
		}
		
		return process;
	}
	
	/**
	 * Removes a process identified by the handle passed as an argument from the Map containing the
	 * Processes.
	 * 
	 * @param handle An identificator of the the Process in the Map.
	 * @return The removed instance of Process.
	 */
	protected synchronized Process removeProcess(ExecutorHandle handle) {
		Process process = null;
		
		HandleEntry handleEntry = commandHandleEntries.remove(handle);
		if (handleEntry != null) {
			process = handleEntry.getProcess();
		}
		
		return process;
	}

	/**
	 * This method manage the handles issued of the each command execution.
	 * 
	 * @return A handle to be used by the client to identify its execution
	 */
	protected synchronized ExecutorHandle getNextHandle( ) {
		IntegerExecutorHandle newHandle = new IntegerExecutorHandle( nextHandle );
		this.nextHandle++;
		return newHandle;
	}
	
	/**
	 * Gets the processes which results were not collected yet.
	 * @return a map containing all processes
	 */
	protected Map<ExecutorHandle, HandleEntry> getHandleEntries() {
		return commandHandleEntries;
	}
	
	/**
	 * Sets the identificator of the current process of the preparing allocation.
	 * @param preparingAllocationHandle the handle of the process
	 */
	public void setPreparingAllocationHandle(ExecutorHandle preparingAllocationHandle) {
		this.preparingAllocationHandle = preparingAllocationHandle;
	}

	/**
	 * Adds a identificator for a new process into the map of processes whose results are yet to be collected.
	 * @param handle the process handle
	 * @param entry an entry containing the handle and the process
	 */
	public void addHandleEntry(ExecutorHandle handle, HandleEntry entry) {
		commandHandleEntries.put(handle, entry);
	}
	
	/**
	 * Adds a identificator of the process for preparing allocation.
	 * @param handle the process handle
	 * @param entry an entry containing the handle and the preparing allocation process
	 */
	public void addPreparingAllocationEntry(ExecutorHandle handle, HandleEntry entry) {
		addHandleEntry(handle, entry);
		setPreparingAllocationHandle(handle);
	}
	
	/**
	 * Returns a logger object used to store logs.
	 * @return the logger object
	 */
	public CommuneLogger getLogger() {
		return logger;
	}

	/**
	 * Returns the current handle of the preparing allocation.
	 * @return the current preparing allocation handle
	 */
	public ExecutorHandle getPreparingAllocatioHandle() {
		return preparingAllocationHandle;
	}

}
