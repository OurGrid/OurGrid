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
package org.ourgrid.common.ui.servicesetup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 */
public abstract class AbstractUnixServiceSetupStrategy implements ServiceSetupStrategy {

	protected static final String SCRIPTS_PATH = "servicewrapper" + File.separator + "unix" + File.separator; 
	private final String BOOT_FILE_PATH = "/etc/init.d/" + getServiceName();
	private final String LINK_FILE_PATH = "/etc/rcS.d/S99" + getServiceName();
	private final String SERVICE_HOME_TOKEN = getServiceHomeToken() + "=";
	private final String SERVICE_HOME_FLAG = "#" + SERVICE_HOME_TOKEN;
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.ui.servicesetup.ServiceSetupStrategy#installAsService()
	 */
	public void installAsService() throws ServiceSetupException {
		if (!isRoot()) {
			throw new ServiceSetupException("You must have super user " +
					"access to install Openfire as service.");
		}
		
		copyBootFile();
		setBootFileAsExecutable();
		linkFiles();
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.ui.servicesetup.ServiceSetupStrategy#uninstallService()
	 */
	public void uninstallService() throws ServiceSetupException {
		if (!isRoot()) {
			throw new ServiceSetupException("You must have super user " +
					"access to uninstall Openfire service.");
		}
		removeLinkFile();
		removeBootFile();
	}
	
	private void setBootFileAsExecutable() {
//		new File(BOOT_FILE_PATH).setExecutable(true);
	}
	
	private void linkFiles() {
		runCommand("/bin/sh", "linkFiles.sh", BOOT_FILE_PATH, LINK_FILE_PATH);
	}
	
	private void removeBootFile() {
		new File(BOOT_FILE_PATH).delete();
	}

	private void removeLinkFile() {
		new File(LINK_FILE_PATH).delete();
	}

	private boolean isRoot() {
		return runCommand("/bin/sh", SCRIPTS_PATH + "checkRoot.sh") == 0;
	}
	
	private int runCommand(String... command) {
		Process service = null;
		try {
			service = new ProcessBuilder(command).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			service.waitFor();
		} catch (InterruptedException e) {}
		
		return service.exitValue();
	}
	
	private void copyBootFile() throws ServiceSetupException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(getTemplateFilePath()));
		} catch (FileNotFoundException e) {
			throw new ServiceSetupException(e);
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(BOOT_FILE_PATH));
		} catch (IOException e) {
			throw new ServiceSetupException(e);
		}
		
		while (true) {
			String line = null;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (line == null) {
				break;
			}
			
			if (line.contains(SERVICE_HOME_FLAG)) {
				line = line.replace(SERVICE_HOME_FLAG, SERVICE_HOME_TOKEN + getServiceHome());
			}
			
			try {
				writer.write(line);
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getServiceHome() {
		return new File("").getAbsolutePath();
	}

	protected abstract String getTemplateFilePath();
	
	protected abstract String getServiceName();
	
	protected abstract String getServiceHomeToken();

}
