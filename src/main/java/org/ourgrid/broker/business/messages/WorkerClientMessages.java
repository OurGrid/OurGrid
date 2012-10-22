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
package org.ourgrid.broker.business.messages;

import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.IncomingHandle;

/**
 *
 */
public class WorkerClientMessages {
	
	public static String getWorkerIsReadyMessage(GridProcessHandle handle, GridProcessState state) {
		return "Worker is ready. Handle: " + handle + ", state: " + state;
	}

	public static String getRunningStateInvalidOperation(String operation, String runningState) {
		return "Invalid operation: " + operation + ". The execution is on the state: " + runningState;
	}
	
	public static String getOutgoingTransferCancelledMessage(GenericTransferHandle handle, long amountWritten) {
		return "Outgoing file transfer cancelled: " + handle + ", amount written: " + amountWritten; 
	}
	
	public static String getOutgoingTransferCompletedMessage(String filePath, GridProcessHandle handle) {
		return "File transfer finished: " + filePath + ", replica: " + handle;
	}
	
	public static String getOutgoingTransferFailedMessage(Exception failCause, GenericTransferHandle handle) {
		return "Outgoing transfer failed: " + handle + (failCause == null ? "" : " " + failCause.getMessage());
	}
	
	public static String getIncomingTransferFailedMessage(Exception failCause, IncomingHandle handle) {
		return "Incoming transfer failed: " + handle + failCause == null ? "" : " " + failCause.getMessage();
	}
	
	public static String getPeerSendAWorkerFailureMessage(String workerPublicKey) {
		return "A peer notified a worker [" + workerPublicKey + "] failure, but it is not working to this broker.";
	}
	
	public static String getNotAvaliableWorker(String workerContainerID) {
		return "The worker with container ID " + workerContainerID + " is not avaliable.";
	}

}
