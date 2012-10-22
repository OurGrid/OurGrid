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
package org.ourgrid.broker.business.scheduler;

import java.util.List;

import org.ourgrid.broker.business.scheduler.extensions.GenericTransferProgress;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.job.GridProcess;
import org.ourgrid.worker.business.controller.GridProcessError;

/**
 *
 */
public interface RunningState {

	/**
	 * Notifies the client that the Worker is ready to receive calls to other
	 * methods
	 */
	public void workerIsReady(GridProcess gridProcess, List<IResponseTO> responses);


	/**
	 * Delivers the result of a remote gridProcess to the client
	 * 
	 * @param result a package containing information about the gridProcess
	 * @see Worker#remoteExecute(WorkerClient, long, String, java.util.Map)
	 */
	public void hereIsExecutionResult( ExecutorResult result, GridProcess gridProcess,List<IResponseTO> responses );


	/**
	 * If something wrong occurs during an replica gridProcess (including file
	 * transfers), the client will receive details about the error through this
	 * method
	 * 
	 * @param error a detail about the error
	 */
	public void errorOcurred( GridProcessError error, GridProcess gridProcess, List<IResponseTO> responses);


	/**
	 * Delivers the file information requested to the Worker
	 * 
	 * @param operationHandle a handle to the replica associated to gridProcess
	 * @param fileInfo a package containing information about the file
	 */
	public void hereIsFileInfo(long handlerId, FileInfo fileInfo, GridProcess gridProcess, List<IResponseTO> responses);
	
	/**
	 * File transfer was reject (by the remote access point or by the object).
	 * 
	 * @param handle
	 *            Handler to identify this file transfer.
	 */
	public void fileRejected(OutgoingHandle handle, GridProcess gridProcess, List<IResponseTO> responses);


	/**
	 * Signals that a transfer has been failed. This means that the stream was
	 * closed or broken.
	 * 
	 * @param handle
	 *            Handler to identify this file transfer.
	 * @param failCause 
	 * @param amountWritten 
	 */
	public void outgoingTransferFailed(OutgoingHandle handle, String failCause, long amountWritten, 
			GridProcess gridProcess, List<IResponseTO> responses);

	/**
	 * Transfer was complete.
	 * 
	 * @param handle
	 *            Handler to identify this file transfer.
	 * @param amountWritten 
	 */
	public void outgoingTransferCompleted(OutgoingHandle handle, long amountWritten, GridProcess gridProcess, List<IResponseTO> responses);

	/**
	 * Transfer has been cancelled by the receiver
	 * 
	 * @param handle 
	 * @param amountWritten 
	 */
	public void outgoingTransferCancelled( OutgoingHandle handle, long amountWritten, GridProcess gridProcess, List<IResponseTO> responses);
	
	public void fileTransferRequestReceived( IncomingHandle handle, GridProcess gridProcess, List<IResponseTO> responses);

	public void incomingTransferFailed( IncomingHandle handle, Exception failCause, long amountWritten, 
			GridProcess gridProcess, List<IResponseTO> responses);

	public void incomingTransferCompleted( IncomingHandle handle, long amountWritten, GridProcess gridProcess, List<IResponseTO> responses);
	
	public void updateTransferProgress( GenericTransferProgress fileTransferProgress, GridProcess gridProcess, List<IResponseTO> responses);
	
}
