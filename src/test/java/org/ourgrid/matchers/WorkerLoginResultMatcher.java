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
import org.ourgrid.common.WorkerLoginResult;

public class WorkerLoginResultMatcher implements IArgumentMatcher {
	
	private static String NO_RESULT = null;

	String expectedResultMessage;

	public WorkerLoginResultMatcher() {
		this(NO_RESULT);
	}

	public WorkerLoginResultMatcher(String errorMessage) {
		this.expectedResultMessage = errorMessage;
	}


	public boolean matches(Object arg0) {
		if ( !(arg0 instanceof WorkerLoginResult) ) {
			return false;
		}
		
		WorkerLoginResult lr = (WorkerLoginResult) arg0;
		if(expectedResultMessage == NO_RESULT){
			return lr.getResultMessage() == null;
		}
		String realMessage = lr.getResultMessage();
	
		return expectedResultMessage.equals(realMessage);
	}
	
	public void appendTo( StringBuffer arg0 ) {

	}

	public static WorkerLoginResult eqErrorMessage(String errorMessage) {
		EasyMock.reportMatcher(new WorkerLoginResultMatcher(errorMessage));
		return null;
	}

	public static WorkerLoginResult noError() {
		EasyMock.reportMatcher(new WorkerLoginResultMatcher(WorkerLoginResult.OK));
		return null;
	}

}
