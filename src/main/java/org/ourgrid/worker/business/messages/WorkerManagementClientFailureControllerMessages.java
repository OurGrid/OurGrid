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



public class WorkerManagementClientFailureControllerMessages {

	public static String getUnknownPeerFailsMessage(String peerID) {
		return "The unknown peer [" + peerID + "] has failed. This message was ignored.";
	}

	public static String getUndefinedMasterPeerFailsMessage(String peerID) {
		return "The peer [" + peerID + "] that didn't set itself as manager of this Worker has failed." +
				" This message was ignored.";
	}

	public static String getMasterPeerFailsMessage(String peerID) {
		return "The master peer [" + peerID + "] has failed. Worker will interrupt the working," +
				" it means cancel any transfer or execution.";
	}


}
