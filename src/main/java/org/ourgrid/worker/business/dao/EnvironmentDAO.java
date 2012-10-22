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
package org.ourgrid.worker.business.dao;

import org.ourgrid.reqtrace.Req;

/**
 * Manages environment's data.
 */
@Req("REQ079")
public class EnvironmentDAO {
	
	/**
	 * The worker's playpen directory.
	 */
	private String playpenDir;
	
	/**
	 * The worker's storage directory.
	 */
	private String storageDir;
	
	@Req("REQ079")
	/**
	 * Builds a new Environment DAO. Both the playpen and storage directory are initially <code>null</code>.
	 */
	EnvironmentDAO() {
		playpenDir = null;
		storageDir = null;
	}
	
	@Req("REQ079")
	/**
	 * Returns the worker's playpen directory.
	 * @return the worker's playpen directory. If this worker is not working, is it <code>null</code>.
	 */
	public String getPlaypenDir() {
		return playpenDir;
	}
	
	@Req("REQ079")
	/**
	 * Sets the worker's new playpen directory.
	 * @param playpenDirectory new playpen directory.
	 */
	public void setPlaypenDir(String playpenDirectory) {
		playpenDir = playpenDirectory;
	}
	
	@Req({"REQ079", "REQ082"})
	/**
	 * Returns the worker's storage directory.
	 * @return the worker's storage directory. If this worker is not working, is it <code>null</code>.
	 */
	public String getStorageDir() {
		return storageDir;
	}

	@Req({"REQ079", "REQ082"})
	/**
	 * Sets the worker's new storage directory.
	 * @param storageDirPath new storage directory.
	 */
	public void setStorageDir(String storageDirPath) {
		storageDir = storageDirPath;
	}
	
	@Req({"REQ079", "REQ082"})
	/**
	 * Makes all the environment variables (playpen and storage directories) <code>null</code>.
	 */
	public void resetEnvVariables() {
		setPlaypenDir(null);
		setStorageDir(null);
	}

}
