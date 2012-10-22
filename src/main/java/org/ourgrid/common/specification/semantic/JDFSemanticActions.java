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
package org.ourgrid.common.specification.semantic;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.ourgrid.common.specification.CompilerMessages;
import org.ourgrid.common.specification.exception.JobSpecificationException;
import org.ourgrid.common.specification.exception.TaskSpecificationException;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.main.CommonCompiler;
import org.ourgrid.common.specification.semantic.exception.SemanticException;
import org.ourgrid.common.specification.syntactical.CommonSyntacticalAnalyzer;
import org.ourgrid.common.specification.token.Token;

/**
 * This entity is the set of actions that the JOB grammar uses to build a answer
 * to the compilation of sources wrote in this language. Created on 15/06/2004
 * 
 * @see JDFSemanticActionsTest
 */
public class JDFSemanticActions implements SemanticActions {

	private static transient final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
		.getLogger( JDFSemanticActions.class );

	private Stack<String> stack;

	private Token actualToken;

	private int mode = CommonSyntacticalAnalyzer.MODE_NORMAL;

	// Needed variables
	private boolean isJobAttrib = true;

	private IOBlock transferEntries = null;

	private List<TaskSpecification> tasksSpecs = new ArrayList<TaskSpecification>();

	// Job (default) attributes
	private String jobRemoteScript = null;

	private IOBlock jobInputEntries, jobOutputEntries = null;

	// Actual Task attributes
	private String remoteScript = null;

	private IOBlock inputEntries, outputEntries = null;

	private String condition = null;

	private JobSpecification theJob = null;

	/* The last action that will be executed has to set it */
	private List<JobSpecification> result;

	private String sabotageCheckCommand;

	private String jobSabotageCheckCommand;


	/**
	 * The constructor
	 */
	public JDFSemanticActions() {

		this.stack = new Stack<String>();
	}


	/**
	 * @see org.ourgrid.common.specification.semantic.SemanticActions#performAction(java.lang.String,
	 *      org.ourgrid.common.specification.token.Token)
	 */
	public void performAction( String action, Token token ) throws SemanticException {

		this.actualToken = token;
		try {
			Class semantic = Class.forName( this.getClass().getName() );
			Method method = semantic.getMethod( action );
			method.invoke( this );
		} catch ( NoSuchMethodException nsmex ) {
			throw new SemanticException( CompilerMessages.SEMANTIC_ACTION_NOT_FOUND, nsmex );
		} catch ( ClassNotFoundException cnfex ) {
			throw new SemanticException( CompilerMessages.SEMANTIC_CLASS_NOT_FOUND, cnfex );
		} catch ( InvocationTargetException itex ) {
			if ( itex.getCause() instanceof SemanticException ) {
				throw (SemanticException) itex.getCause();
			}
			throw new SemanticException( CompilerMessages.SEMANTIC_FATAL_ERROR(), itex.getCause() );
		} catch ( IllegalAccessException iaex ) {
			throw new SemanticException( CompilerMessages.SEMANTIC_FATAL_ILLEGAL_ACCESS, iaex );
		}
	}


	/**
	 * @see org.ourgrid.common.specification.semantic.SemanticActions#getOperationalMode()
	 */
	public int getOperationalMode() {

		return this.mode;
	}


	/**
	 * @see org.ourgrid.common.specification.semantic.SemanticActions#getResult()
	 */
	public List<JobSpecification> getResult() {

		return this.result;
	}


	/**
	 * This action: Sets the actual script of the remote script. Actual can be
	 * jobs (default) script if this.isJobAttrib == true It happens only when
	 * the first tag "task:" was not found yet.
	 */
	public void action4() {

		if ( this.isJobAttrib == true ) {
			this.jobRemoteScript = actualToken.getSymbol();
		} else {
			this.remoteScript = actualToken.getSymbol();
		}
	}

	/**
	 * This action: Tells to this entity that the job (default) attributes
	 * reading was finished. That means that will begin to read tasks.
	 */
	public void action8() {

		this.isJobAttrib = false;
	}


