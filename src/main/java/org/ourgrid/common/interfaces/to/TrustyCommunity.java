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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ourgrid.reqtrace.Req;

/**
 * This entity stands for a trusty community.
 * 
 * This class should not be modified without a strong reason. It is used
 * in serialisation code using the XStream framework. The name of instance
 * fields are mapped into xml archive. Changes in this code must be reflected
 * in <code>TrustCommunitiesFileManipulator</code>.   
 * 
 * Letter for a future programmer: Look for the immutability of this object, please
 * do not change this.
 */
@Req({"REQ110"})
public class TrustyCommunity implements Comparable<TrustyCommunity>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String name;
	private final int priority;
	private LinkedList<TrustyPeerInfo> peers;
	
	/**
	 * @param communityName
	 * @param priority
	 * @param peers 
	 */
	private TrustyCommunity(String communityName, int priority, List<TrustyPeerInfo> peers) {

		if(communityName == null) {
			throw new IllegalArgumentException("The community same must be a non-null value");
		}
		
		if(priority <= 0) {
			throw new IllegalArgumentException("The community priority must be a positive integer");
		}
		
		if(peers == null)  {
			throw new IllegalArgumentException("The community entities must be a a non-null value");
		}
		
		this.name = communityName;
		this.priority = priority;
		this.peers = new LinkedList<TrustyPeerInfo>();
		this.peers.addAll(peers);
		
		Collections.sort(this.peers);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @return the entities
	 */
	public List<TrustyPeerInfo> getEntities() {
		
		if(this.peers == null) {
			this.peers = new LinkedList<TrustyPeerInfo>();
		}
		
		return Collections.unmodifiableList(this.peers);
	}
	
	/**
	 * @param peerPubKey
	 * @return
	 */
	public boolean containsPeer(String peerPubKey) {
		
		for (TrustyPeerInfo peerInfo : peers) {
			if(peerInfo.getEntityPubKey().equals(peerPubKey)) {
				return true;
			}
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((peers == null) ? 0 : peers.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + priority;
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
		final TrustyCommunity other = (TrustyCommunity) obj;
		if (peers == null) {
			if (other.peers != null)
				return false;
		} else if (!peers.equals(other.peers))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (priority != other.priority)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Trust community name<"+name+">"+" priority<"+priority+">"+"\n entities <"+peers+">";
	}
	
	/**
	 * The comparison is done using the natural order of priority number. If two
	 * {@link TrustyCommunity} have the same priority number the comparison is done
	 * the lexicographic name order.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(TrustyCommunity o) {

		if(o != null) {
			int firstComparation = o.priority - this.priority;
			return (firstComparation != 0) ? firstComparation : this.name.compareTo(o.name);
		}
		
		throw new NullPointerException();
	}

	public static class Builder{

		private String communityNamebuild;
		private int prioritybuild;
		private List<TrustyPeerInfo> entitiesbuild;
		
		public Builder(String communityName, int priority){
			communityNamebuild = communityName;
			prioritybuild = priority;
			entitiesbuild = new LinkedList<TrustyPeerInfo>();
		}
		
		public Builder addEntity(String entityName, String entityPubKey){
			entitiesbuild.add(new TrustyPeerInfo(entityName, entityPubKey));
			return this;
		}
		
		public TrustyCommunity build(){
			return new TrustyCommunity(communityNamebuild, prioritybuild, entitiesbuild);
		}
	}

}
