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
import java.util.Map;

import org.ourgrid.common.executor.config.ExecutorConfiguration;
import org.ourgrid.common.executor.config.GatewayExecutorConfiguration;
import org.ourgrid.common.executor.config.GenericExecutorConfiguration;
import org.ourgrid.common.executor.config.VMWareExecutorConfiguration;
import org.ourgrid.common.executor.config.VServerExecutorConfiguration;
import org.ourgrid.common.executor.config.VirtualMachineExecutorConfiguration;
import org.ourgrid.common.executor.gateway.GatewayExecutor;
import org.ourgrid.common.executor.generic.GenericExecutor;
import org.ourgrid.common.executor.vbox.VirtualBoxEnvironment;
import org.ourgrid.common.executor.vmware.VMWareSandBoxEnvironment;
import org.ourgrid.common.executor.vserver.VServerSandBoxedEnvironment;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.common.util.OS;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;

import br.edu.ufcg.lsd.commune.ModuleProperties;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;

/**
 * This class is responsible for obtaining an Executor instance according to the
 * Operating System being used.
 */
public class ExecutorFactory {

	// Enumeration representing the kind of SandboxedExecutor for the machine
	public enum SandboxedExecutorType {SWAN, VSERVER, VBOX, VMWARE, GATEWAY, GENERIC};
	
	private ModuleContext context;
	private final CommuneLogger logger;
	
	/**
	 * @param logger the logger object to keep the information about the operations in the execution
	 */
	public ExecutorFactory(CommuneLogger logger) {
		this(null, logger);
	}
	
	/**
	 * @param context the ModuleContext which will be used to set the properties of the virtual machine
	 * @param logger the logger object to keep the information about the operations in the execution
	 */
	public ExecutorFactory(ModuleContext context, CommuneLogger logger) {
		this.context = context;
		this.logger = logger;
	}
	
	/**
	 * Chooses and returns the kind of executor which will be used for the execution according
	 * to the Executor_Type property contained in the context. The executor can be a Sandboxed
	 * Environment (using virtualization) or a Native Executor (without virtualization).
	 * @return the correct Executor for the execution
	 */
	public Executor buildExecutor() {
		
		if (context == null) {
			logger.warn(WorkerControllerMessages.getBuildExecutorWithoutContextMessage());
			return null;
		}
		
		String sandBoxTypeStr = context.getProperty(WorkerConfiguration.PROP_EXECUTOR_TYPE);
		if (sandBoxTypeStr == null || sandBoxTypeStr.equals("OS")) {
			return buildNewNativeExecutor();
		}
		
		SandboxedExecutorType sandBoxType = SandboxedExecutorType.valueOf(sandBoxTypeStr);
		
		if (sandBoxType != null) {
			return buildNewSandboxedExecutor(sandBoxType);
		} 
		
		return buildNewNativeExecutor();
	}
	
	/**
	 * This is a factory method that returns a Executor depending on the
	 * platform the software is running.
	 * 
	 * @param type The type of the executor.
	 * 
	 * @return A concrete implementation of Executor depending on the OS
	 */
	private Executor buildNewSandboxedExecutor(SandboxedExecutorType type) {
		Executor instance = null;
		ExecutorConfiguration executorConfig = null;
		
		switch (type) {
			case SWAN:
				instance = new SWANExecutor(logger);
				break;
				
			case VSERVER:
				if (!OS.isFamilyUnix()) {
					throw new ExecutorFactoryException("Unable to build new executor: VSERVER executor only works on Linux OS");
				}
				
                executorConfig = new VServerExecutorConfiguration(new File(context.getProperty(ModuleProperties.PROP_CONFDIR)));
                instance = new SandboxedExecutor(new VServerSandBoxedEnvironment(logger));
				break;
			
			case VMWARE:
				
				executorConfig = new VMWareExecutorConfiguration(new File(context.getProperty(ModuleProperties.PROP_CONFDIR)));
				instance = new SandboxedExecutor(new VMWareSandBoxEnvironment(logger));
				break;
				
			case VBOX:
				if (!(OS.isFamilyWin9x() || OS.isFamilyWindows() || OS.isFamilyDOS())) {
					throw new ExecutorFactoryException("Unable to build new executor: VBOX executor only works on Windows OS");
				}
				
				executorConfig = new VirtualMachineExecutorConfiguration(new File(context.getProperty(ModuleProperties.PROP_CONFDIR)));
				instance = new SandboxedExecutor(new VirtualBoxEnvironment(logger));
				break;
				
			case GATEWAY:
				
				executorConfig = new GatewayExecutorConfiguration(new File(context.getProperty(ModuleProperties.PROP_CONFDIR)));
				instance = new GatewayExecutor(logger);
				break;
				
			case GENERIC:
				
				executorConfig = new GenericExecutorConfiguration(new File(context.getProperty(ModuleProperties.PROP_CONFDIR)));
//				instance = new SandboxedExecutor(new GenericSandBoxEnvironment(logger));
				instance = new GenericExecutor(logger);
				break;
				
			default:
				throw new ExecutorFactoryException("Unable to build new executor: Executor " + type + " don't exists");
		}
		
		//Creating Properties
		if (executorConfig != null) {
			loadProperties(executorConfig);
		}
		instance.setConfiguration(executorConfig);
		
		return instance;
	}
	
	// Sets in a new map the properties contained in the ExecutorConfiguration object
	// or in the ModuleContext, prioritizing the ModuleContext properties. The name
	// of the property is set as the key and the property itself as the value in the map.
	private void loadProperties(ExecutorConfiguration executorConfiguration) {
		
		Map<String, String> properties = CommonUtils.createSerializableMap();
		
		if (executorConfiguration != null) {
			
			for(String custom : executorConfiguration.getDefaultPropertiesNames()) {
				
				String executorConfigurationProperty = executorConfiguration.getProperty( custom );
				
				String configurationBasedProperty = context.getProperty( custom );
				properties.put(custom, (configurationBasedProperty != null)	? configurationBasedProperty : executorConfigurationProperty);
			}
		}
		
		executorConfiguration.loadCustomProperty(properties);
	}
	
	/**
	 * Creates a native OS executor, without virtualization.
	 * 
	 * @return Returns the executor.
	 */
	public Executor buildNewNativeExecutor() {
		Executor instance = null;
		
		if (OS.isFamilyUnix()) {
			instance = new LinuxExecutor(logger);
		} else if (OS.isFamilyWin9x() || OS.isFamilyWindows() || OS.isFamilyDOS()) {
			instance = new Win32Executor(logger);
		} else {
			throw new ExecutorFactoryException("Unable to build new executor, OS " + System.getProperty("os.name") + " is unsupported");
		}
		
		return instance;
	}
}
