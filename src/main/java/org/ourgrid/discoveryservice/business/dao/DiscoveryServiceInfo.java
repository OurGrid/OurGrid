package org.ourgrid.discoveryservice.business.dao;

import java.io.Serializable;

import org.ourgrid.common.util.StringUtil;

public class DiscoveryServiceInfo implements Comparable<DiscoveryServiceInfo>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5014767808853857806L;

	
	private String dsAddress;
	private boolean isUp;
	
	
	public DiscoveryServiceInfo(String dsAddress, boolean isUp) {
		this.dsAddress = dsAddress;
		this.isUp = isUp;
	}

	public DiscoveryServiceInfo(String dsAddress) {
		this(dsAddress, false);
	}
	
	
	public String getDsAddress() {
		return dsAddress;
	}

	public void setDsAddress(String dsAddress) {
		this.dsAddress = dsAddress;
	}

	public boolean isUp() {
		return isUp;
	}
	
	public void setAsDown() {
		this.isUp = false;
	}
	
	public void setAsUp() {
		this.isUp = true;
	}

	
	@Override
	public boolean equals(Object object) {
		if (! (object instanceof DiscoveryServiceInfo)) {
			return false;
		}
		
		DiscoveryServiceInfo dsInfo = (DiscoveryServiceInfo) object;
		
		
		return dsInfo.getDsAddress().equals(this.dsAddress);
	}

	@Override
	public int compareTo(DiscoveryServiceInfo otherDSInfo) {
		return this.dsAddress.compareTo(otherDSInfo.dsAddress);
	}
	
	@Override
	public String toString() {
		return (StringUtil.addressToUserAtServer(dsAddress) + "\tStatus: " + (isUp? "UP":"DOWN")); 
	}
	
	@Override
	public  int hashCode(){
		return dsAddress.hashCode();
	}
}
