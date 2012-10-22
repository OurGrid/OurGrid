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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ourgrid.common.util.CommonUtils;

/**
 * This entity handles the input and output entries for a task.
 * 
 * @see IOEntry Created on Jul 1, 2004
 */
public class IOBlock implements Serializable {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 33L;

	Map<String,ArrayList<IOEntry>> entries;

	/**
	 * An empty constructor
	 */
	public IOBlock() {
		entries = CommonUtils.createSerializableMap();
	}


	/**
	 * Inserts a new input/output entry at this block of I/O commands.
	 * 
	 * @param condition The condition that tells if the I/O command will be
	 *        executed. It happens only when the command was written inside a
	 *        if/else block.
	 * @param entry A IOEntry object that defines the command and the paths of
	 *        origin and destiny of a file.
	 */
	public void putEntry( String condition, IOEntry entry ) {

		ArrayList<IOEntry> set;
		if ( entries.containsKey( condition ) ) {
			set = entries.get( condition );
		} else {
			set = new ArrayList<IOEntry>();
		}
		set.add( entry );
		entries.put( condition, set );
	}


	/**
	 * Inserts a new input/output entry at this block of I/O commands, using as
	 * condition the empty string, that means this entry will always be used.
	 * 
	 * @param entry The input/output entry.
	 */
	public void putEntry( IOEntry entry ) {

		this.putEntry( "", entry );
	}


	/**
	 * Tells how many entries this I/O block has. Notice that the lenght means
	 * how many conditions blocks where inserted and not how many commands has
	 * been inserted.
	 * 
	 * @return How many entries this block has.
	 */
	public int length() {

		return entries.size();
	}


	/**
	 * Returns a collection with the entries related with a condition. To obtain
	 * all the conditions at this block use this.getConditions.
	 * 
	 * @param condition The condition that indexes all the entries that will be
	 *        used if it is true.
	 * @return The collection of entries related with a condition - null if the
	 *         condition does not exist.
	 */
	public List<IOEntry> getEntry( String condition ) {

		if ( entries.containsKey( condition ) ) {
			return entries.get( condition );
		}
		return null;

	}


	/**
	 * Returns all the conditions that indexes the entry blocks.
	 * 
	 * @return A iterator with all the valid conditions that indexes the entries
	 *         at this input/output block.
	 */
	public Iterator<String> getConditions() {

		return entries.keySet().iterator();
	}


	/**
	 * Returns a string representation of an IOBlock.
	 */
	@Override
	public String toString() {

		StringBuffer message = new StringBuffer();
		Iterator<String> it = entries.keySet().iterator();
		while ( it.hasNext() ) {
			String key = it.next();
			message.append( "    Condition: [" + key + "]\n" );
			Iterator<IOEntry> it1 = (entries.get( key )).iterator();
			while ( it1.hasNext() ) {
				message.append( "      IOEntry: [" + it1.next().toString() + "]\n" );
			}
		}
		return message.toString();
	}


	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.entries == null) ? 0 : this.entries.hashCode());
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
		final IOBlock other = (IOBlock) obj;
		if ( !(this.entries == null ? other.entries == null : this.entries.equals( other.entries )) )
			return false;
		return true;
	}


	public Map<String, IOEntry[]> getEntries() {
		
		Map<String, IOEntry[]> newMap = CommonUtils.createSerializableMap();
		
		if (this.entries != null) {
			for (Entry<String, ArrayList<IOEntry>> entry : this.entries.entrySet()) {
				newMap.put(entry.getKey(), getArray(entry.getValue()));
			}
		}	
		return newMap;
	}
	
	public void setEntries(Map<String, IOEntry[]> entries) {
		
		Map<String, ArrayList<IOEntry>> newMap = CommonUtils.createSerializableMap();
		
		if (entries != null) {
			for (Entry<String, IOEntry[]> entry : entries.entrySet()) {
				newMap.put(entry.getKey(),getList(entry.getValue()));
			}
		}
		this.entries = newMap;
	}
	
	private IOEntry[] getArray(ArrayList<IOEntry> list) {
		
		IOEntry[] array = new IOEntry[list.size()];
		
		int i = 0;
		for (IOEntry entry : list) {
			array[i] = entry;
			i++;
		}
		
		return array;
	}
	
	private ArrayList<IOEntry> getList(IOEntry[] array) {
		
		ArrayList<IOEntry> list = new ArrayList<IOEntry>();
		
		for (IOEntry entry : array) {
			list.add(entry);
		}
		
		return list;
	}

}
