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

import java.util.Map;

public class ExecutionControllerMessages {
	
	public static String getScheduleExecutionMessage(String command, long requestID, Map<String, String> environmentVars,
			String senderPublicKey) {
		return "Command scheduled to execution. Command: " + command + " ; RequestID: " + requestID + " ;" +
				" Environment variables: " + environmentVars + " ; Client public key: [" + senderPublicKey + "].";
	}

	public static String getConcurrentExecutionMethod(long requestID, String command, Map<String, String> envVars) {
		return "A client is trying to EXECUTE more than one command simultaneously. " +
		"RequestID: " + requestID + " ; command: " + command  + " ; Environment variables: " + envVars;
	}
	
	public static String getPrepareAllocationActionStartedMessage() {
		return "Worker begin allocation action, preparing to start the working.";
	}
	
	public static String getPrepareAllocationActionCancelledMessage() {
		return "Allocation action was cancelled.";
	}
	
	public static String getPrepareAllocationActionIsCompletedMessage() {
		return "Allocation action was completed.";
	}

	public static String getPrepareAllocationErrorMessage(String message) {
		return "A error ocurred while worker was creating the execution environment. Error: " + message;
	}
	
	public static String getNotInPrepareAllocationStateMessage(String state) {
		return "The worker was not in the Preparing Allocation state. It was in the state: " + state;
	}
	
	public static String getWorkerIsNotExecutingMessage() {
		return "Can't get the execution result: the worker is not executing anything.";
	}
	
	public static String getExecutionIsNotRunningMessage() {
		return "The Execution is not running: the worker is not in execute state.";
	}
	
	public static String getCanNotOcurredExecutionErrorMessage() {
		return "Can not ocurred any execution error: the worker is not in execute or executing state.";
	}
	
	public static String getShutdowningExecutorMessage() {
		return "Shutdowning Executor";
	}

}
