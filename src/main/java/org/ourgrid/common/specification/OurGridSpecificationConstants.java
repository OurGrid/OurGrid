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
package org.ourgrid.common.specification;


public interface OurGridSpecificationConstants {

	public static final String ATT_SITE = "site";
	
	/** Used on deployment, contains the machine host name */
	public static final String ATT_MACHINE = "machine";
	
	public static final String ATT_COPY_TO = "copyTo";
	
	public static final String ATT_COPY_FROM = "copyFrom";
	
	public static final String ATT_REM_EXEC = "remExec";
	
	public static final String OS_LINUX_XEN = "/proc/xen";
	
	public static final String OS_SOLARIS = "solaris";
	
	public static final String OS_HPUX = "hpux";
	
	public static final String OS_WINDOWS = "windows";
	
	public static final String OS_LINUX = "linux";
	
	public static final String ATT_ENVIRONMENT = "environment";
	
	public static final String ATT_STORAGE_SHARED = "storageshared";
	
	public static final String ATT_DEBUG = "debug";
	
	public static final String ATT_BOGO_MIPS = "bogomips";
	
	public static final String ATT_MEM = "mem";
	
	public static final String ATT_PROCESSOR_FAMILY = "processorfamily";
	
	public static final String ATT_OS = "os";
	
	public static final String ATT_STORAGE_SIZE = "storagesize";
	
	public static final String ATT_STORAGE_DIR = "storagedir";
	
	public static final String ATT_PLAYPEN_SIZE = "playpensize";
	
	public static final String ATT_PLAYPEN_ROOT = "playpenroot";
	
	public static final String ATT_TYPE = "type";
	
	public static final String ATT_USERNAME = "username";
	
	public static final String ATT_SERVERNAME = "servername";
	
	/**
	 * Used on deployment only, contains the user password for this worker. This
	 * attribute is removed by the peer on 'setworkers'
	 */
	public static final String ATT_PASSWORD = "password";
	
	/**
	 * An identification of the worker's provider peer in the form "user@server"
	 */
	public static final String ATT_PROVIDER_PEER = "provider";

	public String USERNAME = "username";

	public String SERVERNAME = "servername";
	

}

