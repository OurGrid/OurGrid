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
package org.ourgrid.common.executor.vserver;

import static org.ourgrid.common.executor.ProcessUtil.buildAndRunProcess;
import static org.ourgrid.common.executor.ProcessUtil.buildAndRunProcessNoWait;
import static org.ourgrid.common.executor.ProcessUtil.parseCommand;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.executor.FolderBasedSandboxedUnixEnvironmentUtil;
import org.ourgrid.common.executor.SandBoxEnvironment;
import org.ourgrid.common.executor.config.ExecutorConfiguration;
import org.ourgrid.common.executor.config.VServerExecutorConfiguration.PROPERTIES;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;

/**
 * This entity provides an abstraction layer to handle commands execution on the 
 * Linux VServer kernel. The Linux VServer project implements a OS based virtualization approach.
 * 
 * The current OurGrid VServer workflow look like this:
 * 	
 * 1.	The playpen directory is MAPPED in a directory inside the virtual environment.
 * 2.	The ourgrid storage is copied to a directory inside the virtual environment.
 * 3.	The remote execution command held by a script is created on the virtual environment.
 * 4.	A blocking wait is executed, looking up to a termination file.
 * 5.	In the end of execution the virtual storage directory is cleaned.
 *  	
 * A new virtual machine is created, started and destroied in every execution. 
 *  
 * @see http://linux-vserver.org/Welcome_to_Linux-VServer.org
 * 
 * @author Thiago Emmanuel Pereira da Cunha Silva, thiago.manel@gmail.com
 * since 21/07/2007
 */
public class VServerSandBoxedEnvironment implements SandBoxEnvironment {

	private static final long serialVersionUID = -2576479282260443738L;

	/* These file contains the standard and error execution output. Also the execution exit value */
	private File stdOutput;
	private File errorOutput;
	private File exitValue;

	private final FolderBasedSandboxedUnixEnvironmentUtil unixFolderUtil = new FolderBasedSandboxedUnixEnvironmentUtil();

    private File playPenVmRoot;
	private File storageVmRoot;
    private String host_playpen_path;
    private String host_storage_path;
    private String playPenVmDir;
    private String storageVmDir;
    
    private CommuneLogger logger;
    
    private String vmName;

	private List<String> startvmCmd;
	private List<String> stopvmCmd;
	private List<String> verifyStatusCmd;
	private List<String> execCmd;
	private List<String> prepareAllocationCmd;
	private List<String> stopPrepareAllocationCmd;
	
	private String copyCommandScript;
	private String replaceImageCommandScript;
	private String killTarProcScript;
	
	private File ourGridAppFile;
	private ExecutorConfiguration configuration;
	
