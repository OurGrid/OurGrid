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
package org.ourgrid.broker.controlws;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.specification.exception.JobSpecificationException;

/**
 * Entity that encapsulates all the information given by the user about each
 * job. To inform, the user uses the Description Files that can be compiled by
 * the CommonCompiler.
 * 
 * @see org.ourgrid.common.specification.main.CommonCompiler
 * @version 1.0
 */

public class WSJobSpec implements Serializable {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 1L;

	private String label;
	private String requirements;
	private List<WSTaskSpec> taskSpecs;
	
	public WSJobSpec() {}


	/**
	 * Constructor.
	 * 
	 * @param label The label for the job.
	 * @param requirements The logical expression that defines the job. It will
	 *        be used to choose machines that are able to run its tasks. To
	 *        define it well, check the OurGrid manual.
	 * @param taskSpecs A list with all the task specifications of this job.
	 */
	public WSJobSpec( String label, String requirements, List<WSTaskSpec> taskSpecs ) throws JobSpecificationException {

		this.label = label;
		this.requirements = requirements;
		this.taskSpecs = taskSpecs;
		validate();
	}


	/**
	 * This method validates the attributes of this Job Spec
	 */
	private void validate() throws JobSpecificationException {

		if ( this.taskSpecs == null ) {
			throw new JobSpecificationException( "A Job Spec could not be initialized with a null list of Task Specs." );
		}

		if ( this.taskSpecs.size() == 0 ) {
			throw new JobSpecificationException(
				"A Job Spec could not be initialized with an empty list of Task Specs." );
		}

		if ( this.taskSpecs.contains( null ) ) {
			throw new JobSpecificationException(
				"A Job Spec could not contain a null element into the list of Task Specs." );
		}

	}


	/**
	 * The constructor
	 * 
	 * @param label The label for the job.
	 */
	public WSJobSpec( String label ) {

		this.label = label;
		this.requirements = "";
		taskSpecs = new ArrayList<WSTaskSpec>();
	}


	/**
	 * @return A list with the tasks specification in this job.
	 */
	public List<WSTaskSpec> getTaskSpecs() {

		return taskSpecs;
	}


	/**
	 * Inserts a list of task specifications.
	 * 
	 * @param taskSpecs The list of tasks that will be contained by this job.
	 * @throws JobSpecificationException 
	 */
	public void setTaskSpecs( List<WSTaskSpec> taskSpecs ) throws JobSpecificationException {

		this.taskSpecs = taskSpecs;
		validate();
	}


	/**
	 * @return The logical expression that will be used to choose machines to
	 *         run the tasks in this job.
	 */
	public String getRequirements() {

		return this.requirements;
	}


	/**
	 * Sets the logical expression for the job.
	 * 
	 * @param expression The logical expression that defines the job. It will be
	 *        used to choose machines that are able to run its tasks.
	 */
	public void setRequirements( String expression ) {

		this.requirements = expression;
	}


	/**
	 * @return The label of the job.
	 */
	public String getLabel() {

		return this.label;
	}


	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.label == null) ? 0 : this.label.hashCode());
		result = PRIME * result + ((this.requirements == null) ? 0 : this.requirements.hashCode());
		result = PRIME * result + ((this.taskSpecs == null) ? 0 : this.taskSpecs.hashCode());
		return result;
	}


	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		final WSJobSpec other = (WSJobSpec) obj;
		if ( !(this.label == null ? other.label == null : this.label.equals( other.label )) )
			return false;
		if ( !(this.requirements == null ? other.requirements == null : this.requirements.equals( other.requirements )) )
			return false;
		if ( !(this.taskSpecs == null ? other.taskSpecs == null : this.taskSpecs.equals( other.taskSpecs )) )
			return false;
		return true;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
