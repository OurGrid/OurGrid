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
package org.ourgrid.common.specification.job;

import java.io.Serializable;


/**
 * Encapsulates informations about a input/output operation at the Broker
 * environment.
 * 
 * @version 1.0 Created on Jul 2, 2004
 */
public class IOEntry implements Serializable {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 33L;

	private String command;

	private String sourceFile;

	private String destination;


	/**
	 * An empty constructor.
	 */
	public IOEntry() {

	}


	/**
	 * Constructor.
	 * 
	 * @param command The command that will be executed at the tranfering time.
	 * @param sourceFile The path file that will be transfered.
	 * @param destination The destiny's path of the file transfered.
	 */
	public IOEntry( String command, String sourceFile, String destination ) {

		this.command = command;
		this.sourceFile = sourceFile;
		this.destination = destination;
	}


	/**
	 * @return Returns the command.
	 */
	public String getCommand() {

		return command;
	}


	/**
	 * @return Returns the destination.
	 */
	public String getDestination() {

		return destination;
	}


	/**
	 * @return Returns the sourceFile.
	 */
	public String getSourceFile() {

		return sourceFile;
	}


	/**
	 * @param command The command to set.
	 */
	public void setCommand( String command ) {

		this.command = command;
	}


	/**
	 * @param destiny The destination to set.
	 */
	public void setDestination( String destiny ) {

		this.destination = destiny;
	}


	/**
	 * @param sourceFile The sourceFile to set.
	 */
	public void setSourceFile( String sourceFile ) {

		this.sourceFile = sourceFile;
	}


	/**
	 * Returns an string representation of this IOEntry
	 */
	public String toString() {

		return command + " " + this.sourceFile + " to " + this.destination;

	}


	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.command == null) ? 0 : this.command.hashCode());
		result = PRIME * result + ((this.destination == null) ? 0 : this.destination.hashCode());
		result = PRIME * result + ((this.sourceFile == null) ? 0 : this.sourceFile.hashCode());
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
		final IOEntry other = (IOEntry) obj;
		if ( !(this.command == null ? other.command == null : this.command.equals( other.command )) )
			return false;
		if ( !(this.destination == null ? other.destination == null : this.destination.equals( other.destination )) )
			return false;
		if ( !(this.sourceFile == null ? other.sourceFile == null : this.sourceFile.equals( other.sourceFile )) )
			return false;
		return true;
	}

}
