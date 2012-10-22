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
package org.ourgrid.common.interfaces.to;

import java.io.Serializable;

import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;

public class ProcessCommand implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public static final String PUT_COMMAND = "put";
	public static final String STORE_COMMAND = "store";
	public static final String GET_COMMAND = "get";
	
	private TransferHandle handle;
	private String destination;
	private String fileName;
	private Long transferBegin;
	private Long transferEnd;
	private Double transferRate;
	private String name;
	private String source;
	
	private long fileSize;
	
	public ProcessCommand(String source, String destination, String fileName, Long transferBegin, 
			Long transferEnd, String name, TransferHandle handle) {
		
		this.destination = destination;
		this.fileName = fileName;
		this.transferBegin = transferBegin;
		this.transferEnd = transferEnd;
		this.name = name;
		this.source = source;
		this.handle = handle;
	}


	public String getName() {
		return name;
	}

	public String getSource() {
		return source;
	}

	public String getDestination() {
		return destination;
	}

	public String getFileName() {
		return fileName;
	}

	public Long getTransferBegin() {
		return transferBegin;
	}


	public Long getTransferEnd() {
		return transferEnd;
	}


	public Double getTransferRate() {
		return transferRate;
	}


	public TransferHandle getHandle() {
		return handle;
	}


	public void setHandle(TransferHandle handle) {
		this.handle = handle;
	}


	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}


	public long getFileSize() {
		return fileSize;
	}


}