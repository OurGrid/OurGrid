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

import org.ourgrid.common.interfaces.to.GridProcessHandle;

import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class SchedulerMessages {

	public static String getUnknownSenderSchedulingBrokerMessage(String senderPublicKey) {
		return "An unknown entity tried to perform a scheduling operation on the Broker. " +
				"Only the local modules can perform this operation. " +
				"Unknown entity public key: [" + senderPublicKey + "].";
	}

	public static String getExecutingReplicaMessage(GridProcessHandle handle, String workerID) {
		return "Executing replica: " + handle.toString() + ", Worker: " + workerID;
	}

	public static String getDisposingWorkerMessage(ServiceID serviceID) {
		return "Worker dispose: " + serviceID;
	}
}
