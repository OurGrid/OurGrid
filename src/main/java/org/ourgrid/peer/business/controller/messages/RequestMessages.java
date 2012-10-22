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

import org.ourgrid.common.interfaces.to.RequestSpecification;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class RequestMessages {

	/*Requesting workers Messages*/
	
	/**
	 * @param requestSpec
	 * @param userID
	 */
	public static String getRequestWithLessThanOneWorkerMessage(
			RequestSpecification requestSpec, String userID) {
		return "Request "+requestSpec.getRequestId()+": request ignored because [" 
				+ userID + "] requested less than 1 worker";
	}

	/**
	 * @param userID
	 * @return
	 */
	public static String getNullRequestMessage(String userID) {
		return "Client [" + userID + "] done a null request. This request was ignored.";
	}

	/**
	 * @param requestSpec
	 * @param userPubKey
	 * @return
	 */
	public static String getUnknownUserMessage(RequestSpecification requestSpec,
			String userPubKey) {
		return "Request "+requestSpec.getRequestId()+": request ignored because its public key is unknown: "+ userPubKey;
	}

	/**
	 * @param requestSpec
	 * @param localWorkerProviderClient
	 * @return
	 */
	public static String getNewRequestMessage(RequestSpecification requestSpec,
			String lwpcDID) {
		
		String plural = (requestSpec.getRequiredWorkers() > 1) ? "s" : "";

		return "Request "+requestSpec.getRequestId()+": [" + lwpcDID +"] requested "
				+ requestSpec.getRequiredWorkers()+" worker" + plural;
	}

	/*Remote requesting messages*/
	
	/**
	 * @param requestID
	 * @param workerProviderID
	 * @return
	 */
	public static String getRequestingFromRemoteProviderMessage(long requestID,
			DeploymentID workerProviderID) {
		return "Request "+ requestID +": requesting workers from a remote worker provider ["+workerProviderID+"].";
	}

	/*Request repetition messages*/
	
	/**
	 * @param delay
	 * @param requestID
	 * @return
	 */
	public static String getRequestRepetitionMessage(int delay, long requestID) {
		return "Request "+requestID+": request scheduled for repetition in "+delay+" seconds.";
	}

	
	/*Updating request messages*/
	
	/**
	 * @param lwpcDeploymentID
	 * @param requestID
	 * @return
	 */
	public static String getNonPositiveNoOfWorkersMessage(String lwpcDeploymentID,
			long requestID) {
		return "The consumer ["+ lwpcDeploymentID +"] updated the request ["+ requestID +"]" +
				" needing lower or equals zero workers. This message was ignored.";
	}

	/**
	 * @param lwpcDeploymentID
	 * @param requestID
	 * @return
	 */
	public static String getUnknownRequestUpdateMessage(String lwpcDeploymentID,
			long requestID) {
		return "The consumer [" + lwpcDeploymentID + "] updated" +
				" the unknown request [" + requestID + "]. This message was ignored.";
	}

	/**
	 * @param lwpcDeploymentID
	 * @return
	 */
	public static String getNullRequestUpdateMessage(String lwpcDeploymentID) {
		return "Ignoring the consumer [" + lwpcDeploymentID + "] " +
				"that updated a null request.";
	}

	/**
	 * @param userPubKey
	 * @return
	 */
	public static String getUnknownConsumerUpdatingRequestMessage(String userPubKey) {
		return "Ignoring an unknown consumer that updated a request. Sender public key: " + userPubKey;
	}

	/*Pausing request messages*/
	
	/**
	 * @param requestID
	 * @param lwpcOID
	 * @return
	 */
	public static String getRequestAlreadyPausedMessage(long requestID,
			String lwpcOID) {
		return "The consumer [" + lwpcOID + "] paused the already paused request" +
				" [" + requestID + "]. This message was ignored.";
	}

	/**
	 * @param requestID
	 * @param lwpcOID
	 * @return
	 */
	public static String getRequestPausedMessage(long requestID, String lwpcOID) {
		return "Request " + requestID + ": Consumer [" + lwpcOID + "] paused the request.";
	}

	/**
	 * @param requestID
	 * @param lwpcOID
	 * @return
	 */
	public static String getUnknownRequestPauseMessage(long requestID,
			String lwpcOID) {
		return "The consumer [" + lwpcOID + "] paused the unknown request" +
				" [" + requestID + "]. This message was ignored.";
	}

	/**
	 * @param senderPubKey
	 * @return
	 */
	public static String getUnknownConsumerPausingRequestMessage(String senderPubKey) {
		return "Ignoring an unknown consumer that paused a request. Sender public key: " + senderPubKey;
	}

	/*Finishing request messages*/
	
	/**
	 * @param requestSpec
	 * @param lwpcOID
	 * @return
	 */
	public static String getUnknownRequestFinishedMessage(RequestSpecification requestSpec,
			String lwpcOID) {
		return "The consumer ["+ lwpcOID +"] finished the unknown request ["+ requestSpec.getRequestId() +"]. This message was ignored.";
	}

	/**
	 * @param brokerPublicKey
	 * @return
	 */
	public static String getUnknownUserFinishingRequestMessage(String brokerPublicKey) {
		return "Ignoring an unknown consumer that finished a request. Sender public key: " + brokerPublicKey;
	}

	/*Resuming request messages*/
	
	/**
	 * @param requestID
	 * @param lwpcOID
	 * @return
	 */
	public static String getNotPausedRequestMessage(long requestID, String lwpcOID) {
		return "The consumer [" + lwpcOID + "] resumed the request" +
				" [" + requestID + "], that was not paused. This message was ignored.";
	}

	/**
	 * @param requestID
	 * @param lwpcOID
	 * @return
	 */
	public static String getRequestResumedMessage(long requestID, String lwpcOID) {
		return "Request " + requestID + ": Consumer [" + lwpcOID + "] resumed the request.";
	}

	/**
	 * @param requestID
	 * @param lwpcOID
	 * @return
	 */
	public static String getUnknownRequestResumedMessage(long requestID,
			String lwpcOID) {
		return "The consumer [" + lwpcOID + "] resumed the unknown request" +
				" [" + requestID + "]. This message was ignored.";
	}

	/**
	 * @param senderPubKey
	 * @return
	 */
	public static String getUnknownConsumerResumingRequestMessage(String senderPubKey) {
		return "Ignoring an unknown consumer that resumed a request. Sender public key: " + senderPubKey;
	}

	/* Forwarded request messages */
	
	/**
	 * @param requestID
	 * @return
	 */
	public static String getForwardedRequestMessage(long requestID) {
		return "Request "+requestID+": request forwarded to community.";
	}

	/* Remote requesting workers message */
	
	/**
	 * @param wpcOID
	 * @param requestSpec
	 * @return
	 */
	public static String getRequestWorkersMesasge(
			String wpcContainerID,
			RequestSpecification requestSpec) {
		String plural = requestSpec.getRequiredWorkers() > 1 ? "s" : ""; 
		
		return "Request "+requestSpec.getRequestId()+": [" + wpcContainerID + "] requested "
				+ requestSpec.getRequiredWorkers()+" worker"+plural;
	}

	/**
	 * @param wpcOID
	 * @param requestID
	 * @return
	 */
	public static String getNonPositiveWorkerRequestMessage(
			String wpcContainerID,
			long requestID) {
		return "Request "+requestID+": request ignored because [" 
				+ wpcContainerID + "] requested less than 1 worker";
	}

	/**
	 * @param wpcOID
	 * @return
	 */
	public static String getNullRequestSpecMessage(
			String wpcContainerID) {
		return "Client [" + wpcContainerID + "] done a null request. " +
				"This request was ignored.";
	}

	/**
	 * @param requestSpec
	 * @return
	 */
	public static String getRequestWithNoClientMessage(RequestSpecification requestSpec) {
		return "Request "+(requestSpec != null ? requestSpec.getRequestId() : null )+": " +
				"request ignored because it has not a client.";
	}

	/**
	 * @param requestSpec
	 * @return
	 */
	public static String getRequestWithNoPublicKeyMessage(RequestSpecification requestSpec) {
		return "Request "+(requestSpec != null ? requestSpec.getRequestId() : null )+": " +
				"request ignored because it has not a public key.";
	}

	/* Preemption messages */
	
	/**
	 * @param requestID
	 * @param allocableWorker
	 * @return
	 */
	public static String getRequestPreemptionMessage(long requestID,
			String workerContainerID, String consumerContainerID) {
		return "Request "+requestID+": Taking worker [" + workerContainerID + "] from [" + consumerContainerID + "]";
	}

	public static String getRequestIDAlreadyExistsMessage(long id, String lwpcOID) {
		return "Request " + id + ": New request ignored because this request ID " +
		"number is already being used. Local consumer ID: [" + lwpcOID + "]";
	}

	public static String getUserDownMessage(long id, String publickey) {
		return "Request " + id + ": The user with this public key is down: [" + publickey + "]";
	}

	public static String getInvalidCertPathMessage(String workerProviderClientContainerID, long requestId) {
		
		return "Request "+requestId+": request ignored because ["+ workerProviderClientContainerID + "] " +
				" has an invalid Certificate Path.";
	}

	public static String getNonIssuedCertPathMessage(
			String workerProviderClientContainerID, long requestId) {
		return "Request "+requestId+": request ignored because ["+ workerProviderClientContainerID + "] " +
				" has an non issued Certificate Path.";
	}

	public static String getFinishRequestMessage(RequestSpecification requestSpec,
			String lwpcAddress) {
		return "Request " + requestSpec.getRequestId() + " finished by [" + lwpcAddress + "].";
	}
	

}
