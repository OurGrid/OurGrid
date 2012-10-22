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

import java.util.Map;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerStatus;

public class WorkerMessages {

	/* Worker disposal messages */
	
	public static String getDisposingWorkerMessage(long requestID, String lwpcOID, String wmAddress) {
		return "Request " + requestID + ": [" + lwpcOID + "] " +
		"disposed the worker [" + wmAddress + "].";
	}

	/**
	 * @param brokerPublicKey
	 * @return
	 */
	public static String getUnknownConsumerDisposingWorkerMessage(
			String brokerPublicKey) {
		return "Ignoring an unknown consumer which disposed a worker. Consumer public key: " + 
				brokerPublicKey;
	}
	
	/**
	 * @param brokerPublicKey
	 * @return
	 */
	public static String getNotLoggedConsumerDisposingWorkerMessage(
			String brokerPublicKey) {
		return "Ignoring a not logged consumer which disposed a worker. Consumer public key: " + 
				brokerPublicKey;
	}
	
	public static String getUnknownConsumerAllocatedForBroker(
			String brokerPublicKey) {
		return "Ignoring an unknown consumer which receiving a worker. Consumer public key: " + 
				brokerPublicKey;
	}
	
	/**
	 * @return
	 */
	public static String getNullConsumerChangeStatusToAllocatedForBroker() {
		return "Allocation with a null consumer. The status change was ignored.";
	}
	
	/**
	 * @return
	 */
	public static String getDifferentConsumerChangeStatusToAllocatedForBroker() {
		return "Allocation with a different consumer. The status change was ignored.";
	}

	/**
	 * @return
	 */
	public static String getNullConsumerChangeStatusToAllocatedForPeer() {
		return "Allocation with a null consumer. The status change was ignored.";
	}
	
	/**
	 * @return
	 */
	public static String getDifferentConsumerChangeStatusToAllocatedForPeer() {
		return "Allocation with a different consumer. The status change was ignored.";
	}
	
	/**
	 * @param brokerPublicKey
	 * @return
	 */
	public static String getNotLoggedUserDisposingWorkerMessage(String brokerPublicKey) {
		return "A not logged consumer disposed a worker. This disposal was ignored. Consumer public key " + 
				brokerPublicKey;
	}

	/**
	 * @param localWorker
	 * @param lwpcOID
	 * @return
	 */
	public static String getConsumerDisposingNotAllocatedWorkerMessage(String wAddress,
			String lwpcOID) {
		return "The consumer [" + lwpcOID + "] disposed the worker " +
				"[" + wAddress + "], that is not allocated for him. This disposal was ignored.";
	}

	/**
	 * @param lwpcOID
	 * @return
	 */
	public static String getUnknownWorkerDisposalMessage(String lwpcOID) {
		return "The consumer [" + lwpcOID + 
				"] disposed an unknown worker. This disposal was ignored.";
	}

	/**
	 * @param lwpc
	 * @return
	 */
	public static String getNullWorkerDisposalMessage(String lwpcDID) {
		return "The consumer [" + lwpcDID + 
				"] disposed a null worker. This disposal was ignored.";
	}

	/**
	 * @param providerID
	 * @param workerID
	 * @return
	 */
	public static String getDisposingWorkerToRemoteProviderMessage(
			String providerID, String workerID) {
		return "The remote worker " + workerID +" does not match any request. " +
				"Disposing it back to its provider: "+ providerID+ ".";
	}
	
	/* Unwanted worker messages */
	
	/**
	 * @param brokerPublicKey
	 * @return
	 */
	public static String getUnknownConsumerMarkedUnwantedWorkerMessage(
			String brokerPublicKey) {
		return "Ignoring an unknown consumer that set a worker as unwanted. Consumer public key: " + brokerPublicKey;
	}

	/**
	 * @param lwpcString
	 * @return
	 */
	public static String getNullUnwantedWorkerMessage(String lwpcString) {
		return "Ignoring the consumer [" + lwpcString + "] that set a null worker as unwanted.";
	}

	/**
	 * @param lwpcString
	 * @param workerString
	 * @return
	 */
	public static String getUnknownUnwantedWorkerMessage(String lwpcString,
			String workerString) {
		return "Ignoring the consumer [" + lwpcString + "] that set the unknown worker " +
				"[" + workerString + "] as unwanted.";
	}

	/**
	 * @param lwpcString
	 * @param workerString
	 * @return
	 */
	public static String getNotAllocatedUnwantedWorkerMessage(String lwpcString,
			String workerString) {
		return "Ignoring the consumer [" + lwpcString + "] that set a not allocated worker " +
				"[" + workerString + "] as unwanted.";
	}

