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

import org.ourgrid.peer.PeerConfiguration;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class VOMSMessages {

	/*VOMS Messages*/
	
	/**
	 * @param workerProviderClientID
	 * @param requestId
	 * @param errorMessage
	 * @return
	 */
	public static String getErrorOnConnectingToVOMSMessage(
			String workerProviderClientContainerID, long requestId, String errorMessage) {
		return "Request "+requestId+": request from ["+ workerProviderClientContainerID + "] ignored because " +
			"there was an error while connecting to VOMS. Error cause: " + errorMessage;
	}
	
	/**
	 * @param workerProviderID
	 * @param errorMessage
	 * @return
	 */
	public static String getErrorOnConnectingToVOMSMessage(
			DeploymentID workerProviderID, String errorMessage) {
		return "Disposing worker provider [" + workerProviderID + "] because there was an error while connecting to VOMS. " +
				"Error cause: " + errorMessage;
	}

	/**
	 * @param workerProviderClientID
	 * @param requestId
	 * @return
	 */
	public static String getNonAuthorisedConsumerMessage(String workerProviderClientContainerID, long requestId) {
		return "Request "+requestId+": request ignored because ["+ workerProviderClientContainerID + "] " +
				" is not authorized at VOMS.";
	}

	/**
	 * @param deploymentID
	 * @return
	 */
	public static String getNonAuthorisedProviderMessage(DeploymentID deploymentID) {
		return "Disposing worker provider [" + deploymentID + "] because it " +
			"is not authorized at VOMS.";
	}

	/**
	 * @return
	 */
	public static String getNullVOMSUrlMessage() {
		return "Property " + PeerConfiguration.PROP_VOMS_URL + " should not be null.";
	}
	
}
