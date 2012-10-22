/**
 * 
 */
package org.ourgrid.common.executor.vmware;

import static org.ourgrid.common.executor.ProcessUtil.buildAndRunProcess;
import static org.ourgrid.common.executor.ProcessUtil.buildAndRunProcessNoWait;
import static org.ourgrid.common.executor.ProcessUtil.waitForProcess;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.executor.FolderBasedSandboxedUnixEnvironmentUtil;
import org.ourgrid.common.executor.OutputCatcher;
import org.ourgrid.common.executor.ProcessUtil;
import org.ourgrid.common.executor.SandBoxEnvironment;
import org.ourgrid.common.executor.config.ExecutorConfiguration;
import org.ourgrid.common.executor.config.VMWareExecutorConfiguration;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;

public class VMWareSandBoxEnvironment implements SandBoxEnvironment {

	// scripts
	private String vmImagePath;

	/*
	 * These file contains the standard and error execution output. Also the
	 * execution exit value
	 */
	private File stdOutput;
	private File errorOutput;
	private File exitValueOutput;

	private ExecutorConfiguration configuration;

	private FolderBasedSandboxedUnixEnvironmentUtil unixFolderUtil = new FolderBasedSandboxedUnixEnvironmentUtil();
	private String host_playpen_path;
	private String host_storage_path;

	// execution environment in Virtual Machine
	private String playpenDirInVm;
	private String storageDirInVM;

	private VMWareCommandFactory commandFactory;
	
	private CommuneLogger logger;
	
