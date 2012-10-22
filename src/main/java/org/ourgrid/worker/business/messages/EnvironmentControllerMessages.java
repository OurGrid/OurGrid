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
package org.ourgrid.worker.business.messages;

public class EnvironmentControllerMessages {
	
	public static String getUnsuccesfulPlaypenDirDeletionMessage(String playpenDir) {
		return "Error while trying to clean the playpen directory [" + playpenDir + "].";
	}
	
	public static String getNullFilePathMessage() {
		return "File path is null.";
	}
	
	public static String getInvalidVariableFoundMessage() {
		return "Invalid variable found.";
	}

	public static String getFileNotFoundMessage() {
		return "File not found.";
	}
	
	public static String getNotRelativeFilePathMessage(String absolutePath) {
		return "File path is not relative to " + absolutePath + " directory.";
	}

	public static String getUnreadableFileInfoMessage() {
		return "File cannot be read.";
	}

}
