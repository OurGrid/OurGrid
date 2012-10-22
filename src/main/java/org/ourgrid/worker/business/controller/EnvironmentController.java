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
package org.ourgrid.worker.business.controller;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.ourgrid.common.exception.UnableToDigestFileException;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.util.JavaFileUtil;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.exception.UnableToCreatePlaypenException;
import org.ourgrid.worker.business.exception.UnableToCreateStorageException;
import org.ourgrid.worker.business.messages.EnvironmentControllerMessages;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.utils.RandomNumberUtil;

import br.edu.ufcg.lsd.commune.network.signature.Util;


/**
 * Controls this worker's environment.
 */
public class EnvironmentController {

	private static EnvironmentController instance;
	
	@Req("REQ079")
	/**
	 * Builds a new Environment Controller. 
	 */
	public static synchronized EnvironmentController getInstance() {
		if (instance == null) {
			instance = new EnvironmentController();
		}
		return instance;
	}
	
	@Req("REQ079")
	/**
	 * Creates the playpen directory. This is a random directory,
	 * so the chance the same directory is picked twice is very low.
	 * @throw UnableToCreatePlaypenException In case the playpen root cannot be created
	 * or is read-only; or the directory picked already exists, cannot be created or is read-only.
	 */
	public void mountPlaypen(String playpenRoot) throws UnableToCreatePlaypenException {
		
		String playpenDirPath = playpenRoot + File.separator + generatePlaypenDir();
		
		File playpenRootFile = new File(playpenRoot);
		if(!playpenRootFile.exists()) {
			boolean rootSuccessfulCreation = playpenRootFile.mkdirs();
			
			if(!rootSuccessfulCreation) {
				throw new UnableToCreatePlaypenException(playpenDirPath);
			}
		} else {
			if(!playpenRootFile.canWrite()) {
				throw new UnableToCreatePlaypenException(playpenDirPath);
			}
		}
		
		File playpenDir = new File(playpenDirPath);
		if(playpenDir.exists()) {
			throw new UnableToCreatePlaypenException(playpenDirPath);
		}
		
		boolean dirSuccessfulCreation = playpenDir.mkdir(); 
		
		if(!dirSuccessfulCreation) {
			throw new UnableToCreatePlaypenException(playpenDirPath);
		}
		
		if(!playpenDir.canWrite()) {
			throw new UnableToCreatePlaypenException(playpenDirPath);
		}

		WorkerDAOFactory.getInstance().getEnvironmentDAO().setPlaypenDir(playpenDirPath);
	}
	
	@Req("REQ079")
	/**
	 * Destroys the playpen directory.
	 * This operation is also known as <code>WORKER CLEANING</code>.
	 */
	public void unmountPlaypen(List<IResponseTO> responses) {
		
		try {
			String playpenDir = WorkerDAOFactory.getInstance().getEnvironmentDAO().getPlaypenDir();
			WorkerDAOFactory.getInstance().getEnvironmentDAO().setPlaypenDir(null);
			
			if (playpenDir == null) {
				return;
			}

			responses.add(new LoggerResponseTO(WorkerControllerMessages.getCleaningWorkerMessage(), LoggerResponseTO.DEBUG));
			
			boolean successfulDestruction = destroyPlaypen(playpenDir);
			
			if(!successfulDestruction) {
				responses.add(new LoggerResponseTO(EnvironmentControllerMessages
						.getUnsuccesfulPlaypenDirDeletionMessage(playpenDir), LoggerResponseTO.ERROR));
			}
		} catch (IOException e) {
			responses.add(new LoggerResponseTO(EnvironmentControllerMessages
					.getUnsuccesfulPlaypenDirDeletionMessage(WorkerDAOFactory.getInstance().getEnvironmentDAO().getPlaypenDir()), LoggerResponseTO.ERROR, e));
			
		}
	}
	
	@Req("REQ082")
	/**
	 * Unmounts the playpen and sets the storage directory to <code>null</code>.
	 */
	public void unmountEnvironment(List<IResponseTO> responses) {
		unmountPlaypen(responses);
		WorkerDAOFactory.getInstance().getEnvironmentDAO().setStorageDir(null);
	}
	
	@Req({"REQ079", "REQ082"})
	/**
	 * Creates the storage directory. This directory is specific for
	 * every consumer this worker has worked for, and the chance it can
	 * be repeated is very low. That means the directory will be the same
	 * every time this worker works for the same consumer.
	 * @throws UnableToCreateStorageException In case the storage directory cannot be determined,
	 * or either the storage root or the storage directory cannot be created or is read-only.
	 */
	public void mountStorage(String consumerPubKey, String storageRoot) throws UnableToCreateStorageException {
		
		String storageDirPath;
		try {
			storageDirPath = storageRoot + File.separator + generateStorageDir(consumerPubKey);
		} catch (NoSuchAlgorithmException e) {
			throw new UnableToCreateStorageException(null);
		}
		
		File storageRootFile = new File(storageRoot);
		if(!storageRootFile.exists()) {
			boolean rootSuccessfulCreation = storageRootFile.mkdirs();
			
			if(!rootSuccessfulCreation) {
				throw new UnableToCreateStorageException(storageDirPath);
			}
		} else {
			if(!storageRootFile.canWrite()) {
				throw new UnableToCreateStorageException(storageDirPath);
			}
		}
		
		File storageDir = new File(storageDirPath);
		if(!storageDir.exists()) {
			boolean dirSuccessfulCreation = storageDir.mkdir();
			
			if(!dirSuccessfulCreation) {
				throw new UnableToCreateStorageException(storageDirPath);
			}
		}
		
		if(!storageDir.canWrite()) {
			throw new UnableToCreateStorageException(storageDirPath);
		}

		WorkerDAOFactory.getInstance().getEnvironmentDAO().setStorageDir(storageDir.getAbsolutePath());
	}
	
