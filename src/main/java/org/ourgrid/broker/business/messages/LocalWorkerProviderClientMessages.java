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


/**
 *
 */
public class LocalWorkerProviderClientMessages {

	/**
	 * @param result
	 * @param peerPublicKey
	 * @return
	 */
	public static String getErrorOcurredWhileLoggingIn(String resultMsg,
			String peerPublicKey) {
		return "An error ocurred while logging in the peer with public key : [" + peerPublicKey + "] - " + resultMsg;
	}

	/**
	 * @param peerPublicKey
	 * @return
	 */
	public static String getPeerLoggedLoginSucceedMessage(String peerPublicKey) {
		return "The peer with public key [" + peerPublicKey + "] tried to send a login response, but it is already logged.";
	}

	/**
	 * @param peerPublicKey
	 * @return
	 */
	public static String getPeerDownLoginSucceedMessage(String peerPublicKey) {
		return "The peer with public key [" + peerPublicKey + "] tried to send a login response, but it is down.";
	}

	/**
	 * @param peerPublicKey
	 * @return
	 */
	public static String getUnknownPeerSentALoginSucceedMessage(String peerPublicKey) {
		return "An unknown peer sent a login response. Peer public key: [" + peerPublicKey + "]";
	}

	public static String getUnknownPeerDeliveredAWorkerMessage(String workerPublicKey, String senderPublicKey) {
		return "An unknown peer delivered a worker with public key: [" + workerPublicKey + "], which was ignored. " +
				"Peer public key: [" + senderPublicKey + "].";
	}

	public static String getDownPeerDeliveredAWorkerMessage(String workerPublicKey, String senderPublicKey) {
		return "The peer with public key [" + senderPublicKey + "], which is down, delivered a worker with public key: [" + workerPublicKey + "].";
	}

	public static String getNotLoggedPeerDeliveredAWorkerMessage(String workerPublicKey, String senderPublicKey) {
		return "The broker is not logged in the peer with public key [" + senderPublicKey + "]. This worker with public key ["
			+ workerPublicKey + "] delivery was ignored.";
	}

	public static String getBrokerWithoutRunningJobsReceivingWorkerMessage(String workerPublicKey, String senderPublicKey) {
		return "The broker has no running jobs. Disposing worker with public key: [" + workerPublicKey + "] to " +
				" peer with public key: [" + senderPublicKey + "].";
	}
	
	public static String nullRequestMessage(String workerPublicKey, String senderPublicKey) {
		return "Request is null. Disposing worker with public key: [" + workerPublicKey + "] to " +
		" peer with public key: [" + senderPublicKey + "].";
	}

}
