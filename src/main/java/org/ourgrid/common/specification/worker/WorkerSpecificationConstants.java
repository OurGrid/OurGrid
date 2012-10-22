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
package org.ourgrid.common.specification.worker;

import org.ourgrid.common.specification.OurGridSpecificationConstants;

/**
 * Defines the systems info properties names for 
 * the class {@link WorkerSpecification}.
 * 
 */
public interface WorkerSpecificationConstants {

	//JDF Constants
	
	//SDF Constants
//	public String COPY_FROM = "copyFrom";
//	public String COPY_TO = "copyTo";
//	public String REM_EXEC = "remExec";
	
	public String WORKERS = "workers";
	public String SITE_NAME = "siteName";
	public String SITE_ID = "siteID";
	public String SITE_DESCRIPTION = "siteDescription";
	public String SITE_INFO = "siteInfo";
	public String SITE_URL = "siteURL";
	
	public String HOST_NAME = "hostName";
	public String HOST_ID = "hostID";
	public String HOST_ENVIRONMENT = "hostEnvironment";
	
	public String OS = "OS";
	public String OS_VERSION = "OSVersion";
	public String OS_RELEASE = "OSRelease";
	public String OS_VENDOR = "OSVendor";
	public String OS_WORD_LENGTH = "OSWordLength";
	public String OS_DESCRIPTION = "OSDescription";
	public String OS_UP_TIME = "OSUpTime";
	
	public String CPU_VENDOR = "CPUVendor";
	public String CPU_MODEL = "CPUModel";
	public String CPU_VERSION = "CPUVersion";
	public String CPU_CLOCK = "CPUClock";
	public String CPU_PLATFORM = "CPUPlatform";
	public String PHYSICAL_CPUS = "physicalCPUs";
	public String VIRTUAL_CPUS = "virtualCPUs";
	public String CPU_CORES = "CPUCores";
	public String CPU_LOAD = "CPULoad"; 
	public String CPU_IDLE_TIME = "CPUIdleTime";
	public String CPU_USER_TIME = "CPUUserTime";
	public String CPU_SYS_TIME = "CPUSysTime";
	public String CPU_NICE_TIME = "CPUNiceTime";
	public String CPU_WAIT_TIME = "CPUWaitTime";
	public String CPU_USED_TOTAL_TIME = "CPUUsedTotalTime";
	public String CPU_PERC_SYS_ONLY = "CPUPercSysOnly";
	
	public String PREEMPTION_ENABLED = "preemptionEnabled";
	public String INSTRUCTION_SET = "instructionSet";
	public String MACHINE_LOAD = "machineLoad";
	public String VIRTUAL_MACHINE = "virtualMachine";

	public String MAIN_MEMORY = "mainMemory";
	public String FREE_MAIN_MEMORY = "freeMainMemory";
	public String FREE_PERCENT_OF_MAIN_MEMORY = "freePercentMainMemory";
	public String VIRTUAL_MEMORY = "virtualMemory";

	public String SOFTWARE = "software";
	public String SOFTWARE_NAME = "softwareName";
	public String SOFTWARE_VERSIONS = "softwareVersions";
	public String SOFTWARE_INSTALATION_ROOT = "softwareInstallationRoot";

	public String EXPRESSION = "expression";
	
	public String DISK_TOTAL = "DiskTotal";
	public String DISK_AVAIL = "DiskAvailability";
	
	public String FILE_SYSTEM_DIR_NAME = "FileSystemDirName";
	public String FILE_SYSTEM_TYPE = "FileSystemType";
	
	public String SYS_ARCHITECTURE = "SystemArchitecture"; 

	//Attributes types
	public static final String[] integerAttributes = {
		CPU_CORES, PHYSICAL_CPUS, VIRTUAL_CPUS, MAIN_MEMORY, VIRTUAL_MEMORY
    };
	
	public static final String[] longAttributes = {
	    DISK_TOTAL, DISK_AVAIL, FREE_MAIN_MEMORY
	};
	                                          	
	public static final String[] doubleAttributes = {
		CPU_CLOCK, CPU_LOAD, MACHINE_LOAD, CPU_IDLE_TIME, CPU_USER_TIME, CPU_SYS_TIME, CPU_NICE_TIME, CPU_WAIT_TIME, 
		CPU_USED_TOTAL_TIME, CPU_PERC_SYS_ONLY, OS_UP_TIME, FREE_PERCENT_OF_MAIN_MEMORY
	};
	
	public static final String[] booleanAttributes = {
		PREEMPTION_ENABLED, VIRTUAL_MACHINE
	};
	
	public static final String [] stringAttributes = {
		OurGridSpecificationConstants.USERNAME, OurGridSpecificationConstants.SERVERNAME, SITE_NAME, SITE_ID, SITE_INFO, SITE_DESCRIPTION, SITE_URL, HOST_NAME, HOST_ID, HOST_ENVIRONMENT,
		OS, OS_VERSION, OS_RELEASE, CPU_VENDOR, CPU_MODEL, CPU_VERSION, CPU_PLATFORM, INSTRUCTION_SET, SOFTWARE_NAME, SOFTWARE_VERSIONS,
		SOFTWARE_INSTALATION_ROOT,//, COPY_FROM, COPY_TO, REM_EXEC
		FILE_SYSTEM_DIR_NAME, FILE_SYSTEM_TYPE, 
		OS_VENDOR, OS_WORD_LENGTH, OS_DESCRIPTION, SYS_ARCHITECTURE 
	};
}