	/**
	 * This action: Closes a Task and put it at "this.tasksSpec" list.
	 * 
	 * @throws SemanticException When the task could not be validated then the
	 *         TaskSpecificationException is wrapped into this one.
	 */
	public void action9() throws SemanticException {

		checkTaskEntries();
		TaskSpecification task;
		try {
			task = new TaskSpecification( this.inputEntries, this.remoteScript, this.outputEntries, this.sabotageCheckCommand );
			tasksSpecs.add( task );
			nullTaskEntries();
		} catch ( TaskSpecificationException tsex ) {
			LOG.error( "A task could not be validated! - Interrupting the compilation process." );
			throw new SemanticException( CompilerMessages.BAD_TASK_DEFINITION( (tasksSpecs.size() + 1), tsex.getCause()
				.getMessage() ) );
		}
	}


	/**
	 * This action: Initializes the "condition" string for the actual block of
	 * I/O commands or job expression.
	 */
	public void action10() {

		this.condition = new String();
	}


	/**
	 * This action: Concatenates the symbol of the Token at the condition string
	 * Statement.
	 */
	public void action11() {

		this.condition = condition + " " + this.actualToken.getSymbol();
	}


	/**
	 * This action: Sets the condition string to null, it means that the
	 * condition read will not be used anymore.
	 */
	public void action12() {

		this.condition = null;
	}


	/**
	 * This action: Mounts the condition for the ELSE block and push it at the
	 * stack.
	 */
	public void action13() {

		this.condition = "! ( " + condition.trim() + " )";
		this.stack.push( condition );
	}


	/**
	 * This action: Closes and set the job expression.
	 */
	public void action14() {

		this.theJob.setRequirements( condition.trim() );
	}
	

	/**
	 * This action: initializes the IOBlock to receive a new one.
	 */
	public void action15() {

		this.transferEntries = new IOBlock();
	}


	/**
	 * This action: Puts the I/O block condition statement at the stack.
	 */
	public void action16() {

		this.stack.push( this.condition.trim() );
	}


	/**
	 * This action: Will push a empty String object at the stack. It happens
	 * when the I/O entries have no conditions to be transfered.
	 */
	public void action17() {

		this.condition = "";
		this.stack.push( condition );
	}


	/**
	 * This action: Will push the other parts ( command, file, path ) of the I/O
	 * commands at the stack.
	 */
	public void action18() {

		this.stack.push( this.actualToken.getSymbol() );
	}


	/**
	 * This action: Builds a IOEntry and insert it at the actual IOBlock.
	 * 
	 * @throws SemanticException If the user did not define a part of the I/O
	 *         command.
	 */
	public void action19() throws SemanticException {

		String place = stack.pop();
		String filePath = stack.pop();
		String command = stack.pop();
		String condition = stack.peek(); // do not remove it because
		// it can be necessary for
		// other entries.
		IOEntry entry = buildEntry( command, filePath, place );
		this.transferEntries.putEntry( condition, entry );
	}


	/**
	 * This action:Pops the "condition" string that remains at the stack top.
	 */
	public void action20() {

		stack.pop();
	}


	/**
	 * This action: Sets the IOBlock built as the input entry for the
	 * actualTask.
	 */
	public void action21() {

		if ( this.isJobAttrib == true ) {
			this.jobInputEntries = this.transferEntries;
		} else {
			this.inputEntries = this.transferEntries;
		}
	}


	/**
	 * This action: Sets the final result LIST object.
	 * 
	 * @throws SemanticException
	 */
	public void action22() throws SemanticException {

		try {
			this.theJob.setTaskSpecs( this.tasksSpecs );
		} catch ( JobSpecificationException e ) {
			throw new SemanticException( "Tried to contruct a Job Spec based on a problematic list of Tasks Specs. " );
		}
		this.result = new LinkedList<JobSpecification>();
		result.add( theJob );
	}


	/**
	 * This action: Sets the IOBlock built as the output entry for the
	 * actualTask.
	 */
	public void action23() {

		if ( this.isJobAttrib == true ) {
			this.jobOutputEntries = this.transferEntries;
		} else {
			this.outputEntries = this.transferEntries;
		}
	}


	/**
	 * This action: initializes the object JobSpec with the found label.
	 */
	public void action24() {

		theJob = new JobSpecification( this.actualToken.getSymbol() );
		mode = CommonSyntacticalAnalyzer.MODE_NORMAL;
	}


	/**
	 * This action: initializes the object JobSpec with a empty string because
	 * any label was defined.
	 */
	public void action25() {

		theJob = new JobSpecification( "" );
	}


