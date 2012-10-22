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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.common.util.TempFileManager;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;

/**
 * This class is the concrete implementation of Executor interface that provides
 * the platform dependent command execution. The reationale behind this
 * implementation is to implement Linux native command invocation.
 */
public class LinuxExecutor extends VanillaExecutor {

	private static final long serialVersionUID = 33L;

	/**
	 * execute permission.
	 */
	static int XPerm = 1;

	/**
	 * write permission.
	 */
	static int WPerm = 2;

	/**
	 * read permission.
	 */
	static int RPerm = 4;


	private static final String MYPIDS = ".mypids";


	/**
	 * A protected constructor to be accessible only by the
	 * <code>ExecutorFactory</code>.
	 */
    @Req("REQ004")
	protected LinuxExecutor( CommuneLogger logger ) {
		// An Executor should only be constructed by ExecutorFactory;
    	super(logger);
	}


	/**
	 * @see Executor#chmod(File, String)
	 */
	public void chmod( File file, String modeStr ) throws ExecutorException {

		/* The linux command for changing the permissions of files */
		String cmd = "chmod";

		/* Octal representation of native file permissions */
		int mode;

		try {
			mode = Integer.parseInt( modeStr );
		} catch ( NumberFormatException nfe ) {
			throw new ExecutorException( "The permissions are invalid: " + modeStr, nfe );
		}

		if ( ( mode < 0 ) || ( mode > 7777 ) ) {
			throw new ExecutorException( "The permissions are invalid: " + mode );
		}

		/* Verify if the the file object is null */
		if ( file == null ) {
			throw new ExecutorException( cmd, new FileNotFoundException( "'null'" ) );
		}

		/* Verify if the file exists */
		if ( !( file.exists() ) ) {
			throw new ExecutorException( cmd + " " + file.toString(),
											new FileNotFoundException( file.getAbsolutePath() ) );
		}

		/*
		 * The command line that must be executed to change the permissions of
		 * the file
		 */
		String commandLine = "";

		/* Compose the command line */
		commandLine = cmd + " " + mode + " " + file.toString();

		getLogger().debug( "Changing file permissions like " + commandLine);

		/* execute the command */
		ExecutorResult result = this.getResult( this.execute( file.getParent(), commandLine ) );

		if ( result.getExitValue() != 0 ) {
			throw new ExecutorException( "Could not change the file permissions. " + result.getStderr() );
		}

	}


	/**
	 * This method implements the command execution feature for Linux
	 * environment.
	 * 
	 * @param command The command must be executed
	 * @param dirName The execution root directory following the Linux name
	 *        convention
	 * @return A handle that identifies this execution
	 * @throws ExecutorException If the command could not be executed.
	 */
	public ExecutorHandle execute( String dirName, String command ) throws ExecutorException {
		return this.execute( dirName, command, new LinkedHashMap() );
	}


	/**
	 * This method implements the command execution feature for Linux
	 * environment.
	 * 
	 * @param command The command must be executed
	 * @param dirName The execution root directory following the Linux name
	 *        convention
	 * @param envVars A map (var name, value) with the environment variables
	 *        used by the command
	 * @return A handle that identifies this execution
	 * @throws ExecutorException If the command could not be executed.
	 */
	public ExecutorHandle execute( String dirName, String command, Map envVars ) throws ExecutorException {
		
		if ( dirName == null || command == null ) {
			throw new ExecutorException( "Invalid parameters: " + dirName + ", " + command );
		}
		
		/*
		 * The native process abstract representation. For Linux
		 */
		Process process;

		/* The handle for this execution. */
		ExecutorHandle handle;

		/* Get a new one */
		handle = this.getNextHandle();

		/* An script created to provide shell facilities for command execution */
		File script = createScript( command, dirName, envVars, handle );
		getLogger().debug( "About to invoke sh " + script.getPath() + " command:  " + command );
		File mypidFile = new File( getMypidFileName( dirName, handle ) );
		if ( mypidFile.exists() ) {
			mypidFile.delete();
		}
		try {

			/* Invoke the native method of command executino */
			process = Runtime.getRuntime().exec( "sh " + script.getPath() );

			/** Register one more */
			this.includeInProcesses( handle, process, dirName );

		} catch ( IOException e ) {
			throw new ExecutorException( command, e );
		}

		mypidFile.deleteOnExit();

//		script.deleteOnExit();
		return handle;
	}


	/**
	 * Gets the mypids file name
	 */
	private String getMypidFileName( String dirName, ExecutorHandle handle ) {
		String fileName = dirName + File.separator + LinuxExecutor.MYPIDS + "." + handle.toString();
		return fileName;
	}


	/**
	 * Adds a process into the set of the ones which results were not collected
	 * yet.
	 * 
	 * @param handle The handle for the process
	 * @param process The process to be included at the group
	 * @param command 
	 * @param dirName The directory where the process was started.
	 */
	protected synchronized void includeInProcesses( ExecutorHandle handle, Process process, String dirName ) {
		HandleEntry hEntry = new HandleEntry( handle, process, dirName );
		addHandleEntry(handle, hEntry);
	}


