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
package org.ourgrid.common.executor;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.common.util.TempFileManager;

public class FolderBasedSandboxedUnixEnvironmentUtil{

	private static transient final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FolderBasedSandboxedUnixEnvironmentUtil.class );

	@SuppressWarnings("unchecked")
	public void copyStorageFiles(File sourceStorageDir, File destStorageDir) throws IOException {
		
		if (sourceStorageDir == null || !sourceStorageDir.exists() || !sourceStorageDir.isDirectory()) {
			throw new IOException("Storage directory does not exist.");
		}

		Collection<File> storageFiles = FileUtils.listFiles(sourceStorageDir, null, true);
		
		String storagePath = sourceStorageDir.getAbsolutePath();
		LOG.debug("Storage path: " + storagePath);
		StringBuffer sb = new StringBuffer("Storage files:\n");
		for (File file : storageFiles) {
			sb.append(file.getAbsolutePath() + "\n");
		}
		LOG.debug(sb.toString());
		
		if (!destStorageDir.exists()) {
			destStorageDir.mkdirs();
		}
		
		for (File file : storageFiles) {
			String destination = file.getAbsolutePath().replace(storagePath, "");
			File vmStoredFile = new File(destStorageDir + File.separator + destination);
			FileUtils.copyFile(file, vmStoredFile);
			LOG.debug("Held stored file copied to: " + vmStoredFile + " vm storage directory ");
		}
	}

	/**
	 * A script created to run the command passed as a param. This is
	 * the script that will be executed in an Unix secure environment.
	 * 
	 * @param command
	 * @param dirName
	 * @param envVars
	 * @return
	 * @throws ExecutorException
	 */
	public File createScript( String command, String dirName, Map<String, String> envVars ) throws ExecutorException {
		
		File dir = new File( dirName ); // The execution directory
		File commandFile = null; // The abstraction for the command
		boolean isScript = false; // Indicate if the command is already an script

		if ( dirName == null ) {// Check if dir is not null. Convert to "." to void problems
			dirName = ".";
		} else if ( !dirName.equals( "." ) && !dir.isDirectory() ) {
			throw new ExecutorException( command, new FileNotFoundException( dir.getAbsolutePath() ) );
		}

		/*
		 * try to figure out if command is already a script note that this is
		 * incomplete as it only works if the script is in dirName
		 */
		try {
			commandFile = new File( dir, command );
			if ( commandFile.exists() && commandFile.canRead() ) {
				FileInputStream commandFIS = new FileInputStream( commandFile );
				DataInputStream commandDIS = new DataInputStream( commandFIS );
				if ( commandDIS.readChar() == '#' && commandDIS.readChar() == '!' ) {
					isScript = true;
				}
				/** Close to avoid the "Too many open files" message and release resources */
				commandFIS.close();
				commandDIS.close();
			}
		} catch ( FileNotFoundException e ) {
			throw new ExecutorException( command, new FileNotFoundException( commandFile.getAbsolutePath() ) );
		} catch ( IOException e ) {
			throw new ExecutorException( command, e );
		}

		File temp;
		BufferedWriter writerTemp;
		String exportCommand = "export ";

		/** Try create the temporary script in fact.*/
		try {
			temp = TempFileManager.createTempFile( "broker", ".tmp", dir );
			writerTemp = new BufferedWriter( new FileWriter( temp ) );

			/* If the command does not need any kind of environment variables */
			if ( envVars != null ) {
				if ( !envVars.isEmpty() ) {
					for (String key : envVars.keySet()) {
						writerTemp.write( key + "=\'" + envVars.get( key ) + "\'" );
						writerTemp.newLine();
						exportCommand = exportCommand + " " + key;
					}
				}
			}

			writerTemp.write( "PATH=$PATH:$PLAYPEN:$STORAGE:." );
			writerTemp.newLine();
			exportCommand = exportCommand + " PATH";
			writerTemp.write( exportCommand );
			writerTemp.newLine();

			if ( isScript ) {
				writerTemp.write( "sh " );
			}
			
			// put the command in the script redirecting its output to files.
			// Ex. "ls -la > outputfile 2>errorfile"
			writerTemp.write( command );
			writerTemp.newLine();
			writerTemp.flush();
			writerTemp.close();

			return temp;

		} catch ( IOException ioe ) {
			throw new ExecutorException( ioe );
		}
	}
	
	/**
	 * @param result
	 * @param fileStdOutPut
	 * @param fileStdError
	 * @param fileExitValue
	 * @throws IOException
	 */
	public void catchOutputFromFile(ExecutorResult result, File fileStdOutPut, File fileStdError, File fileExitValue) throws IOException {
		//The String Buffer that will contain the outputs from the files
		StringBuffer outputResult = new StringBuffer();
		StringBuffer outputErrorResult = new StringBuffer();
		StringBuffer exitValueResult = new StringBuffer();
		
		LOG.debug("Catching output file: "+fileStdOutPut.getAbsolutePath());
		
		outputResult = StringUtil.readFile(fileStdOutPut);
		result.setStdout( outputResult.toString() );
		
		LOG.debug("Output catched - file: "+fileStdOutPut.getAbsolutePath());
		
		LOG.debug("Catching error output file: "+fileStdError.getAbsolutePath());
		
		outputErrorResult = StringUtil.readFile(fileStdError);
		result.setStderr( outputErrorResult.toString() );
		
		LOG.debug("Error Output catched - file: "+fileStdError.getAbsolutePath());
		
		LOG.debug("Catching Exit value file: "+fileExitValue.getAbsolutePath());
		
		exitValueResult = StringUtil.readFile(fileExitValue);
		String ecode = exitValueResult.toString().trim();
		int exit = Integer.parseInt(ecode);
		result.setExitValue(exit);
		
		LOG.debug("Exit value catched - file: "+fileExitValue.getAbsolutePath());
	}
}
