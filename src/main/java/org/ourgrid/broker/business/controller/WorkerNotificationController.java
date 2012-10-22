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
package org.ourgrid.broker.business.controller;

import java.util.List;

import org.ourgrid.broker.business.requester.util.UtilProcessor;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.reqtrace.Req;

/**
 * Provides information related to the Worker failure.
 */
public class WorkerNotificationController {

	private static WorkerNotificationController instance;
	
	public static WorkerNotificationController getInstance() {
		if (instance == null) {
			instance = new WorkerNotificationController();
		}
		return instance;
	}
	
	private WorkerNotificationController() {}
	
	/**
	 * Notifies the worker failure, informing the Worker stub and its DeploymentID. 
	 * @param responses
	 * @param workerAddress
	 * @param workerContainerID
	 * @param workerPublicKey
	 */
	@Req("REQ330")
	public void doNotifyFailure(List<IResponseTO> responses, String workerContainerID) {
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(
				workerContainerID);
		
		if (scheduler != null) {
			scheduler.workerFailure(workerContainerID, 
					responses);
		}
	}
	
	/**
	 * 
	 * @param responses
	 * @param workerDeploymentID
	 * @param workerAddress
	 * @param workerContainerID
	 * @param workerPublicKey
	 */
	public void doNotifyRecovery(List<IResponseTO> responses, String workerDeploymentID, String workerPublicKey) {
		
		SchedulerIF scheduler = UtilProcessor.getScheduler(
				StringUtil.deploymentIDToContainerID(workerDeploymentID));
		
		if (scheduler != null) {
			scheduler.workerRecovery(workerPublicKey, workerDeploymentID, responses);
		}
		
	}
}
