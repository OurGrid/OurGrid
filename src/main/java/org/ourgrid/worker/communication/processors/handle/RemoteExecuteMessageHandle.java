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
package org.ourgrid.worker.communication.processors.handle;

import java.util.Map;

import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.worker.WorkerConstants;


public class RemoteExecuteMessageHandle extends MessageHandle {

	private long requestID;
	private String command;
	private Map<String, String>  environmentVars;

	public RemoteExecuteMessageHandle(long requestID, String command, Map<String, String> environmentVars) {
		super(WorkerConstants.REMOTE_EXECUTE_ACTION_NAME);
		this.requestID = requestID;
		this.command = command;
		this.environmentVars = environmentVars;
	}

	public long getRequestID() {
		return requestID;
	}

	public String getCommand() {
		return command;
	}
	
	public Map<String, String> getEnvironmentVars() {
		return environmentVars;
	}
}
