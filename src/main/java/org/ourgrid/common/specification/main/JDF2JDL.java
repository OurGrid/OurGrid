package org.ourgrid.common.specification.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.naming.directory.InvalidAttributeValueException;

import org.glite.jdl.CollectionAd;
import org.glite.jdl.Jdl;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.main.CommonCompiler.FileType;

import condor.classad.Constant;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;

/**
 * This class works as a translator. It receives a JDF file as an input
 * and creates a JDL file as the output.
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public class JDF2JDL {

	private static final String JDL_EXTENSION = ".jdl";

	private static final String JDF_EXTENSION = ".jdf";

	private static int jobID = 0;

	private static int taskID = 0;

	private static Pattern pattern;


	/**
	 * This method receives a JDF file as input and requests the mapping of
	 * its attributes to JDL attributes, generating a JDL file at the end
	 * @param args 
	 */
	public static void main( String[ ] args ) {

		if ( args.length >= 1 ) {
			
			String jdfFileName = args[0];
			
			if(jdfFileName == null){
				System.err.println("Invalid jdf file: "+jdfFileName);
				return;
			}
			
			String jdlFileName;
			if ( args.length == 1 ) {
				jdlFileName = jdfFileName.replace( JDF_EXTENSION, JDL_EXTENSION );
			} else {
				jdlFileName = args[1];
			}

			File file = new File( jdfFileName );
			if ( file.exists() ) {

				if ( file.canRead() ) {

					File jdlFile = new File( jdlFileName );

					if ( jdlFile.exists() ) {
						System.out.println( "Overwriting file: " + file.getAbsolutePath() );
					}

					//Compiling JDF
					CommonCompiler commonCompiler = new CommonCompiler();
					try {
						commonCompiler.compile( jdfFileName, FileType.JDF );

						JobSpecification jobSpec = (JobSpecification) commonCompiler.getResult().get( 0 );
						CollectionAd collection = new CollectionAd();
						ListExpr listExpr = new ListExpr();

						//Mapping attributes
						taskID = 0;
						for ( TaskSpecification task : jobSpec.getTaskSpecs() ) {
							RecordExpr recordExpr = new RecordExpr();

							parseExecutable( task, recordExpr );

							parseEpilogue( task, recordExpr );

							parseInputBlocks( task, recordExpr );

							parseOutputBlocks( task, recordExpr );

							listExpr.add( recordExpr );
							taskID++;
						}
						collection.setAttribute( "nodes", listExpr );
						collection.setAttribute( JdlOGExtension.NAME, Constant.getInstance( jobSpec.getLabel() ) );

						FileWriter writer;
						writer = new FileWriter( jdlFile );
						writer.append( collection.toString( true, true ) );
						writer.close();
						System.out.println("File " + jdlFile.getAbsolutePath() + " successfully generated.");
						jobID++;
					} catch ( InvalidAttributeValueException e ) {
						System.err.println("Error on parsing to JDL. It can be a bug, please report it on ourgrid mainling list.");
						e.printStackTrace();
					} catch ( IOException e ) {
						System.out.println("Problem for writing results in a new JDL file. Check error messages below.");
						e.printStackTrace();
					} catch ( CompilerException e ) {
						System.err.println( "Problems with your JDF file. See errors below:");
						e.printStackTrace();
					} catch ( Exception e ) {
						e.printStackTrace();
					}
				}else{
					System.err.println( "Check your permissions for file: " + file.getAbsolutePath() );
				}
			}else{
				System.err.println( "File: " + file.getAbsolutePath() + " does not exists." );
			}
		}else{
			System.err.println("Missing arguments.");
		}
	}


	/**
	 * This method translates the JDF remote executable command into the JDL format
	 * @param task The task specification {@link TaskSpecification}
	 * @param recordExpr The output expression containing the JDL job
	 * @throws Exception 
	 */
	private static void parseExecutable( TaskSpecification task, RecordExpr recordExpr ) throws Exception {

		String exec = task.getRemoteExec();
		if( exec.contains( ";" ) ){
			System.err.println( "Task \n-------\n" + task + " \n-------\ncould not be parsed as it contains more than one executable command." );
			throw new Exception();
		}

		exec = parseEnvironmentVariables(exec);

		Scanner input = new Scanner( exec );

		pattern = Pattern.compile( "\\s*\\<\\s*|\\s*2\\>+\\s*|\\s*\\>+\\s*|\\s+" );
		input.useDelimiter( pattern );

		String previous = exec;
		if( input.hasNext() ){
			previous = input.next();
		}

		recordExpr.insertAttribute( Jdl.EXECUTABLE, Constant.getInstance( previous ) );

		int indexOfIn = exec.indexOf( "<" );
		int indexOfErr = exec.indexOf( "2>" );
		int indexOfOut = exec.indexOf( ">" );
		if(indexOfOut == indexOfErr + 1){
			indexOfOut = exec.indexOf( ">", indexOfOut+2 );
		}

		String args = "";
		while ( input.hasNext() ) {
			String next = input.next();
			int indexOfPrevious = exec.indexOf( previous );
			int indexOfNext = exec.indexOf( next );

			if( indexOfPrevious < indexOfIn && indexOfIn < indexOfNext ){
				recordExpr.insertAttribute( Jdl.STDINPUT, Constant.getInstance( next ) );
			}else if( indexOfPrevious < indexOfOut && indexOfOut < indexOfNext ){
				if(exec.charAt( indexOfOut+1 ) == '>'){
					args += " >> " + next;
				}else{
					recordExpr.insertAttribute( Jdl.STDOUTPUT, Constant.getInstance( next ) );
				}
			}else if( indexOfPrevious < indexOfErr && indexOfErr < indexOfNext ){
				if(exec.charAt( indexOfErr+2 ) == '>'){
					args += " 2>> " + next;
				}else{
					recordExpr.insertAttribute( Jdl.STDERROR, Constant.getInstance( next ) );
				}
			} else {
				args += next + " ";
			}
			previous = next;
		}

		if(!(args.trim().length() == 0)){
			recordExpr.insertAttribute( Jdl.ARGUMENTS, Constant.getInstance( args.trim()) );
		}
	}

	/**
	 * This method replaces environment variables defined in the JDF to its
	 * values.
	 * @param string A string representing the remote executable command of the JDF job
	 * @return A string with the environment variables replaced
	 */
	private static String parseEnvironmentVariables( String string ) {

		return string.replaceAll( "\\$JOB", Integer.toString( jobID ) ).replaceAll( "\\$TASK",
				Integer.toString( taskID ) ).replaceAll( "\\$PLAYPEN", "." ).replaceAll( "\\$STORAGE", ".");
	}

	/**
	 * This method translates the JDF sabotage check command to the
	 * JDL epilogue command
	 * @param task The task specification {@link TaskSpecification}
	 * @param recordExpr The output expression containing the JDL job 
	 */
	private static void parseEpilogue( TaskSpecification task, RecordExpr recordExpr ) {

		String sabotageCheck = task.getSabotageCheck();
		if ( sabotageCheck == null || (sabotageCheck.trim().length() == 0) ) {
			return;
		}
		sabotageCheck = parseEnvironmentVariables( sabotageCheck );
		int indexOf = sabotageCheck.indexOf( " " );
		if ( indexOf != -1 ) {
			String sabotageCommand = sabotageCheck.substring( 0, indexOf );
			String sabotageArgs = sabotageCheck.substring( indexOf + 1, sabotageCheck.length() );
			recordExpr.insertAttribute( Jdl.EPILOGUE, Constant.getInstance( sabotageCommand ) );
			recordExpr.insertAttribute( Jdl.EPILOGUE_ARGUMENTS, Constant.getInstance( sabotageArgs ) );
		} else {
			recordExpr.insertAttribute( Jdl.EPILOGUE, Constant.getInstance( sabotageCheck ) );
		}
	}

	/**
	 * This method translates the Ourgrid input IOBlocks to JDL InputSandbox
	 * @param task The task specification {@link TaskSpecification}
	 * @param recordExpr The output expression containing the JDL job
	 */
	private static void parseInputBlocks( TaskSpecification task, RecordExpr recordExpr ) {

		List<IOEntry> initBlocks = task.getInitBlock().getEntry( "" );
		if ( initBlocks == null ) {
			return;
		}
		ListExpr isbList = new ListExpr();
		for ( IOEntry ioEntry : initBlocks ) {
			String sourceFile = parseEnvironmentVariables( ioEntry.getSourceFile() );
			isbList.add( Constant.getInstance( sourceFile ) );
		}
		if ( isbList.size() != 0 ) {
			recordExpr.insertAttribute( Jdl.INPUTSB, isbList );
		}
	}

	/**
	 * This method translates the Ourgrid output IOBlocks to JDL InputSandbox
	 * @param task The task specification {@link TaskSpecification}
	 * @param recordExpr The output expression containing the JDL job
	 */
	private static void parseOutputBlocks( TaskSpecification task, RecordExpr recordExpr ) {

		List<IOEntry> finalBlocks = task.getFinalBlock().getEntry( "" );
		if ( finalBlocks == null ) {
			return;
		}
		ListExpr osbList = new ListExpr();
		ListExpr osbDest = new ListExpr();
		for ( IOEntry ioEntry : finalBlocks ) {
			osbList.add( Constant.getInstance( parseEnvironmentVariables( ioEntry.getSourceFile() ) ) );
			osbDest.add( Constant.getInstance( parseEnvironmentVariables( ioEntry.getDestination() ) ) );
		}
		if ( osbList.size() != 0 ) {
			recordExpr.insertAttribute( Jdl.OUTPUTSB, osbList );
			recordExpr.insertAttribute( Jdl.OSBURI, osbDest );
		}
	}
}
