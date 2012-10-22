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

import java.util.List;

public class WorkerControllerMessages {

	public static String getUnknownClientSendsStartWorkMessage(String brokerID,
			String publicKey) {
		return "The unknown client [" + brokerID
				+ "] tried to start the work of this Worker. "
				+ "This message was ignored. Unknown client public key: ["
				+ publicKey + "].";
	}

	public static String getWorkerDoesNotRequestedAnyTransferMessage(
			String senderPublicKey) {
		return "Worker does not requested any transfer to the client ["
				+ senderPublicKey + "].";
	}

	public static String workerTransferCanNotBeRejectedIsDownloadingMessage() {
		return "The transfer can not be rejected. Worker is downloading a file"
				+ "from the client, not uploading.";
	}

	public static String workerOutgoingTransferCanNotBeCancelledIsDownloadingMessage() {
		return "The outgoing transfer can not be cancelled. Worker is downloading a file"
				+ "from the client, not uploading.";
	}

	public static String workerOutgoingTransferCanNotFailIsDownloadingMessage() {
		return "The outgoing transfer can not fail. Worker is downloading a file"
				+ "from the client, not uploading.";
	}

	public static String workerOutgoingTransferCanNotBeCompletedIsDownloadingMessage() {
		return "The outgoing transfer can not be completed. Worker is downloading a file"
				+ "from the client, not uploading.";
	}

	public static String getCleaningWorkerMessage() {
		return "Cleaning Worker playpen.";
	}

	public static String getPlaypenCreationErrorMessage(String brokerID,
			String playpenDir) {
		return "The client [" + brokerID
				+ "] tried to start the work of this Worker, but "
				+ "the playpen directory [" + playpenDir
				+ "] cannot be created.";
	}

	public static String getSuccessfulStartWorkMessage(String brokerID) {
		return "Worker is ready to start working for client [" + brokerID
				+ "].";
	}

	public static String getUnknownClientTriesToGetFileInfoMessage(
			String filePath, String senderPubKey) {
		return "An unknown client tried to get info about the file ["
				+ filePath + "]. "
				+ "This message was ignored. Unknown client public key: ["
				+ senderPubKey + "].";
	}

	public static String getClientWithoutStartingWorkTriesToGetFileInfoMessage(
			String filePath, String senderPubKey) {
		return "The client requested info about the file [" + filePath + "], "
				+ "but this Worker was not commanded to start the work yet. "
				+ "This message was ignored. Client public key: ["
				+ senderPubKey + "].";
	}

	public static String getFileInfoInAWorkerNotInDownloadingStateMessage(
			String filePath, String senderPubKey) {
		return "The client requested info about the file [" + filePath + "], "
				+ "but this Worker is not in downloading state. "
				+ "This message was ignored. Client public key: ["
				+ senderPubKey + "].";
	}

	public static String getSuccessfulGetFileInfoMessage(String filePath,
			String senderPubKey) {
		return "Client successfully got file info. File: [" + filePath + "]. "
				+ "Client public key: [" + senderPubKey + "].";
	}

	public static String getUnknownClientRequestsToTransferFileMessage(
			String filePath, long fileSize, long handleID, String senderId) {
		return "An unknown client tried to transfer the file [" + filePath
				+ "] with size " + fileSize + " bytes. Handle: " + handleID
				+ ". Unknown client id: [" + senderId + "].";
	}

	public static String getStorageCreationErrorMessage(String brokerID,
			String storageDir) {
		return "The client [" + brokerID
				+ "] tried to start the work of this Worker, but "
				+ "the storage directory [" + storageDir
				+ "] cannot be created.";
	}

	public static String getClientTriesToTransferFileOnUnstartedWorkerMessage(
			String senderPubKey) {
		return "The client tried to transfer a file, but this Worker was not commanded to start the work yet."
				+ " This message was ignored. Client public key: ["
				+ senderPubKey + "].";
	}

	public static String getClientTriedToTransferFileOnExecutionWorkerMessage(
			String senderPubKey) {
		return "The client tried to transfer a file, but this Worker is in executing state."
				+ " This message was ignored. Client public key: ["
				+ senderPubKey + "].";
	}

