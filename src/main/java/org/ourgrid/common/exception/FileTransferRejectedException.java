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

public class FileTransferRejectedException extends OurgridException {

	private static final long serialVersionUID = 1L;


	public FileTransferRejectedException() {

		super( "File transfer was rejected" );
	}


	public FileTransferRejectedException( String message, Throwable newDetail ) {

		super( message, newDetail );
	}


	public FileTransferRejectedException( String message ) {

		super( message );
	}


	public FileTransferRejectedException( Throwable newDetail ) {

		super( newDetail );
	}

}
