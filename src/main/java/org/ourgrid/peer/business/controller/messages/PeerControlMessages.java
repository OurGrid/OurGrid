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
package org.ourgrid.peer.business.controller.messages;

public class PeerControlMessages {

	/**
	 * @param senderPubKey
	 * @return
	 */
	public static String getUnknownSenderSettingWorkersMessage(String senderPubKey) {
		return "An unknown entity tried to set the workers. Only the local modules can perform " +
				"this operation. Unknown entity public key: [" + senderPubKey + "].";
	}

	/**
	 * @param senderPubKey
	 * @return
	 */
	public static String getUnknownSenderAddingWorkerMessage(String senderPubKey) {
		return "An unknown entity tried to add a worker. Only the local modules can perform " +
				"this operation. Unknown entity public key: [" + senderPubKey + "].";
	}
	
	/**
	 * @param senderPubKey
	 * @return
	 */
	public static String getUnknownSenderRemovingWorkerMessage(String senderPubKey) {
		return "An unknown entity tried to remove a worker. Only the local modules can perform " +
				"this operation. Unknown entity public key: [" + senderPubKey + "].";
	}
	
	/**
	 * @param senderPublicKey
	 * @return
	 */
	public static String getUnknownSenderStoppingPeerMessage(String senderPublicKey) {
		return "An unknown entity tried to stop the Peer. Only the local modules can perform this operation. " +
				"Unknown entity public key: [" + senderPublicKey + "].";
	}

	/**
	 * @param senderPublicKey
	 * @return
	 */
	public static String getUnknownSenderStartingPeerMessage(String senderPublicKey) {
		return "An unknown entity tried to start the Peer. Only the local modules can perform this operation. " +
				"Unknown entity public key: [" + senderPublicKey + "].";
	}
	
	public static String getSuccessfullyStartedPeerMessage() {
		return "Peer has been successfully started.";
	}

}