	public static String getClientTriedToTransferFileOnExecutionFinishedWorkerMessage(
			String senderPubKey) {
		return "The client tried to transfer a file, but this Worker finished "
				+ "the execution. This message was ignored. Client public "
				+ "key: [" + senderPubKey + "].";
	}

	public static String getClientRequestsToTransferFileOnWorkerWithErrorMessage(
			String filePath, long fileSize, long handleID,
			String senderPublicKey) {
		return "The client tried to transfer a file. This message was ignored, because a error already ocurred."
				+ " This message was ignored. File path: ["
				+ filePath
				+ "]. Handle: "
				+ handleID
				+ ". Size: "
				+ fileSize
				+ " bytes."
				+ " Client public key: [" + senderPublicKey + "].";
	}

	public static String getErrorWhileGettingFileInfoMessage(String filePath,
			String senderPubKey, String cause) {
		return "Error occurred while trying to get file INFO. File: ["
				+ filePath + "]. " + "Client public key: [" + senderPubKey
				+ "]. Cause: [" + cause + "].";
	}

	public static String getUnknownClientSendsIncomingTransferFailedMessage(
			String senderPublicKey) {
		return "The worker received an incoming transfer failed message from a unknown client."
				+ " This message was ignored. Client public key: ["
				+ senderPublicKey + "].";
	}

	public static String getUnknownClientSendsIncomingTransferCompletedMessage(
			String senderPublicKey) {
		return "The worker received an incoming transfer completed message from a unknown client."
				+ " This message was ignored. Client public key: ["
				+ senderPublicKey + "].";
	}

	public static String getIncomingTranferFailedWithUnknownHandleMessage(
			long handleID, String senderPublicKey) {
		return "The Worker received an incoming transfer failed message with unknown handle."
				+ " Client public key: ["
				+ senderPublicKey
				+ "]. Handle: "
				+ handleID + ".";
	}

	public static String getIncomingTranferCompletedWithUnknownHandleMessage(
			long handleID, String senderPublicKey) {
		return "The Worker received an incoming transfer completed message with unknown handle."
				+ " Client public key: ["
				+ senderPublicKey
				+ "]. Handle: "
				+ handleID + ".";
	}

	public static String getWorkerWithErrorReceivesAnIncomingTransferFailedMessage(
			long handleID, long amountWritten, String senderPublicKey) {
		return "The worker received an incoming transfer failed message. This message was ignored, because an error already ocurred. "
				+ "Handle: "
				+ handleID
				+ ". Amount of data uploaded: "
				+ amountWritten
				+ " bytes. Client public key: ["
				+ senderPublicKey + "].";
	}

	public static String getWorkerWithErrorReceivesAnIncomingTransferCompletedMessage(
			long handleID, long amountWritten, String senderPublicKey) {
		return "The worker received an incoming transfer completed message. This message was ignored, because a error already ocurred. "
				+ "Handle: "
				+ handleID
				+ ". Amount of data uploaded: "
				+ amountWritten
				+ " bytes. Client public key: ["
				+ senderPublicKey + "].";
	}

	public static String getUnknownClientTriesToRemoteExecuteMessage(
			String command, String senderPublicKey) {
		return "An unknown client tried to execute the command [" + command
				+ "]. " + "Unknown client public key: [" + senderPublicKey
				+ "].";
	}

	public static String getRemoteExecuteInANonWorkingWorkerMessage(
			String command, String senderPublicKey) {
		return "A client tried to execute the command [" + command
				+ "] but did not send startwork message. "
				+ "Client public key: [" + senderPublicKey + "].";
	}

	public static String getRemoteExecuteInANExecutionFinishedWorkerMessage(
			String command, String senderPublicKey) {
		return "A client tried to execute the command [" + command
				+ "] but the worker finished the execution. "
				+ "Client public key: [" + senderPublicKey + "].";
	}

	public static String getRemoteExecuteInAnAlreadyExecutingWorkerMessage(
			String command, String senderPublicKey) {
		return "A client tried to execute the command [" + command
				+ "] but is already executing. " + "Client public key: ["
				+ senderPublicKey + "].";
	}