	/**
	 * Creates a script file to execute the command passed as paramether. This
	 * script will execute the command into the directory and will export the
	 * environment variables at the map passed as arguments.
	 * 
	 * @param command The command to be executed.
	 * @param dirName Where the command has to be executed.
	 * @param envVars The environment variables to be exported and used by the
	 *        command.
	 * @param handle The process handle.
	 * @return A script file that has the functionalities described here.
	 * @throws ExecutorException If the directory passed is not valid (does not
	 *         exists) and if could not create the script file for any I/O
	 *         problem..
	 */
	protected File createScript( String command, String dirName, Map envVars, ExecutorHandle handle )
																										throws ExecutorException {

		/* Check if dir is not null. Convert to "." to void problems */
		if ( dirName == null ) {
			dirName = ".";
		}

		getLogger().debug( "Creating script on dir..." + dirName + " for command " + command );

		/* The execution root directory */
		File dir = new File( dirName );

		getLogger().debug( "Will create file on dir " + dir + " is Directory: " + dir.isDirectory() );

		/* The abstraction for the command */
		File commandFile = null;

		/* Indicate if the command is already an script */
		boolean isScript = false;

		if ( !dirName.equals( "." ) && !dir.isDirectory() ) {
			throw new ExecutorException( command, new FileNotFoundException( dir.getAbsolutePath() ) );
		}

		/*
		 * try to figure out if command is already a script note that this is
		 * incomplete as it only works if the script is in dirName
		 */

		DataInputStream commandDIS = null;

		try {

			getLogger().debug( "Will create file on dir " + dir + " command: " + command );
			commandFile = new File( dir, command );
			if ( commandFile.exists() && commandFile.canRead() ) {
				commandDIS = new DataInputStream( new FileInputStream( commandFile ) );
				if ( commandDIS.readChar() == '#' && commandDIS.readChar() == '!' ) {
					isScript = true;
				}
			}

		} catch ( FileNotFoundException e ) {
			throw new ExecutorException( command, new FileNotFoundException( commandFile.getAbsolutePath() ) );
		} catch ( IOException e ) {
			throw new ExecutorException( command, e );
		} finally {

			/**
			 * Close to avoid the "Too many open files" message and release
			 * resources
			 */
			if ( commandDIS != null ) {
				try {
					commandDIS.close();
				} catch ( IOException e1 ) {

				}
			}

		}

		File temp;
		BufferedWriter writerTemp = null;
		Iterator keys;
		String theKey;
		String exportCommand = "export ";

		/**
		 * Try create the temporary script in fact.
		 */
		try {

			temp = TempFileManager.createTempFile( "ourgrid", ".tmp" );

			/* A writer to produce the script commands */
			writerTemp = new BufferedWriter( new FileWriter( temp ) );

			/* Write the PID of process that represents the script */
			writerTemp.write( "echo $$ >> " + getMypidFileName( dirName, handle ) );
			writerTemp.newLine();

			if ( envVars != null ) {

				/* Gets an iterator to the keys in the Map */
				keys = envVars.keySet().iterator();

				while ( keys.hasNext() ) {

					theKey = ( String ) keys.next();
					writerTemp.write( theKey + "=\'" + envVars.get( theKey ) + "\'" );
					writerTemp.newLine();
					exportCommand = exportCommand + " " + theKey;
				}

				if ( envVars.get( "STORAGE" ) != null ) {

					writerTemp.write( "PATH=$PATH:$STORAGE:$PLAYPEN:." );
					writerTemp.newLine();
					exportCommand = exportCommand + " PATH";

				}

				// this test is only for not writing blank lines in the script
				if ( !envVars.isEmpty() ) {

					writerTemp.write( exportCommand );
					writerTemp.newLine();
				}

			}

			writerTemp.write( "cd " + dirName );
			writerTemp.newLine();
			if ( isScript ) {
				writerTemp.write( "sh " );
			}

			writerTemp.write( command );
			writerTemp.newLine();

			return temp;

		} catch ( IOException ioe ) {
			throw new ExecutorException( ioe );
		} finally {

			if ( writerTemp != null ) {
				try {
					writerTemp.close();
				} catch ( IOException e1 ) {
				}
			}

		}
	}


	/**
	 * @see Executor#kill(ExecutorHandle)
	 */
	public void kill( ExecutorHandle handle ) throws ExecutorException {
		if ( handle != null ) {
			if(getHandleEntries().containsKey( handle )){
				String dirName = getDirName( handle );
				
				// script that kills a process and all its children
				String lsCommand = "ls -l " + this.getMypidFileName( dirName, handle );
				Runtime runtime = Runtime.getRuntime();
				Process process;
				int MAX_RETRIES = 5;
				try {
					for ( int retries = 0; retries < MAX_RETRIES; retries++ ) {
						process = runtime.exec( lsCommand );
						if ( process.waitFor() != 0 ) {
							Thread.sleep( 1000 );
						}
					}
				} catch ( Exception e ) {
					e.printStackTrace();
				}
				
				String killCommand = "reckill() { for pid in `pgrep -P $1`; do reckill $pid; done; kill -9 $1; }; for realPID in `cat "
					+ this.getMypidFileName( dirName, handle )
					+ "`; do for pid2 in `pgrep -P $realPID`; do reckill $pid2; done; done";
				Map envVars = CommonUtils.createMap();
				
				ExecutorHandle killHandle = this.execute( dirName, killCommand, envVars );
				this.getResult( killHandle );
				getLogger().debug( "Command " + killCommand + " has been executed." );
			}else{
				getLogger().debug( "Command kill for handle " + handle.toString()
						+ " is not necessary because this process is already finished." );
			}
		}
	}


	/**
	 * @see Executor#getResult(ExecutorHandle)
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


	public void finishExecution() throws ExecutorException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This method provides a synchronized access to the Map containning the
	 * Processes.
	 * 
	 * @param handle An identificator for the Process in the Map.
	 * @return The dirName where the process was started.
	 */
	private synchronized String getDirName( ExecutorHandle handle ) {
		HandleEntry handleEntry = getHandleEntries().get( handle );
		if ( handleEntry == null )
			return null;
		return handleEntry.getDirName();
	}

	public void prepareAllocation() throws ExecutorException {
		// TODO Auto-generated method stub
		
	}

	public void killCommand(ExecutorHandle handle) throws ExecutorException {
		// TODO Auto-generated method stub
		
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
