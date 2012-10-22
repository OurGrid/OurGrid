/**
 * 
 */
package org.ourgrid.common.executor.config;

import java.io.File;

import org.ourgrid.common.util.OS;
import org.ourgrid.worker.WorkerConstants;

public class VMWareExecutorConfiguration extends AbstractExecutorConfiguration {
	
	public static enum PROPERTIES{
		START_VM_COMMAND, STOP_VM_COMMAND, 
		LIST_VM_COMMAND, VM_IMAGE_PATH, 
		VM_HOST_ADDRESS, VM_HOST_USER, 
		VM_HOST_PASSWD, APP_STD_OUTPUT_FILE_NAME, 
		APP_STD_ERROR_FILE_NAME, APP_STD_EXITVALUE_FILE_NAME,
		COPY_FROM_GUEST_VM_COMMAND, VM_GUEST_USER, 
		VM_GUEST_PASSWD, EXEC_VM_COMMAND, 
		VMWARE_SCRIPTS_DIR, CREATE_DIR_VM_COMMAND,
		COPY_FROM_HOST_VM_COMMAND, PLAYPEN_DIR_IN_VM, 
		STORAGE_DIR_IN_VM
	}
	
	/**
	 * @param rootDir
	 * @param propNames
	 */
	public VMWareExecutorConfiguration(File rootDir, String[] propNames) {
		super(rootDir, propNames);
	}

	public VMWareExecutorConfiguration(File rootDir) {
		super(rootDir, parseProperties());
	}
	
	private static String[] parseProperties(){
		
		String[] properties = new String[PROPERTIES.values().length] ;
		
		for (int i = 0; i < PROPERTIES.values().length; i++) {
			properties[i] = WorkerConstants.PREFIX + PROPERTIES.values()[i].toString();
		}
		
		return properties;
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.config.AbstractExecutorConfiguration#setDefaultProperties()
	 */
	@Override
	public void setDefaultProperties() {
		
		String startvmCmd = null;
		String stopvmCmd = null;
		String listvmCmd = null;
		String execCmd = null;
		String copyFromGuestCmd = null;
		String copyFromHostCmd = null;
		String createDirCmd = null;
		String vmwareScriptsDir = null;
		
		String path = getRootDir().getAbsolutePath() + File.separator + "vmware_scripts";
		
		path = path.replace("," , "");
		
		String quotes = "\"";
		if (OS.isFamilyWin9x() || OS.isFamilyWindows()) {
			startvmCmd = quotes + path + File.separator + "start-vm.bat" + quotes;
			stopvmCmd = quotes + path + File.separator + "kill-vm.bat" + quotes;
			listvmCmd = quotes + path + File.separator + "list-vm.bat" + quotes;
			execCmd = quotes + path + File.separator + "execcmd-vm.bat" + quotes;
			copyFromGuestCmd = quotes + path + File.separator + "copyFileFromGuestToHost.bat" + quotes;
			copyFromHostCmd = quotes + path + File.separator + "copyFileFromHostToGuest.bat" + quotes;
			createDirCmd = quotes + path + File.separator + "createDirInGuest.bat" + quotes;
			vmwareScriptsDir = path;
		}
		else if(OS.isFamilyUnix()){
			startvmCmd = path + File.separator + "start-vm.sh";
			stopvmCmd =  path + File.separator + "kill-vm.sh";
			listvmCmd =  path + File.separator + "list-vm.sh";
			execCmd =  path + File.separator + "execcmd-vm.sh";
			copyFromGuestCmd =  path + File.separator + "copyFileFromGuestToHost.sh" ;
			copyFromHostCmd =  path + File.separator + "copyFileFromHostToGuest.sh";
			createDirCmd =  path + File.separator + "createDirInGuest.sh";
			vmwareScriptsDir = path + quotes;
		}
		
		this.properties.put(PROPERTIES.START_VM_COMMAND.toString(), startvmCmd);
		this.properties.put(PROPERTIES.STOP_VM_COMMAND.toString(), stopvmCmd);
		this.properties.put(PROPERTIES.LIST_VM_COMMAND.toString(), listvmCmd);
		this.properties.put(PROPERTIES.EXEC_VM_COMMAND.toString(), execCmd);
		this.properties.put(PROPERTIES.COPY_FROM_GUEST_VM_COMMAND.toString(), copyFromGuestCmd);
		this.properties.put(PROPERTIES.COPY_FROM_HOST_VM_COMMAND.toString(), copyFromHostCmd);
		this.properties.put(PROPERTIES.CREATE_DIR_VM_COMMAND.toString(), createDirCmd);
		this.properties.put(PROPERTIES.VMWARE_SCRIPTS_DIR.toString(), vmwareScriptsDir);
	
		this.properties.put(PROPERTIES.APP_STD_OUTPUT_FILE_NAME.toString(), "app.stdout");
		this.properties.put(PROPERTIES.APP_STD_ERROR_FILE_NAME.toString(), "app.sdterror");
		this.properties.put(PROPERTIES.APP_STD_EXITVALUE_FILE_NAME.toString(), "terminate");
	}
	
}