	public VServerSandBoxedEnvironment(CommuneLogger logger) {
		this.logger = logger;
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.SandBoxEnvironment#setConfiguration(org.ourgrid.common.executor.config.ExecutorConfiguration)
	 */
	public void setConfiguration(ExecutorConfiguration executorConfiguration) {
		
	    this.vmName = executorConfiguration.getProperty(WorkerConstants.PREFIX + PROPERTIES.VM_NAME.toString());
        
	    this.prepareAllocationCmd = 
	    	 parseCommand(executorConfiguration.getProperty(WorkerConstants.PREFIX + PROPERTIES.PREPARE_ALLOCATION_COMMAND.toString()));
	    
		this.startvmCmd = 
            parseCommand(executorConfiguration.getProperty(WorkerConstants.PREFIX + PROPERTIES.START_VM_COMMAND.toString()));
		this.stopvmCmd = 
            parseCommand(executorConfiguration.getProperty(WorkerConstants.PREFIX + PROPERTIES.STOP_VM_COMMAND.toString()));
		this.verifyStatusCmd = 
            parseCommand(executorConfiguration.getProperty(WorkerConstants.PREFIX + PROPERTIES.STATUS_VM_COMMAND.toString()));
		this.execCmd = 
            parseCommand(executorConfiguration.getProperty(WorkerConstants.PREFIX + PROPERTIES.EXEC_COMMAND.toString()));
		this.stopPrepareAllocationCmd =
			parseCommand(executorConfiguration.getProperty(WorkerConstants.PREFIX + PROPERTIES.STOP_PREPARING_ALLOCATION_COMMAND.toString()));
		
		this.copyCommandScript = executorConfiguration.getProperty(WorkerConstants.PREFIX + PROPERTIES.COPY_FILES_COMMAND.toString());
		this.replaceImageCommandScript = executorConfiguration.getProperty(WorkerConstants.PREFIX + PROPERTIES.REPLACE_VM_IMAGE_COMMAND.toString());
		this.killTarProcScript = executorConfiguration.getProperty(WorkerConstants.PREFIX + PROPERTIES.KILL_TAR_PROC_COMMAND.toString());
		
        this.playPenVmRoot = new File(executorConfiguration.getProperty(WorkerConstants.PREFIX + PROPERTIES.VM_PLAYPEN.toString()));
        this.storageVmRoot = new File(executorConfiguration.getProperty(WorkerConstants.PREFIX + PROPERTIES.VM_STORAGE.toString()));
        
        this.host_playpen_path = "";
        this.host_storage_path = "";
        this.storageVmDir = "";
        this.playPenVmDir = "";
        
        this.stdOutput = new File("");
        this.errorOutput = new File("");
        this.exitValue = new File("");
        
        this.configuration = executorConfiguration;
	}
	
	public Process prepareAllocation() throws ExecutorException {
		return buildAndRunProcessNoWait( createBeginAllocationCommand(), "Could not preparing allocation");
	}
	
	private List<String> createBeginAllocationCommand() {
        List<String> beginAlloc = new LinkedList<String>(prepareAllocationCmd);
        beginAlloc.add(vmName);
        beginAlloc.add(replaceImageCommandScript);
        return beginAlloc;
    }
	

	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.SandboxedExecutor#initSandboxEnvironment()
	 */
	public void initSandboxEnvironment(Map<String, String> envVars) throws ExecutorException {

		this.host_playpen_path = envVars.get(WorkerConstants.ENV_PLAYPEN);
		this.host_storage_path = envVars.get(WorkerConstants.ENV_STORAGE);
			
			File host_playpenDir = new File(host_playpen_path);
			File host_storageDir = new File(host_storage_path);
			
			playPenVmDir = playPenVmRoot.getAbsolutePath() + 
			File.separator + host_playpenDir.getName();
			
			storageVmDir = storageVmRoot.getAbsolutePath() +
			File.separator + host_storageDir.getName();
			
			try {
				this.ourGridAppFile = 
					new File(host_playpen_path, configuration.getProperty(WorkerConstants.PREFIX + PROPERTIES.APP_SCRIPT.toString()));
				this.stdOutput = 
					new File(host_playpen_path, configuration.getProperty(WorkerConstants.PREFIX + PROPERTIES.APP_STDOUT_FILE_NAME.toString()));
				this.errorOutput = 
					new File(host_playpen_path, configuration.getProperty(WorkerConstants.PREFIX + PROPERTIES.APP_STDERROR_FILE_NAME.toString()));
				this.exitValue = 
					new File(host_playpen_path, configuration.getProperty(WorkerConstants.PREFIX + PROPERTIES.TERMINATION_FILE_NAME.toString()));
			} catch (NullPointerException e) {
				throw new ExecutorException(e);
			}
	
			buildAndRunProcess(createInitCommand(), "Could not init VServer");
	}
	
	private List<String> createInitCommand() {
        List<String> initCommand = new LinkedList<String>(startvmCmd);
        initCommand.add(vmName);
        initCommand.add(host_playpen_path);
        initCommand.add(host_storage_path);
        initCommand.add(storageVmRoot.getAbsolutePath());
        initCommand.add(copyCommandScript);

        return initCommand;
    }

    /* (non-Javadoc)
	 * @see org.ourgrid.common.executor.SandBoxEnvironment#executeRemoteCommand(java.lang.String, java.lang.String, java.util.Map)
	 */
	public Process executeRemoteCommand(String dirName, String command, Map<String, String> envVars) throws ExecutorException {

		logger.info( "Asked to run remote command " + command);
		
		if(! isSandBoxUp()){
			IllegalStateException illegalStateException 
							= new IllegalStateException("VServer environment is not running. Can not execute commands.");
			
			throw new ExecutorException( illegalStateException );
		}

		Map<String, String> clone = CommonUtils.createSerializableMap();
		clone.putAll(envVars);
        
		clone.remove(WorkerConstants.PROP_PLAYPEN_ROOT);
		clone.remove(WorkerConstants.PROP_STORAGE_DIR);
		clone.put(WorkerConstants.PROP_PLAYPEN_ROOT, playPenVmDir);
		clone.put(WorkerConstants.PROP_STORAGE_DIR, storageVmDir);

		//The VServer executor must run in the playpen directory.
		/* Command execution script. This one runs in the vserver secure environment.*/
		File remoteScript = unixFolderUtil.createScript( command, host_playpen_path, clone );
		try {
			FileUtils.copyFile(remoteScript, ourGridAppFile);
		} catch (IOException e) {
			throw new ExecutorException("Unable to create remote execution script", e);
		}
		
		return executeRemoteCommand(playPenVmDir);
	}

	/**
	 * @param dirName
	 * @throws ExecutorException
	 */
	private Process executeRemoteCommand(String dirName) throws ExecutorException{
		return buildAndRunProcessNoWait(createExecCommand(dirName), "Could not execute command");
	}
	
	/**
	 * @see vserver_exec script
	 * 
	 * @param dirName
	 * @return
	 */
	private List<String> createExecCommand(String dirName) {
        
		List<String> exec = new LinkedList<String>(execCmd);
		exec.add(dirName);
        exec.add(vmName);
        exec.add(playPenVmDir);
        exec.add(storageVmDir);
        exec.add(ourGridAppFile.getName());
        exec.add(stdOutput.getName());
        exec.add(errorOutput.getName());
        exec.add(exitValue.getName());
        
		return exec;
	}
	
	/**
	 * @return
	 * @throws ExecutorException
	 */
	private boolean isSandBoxUp() throws ExecutorException {
		return buildAndRunProcess(createVerifyCommand(), "Could not verify Vserver state: "+vmName);
	}
	
	private List<String> createVerifyCommand() {
        List<String> verify = new LinkedList<String>(verifyStatusCmd);
        verify.add(vmName);
        return verify;
    }

	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.SandBoxEnvironment#shutDownSandBoxEnvironment()
	 */
	public void shutDownSandBoxEnvironment() throws ExecutorException {
		stopVserver(host_playpen_path);
		cleanEnvironment();
	}
	
	public void stopPrepareAllocation() throws ExecutorException {
		buildAndRunProcess( createStopPreparingAllocation(), "Could not stop preparing allocation");
	}
	
	/**
	 * Stop VServer VM and delete the output files staged out from the VM
	 * @param host_playpen 
	 * @throws ExecutorException
	 */
	private void stopVserver(String host_playpen) throws ExecutorException{
		buildAndRunProcess( createStopCommand(), "Could not stop VServer");
	}
	
	private void cleanEnvironment() {
		deleteFile(stdOutput, "Could not delete output file" + stdOutput.getAbsolutePath());
		deleteFile(errorOutput, "Could not delete error output file" + errorOutput.getAbsolutePath());
		deleteFile(exitValue, "Could not delete exit value file" + exitValue.getAbsolutePath());		
	}
	
	private List<String> createStopCommand() {
        List<String> stop = new LinkedList<String>(stopvmCmd);
        stop.add(vmName);
        stop.add(host_playpen_path);
        stop.add(host_storage_path);
        stop.add(storageVmDir);
        stop.add(copyCommandScript);
        
        return stop;
    }
	
	private List<String> createStopPreparingAllocation() {
        List<String> stop = new LinkedList<String>(stopPrepareAllocationCmd);
        stop.add(vmName);
        stop.add(killTarProcScript);
        
        return stop;
    }
	
    /* (non-Javadoc)
	 * @see org.ourgrid.common.executor.SandBoxEnvironment#getResult()
	 */
	public ExecutorResult getResult() throws ExecutorException {

		buildAndRunProcess( createStopCommand(), "Could not stop VServer");
		
		logger.debug( "Getting result of execution..." );
		ExecutorResult result = new ExecutorResult();

		try{
			unixFolderUtil.catchOutputFromFile(result, stdOutput, errorOutput, exitValue);
		} catch (Exception e) {
			throw new ExecutorException("Unable to catch output ", e);
		}

		logger.debug( "Finished getResult. Single Execution released." );
		return result;
	}
	
	private void deleteFile(File file, String erroMsg) {
		if ( file.delete() == false ) {
			logger.error( "Could not delete file: " + erroMsg );
		}
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.SandBoxEnvironment#hasExecutionFinished()
	 */
	public boolean hasExecutionFinished() throws ExecutorException {
		return exitValue.exists();
	}

	public void finishExecution() throws ExecutorException {
		// TODO Auto-generated method stub
	}

	public CommuneLogger getLogger() {
		return logger;
	}
	
}