	/**
	 * @param lwpcString
	 * @param workerString
	 * @param isLocal
	 * @return
	 */
	public static String getNullRequestUnwantedWorkerMessage(String lwpcString,
			String workerString, boolean isLocal) {
		return "Ignoring the consumer [" + lwpcString + "] that set the " +
				(isLocal ? "" : "remote ") + "worker [" + workerString + "]" +
						" as unwanted for a null request.";
	}

	/**
	 * @param requestID
	 * @param lwpcString
	 * @param workerString
	 * @param isLocal
	 * @return
	 */
	public static String getInvalidRequestUnwantedWorkerMessage(
			long requestID, String lwpcString,
			String workerString, boolean isLocal) {
		return "Ignoring the consumer [" + lwpcString + "] that set the " +
				(isLocal ? "" : "remote ") + "worker [" + workerString + "]" +
						" as unwanted for an invalid request [" + requestID + "].";
	}

	/* Remote worker failure messages */
	
	/**
	 * @param remoteWorkerID
	 * @return
	 */
	public static String getRemoteWorkerFailureMessage(String remoteWorkerID) {
		return "The remote Worker [" + remoteWorkerID + "] has failed. Disposing this Worker.";
	}

	/**
	 * @param remoteWorkerID
	 * @return
	 */
	public static String getAlreadyDeliveredRemoteWorkerFailureMessage(
			String remoteWorkerID) {
//		return "Failure of a remote Worker [" + remoteWorkerID + "] that was already delivered. " +
//				"This notification was ignored.";
		return "Failure of a remote Worker [" + remoteWorkerID + "] that was already delivered.";
	}

	/**
	 * @param remoteWorkerID
	 * @return
	 */
	public static String getUnknownOrDisposedRemoteWorkerFailureMessage(String remoteWorkerID) {
		return "Failure of an unknown or already disposed remote Worker [" + remoteWorkerID  + "]. This notification was ignored.";
	}
	
	/**
	 * @param remoteWorkerID
	 * @return
	 */
	public static String getUnknownOrDisposedPreemptedRemoteWorkerMessage(String remoteWorkerPublicKey) {
		return "Preemption of an unknown or already disposed remote Worker " +
				"with public key[" + remoteWorkerPublicKey  + "]. This notification was ignored.";
	}
	
	/* Remote worker status changed messages */
	
	/**
	 * @param lwpcOID
	 * @param rwmOID
	 * @return
	 */
	public static String getGivingRemoteWorkerMessage(String lwpcOID, String rwmOID) {
		return "Giving the remote worker [" + rwmOID + "] to [" + lwpcOID + "].";
	}

	/**
	 * @param workerString
	 * @return
	 */
	public static String getNullRemoteWorkerStatusChangedMessage(
			String workerString) {
		return "The remote worker " + workerString +
				" changed its status to ALLOCATED FOR BROKER, but it did not provide a worker reference. " +
				"This status change was ignored.";
	}

	/**
	 * @param wmPublicKey
	 * @return
	 */
	public static String getUnknownRemoteWorkerStatusChangedMessage(String wmPublicKey) {
		return "An unknown worker changed its status to Allocated for Broker. " +
				"It will be ignored. Worker public key: " + wmPublicKey;
	}

	/* Receiving remote worker messages */
	
	/**
	 * @param providerString
	 * @return
	 */
	public static String getReceivingNullRemoteWorkerMessage(
			String providerString) {
		return "Ignoring a null worker, which was received from the provider: " + providerString;
	}

	/**
	 * @param senderPublicKey
	 * @return
	 */
	public static String getReceivingNullRemoteProviderMessage(String senderPublicKey) {
		return "Ignoring a null provider. Sender public key: " + senderPublicKey;
	}
	
	/**
	 * @param providerString
	 * @return
	 */
	public static String getReceivingNullWorkerSpecRemoteWorkerMessage(
			String providerString) {
		return "Ignoring a worker without specification, which was received from the provider: " + providerString;
	}

	/**
	 * @param workerString
	 * @return
	 */
	public static String getReceivingAlreadyAllocatedRemoteWorkerMessage(
			String workerString) {
		return "Receiving a remote worker ["+ workerString +"] that is already allocated in this peer. " +
				"This message was ignored.";
	}

