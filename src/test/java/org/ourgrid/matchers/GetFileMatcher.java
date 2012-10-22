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
import org.ourgrid.common.FileTransferInfo;
import org.ourgrid.worker.communication.processors.handle.GetFileInfoMessageHandle;
import org.ourgrid.worker.communication.processors.handle.GetFilesMessageHandle;

public class GetFileMatcher  implements IArgumentMatcher {

	String[] fileNames;
	
	public GetFileMatcher(String[] fileNames) {
		this.fileNames = fileNames;
	}
	
	public void appendTo(StringBuffer arg0) {
		
	}

	public boolean matches(Object arg0) {
		if ( !(arg0 instanceof GetFilesMessageHandle) ) {
			return false;
		}
		
		GetFilesMessageHandle handle = (GetFilesMessageHandle) arg0;
		FileTransferInfo[] files = handle.getFiles();
		
		if ((files == null && this.fileNames == null)
				|| (files.length == 0 && this.fileNames.length == 0)
				|| (files.length != this.fileNames.length)) {
			return true;
		}
		
		boolean matches = true;
		for (int i = 0; i < fileNames.length; i++) {
			matches = matches && containsFile(files[i].getFilePath());
		}
		
		return matches;
	}
	
	private boolean containsFile(String fileName) {
		if (this.fileNames != null) {
			for (int i = 0; i < fileNames.length; i++) {
				if (fileNames[i].equals(fileName)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static GetFileInfoMessageHandle eqMatcher(String[] fileName) {
		EasyMock.reportMatcher(new GetFileMatcher(fileName));
		return null;
	}

}
