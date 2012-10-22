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
package org.ourgrid.common.interfaces.control;

import org.ourgrid.common.interfaces.status.WorkerCompleteStatus;
import org.ourgrid.common.interfaces.to.WorkerStatus;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.container.control.ModuleControlClient;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;


/**
 * This is a callback interface of the WorkerControl interface. It will receive the
 * results of the WorkerControl operations.
 *
 */
@Remote
public interface WorkerControlClient extends ModuleControlClient {
	
	/**
	 * This method configures the master peer of this client.
	 * @param masterPeer
	 */
	void hereIsMasterPeer(ServiceID masterPeer);

	/**
	 * This method configures the status of this client. The possible status
	 * are defined by the workerStatus enumeration. 
	 * @param workerStatus Defines the status of this Worker.
	 */
	void hereIsStatus( WorkerStatus workerStatus );
	
	/**
	 * Configures complete information about the worker current status.
	 * @param completeStatus Defines information about the worker status, current playpen and storage 
	 * directories and the worker master peer. 
	 * 
	 */
	void hereIsCompleteStatus(WorkerCompleteStatus completeStatus);
	
}