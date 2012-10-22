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

import java.io.Serializable;

import org.ourgrid.reqtrace.Req;

/**
 * This class should not be modified without a strong reason. It is used
 * in serialisation code using the XStream framework. The name of instance
 * fields are maped into xml archive. Changes in this code must be reflected
 * in <code>TrustCommunitiesFileManipulator</code>.   
 */
@Req({"REQ110"})
public class TrustyPeerInfo implements Comparable<TrustyPeerInfo>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String name;
	private final String publickey;
	
	/**
	 * @param entityName
	 * @param entityPubKey
	 */
	public TrustyPeerInfo(String entityName, String entityPubKey) {
		
		if(entityName == null) {
			throw new IllegalArgumentException("The entity name must be a non-null reference");
		}
		
		if(entityPubKey == null) {
			throw new IllegalArgumentException("The entity name must be a non-null reference");
		}
		
		this.name = entityName;
		this.publickey = entityPubKey;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return name;
	}

	/**
	 * @return the entityPubKey
	 */
	public String getEntityPubKey() {
		return publickey;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(TrustyPeerInfo o) {
		
		if(o != null) {
			return this.name.compareTo(o.name);
		}
		
		throw new NullPointerException();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((publickey == null) ? 0 : publickey.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TrustyPeerInfo other = (TrustyPeerInfo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (publickey == null) {
			if (other.publickey != null)
				return false;
		} else if (!publickey.equals(other.publickey))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Entity: name<"+name+">"+" pubkey<"+publickey+">";
	}

}
