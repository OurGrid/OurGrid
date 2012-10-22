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



public class WorkerManagementClientRecoveryControllerMessages {

	public static String getMasterPeerRecoveryMessage(String peerID) {
		return "The peer [" + peerID + "] has recovered and will be set like master peer.";
	}

	public static String getPeerAddressIsNotTheMasterPeerAddressMessage(String peerID) {
		return "The peer [" + peerID + "] is not the master peer.";
	}
	
	public static String getPeerAlreadyIsUpMessage(String peerID) {
		return "The peer [" + peerID + "] already is up.";
	}
	
}
