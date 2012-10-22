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
package org.ourgrid.common.exception;


public class FileTransferException extends OurgridException {

	private static final long serialVersionUID = 40L;

	private String filePath;

	private boolean local;


	public FileTransferException( String filePath, Throwable detail, boolean local ) {

		super( "Filepath: " + filePath, detail );
		this.filePath = filePath;
		this.local = local;

	}


	public FileTransferException( String filePath, boolean local ) {

		super( "Filepath: " + filePath );
		this.filePath = filePath;
		this.local = local;
	}


	public FileTransferException( String filePath, String causeMessage, boolean local ) {

		super( "Filepath: " + filePath + ": " + causeMessage );
		this.filePath = filePath;
		this.local = local;
	}


	/**
	 * Returns the exception message.
	 * 
	 * @return The message of this excpetion
	 */
	public String getMessage() {

		if ( local ) {
			return "Unable to transfer LOCAL file " + filePath + " to Grid Machine";
		}

		return "Unable to transfer REMOTE file " + filePath + " from Grid Machine";
	}

	public void setFile( String path ) {

		filePath = path;
	}

}
