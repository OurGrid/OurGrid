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
package org.ourgrid.peer.ui.async.servicesetup;

import org.ourgrid.common.ui.servicesetup.AbstractWin32ServiceSetupStrategy;

/**
 *
 */
public class PeerWin32ServiceSetupStrategy extends AbstractWin32ServiceSetupStrategy {

	private static final String INSTALL_SCRIPT_PATH = SCRIPT_HOME + "InstallPeerService.bat";
	private static final String UNINSTALL_SCRIPT_PATH = SCRIPT_HOME + "UninstallPeerService.bat";
	
	protected String getInstallScriptPath() {
		return INSTALL_SCRIPT_PATH;
	}

	protected String getUninstallScriptPath() {
		return UNINSTALL_SCRIPT_PATH;
	}

}
