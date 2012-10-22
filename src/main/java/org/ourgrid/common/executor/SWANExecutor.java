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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ourgrid.common.executor.config.ExecutorConfiguration;
import org.ourgrid.common.util.TempFileManager;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;

/**
 * This class is the concrete implementation of Executor interface that provides
 * the platform dependent command execution. The reationale behind this
 * implementation is to use SWAN / XEN 's mechanisms to involke Linux native
 * commands. Unlike Regular Linux implementation of executor, SWANExecutor has a
 * couple of limitations: i) dirName on execute() must be the playpen. ii) only
 * one command can be executed per time. SWANExecutor may run a little slow.
 * It's the trade-off for security.
 */
public class SWANExecutor implements Executor {

	private static final long serialVersionUID = 33L;

	/** The working directory. For SWAN it must me the playpen. */
	private File dir;

	/** The file that will contain the standard output of the execution. */
	private File stdOutput;

	/** The file that will contain the error output of the execution. */
	private File errorOutput;

	/** The file that will contain the exit value of the execution. */
	private File exitValue;

	/*
	 * This is where the handle will be stored. Since SWANExecutor suports only
	 * one instance of execution per time, It does NOT use the same
	 * implementation as LinuxExecutor, where multiple handles can be issued.
	 */
	/** Used to limit to a single execution. The handle is unique. */
	private ExecutorHandle theOnlyHandle = new IntegerExecutorHandle( 1 );

	/**
	 * The handle (also unique) for the execution of the script that involkes
	 * the three stages os SWAN.
	 */
	private ExecutorHandle stagesHandle;

	/**
	 * An instance of LinuxExecutor, used to assist the native linux command
	 * invokation. It's needed to run the scripts used by SWAN.
	 */
	private LinuxExecutor linExecutor;

	/** Sets if the execution (limited to one in SWAN) is being used or not */
	private boolean singleExecutionInUse = false;

	/** Sets if the process has been killed or not */
	private boolean processKilled = false;

	/**
	 * Logger to store log messages
	 */
	private transient final CommuneLogger logger;



	/**
	 * A protected constructor to be accessible only by the
	 * <code>ExecutorFactory</code>.
	 * @param logger 
	 */
	protected SWANExecutor( CommuneLogger logger ) {
		// An Executor should only be constructed by ExecutorFactory;
		this.logger = logger;
		this.linExecutor = new LinuxExecutor(logger);
	}


	/**
	 * A protected constructor to be used only by tests.
	 */
	protected SWANExecutor( LinuxExecutor testLinuxExecutor, File stdOutput, File errorOutput, File exitValue, CommuneLogger logger ) {
		this.linExecutor = testLinuxExecutor;
		this.stdOutput = stdOutput;
		this.errorOutput = errorOutput;
		this.exitValue = exitValue;
		this.logger = logger;
	}

	public void prepareAllocation() throws ExecutorException {
		// TODO Auto-generated method stub
	}
	
	
	/**
	 * This method implements the command execution feature for SWAN
	 * environment. <br>
	 * One of SWAN's limitation is that it can run only one execution per time.
	 * Multiple executions are not allowed. A getResult() or a kill() must be
	 * done before starting a new execution.
	 * 
	 * @param command The command must be executed
	 * @param dirName The execution root directory following the Linux name
	 *        convention. It must be the playpen.
	 * @return A handle that identifies this execution
	 * @throws ExecutorException If the command could not be executed.
	 */
	public ExecutorHandle execute( String dirName, String command ) throws ExecutorException {
		return this.execute( dirName, command, new LinkedHashMap() );
	}


