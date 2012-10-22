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


/**
 * This class represents an Exception thrown when it is not possible to get a
 * digest representation (hash) from a particular file
 */
public class UnableToDigestFileException extends OurgridException {

	private static final long serialVersionUID = 33L;


	/**
	 * Constructs a new exception that represents a problem during the digest
	 * representation calculation of a given file.
	 * 
	 * @param filePath the file path used during the digest process.
	 */
	public UnableToDigestFileException(String filePath) {
		super("Unable to digest file: " + filePath);
	}

	/**
	 * Constructs a new exception that represents a problem during the digest
	 * representation calculation of a given file. This constructor encapsulates
	 * another throwable class that possibly caused the failure.
	 * 
	 * @param filePath the file path used during the digest process.
	 * @param detail the throwable class possibly caused the failure.
	 */
	public UnableToDigestFileException(String filePath, Throwable detail) {
		super("Unable to digest file: " + filePath, detail);
	}
}
