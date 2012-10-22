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
package org.ourgrid.common.specification.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.glite.jdl.Ad;
import org.glite.jdl.CollectionAd;
import org.glite.jdl.Jdl;
import org.glite.jdl.JobAd;
import org.glite.jdl.JobAdException;
import org.glite.jdl.ParametricAd;
import org.ourgrid.common.specification.CompilerMessages;
import org.ourgrid.common.specification.job.InputBlock;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.OutputBlock;
import org.ourgrid.common.specification.job.TaskSpecification;

import condor.classad.Constant;
import condor.classad.Expr;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;

/**
 * A JDL semantic check compiler. 
 * 
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public class JDLSemanticAnalyzer {
	
	private static transient final Logger LOG = Logger.getLogger( JDLSemanticAnalyzer.class );
	private static String sourceFileName;
	
	/**
	 * Compiles a JDL building a list of valid {@link JobSpecification} object.
	 * @param sourceFileName The JDL file source path
	 * @return The resultant list of {@link JobSpecification} 
	 * @throws CompilerException If there is no source file, or any syntactical mistake.
	 */
	public static List<JobSpecification> compileJDL( String sourceFileName ) throws CompilerException {
		
		JDLSemanticAnalyzer.sourceFileName = sourceFileName;
		Ad ad = JDLSyntacticalAnalyzer.compileJDL( sourceFileName );
		
		validateSupportedTypes(ad);
		
		return parseType( ad );
	}

	/**
	 * This method verifies if the Ad is a job or a collection of jobs and produces the corresponding result.
	 * @param ad The ad containing the job specification
	 * @return A list of job specifications
	 * @throws CompilerException Exception thrown if an error occurs during semantic
	 * validations of the job specification
	 */
	private static List<JobSpecification> parseType( Ad ad ) throws CompilerException {

		String type = ad.lookup( Jdl.TYPE ).stringValue().trim();
		if( Jdl.TYPE_COLLECTION.equalsIgnoreCase( type )){
			return parseTypeCollection(ad);
		}
		return parseTypeJob(ad);
	}

	/**
	 * This method requests some validations for the job being compiled and then
	 * requests the construction of JobSpecs
	 * @param ad The ad of the compiled job
	 * @return A list containing {@link JobSpecification}s. 
	 * @throws CompilerException 
	 */
	private static List<JobSpecification> parseTypeJob( Ad ad ) throws CompilerException {
		
		List<JobSpecification> list = new ArrayList<JobSpecification>();
		
		JobAd jobAd = (JobAd) ad;
		
		validateSupportedJobTypes(jobAd);
		
		validateAttributes(jobAd);
		
		list.add( buildJobSpec(jobAd) );
		
		return list;
	}

	/**
	 * Parses Collection Jobs which are translated into a list of independent {@link JobSpecification}.
	 * @param ad The syntactically correct {@link Ad}
	 * @return A list of independent {@link JobSpecification}
	 * @throws CompilerException When parsing the collection or parsing one of each independent {@link JobSpecification}.
	 */
	@SuppressWarnings("unchecked")
	private static List<JobSpecification> parseTypeCollection( Ad ad ) throws CompilerException {

		List<JobSpecification> list = new ArrayList<JobSpecification>();

		CollectionAd collectionAd = (CollectionAd) ad;

		Enumeration<JobAd> jobEnumeration;
		try {
			jobEnumeration = collectionAd.getJobEnumeration();
			
			String collectionName = ad.lookup( JdlOGExtension.NAME ).stringValue();

			int nodeIndex = 1;
			while ( jobEnumeration.hasMoreElements() ) {
				JobAd nextJobAd = jobEnumeration.nextElement();
				if( nextJobAd.lookup( JdlOGExtension.NAME ) == null ){
					try {
						nextJobAd.addAttribute( JdlOGExtension.NAME, collectionName.concat( Integer.toString( nodeIndex ) ) );
					} catch ( Exception e ) {
						LOG.error( e.getMessage(), e );
					}
				}
				list.addAll( parseTypeJob( nextJobAd ) );
				nodeIndex++;
			}
			
			return list;
		} catch ( JobAdException e ) {
			throw new CompilerException( e.getMessage(), e );
		}
	}

	/**
	 * This method checks if a valid name, label, was defined for the job
	 * @param jobAd The compiled job
	 * @throws CompilerException Exception thrown if an invalid name was defined
	 * for the job  
	 */
	private static void validateAttributes( JobAd jobAd ) throws CompilerException {
		
		Expr lookup = jobAd.lookup( JdlOGExtension.NAME );
		if(lookup.type != Expr.STRING ){
			throw new CompilerException("");
		}
	}

	/**
	 * OurGrid does not support DAG or Collection types.
	 * 
	 * @param ad The compiled {@link Ad}
	 * @throws CompilerException When one is trying to compile an unsupported type.
	 */
	private static void validateSupportedTypes(Ad ad) throws CompilerException {
		
		Expr value = ad.lookup( Jdl.TYPE );
		if(value != null){
			String type = value.stringValue().trim();
			if( !type.equalsIgnoreCase( Jdl.TYPE_JOB ) && ! type.equalsIgnoreCase( Jdl.TYPE_COLLECTION ) ){
				throw new CompilerException( CompilerMessages.JDL_UNSUPPORTED_TYPE( type.toLowerCase() ) );
			}
		}
	}

	/**
	 * OurGrid does only support execution of {@link Jdl#JOBTYPE_NORMAL} and {@link Jdl#JOBTYPE_PARAMETRIC} type jobs.
	 * 
	 * @param jobAd The compiled {@link JobAd}
	 * @throws CompilerException When one is trying to compile an unsupported job type.
	 */
	private static void validateSupportedJobTypes(JobAd jobAd) throws CompilerException {

		Expr value = jobAd.lookup( Jdl.JOBTYPE );
		if(value != null){
			String jobType = value.stringValue().trim();
			if( !jobType.equalsIgnoreCase( Jdl.JOBTYPE_NORMAL ) && 
				!jobType.equalsIgnoreCase( Jdl.JOBTYPE_PARAMETRIC ) ){
				throw new CompilerException( CompilerMessages.JDL_UNSUPPORTED_JOB_TYPE( jobType.toLowerCase() ) );
			}
		}
	}

	/**
	 * It builds an {@link JobSpecification} from a compiled {@link JobAd}. 
	 * It also checks some parameters existence.
	 *  
	 * @param jobAd The compiled {@link JobAd}
	 * @return A new {@link JobSpecification}
	 * @throws CompilerException 
	 */
	private static JobSpecification buildJobSpec( JobAd jobAd ) throws CompilerException {
		
		String jobType = jobAd.lookup( Jdl.JOBTYPE ).stringValue();
		
		JobSpecification jobSpec;
		if ( jobType.equalsIgnoreCase( Jdl.JOBTYPE_PARAMETRIC ) ) {
			jobSpec = new JobSpecification(parseParametricTaskSpec((ParametricAd) jobAd));
		} else {
			jobSpec = new JobSpecification(parseTaskSpec(jobAd));
		}
		jobSpec.setLabel( parseLabel( jobAd ) );
		parseRequirements(jobSpec, jobAd);
		
		return jobSpec;
	}

	/**
	 * This method is responsible for iterating over the parametric tasks in the defined
	 * job requesting their parsing
	 * @param parametricAd The compiled job
	 * @return A list of tasks specifications {@link TaskSpecification}
	 * @throws CompilerException When trying to parse a non-parametric job.
	 */
	private static TaskSpecification[] parseParametricTaskSpec( ParametricAd parametricAd ) throws CompilerException {
		
		try {
			Enumeration<JobAd> jobEnumeration = parametricAd.getJobEnumeration();
			List<TaskSpecification> taskSpecs = new ArrayList<TaskSpecification>();
			while ( jobEnumeration.hasMoreElements() ) {
				JobAd jobAd = jobEnumeration.nextElement();
				taskSpecs.add( parseTaskSpec( jobAd ) );
			}
			return taskSpecs.toArray( new TaskSpecification[1] );
		} catch ( JobAdException e ) {
			throw new CompilerException( e.getMessage(), e );
		}
	}
	
	/**
	 * This method is responsible for extracting from the job ad
	 * the parameters used to characterize a task in Ourgrid
	 * @param jobAd The compiled job
	 * @return A specification of the task {@link TaskSpecification}
	 */
	private static TaskSpecification parseTaskSpec(JobAd jobAd) {
		String executable = parseExecCommand( jobAd );
		
		String epilogue = parseEpilogue( jobAd );
		
		InputBlock inputBlock = parseInput(jobAd);
		
		OutputBlock outputBlock = parseOutput(jobAd);
		
		TaskSpecification task = new TaskSpecification(jobAd.toString(), executable, inputBlock, outputBlock, epilogue);
		return task;
	}
	
	/**
	 * This method parses from the job ad the output commands to be executed after task
	 * execution
	 * @param jobAd The compiled job
	 * @return A IOBlock containing the output commands
	 */
	private static OutputBlock parseOutput(JobAd jobAd) {
		List<String> outputSandBox = parseOutputSandBox(jobAd);
		
		String outputSandBoxBaseURI = parseOutputSandBoxBaseURI(jobAd);
		
		List<String> outputSandBoxDest = parseOutputSandBoxDest(jobAd);
		
		if( outputSandBoxDest.isEmpty() ){
			outputSandBoxDest.addAll( outputSandBox );
		}

		OutputBlock outputBlock = new OutputBlock( outputSandBox, outputSandBoxBaseURI, outputSandBoxDest );
		return outputBlock;
	}
	
	/**
	 * This method parses from the job ad the input commands to be executed before a task
	 * execution
	 * @param jobAd The compiled job
	 * @return A IOBlock containing the input commands
	 */
	private static InputBlock parseInput(JobAd jobAd) {
		String inputSandBoxBaseURI = parseInputSandBoxBaseURI( jobAd );

		List<String> inputSandBox = parseInputSandBox( jobAd );
		
		InputBlock inputBlock = new InputBlock( inputSandBoxBaseURI, inputSandBox );
		return inputBlock;
	}
	
	/**
	 * This method verifies if a label was defined for the job
	 * @param jobAd The compiled job
	 * @return The label of the job
	 */
	private static String parseLabel(JobAd jobAd) {
		String label = jobAd.lookup( "Name" ).stringValue().trim();
		return label;
	}
	
	/**
	 * This method is responsible for parsing the command to be executed
	 * in the worker machine.
	 * @param jobAd The compiled job
	 * @return A string containing the command to be executed
	 */
	private static String parseExecCommand( JobAd jobAd ) {
		Expr remoteExec = jobAd.lookup( Jdl.EXECUTABLE );
		Expr stdInput = jobAd.lookup( Jdl.STDINPUT );
		Expr stdOutput = jobAd.lookup( Jdl.STDOUTPUT );
		Expr stdError = jobAd.lookup( Jdl.STDERROR );
		Expr arguments = jobAd.lookup( Jdl.ARGUMENTS );
		
		StringBuilder builder = new StringBuilder(remoteExec.stringValue().trim());
		if(arguments != null){
			builder.append( ' ' );
			builder.append( arguments.stringValue().trim() );
		}else{
			if(stdInput != null){
				builder.append( " < " );
				builder.append( stdInput.stringValue().trim() );
			}
		}
		if(stdOutput != null){
			builder.append( " > " );
			builder.append( stdOutput.stringValue().trim() );
		}
		if(stdError != null){
			builder.append( " 2> " );
			builder.append( stdError.stringValue().trim() );
		}
		return builder.toString();
	}
	
	/**
	 * This method parses the sabotage check command {@link Jdl#EPILOGUE in JDL} to be
	 * executed
	 * @param jobAd The compiled job
	 * @return A string representing the command
	 */
	private static String parseEpilogue( JobAd jobAd ) {
		Expr epilogue = jobAd.lookup( Jdl.EPILOGUE );
		Expr arguments = jobAd.lookup( Jdl.EPILOGUE_ARGUMENTS );
		StringBuilder builder = new StringBuilder();
		if(epilogue != null){
			builder.append( epilogue.stringValue().trim() );
			if(arguments != null){
				builder.append( arguments.stringValue().trim() );
				
			}
		}
		return epilogue == null? null:builder.toString();
	}
	
	/**
	 * This method is responsible for parsing the absolute path of the directory containing
	 * input files for the task
	 * @param jobAd The compiled job
	 * @return A string representing the absolute path of the input directory
	 */
	private static String parseInputSandBoxBaseURI( JobAd jobAd ) {

		String inputSandBoxBaseURI;
		try {
			inputSandBoxBaseURI = new File( sourceFileName ).getParentFile().getCanonicalPath();
		} catch ( IOException e ) {
			inputSandBoxBaseURI = ".";
		}
		Expr inputSandBoxBaseURIExpr = jobAd.lookup( Jdl.ISBBASEURI );
		if ( inputSandBoxBaseURIExpr != null ) {
			
			String inputSandBoxBaseURIExprValue = inputSandBoxBaseURIExpr.eval().stringValue().trim();
			if(!new File(inputSandBoxBaseURIExprValue).isAbsolute()){
				try {
					inputSandBoxBaseURI = new File(inputSandBoxBaseURI + File.separator + inputSandBoxBaseURIExprValue).getCanonicalPath();
				} catch ( IOException e ) {
					inputSandBoxBaseURI = ".";
				}
			}else{
				inputSandBoxBaseURI = inputSandBoxBaseURIExprValue;
			}
		}
		return inputSandBoxBaseURI;
	}
	
	
	/**
	 * This method parses the files that should be transfered to the worker machine before a task 
	 * execution
	 * @param jobAd The compiled job
	 * @return A list of the input files
	 */
	@SuppressWarnings("unchecked")
	private static List<String> parseInputSandBox( JobAd jobAd ) {
		Expr inputSandBoxExpr = jobAd.lookup( Jdl.INPUTSB );
		List<String> inputSandBox = new ArrayList<String>();
		if ( inputSandBoxExpr != null ) {
			ListExpr eval = (ListExpr) inputSandBoxExpr.eval();
			Iterator<Expr> iterator = eval.iterator();
			while ( iterator.hasNext() ) {
				Expr expr = iterator.next();
				inputSandBox.add( expr.eval().stringValue().trim() );
			}
		}
		return inputSandBox;
	}

	/**
	 * This method is responsible for parsing the canonical path of the execution output directory 
	 * @param jobAd The compiled job
	 * @return A string representing the path of the output directory
	 */
	private static String parseOutputSandBoxBaseURI( JobAd jobAd ) {

		String outputSandBoxBaseURI;
		try {
			outputSandBoxBaseURI = new File( sourceFileName ).getParentFile().getCanonicalPath();
		} catch ( IOException e ) {
			outputSandBoxBaseURI = ".";
		}
		Expr outputSandBoxBaseURIExpr = jobAd.lookup( Jdl.OSBBASEURI );
		if ( outputSandBoxBaseURIExpr != null ) {
			outputSandBoxBaseURI = outputSandBoxBaseURIExpr.eval().stringValue().trim();
		}
		return outputSandBoxBaseURI;
	}
	
	
	/**
	 * This method parses the output files that should be retrieved after execution ends.
	 * @param jobAd The compiled job
	 * @return A list of output files
	 */
	@SuppressWarnings("unchecked")
	private static List<String> parseOutputSandBox( JobAd jobAd ) {
		Expr outputSandBoxExpr = jobAd.lookup( Jdl.OUTPUTSB );
		List<String> outputSandBox = new ArrayList<String>();
		if ( outputSandBoxExpr != null ) {
			ListExpr eval = (ListExpr) outputSandBoxExpr.eval();
			Iterator<Expr> iterator = eval.iterator();
			while ( iterator.hasNext() ) {
				Expr expr = iterator.next();
				outputSandBox.add( expr.eval().stringValue().trim() );
			}
		}
		return outputSandBox;
	}
	
	
	/**
	 * This method parses the exact location at which output files should be placed
	 * @param jobAd The compiled job
	 * @return A list of output files destinations
	 */
	@SuppressWarnings("unchecked")
	private static List<String> parseOutputSandBoxDest( JobAd jobAd ) {
		Expr outputSandBoxDestExpr = jobAd.lookup( Jdl.OSBURI );
		List<String> outputSandBoxDest = new ArrayList<String>();
		if ( outputSandBoxDestExpr != null ) {
			ListExpr eval = (ListExpr) outputSandBoxDestExpr.eval();
			Iterator<Expr> iterator = eval.iterator();
			while ( iterator.hasNext() ) {
				Expr expr = iterator.next();
				outputSandBoxDest.add( expr.eval().stringValue().trim() );
			}
		}
		return outputSandBoxDest;
	}
	
	/**
	 * This method is responsible for verifying if requirements and rank expressions were defined
	 * by the user or if default values should be used
	 * @param jobSpec The specification of the job
	 * @param jobAd The compiled job
	 */
	private static void parseRequirements( JobSpecification jobSpec, JobAd jobAd ) {
		
		RecordExpr expr = new RecordExpr();
		
		Expr requirementsExpr = jobAd.lookup( Jdl.REQUIREMENTS );
		expr.insertAttribute( Jdl.REQUIREMENTS, requirementsExpr == null? Constant.TRUE : requirementsExpr);

		Expr rankExpr = jobAd.lookup( Jdl.RANK );
		expr.insertAttribute( Jdl.RANK, rankExpr == null? Constant.getInstance( 0 ) : rankExpr);
		
		jobSpec.setRequirements( expr.toString() );
	}
}