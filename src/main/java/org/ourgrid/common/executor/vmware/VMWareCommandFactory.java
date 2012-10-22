package org.ourgrid.common.executor.vmware;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.executor.config.ExecutorConfiguration;
import org.ourgrid.common.executor.config.VMWareExecutorConfiguration;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.common.util.OS;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.WorkerConstants;

public class VMWareCommandFactory {
	
	// scripts
	private String vmImagePath;
	private String startvmScript;
	private String execCmdScript;
	private String listvmCmdScript;
	private String stopvmScript;
	private String copyFileFromGuestScript;
	private String copyFileFromHostScript;
	private String createDirScript;
	
	// Host and Guest settings
	private String vmHostAddress;
	private String vmHostUser;
	private String vmHostPasswd;
	private String vmGuestUser;
	private String vmGuestPasswd;
	
	//Virtual Machine Environment
	private String controlDirInVM;
	private String playpenDirInVm;
	private String storageDirInVM;
	private String stdOutPutFileInVM;
	private String errOutPutFileInVM;
	private String exitValueFileInVM;
	
	//
	private String redirectSimbol = "> ";
	private String endOfCommandSimbol = " ; ";
	private String quotes = "\"";
	private String setExecutablePermission = "chmod -R 700 ";
	
	public VMWareCommandFactory(ExecutorConfiguration executorConfiguration){
		init(executorConfiguration);
	}
	
	public  void init(ExecutorConfiguration executorConfiguration){
		this.vmImagePath = executorConfiguration
				.getProperty(WorkerConfiguration.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_IMAGE_PATH.toString());

		this.startvmScript = executorConfiguration
				.getProperty(VMWareExecutorConfiguration.PROPERTIES.START_VM_COMMAND.toString());
		this.listvmCmdScript = executorConfiguration
		.getProperty(VMWareExecutorConfiguration.PROPERTIES.LIST_VM_COMMAND.toString());
		this.execCmdScript = executorConfiguration
				.getProperty(VMWareExecutorConfiguration.PROPERTIES.EXEC_VM_COMMAND.toString());
		this.stopvmScript = executorConfiguration
				.getProperty(VMWareExecutorConfiguration.PROPERTIES.STOP_VM_COMMAND.toString());
		this.copyFileFromGuestScript = executorConfiguration
				.getProperty(VMWareExecutorConfiguration.PROPERTIES.COPY_FROM_GUEST_VM_COMMAND.toString());
		this.copyFileFromHostScript = executorConfiguration
				.getProperty(VMWareExecutorConfiguration.PROPERTIES.COPY_FROM_HOST_VM_COMMAND.toString());
		this.createDirScript = executorConfiguration
				.getProperty(VMWareExecutorConfiguration.PROPERTIES.CREATE_DIR_VM_COMMAND.toString());

		this.vmHostAddress = executorConfiguration
				.getProperty(WorkerConfiguration.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_HOST_ADDRESS.toString());
		this.vmHostUser = executorConfiguration
				.getProperty(WorkerConfiguration.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_HOST_USER.toString());
		this.vmHostPasswd = executorConfiguration
				.getProperty(WorkerConfiguration.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_HOST_PASSWD.toString());
		this.vmGuestUser = executorConfiguration
				.getProperty(WorkerConfiguration.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_GUEST_USER.toString());
		this.vmGuestPasswd = executorConfiguration
				.getProperty(WorkerConfiguration.PREFIX + VMWareExecutorConfiguration.PROPERTIES.VM_GUEST_PASSWD.toString());

		this.controlDirInVM = "/tmp/vm";
		this.playpenDirInVm = executorConfiguration
				.getProperty(WorkerConfiguration.PREFIX + VMWareExecutorConfiguration.PROPERTIES.PLAYPEN_DIR_IN_VM.toString());
		this.storageDirInVM = executorConfiguration
				.getProperty(WorkerConfiguration.PREFIX + VMWareExecutorConfiguration.PROPERTIES.STORAGE_DIR_IN_VM.toString());
		
		stdOutPutFileInVM = controlDirInVM + "/out/" + 
				executorConfiguration .getProperty(VMWareExecutorConfiguration.PROPERTIES.APP_STD_OUTPUT_FILE_NAME.toString());
		errOutPutFileInVM = controlDirInVM + "/out/" + 
				executorConfiguration.getProperty(VMWareExecutorConfiguration.PROPERTIES.APP_STD_ERROR_FILE_NAME.toString());
		exitValueFileInVM = controlDirInVM + "/out/" + 
				executorConfiguration.getProperty(VMWareExecutorConfiguration.PROPERTIES.APP_STD_EXITVALUE_FILE_NAME.toString());
	}

	public List<String> createInitCommand(){
		List<String> initCommand = new LinkedList<String>();
		
		initCommand.add(startvmScript);
		initCommand.add(vmHostAddress);
		initCommand.add(vmHostUser);
		initCommand.add(vmHostPasswd);
		initCommand.add(vmImagePath);
		return initCommand;
	}
	
	public List<String> createNewDirCommand(String dirPath) { 
		List<String> createDir = new LinkedList<String>();
		
		createDir.add(createDirScript);
		fillCommandList(createDir);
		createDir.add(dirPath);
		return createDir;
	}
	
