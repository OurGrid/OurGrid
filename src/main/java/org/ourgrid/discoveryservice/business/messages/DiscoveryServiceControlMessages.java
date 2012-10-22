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
package org.ourgrid.discoveryservice.business.messages;


public class DiscoveryServiceControlMessages {

	/**
	 * @param senderPublicKey
	 * @return
	 */
	public static String getUnknownSenderStartingDiscoveryServiceMessage(String senderPublicKey) {
		return "An unknown entity tried to start the Discovery Service. Only the local modules can perform this operation. " +
				"Unknown entity public key: [" + senderPublicKey + "].";
	}
	
	/**
	 * Requirement 502
	 */
	public static String getSuccessfullyStartedDiscoveryServiceMessage() {
		return "Discovery Service has been successfully started.";
	}
	
	/**
	 * @param senderPublicKey
	 * @return
	 */
	public static String getUnknownSenderStoppingDiscoveryServiceMessage(String senderPublicKey) {
		return "An unknown entity tried to stop the Discovery Service. Only the local modules can perform this operation. " +
				"Unknown entity public key: [" + senderPublicKey + "].";
	}
	
	/**
	 * @param senderPublicKey
	 * @return
	 */
	public static String getClientNotJoinedToTheCommunityMessage(String clientString) {
		return "The client with ID [" + clientString + "] is not joined to the community.";
	}
	
	/**
	 * @param senderPublicKey
	 * @return
	 */
	public static String getDiscoveryServiceIsOverloadedMessage(String clientString) {
		return "The client with ID [" + clientString + "] could not join the community because the Discovery Service is overloaded.";
	}
	
	/**
	 * @param senderPublicKey
	 * @return
	 */
	public static String getClientAlreadyJoinedToTheCommunityMessage(String clientString) {
		return "The client with ID [" + clientString + "] is already joined to the community.";
	}
	
	/**
	 * @param senderPublicKey
	 * @return
	 */
	public static String getClientNotLoggedMessage(String clientUserAtServer) {
		return "The client [" + clientUserAtServer + "] is not logged.";
	}
	
	/**
	 * @param senderPublicKey
	 * @return
	 */
	public static String getNullMonitorableIDMessage() {
		return "Client ID invalid: null";
	}
	
	public static String getUnknownSenderHereIsRemoteWorkerProvidersListMessage(String dsAddress) {
		return "The DS [" + dsAddress + "] tried to send a Remote Worker Providers List but it does not belong to my network.";
	}
	
	public static String getUnknownSenderHereAreDiscoveryServicesMessage(String dsAddress){
		return "The DS [" + dsAddress + "] tried to send a Discovery Services List but it does not belong to my network.";
	}
	
	public static String getNotInterestedDiscoveryServiceClientIsUpMessage(String dscAddress) {
		return "The DS received a DS Client is UP message for [" + dscAddress + "], but the DS is not interested in this entity.";
	}
	
	public static String getDSNotMemberOfNetworkMessage(String dsAddress) {
		return "The DS [" + dsAddress + "] does not belong to my network.";
	}
	
	public static String getFailureNotificationFromAFailedDSMessage(String dsAddress) {
		return "The DS [" + dsAddress + "] has already failed.";
	}
	
	public static String getDiscoveryServiceFailureNotificationMessage(String dsAddress) {
		return "The DS [" + dsAddress + "] has failed.";
	}
	
	public static String getDiscoveryServiceIsUpNotificationMessage(String dsAddress) {
		return "The DS [" + dsAddress + "] is up.";
	}
	
	public static String getDiscoveryServiceIsDownNotificationMessage(String dsAddress) {
		return "The DS [" + dsAddress + "] is down.";
	}
	
	public static String getPeerLeftCommunityNotificationMessage(String peerAddress) {
		return "The Peer [" + peerAddress + "] left the community.";
	}
	
}
