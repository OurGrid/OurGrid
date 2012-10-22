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

import org.ourgrid.reqtrace.Req;

/**
 * This enumeration represents the status that a user can assume. These
 * status can be classified in the following way:
 * <ul>
 * <ul>
 * <li><b>NEVER_LOGGED</b>: This user is registered, but has never logged in on the Peer</li>
 * <li><b>OFFLINE</b>: The user has already logged in, but is currently offline</li>
 * <li><b>LOGGED</b>: The user is online</li>
 * <li><b>CONSUMING</b>: The user is online, and is consuming resources</li>
 * </ul>
 * </ul>
 */
@Req({"REQ106","REQ38a"})
public enum UserState {
	NEVER_LOGGED, OFFLINE, LOGGED, CONSUMING
}
