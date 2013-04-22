package org.ourgrid.common.executor.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.ourgrid.common.executor.Executor;
import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.executor.ExecutorHandle;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.executor.IntegerExecutorHandle;
import org.ourgrid.common.executor.config.ExecutorConfiguration;
import org.ourgrid.common.executor.config.GenericExecutorConfiguration;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.virt.OurVirt;
import org.ourgrid.virt.exception.SnapshotAlreadyExistsException;
import org.ourgrid.virt.model.ExecutionResult;
import org.ourgrid.virt.model.HypervisorType;
import org.ourgrid.virt.model.VirtualMachineStatus;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.utils.RandomNumberUtil;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;

public class GenericExecutor implements Executor {

	private static final Logger LOGGER = Logger.getLogger(GenericExecutor.class);
	
	private static final long serialVersionUID = 34L;
	private String vmName;
	private HypervisorType hypervisorType;
	private String snapshotName;

	private ExecutorConfiguration executorConfiguration;
	private Map<String, String> virtualMachineConfiguration;
	
	private OurVirt ourVirt;
	
	private String virtualMachinePlaypenPath;
	private String virtualMachineStoragePath;
	private String workerPlaypenPath;
	private String workerStoragePath;
	
	private static int nextHandle = 0;
	private ExecutorResult executorResult;

	public GenericExecutor(CommuneLogger logger) {
		this.ourVirt = new OurVirt();
	}

