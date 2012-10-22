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

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.reqtrace.Req;

public class ControlMessages {
	
	public static String getSuccessfullyStartedWorkerMessage() {
		return "Worker has been successfully started.";
	}
	
	public static String getSuccessfullyShutdownWorkerMessage() {
		return "Worker has been successfully shutdown.";
	}
	
	public static String getUnknownEntityTryingToStartWorkerMessage(String senderPublicKey) {
		return "An unknown entity tried to start the Worker. Only the local modules can perform this operation." +
				" Unknown entity public key: [" + senderPublicKey + "].";
	}

	public static String getUnknownEntityTryingToStopWorkerMessage(String senderPublicKey) {
		return "An unknown entity tried to stop the Worker. Only the local modules can perform this operation." +
				" Unknown entity public key: [" + senderPublicKey + "].";
	}
	
	@Req("REQ087")
	public static String getUnknownEntityTryingToPauseWorkerMessage(String senderPublicKey){
		return "An unknown entity tried to pause the Worker. Only the local modules can perform this operation." +
				" Unknown entity public key: [" + senderPublicKey + "].";
	}
	
	@Req("REQ087")
	public static String getWorkerPausedMessage() {
		return "Worker has been PAUSED.";
	}
	
	public static String getOwnerWorkerResumedMessage() {
		return "Worker has been RESUMED.";
	}
	
	public static String getWorkerStatusChangedMessage(WorkerStatus beforeStatus, WorkerStatus afterStatus) {
		return "Status changed from " + beforeStatus + " to " + afterStatus + ".";
	}
	
	public static String getUnknownEntityTryingToResumeWorkerMessage(String senderPublicKey){
		return "An unknown entity tried to resume the Worker. Only the local modules can perform this operation." +
		" Unknown entity public key: [" + senderPublicKey + "].";
	}

}