	/**
	 * This method implements the command execution feature for SWAN
	 * environment. <br>
	 * One of SWAN's limitation is that it can run only one execution per time.
	 * Multiple executions are not allowed. A getResult() or a kill() must be
	 * done before starting a new execution.
	 * 
	 * @param dirName The execution root directory following the Linux name
	 *        convention. It must be the playpen.
	 * @param command The command must be executed
	 * @param envVars A map (var name, value) with the environment variables
	 *        used by the command
	 * @return A handle that identifies this execution
	 * @throws ExecutorException If the command could not be executed.
	 */
	public ExecutorHandle execute( String dirName, String command, Map envVars ) throws ExecutorException {
		
		if ( dirName == null || command == null ) {
			throw new ExecutorException( "Invalid parameters: " + dirName + ", " + command );
		}
		
		logger.debug( "Requested to execute command: " + command + ", Dir Name: " + dirName );

		/*
		 * The var singleExecutionInUse is false if the single execution is not
		 * running and the result has been gotten or if the process has been
		 * killed.
		 */
		if ( singleExecutionInUse ) {
			logger.debug( "Unable to execute command: " + command + " because "
					+ "Swan's single execution is unavailable (busy)." );
			throw new ExecutorException(
											"SwanExecutor: There is already one execution in progress. GetResult() or Kill() must be done before another execute()",
											new UnsupportedOperationException(
																				"Swan only supports one execution per time." ) );
		}
		
		/*
		 * Since a new execution is being started, reset the flag processKilled,
		 * and set singleExecutionInUse=true. Also set the working directory.
		 */
		processKilled = false;
		singleExecutionInUse = true; /* SWANExecutor is now performmng its
		only execution. */
		dir = new File( dirName ); /* setting the working directory. Remember
		 that it must be the playpen.*/

		try {
			/*
			 * Creating the files that will contain the outputs... The files are
			 * created in the user's playpen and will be copyed to dom1 through
			 * stagein. The outputs will be stored, the files will be staged out
			 * back to the playpen, their contents will be saved and they will
			 * be deleted.
			 */
			
			stdOutput = TempFileManager.createTempFile( "stdOutput_", ".txt", dir );
			errorOutput = TempFileManager.createTempFile( "stdError_", ".txt", dir );
			exitValue = TempFileManager.createTempFile( "exitValue_", ".txt", dir );

			/*
			 * A script created to run the command passed as a param. This is
			 * the script that will be executed in dom1.
			 */
			
			File script = createScript( command, dirName, envVars );
			
			/* The initvm command to be executed */
			String initVMCmd = "swan_initvm -p " + dirName + " -c  \"sh " + script.getName() + " >"
					+ stdOutput.getName() + " 2>" + errorOutput.getName() + " \" ";

			/*
			 * A new script iscreated. createSWANStagesScript() creates a script
			 * that contains the stagein, initVM and stageout commands. The
			 * dirName and the whole initVM command are passed as params.
			 */
			File swanStagesScript = createSWANStagesScript( dirName, initVMCmd, envVars );
			
			/*
			 * script with the swan_initvm command
			 */

			// Now, run the script containing the three stages of SWAN.
			stagesHandle = linExecutor.execute( dirName, "sh " + swanStagesScript.getName() );

		} catch ( Exception e ) {
			throw new ExecutorException( command, e );
		}
		return theOnlyHandle;
	}


	/**
	 * Returns the result of a execution specified by a handle. Due to SWAN
	 * limitations, there is only one execution per time, and its result must be
	 * returned (or a kill has to be called) before a new one takes place.
	 * 
	 * @param handle the command handle
	 * @return ExecutorResult StdOut, StdErr and exitValue from kill command.
	 */
	public ExecutorResult getResult( ExecutorHandle handle ) throws ExecutorException {
		logger.debug( "Getting result of execution... Handle: " + handle );

		/* The ExecutorResult to be returned. */
		ExecutorResult result = null;

		/*
		 * If there is no execution running (or done) to have it's result
		 * gotten, it's an error.
		 */
		if ( !singleExecutionInUse ) {
			logger.error( "Tried to get result of an inexistent execution." );
			return null;
		}

		/*
		 * Waits for the end of the execution of stagesScripts. This is where
		 * execution hangs.
		 */
		ExecutorResult stagesResult = linExecutor.getResult( stagesHandle );

		if ( stagesResult.getExitValue() == 0 ) {
			logger.debug( "stagesScript finished successfully." );
		} else {
			logger.debug( "stagesScript did NOT finish successfully." );
		}

		/*
		 * Checks if process has been killed. If so, it returns the result of
		 * the execution of the stagesScript. If not, continue with normal
		 * execution - catch the output!
		 */
		if ( processKilled ) {
			logger.debug( "Process killed." );
			processKilled = false;
			return stagesResult;
		}
		result = this.catchOutputFromFile();

		/*
		 * Checks again if process has been killed. It is done because a kill
		 * might been involked while catchOutputFromFile was running. In this
		 * case, It's possible that we have the right results, but since It's
		 * been killed the exitValue must be not equals to 0
		 */
		if ( processKilled ) {
			logger.debug( "Process killed while catching output from files." );
			result.setExitValue( 1 );
		}

		/* resets the flag, so execute can be involked again. */
		singleExecutionInUse = false;

		logger.debug( "Finished getResult. Single Execution released." );
		return result;
	}


