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
 * Exception raised when a problem occurs during playpen creation. 
 * 
 * @since 4.0
 */
public class UnableToCreatePlaypenException extends OurgridException {
	
	private static final long serialVersionUID = 1L;
	
	private final String playpenPath;

	/**
	 * Constructor
	 * @param playpenPath Playpen path 
	 */
	public UnableToCreatePlaypenException(String playpenPath){
		super("Unable to create playpen: " + playpenPath);
		this.playpenPath = playpenPath;
	}
	
	public String getPlaypenPath() {
		return this.playpenPath;
	}

}
