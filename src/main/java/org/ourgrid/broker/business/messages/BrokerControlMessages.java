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

import org.ourgrid.common.specification.job.JobSpecification;

/**
 *
 */
public class BrokerControlMessages {

	public static String getComponentNotStartedMessage() {
		return "Broker control was not started.";
	}
	
	public static String getTryingToStartBrokerMessage() {
		return "Trying to start a broker component.";
	}

	/**
	 * @param jobSpec
	 * @param jobID
	 * @return
	 */
	public static String getJobAddedMessage(JobSpecification jobSpec, int jobID) {
		return "Job [" + jobID + "] was added, with " + jobSpec.getTaskSpecs().size() + " tasks";
	}

	/**
	 * @param jobID
	 * @return
	 */
	public static String getJobCancelledMessage(int jobID) {
		return "Job [" + jobID  + "] was cancelled.";
	}

	/**
	 * @param jobID
	 * @return
	 */
	public static String getNoSuchJobToCancelMessage(int jobID) {
		return "Job [" + jobID  + "] was not cancelled, there is no job with such id.";
	}

	/**
	 * @param monitorableStub
	 * @return
	 */
	public static String getPeerIsUpMessage(String deploymentID) {
		return "Peer with deployment id: [" + deploymentID + "] is UP.";
	}

	public static String getPeerIsDownMessage(String deploymentID) {
		return "Peer with deployment id: [" + deploymentID + "] is DOWN.";
	}

	/**
	 * @param monitorable
	 * @return
	 */
	public static String getNoPeerWithSuchEntityIDMessage(String deploymentID) {
		return "There is no Peer Entry with deployment id: [" + deploymentID + "]";
	}
	
	/**
	 * @param monitorable
	 * @return
	 */
	public static String getPeerAlreadyDownMessage(String deploymentID) {
		return "The Peer Entry with deployment id: [" + deploymentID + "] is already down.";
	}

	public static String getUnknownSenderControllingBrokerMessage(
			String senderPublicKey) {
		return "An unknown entity tried to perform a control operation on the Broker. " +
			"Only the local modules can perform this operation. " +
			"Unknown entity public key: [" + senderPublicKey + "].";
	}

	public static String getSuccessfullyStartedBrokerMessage() {
		return "Broker has been successfully started.";
	}
	
	public static String getSuccessfullyShutdownBrokerMessage() {
		return "Broker has been successfully shutdown.";
	}
	
	public static String getJobStillRunningMessage(int jobID) {
		return "Job with ID " + jobID + " is still in execution";
	}
	
	public static String getStartingBrokerMessage() {
		return "Starting the broker.";
	}

	public static String getTryingToStopBrokerMessage() {
		return "Trying to stop broker component.";
	}
}
