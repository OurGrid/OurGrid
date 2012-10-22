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
package org.ourgrid.common.interfaces.to;

/**
 * This Enumeration is used to keep the possible states that the
 * <code>Worker</code> will could be.
 * 
 * @since 4.0
 */
public enum WorkerStatus {

	/**
	 * Indicate that some user is using the machine. So, the <code>Worker</code>
	 * could not execute any command or transfer files. From this state the
	 * <code>Worker</code> could pass to the <code>IDLE</code> or
	 * <code>STOPPED</code> states.
	 */
	OWNER,

	/**
	 * Indicate that the <b>Peer</b> that controls this <code>Worker</code>
	 * has had set and the <code>Worker</code> has not been allocated yet.
	 * From this state the <code>Worker</code> could pass to
	 * <code>OWNER</code>, <code>ALLOCATED</code> or <code>STOPPED</code>
	 * states.
	 */
	IDLE,

	/**
	 * Indicate that the <code>Worker</code> is allocated to be used (e.g.
	 * transfer files, execute commands) by some <code>WorkerClient</code>.
	 * From this state the <code>Worker</code> could pass to
	 * <code>RUNNING_COMMAND</code>, <code>IDLE</code>, <code>OWNER</code>
	 * or <code>STOPPED</code> states.
	 */
	ALLOCATED_FOR_BROKER,
	
	ALLOCATED_FOR_PEER,

	/**
	 * Indicate that the <code>Worker</code> has stopped it activities. From
	 * this state the <code>Worker</code> must not pass to any other state.
	 */
	STOPPED,

	/**
	 * Indicates that the <code>Worker</code> has entered into an error state.
	 */
	ERROR;
	
	public boolean isAllocated() {
		return this.equals(ALLOCATED_FOR_BROKER) || this.equals(ALLOCATED_FOR_PEER);
	}
	
	/**
	 * This method implements the state precedence order. In other words,
	 * verifies if some state comes after other.
	 * 
	 * @param after A state to be verified.
	 * @return True if the <code>after</code> state comes after (in state
	 *         precedence order) than the current state. False, otherwise.
	 */
	public boolean comesBefore( WorkerStatus after ) {

		switch ( this ) {
			case OWNER:
				return after.equals( IDLE )
				|| after.equals( ALLOCATED_FOR_BROKER )
				|| after.equals( ALLOCATED_FOR_PEER );
			case IDLE:
				return after.equals( ALLOCATED_FOR_BROKER )
				|| after.equals( ALLOCATED_FOR_PEER );
			case STOPPED:
				return false;
			default:
				return false;
		}
	}


	/**
	 * This method implements the state precedence order
	 * 
	 * @param before A state to be verified.
	 * @return True if the <code>before</code> state comes before (in state
	 *         precedence order) than the current state. False, otherwise.
	 */
	public boolean comesAfter( WorkerStatus before ) {

		return before.comesBefore( this );
	}

}