	public VMWareSandBoxEnvironment(CommuneLogger logger) {
		this.logger = logger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ourgrid.common.executor.SandBoxEnvironment#setConfiguration(org.ourgrid.common.executor.config.ExecutorConfiguration)
	 */
	public void setConfiguration(ExecutorConfiguration executorConfiguration) {
		
		this.vmImagePath = executorConfiguration
				.getProperty(WorkerConfiguration.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_IMAGE_PATH.toString());

		this.configuration = executorConfiguration;

		this.playpenDirInVm = executorConfiguration
				.getProperty(WorkerConfiguration.PREFIX + VMWareExecutorConfiguration.PROPERTIES.PLAYPEN_DIR_IN_VM.toString());
		this.storageDirInVM = executorConfiguration
				.getProperty(WorkerConfiguration.PREFIX + VMWareExecutorConfiguration.PROPERTIES.STORAGE_DIR_IN_VM.toString());
		this.commandFactory = new VMWareCommandFactory(configuration);
	}

	public Process prepareAllocation() throws ExecutorException {
		// init Virtual Machine
		return buildAndRunProcessNoWait(commandFactory.createInitCommand(), "Could not init VMWare");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ourgrid.common.executor.SandBoxEnvironment#initSandboxEnvironment(java.util.Map)
	 */
	public void initSandboxEnvironment(Map<String, String> envVars)
			throws ExecutorException {
		this.host_playpen_path = envVars.get(WorkerConstants.ENV_PLAYPEN);

		this.stdOutput = new File(
				host_playpen_path,
				configuration
						.getProperty(VMWareExecutorConfiguration.PROPERTIES.APP_STD_OUTPUT_FILE_NAME.toString()));
		this.errorOutput = new File(
				host_playpen_path,
				configuration
						.getProperty(VMWareExecutorConfiguration.PROPERTIES.APP_STD_ERROR_FILE_NAME.toString()));
		this.exitValueOutput = new File(
				host_playpen_path,
				configuration
						.getProperty(VMWareExecutorConfiguration.PROPERTIES.APP_STD_EXITVALUE_FILE_NAME.toString()));

	}

	private void createVMExecutionEnvironment(String tempStorage)throws ExecutorException {
		createDirInGuest(playpenDirInVm);
		createDirInGuest(storageDirInVM);
		createDirInGuest("/tmp/vm/out");
		
//		copy the playpen from host to VM
		buildAndRunProcess(commandFactory.createCopyFromHostCommand(host_playpen_path, 
				playpenDirInVm), "Could not copy files from Host to VM.");
//		copy storage from host to VM
		buildAndRunProcess(commandFactory.createCopyFromHostCommand(tempStorage, 
				storageDirInVM), "Could not copy files from Host to VM.");
		
	}

	private void createDirInGuest(String dirPath) throws ExecutorException {
		buildAndRunProcess(commandFactory.createNewDirCommand(dirPath),
				"Could not create new dir in VM");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ourgrid.common.executor.SandBoxEnvironment#executeRemoteCommand(java.lang.String,
	 *      java.lang.String, java.util.Map)
	 */
	public Process executeRemoteCommand(String dirName, String command,
			Map<String, String> envVars) throws ExecutorException {

		logger.info( "Asked to run remote command " + command);

		if(!isSandBoxUp()){
			IllegalStateException illegalStateException
			= new IllegalStateException("VMware environment is not running. Can not execute commands.");

			throw new ExecutorException( illegalStateException );
		}

		return executeRemoteCommand(command, envVars);
	}

	/**
	 * @throws ExecutorException
	 * @throws IOException
	 */
	private void copyFilesFromVMToHost(String pathOnVM, String pathOnHost) throws ExecutorException, IOException {
		buildAndRunProcess(commandFactory.createCopyFromGuestCommand(pathOnVM, pathOnHost),
				"Could not copy file.");
	}

	private Process executeRemoteCommand(String cmd, Map<String, String> envVars) throws ExecutorException {
		host_storage_path = envVars.get(WorkerConstants.ENV_STORAGE);
				
		createVMExecutionEnvironment(host_storage_path);
		String errorMessage = "Could not execute command.";
		
		//create a execution script with the command on VM
		Process proc = buildAndRunProcessNoWait(commandFactory.creteExecutionScriptCommand(cmd, envVars), errorMessage);
		
		boolean normalTermination = waitForProcess(proc);
			
		if (!normalTermination) {
			throw new ExecutorException(errorMessage);
		}
		
		try {
			//copy out, error and exit files from VM to host
			copyFilesFromVMToHost("/tmp/vm/out", host_playpen_path);
		} catch (IOException e) {
			throw new ExecutorException("Could not copy files from VM to host.");
		}
		
		return proc;
	}

	private boolean isSandBoxUp() throws ExecutorException {
		//execute list-vm.bat script
		Process process = ProcessUtil.buildAndRunProcessNoWait(commandFactory.createVerifyCommand(), 
								"Could not list started VMs");
		//get all started VMs
		ExecutorResult result = getResultFromProcess(process);

		if (result.getExitValue() != 0) {
			logger.error("Unable to execute isSandBoxUp command. Standard Out message: " + 
					result.getStdout() + " Error message: " + result.getStderr());
			
			throw new ExecutorException("Unable to execute isSandBoxUp command: " + result.getStderr());
		} else {
			String stdOut = result.getStdout();
			//verify if this vm is up
			return isSandBoxUp(vmImagePath, stdOut);
		}
	}

	private ExecutorResult getResultFromProcess(Process process) throws ExecutorException {
		ExecutorResult result = new ExecutorResult();
		
		OutputCatcher stdOutput = new OutputCatcher( process.getInputStream() );
		OutputCatcher stdErr = new OutputCatcher( process.getErrorStream() );
		
		try {
			result.setExitValue( process.waitFor() );
			result.setStdout( stdOutput.getResult() );
			result.setStderr( stdErr.getResult() );
		} catch (InterruptedException e) {
			throw new ExecutorException(e.getCause());
		}
		return result;
	}

	private boolean isSandBoxUp(String vm_image_path, String virtualMachines) {
		//remove quotes from vm_image_path string
		vm_image_path = vm_image_path.substring(1, vm_image_path.length() - 1);
		
		return virtualMachines.indexOf(vm_image_path) != -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ourgrid.common.executor.SandBoxEnvironment#getResult()
	 */
	public ExecutorResult getResult() throws ExecutorException {
		logger.debug( "Getting result of execution..." );
		
		ExecutorResult result = new ExecutorResult();

		try {
			unixFolderUtil.catchOutputFromFile(result, stdOutput, errorOutput,
					exitValueOutput);
		} catch (Exception e) {
			throw new ExecutorException("Unable to catch output ", e);
		}

		logger.debug( "Finished getResult. Single Execution released." );
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ourgrid.common.executor.SandBoxEnvironment#hasExecutionFinished()
	 */
	public boolean hasExecutionFinished() throws ExecutorException {
		return exitValueOutput.exists();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ourgrid.common.executor.SandBoxEnvironment#shutDownSandBoxEnvironment()
	 */
	public void shutDownSandBoxEnvironment() throws ExecutorException {
		buildAndRunProcess(commandFactory.createStopCommand(), "Could not stop VM.");
	}

	public void finishExecution() throws ExecutorException {
		try {
			//copy the result execution files from VM to host
			copyFilesFromVMToHost(playpenDirInVm, host_playpen_path);
			copyFilesFromVMToHost(storageDirInVM, host_storage_path);
		} catch (IOException e) {
			throw new ExecutorException("Could not copy files from VM to host.");
		}
	}

	public void stopPrepareAllocation() throws ExecutorException {
		
	}

	public CommuneLogger getLogger() {
		return logger;
	}
	
}
