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
package org.ourgrid.aggregator.business.dao;


/**
 * This class store some data of a Peer.
 */
public class AggregatorPeerStatusProvider {
	
	private String providerAddress;
	
//	private boolean isUp;
//	
	private boolean mustUpdate;
	
	private long lastUpdateTime;
	
	/**
	 * Constructor default of this class.
	 */
	public AggregatorPeerStatusProvider() {
//		this.setUp(false);
		this.lastUpdateTime = 0L;
	}
	
	/**
	 * Constructor of this class.
	 * @param providerAddress {@link String}
	 */
	public AggregatorPeerStatusProvider(String providerAddress) {
		this();
		this.providerAddress = providerAddress;
		this.setMustUpdate(true);
	}
	
	public boolean mustUpdate() {
		return mustUpdate;
	}

	public void setMustUpdate(boolean mustUpdate) {
		this.mustUpdate = mustUpdate;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public void setProviderAddress(String providerAddress) {
		this.providerAddress = providerAddress;
	}

	public String getProviderAddress() {
		return providerAddress;
	}

//	/**
//	 * This method set the state. If the state is down
//	 * change the mustUpdate to false.
//	 * @param isUp boolean
//	 */
//	public void setUp(boolean isUp) {
//		this.isUp = isUp;
//		if(!isUp){
//			this.setMustUpdate(false);
//		}
//	}
//
//	public boolean isUp() {
//		return isUp;
//	}
	
	@Override
	public int hashCode() {
		return providerAddress.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AggregatorPeerStatusProvider)){
			return false;
		}
		
		return same(((AggregatorPeerStatusProvider) obj).getProviderAddress(),
				this.getProviderAddress());
	}
	
	private boolean same(String st1, String st2){
		if(st1 == null && st2 == null){
			return true;
		}else if(st1 == null || st2 == null){
			return false;
		}else{
			return st1.equals(st2);
		}
	}
}
