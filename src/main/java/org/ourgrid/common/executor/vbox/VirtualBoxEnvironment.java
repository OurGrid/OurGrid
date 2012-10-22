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
package org.ourgrid.common.executor.vbox;

import static org.ourgrid.common.executor.ProcessUtil.buildAndRunProcess;
import static org.ourgrid.common.executor.ProcessUtil.buildAndRunProcessNoWait;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.executor.FolderBasedSandboxedUnixEnvironmentUtil;
import org.ourgrid.common.executor.SandBoxEnvironment;
import org.ourgrid.common.executor.config.ExecutorConfiguration;
import org.ourgrid.common.executor.config.VirtualMachineExecutorConfiguration;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;

/**
 * Executor that performs application executions on a sandboxed environment. The virtual environment
 * is started using predetermined scripts. Currently the only virtual machine supported by this 
 * executor is <i>VirtualBox (http://www.virtualbox.org)</i>.   
 */
public class VirtualBoxEnvironment implements SandBoxEnvironment {
	
	private static final long serialVersionUID = 40L;

	/*
	 * Constants used to build properties file that will be transfered to the virtual machine. The
	 * file has the following syntax.
	 * 
	 * APP_SCRIPT="value"
	 * APP_EXIT="value"
	 * APP_STDOUT="value"
	 * APP_STDERR="value"
	 * TERMINATION_FILE="value" 
	 * STORAGE_NAME="value" 
	 */ 
	private static final String VIRTUAL_ENV_APP_SCRIPT_PROP = "APP_SCRIPT";
	private static final String VIRTUAL_ENV_APP_EXIT_PROP = "APP_EXIT";
	private static final String VIRTUAL_ENV_APP_STDOUT_PROP = "APP_STDOUT";
	private static final String VIRTUAL_ENV_APP_STDERR_PROP = "APP_STDERR";
	private static final String VIRTUAL_ENV_TERMINATION_FILE_PROP = "TERMINATION_FILE";
	private static final String VIRTUAL_ENV_STORAGE_NAME_PROP = "STORAGE_NAME";
	private static final String VIRTUAL_ENV_PROPS_FILES = "OG_OPTS";
	
	/*
	 * Constants that determine default names for output files.
	 */
	private static final String APP_EXIT = "app.exit";
	private static final String APP_STDERR = "app.stderr";
	private static final String APP_STDOUT = "app.stdout";
	private static final String APP_SCRIPT_SH = "app.script.sh";
	private static final String TERMINATED = ".terminated";
	private static final String VBOX_VM_STORAGE = "storage";
	
	/*
	 * Location of the virtual environment properties file. 
	 */
	private File virtualEnvPropertiesFile;
	
	/*
	 * Configured commands.
	 */
	private String startvmCmd;
	private String stopvmCmd;
	private String machineName;
	private String vBoxLocation;
	
	/*
	 * Class parameters to manipulate executions.
	 */
	private File terminationFile;
	private File execFile;
	private File stdOut;
	private File stdErr;
	private File exitStatus;
	private File vmStorage;
	private File playpen;
	private File storage;

	/*
	 * Other
	 */
	private final FolderBasedSandboxedUnixEnvironmentUtil unixFolderUtil;

	private CommuneLogger logger;
	
	private Process execProcess;

	/**
	 * Creates a new <code>VirtualMachineExecutor</code> using the
	 * given <code>Executor</code> to perform tasks.
	 * 
	 * @param osExecutor <code>Executor</code> that will execute scripts.
	 * @param logger 
	 */
	public VirtualBoxEnvironment(CommuneLogger logger) {
		this.unixFolderUtil = new FolderBasedSandboxedUnixEnvironmentUtil();
		this.logger = logger;
	}
	
	public void setConfiguration(ExecutorConfiguration executorConfiguratrion) {
		this.startvmCmd = ("\"" + executorConfiguratrion.getProperty(VirtualMachineExecutorConfiguration.PROPERTY_START_VM_COMMAND) + "\"");
		this.stopvmCmd = ("\"" + executorConfiguratrion.getProperty(VirtualMachineExecutorConfiguration.PROPERTY_STOP_VM_COMMAND) + "\"");
		this.vBoxLocation =("\"" + executorConfiguratrion.getProperty(VirtualMachineExecutorConfiguration.PROPERTY_VBOX_LOCATION) + "\"");
		this.machineName = executorConfiguratrion.getProperty(VirtualMachineExecutorConfiguration.PROPERTY_MACHINE_NAME);
	}
	
	private void initEnvironmentVariables(Map<String, String> envVars) throws ExecutorException {
		String uniquifier = Integer.toString((int)(Math.random() * Integer.MAX_VALUE));
		
		this.playpen = new File(envVars.get(WorkerConstants.ENV_PLAYPEN));
		this.storage = new File(envVars.get(WorkerConstants.ENV_STORAGE));
		this.vmStorage = new File(playpen.getAbsolutePath() + File.separator + uniquifier + VBOX_VM_STORAGE);
		this.terminationFile = new File (playpen.getAbsolutePath() + File.separator + uniquifier + TERMINATED);
		this.execFile = new File (playpen.getAbsolutePath() + File.separator + uniquifier + APP_SCRIPT_SH);
		this.stdOut = new File (playpen.getAbsolutePath() + File.separator + uniquifier + APP_STDOUT);
		this.stdErr = new File (playpen.getAbsolutePath() + File.separator + uniquifier + APP_STDERR);
		this.exitStatus = new File (playpen.getAbsolutePath() + File.separator + uniquifier + APP_EXIT);
		this.virtualEnvPropertiesFile = new File (playpen.getAbsolutePath() + File.separator + VIRTUAL_ENV_PROPS_FILES);
	}