	/**
	 * @param providerString
	 * @param workerString
	 * @return
	 */
	public static String getReceivedRemoteWorkerMessage(String providerString,
			String workerString) {
		return "Received a worker ["+ workerString +"] from a remote worker provider ["+ providerString + "].";
	}

	/* Worker spec update messages */
	
	/**
	 * @param workerString
	 * @param newAttributes
	 * @return
	 */
	public static String getWorkerSpecUpdatedMessage(String workerString,
			Map<String, String> newAttributes) {
		return "The Worker [" + workerString + "] updated its specification. Updated attributes: "
				+ newAttributes + ".";
	}

	/**
	 * @param workerPublicKey
	 * @return
	 */
	public static String getUnknownWorkerUpdatingSpecMessage(String workerPublicKey) {
		return "An unknown Worker has updated its specification. This message was ignored." +
				" Unknown worker public key: [" + workerPublicKey + "]";
	}

	/* Local Worker notification messages */
	
	/**
	 * @param failedWorkerOID
	 * @return
	 */
	public static String getFailedWorkerMessage(String failedWorkerAddress) {
		return "Worker <" + failedWorkerAddress + "> is now DOWN";
	}

	/**
	 * @param failedWorkerOID
	 * @return
	 */
	public static String getNonRecoveredWorkerFailureMessage(String failedWorkerAddress) {
		return "Failure of a non-recovered worker: " + failedWorkerAddress;
	}

	/**
	 * @param failedWorkerAddress
	 * @return
	 */
	public static String getNonExistentWorkerFailureMessage(String failedWorkerAddress) {
		return "Failure of a non-existent worker: " + failedWorkerAddress;
	}

	/**
	 * @param recoveredWorkerID
	 * @return
	 */
	public static String getNotSetWorkerRecoveryMessage(String recoveredWorkerAddress) {
		return "Receiving a worker that was not set by 'setworkers': " + recoveredWorkerAddress;
	}

	/**
	 * @param recoveredWorkerID
	 * @return
	 */
	public static String getExistentWorkerRecoveryMessage(String recoveredWorkerAddress) {
		return "Receiving an already existent worker: " + recoveredWorkerAddress;
	}

	/**
	 * @param recoveredWorkerID
	 * @return
	 */
	public static String getWorkerRecoveryMessage(String recoveredWorkerAddress) {
		return "Worker <" + recoveredWorkerAddress + "> is now UP";
	}

	/**
	 * @param localWorkerID
	 * @return
	 */
	public static String getInvalidStatusChangeMessage(String localWorkerString, 
			LocalWorkerState localWorkerState) {
		return "The worker <" + localWorkerString + "> (" + localWorkerState + 
				") changed its status to ALLOCATED_FOR_BROKER. This status change was ignored.";
	}

	/**
	 * @param workerOID
	 * @return
	 */
	public static String getWrongPublicKeyForWorkerMessage(String workerString) {
		return "Wrong public key for Worker: " + workerString;
	}

	/**
	 * @param workerPubKey
	 * @return
	 */
	public static String getUnknownWorkerChangingStatusMessage(String workerPubKey, WorkerStatus newState) {
		return "Unknown worker changed status: " + workerPubKey +"/"+ newState;
	}

	/**
	 * @param workerID
	 * @return
	 */
	public static String getIgnoredStatusChangeMessage(String workerContainerID, LocalWorkerState oldState, WorkerStatus newState) {
		return "The worker <" + workerContainerID + "> (" + oldState + ") changed " +
				"its status to " + newState + ". This status change was ignored.";
	}

	/**
	 * @param localWorker
	 * @param rwpc
	 * @return
	 */
	public static String getDonatingWorkerMessage(String workerContainerID, String rwpcContainerID) {
		return "Donating Worker <" + workerContainerID + "> to <" + rwpcContainerID + ">";
	}

	/**
	 * @param workerID
	 * @return
	 */
	public static String getNullWorkerStatusChangedMessage(String workerID, WorkerStatus newStatus) {
		return "Worker <" + workerID + "> changed its status to" +
				" " + newStatus + ", but it did not provide a worker reference. This status change was ignored.";
	}

	/**
	 * @param localWorker
	 * @param allocable
	 * @return
	 */
	public static String getGivingWorkerMessage(String workerID, String consumerID) {
		return "Giving Worker <" + workerID + "> to <" + consumerID + ">";
	}

	/**
	 * @param workerID
	 * @param newState
	 * @return
	 */
	public static String getStatusChangedMessage(String workerContainerID, LocalWorkerState newState) {
		return "Worker <" + workerContainerID + "> is now " + newState; 
	}

}
