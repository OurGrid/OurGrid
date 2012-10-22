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
package org.ourgrid.common.status;

import java.io.Serializable;

import org.ourgrid.reqtrace.Req;

public abstract class CompleteStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long upTime;

	private String configuration;
	
	public CompleteStatus() {}


	@Req({"REQ068a","REQ095"})
	public CompleteStatus( final long upTime, final String configuration ) {

		super();
		this.upTime = upTime;
		this.configuration = configuration;
	}

	public String getConfiguration() {
		return this.configuration;
	}

	public long getUpTime() {
		return this.upTime;
	}
	
	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + this.configuration.hashCode();
		result = PRIME * result + (int) (this.upTime ^ (this.upTime >>> 32));
		return result;
	}


	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( !(obj instanceof CompleteStatus) )
			return false;
		final CompleteStatus other = (CompleteStatus) obj;
		if ( !this.configuration.equals( other.configuration ) )
			return false;
		if ( this.upTime != other.upTime )
			return false;
		return true;
	}

}