	public Process executeRemoteCommand(String dirName, String command, Map<String, String> envVars) throws ExecutorException {
		try {

//			String env_storage = envVars.get(WorkerConstants.ENV_STORAGE);

//			command = command.replace(env_storage, vmStorage.getName());
			getLogger().info( "Asked to run command " + command);

			//Defining new environment variables
			Map<String, String> clone = CommonUtils.createSerializableMap();
			clone.putAll(envVars);
			clone.remove(WorkerConstants.ENV_PLAYPEN);
			clone.remove(WorkerConstants.ENV_STORAGE);
			clone.put(WorkerConstants.PROP_STORAGE_DIR, vmStorage.getName());

			//Creating application script
			File script = unixFolderUtil.createScript(command, dirName, clone);

			//Writing virtual environment variables
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(virtualEnvPropertiesFile)));
			writer.println(VIRTUAL_ENV_APP_SCRIPT_PROP + "=" + execFile.getName());
			writer.println(VIRTUAL_ENV_APP_EXIT_PROP + "=" + exitStatus.getName());
			writer.println(VIRTUAL_ENV_APP_STDOUT_PROP + "=" + stdOut.getName());
			writer.println(VIRTUAL_ENV_APP_STDERR_PROP + "=" + stdErr.getName());
			writer.println(VIRTUAL_ENV_TERMINATION_FILE_PROP + "=" + terminationFile.getName());
			writer.println(VIRTUAL_ENV_STORAGE_NAME_PROP + "=" + vmStorage.getName());

			try {
				if (writer.checkError()) {
					throw new IOException("Unable to create Virtual environment");
				}
			} finally {
				writer.close();
			}

			//Copying files to needed location
			FileUtils.copyFile(script, execFile);
			unixFolderUtil.copyStorageFiles(storage, vmStorage);
			
		} catch (IOException e) {
			throw new ExecutorException("Unable to create remote execution script", e);
		}

		getLogger().debug("About to start secure environment");

		//Executing command that will initiate virtual environment and execute the process
		Process execProcess = buildAndRunProcessNoWait(createStartVmAndExecuteCmd(), "Could not execute command");
		setExecProcess(execProcess);
		
		getLogger().debug("About to wait for secure environment to exit");
		
		return execProcess;
	}

	private List<String> createStartVmAndExecuteCmd() {
		List<String> cmd = new LinkedList<String>();
		cmd.add(startvmCmd);
        cmd.add(vBoxLocation);
        cmd.add(machineName);
        cmd.add(getPlayPenPath());
        
		return cmd;
	}

	private String getPlayPenPath() {
		return "\"" + playpen.getAbsolutePath() + "\"";
	}

	public ExecutorResult getResult() throws ExecutorException {
		
		if (!isExecutionInProcess()) {
			throw new ExecutorException("No execution is in process, probably it was killed.");
		}
	
		//Acquiring results
		ExecutorResult result = new ExecutorResult();
		try {
			unixFolderUtil.copyStorageFiles(vmStorage, storage);
			unixFolderUtil.catchOutputFromFile(result, stdOut, stdErr, exitStatus);
			
		} catch (Throwable e) {
			throw new ExecutorException("Unable to catch output ", e);
		} finally {
			stopVm();
			cleanup();
		}
			
		return result;
	}
	
	public void killVm() throws ExecutorException {
			
		if (!isExecutionInProcess()) {
			throw new ExecutorException("No execution is in process");
		}
		stopVm();
		cleanup();
	}
	
	private void stopVm() throws ExecutorException {
		
		getLogger().debug("About to kill secure environment");
		buildAndRunProcess(createStopVmCommand(), "Unable to kill virtual environment");
		
		Process execProcess = getExecProcess();
		
		if (execProcess != null) {
			execProcess.destroy();
		}
	}
	
	private List<String> createStopVmCommand() {
		List<String> cmd = new LinkedList<String>();
		cmd.add(stopvmCmd);
        cmd.add(vBoxLocation);
        cmd.add(machineName);
        
        return cmd;
	}

	private void cleanup() {
		this.playpen = null;
		this.vmStorage = null;
		this.terminationFile = null; 
		this.execFile = null;
		this.stdOut = null;
		this.stdErr = null;
		this.exitStatus = null;
	}

	protected boolean isExecutionInProcess() {
		return playpen != null;
	}

	public void chmod(File file, String perm) throws ExecutorException { 
		
	}

	public void finishExecution() throws ExecutorException {
		
	}
	public CommuneLogger getLogger() {
		return logger;
	}

	public boolean hasExecutionFinished() throws ExecutorException {
		return exitStatus.exists();
	}

	public void initSandboxEnvironment(Map<String, String> envVars)
			throws ExecutorException {
		initEnvironmentVariables(envVars);		
	}

	public void shutDownSandBoxEnvironment() throws ExecutorException {
		killVm();
	}

	public void stopPrepareAllocation() throws ExecutorException {
		stopVm();
		cleanup();
	}

	public Process prepareAllocation() throws ExecutorException {
		return null;
	}

	public void setExecProcess(Process execProcess) {
		this.execProcess = execProcess;
	}

	public Process getExecProcess() {
		return execProcess;
	}
	
}