	private String generateStorageDir(String stringToBeHashed) throws NoSuchAlgorithmException {
		
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(stringToBeHashed.getBytes());
		byte[] hashedKey = digest.digest();
		
		return Util.encodeArrayToHexadecimalString(hashedKey);
	}

	@Req("REQ079")
	private boolean destroyPlaypen(String playpenPath) throws IOException {
		
		boolean successful = true;

		File file = new File(playpenPath);
		if(file.exists() && file.isDirectory()) {
			successful = successful && deleteFilesInDir(file);
			successful = successful && file.delete();
		}
		
		return successful;
	}

	/**
	 * Removes all the files in a specified directory.
	 * 
	 * @param directory The directory from which all the files will be removed.
	 * @throws IOException if any I/O problem happens in the deletion process.
	 */
	@Req("REQ079")
	private boolean deleteFilesInDir(File directory) throws IOException {

		boolean successful = true;
		
		File[] files = directory.listFiles();
		
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					successful = successful && deleteFilesInDir(file);
				}
				successful = successful && file.delete();
			}
		}
		
		return successful;
		
	}
	
	@Req("REQ079")
	private String generatePlaypenDir() {
		long randomNumber =  (long) (RandomNumberUtil.getRandom() * Long.MAX_VALUE);
		randomNumber = Long.signum(randomNumber) == -1 ? randomNumber * (-1) : randomNumber;
		
		return "worker-" + randomNumber;
	}
	
	@Req({"REQ082"})
	public String solveStorageDir(String filePath) throws IOException {
		return solveDir(filePath, WorkerConstants.ENV_STORAGE, WorkerDAOFactory.getInstance().getEnvironmentDAO().getStorageDir());
	}
	
	@Req("REQ080")
	public String solvePlaypenDir(String filePath) throws IOException {
		return solveDir(filePath, WorkerConstants.ENV_PLAYPEN, WorkerDAOFactory.getInstance().getEnvironmentDAO().getPlaypenDir());
	}
	
	@Req("REQ082")
	public String getFileDigest(String filePath) throws UnableToDigestFileException {
		File file = new File(filePath);
		if(!file.exists() || file.isDirectory()) {
			return "0";  // Digest from a directory.
		}
		return JavaFileUtil.getDigestRepresentation(file);
	}
	
	
	@Req("REQ080")
	public String solveDir(String filePath) throws IOException {
		if (filePath != null && filePath.contains("$" + WorkerConstants.ENV_STORAGE)) {
			return solveStorageDir(filePath);
		}
		return solvePlaypenDir(filePath);
	}

	@Req({"REQ080", "REQ082"})
	public String solveDir(String filePath, String relativePath, String absolutePath) throws IOException {
		
		if(filePath == null) {
			throw new IOException(EnvironmentControllerMessages.getNullFilePathMessage());
		}
		
		if(!checkFilePathVar(filePath, relativePath)) {
			throw new IOException(EnvironmentControllerMessages.getInvalidVariableFoundMessage());
		}
		
		String solvedPath = null;
			
		if (filePath.indexOf("$") == 0) {
			solvedPath = filePath.replace("$" + relativePath, absolutePath);
		} else {
			/*
			 * Any relative or absolute path will become relative to the
			 * variable value
			 */
			solvedPath = absolutePath + File.separator + filePath;
		}

		if (!isRelativeTo(solvedPath, absolutePath)) {
			throw new IOException(EnvironmentControllerMessages.getNotRelativeFilePathMessage(absolutePath));
		}
		
		File file = new File(solvedPath);
		if(file.exists() && !file.canRead()) {
			throw new IOException(EnvironmentControllerMessages.getUnreadableFileInfoMessage());
		}
		
		return file.getCanonicalPath();
	}
	
	/**
	 * If file path does not contain any $VARIABLE, return <code>true</code>;
	 * If file path contains a $VARIABLE, it has to start with $ALLOWED_VARIABLE and cannot
	 * contain any additional variable.
	 */
	@Req("REQ080")
	private boolean checkFilePathVar(String filePath, String allowedVariable) {
		int firstDollar = filePath.indexOf("$");
		if(firstDollar == -1){
			return true;
		}
		return filePath.startsWith("$" + allowedVariable) && 
									(firstDollar == filePath.lastIndexOf("$"));
	}

	@Req("REQ080")
	private boolean isRelativeTo(String filePath, String directory) throws IOException {

		final String fileCanPath = new File(filePath).getCanonicalPath();
		final String dirCanPath = new File(directory).getCanonicalPath();

		return fileCanPath.startsWith(dirCanPath);
	}

	public boolean isCleaning(String playpenRoot) {
		File playpenRootFile = new File(playpenRoot);
		
		if (!playpenRootFile.isDirectory()) {
			return true;
		}
		return playpenRootFile.listFiles().length == 0;
	}

}
