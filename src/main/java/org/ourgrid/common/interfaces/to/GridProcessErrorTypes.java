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


/**
 * Enumeration that keeps the possible error types that could be returned by an
 * execution request.
 * 
 * @see org.ourgrid.worker.server.WorkerImpl
 * @since 4.0
 */
public enum GridProcessErrorTypes {

	/**
	 * Errors that the application is responsible. For instance, when the
	 * application tries to get an inexistent remote file file.
	 */
	APPLICATION_ERROR,

	/**
	 * Any error that occurs during a remote execution.
	 */
	EXECUTION_ERROR,

	/**
	 * An IO_ERROR is an error captured via an <code>IOException</code>. In
	 * <b>OurGrid</b> implementation, the code must catch all
	 * <code>IOException</code> subclasses before catch it. The rationale
	 * behind this procedure is try to map more precisely the
	 * <code>IOException</code>s in <code>ExecutionErrorType</code>s. For
	 * instance, lets suppose that a client needs to get a file and pass as
	 * parameter an invalid remote path. When the <code>Worker</code> is
	 * trying to open the file to get the content, an
	 * <code>FileNotFoundException</code> will be raised.
	 * <code>FileNotFoundException</code> is an <code>IOException</code>.
	 * Observe that it is an application fault. So, an
	 * <code>APPLICATION_ERROR</code> must be used as type instead of an
	 * <code>IO_ERROR</code>.
	 */
	IO_ERROR,

	/**
	 * Used when the client is trying to invoke a method using an invalid
	 * request.
	 */
	FILE_TRANSFER_ERROR,

	/**
	 * Used when the client is trying to invoke a method using an invalid
	 * request.
	 */
	INVALID_SESSION,

	/**
	 * Used when a machine has failed
	 */
	MACHINE_FAILURE,

	/**
	 * Used when the client is trying to invoke a method and the Idleness
	 * Detector detects that user is using the machine.
	 */
	MACHINE_NOT_IDLE,

	/**
	 * Used when an user (that has the grant to use this worker) is trying to do
	 * an execution and the worker is doing another execution (from the same
	 * client).
	 */
	CONCURRENT_RUNNING,

	BROKER_ERROR,

	SABOTAGE_ERROR;

	/**
	 * Auxiliary method used to define if an <code>ExecutionError</code> was
	 * caused by the task specification or the user's application.
	 * 
	 * @return <code>true</code> if it was.
	 */
	public boolean causedByUserApplication() {

		return this == APPLICATION_ERROR || this == EXECUTION_ERROR;
	}


	/**
	 * Auxiliary method used to define if an <code>ExecutionError</code> will
	 * cause Worker to enter Broker's blacklist.
	 * 
	 * @return <code>true</code> if it was.
	 */
	public boolean blackListError() {

		return causedByUserApplication() || this == IO_ERROR || this == SABOTAGE_ERROR || this == FILE_TRANSFER_ERROR;
	}


	public String getDescription() {

		String type = this.getName();
		String details = " - ";
		switch ( this ) {
		case INVALID_SESSION:
			details += "When a "
				+ type
				+ " occurs it means that the Worker no longer works for this Broker, i.e. the Worker has been reallocated to another user to improve fairness.";
			break;
		case MACHINE_FAILURE:
			details += "When a " + type + " occurs it means that the Worker has failed.";
			break;
		case APPLICATION_ERROR:
			details += "When a "
				+ type
				+ " occurs it means that the task being executed failed because of the user's task description. Verify your task description.";
			break;
		case CONCURRENT_RUNNING:
			details += "When a "
				+ type
				+ " occurs it means the Worker received two or more remote phase executions. This a probably a bug, visit http://www.ourgrid.org and contact us.";
			break;
		case EXECUTION_ERROR:
			details += "When a "
				+ type
				+ " occurs it means that the task being executed failed because of the user's application. Verify the command line submitted in the remote phase of the task.";
			break;
		case IO_ERROR:
			details += "When a "
				+ type
				+ " occurs it means that the Worker is unable to execute your task due to an I/O error. For instance, the Worker's disk might be full.";
			break;
		case FILE_TRANSFER_ERROR:
			details += "When a "
				+ type
				+ " occurs it means that the file transfer has failed ";
			break;
		case MACHINE_NOT_IDLE:
			details += "When a "
				+ type
				+ " occurs it means that the machine providing the Worker services is being used by its owner, i.e. it is no longer idle and thus not available for OurGrid.";
			break;
		case SABOTAGE_ERROR:
			details += "When a "
				+ type
				+ " occurs it means that the user's application was sabotaged by the executor resource.";
			break;
		case BROKER_ERROR:
			details += "When a "
				+ type
				+ " occurs it means that the Broker failed to move the temp files of a execution.";
			break;
		default:
			details += "No additional information";
			break;
		}

		return type + details;
	}

	public String getName() {
		return this.toString();
	}
	
	public static GridProcessErrorTypes getType(String error) {
		return GridProcessErrorTypes.valueOf(error);
	}
}
