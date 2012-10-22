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
package org.ourgrid.peer.to;


/**
 * Defines allocation priorities.
 * Local allocations are greater, followed by sub communities allocations 
 * and unknown remote sites allocations.
 * 
 * This class was designed to be immutable.
 */
public class Priority implements Comparable<Priority>{
    
    public static final Priority IDLE = new Priority(Range.IDLE);
    public static final Priority LOCAL_CONSUMER = new Priority(Range.ALLOC_FOR_LOCAL_REQUEST);
    public static final Priority UNKNOWN_PEER = new Priority(Range.ALLOC_FOR_UNKNOWN_COMMUNITY);

	private final Range range;
	private final int ordinal;

	/**
	 * @param range
	 * @param ordinal 
	 */
	public Priority(Range range, int ordinal){
		this.range = range;
		this.ordinal = ordinal;
	}
	
	/**
	 * Creates a <code>Priority</code> instance.
	 * @param range
	 * @return
	 */
	public Priority(Priority.Range range){
		this(range, 0);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Priority o) {
		
		if(o != null) {
			int rangeComparison = range.compareTo(o.range);
			return (rangeComparison != 0) ? rangeComparison : (o.ordinal - this.ordinal);
		}

		throw new NullPointerException();
	}

	public static enum Range {
		//the comparison order is defined by declaration order.
		IDLE, ALLOC_FOR_UNKNOWN_COMMUNITY, ALLOC_FOR_TRUST_COMMUNITY, ALLOC_FOR_LOCAL_REQUEST;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ordinal;
		result = prime * result + ((range == null) ? 0 : range.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		final Priority other = (Priority) obj;
		
		if (ordinal != other.ordinal) {
			return false;
		}
		if (range == null) {
			if (other.range != null) {
				return false;
			}
		} else if (!range.equals(other.range)) {
			return false;
		}
		
		return true;
	}
	
}
