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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * 
 * 
 * @author Thiago Emmanuel Pereira da Cunha Silva, thiago.manel@gmail.com
 */
public class ProcessUtil {

	private static transient final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessUtil.class );
	
	/**
	 * Builds a <code>Process</code> object and do not wait for the execution termination.
	 * 
	 * @param command The process arguments
	 * @param errorMsg An error message, used in execution error case.
	 * @return
	 * @throws ExecutorException
	 */
	public static Process buildAndRunProcessNoWait(List<String> command, String errorMsg) throws ExecutorException{

		Process commandProcess = null;

		try {
			LOG.debug("Run process command: " + command);
			commandProcess = new ProcessBuilder(command).start();
		} catch (IOException e) {
			throw new ExecutorException(errorMsg, e);
		}

		return commandProcess;
	}
	
	/**
	 * Builds a <code>Process</code> object and wait for the execution termination.
	 * 
	 * @param command The process arguments
	 * @param errorMsg An error message, used in execution error case.
	 * @return Indicates the normal execution termination.
	 * @throws ExecutorException
	 */
	public static boolean buildAndRunProcess(List<String> command, String errorMsg) throws ExecutorException{
		return waitForProcess(buildAndRunProcessNoWait(command, errorMsg));
	}
	
	public static boolean waitForProcess(Process p) throws ExecutorException {
		try {		
			return (p.waitFor() == 0);
		} catch (InterruptedException e) {
			throw new ExecutorException(e);
		}
	}
	
	/**
	 * Parses a <code>String</code> fashioned command in a <code>List</code> of 
	 * arguments. The original command delimiter is blank space.
	 * 
	 * @param command
	 * @return
	 */
	public static List<String> parseCommand(String command) {
		
		List<String> commandList = new LinkedList<String>();
		StringTokenizer tokenizer = new StringTokenizer(command);
		
		while(tokenizer.hasMoreTokens()) {
			commandList.add(tokenizer.nextToken());
		}
		
		return commandList;
	}
}
