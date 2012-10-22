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
import java.util.LinkedHashMap;
import java.util.Map;

import org.ourgrid.common.executor.config.ExecutorConfiguration;

/**
 * An <code>Executor</code> implementation used for executing applications
 * inside a secure environment.
 * <br>
 * This class is thread safe.
 * @author Thiago Emmanuel Pereira da Cunha Silva, thiago.manel@gmail.com
 */
public class SandboxedExecutor extends AbstractExecutor {
	
	private static final long serialVersionUID = 4L;
	
	private final SandBoxEnvironment sandBoxEnv;
	
	public SandboxedExecutor(SandBoxEnvironment sandBoxEnv){
		super(sandBoxEnv.getLogger());
		this.sandBoxEnv = sandBoxEnv;
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.Executor#execute(java.lang.String, java.lang.String, java.util.Map)
	 */
	public ExecutorHandle execute(String dirName, String command, Map<String, String> envVars) throws ExecutorException {
		
		if ( dirName == null || command == null ) {
			throw new ExecutorException( "Invalid parameters: " + dirName + ", " + command );
		}
		
		getLogger().debug( "Requested to execute command: " + command + ", Dir Name: " + dirName );

		sandBoxEnv.initSandboxEnvironment(envVars);	
		
		Process proc = sandBoxEnv.executeRemoteCommand(dirName, command, envVars); 
		ExecutorHandle nextHandle = getNextHandle();
		addHandleEntry(nextHandle, new HandleEntry(nextHandle, proc, dirName));
		
		return nextHandle;
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.Executor#execute(java.lang.String, java.lang.String)
	 */
	public ExecutorHandle execute(String dirName, String command) throws ExecutorException {
		return execute(dirName, command, new LinkedHashMap<String, String>());
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.Executor#getResult(org.ourgrid.common.executor.ExecutorHandle)
	 */
	public ExecutorResult getResult(ExecutorHandle handle) throws ExecutorException {
			
		getLogger().debug( "Requested to get result: " + " handle: " + handle);
		
		while (!sandBoxEnv.hasExecutionFinished()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				getLogger().debug("Command was killed");
				throw new ExecutorException(e);
			}
		}
			
		return sandBoxEnv.getResult();
		
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.Executor#chmod(java.io.File, java.lang.String)
	 */
	public void chmod(File file, String perm) throws ExecutorException {
		//Not implemented
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.Executor#setConfiguration(org.ourgrid.common.executor.config.ExecutorConfiguration)
	 */
	public void setConfiguration(ExecutorConfiguration executorConfiguratrion) {
		sandBoxEnv.setConfiguration(executorConfiguratrion);
	}

	public void prepareAllocation() throws ExecutorException {
		Process preparingAllocationProc = sandBoxEnv.prepareAllocation();
		
		if (preparingAllocationProc == null) {
			return;
		}
		
		ExecutorHandle nextHandle = getNextHandle();
		addPreparingAllocationEntry(nextHandle, new HandleEntry(nextHandle, preparingAllocationProc, ""));
		
		boolean normalTermination = ProcessUtil.waitForProcess(preparingAllocationProc);
		
		if (!normalTermination) {
			throw new ExecutorException("Could not prepare the environment for execution.");
		}
	}

	public void finishCommandExecution(ExecutorHandle handle) throws ExecutorException {
		removeProcess(handle);
		sandBoxEnv.finishExecution();
	}

	public void killCommand(ExecutorHandle handle) throws ExecutorException {
		if (handle != null) {
			try {
				sandBoxEnv.shutDownSandBoxEnvironment();
			} finally {
				super.killCommand(handle);
			}
		}		
	}

	public void killPreparingAllocation() throws ExecutorException {
		ExecutorHandle handle = getPreparingAllocatioHandle();
		
		if (handle != null) {
			try {
				sandBoxEnv.stopPrepareAllocation();
			} finally {
				super.killCommand(handle);
			}
		}	
	}

	@Override
	public void shutdown() throws ExecutorException {
		sandBoxEnv.shutDownSandBoxEnvironment();
		
	}

}