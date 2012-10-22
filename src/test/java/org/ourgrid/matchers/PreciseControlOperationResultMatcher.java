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

public class PreciseControlOperationResultMatcher implements IArgumentMatcher {
	
	private static String ANY_MESSAGE = null;
	private static Class<?> NO_EXCEPTION = null;

	String expectedErrorCauseMessage;
	Class<?> expectedErrorType;

	public PreciseControlOperationResultMatcher() {
		this(ANY_MESSAGE, NO_EXCEPTION);
	}

	public PreciseControlOperationResultMatcher(Class<?> errorType) {
		this(ANY_MESSAGE, errorType);
	}

	public PreciseControlOperationResultMatcher(String errorCause, Class<?> errorType) {
		this.expectedErrorCauseMessage = errorCause;
		this.expectedErrorType = errorType;
	}


	public boolean matches(Object arg0) {
		if ( !(arg0 instanceof ControlOperationResult) ) {
			return false;
		}
		
		ControlOperationResult cor = (ControlOperationResult) arg0;
		Exception errorCause = cor.getErrorCause();
		
		Throwable preciseErrorCause = null;
		
		if (errorCause != null) {
			preciseErrorCause = cor.getErrorCause().getCause();
		}
		
		String preciseErrorMessage = (preciseErrorCause == null) ? null : preciseErrorCause.getMessage();
		
		return compareErrorType(preciseErrorCause) && compareErrorMessage(preciseErrorMessage);
	}
	
	public boolean compareErrorType(Throwable e) {
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


	public void appendTo( StringBuffer arg0 ) {

	}

	public static ControlOperationResult eqCauseType(String errorCause, Class<?> errorType) {
		EasyMock.reportMatcher(new PreciseControlOperationResultMatcher(errorCause, errorType));
		return null;
	}

	public static ControlOperationResult eqType(Class<?> errorType) {
		EasyMock.reportMatcher(new PreciseControlOperationResultMatcher(errorType));
		return null;
	}

	public static ControlOperationResult noError() {
		EasyMock.reportMatcher(new PreciseControlOperationResultMatcher());
		return null;
	}

}
