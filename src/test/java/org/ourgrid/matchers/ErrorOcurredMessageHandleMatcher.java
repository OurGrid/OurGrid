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

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.ourgrid.broker.communication.actions.ErrorOcurredMessageHandle;

public class ErrorOcurredMessageHandleMatcher implements IArgumentMatcher {
	
	private final ErrorOcurredMessageHandle handle;
	
	public ErrorOcurredMessageHandleMatcher(ErrorOcurredMessageHandle handle) {
		this.handle = handle;
	}

	public void appendTo(StringBuffer arg0) {
		
	}

	public boolean matches(Object arg0) {
		if(arg0.getClass() != ErrorOcurredMessageHandle.class) {
			return false;
		}
		
		ErrorOcurredMessageHandle other = (ErrorOcurredMessageHandle) arg0;
		
		if (!this.handle.getActionName().equals(other.getActionName())) {
			return false;
		}
		
		return this.handle.getGridProcessError().equals(other.getGridProcessError());
	}
	
	public static ErrorOcurredMessageHandle eqMatcher(ErrorOcurredMessageHandle handle) {
		EasyMock.reportMatcher(new ErrorOcurredMessageHandleMatcher(handle));
		return null;
	}
}
