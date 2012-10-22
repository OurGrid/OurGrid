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
package org.ourgrid.acceptance.util.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.easymock.classextension.EasyMock;
import org.hibernate.hql.ast.InvalidPathException;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.broker.communication.actions.ErrorOcurredMessageHandle;
import org.ourgrid.common.exception.UnableToDigestFileException;
import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.matchers.ErrorOcurredMessageHandleMatcher;
import org.ourgrid.matchers.GetFileInfoMessageHandleMatcher;
import org.ourgrid.matchers.HereIsFileInfoMessageHandleMatcher;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.business.controller.GridProcessError;
import org.ourgrid.worker.communication.processors.handle.GetFileInfoMessageHandle;

import sun.misc.BASE64Encoder;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

@SuppressWarnings("restriction")
public class Req_082_Util extends WorkerAcceptanceUtil {

	public Req_082_Util(ModuleContext context) {
		super(context);
	}

	public void getFileInfoByUnknownClient(WorkerComponent component,
			String filePath, String senderPubKey) {
		getFileInfo(component, null, filePath, filePath, senderPubKey, false,
				false, null, false, false, null);
	}

	public void getFileInfoByClientWithoutStartingWork(
			WorkerComponent component, String filePath, String senderPubKey) {
		getFileInfo(component, null, filePath, filePath, senderPubKey, true,
				false, null, false, false, null);
	}

	public void getFileInfoByClientWorkerRemoteWorking(
			WorkerComponent component, String filePath, String senderPubKey,
			String fileDigest) {
		getFileInfo(component, null, filePath, filePath, senderPubKey, true,
				true, null, true, false, fileDigest);
	}

	public void getFileInfoSuccessfully(WorkerComponent component,
			WorkerClient workerClient, String workerClientPublicKey,
			String filePath, String fileDigest) {
		getFileInfo(component, workerClient, filePath, filePath,
				workerClientPublicKey, true, true, null, true, true, fileDigest);
	}

	public void getFileInfoWithoutIncomingFile(WorkerComponent component,
			WorkerClient workerClient, String workerClientPublicKey,
			String relativeFilePath, String absoluteFilePath, String fileDigest) {

		getFileInfo(component, workerClient, relativeFilePath,
				absoluteFilePath, workerClientPublicKey, true, true, null,
				true, false, fileDigest);
	}

	public void getFileInfoWithIncomingFile(WorkerComponent component,
			WorkerClient workerClient, String workerClientPublicKey,
			String relativeFilePath, String absoluteFilePath, String fileDigest) {

		getFileInfo(component, workerClient, relativeFilePath,
				absoluteFilePath, workerClientPublicKey, true, true, null,
				true, true, fileDigest);
	}

	public void getNullFileInfo(WorkerComponent component,
			WorkerClient workerClient, String workerClientPublicKey) {
		getFileInfoWithError(component, workerClient, workerClientPublicKey,
				null, "File path is null.");
	}

	public void getFilePathInfoWithInvalidVariable(WorkerComponent component,
			WorkerClient workerClient, String workerClientPublicKey,
			String filePath) {
		getFileInfoWithError(component, workerClient, workerClientPublicKey,
				filePath, "Invalid variable found.");
	}

	public void getFileInfoForNotRelativeFilePath(WorkerComponent component,
			WorkerClient workerClient, String workerClientPublicKey,
			String filePath, String absolutePath) {
		getFileInfoWithError(component, workerClient, workerClientPublicKey,
				filePath, "File path is not relative to " + absolutePath
						+ " directory.");
	}

	public void getUnreadableFileInfo(WorkerComponent component,
			WorkerClient workerClient, String workerClientPublicKey,
			String filePath) {
		getFileInfoWithError(component, workerClient, workerClientPublicKey,
				filePath, "File cannot be read.");
	}

	private void getFileInfoWithError(WorkerComponent component,
			WorkerClient workerClient, String workerClientPublicKey,
			String filePath, String cause) {
		getFileInfo(component, workerClient, filePath, filePath,
				workerClientPublicKey, true, true, cause, false, false, null);
	}

