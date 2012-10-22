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
package org.ourgrid.matchers;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.ourgrid.common.filemanager.FileInfo;

public class FileInfoMatcher implements IArgumentMatcher {

	private String filePath;
	
	private String fileDigest;
	
	public FileInfoMatcher(String filePath, String fileDigest) {
		this.filePath = filePath;
		this.fileDigest = fileDigest;
	}

	public boolean matches(Object arg0) {
		if (!FileInfo.class.isInstance(arg0)){
			return false;
		}
		
		FileInfo fileInfo = (FileInfo) arg0;
		
		if(fileInfo == null) {
			return false;
		}
		
		if(this.filePath == null) {
			if(fileInfo.getFilePath() != null) {
				return false;
			}
		} else {
			if(!this.filePath.equals(fileInfo.getFilePath())) {
				return false;
			}
		}
		
		if(this.fileDigest == null || fileInfo.getFileDigest() == null) {
			return false;
		}
		
		if(!(this.fileDigest.length() == 0) && !this.fileDigest.equals(fileInfo.getFileDigest())) {
			return false;
		}
		
		return true;
	}

	public void appendTo(StringBuffer arg0) {
	}
	
	public static FileInfo eqMatcher(String filePath, String fileDigest) {
		EasyMock.reportMatcher(new FileInfoMatcher(filePath, fileDigest));
		return null;
	}
	
}
