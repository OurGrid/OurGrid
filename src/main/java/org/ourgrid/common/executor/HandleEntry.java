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

/**
 * This class holds an ExecutorHandle, the Process associated with it and the
 * directory where the process was started.
 */
public class HandleEntry {

	private ExecutorHandle handle;

	private Process process;
	
	private String dirName;


	/**
	 * Creates a HandleEntry
	 * 
	 * @param handle The handle used to identify a running process.
	 * @param process The process.
	 * @param dirName The directory where the process was started.
	 */
	public HandleEntry( ExecutorHandle handle, Process process, String dirName ) {
		this.handle = handle;
		this.process = process;
		this.dirName = dirName;
	}

	/**
	 * Returns the dirName.
	 * 
	 * @return Returns the dirName.
	 */
	public String getDirName( ) {
		return dirName;
	}


	/**
	 * Returns the handle.
	 * 
	 * @return Returns the handle.
	 */
	public ExecutorHandle getHandle( ) {
		return handle;
	}


	/**
	 * Returns the process.
	 * 
	 * @return Returns the process.
	 */
	public Process getProcess( ) {
		return process;
	}
	
}