	public static String getUnknownClientRequestsToRecoverFilesMessage(
			List<String> filePath, String senderPubKey) {
		return "An unknown client tried to recover the files " + filePath
				+ ". Unknown client public key: [" + senderPubKey + "].";
	}

	public static String getClientRequestsToRecoverFilesOnUnstartedWorkerMessage(
			List<String> filePath, String senderPubKey) {
		return "A client tried to recover the files "
				+ filePath
				+ ", but this Worker was not commanded to start "
				+ "the work yet. This message was ignored. Client public key: ["
				+ senderPubKey + "].";
	}

	public static String getClientRequestsToRecoverFilesBeforeExecutionFinishMessage(
			List<String> filePath, String senderPubKey) {
		return "A client tried to recover the files "
				+ filePath
				+ ", but the Broker cannot download files before the execution finish."
				+ " This message was ignored. Client public key: ["
				+ senderPubKey + "].";
	}

	public static String getClientRequestsToRecoverFilesOnWorkerWithErrorMessage(
			List<String> filePath, String senderPublicKey) {
		return "A client tried to recover the files " + filePath
				+ ". This message was ignored, because an error already"
				+ " ocurred. Client public key: [" + senderPublicKey + "].";
	}

	public static String getWorkerReceivesAFileTransferRejectedWithUnknownHandleMessage(
			long handleID, String senderPublicKey) {
		return "The worker received a file reject message with unknown handle. This message was ignored. Handle: "
				+ handleID + ". Client public key: [" + senderPublicKey + "].";
	}

	public static String getWorkerWithErrorReceivesAFileTransferRejectedMessage(
			String filePath, long handleID, String senderPublicKey) {
		return "The worker received a file reject message from the client. This message was ignored, because an error already"
				+ " ocurred. File path: ["
				+ filePath
				+ "]. Handle: "
				+ handleID + ". Client public key: [" + senderPublicKey + "].";
	}

	public static String getWorkerReceivesAnOutgoingFileTransferCancelledWithUnknownHandleMessage(
			long handleID, long amountDataUploaded, String senderPublicKey) {
		return "The worker received an outgoing transfer cancelled message with unknown handle. This message was ignored."
				+ " Handle: "
				+ handleID
				+ ". Amount of data uploaded: "
				+ amountDataUploaded
				+ " bytes. Client public key: ["
				+ senderPublicKey + "].";
	}

	public static String getWorkerWithErrorReceivesAnOutgoingFileTransferCancelledMessage(
			String filePath, long handleID, long amountDataUploaded,
			String senderPublicKey) {
		return "The worker received an outgoing transfer cancelled message from the client. This message was ignored,"
				+ " because a error already ocurred. File path: ["
				+ filePath
				+ "]. Handle: "
				+ handleID
				+ ". Amount of data uploaded: "
				+ amountDataUploaded
				+ " bytes. Client public key: ["
				+ senderPublicKey + "].";
	}

	public static String getWorkerReceivesAnOutgoingFileTransferFailedWithUnknownHandleMessage(
			long handleID, long amountDataUploaded, String senderPublicKey) {
		return "The worker received an outgoing transfer failed message with unknown handle. This message was ignored."
				+ " Handle: "
				+ handleID
				+ ". Amount of data uploaded: "
				+ amountDataUploaded
				+ " bytes. Client public key: ["
				+ senderPublicKey + "].";
	}

	public static String getWorkerWithErrorReceivesAnOutgoingFileTransferFailedMessage(
			String filePath, long handleID, long amountDataUploaded,
			String senderPublicKey) {
		return "The worker received an outgoing transfer failed message from the client. This message was ignored,"
				+ " because an error already ocurred. File path: ["
				+ filePath
				+ "]. Handle: "
				+ handleID
				+ ". Amount of data uploaded: "
				+ amountDataUploaded
				+ " bytes. Client public key: ["
				+ senderPublicKey + "].";
	}

