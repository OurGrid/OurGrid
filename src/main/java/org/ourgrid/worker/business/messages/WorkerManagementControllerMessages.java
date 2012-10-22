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


public class WorkerManagementControllerMessages {

	public static String getUnknownPeerSetPeerMessage(String workerManagClientID, String senderPubKey) {
		return "The unknown peer [" + workerManagClientID + "] tried to set " +
				"itself as manager of this Worker. This message was ignored. " +
				"Unknown peer public key: [" + senderPubKey + "].";
	}

	public static String getPeerSetPeerMessage(String peerID) {
		return "The peer [" + peerID + "] set itself as manager of this Worker.";
	}

	public static String getUnknownPeerTryingToCommandWorkerToWorkForBrokerMessage(String senderPubKey) {
		return "An unknown peer tried to command this Worker to work for a local consumer." +
				" This message was ignored. Unknown peer public key: [" + senderPubKey + "].";
	}

	public static String getMasterPeerTryingToCommandWorkerBeforeSettingAsManagerMessage() {
		return "The master Peer tried to manage this Worker before setting itself as manager of this Worker." +
				" This message was ignored.";
	}
	
	public static String getMasterPeerTryingToCommandWorkerOnErrorStateMessage() {
		return "The master Peer tried to manage this Worker, but it's on error state.";
	}

	public static String getMasterPeerCommandedOwnerWorkerToWorkForBrokerMessage() {
		return "This Worker was commanded to work for a local consumer," +
				" but it is in the OWNER status. This message was ignored.";
	}

	public static String getMasterPeerCommandedWorkerToWorkForBrokerMessage(String brokerPublicKey) {
		return 	"Peer commanded this Worker to work for a local consumer." +
				" Local consumer public key: [" + brokerPublicKey + "].";
	}
	
	public static String getMasterPeerSendsWorkForBrokerToWorkerOnErrorStateMessage() {
		return 	"Peer commanded this Worker to work for a local consumer, but it's on error state.";
	}
	
	public static String getUnknownPeerSendsWorkForPeerMessage(String senderPubKey) {
		return "An unknown peer tried to command this Worker to work for a remote peer. " +
					"This message was ignored. Unknown peer public key: [" + senderPubKey + "].";
	}

	public static String getUnsetMasterPeerSendsWorkForPeerMessage() {
		return "The master Peer tried to manage this Worker before setting itself as manager " +
				"of this Worker. This message was ignored.";
	}
	
	public static String getMasterPeerSendsWorkForPeerToWorkerOnErrorStateMessage() {
		return "Peer commanded this Worker to work for a remote peer, but it's on error state.";
	}

	public static String getWorkForPeerOnOwnerWorkerMessage() {
		return "This Worker was commanded to work for a remote peer, " +
				"but it is in the OWNER status. This message was ignored.";
	}
	
	public static String getWorkForPeerOnWorkingStateWorkerMessage() {
		return "This Worker was commanded to work for a remote peer, " +
				"but it is in the Working State. This message was ignored.";
	}
	
	public static String getSuccessfulWorkForPeerMessage(String remotePeerPubKey) {
		return "Peer commanded this Worker to work for a remote peer. " +
				"Remote peer public key: [" + remotePeerPubKey + "].";
	}
	
	public static String getWorkForPeerOnAllocatedForBrokerWorkerMessage() {
		return "Strange behavior: This Worker was allocated to a local consumer, " +
				"but was now commanded to work for a remote peer.";
	}
	
	public static String getStopWorkingByUnknownPeerMessage(String senderPubKey) {
		return "An unknown peer tried to command this Worker to stop working. " +
				"This message was ignored. Unknown peer public key: [" + senderPubKey + "].";
	}
	
	public static String getStopWorkingOnNotWorkingWorkerMessage() {
		return "This Worker was commanded to stop working, but it's not working for any client. " +
				"This message was ignored.";
	}
	
	public static String getSuccessfulStopWorkingMessage() {
		return "This Worker was commanded to stop working for the current client.";
	}

	public static String getMasterPeerThatDidNotFailTryToSetPeer(String workerManagClientID) {
		return "The peer [" + workerManagClientID + "] set itself as manager of this Worker. This message was ignored." +
					" Because the master peer did not notify fail.";
	}

	public static String getWorkerIsNotLoggedInPeerMessage() {
		return "This Worker is not logged at peer.";
	}

}
