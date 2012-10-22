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
package org.ourgrid.common.filemanager;

import java.io.Serializable;

public class FileInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String fileDigest;
	
	private String filePath;
	
	public FileInfo() {}

	public FileInfo(String filePath, String fileDigest) {
		this.filePath = filePath;
		this.fileDigest = fileDigest;
	}
	
	public String getFileDigest() {
		return this.fileDigest;
	}

	public String getFilePath() {
		return this.filePath;
	}

	@Override
	public boolean equals(Object obj) {
		if (!FileInfo.class.isInstance(obj)) {
			return false;
		}
		
		FileInfo fileInfo = (FileInfo) obj;
		return fileInfo.getFileDigest().equals(this.getFileDigest()) && fileInfo.getFilePath().equals(this.getFilePath());
	}

	public void setFileDigest(String fileDigest) {
		this.fileDigest = fileDigest;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
}
