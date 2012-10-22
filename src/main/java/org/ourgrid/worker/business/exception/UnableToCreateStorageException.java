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
package org.ourgrid.worker.business.exception;

import org.ourgrid.common.exception.OurgridException;

/**
 * Exception raised when a problem occurs during storage creation.
 * 
 * @since 4.0
 */
public class UnableToCreateStorageException extends OurgridException {

	private static final long serialVersionUID = 1L;
	
	private final String storagePath;

	public UnableToCreateStorageException( String storagePath ) {
		super( "Unable to create storage directory: " + storagePath );
		this.storagePath = storagePath;
	}
	
	public String getStoragePath() {
		return this.storagePath;
	}
	
}