	/**
	 * This action: sets the reading mode to readstring
	 */
	public void action26() {

		mode = CommonSyntacticalAnalyzer.MODE_READSTRING;
	}


	/**
	 * This action: sets the reading mode to normal
	 */
	public void action27() {

		mode = CommonSyntacticalAnalyzer.MODE_NORMAL;
	}


	/**
	 * This action: sets the reading mode to readline
	 */
	public void action28() {

		mode = CommonSyntacticalAnalyzer.MODE_READLINE;
	}

	
	/**
	 * This action: sets the sabotage check command
	 */
	public void action29() {
		
		if ( this.isJobAttrib == true ) {
			this.jobSabotageCheckCommand = actualToken.getSymbol();
		} else {
			this.sabotageCheckCommand = actualToken.getSymbol();
		}
	}
	
	/**
	 * This action: Puts the value string for a attribute at the top of the
	 * stack.
	 * 
	 * @throws SemanticException
	 */
	public void action30() throws SemanticException {

		this.stack.push( actualToken.getSymbol() );
	}
	
	/**
	 * This action: Puts the value string for a attribute at the top of the
	 * stack.
	 * 
	 * @throws SemanticException
	 */
	public void action31() throws SemanticException {

		String tempAttValue = actualToken.getSymbol();
		if ( tempAttValue.equals( "" ) ) {
			throw new SemanticException( CompilerMessages.SEMANTIC_EMPTY_ATTRIBUTE_VALUE( stack.pop(), actualToken
					.getLine() ) );
		}
		this.stack.push( tempAttValue );
	}
	
	/**
	 * This action: Inserts a new attribute at the actual worker specification
	 * or at the default map depending of the type of the attribute (worker or
	 * default).
	 */
	public void action32() {

		String attValue = stack.pop();
		String attName = stack.pop();
		theJob.getAnnotations().put( attName, attValue );
	}

	// /////////// AUXILIAR METHODS /////////////////////

	/*
	 * Make all the entries for a task point to null.
	 */
	private void nullTaskEntries() {

		inputEntries = null;
		remoteScript = null;
		outputEntries = null;
		sabotageCheckCommand = null;
	}


	/*
	 * This method checks if a task has any non defined entry and if exists any
	 * defauld value (from Job) to insert at that.
	 */
	private void checkTaskEntries() {

		if ( inputEntries == null ) {
			if ( jobInputEntries != null )
				inputEntries = jobInputEntries;
			else
				inputEntries = new IOBlock();
		}
		if ( remoteScript == null && jobRemoteScript != null ) {
			remoteScript = jobRemoteScript;
		}

		if ( outputEntries == null ) {
			if ( jobOutputEntries != null )
				outputEntries = jobOutputEntries;
			else
				outputEntries = new IOBlock();
		}
		
		if ( sabotageCheckCommand == null && jobSabotageCheckCommand != null ) {
			sabotageCheckCommand = jobSabotageCheckCommand;
		}
	}


	/*
	 * Builds a IOEntry object making the local file paths absolute ones. @param
	 * command the command of the I/O operation, that can be - GET, PUT or STORE
	 * @param filePath the file path of the source @param place the file path of
	 * the destiny @return a IOEntry object referente of the attributes but,
	 * including a absolute path for the local ( that will be found at the home
	 * machine ) file paths using the description file parent. @throws
	 * SemanticException if any of the paramethers is a empty string
	 */
	private IOEntry buildEntry( String command, String filePath, String place ) throws SemanticException {

		if ( command.equals( "" ) || filePath.equals( "" ) || place.equals( "" ) ) {
			throw new SemanticException( CompilerMessages.SEMANTIC_MALFORMED_IO_COMMAND );
		}
		String localParentDir = CommonCompiler.getSourceParentDir();
		if ( command.equalsIgnoreCase( "GET" ) ) {
			// To insert the JDF parent directory in relative path
			File temp = new File( place );
			if ( !temp.isAbsolute() )
				place = localParentDir + File.separator + place;

		} else { // command is PUT or STORE
			// To insert the JDF parent directory in relative path
			File temp = new File( filePath );
			if ( !temp.isAbsolute() )
				filePath = localParentDir + File.separator + filePath;
		}

		return new IOEntry( command, filePath, place );
	}

}
