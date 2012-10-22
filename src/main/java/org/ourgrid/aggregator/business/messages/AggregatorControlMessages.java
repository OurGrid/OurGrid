/*
 * Copyright (C) 2011 Universidade Federal de Campina Grande
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
package org.ourgrid.aggregator.business.messages;


/**
 * This class provide a lot of messages for all module Aggregator.
 *
 */
public class AggregatorControlMessages {

	/**
	 * This static method throws a message from an unknown sender
	 * public key that trying to stop the component.
	 * @param senderPublicKey {@link String}
	 * @return String {@link String}
	 */
	public static String getUnknownSenderStoppingAggregatorMessage(
			String senderPublicKey) {
		return "An unknown entity tried to stop the Aggregator. Only the local modules can perform this operation. "
				+ "Unknown entity public key: [" + senderPublicKey + "].";
	}

	/**
	 * This static method throws a message from an unknown sender
	 * public key that trying to start the component.
	 * @param senderPublicKey {@link String}
	 * @return String {@link String}
	 */
	public static String getUnknownSenderStartingAggregatorMessage(
			String senderPublicKey) {
		return "An unknown entity tried to start the Aggregator. Only the local modules can perform this operation. "
				+ "Unknown entity public key: [" + senderPublicKey + "].";
	}

	/**
	 * This static method throws a message informs that the
	 * Aggregator has started correctly.
	 * @return String {@link String}
	 */
	public static String getSuccessfullyStartedAggregatorMessage() {
		return "Aggregator has been successfully started.";
	}

	/**
	 * This static method throws a message informs that the 
	 * Aggregator has shutdown correctly.
	 * @return String {@link String}
	 */
	public static String getSuccessfullyShutdownAggregatorMessage() {
		return "Aggregator has been successfully shutdown.";
	}
	/**
	 * This method throws a message that did not complete the
	 * operation due to the CommunityStatusProvider to be down,
	 * here is applicable to class CommunityStatusProviderIsDownRequester.
	 * @return String {@link String}
	 */
	public static String getCommunityStatusProviderIsDownWarningMessage() {
		return "Unsuccessful data transfer. The Community Status Provider is Down";
	}

	/**
	 * This method throws a message saying that the CommunityStatusProvider
	 * is still up, valid for class CommunityStatusProviderIsUpRequester.
	 * @return String {@link String}
	 */
	public static String getCommunityStatusProviderIsUpWarningMessage() {
		return "Unsuccessful data transfer. The Community Status Provider is Up"; 
	}

	/**
	 * This method throws a message saying that complete the operation and
	 * the CommunityStatusProvider is down, valid for class CommunityStatusProviderIsDownRequester.
	 * @return String {@link String}
	 */
	public static String getCommunityStatusProviderIsDownInfoMessage() {
		return "Successful operation. The Community Status Provider is Down";
	}

	/**
	 * This method throws a message saying that complete the operation
	 * and the CommunityStatusProvider is up, valid for class CommunityStatusProviderIsUpRequester.
	 * @return String {@link String}
	 */
	public static String getCommunityStatusProviderIsUpInfoMessage() {
		return "Successful data transfer. The Community Status Provider is up";
	}

	/**
	 * This method throws a message saying that the operation 
	 * is realized successful and the list of peers were updated.
	 * @return String {@link String}
	 */
	public static String getHereIsPeerStatusChangeHistoryInfoMessage() {
		return "Successful data transfer. The list of peers were updated";
	}

	/**
	 * This method throws a message saying that the operation 
	 * is realized successful and the address list is now update. 
	 * @return String {@link String}
	 */
	public static String getHereIsStatusProviderListInfoMessage() {
		return "Successful data transfer. The addresses are now ready";
	}

	/**
	 * This method throws a message saying that the operation 
	 * is realized successful and the Peer Status provider is up. 
	 * @return String {@link String}
	 */
	public static String getHereIsCompleteHistoryStatusPeerStatusInfoMessage() {
		return "Successful data transfer. The Peer Status provider is up";
	}

	/**
	 * This method throws a message saying that the operation 
	 * is realized successful and the history status was updated. 
	 * @return String {@link String}
	 */
	public static String getHereIsCompleteHistoryStatusInfoMessage() {
		return "Successful data transfer. The history status was updated";
	}

	/**
	 * This method throws a message saying that the operation 
	 * is realized successful and the Status provider is now up. 
	 * @return String {@link String}
	 */
	public static String getPeerStatusProviderIsUpInfoMessage() {
		return "Successful data transfer. The Status provider is now up";
	}

	/**
	 * This method throws a message saying that the operation 
	 * is realized successful and the Community Status Provider is Down. 
	 * @return String {@link String}
	 */
	public static String getPeerStatusProviderIsDownInfoMessage(String peerAddress) {
		return "Successful data transfer. The Peer Status Provider is Down: " + peerAddress;
	}

