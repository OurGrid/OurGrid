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

import java.io.File;
import java.io.IOException;

public abstract class AbstractWin32ServiceSetupStrategy implements ServiceSetupStrategy {

	protected static final String SCRIPT_HOME = "deployer" + File.separator + "servicewrapper" + File.separator + "win32" + File.separator;
	
	public void installAsService() throws ServiceSetupException {
		Process process = null;
		try {
			process = new ProcessBuilder(getInstallScriptPath()).start();
		} catch (IOException e1) {
			throw new ServiceSetupException(e1);
		}
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			throw new ServiceSetupException(e);
		}
		if (process.exitValue() != 0) {
			throw new ServiceSetupException("Service setup completed with errors.");
		}

	}

	public void uninstallService() throws ServiceSetupException {
		Process process = null;
		try {
			process = new ProcessBuilder(getUninstallScriptPath()).start();
		} catch (IOException e1) {
			throw new ServiceSetupException(e1);
		}
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			throw new ServiceSetupException(e);
		}
		if (process.exitValue() != 0) {
			throw new ServiceSetupException("Service removal completed with errors.");
		}

	}
	
	protected abstract String getInstallScriptPath();
	
	protected abstract String getUninstallScriptPath();

}
