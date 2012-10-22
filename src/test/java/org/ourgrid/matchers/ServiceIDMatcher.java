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
package org.ourgrid.matchers;

import org.easymock.IArgumentMatcher;
import org.easymock.classextension.EasyMock;

import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 */
public class ServiceIDMatcher implements IArgumentMatcher {

	private ServiceID eid;
	
	/**
	 * @param name 
	 * @param requestSpec
	 */
	public ServiceIDMatcher(ServiceID eid) {
		this.eid = eid;
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#appendTo(java.lang.StringBuffer)
	 */
	public void appendTo(StringBuffer arg0) {
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object arg0) {
		
		//TODO: NULL POINTER ?
		if ( !(ServiceID.class.isInstance(arg0)) ) {
			return false;
		}
		
		if (arg0 == null) return false; 
		
		ServiceID otherProcessor = (ServiceID)arg0;
	
		return eid.equals(otherProcessor);
	}

	public static <T> T eqMatcher(ServiceID eid) {
		EasyMock.reportMatcher(new ServiceIDMatcher(eid));
		return null;
	}
	
}