	public static String getWorkerReceivesAnOutgoingFileTransferCompletedWithUnknownHandleMessage(
			long handleID, long amountDataUploaded, String senderPublicKey) {
		return "The worker received an outgoing transfer completed message with unknown handle. This message was ignored."
				+ " Handle: "
				+ handleID
				+ ". Amount of data uploaded: "
				+ amountDataUploaded
				+ " bytes. Client public key: ["
				+ senderPublicKey + "].";
	}

	public static String workerReceivedAnOutGoingTransferCanceledAndDoNotStartedTheUploadMessage(
			String senderPublicKey) {
		return "The worker received an outgoing transfer canceled message and do not started the upload. This message was ignored."
				+ "Client public key: [" + senderPublicKey + "].";
	}

	public static String getWorkerWithErrorReceivesAnOutgoingFileTransferCompletedMessage(
			String filePath, long handleID, long amountDataUploaded,
			String senderPublicKey) {
		return "The worker received an outgoing transfer completed message from the client. This message was ignored,"
				+ " because an error already ocurred. File path: ["
				+ filePath
				+ "]. Handle: "
				+ handleID
				+ ". Amount of data uploaded: "
				+ amountDataUploaded
				+ " bytes. Client public key: ["
				+ senderPublicKey + "].";
	}

	public static String getRemoteExecuteOnWorkerWithErrorMessage(
			String newCommand, String senderPublicKey) {
		return "A client tried to execute the command ["
				+ newCommand
				+ "]. This message was ignored, because a error already ocurred. "
				+ "Client public key: [" + senderPublicKey + "].";
	}

	public static String getBuildExecutorWithoutContextMessage() {
		return "Cannot build an executor without a context.";
	}

	public static String getInvalidCertPathMessage(String brokerID) {

		return "Start work message was ignored because [" + brokerID + "] "
				+ " has an invalid Certificate Path.";
	}

	public static String getNonIssuedCertPathMessage(String brokerID) {
		return "Start work message was ignored because [" + brokerID + "] "
				+ " has an non issued Certificate Path.";
	}

	public static String getBrokerIsNotOnVomsListMessage(String brokerID) {
		return "Start work message was ignored because [" + brokerID + "]"
				+ " is not authorised.";
	}

	public static String getWorkerIsNotInWorkingStateIncomingTrasferFailedMessage(
			String senderPublicKey) {
		return "Worker is not started and the incoming transfer from " + "["
				+ senderPublicKey + "] failed.";
	}

	public static String getWorkerIsStillWorkingIncomingTransferFailedMessage(
			String senderPublicKey) {
		return "Worker is still working and the incoming transfer from " + "["
				+ senderPublicKey + "] failed.";
	}

	public static String getWorkerIsNotInWorkingStateIncomingTrasferCompleteMessage(
			String senderPublicKey) {
		return "An unstarted worker received an incoming transfer completed message from a client."
				+ " This message was ignored. Client public key: ["
				+ senderPublicKey + "]";
	}

	public static String getWorkerIsNotInWorkingStateTrasferRejectedMessage(
			String senderPublicKey) {
		return "An unstarted worker received a transfer rejected message. This message was ignored."
				+ ". Client public key: [" + senderPublicKey + "].";
	}

	public static String getWorkerIsNotInWorkingStateTrasferCancelledMessage(
			String senderPublicKey) {
		return "An unstarted worker received a transfer cancelled message. This message was ignored."
				+ ". Client public key: [" + senderPublicKey + "].";
	}

	public static String getWorkerIsNotInWorkingStateTrasferFailedMessage(
			String senderPublicKey) {
		return "An unstarted worker received a transfer failed message. This message was ignored."
				+ ". Client public key: [" + senderPublicKey + "].";
	}

	public static String getWorkerIsNotInWorkingStateTrasferCompletedMessage(
			String clientPublicKey) {
		return "An unstarted worker received a transfer failed message. This message was ignored."
				+ ". Client public key: [" + clientPublicKey + "].";
	}

	public static String getTryToDoWorkerClientIsUpMessage() {
		return "The worker received a workerClientIsUp message. This message was ignored.";
	}

	public static String getExecutorExceptionMessage() {
		return "Failure to execute command on Executor";
	}
}
