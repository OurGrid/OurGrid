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
package org.ourgrid.common.executor.config;

import java.io.File;

import org.ourgrid.worker.WorkerConstants;

public class GenericExecutorConfiguration extends AbstractExecutorConfiguration {
	
	/** CONSTANTS **/
	/**
	 * Name of the Playpen Shared Folder.
	 */
	public static final String PLAYPEN_SHARED_FOLDER = "PLAYPEN";

	/**
	 * Name of the Storage Shared Folder.
	 */
	public static final String STORAGE_SHARED_FOLDER = "STORAGE";
	
	public static final String VM_SNAPSHOT_NAME = WorkerConstants.PREFIX + "snapshot.name";
	public static final String GENERIC_SNAPSHOT_NAME = "cleanState";

	/** PROPERTY FIELDS **/	
	public static final String VM_PREFIX = "vm.";
	
	public static final String APP_STD_OUTPUT_FILE_NAME = WorkerConstants.PREFIX + "stdout.file";
	public static final String APP_STD_ERROR_FILE_NAME = WorkerConstants.PREFIX + "stderr.file";
	public static final String APP_STD_EXIT_VALUE_FILE_NAME = WorkerConstants.PREFIX + "exitvalue.file";
	public static final String GUEST_PLAYPEN_PATH = WorkerConstants.PREFIX + "guest.playpenroot";
	public static final String GUEST_STORAGE_PATH = WorkerConstants.PREFIX + "guest.storagedir";
	public static final String VM_NAME = VM_PREFIX + "name";
	public static final String VM_USER = VM_PREFIX + "user"; 
	public static final String VM_PASSWORD = VM_PREFIX + "password";
	public static final String VM_MEMORY = VM_PREFIX + "memory";
	public static final String VM_OS = VM_PREFIX + "os";
	public static final String VM_OS_VERSION = VM_PREFIX + "os.version";
	public static final String VM_DISK_TYPE = VM_PREFIX + "disk.type";
	public static final String VM_DISK_IMAGE_PATH = VM_PREFIX + "disk.path";
	public static final String VM_HYPERVISOR_TYPE = VM_PREFIX + "hypervisor.type";
	public static final String VM_START_TIMEOUT = VM_PREFIX + "start.timeout";
	public static final String VM_NETWORK_TYPE = VM_PREFIX + "networktype";
	public static final String VM_NETWORK_ADAPTER_NAME = VM_PREFIX + "networkadaptername";
	public static final String VM_PAE_ENABLED = VM_PREFIX + "pae.enabled";
	
	/** DEFAULT PROPERTIES **/
	
	public static final String DEF_PROP_VM_DOMAIN_NAME = "worker_vm_domain";
	
	/**
	 * Name of the <tt>Snapshot</tt> identifier and xml file.
	 */
	
	/**
	 * Path of the <tt>Snapshot</tt> folder.
	 */
	public static final String DEF_VM_SNAPSHOT_PATH = "snapshot";

	public static final String DEF_APP_EXIT_VALUE = "exit.value";

	public static final String DEF_APP_STDERR = "app.sdterr";

	public static final String DEF_APP_STDOUT = "app.stdout";
	
	public static final String DEF_GUEST_PLAYPEN_PATH = "/tmp/playpen";
	public static final String DEF_GUEST_STORAGE_PATH = "/tmp/storage";
	public static final String DEF_VM_USER = "vm-user";
	public static final String DEF_VM_PASSWORD = "vm-password";
	public static final String DEF_VM_MEMORY = "vm-memory";
	public static final String DEF_VM_OS = "vm-os";
	public static final String DEF_VM_OS_VERSION = "vm-os-version";
	public static final String DEF_VM_DISK_TYPE = "vm-disk-type";
	public static final String DEF_VM_DISK_IMAGE_PATH = "vm-disk-image-path";
	public static final String DEF_VM_HYPERVISOR_TYPE = "VBOX";
	public static final String DEF_VM_START_TIMEOUT = "300";
	
	public GenericExecutorConfiguration(File rootDir) {
		super(rootDir, getPropertiesNames());
	}
	
	private static String[] getPropertiesNames(){
		return new String[]{
				
				APP_STD_OUTPUT_FILE_NAME,
				APP_STD_ERROR_FILE_NAME,
				APP_STD_EXIT_VALUE_FILE_NAME,
				VM_SNAPSHOT_NAME,
				VM_NAME,
				WorkerConstants.PROP_PLAYPEN_ROOT,
				GUEST_PLAYPEN_PATH,
				WorkerConstants.PROP_STORAGE_DIR,
				GUEST_STORAGE_PATH,
				VM_USER,
				VM_PASSWORD,
				VM_MEMORY,
				VM_OS,
				VM_OS_VERSION,
				VM_DISK_TYPE,
				VM_DISK_IMAGE_PATH,
				VM_HYPERVISOR_TYPE,
				VM_START_TIMEOUT,
				VM_NETWORK_TYPE,
				VM_NETWORK_ADAPTER_NAME,
				VM_PAE_ENABLED
				};
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.executor.config.AbstractExecutorConfiguration#setDefaultProperties()
	 */
	@Override
	public void setDefaultProperties() {
		
		this.properties.put(APP_STD_OUTPUT_FILE_NAME, DEF_APP_STDOUT);
		this.properties.put(APP_STD_ERROR_FILE_NAME, DEF_APP_STDERR);
		this.properties.put(APP_STD_EXIT_VALUE_FILE_NAME, DEF_APP_EXIT_VALUE);
		this.properties.put(GUEST_PLAYPEN_PATH, DEF_GUEST_PLAYPEN_PATH);
		this.properties.put(GUEST_STORAGE_PATH, DEF_GUEST_STORAGE_PATH);
		this.properties.put(VM_SNAPSHOT_NAME, GENERIC_SNAPSHOT_NAME);
		this.properties.put(VM_HYPERVISOR_TYPE, DEF_VM_HYPERVISOR_TYPE);
		this.properties.put(VM_NAME, DEF_PROP_VM_DOMAIN_NAME);
		this.properties.put(VM_USER, DEF_VM_USER);
		this.properties.put(VM_PASSWORD, DEF_VM_PASSWORD);
		this.properties.put(VM_MEMORY, DEF_VM_MEMORY);
		this.properties.put(VM_OS, DEF_VM_OS);
		this.properties.put(VM_OS_VERSION, DEF_VM_OS_VERSION);
		this.properties.put(VM_DISK_TYPE, DEF_VM_DISK_TYPE);
		this.properties.put(VM_DISK_IMAGE_PATH, DEF_VM_DISK_IMAGE_PATH);
		this.properties.put(VM_START_TIMEOUT, DEF_VM_START_TIMEOUT);
		
	}
	
}
