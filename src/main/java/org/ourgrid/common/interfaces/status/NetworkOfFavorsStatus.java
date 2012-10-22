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
package org.ourgrid.common.interfaces.status;

import static org.ourgrid.common.interfaces.Constants.LINE_SEPARATOR;

import java.io.Serializable;
import java.util.Map;

import org.ourgrid.peer.to.PeerBalance;

public class NetworkOfFavorsStatus implements Serializable {

	private static final long serialVersionUID = 40L;

	private final Map<String,PeerBalance> nofTable;


	public NetworkOfFavorsStatus( Map<String,PeerBalance> knownPeerBalances ) {

		this.nofTable = knownPeerBalances;
	}


	public Map<String,PeerBalance> getTable() {

		return this.nofTable;
	}


	public PeerBalance getBalance( String peer ) {

		return this.nofTable.get( peer );
	}


	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + this.nofTable.hashCode();
		return result;
	}


	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( !(obj instanceof NetworkOfFavorsStatus) )
			return false;
		final NetworkOfFavorsStatus other = (NetworkOfFavorsStatus) obj;
		if ( !this.nofTable.equals( other.nofTable ) )
			return false;
		return true;
	}


	@Override
	public String toString() {
		String format1 = " %1$-60s %2$-60s " + LINE_SEPARATOR;
		String format2 = " %1$-60s %2$-60s " + LINE_SEPARATOR;
		StringBuilder nof = new StringBuilder();

		if ( nofTable.keySet().size() > 0 ) {
			nof.append(String.format(format1, "Peer", "Balance"));
			
			for ( String eID : nofTable.keySet() ) {
				nof.append(String.format(format2, eID, nofTable.get( eID )));
			}
		} else {
			nof.append( "\tNO DATA AVAILABLE" + LINE_SEPARATOR );
		}

		return nof.toString();
	}
}
