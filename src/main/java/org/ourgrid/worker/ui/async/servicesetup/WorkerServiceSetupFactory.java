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
package org.ourgrid.worker.ui.async.servicesetup;

import org.ourgrid.common.ui.servicesetup.ServiceSetupStrategy;

/**
 *
 */
public class WorkerServiceSetupFactory {

	public ServiceSetupStrategy createPeerServiceSetupStrategy () {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("windows")) {
			return new WorkerWin32ServiceSetupStrategy();
		} else if (osName.contains("linux")) {
			return new WorkerUnixServiceSetupStrategy();
		}
		return null;
	}
	
}