	public List<String> createCopyFromGuestCommand(String FilePathOnVm,
			String FilePathOnHost) {
		List<String> copyFromGuestCommand = new LinkedList<String>();
		
		copyFromGuestCommand.add(copyFileFromGuestScript);
		fillCommandList(copyFromGuestCommand);
		copyFromGuestCommand.add(FilePathOnVm);
		copyFromGuestCommand.add(FilePathOnHost);
		return copyFromGuestCommand;
	}
	
	
	public List<String> creteExecutionScriptCommand(String cmd, Map<String, String> envVars) {
		List<String> execCommand = new LinkedList<String>();
		execCommand.add(execCmdScript);
		
		String runScriptCommand = getCompleteTaskCommand(cmd, envVars);
		
		fillCommandList(execCommand);
		execCommand.add(runScriptCommand);
		execCommand.add("/bin/bash");
		return execCommand;
	}

	private String getCompleteTaskCommand(String command, Map<String, String> envVars) {
		
		Map<String, String> clone = CommonUtils.createSerializableMap();
		clone.putAll(envVars);
        
		clone.remove(WorkerConstants.ENV_PLAYPEN);
		clone.remove(WorkerConstants.ENV_STORAGE);
		clone.put(WorkerConstants.ENV_PLAYPEN, playpenDirInVm);
		clone.put(WorkerConstants.ENV_STORAGE, storageDirInVM);
		
		String exportVariablesCommand = createExportVariablesCommand(clone); 
		
//		command = command.replace("$STORAGE", storageDirInVM);
//		command = command.replace("$PLAYPEN", playpenDirInVm);
		command = command.replace("$", "\\$");
		
		String finalCommand = "";
		if(OS.isFamilyUnix()){
			finalCommand = "echo " + quotes + exportVariablesCommand + 
						   createSettingFilesInDirAsExecutableCommand(storageDirInVM) +
						   createSettingFilesInDirAsExecutableCommand(playpenDirInVm) +
						   "cd" + " " + playpenDirInVm + endOfCommandSimbol + command + quotes + redirectSimbol + 
						   controlDirInVM + "/script" + endOfCommandSimbol + getRunScriptCommand();
		}
		else{
			finalCommand = quotes + "echo " + quotes + quotes + exportVariablesCommand + 
						   createSettingFilesInDirAsExecutableCommand(storageDirInVM) +
						   createSettingFilesInDirAsExecutableCommand(playpenDirInVm) +
						   "cd" + " " + playpenDirInVm + endOfCommandSimbol + command + quotes + quotes + redirectSimbol + 
						   controlDirInVM + "/script" + endOfCommandSimbol + getRunScriptCommand() + quotes;
		}
		return finalCommand;
	}
	
	private String createSettingFilesInDirAsExecutableCommand(String filePath) {
		return setExecutablePermission + filePath + endOfCommandSimbol;
	}

	private String createExportVariablesCommand(Map<String, String> envVars) {
		String command = "";
		String exportCommand = "export ";
		
		if ( envVars != null ) {
			if ( !envVars.isEmpty() ) {
				for (String key : envVars.keySet()) {
					command += key + "=\'" + envVars.get( key ) + "\'" + endOfCommandSimbol
					+ exportCommand + key + endOfCommandSimbol;
				}
			}
		}
		return command;
	}

	private String getRunScriptCommand() {
		return "sh " + controlDirInVM + "/script" + 
			   " 1" + redirectSimbol + stdOutPutFileInVM +
			   " 2" + redirectSimbol + errOutPutFileInVM + endOfCommandSimbol + 
			   "echo $?" + redirectSimbol + exitValueFileInVM;
	}
	
	public List<String> createCopyFromHostCommand(String filePathOnHost, String filePathOnVm) {
		List<String> copyFromHostCommand = new LinkedList<String>();

		copyFromHostCommand.add(copyFileFromHostScript);
		fillCommandList(copyFromHostCommand);
		copyFromHostCommand.add(filePathOnHost);
		copyFromHostCommand.add(filePathOnVm);
		return copyFromHostCommand;
	}
	
	public List<String> createStopCommand() {
		List<String> stopCommand = new LinkedList<String>();

		stopCommand.add(stopvmScript);
		stopCommand.add(vmHostAddress);
		stopCommand.add(vmHostUser);
		stopCommand.add(vmHostPasswd);
		stopCommand.add(vmImagePath);
		return stopCommand;
	}
	
	public List<String> createVerifyCommand() {
		List<String> listCommand = new LinkedList<String>();

		listCommand.add(listvmCmdScript);
		listCommand.add(vmHostAddress);
		listCommand.add(vmHostUser);
		listCommand.add(vmHostPasswd);
		return listCommand;
	}
	
	private void fillCommandList(List<String> commandList) {
		commandList.add(vmHostAddress);
		commandList.add(vmHostUser);
		commandList.add(vmHostPasswd);
		commandList.add(vmGuestUser);
		commandList.add(vmGuestPasswd);
		commandList.add(vmImagePath);
	}
}
