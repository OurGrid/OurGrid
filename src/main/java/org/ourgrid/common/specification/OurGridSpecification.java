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
package org.ourgrid.common.specification;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public abstract class OurGridSpecification implements Serializable {

	private static final long serialVersionUID = 6271557263229270195L;

	protected Map<String,String> attributes;
	
	public OurGridSpecification() {
		
		this( new TreeMap<String, String>( ) );
	}
	
	public OurGridSpecification( Map<String,String> attributes ) {
		this.attributes = attributes;
	}
	
	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( !(obj instanceof OurGridSpecification) )
			return false;
		final OurGridSpecification other = (OurGridSpecification) obj;

		if ( !this.attributes.equals( other.attributes ) )
			return false;
		return true;
	}
	
	/**
	 * Returns the value of the attribute named as given.
	 * 
	 * @param attName the attribute name.
	 * @return the attribute value.
	 */
	public String getAttribute( String attName ) {
		
		if(attName == null){
			return null;
		}
		
		String value = this.attributes.get( attName.toLowerCase() );
		if ( value != null ) {
			return value;
		}
		return null;
	}

	/**
	 * Retrieves the map of attributes.
	 * 
	 * @return a map with all the attributes of this grid machine indexed by the
	 *         name of the attribute.
	 */
	public Map<String,String> getAttributes() {

		return this.attributes;
	}

	@Req("REQ010")
	public ServiceID getServiceID() {

		return new ServiceID( getUser(), getServer(), getModuleName(), getObjectName());
	}


	public String getLocation() {

		return getUserAndServer() + "/" + getModuleName();
	}


	public String getURL() {
		return getLocation() + "/" + getObjectName();
	}


	public String getUserAndServer() {
		return getUser() + "@" + getServer();
	}
	
	public void setUserAtServer(String userAtServer) {
		String[] userAtServerSplitted = userAtServer.split("@");
		putAttribute(OurGridSpecificationConstants.USERNAME, userAtServerSplitted[0]);
		putAttribute(OurGridSpecificationConstants.SERVERNAME, userAtServerSplitted[1]);
	}

	public String getUser() {
		return getAttribute( OurGridSpecificationConstants.USERNAME );
	}
	
	public String getServer() {
		return getAttribute( OurGridSpecificationConstants.SERVERNAME );
	}

	/**
	 * Checks if the given attribute name is contained in the machine attribute
	 * set.
	 * 
	 * @param name the attribute name.
	 * @return true if the attribute name already exists and false otherwise.
	 */
	public boolean hasAttribute( String name ) {

		return this.attributes.containsKey( name.toLowerCase() );
	}

	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.attributes == null) ? 0 : this.attributes.hashCode());
		return result;
	}
	
	public boolean isValid() {

		final String username = getAttribute( OurGridSpecificationConstants.USERNAME );
		final String servername = getAttribute( OurGridSpecificationConstants.SERVERNAME );
		if ( username != null && username != "" && servername != null && servername != "" ) {
			return DeploymentID.validate( getURL() );
		}
		return false;
	}


	/**
	 * Inserts a new attribute at this worker specification.
	 * 
	 * @param name the name of the new attribute. If the name already exists the
	 *        new value will substitute the old one.
	 * @param value the value for this attribute.
	 */
	public void putAttribute( String name, String value ) {
		this.attributes.put( name.toLowerCase(), value );
	}

	/**
	 * Inserts a set of attributes at this worker specification.
	 * 
	 * @param mapAttributes the attributes to be added.
	 */
	public void putAttributes( Map<String,String> mapAttributes ) {
		
		for(String key : mapAttributes.keySet()){
			this.putAttribute(key, mapAttributes.get(key));
		}
	}
	/**
	 * Removes the given attribute.
	 * 
	 * @param key The key to be removed.
	 */
	public void removeAttribute( String key ) {
		if(key == null){
			return;
		}
		
		this.attributes.remove( key.toLowerCase() );
	}

	@Override
	public String toString() {
		
		StringBuilder s = new StringBuilder();

		s.append( "Attributes: " + this.attributes.size() ).append( System.getProperty("line.separator") );
		
		for (Entry<String,String> entry : this.attributes.entrySet()) {
			s.append( entry.getKey() ).append( " : " ).append( entry.getValue() ).append( System.getProperty("line.separator") );
		}
		
		return s.toString();
	}

	protected abstract String getModuleName();

	protected abstract String getObjectName();

}
