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
package org.ourgrid.broker.communication.operations;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ourgrid.broker.response.RemoteExecuteMessageHandleResponseTO;
import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.util.StringUtil;

/**
 * This class implements the operation running a code in a remote machine.
 */
public class RemoteOperation extends AbstractOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The command to be executed in a remote grid machine (Worker).
	 */
	private String command;

	/**
	 * A Map containing all environment variables parsed by the
	 * <code>ReplicaParser</code> instance.
	 */
	private Map<String,String> envVars;


	/**
	 * Constructs a new instance of this type for the given command.
	 * 
	 * @param replicaHandle a handle to the currently executing replica
	 * @param requestID the ID of the request that generated this operation
	 * @param worker worker where the remote operation will be executed.
	 * @param command the command to be executed in a remote grid machine
	 *        (Worker).
	 * @param envVars a Map containing all environment variables parsed by the
	 *        <code>ReplicaParser</code> instance.
	 */
	public RemoteOperation( GridProcessHandle replicaHandle, long requestID, String workerID, String command,
							Map<String,String> envVars) {

		super( replicaHandle, requestID, workerID );
		this.command = command;
		this.envVars = envVars;
		setType( OperationType.REMOTE );
	}


	/**
	 * Runs a command in a remote grid machine (Worker).
	 */
	public void run(List<IResponseTO> responses) throws OperationException {
		
		String[] vars = null;
		if (this.envVars != null) {
			vars = new String[this.envVars.size()];
			int i = 0;
			
			for (Entry<String, String> entry : this.envVars.entrySet()) {
				vars[i] = entry.getKey() + "=" + entry.getValue();
				i++;
			}
		}	

		RemoteExecuteMessageHandleResponseTO to = new RemoteExecuteMessageHandleResponseTO(getRequestID(),
				command, envVars, StringUtil.deploymentIDToAddress(getWorkerID()));
		
		responses.add(to);
	}


	public String getCommand() {

		return command;
	}


	@Override
	public GenericTransferHandle getHandle() {
		return null;
	}
}
