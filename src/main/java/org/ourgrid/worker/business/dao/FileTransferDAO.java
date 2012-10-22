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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.reqtrace.Req;

@Req("REQ084")
public class FileTransferDAO {
	
	
	private Map<IncomingHandle, String> incomingFiles;
	private Map<OutgoingHandle, String> uploadingFiles;

	
	FileTransferDAO() {
		incomingFiles = CommonUtils.createSerializableMap();
		uploadingFiles = CommonUtils.createSerializableMap();
	}

	
	@Req("REQ080")
	public File getIncomingFile(IncomingHandle handle) {
		return incomingFiles.get(handle) == null ? null : new File(incomingFiles.get(handle));
	}
	
	@Req("REQ080")
	public String removeIncomingFile(IncomingHandle handle) {
		return incomingFiles.remove(handle);
	}

	@Req("REQ080")
	public boolean containsHandle(IncomingHandle handle) {
		return incomingFiles.containsKey(handle);
	}

	@Req("REQ080")
	public boolean containsIncomingFile(String filePath) {
		return incomingFiles.containsValue(filePath);
	}
	
	@Req("REQ080")
	public List<IncomingHandle> getIncomingFileHandles() {
		return new ArrayList<IncomingHandle>(incomingFiles.keySet());
	}

	@Req("REQ080")
	public void addIncomingFile(IncomingHandle handle, String filePath) {
		incomingFiles.put(handle, filePath);
	}

	@Req("REQ081")
	public OutgoingHandle addUploadingFile(OutgoingHandle handle, String solvedFilePath) {
		uploadingFiles.put(handle, solvedFilePath);
		return handle;
	}

	@Req("REQ081")
	public boolean containsUploadingFile(String filePath) {
		return uploadingFiles.containsValue(filePath);
	}
	
	@Req("REQ080")
	public File getUploadingFile(OutgoingHandle handle) {
		return uploadingFiles.get(handle) == null ? null : new File(uploadingFiles.get(handle));
	}
	
	@Req("REQ081")
	public String removeUploadingFile(OutgoingHandle handle) {
		return uploadingFiles.remove(handle);
	}
	
	public boolean hasUploadingFile() {
		return !uploadingFiles.isEmpty();
	}
	
	@Req("REQ081")
	public List<OutgoingHandle> getUploadingFileHandles() {
		return new ArrayList<OutgoingHandle>(uploadingFiles.keySet());
	}

}
