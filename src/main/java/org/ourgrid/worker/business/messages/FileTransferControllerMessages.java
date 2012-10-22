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


public class FileTransferControllerMessages {
	
	public static String getClientRequestsToTransferFileWithRepeatedHandleMessage(long handleID, String senderPublicKey) {
		return "The client tried to transfer a file with a repeated handle." +
				" This message was ignored. Client public key: [" + senderPublicKey + "]. Handle: " + handleID + ".";
	}

	public static String getClientRequestsToTranferFileButErrorOccuredOnSolvingPathMessage(String filePath, long fileSize,
			long handleID, String senderPubKey, String cause) {
		return "The client tried to transfer the file, but error occured on solving path. This message was ignored. " +
				"Error cause: [" + cause + "]. File path: [" + filePath + "]. Size: " + fileSize +
				" bytes. Handle: " + handleID + ". Client public key: [" + senderPubKey + "].";
	}

	public static String getClientRequestsToTranferIncomingFileMessage(String filePath, String senderPublicKey) {
		return "The client tried to transfer a file that is being received. This message was ignored." +
						" Client public key: [" + senderPublicKey + "]. File destination: [" + filePath + "].";
	}

	public static String getTransferRequestAcceptedMessage(String filePath, long handleID, String senderPubKey) {
		return "A transfer request from the client was successfully accepted." +
				" Client public key: [" + senderPubKey +"]. File destination: [" + filePath + 
				"]. Handle: " + handleID + ".";
	}

	public static String getIncomingTransferFailedMessage(String filePath, long amountWritten, String senderPubkey) {
		return "Error while trying to receive file from client. Reporting error to client." +
				" Client public key: [" + senderPubkey + "]. Destination path: [" + filePath +
				"]. Amount of data received: " + amountWritten + " bytes.";
	}
	
	public static String getIncomingTransferCompletedMessage(long handleID, long amountWritten,
			String senderPublicKey) {
		return "File successfully received from client. Client public key: [" + senderPublicKey 
				+ "]. Handle: " + handleID + ". Amount of data received: " + amountWritten + " bytes.";
	}

	public static String getWorkerReceivesAFileTransferRejectedMessage(String filePath, long handleID, String senderpublicKey) {
		return "The worker received a file reject message from the client. This message was successfully accepted. " +
				"File path: [" + filePath + "]. Handle: " + handleID + ". Client public key: [" + senderpublicKey + "].";
	}

	public static String getWorkerReceivesAnOutgoingFileTransferFailedMessage(String filePath, long handleID,
			long amountDataUploaded, String senderPublicKey) {
		return "The worker received an outgoing transfer failed message from the client. This message was successfully accepted. " +
				 "File path: [" + filePath + "]. Handle: " + handleID + ". Amount of data uploaded: " + amountDataUploaded + " bytes. " +
					"Client public key: [" + senderPublicKey + "].";
	}

	public static String getWorkerReceivesAnOutgoingFileTransferCancelledMessage(String filePath, long handleID,
			long amountDataUploaded, String senderPublicKey) {
		return "The worker received an outgoing transfer cancelled message from the client. This message was successfully accepted. " +
				 "File path: [" + filePath + "]. Handle: " + handleID + ". Amount of data uploaded: " + amountDataUploaded + " bytes. " +
					"Client public key: [" + senderPublicKey + "].";
	}

	public static String getWorkerReceivesAnOutgoingFileTransferCompletedMessage(String filePath, long handleID,
			long amountDataUploaded, String senderPublicKey) {
		return "The worker received an outgoing transfer completed message from the client. This message was successfully accepted. " +
				 "File path: [" + filePath + "]. Handle: " + handleID + ". Amount of data uploaded: " + amountDataUploaded + " bytes. " +
					"Client public key: [" + senderPublicKey + "].";
	}

	public static String getAllUploadsFinishMessage() {
		return "All current uploading files has been finished.";
	}

	public static String getClientRequestsToRecoverAnUploadingFileMessage(String filePath, String senderPublicKey) {
		return "The client tried to recover the files that already being uploaded. File path: [" + filePath +
				"]. Client public key: [" + senderPublicKey + "].";
	}

	public static String getWorkerStartsFileTransferWithSuccess(String filePath, long handleID, String senderPublicKey) {
		return "The client tried to recover the files. Worker accepted the transfer request and is starting to upload" +
				" the file. File path: [" + filePath + "]. Handle: " + handleID + ". Client public key: [" + senderPublicKey + "].";
	}

	public static String getClientRequestsToRecoverFilesWithNullFilesMessage(String senderPublicKey) {
		return getClientRequestsToRecoverFilesButErrorOccuredOnSolvingPathMessage(null, senderPublicKey,
				EnvironmentControllerMessages.getNullFilePathMessage());
	}

	public static String getClientRequestsToRecoverFilesButThereAreFilesWithSamePathMessage(String filePath, String senderPublicKey) {
		return getClientRequestsToRecoverFilesButErrorOccuredOnSolvingPathMessage(filePath, senderPublicKey,
				"There are files with same path.");
	}

	public static String getClientRequestsToRecoverAnInexistentFileMessage(String filePath, String senderPublicKey) {
		return getClientRequestsToRecoverFilesButErrorOccuredOnSolvingPathMessage(filePath, senderPublicKey,
				EnvironmentControllerMessages.getFileNotFoundMessage());
	}
	
	public static String getClientRequestsToRecoverFilesButErrorOccuredOnSolvingPathMessage(String filePath, String senderPubKey,
			String cause) {
		return "The client tried to recover the files, but a error occured on solving path. This message was ignored. Error cause: [" +
				cause + "]. File path: [" + filePath + "]. Client public key: [" + senderPubKey + "].";
	}

	public static String getClientRequestsToTransferFileWithInvalidTransferDescription(String transferDescription,
			String senderPublicKey) {
		return "The client tried to transfer a file with a invalid transfer description. This message was ignored." +
				" Transfer description: [" + transferDescription + "]. Client public key: [" + senderPublicKey + "].";
	}
	
}
