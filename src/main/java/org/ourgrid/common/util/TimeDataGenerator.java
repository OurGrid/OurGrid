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
package org.ourgrid.common.util;

import java.io.Serializable;

import org.ourgrid.reqtrace.Req;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Description:This class generates data about the execution of a replica
 * 
 * @version 1.0 Created on 23/08/2004
 */
public class TimeDataGenerator implements Serializable {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 33L;

	/**
	 * Logger to store log messages
	 */
	private static transient final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
		.getLogger( TimeDataGenerator.class );

	/**
	 * The start time held by an instance of this type
	 */
	private long startTime = -1;

	/**
	 * The end time held by an instance of this type
	 */
	private long endTime = -1;

	/**
	 * A text defined by the user of this type that will appear in the
	 * <code>toString()</code> method.
	 */
	@XStreamOmitField
	private String preText = "";


    @Req("REQ004")
	public TimeDataGenerator( ) {
		
	}

	/**
	 * Constructor method
	 * 
	 * @param text the name of the time collection
	 */
	public TimeDataGenerator( String text ) {

		preText = text;
	}


	/**
	 * This method sets the initial part of the caption text of this object
	 * 
	 * @param newText the name of the time collection
	 */
	public void setPreText( String newText ) {

		preText = newText;
	}


	/**
	 * This method returns the initial part of caption text of this object
	 * 
	 * @return A string representing the initial part of caption text of this
	 *         object
	 */
	public String getPreText() {

		return preText;
	}


	/**
	 * This method sets the start time of an execution
	 */
    @Req("REQ004")
	public void setStartTime( ) {
		startTime = System.currentTimeMillis();
	}


	/**
	 * This method sets the end time of an execution
	 */
	@Req({"REQ068a","REQ095"})
	public void setEndTime( ) {
		endTime = System.currentTimeMillis();
	}


	/**
	 * This method report the times collect
	 */
	public void report() {

		LOG.info( toString() );
	}


	/**
	 * Returns a string representation ot this object.
	 */
	@Override
	public String toString() {

		if ( startTime == -1 ) {
			return preText + " unstarted!";
		} else if ( endTime == -1 ) {
			return preText + " unfinished!";
		}

		return preText + " " + (endTime - startTime) + " ms";
	}


	/**
	 * Gets the start time held by this type.
	 * 
	 * @return the start time held by this type.
	 */
	public long getStartTime() {

		return startTime;
	}


	/**
	 * Gets the end time held by this type.
	 * 
	 * @return the end time held by this type.
	 */
	public long getEndTime() {

		return endTime;
	}
	
	
	@Req({"REQ068a","REQ095"})
	public long getElapsedTimeInSeconds() {

		return this.getElapsedTimeInMillis() / 1000;
	}


	public long getElapsedTimeInMillis() {

		if ( !isValid() )
			return 0L;
		return (endTime - startTime);

	}


	/**
	 * This method verifies if the StartTime and EndTime are not a valid time
	 * stamp. By an invalid timestamp we mean that the replica phase were not
	 * started or interrupted during its execution.
	 * 
	 * @return <b>true</b> if StarTime and EndTime timestamps were marked.
	 *         <b>false</b> otherwise.
	 */
	public boolean isValid() {

		return ((this.startTime != -1) && (this.endTime != -1));
	}
	
	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int) (this.endTime ^ (this.endTime >>> 32));
		result = PRIME * result + (int) (this.startTime ^ (this.startTime >>> 32));
		return result;
	}
	
	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( !(obj instanceof TimeDataGenerator) )
			return false;
		final TimeDataGenerator other = (TimeDataGenerator) obj;
		if ( this.endTime != other.endTime )
			return false;
		if ( this.startTime != other.startTime )
			return false;
		return true;
	}
}
