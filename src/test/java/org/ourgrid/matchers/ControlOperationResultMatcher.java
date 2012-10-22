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

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;

public class ControlOperationResultMatcher implements IArgumentMatcher {
	
	private static String ANY_MESSAGE = null;
	private static Class<?> NO_EXCEPTION = null;
	private static String NO_RESULT = null;

	String result;
	String expectedErrorCauseMessage;
	Class<?> expectedErrorType;

	public ControlOperationResultMatcher() {
		this(ANY_MESSAGE, NO_EXCEPTION, NO_RESULT);
	}

	public ControlOperationResultMatcher(String result) {
		this(ANY_MESSAGE, NO_EXCEPTION, result);
	}

	public ControlOperationResultMatcher(Class<?> errorType) {
		this(ANY_MESSAGE, errorType, NO_RESULT);
	}

	public ControlOperationResultMatcher(String errorCause, Class<?> errorType) {
		this(errorCause, errorType, NO_RESULT);
	}

	public ControlOperationResultMatcher(String errorCause, Class<?> errorType, String result) {
		this.expectedErrorCauseMessage = errorCause;
		this.expectedErrorType = errorType;
		this.result = result;
	}


	public boolean matches(Object arg0) {
		if ( !(arg0 instanceof ControlOperationResult) ) {
			return false;
		}
		
		ControlOperationResult cor = (ControlOperationResult) arg0;
		Exception realErrorCause = cor.getErrorCause();
		
		String errorMessage = (realErrorCause == null) ? null : realErrorCause.getMessage();
		
		String result = (String) cor.getResult();
		
		return compareErrorType(realErrorCause) && compareErrorMessage(errorMessage) && compareResult(result);
	}
	
	public boolean compareErrorType(Exception e) {
		if (expectedErrorType == NO_EXCEPTION) {
			return e == null;
		}
		return expectedErrorType.isInstance(e);
	}
	
	public boolean compareErrorMessage(String msg) {
		if (expectedErrorCauseMessage == ANY_MESSAGE) {
			return true;
		}
		return expectedErrorCauseMessage.equals(msg);
	}

	public boolean compareResult(String result) {
		if (this.result == NO_RESULT) {
			return true;
		}
		return this.result.equals(result);
	}


	public void appendTo( StringBuffer arg0 ) {

	}

	public static ControlOperationResult eqCauseType(String errorCause, Class<?> errorType) {
		EasyMock.reportMatcher(new ControlOperationResultMatcher(errorCause, errorType));
		return null;
	}

	public static ControlOperationResult eqType(Class<?> errorType) {
		EasyMock.reportMatcher(new ControlOperationResultMatcher(errorType));
		return null;
	}

	public static ControlOperationResult noError() {
		EasyMock.reportMatcher(new ControlOperationResultMatcher());
		return null;
	}

	public static ControlOperationResult withResult(String result) {
		EasyMock.reportMatcher(new ControlOperationResultMatcher(result));
		return null;
	}

}
