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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;

public class Win32Executor extends VanillaExecutor {

	private static final long serialVersionUID = 33L;

	/**
	 * The native process abstract representation. It is used to control the
	 * execution of command.
	 */
	private Process process = null;

	/** This is the next handle can be issued. */
	private int nextHandle = 0;


	public Win32Executor(CommuneLogger logger) {
		super(logger);
	}
	
	public void prepareAllocation() throws ExecutorException {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see broker.common.Executor#chmod(File file, int mode)
	 */
	public void chmod( File file, String mode ) throws ExecutorException {
		return;
	}


	/**
	 * Execute a command like a OS native script. This a method used by the
	 * Executor class in order to provide some facilities provided by shell. For
	 * example, wild cards expansion.
	 * 
	 * @param command The command must be executed
	 * @param dirName The directory where the execution will be started.
	 * @return The result (stdout and stderr) of the command execution
	 * @throws ExecutorException If the execution could not be performed.
	 */
	public ExecutorHandle execute( String dirName, String command ) throws ExecutorException {
		return execute( dirName, command, new LinkedHashMap() );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see broker.common.Executor#executeScript(String command, String dirName,
	 *      Map envVars)
	 */
	public ExecutorHandle execute( String dirName, String command, Map envVars ) throws ExecutorException {

		/* The handle for this execution. */
		ExecutorHandle handle;

		/* Get a new one */
		handle = this.getNextHandle();

		/* An script created to provide shell facilities for command execution */
		File script = createScript( command, dirName, envVars );

		getLogger().debug( "About to invoke cmd /C " + script.getPath() + " command:  " + command );

		try {

			/* Invoke the native method of command executino */
			process = Runtime.getRuntime().exec( "cmd /C \"" + script.getPath() + "\"" );

			/** Register one more */
			this.includeInProcesses( handle, process );

			/* get the process output */
			// result = this.catchOutput(process);
		} catch ( IOException e ) {
			throw new ExecutorException( command, e );
			// } catch (InterruptedException e) {
			// throw new ExecutorException(command, e );
		}

		script.deleteOnExit();

		return handle;
	}

	/**
	 * Yet to be implemented... (non-Javadoc)
	 * 
	 * @see org.ourgrid.common.executor.Executor#getResult(org.ourgrid.common.executor.ExecutorHandle)
	 */
	public ExecutorResult getResult( ExecutorHandle handle ) throws ExecutorException {

		ExecutorResult result = null;
		Process processToWait = null;

		try {

			/* Get the reference to the process will return ther result */
			processToWait = this.getProcess( handle );

			/* get the process output */
			result = this.catchOutput( processToWait );

			removeFromProcesses( handle );

		} catch ( InterruptedException e ) {
			throw new ExecutorException( "Cannot get the result of command execution.", e );
		}

		return result;

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see broker.common.Executor#createScript(java.lang.String,
	 *      java.lang.String, java.util.Map)
	 */
	protected File createScript( String command, String dirName, Map envVars ) throws ExecutorException {

		/* Create a file object to represent the name of execution root dir */
		File dir;

		dirName = convert2WinStyle( dirName );
		command = convert2WinStyle( command );

		getLogger().debug( "Creating script on dir..." + dirName + " for command " + command );

		/* The script created by this method to execute the command */
		File temporaryScript;

		/* A Buffer Writer to produce the temporary script file contents */
		BufferedWriter bwTemp = null;

		/*
		 * The keys in the map of environment variables. The name of variables.
		 */
		Iterator keys;

		/* The command used to set environement variables */
		String setCommand = "set ";

		/* Create the file abstraction to the dirName */
		dir = new File( dirName );
		getLogger().debug( "Will create file on dir " + dir + " is Directory: " + dir.isDirectory() );

		/* Check if the dirName actually refers to a directory */
		if ( !dirName.equals( "." ) && !dir.isDirectory() ) {
			throw new ExecutorException( command, new FileNotFoundException( dir.getAbsolutePath() ) );
		}
		getLogger().debug( "Will create file on dir " + dir + " command: " + command );
		/* Init the file object represents the command to be executed */
		new File( dir, command );

		try {

			/*
			 * This is an important phase. The idea is that Win32 platform just
			 * recognize exe, com, bat and pif like executable files. Therefore,
			 * this layer need to be aware about it.
			 */
			temporaryScript = File.createTempFile( "broker", ".bat", dir );

			temporaryScript.deleteOnExit();

			bwTemp = new BufferedWriter( new FileWriter( temporaryScript ) );

			// turns echo off [needed]
			bwTemp.write( "@echo off" );
			bwTemp.newLine();

			if ( envVars != null ) {

				/* Get the itarator for the var map */
				keys = envVars.keySet().iterator();

				if ( keys.hasNext() ) {

					while ( keys.hasNext() ) {

						String theKey = ( String ) keys.next();

						bwTemp.write( setCommand + " " + theKey + "=" + envVars.get( theKey ) + "" );

						bwTemp.newLine();
					}

					bwTemp.newLine();
				}
			}

			bwTemp.write( "cd " + dirName );
			bwTemp.newLine();

			bwTemp.write( command );
			bwTemp.newLine();

			bwTemp.flush();
			bwTemp.close();

			return temporaryScript;

		} catch ( IOException ioe ) {
			throw new ExecutorException( command, ioe );
		} finally {
			try {
				if ( bwTemp != null ) {
					bwTemp.close();
				}
			} catch ( IOException e ) {
				getLogger().debug( "Failed to close stream on Exception." );
			}
		}

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see broker.common.Executor#createScript(java.lang.String,
	 *      java.lang.String)
	 */
	protected File createScript( String command, String dirName ) throws ExecutorException {
		return this.createScript( command, dirName, null );
	}


	/**
	 * This method is responsible to convert some linux variables and separators
	 * styles to Windows' ones
	 * 
	 * @param inn String containing the text that should be processed and if
	 *        necessary converted to windows style
	 * @return The string converted for windows' format
	 */

	public static String convert2WinStyle( String inn ) {
		StringBuffer sb = new StringBuffer( inn );
		Pattern p = Pattern.compile( "\\${1}[1-9a-zA-Z]+" );
		Matcher m = p.matcher( inn );
		int increased = 0;
		while ( m.find() ) {
			sb.replace( m.start() + increased, m.start() + increased + 1, "%" );
			sb.insert( m.end() + increased, "%" );
			increased++;
		}
		return sb.toString().replace( '/', '\\' );
		// TODO Verify if we should also do replace(':',';')
	}

	/**
	 * Adds a process into the set of the ones which results were not collected
	 * yet.
	 * 
	 * @param handle The handle for the process
	 * @param process The process to be included at the group
	 */
	protected synchronized void includeInProcesses( ExecutorHandle handle, Process process ) {
		getHandleEntries().put( handle, new HandleEntry(handle, process, "") );
	}


	/**
	 * This method manage the handles issued for each command execution
	 * 
	 * @return A handle to be used by the client to identify its execution
	 */
	protected synchronized ExecutorHandle getNextHandle( ) {

		/** Produce a new handle */
		IntegerExecutorHandle newHandle = new IntegerExecutorHandle( nextHandle );

		/** Increment the handle pointer */
		this.nextHandle++;

		return newHandle;
	}

	public void killPreparingAllocation() throws ExecutorException {
		// TODO Auto-generated method stub
		
	}

	public void finishCommandExecution(ExecutorHandle handle) throws ExecutorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() throws ExecutorException {
		// TODO Auto-generated method stub
		
	}

}