	@Override
	public void setConfiguration(ExecutorConfiguration configuration) {

		LOGGER.debug("Setting up Generic Executor configuration.");
		
		executorConfiguration = configuration;

		vmName = executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_NAME.toString());
		hypervisorType = HypervisorType.valueOf(executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_HYPERVISOR_TYPE.toString()));
		snapshotName = executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_SNAPSHOT_NAME.toString());
		
		LOGGER.debug("Generic Executor virtual machine name: " + vmName + ", Hypervisor: "+hypervisorType);
		
		virtualMachineConfiguration = new HashMap<String, String>();
		virtualMachineConfiguration.put("user", executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_USER.toString()));
		virtualMachineConfiguration.put("password", executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_PASSWORD.toString()));
		virtualMachineConfiguration.put("memory", executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_MEMORY.toString()));
		virtualMachineConfiguration.put("os", executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_OS.toString()));
		virtualMachineConfiguration.put("osversion", executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_OS_VERSION.toString()));
		virtualMachineConfiguration.put("networktype", executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_NETWORK_TYPE.toString()));
		virtualMachineConfiguration.put("networkadaptername", executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_NETWORK_ADAPTER_NAME.toString()));
		virtualMachineConfiguration.put("pae.enabled", executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_PAE_ENABLED.toString()));
		
		LOGGER.debug("Generic Worker Guest OS: " + executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_OS.toString()));
		
		virtualMachineConfiguration.put("disktype", executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_DISK_TYPE.toString()));
		virtualMachineConfiguration.put("diskimagepath", executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_DISK_IMAGE_PATH.toString()));
		
		String timeout = executorConfiguration.getProperty(
				GenericExecutorConfiguration.VM_START_TIMEOUT.toString());
		
		if (timeout != null) {
			virtualMachineConfiguration.put("starttimeout", timeout);
		}
		
		LOGGER.debug("Virtual machine configuration: " + virtualMachineConfiguration);

		workerPlaypenPath = executorConfiguration.
				getProperty(WorkerConstants.PROP_PLAYPEN_ROOT);
		workerStoragePath = executorConfiguration.
				getProperty(WorkerConstants.PROP_STORAGE_DIR);
		
		virtualMachinePlaypenPath = executorConfiguration.
				getProperty(GenericExecutorConfiguration.GUEST_PLAYPEN_PATH.toString());
		virtualMachineStoragePath = executorConfiguration.
				getProperty(GenericExecutorConfiguration.GUEST_STORAGE_PATH.toString());
		
		LOGGER.debug("Generic Worker established a connection to virtual machine." +
				"Storage (HOST): " + workerStoragePath + ", Storage (GUEST): " + virtualMachineStoragePath);
		LOGGER.debug("Generic Worker established a connection to virtual machine." +
				"Playpen (HOST): " + workerPlaypenPath + ", Playpen (GUEST): " + virtualMachinePlaypenPath);
	}

	@Override
	public void prepareAllocation() throws ExecutorException {

		LOGGER.debug("Generic Executor is preparing allocation.");
		
		try{
			ourVirt.register(vmName, virtualMachineConfiguration);
			ourVirt.create(hypervisorType, vmName);
			restart();
		} catch (Exception e) {
			LOGGER.error(e);
			throw new ExecutorException("OurVirt: " + e.getMessage());
		}
	}

	private void restart() throws Exception {
		VirtualMachineStatus status = ourVirt.status(hypervisorType, vmName);
		if (status != VirtualMachineStatus.RUNNING) {

			LOGGER.debug("Virtual machine [ " + vmName + " ]: Status: " + status);
			
			try {
				takeSnapshot();
			} catch(SnapshotAlreadyExistsException snapshotAlreadyExistsException) {
				restoreSnapshot();
			} catch (Exception e) {
				LOGGER.warn(e);
			}
			
			deleteSharedFolder(GenericExecutorConfiguration.PLAYPEN_SHARED_FOLDER);
			createSharedFolder(GenericExecutorConfiguration.PLAYPEN_SHARED_FOLDER, 
					workerPlaypenPath, virtualMachinePlaypenPath);
			
			deleteSharedFolder(GenericExecutorConfiguration.STORAGE_SHARED_FOLDER);
			createSharedFolder(GenericExecutorConfiguration.STORAGE_SHARED_FOLDER,
					workerStoragePath, virtualMachineStoragePath);
			
			start();
			
			mountSharedFolder(GenericExecutorConfiguration.PLAYPEN_SHARED_FOLDER, workerPlaypenPath, 
					virtualMachinePlaypenPath);
			mountSharedFolder(GenericExecutorConfiguration.STORAGE_SHARED_FOLDER, workerStoragePath, 
					virtualMachineStoragePath);
		
		} else {
			stop(status);
			restart();
		}
	}

	private void stop(VirtualMachineStatus status) throws Exception {
		LOGGER.debug("Virtual machine [ " + vmName + " ]: was " + status +". Stopping.");
		ourVirt.stop(hypervisorType, vmName);
	}

	private void start() throws Exception {
		LOGGER.debug("Virtual machine [ " + vmName + " ]: Starting.");
		ourVirt.start(hypervisorType, vmName);
	}

	private void mountSharedFolder(String shareName, String hostPath, String guestPath) throws Exception {
		LOGGER.debug("Virtual machine [ " + vmName + " ]: Mounting shared folder " + shareName + ".\n" +
				"Host Path: " + hostPath + "\nGuest Path: " + guestPath);
		ourVirt.mountSharedFolder(hypervisorType, vmName, shareName, hostPath, guestPath);
	}

	private void restoreSnapshot() throws Exception {
		LOGGER.debug("Virtual machine [ " + vmName + " ]: " +
				"Has snapshot [ " + snapshotName + " ]. Restoring.");
		ourVirt.restoreSnapshot(hypervisorType, vmName, snapshotName);
	}

	private void takeSnapshot() throws Exception {
		LOGGER.debug("Virtual machine [ " + vmName + " ]: " +
				"Taking snapshot [ " + snapshotName + " ].");
		ourVirt.takeSnapshot(hypervisorType, vmName, snapshotName);
	}

	private void createSharedFolder(String shareName, String hostPath, String guestPath) throws Exception {
		LOGGER.debug("Virtual machine [ " + vmName + " ]: Creating shared folder " + shareName+".");
		ourVirt.createSharedFolder(hypervisorType, vmName,	shareName,	
				hostPath, guestPath);
	}

	private void deleteSharedFolder(String shareName) throws Exception {
		LOGGER.debug("Virtual machine [ " + vmName + " ]: Removing shared folder " + shareName+".");
		ourVirt.deleteSharedFolder(hypervisorType, vmName,	shareName);
	}
	
	@Override
	public ExecutorHandle execute(String dirName, String command,
			Map<String, String> envVars) throws ExecutorException {
		
		LOGGER.debug("Virtual machine [ " + vmName + " ]: Executing command \""+command+"\".");
		
		try {
			
			if (virtualMachineConfiguration.get("os").contains("windows")) {
				
				throw new UnsupportedOperationException("This guest OS is not supported.");
			
			} else {
				
				String playpenOnHost = WorkerDAOFactory.getInstance().getEnvironmentDAO().getPlaypenDir();
				String playpenDirName = new File(playpenOnHost).getName();
				
				String storageOnHost = WorkerDAOFactory.getInstance().getEnvironmentDAO().getStorageDir();
				String storageDirName = new File(storageOnHost).getName();
				
				File script = createExecScript(command, envVars, playpenDirName,
						storageDirName);
				String playpenOnGuest = virtualMachinePlaypenPath + '/' + playpenDirName;
				String scriptPath = playpenOnGuest + '/' + script.getName();
				
				String outputRandom = random();
				String outputPrefix =  playpenOnGuest + "/" + outputRandom;
				
				ExecutionResult result = ourVirt.exec(hypervisorType, vmName, 
						"/bin/bash -c \"/bin/bash " + scriptPath + " 2> " + outputPrefix + "-err 1> " + outputPrefix + "-out \"");
				
				this.executorResult = new ExecutorResult(
						result.getReturnValue(), 
						readOutput(playpenOnHost + "/" + outputRandom + "-out"), 
						readOutput(playpenOnHost + "/" + outputRandom + "-err")
						);
				
			}
		} catch (Exception e) {
			LOGGER.error(e);
			throw new ExecutorException("OurVirt: " + e.getMessage());
		}
		
		return new IntegerExecutorHandle(nextHandle++);
		
	}

	private static String readOutput(String file) throws IOException,
			FileNotFoundException {
		try {
			return IOUtils.toString(new FileInputStream(new File(file).getAbsolutePath()));
		} catch (Exception e) {
			LOGGER.warn(e);
			return "";
		}
	}
	
	private static String random() {
		Long randomNumber =  (long) (RandomNumberUtil.getRandom() * Long.MAX_VALUE);
		randomNumber = Long.signum(randomNumber) == -1 ? randomNumber * (-1) : randomNumber;
		
		return randomNumber.toString();
	}

	private File createExecScript(String command, Map<String, String> envVars,
			String playpenPath, String storageDir) throws IOException {
		File script = new File(workerPlaypenPath + "/" + playpenPath + "/" + "exec.sh");
		FileWriter scriptWriter = new FileWriter(script);
		
		String storagePathOnGuest = virtualMachineStoragePath + "/" + storageDir;
		String playpenPathOnGuest = virtualMachinePlaypenPath + "/" + playpenPath;
		
		scriptWriter.append("#!/bin/bash\n");
		scriptWriter.append("[ -e \"" + storagePathOnGuest + "\" ] && [ -n \"`ls -A " + storagePathOnGuest +  "`\" ]" +
				" && cp -r " + storagePathOnGuest + "/* " + playpenPathOnGuest + "\n");
		
		scriptWriter.append("cd " + playpenPathOnGuest + "\n");
		scriptWriter.append(StringUtil.replaceVariables(command, setGuestEnvVariables(envVars, playpenPath, storageDir)));
		
		scriptWriter.close();
		
		return script;
	}

	private Map<String, String> setGuestEnvVariables(Map<String, String> envVars, String playpenPath,
			String storagePath) {
		Map<String, String> clone = CommonUtils.createSerializableMap();
		clone.putAll(envVars);
		clone.remove(WorkerConstants.PROP_PLAYPEN_ROOT);
		clone.remove(WorkerConstants.PROP_STORAGE_DIR);
		clone.put(WorkerConstants.ENV_PLAYPEN, virtualMachinePlaypenPath + "/" + playpenPath);
		clone.put(WorkerConstants.ENV_STORAGE, virtualMachineStoragePath + "/" + storagePath);
		return clone;
	}

	@Override
	public ExecutorHandle execute(String dirName, String command)
			throws ExecutorException {
		return execute(dirName, command, new LinkedHashMap<String, String>());
	}

	@Override
	public void shutdown() throws ExecutorException {
		
		try {
			VirtualMachineStatus status = ourVirt.status(hypervisorType, vmName);
			if (status == VirtualMachineStatus.RUNNING) {
				stop(status);
			}
		} catch (Exception e) {
			LOGGER.error(e);
			throw new ExecutorException(e.getMessage());
		}
	}

	@Override
	public ExecutorResult getResult(ExecutorHandle handle)
			throws ExecutorException {
		LOGGER.debug("Virtual machine [ " + vmName + " ]: Retrieving result of last command executed.");
		return this.executorResult;
	}


	@Override
	public void killPreparingAllocation() throws ExecutorException {
		shutdown();
	}

	@Override
	public void finishCommandExecution(ExecutorHandle handle)
			throws ExecutorException {
	}

	@Override
	public void chmod(File file, String perm) throws ExecutorException {
	}

	@Override
	public void killCommand(ExecutorHandle handle) throws ExecutorException {
	}

	@Override
	public void finishPrepareAllocation() throws ExecutorException {
	}
	
}