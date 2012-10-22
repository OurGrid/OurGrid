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

import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.MessageHandle;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferReceiver;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferSender;

/**
 * <p>
 * Represents a worker module running in a grid machine. This interface defines
 * the minimal set of operations needed to execute a task. The following
 * operations comprise this minimal set:
 * <ul>
 * <li>Running a command on the machine, that is: set up an user environment
 * (environment variables) and execute the command.
 * <li>Transfer a file to the grid machine, assuring that the file will have
 * execution permissions on the grid machine
 * <li>Transfer a file from the grid machine back to the machine where is the
 * object which initiates the transfer.
 * </ul>
 * <p>
 * This interface contains the methods called by the module that implements the
 * <code>WorkerClient<code> interface, which is the callback interface of 
 * <code>Worker</code>. 
 * <p>
 * When a Worker is delivered to a consumer, a new work session is started. 
 * During this session the Worker is only allowed to work to a specific request. 
 * Every method of this interface must have a parameter representing the 
 * identification of the request associated to current work session. 
 * <p>
 * A call to {@link Worker#startWork(WorkerClient, long, GridProcessHandle)} must be 
 * done prior to calling any other method. Worker implementations will use this
 * method to set up an execution environment, which include tasks such as creating
 * a playpen.
 * <p>
 * It is recommended that for each replica execution, a different 
 * <code>WorkerClient</code> instance is passed, since no replica 
 * handle is included on most callbacks. 
 * <p>
 * The grid machine which runs a worker may the described by a set of attributes
 * embedded in a the <code>WorkerSpec</code> object. The attributes are a set
 * of name/value pairs that represent the machine characteristics. The semantics
 * of the attributes is given by the user. They are used to verify if a grid
 * machine satisfies the requirements of a request. This verification is done by
 * the peer.
 * <p>
 * A worker is uniquely identified in Ourgrid by its </code>EntityID</code>.
 * <p>
 * The file paths passed to the <code>Worker</code> on I/O operations are
 * relative to the storage area or the playpen. It will contain either the
 * $STORAGE or the $PLAYPEN variable. The variable values will have to be
 * translated at the Worker side.
 * <p>
 * This interface is part of the core interfaces of the OurGrid project. They
 * define a general way of getting access to and use machines on a grid,
 * abstracting differences in doing so.
 * 
 * @see WorkerClient
 */
@Remote
public interface Worker extends Shutdownable, TransferSender, TransferReceiver {

	/**
	 * This is the first call that must be done in the Worker before performing
	 * other operations. It will set up the environment for execution. Possible
	 * actions that are done on this method are: (1) creating a playpen and (2)
	 * starting accounting.
	 * 
	 * @param workerClient a reference to the WorkerClient callback interface
	 * @param requestID the request id associated to the current work session
	 * @param replicaHandle a handle representing the replica that will start
	 *        execution. This handle will be used by Worker implementation to
	 *        uniquely identify a replica, possibly on accounting operations.
	 */
	void startWork( WorkerClient workerClient, long requestID, GridProcessHandle replicaHandle );
	
	/**
	 * This method sends a message to the message processor specified on the handle. 
	 * The handle contains the message parameters. 
	 * @param handle The handle that contains the message parameters.
	 */
	public void sendMessage(MessageHandle handle);

}
