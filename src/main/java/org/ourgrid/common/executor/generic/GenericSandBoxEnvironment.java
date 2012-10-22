///*
// * Copyright (C) 2008 Universidade Federal de Campina Grande
// *  
// * This file is part of OurGrid. 
// *
// * OurGrid is free software: you can redistribute it and/or modify it under the
// * terms of the GNU Lesser General Public License as published by the Free 
// * Software Foundation, either version 3 of the License, or (at your option) 
// * any later version. 
// * 
// * This program is distributed in the hope that it will be useful, but WITHOUT 
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
// * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
// * for more details. 
// * 
// * You should have received a copy of the GNU Lesser General Public License 
// * along with this program. If not, see <http://www.gnu.org/licenses/>.
// * 
// */
//package org.ourgrid.common.executor.generic;
//
//import static org.ourgrid.common.executor.ProcessUtil.buildAndRunProcess;
//import static org.ourgrid.common.executor.ProcessUtil.buildAndRunProcessNoWait;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Map;
//
//import org.ourgrid.common.executor.ExecutorException;
//import org.ourgrid.common.executor.ExecutorResult;
//import org.ourgrid.common.executor.FolderBasedSandboxedUnixEnvironmentUtil;
//import org.ourgrid.common.executor.OutputCatcher;
//import org.ourgrid.common.executor.ProcessUtil;
//import org.ourgrid.common.executor.SandBoxEnvironment;
//import org.ourgrid.common.executor.config.AbstractExecutorConfiguration;
//import org.ourgrid.common.executor.config.ExecutorConfiguration;
//import org.ourgrid.common.executor.config.GenericExecutorConfiguration;
//import org.ourgrid.common.util.CommonUtils;
//import org.ourgrid.common.util.StringUtil;
//import org.ourgrid.worker.WorkerConstants;
//
//import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
//
///**
// * This class performs the implementations of the SandboxedEnvironment interface
// * for the generic virtual machine ( for instance, it just can be vbox ).
// */
//public class GenericSandBoxEnvironment implements SandBoxEnvironment {
//
//	/*
//	 * These file contains the standard and error execution output. Also the
//	 * execution exit value
//	 */
//	private File stdOutput;
//	private File errorOutput;
//	private File exitValueOutput;
//
//	private FolderBasedSandboxedUnixEnvironmentUtil unixFolderUtil =
//		new FolderBasedSandboxedUnixEnvironmentUtil();
//	private String rootDir;
//	private String virtualMachineSharedFolderPath;
//	private String virtualMachinePlaypenPath;
//	private String virtualMachineStoragePath;
//	private String flagsPath;
//	private String stdOutputPath;
//	private String stdErrorPath;
//	private String exitValuePath;
//	private String workerPlaypenPath;
//	private String workerStoragePath;
//
//	private ExecutorConfiguration configuration;
//	private String domainName;
//	private GenericCommandFactory commandFactory;
//	private String snapshotName = null;
//	private String snapshotPath = null;
//	
//	
//	private CommuneLogger logger;
//	private final String startedFile = "started"; 
//	
//	public GenericSandBoxEnvironment(CommuneLogger logger) {
//		this.logger = logger;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.ourgrid.common.executor.SandBoxEnvironment#setConfiguration(org.ourgrid.common.executor.config.ExecutorConfiguration)
//	 */
//	public void setConfiguration(ExecutorConfiguration executorConfiguration) {
//		
//		this.configuration = executorConfiguration;
//		this.rootDir = ((AbstractExecutorConfiguration) executorConfiguration).getRootDir().getAbsolutePath();
//		this.rootDir = this.rootDir.substring(0, this.rootDir.length()-2);
//		this.domainName = configuration.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.VM_NAME.toString());
//		this.snapshotName = configuration.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.VM_SNAPSHOT_NAME.toString());
//		this.snapshotPath = ""; //configuration.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.VM_SNAPSHOT_PATH.toString());
//		this.commandFactory = new GenericCommandFactory(configuration);
//		
//	}
//
//	public Process prepareAllocation() throws ExecutorException {
//
//		this.domainName = configuration.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.VM_NAME.toString());
//		this.snapshotName = configuration.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.VM_SNAPSHOT_NAME.toString());
//		this.snapshotPath = ""; //configuration.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.VM_SNAPSHOT_PATH.toString());
//		
//		this.virtualMachineSharedFolderPath = ""; //configuration.getProperty(WorkerConstants.PREFIX + GenericExecutorConfiguration.PROPERTIES.HOST_SHARED_FOLDER_PATH.toString());
//		this.virtualMachinePlaypenPath = "";//virtualMachineSharedFolderPath + File.separator + WorkerConstants.ENV_WORKER_PLAYPEN;
//		this.virtualMachineStoragePath = "";//virtualMachineSharedFolderPath + File.separator + WorkerConstants.ENV_STORAGE;
////		this.stdOutputPath = virtualMachinePlaypenPath + File.separator + WorkerConstants.ENV_STD_OUT;
////		this.stdErrorPath = virtualMachinePlaypenPath + File.separator + WorkerConstants.ENV_STD_ERR;
//		this.flagsPath = "";//virtualMachineSharedFolderPath + File.separator + WorkerConstants.ENV_FLAGS;
//		this.exitValuePath = "";//flagsPath + File.separator + WorkerConstants.ENV_EXIT_VALUE;
//		
//		File virtualMachineSharedFolder = new File(virtualMachineSharedFolderPath);
//		File virtualMachinePlaypen = new File(virtualMachinePlaypenPath);
//		File virtualMachineStorage = new File(virtualMachineStoragePath);
//		File flagsFolder = new File(flagsPath);
//		File snapshotFolder = new File(snapshotPath);
//		
//		stdOutput = new File(stdOutputPath);
//		errorOutput = new File(stdErrorPath);
//		exitValueOutput = new File(exitValuePath);
//		
//		if ( isSandBoxUp() ){
//			// the Virtual Machine needs to shutdown before reverting to previous snapshot
//			buildAndRunProcess(commandFactory.createStopCommand(domainName), "Could not restore Virtual Machine to previous snapshot - Shutdown phase");
//			
//			// restoring Vitual Machine's initial snapshot
//			buildAndRunProcess(commandFactory.createRestoreVMCommand(domainName, snapshotName),
//					"Could not restore Virtual Machine to previous snapshot - Revert phase");
//			
//		}
//		else{
//			snapshotFolder = new File(snapshotPath);
//			snapshotFolder.mkdir();
//			
//			// generating snapshot xml source file
//			File xmlSourceFile = new File(snapshotPath + File.separator + snapshotName+".xml");
//			try{
//				FileWriter xmlWriter = new FileWriter(xmlSourceFile);
//				xmlWriter.write("" +
//						"<domainsnapshot>\n" +
//								"\t<name>"+snapshotName+"</name>\n" +
//								"\t<description>Snapshot before running the required job</description>\n" +
//						"</domainsnapshot>");
//				xmlWriter.close();
//			}
//			catch(IOException errorHappened){
//				throw new ExecutorException("Error creating the XML source file used for creating the virtual machine snapshots.");
//			}
//			
//			// specifying initial Snapshot if possible
//			Process process = buildAndRunProcessNoWait(commandFactory.createInitialSnapshotCommand(domainName, snapshotPath, snapshotName+".xml"), 
//					"Could not create initial snapshot.");
//			ExecutorResult e = getResultFromProcess(process);
//			System.out.println(e);
//	}
//		
//		//Creating the necessary folders
//		virtualMachineSharedFolder.mkdir();
//		virtualMachinePlaypen.mkdir();
//		virtualMachineStorage.mkdir();
//		flagsFolder.mkdir();
//
//		// specifying/adding a Shared Folder
//		Process process = buildAndRunProcessNoWait(commandFactory.createSharedFolderCommand(domainName, virtualMachineSharedFolderPath),
//		"Could not create Shared Folder");
//		
//		ExecutorResult e = getResultFromProcess(process);
//		System.out.println(e);
//		
//		// init Virtual Machine
//		process = buildAndRunProcessNoWait(commandFactory.createInitCommand(domainName, virtualMachineSharedFolderPath, startedFile),
//		"Could not init Virtual Machine");
//		e = getResultFromProcess(process);
//		System.out.println(e);
//		return process;
//	}
//	
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.ourgrid.common.executor.SandBoxEnvironment#initSandboxEnvironment(java.util.Map)
//	 */
//	public void initSandboxEnvironment(Map<String, String> envVars)
//			throws ExecutorException {
//		
////		this.workerPlaypenPath = new File(envVars.get(WorkerConstants.ENV_WORKER_PLAYPEN)).getAbsolutePath();
////		this.workerStoragePath = new File(envVars.get(WorkerConstants.ENV_STORAGE)).getAbsolutePath();
//
//		File virtualMachinePlaypen = new File(virtualMachinePlaypenPath);
//		File virtualMachineStorage = new File(virtualMachineStoragePath);
//		File workerPlaypenFolder = new File(workerPlaypenPath);
//		File workerStorageFolder = new File(workerStoragePath);
//		
//		try{
//			unixFolderUtil.copyStorageFiles(workerStorageFolder, virtualMachineStorage);
//		}
//		catch (IOException exception){
//			throw new ExecutorException("Unable to copy files from storage to virtual machine storage.");
//		}
//		
//		try{
//			unixFolderUtil.copyStorageFiles(workerPlaypenFolder, virtualMachinePlaypen);
//		}
//		catch (IOException exception){
//			throw new ExecutorException("Unable to copy files from playpen to virtual machine playpen.");
//		}
//
//		removeVirtualMachineFlagsAndOutputFiles(); //If still present in the VM shared folder,
//		//or more probably if they were stored in the worker storage folder
//		
//	}
//
//	private void removeVirtualMachineFlagsAndOutputFiles(){
//		
////		File execFile = new File(flagsPath + File.separator + WorkerConstants.ENV_FLAGS_EXEC);
//		File stdOutputFile = new File(stdOutputPath);
//		File errOutputFile = new File(stdErrorPath);
//		File exitValueExecResultFile = new File(exitValuePath);
//		
////		File[] files = { execFile, stdOutputFile, errOutputFile, exitValueExecResultFile };
//		for ( File file : files ){
//			if ( file.exists() ){
//				file.delete();				
//			}
//		}
//
//	}
//	
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.ourgrid.common.executor.SandBoxEnvironment#executeRemoteCommand(java.lang.String,
//	 *      java.lang.String, java.util.Map)
//	 */
//	public Process executeRemoteCommand(String dirName, String command,
//			Map<String, String> envVars) throws ExecutorException {
//
//		logger.info( "Asked to run remote command " + command);
//
//		if (!isSandBoxUp()) {
//			IllegalStateException illegalStateException = new IllegalStateException(
//					"Virtual Machine environment is not running. Can not execute commands.");
//
//			throw new ExecutorException(illegalStateException);
//		}
//
//		return executeRemoteCommand(command, envVars);
//	}
//
//	private Process executeRemoteCommand(String cmd, Map<String, String> envVars) throws ExecutorException {
//
//		File virtualMachinePlaypen = new File(virtualMachinePlaypenPath);
//		File virtualMachineStorage = new File(virtualMachineStoragePath);
//		
//		try{
//			unixFolderUtil.copyStorageFiles(virtualMachineStorage, virtualMachinePlaypen);
//		}
//		catch (IOException exception){
//			throw new ExecutorException("Unable to copy files from virtual machine" +
//					" storage to virtual machine playpen.");
//		}
//		
//		//Defining new environment variables
//		Map<String, String> clone = CommonUtils.createSerializableMap();
//		clone.putAll(envVars);
//		clone.remove(WorkerConstants.ENV_WORKER_PLAYPEN);
//		clone.remove(WorkerConstants.ENV_STORAGE);
//		clone.put(WorkerConstants.ENV_STORAGE, virtualMachineStoragePath);
//		clone.put(WorkerConstants.ENV_WORKER_PLAYPEN, virtualMachinePlaypenPath);
//		
//		try {
//			FileWriter writer = new FileWriter(new File(flagsPath
//					+ File.separator + WorkerConstants.ENV_FLAGS_EXEC));
//			writer.write(StringUtil.replaceVariables(cmd, envVars));
//			writer.close();
//		} catch (IOException e) {
//			throw new ExecutorException(e.getMessage());
//		}
//		return ProcessUtil.buildAndRunProcessNoWait(commandFactory.waitForExecutionToFinish(flagsPath,
//						WorkerConstants.ENV_FLAGS_EXEC, WorkerConstants.ENV_EXIT_VALUE),
//				"Could not monitor virtual machine execution");
//	}
//
//	private boolean isSandBoxUp() throws ExecutorException {
//		//execute list-vm.bat script
//		Process process = ProcessUtil.buildAndRunProcessNoWait(commandFactory.createVerifyCommand(), 
//								"Could not list started VMs");
//		//get all started VMs
//		ExecutorResult result = getResultFromProcess(process);
//
//		if (result.getExitValue() != 0) {
//			logger.error("Unable to execute isSandBoxUp command. Standard Out message: " + 
//					result.getStdout() + " Error message: " + result.getStderr());
//			
//			throw new ExecutorException("Unable to execute isSandBoxUp command: " + result.getStderr());
//		} else {
//			String stdOut = result.getStdout();
//			//verify if this vm is up
//			return isSandBoxUp(domainName, stdOut);
//		}
//	}
//
//	private ExecutorResult getResultFromProcess(Process process) throws ExecutorException {
//		ExecutorResult result = new ExecutorResult();
//
//		OutputCatcher stdOutput = new OutputCatcher(process.getInputStream());
//		OutputCatcher stdErr = new OutputCatcher(process.getErrorStream());
//
//		try {
//			result.setExitValue(process.waitFor());
//			result.setStdout(stdOutput.getResult());
//			result.setStderr(stdErr.getResult());
//		} catch (InterruptedException e) {
//			throw new ExecutorException(e.getCause());
//		}
//		
//		return result;
//	}
//
//	private boolean isSandBoxUp(String vm_image_path, String virtualMachines) {
//		//remove quotes from vm_image_path string
//		vm_image_path = vm_image_path.substring(1, vm_image_path.length() - 1);
//		
//		return virtualMachines.indexOf(vm_image_path) != -1;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.ourgrid.common.executor.SandBoxEnvironment#getResult()
//	 */
//	public ExecutorResult getResult() throws ExecutorException {
//		logger.debug("Getting result of execution...");
//
//		ExecutorResult result = new ExecutorResult();
//
//		try {
//			unixFolderUtil.catchOutputFromFile(result, stdOutput, errorOutput,
//					exitValueOutput);
//		} catch (Exception e) {
//			throw new ExecutorException("Unable to catch output ", e);
//		}
//
//		logger.debug("Finished getResult. Single Execution released.");
//		
//		File virtualMachinePlaypen = new File(virtualMachinePlaypenPath);
//		File virtualMachineStorage = new File(virtualMachineStoragePath);
//		File workerStorageFolder = new File(workerStoragePath);
//		File workerPlaypenFolder = new File(workerPlaypenPath);
//		
//		try{
//			unixFolderUtil.copyStorageFiles(virtualMachineStorage, workerStorageFolder);
//		}
//		catch (IOException exception){
//			throw new ExecutorException("Unable to copy files from virtual machine storage to worker storage.");
//		}
//		
//		try{
//			unixFolderUtil.copyStorageFiles(virtualMachinePlaypen, workerPlaypenFolder);
//		}
//		catch (IOException exception){
//			throw new ExecutorException("Unable to copy files from virtual machine playpen to worker playpen.");
//		}
//		
//		return result;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.ourgrid.common.executor.SandBoxEnvironment#hasExecutionFinished()
//	 */
//	public boolean hasExecutionFinished() throws ExecutorException {
//		return exitValueOutput.exists();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.ourgrid.common.executor.SandBoxEnvironment#shutDownSandBoxEnvironment()
//	 */
//	public void shutDownSandBoxEnvironment() throws ExecutorException {
//
//		finishExecution();
//		if ( isSandBoxUp()) {
//			buildAndRunProcess(commandFactory.createStopCommand(domainName), "Could not stop VM.");
//		}
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see org.ourgrid.common.executor.SandBoxEnvironment#finishExecution()
//	 */
//	public void finishExecution() throws ExecutorException {
//
//		removeVirtualMachineFlagsAndOutputFiles();
//		
//		File sharedFolder = new File(virtualMachineSharedFolderPath);
//		deleteFilesInDir(sharedFolder);
//		sharedFolder.delete();
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see org.ourgrid.common.executor.SandBoxEnvironment#stopPrepareAllocation()
//	 */
//	public void stopPrepareAllocation() throws ExecutorException {
//		
//		removeVirtualMachineFlagsAndOutputFiles();
//		
//		File sharedFolder = new File(virtualMachineSharedFolderPath);
//		deleteFilesInDir(sharedFolder);
//		sharedFolder.delete();
//		
//		if (isSandBoxUp()){
//			buildAndRunProcess(commandFactory.createStopCommand(domainName), "Could not stop VM.");
//		}
//		
//	}
//
//	private boolean deleteFilesInDir(File directory) {
//
//		boolean successful = true;
//		
//		File[] files = directory.listFiles();
//		
//		if (files != null) {
//			for (File file : files) {
//				if (file.isDirectory()) {
//					successful = deleteFilesInDir(file);
//				}
//				successful = successful && file.delete();
//			}
//		}
//		
//		return successful;
//		
//	}
//	
//	public CommuneLogger getLogger() {
//		return logger;
//	}
//	
//}
