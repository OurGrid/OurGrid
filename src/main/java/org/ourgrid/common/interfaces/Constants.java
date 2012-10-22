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
package org.ourgrid.common.interfaces;

public interface Constants {

	/**
	 * **************** COMMAND NAMES /** The stop command name
	 */
	static String STOP_CMD_NAME = "stop";

	/**
	 * The start command name
	 */
	static String START_CMD_NAME = "start";

	/**
	 * The status command name
	 */
	static String STATUS_CMD_NAME = "status";

	static String LINE_SEPARATOR = System.getProperty( "line.separator" );
	
	static int DEFAULT_CHUNK_SIZE_KBYTES = 32;
}