	/**
	 * This method throws a message saying that the operation 
	 * is realized successful and The list of Peers and the status were ordered
	 * to DiscoryService.
	 * @return String {@link String}
	 */
	public static String getGetPeerStatusProviderRepeatedActionInfoMessage() {
		return "Successful data transfer. The list of Peers and the status were ordered" +
				" to DiscoryService";
	}

	/**
	 * This method throws a message saying that the operation 
	 * is realized unsuccessful and The CommunityStatusProvider address is wrong.
	 * @return String {@link String}
	 */
	public static String getWrongCommunityStatusProviderAddressWarningMessage() {
		return "Unsuccessful data transfer. Wrong CommunityStatusProvider address";
	}
	
	/**
	 * This method throws a message saying that the operation 
	 * is realized unsuccessful and The Peer Status provider is Down.
	 * @return String {@link String}
	 */
	public static String getPeerStatusProviderIsDownMessage() {
		return "Unsuccessful data transfer. The Peer Status provider is Down";
	}

	/**
	 * This method throws a message saying that the operation 
	 * is realized unsuccessful and The Peer Status provider is already Up.
	 * @return String {@link String}
	 */
	public static String getPeerStatusProviderIsUpAlreadyUpMessage() {
		return "Unsuccessful data transfer. The Peer Status provider is already Up";
	}
	
	/**
	 * This method throws a message saying that the operation 
	 * is realized unsuccessful and The AggregatorPeerStatusProvider address is
	 * not corresponding to the address.
	 * @return String {@link String}
	 */
	public static String getAggregatorPeerStatusProviderIsNullMessage() {
		return "Unsuccessful data transfer. No AggregatorPeerStatusProvider corresponding to the address";
	}

	/**
	 * This method throws a message saying that the operation 
	 * is realized unsuccessful and The list´s status provider address is empty
	 * @return String {@link String}
	 */
	public static String getProviderAddressListIsEmptyMessage() {
		return "Unsuccessful data transfer. The list´s status provider address is empty";
	}
	
	
	/**
	 * This method throws a message saying that the status was complete unsuccessful.
	 * The component is not started.
	 * @param dsAddress {@link String}
	 * @return String {@link String}
	 */
	public static String getCompleteStatusWarnMessage(String dsAddress) {
		return "Received a status request from: " + dsAddress + " but the component is not started.";
		
	}
	
	/**
	 * This method throws a message saying that the status was complete with successful.
	 * @param dsAddress {@link String}
	 * @return String {@link String}
	 */
	public static String getCompleteStatusInfoMessage(String dsAddress) {
		return "Received a status request from: " + dsAddress + " and the component is started. "
			+ "The status was released.";
		
	}

	/**
	 * This method throws a message saying that the Peer with the address was updated
	 * @param providerAddress {@link String}
	 * @return String {@link String}
	 */
	public static String getProviderWasUpdateInfoMessage(String providerAddress) {
		return "The Peer: " + providerAddress + " was updated.";  
	}

	/**
	 * This method throws a message saying that will register interest for the Peer with the address
	 * @param providerAddress {@link String}
	 * @return String {@link String}
	 */
	public static String getProviderRegisterInterestInfoMessage(
			String providerAddress) {
		return "Intereste in the Peer: " + providerAddress + " will Register.";  
	}

	/**
	 * This method throws a message saying that the Peer with the address was removed
	 * @param oldProviderAddress {@link String}
	 * @return String {@link String}
	 */
	public static String getRemoveOldProviderInfoMessage(
			String oldProviderAddress) {
		return "The Peer: " + oldProviderAddress + " was removed."; 
	}

	/**
	 * This method throws a message saying that the transaction with DB was send an erro
	 * @param method {@link String} was throw exception
	 * @param message {@link String} of exception 
	 * @return String {@link String}
	 */
	public static String getRollbackTransactionMessage(String method, String message) {
		return "Failure on transaction. Hibernate has throws an exception in method " + method + " the exception was:" + message;
	}

	/**
	 * This method throws a message saying that the operation was completed without an erro
	 * for the method addCompleteHistoryStatus of AggregatorDAO
	 * @return String {@link String}
	 */
	public static String getAddCompleteHistoryStatusSuccessfulMessage() {
		return "Successful data transfer. The complete status was safe in DB";
	}

	/**
	 * This method throws a message saying that the operation was completed without an erro
	 * for the method savePeersStatusChanges of AggregatorDAO
	 * @return String {@link String}
	 */
	public static String getSavePeersStatusChangesSuccessfulMessage() {
		return "Successful data transfer. The status changes was safe in DB";
	}
	
	/**
	 * This method returns a message indicating that the community status provider
	 * is down or with an error in its specification.
	 * 
	 * @return String {@link String} The error message
	 */
	public static String getCommunityStatusProviderIsWrongOrDownMessage() {
		return "Unsuccessful connection. The community status provider is " +
				"wrong or down.";
	}

}
