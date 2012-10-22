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



public class ConsumerMessages {

	/*Remote consumer failure messages*/
	
	/**
	 * @param objectID
	 * @return
	 */
	public static String getUnknownRemoteConsumerFailureMessage(
			String monitorableOID) {
		return "Failure of an unknown remote consumer [" + monitorableOID + "]. This notification was ignored.";
	}

	/**
	 * @param remoteConsumerOID
	 * @return
	 */
	public static String getRemoteConsumerFailureMessage(String remoteConsumerOID) {
		return "The remote consumer [" + remoteConsumerOID + "] has failed. Disposing workers allocated to this consumer.";
	}

	/*Local consumer failure messages*/
	
	/**
	 * @param monitorableID
	 * @param brokerPublicKey 
	 * @return
	 */
	public static String getLocalConsumerFailureMessage(String monitorableID, String brokerPublicKey) {
		return "The local consumer [" + monitorableID + "] with publicKey [" + brokerPublicKey + "] has failed. Canceling his requests.";
	}

	/**
	 * @param monitorableID
	 * @return
	 */
	public static String getOfflineLocalConsumerFailureMessage(String monitorableID) {
		return "Failure of an offline local consumer [" + monitorableID + "]." +
				" This notification was ignored.";
	}

	/**
	 * @param monitorableID
	 * @param monitorablePubKey
	 * @return
	 */
	public static String getWrongPubKeyLocalConsumerFailureMessage(String monitorableID,
			String monitorablePubKey) {
		return "Failure of a local consumer [" + monitorableID + "], with a wrong public key." +
				" This notification was ignored. Wrong consumer public key: [" + monitorablePubKey + "].";
	}

	/**
	 * @param monitorableID
	 * @return
	 */
	public static String getUnknownLocalConsumerFailureMessage(String monitorableID) {
		return "Failure of an unknown local consumer [" + monitorableID + "]." +
				" This notification was ignored.";
	}

	/* Remote consumer disposing worker messages */
	
	/**
	 * @param consumerPublicKey
	 * @return
	 */
	public static String getUnknownConsumerDisposingWorkerMessage(
			String consumerPublicKey) {
		return "Ignoring an unknown remote consumer which disposed a worker. Remote consumer public key: "+consumerPublicKey;
	}

	/**
	 * @param remoteConsumerOID
	 * @return
	 */
	public static String getRemoteConsumerDisposingNullWorkerMessage(
			String remoteConsumerOID) {
		return "The remote consumer [" + remoteConsumerOID + "] " +
				"disposed a null worker. This dispose was ignored.";
	}

	/**
	 * @param remoteConsumerOID
	 * @param workerOID
	 * @return
	 */
	public static String getRemoteConsumerDisposingNotAllocatedWorkerMessage(
			String remoteConsumerOID, String workerOID) {
		return "The remote consumer [" + remoteConsumerOID + "] disposed the worker " +
				"[" + workerOID + "], that is not allocated for him. This disposal was ignored.";
	}

	/**
	 * @param remoteConsumerOID
	 * @return
	 */
	public static String getRemoteConsumerDisposingUnknownWorkerMessage(
			String remoteConsumerOID) {
		return "The remote consumer [" + remoteConsumerOID + "] disposed a unknown worker. This disposal was ignored.";
	}

	/**
	 * @param remoteConsumerOID
	 * @param workerOID
	 * @return
	 */
	public static String getRemoteClientDisposingWorkerMessage(
			String remoteConsumerOID, String workerOID) {
		return "The remote client [" + remoteConsumerOID + "] " +
				"disposed the worker [" + workerOID + "].";
	}
}