	private void getFileInfo(WorkerComponent component,
			WorkerClient workerClient, String relativeFilePath,
			String absoluteFilePath, String senderPubKey,
			boolean isClientKnown, boolean hasClientStartedWork,
			String errorCause, boolean isFilePathValid, boolean isIncomingFile,
			String fileDigest) {

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		if (workerClient != null) {
			EasyMock.reset(workerClient);
		}

		if (!isClientKnown) {
			newLogger
					.warn("An unknown client tried to get info about the file ["
							+ relativeFilePath
							+ "]. "
							+ "This message was ignored. Unknown client public key: ["
							+ senderPubKey + "].");
		} else {
			if (!hasClientStartedWork) {
				newLogger
						.warn("The client requested info about the file ["
								+ relativeFilePath
								+ "], "
								+ "but this Worker was not commanded to start the work yet. "
								+ "This message was ignored. Client public key: ["
								+ senderPubKey + "].");
			} else if (!isIncomingFile) {
				newLogger.warn("The client requested info about the file ["
						+ relativeFilePath + "], "
						+ "but this Worker is not in downloading state. "
						+ "This message was ignored. Client public key: ["
						+ senderPubKey + "].");
			} else {
				if (isFilePathValid) {
					try {
						workerClient
						.sendMessage(HereIsFileInfoMessageHandleMatcher
								.eqMatcher(null, new FileInfo(
										relativeFilePath, fileDigest)));
						newLogger.debug("Client successfully got file info. "
								+ "File: ["
								+ new File(absoluteFilePath).getCanonicalPath()
								+ "]. " + "Client public key: [" + senderPubKey
								+ "].");
					} catch (Exception e) {
						new InvalidPathException(e.toString());

					}
				} else {
					newLogger
							.warn("Error occurred while trying to get file INFO. "
									+ "File: ["
									+ relativeFilePath
									+ "]. Client public key: ["
									+ senderPubKey
									+ "]. " + "Cause: [" + errorCause + "].");

					workerClient
							.sendMessage(ErrorOcurredMessageHandleMatcher
									.eqMatcher(new ErrorOcurredMessageHandle(
											new GridProcessError(
													GridProcessErrorTypes.APPLICATION_ERROR))));
				}
			}
		}

		if (workerClient != null) {
			EasyMock.replay(workerClient);
		}

		EasyMock.replay(newLogger);

		Worker worker = getWorker();
		ObjectDeployment workerOD = getWorkerDeployment();

		AcceptanceTestUtil.setExecutionContext(component, workerOD,
				senderPubKey);

		OutgoingHandle handle = getOutgoingTransferHandle(component,
				relativeFilePath);

		if (handle != null) {
			GetFileInfoMessageHandle getFileInfoMessageHandle = new GetFileInfoMessageHandle(
					handle.getId(), 0, relativeFilePath);
			worker.sendMessage(GetFileInfoMessageHandleMatcher
					.eqMatcher(getFileInfoMessageHandle));
		} else {
			worker.sendMessage(new GetFileInfoMessageHandle(0, 0,
					relativeFilePath));
		}

		EasyMock.verify(newLogger);

		if (workerClient != null) {
			EasyMock.verify(workerClient);
			EasyMock.reset(workerClient);
			EasyMock.replay(workerClient);
		}

		EasyMock.reset(newLogger);
		component.setLogger(oldLogger);
	}

	public static String getFileDigest(String filePath)
			throws UnableToDigestFileException {
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory()) {
			return "0";
		}
		return getDigestRepresentation(file);
	}

	/**
	 * That utility method get a File object in applying a Message Digest
	 * Filter, the result is a digest string representation of the file contents
	 * 
	 * @param fileToDigest
	 *            The File object abstraction that denotes a file to be digested
	 * @return The digest string representation of the file contents. Or null if
	 *         some exception occurs,
	 * @throws UnableToDigestFileException
	 *             If there is any problem on the digest generation, like the
	 *             file is not found, I/O errors or the digest algorithm is not
	 *             valid.
	 */
	private static String getDigestRepresentation(File fileToDigest)
			throws UnableToDigestFileException {

		/** *** Declarations **** */

		/*
		 * A message digest to process the input file and generate a hashcode
		 * for it
		 */
		MessageDigest messageDigest;

		/* The FileInputStream used to read the input file */
		FileInputStream inputStream = null;

		/* The size of each read of the input while generating the digest */
		byte[] buffer = new byte[8129];

		/* The number of bytes to be used on the digest update() */
		int numberOfBytes;

		/* An array of bytes representing the result digest value */
		byte[] digestValue;

		/* An encoder to convert the digest to a readable String */
		BASE64Encoder encoder;

		/* A readable representation of the digest */
		String fileHash = new String();

		/** *** Calculating the digest **** */

		try {
			messageDigest = MessageDigest.getInstance("MD5"); /*
															 * MD5 is the
															 * Message Digest
															 * Algorithm
															 */

			inputStream = new FileInputStream(fileToDigest.getAbsoluteFile());
			numberOfBytes = inputStream.read(buffer);

			while (numberOfBytes != -1) {
				messageDigest.update(buffer, 0, numberOfBytes);
				numberOfBytes = inputStream.read(buffer);
			}

			/* generating the digest */
			digestValue = messageDigest.digest();

			/* make the digest a readable string */
			encoder = new BASE64Encoder();
			fileHash = encoder.encode(digestValue);

		} catch (IOException exception) {
			throw new UnableToDigestFileException(
					fileToDigest.getAbsolutePath(), exception);
		} catch (NoSuchAlgorithmException exception) {
			throw new UnableToDigestFileException(
					fileToDigest.getAbsolutePath(), exception);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {

				}
			}
		}

		return fileHash;
	}

}