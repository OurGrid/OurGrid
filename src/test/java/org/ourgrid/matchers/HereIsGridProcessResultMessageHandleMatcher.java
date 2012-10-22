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
import org.ourgrid.broker.communication.actions.HereIsGridProcessResultMessageHandle;
import org.ourgrid.common.executor.ExecutorResult;

public class HereIsGridProcessResultMessageHandleMatcher implements IArgumentMatcher {
	
	private final ExecutorResult executorResult;
	
	public HereIsGridProcessResultMessageHandleMatcher(HereIsGridProcessResultMessageHandle handle) {
		this.executorResult = handle.getResult();
	}

	public void appendTo(StringBuffer arg0) {
		
	}

	public boolean matches(Object arg0) {
		if(arg0.getClass() != HereIsGridProcessResultMessageHandle.class) {
			return false;
		}
		
		HereIsGridProcessResultMessageHandle other = (HereIsGridProcessResultMessageHandle) arg0;

		if (this.executorResult == null && other.getResult() == null)
			return true;
		
		return this.executorResult.equals(other.getResult());
	}
	
	public static HereIsGridProcessResultMessageHandle eqMatcher(HereIsGridProcessResultMessageHandle handle) {
		EasyMock.reportMatcher(new HereIsGridProcessResultMessageHandleMatcher(handle));
		return null;
	}
}
