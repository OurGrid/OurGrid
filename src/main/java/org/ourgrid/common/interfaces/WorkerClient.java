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

import org.ourgrid.common.interfaces.to.MessageHandle;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferReceiver;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferSender;

/**
 * Callback interface that must be implemented by an entity that wants to use a
 * Worker (e.g. the <code>ReplicaExecutor</code> module).
 */
@Remote
public interface WorkerClient extends TransferSender, TransferReceiver {

	/**
	 * Send a message to a worker client according with a handle. This handle contains the message
	 * parameters and has a specific executor.
	 * @param handle The MessageHandle that contains the message parameters.
	 */
	public void sendMessage(MessageHandle handle);

}
