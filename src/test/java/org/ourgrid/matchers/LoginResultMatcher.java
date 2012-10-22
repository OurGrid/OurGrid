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
import org.ourgrid.common.BrokerLoginResult;

public class LoginResultMatcher implements IArgumentMatcher {
	
	private static String NO_ERROR = null;

	String expectedErrorMessage;

	public LoginResultMatcher() {
		this(NO_ERROR);
	}

	public LoginResultMatcher(String errorMessage) {
		this.expectedErrorMessage = errorMessage;
	}


	public boolean matches(Object arg0) {
		if ( !(arg0 instanceof BrokerLoginResult) ) {
			return false;
		}
		
		BrokerLoginResult lr = (BrokerLoginResult) arg0;
		if(expectedErrorMessage == NO_ERROR){
			return !lr.hasAnErrorOcurred();
		}
		String realErrorMessage = lr.getErrorMessage();
	
		return expectedErrorMessage.equals(realErrorMessage);
	}
	
	public void appendTo( StringBuffer arg0 ) {

	}

	public static BrokerLoginResult eqErrorMessage(String errorMessage) {
		EasyMock.reportMatcher(new LoginResultMatcher(errorMessage));
		return null;
	}

	public static BrokerLoginResult noError() {
		EasyMock.reportMatcher(new LoginResultMatcher());
		return null;
	}

}
