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

import org.ourgrid.common.executor.config.ExecutorConfiguration;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;

/**
 * This class define an abstraction for the operating system dependent part of
 * executing commands. The concrete implementors of this class must provide the
 * correct behavior for executing a command, killing a process and changing the
 * permissions of files.
 */
public abstract class VanillaExecutor extends AbstractExecutor {

	public VanillaExecutor(CommuneLogger logger) {
		super(logger);
	}

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 33L;


	/**
	 * Creates and returns a instance of ExcecutorResult that will contain the
	 * informations about the finished process.
	 * 
	 * @param process The process that is supposed to be finished and the
	 *        streams must be caught and the exit value recovered.
	 * @return An instance of ExecutorResult describing the result of <i>process</i>
	 *         execution.
	 * @throws InterruptedException If the thread that is catching the result is
	 *         interrupted.
	 */
	protected ExecutorResult catchOutput( Process process ) throws InterruptedException {
		/* create an instance of Executor result information class */
		ExecutorResult result = new ExecutorResult();
		
		OutputCatcher stdOutput = new OutputCatcher( process.getInputStream() );
		OutputCatcher stdErr = new OutputCatcher( process.getErrorStream() );
		
		result.setExitValue( process.waitFor() );
		result.setStdout( stdOutput.getResult() );
		result.setStderr( stdErr.getResult() );
		return result;
	}

	/**
	 * This method provide a Thread safe implementation for Map management. The
	 * idea is to protect the cuncurrent modification in the Map.
	 * 
	 * @param handle The handle that identifies the process must be removed from
	 *        Map.
	 */
	protected synchronized void removeFromProcesses( ExecutorHandle handle ) {
		Process p = removeProcess(handle);
		p.destroy();
	}
	
	public void setConfiguration(ExecutorConfiguration executorConfiguratrion) {
		// TODO Auto-generated method stub
	}
	
	public void finishExecution() throws ExecutorException {
		// TODO Auto-generated method stub
	}

}
