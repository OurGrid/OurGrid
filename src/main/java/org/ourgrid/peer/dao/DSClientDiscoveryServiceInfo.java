package org.ourgrid.peer.dao;

import java.io.Serializable;

import org.ourgrid.common.util.StringUtil;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;

/**
 * @author guilherme, tarciso
 * DSClientDiscoveryServiceInfo is a class for an object which represents
 * the DS in the DSClient DAO.
 */
public class DSClientDiscoveryServiceInfo implements Comparable<DSClientDiscoveryServiceInfo>, Serializable {

	private static final long serialVersionUID = 1L;
	private String dsAddress;
	private boolean isOverloaded;
	
	/**
	 * Constructor
	 * @param dsAddress the address (ServiceID) of the DS
	 */
	public DSClientDiscoveryServiceInfo(String dsAddress) {
		this.dsAddress = dsAddress;
		this.isOverloaded = false;
	}
	
	public String getDsAddress() {
		return dsAddress;
	}
	public void setDsAddress(String dsAddress) {
		this.dsAddress = dsAddress;
	}
	
	/**
	 * Verifies if the DS is overloaded (has reached its maximum capacity of peer storage)
	 * @return true if the DS is overloaded; false otherwise
	 */
	public boolean isOverloaded() {
		return isOverloaded;
	}
	public void setOverloaded(boolean isOverloaded) {
		this.isOverloaded = isOverloaded;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (! (object instanceof DiscoveryServiceInfo)) {
			return false;
		}
		
		DiscoveryServiceInfo dsInfo = (DiscoveryServiceInfo) object;
		
		
		return dsInfo.getDsAddress().equals(this.dsAddress);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DSClientDiscoveryServiceInfo otherDSInfo) {
		return this.dsAddress.compareTo(otherDSInfo.dsAddress);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return (StringUtil.addressToUserAtServer(dsAddress) + "\tStatus: " + (isOverloaded? "OVERLOADED":"NOT OVERLOADED")); 
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public  int hashCode(){
		return dsAddress.hashCode();
	}
	
}