	/**
	 * Kills command that was issued via an execute method
	 * 
	 * @param handle the command handle
	 * @throws ExecutorException when there is a problem while changing .the
	 *         permissions of a file.
	 */
	public void kill( ExecutorHandle handle ) throws ExecutorException {
		
		/*
		 * Checks if the single execution is being used. If It's not, makes no
		 * sense killing it. If it happens, either an error occurred or the API
		 * is being misused.
		 */
		if ( !singleExecutionInUse ) {
			logger.error( "Tried to kill an inexistent execution." );
			return;
		}

		try {
			/* Try to kill the process executing stagesScript */
			logger.debug( "Kill requested. Will try to kill stagesScript now." );
			linExecutor.kill( stagesHandle );

			/*
			 * Executes swan_killvm to run XEN shutdown commands and unmount the
			 * partition.
			 */
			logger.debug( "Executing swan_killvm to conclude kill process." );
			ExecutorHandle killHandle = linExecutor.execute( dir.getPath(), "swan_killvm" );
			ExecutorResult killResult = linExecutor.getResult( killHandle );
			if ( killResult.getExitValue() != 0 ) {
				logger.error( "Error while executing swan_killvm." );
			}

			/* Flag up processKilled and release the execution. */
			processKilled = true;
			singleExecutionInUse = false;
			logger.debug( "Kill executed successfully." );

		} catch ( Exception e ) {
			logger.error( "Exception", e );
		}

	}

	
	public void finishExecution() throws ExecutorException {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Changes the permissions of an especifc file
	 * 
	 * @param file The file which permissions will be changed
	 * @param modeStr The new permitions.<br>
	 *        Example: "123" = "--x-w--wx"
	 */
	/*
	 * The files permissions must be changed on the playpen, outside the VM that
	 * executes the native commands. When a stagein is executed, the file
	 * permissions will become the same in the VM. So the implementation is the
	 * same as Linux Executor's.
	 */
	public void chmod( File file, String modeStr ) throws ExecutorException {
		linExecutor.chmod( file, modeStr );
	}


	/**
	 * Creates a file "SWANStagesScript" containing the commands used by SWAN.
	 * 
	 * @param dirName The path of the directory where the file will be created.
	 * @param command The command line that will be put an the file.
	 * @param envVars A map containing eh enviroment vars
	 * @return a File representing the script created
	 */
	private File createSWANStagesScript( String dirName, String command, Map envVars ) throws IOException {
		/* the file of SWANStagesScript to be returned */
		File temp = new File( dirName, "SWANStagesScript" );

		/* delete the file if it already exists */
		if ( temp.exists() ) {
			temp.delete();
		}

		BufferedWriter writerTemp = new BufferedWriter( new FileWriter( temp ) );

		String storageDir = ( String ) envVars.get( "STORAGE" );
		if ( storageDir == null ) {
			storageDir = ".";
		}

		writerTemp.write( "swan_stagein -p " + dirName + " -s " + storageDir + " &&" );
		writerTemp.newLine();
		writerTemp.write( command + " &&" );
		writerTemp.newLine();
		writerTemp.write( "swan_stageout -d " + dirName );

		writerTemp.flush();
		writerTemp.close();
		temp.deleteOnExit();

		return temp;
	}


	/**
	 * Creates an auxiliary file (script) containing the command to be executed.
	 * 
	 * @param command The command to be run
	 * @param dirName The path of the directory where the file will be created.
	 * @return a File representing the script created
	 */
	protected File createScript( String command, String dirName ) throws ExecutorException {
		return createScript( command, dirName, null );
	}


	/**
	 * Creates an auxiliary file (script) containing the command to be executed.
	 * 
	 * @param command The command to be run
	 * @param dirName The path of the directory where the file will be created.
	 * @param envVars A map (var name, value) with the environment variables
	 *        used by the command
	 * @return a File representing the script created
	 */
	protected File createScript( String command, String dirName, Map envVars ) throws ExecutorException {

		File dir = new File( dirName ); // The execution directory
		File commandFile = null; // The abstraction for the command
		boolean isScript = false; // Indicate if the command is already an
		// script

		// Check if dir is not null. Convert to "." to void problems
		if ( dirName == null ) {
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
				/**
				 * Close to avoid the "Too many open files" message and release
				 * resources
				 */
				commandFIS.close();
				/**
				 * Close to avoid the "Too many open files" message and release
				 * resources
				 */
				commandDIS.close();
			}
		} catch ( FileNotFoundException e ) {
			throw new ExecutorException( command, new FileNotFoundException( commandFile.getAbsolutePath() ) );
		} catch ( IOException e ) {
			throw new ExecutorException( command, e );
		}

		File temp;
		BufferedWriter writerTemp;
		Iterator keys;
		String theKey;
		String exportCommand = "export ";

		/**
		 * Try create the temporary script in fact.
		 */
		try {

			temp = TempFileManager.createTempFile( "broker", ".tmp", dir );
			/* A writer to produce the script commands */
			writerTemp = new BufferedWriter( new FileWriter( temp ) );

			/* If the command does not need any kind of environment variables */
			if ( envVars != null ) {
				/* If the map has some environment variable to export */
				if ( !envVars.isEmpty() ) {
					/* Gets an iterator to the keys in the Map */
					keys = envVars.keySet().iterator();
					if ( keys.hasNext() ) {
						while ( keys.hasNext() ) {
							theKey = ( String ) keys.next();
							if ( theKey.equals( "PLAYPEN" ) ) {
								writerTemp.write( theKey + "=\'" + "/playpen/" + "\'" );
							} else {
								if ( theKey.equals( "STORAGE" ) ) {
									writerTemp.write( theKey + "=\'" + "/mgstorage/" + "\'" );
								} else {
									writerTemp.write( theKey + "=\'" + envVars.get( theKey ) + "\'" );
								}
							}
							writerTemp.newLine();
							exportCommand = exportCommand + " " + theKey;
						}
						writerTemp.write( exportCommand );
						writerTemp.newLine();
					}
				}
			}

			writerTemp.write( "PATH=$PATH:$STORAGE:." );
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
			writerTemp.write( "echo $? >" + exitValue.getName() );
			writerTemp.flush();
			writerTemp.close();

			return temp;

		} catch ( IOException ioe ) {
			throw new ExecutorException( ioe );
		}
	}


