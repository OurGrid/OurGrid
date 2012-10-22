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

public class StartWorkErrorMessageMatcher implements IArgumentMatcher {
	
	private final ServiceID brokerID;
	
	private final boolean playpenError;
	
	public StartWorkErrorMessageMatcher(ServiceID mgID, String problematicDir, boolean playpenError) {
		this.brokerID = mgID;
		this.playpenError = playpenError;
	}

	public void appendTo(StringBuffer arg0) {
		
	}

	public boolean matches(Object arg0) {
		if(arg0.getClass() != String.class) {
			return false;
		}
		String anotherMessage = (String) arg0;

		String pattern = "The client [" + brokerID + "] tried to start the work of this Worker, " +
						"but the " + (this.playpenError ? "playpen" : "storage") +  " directory ";
		
		return anotherMessage.startsWith(pattern) && anotherMessage.endsWith("cannot be created.");
	}
	
	public static String eqMatcher(ServiceID mgID, String problematicDir, boolean playpenError) {
		EasyMock.reportMatcher(new StartWorkErrorMessageMatcher(mgID, problematicDir, playpenError));
		return null;
	}

}