	/**
	 * Creates and returns an instance of ExcecutorResult that will contain the
	 * informations about the finished process. It reads the files containing
	 * the outputs and creates the result.
	 * 
	 * @return An instance of ExecutorResult describing the result of <i>process</i>
	 *         execution.
	 */
	private ExecutorResult catchOutputFromFile( ) {

		// create an instance of Executor result information class
		ExecutorResult result = new ExecutorResult();

		// The StringBuffers that will contain the outputs from the files
		StringBuffer outputResult = new StringBuffer();
		StringBuffer outputErrorResult = new StringBuffer();
		StringBuffer exitValueResult = new StringBuffer();
		try {
			// CATCHING STANDARD OUTPUT
			String line = null;
			// Read the whole file and put it on the stringBuffer
			BufferedReader stdOutputBR = new BufferedReader( new FileReader( dir.getPath() + "/" + stdOutput.getName() ) );
			while ( ( line = stdOutputBR.readLine() ) != null && !processKilled ) {
				outputResult.append( line + "\n" );
			}
			stdOutputBR.close();

			// CATCHING ERROR OUTPUT
			line = null;
			// Read the whole file and put it on the stringBuffer
			BufferedReader errorOutputBR = new BufferedReader( new FileReader( dir.getPath() + "/"
					+ errorOutput.getName() ) );
			while ( ( line = errorOutputBR.readLine() ) != null && !processKilled ) {
				outputErrorResult.append( line + "\n" );
			}
			errorOutputBR.close();

			// CATCHING EXIT VALUE
			line = null;
			// Read the whole file and put it on the stringBuffer
			BufferedReader exitValueBR = new BufferedReader( new FileReader( dir.getPath() + "/" + exitValue.getName() ) );
			while ( ( line = exitValueBR.readLine() ) != null && !processKilled ) {
				exitValueResult.append( line );
			}
			exitValueBR.close();

			// Delete the output files staged out from the VM
			File file = new File( dir.getPath() + "/" + stdOutput.getName() );
			if ( file.delete() == false ) {
				logger.error( "Could not delete " + stdOutput.getName() );
			}

			file = new File( dir.getPath() + "/" + errorOutput.getName() );
			if ( file.delete() == false ) {
				logger.error( "Could not delete " + errorOutput.getName() );
			}

			file = new File( dir.getPath() + "/" + exitValue.getName() );
			if ( file.delete() == false ) {
				logger.error( "Could not delete " + exitValue.getName() );
			}

		} catch ( IOException ioe ) {
			logger.error( ioe.getMessage() );
		}

		try {
			result.setExitValue( Integer.parseInt( exitValueResult.toString() ) );
		} catch ( NumberFormatException nfe ) {
			logger
				.debug( "Failed parsing exitValueResult.toString() to int. It is normal if the process has been killed " );
		}

		result.setStdout( outputResult.toString() );
		result.setStderr( outputErrorResult.toString() );

		return result;
	}


	public void setConfiguration(ExecutorConfiguration executorConfiguratrion) {
		// TODO Auto-generated method stub
		
	}

	public void killCommand(ExecutorHandle handle) throws ExecutorException {
		// TODO Auto-generated method stub
		
	}


	public void killPreparingAllocation() throws ExecutorException {
		// TODO Auto-generated method stub
		
	}

	public void finishCommandExecution(ExecutorHandle handle)
			throws ExecutorException {
		// TODO Auto-generated method stub
		
	}


	public void finishPrepareAllocation() throws ExecutorException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void shutdown() throws ExecutorException {
		// TODO Auto-generated method stub
		
	}

}// SWANExecutor
