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

/**
 */

public class FileRecoverErrorMessageMatcher implements IArgumentMatcher {

	private String errorCause;
	private String filePath;
	private String senderPublicKey;
	
	public FileRecoverErrorMessageMatcher(String errorCause, String filePath, String senderPublicKey) {
		
		this.errorCause = errorCause;
		this.filePath = filePath;
		this.senderPublicKey = senderPublicKey;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.easymock.IArgumentMatcher#appendTo(java.lang.StringBuffer)
	 */
	public void appendTo(StringBuffer arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.easymock.IArgumentMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object arg0) {

		if (!(String.class.isInstance(arg0))) {
			return false;
		}

		if (arg0 == null) {
			return false;
		}
		
		String msg = (String) arg0;
		
		if (errorCause != null) {
			
			boolean matches = msg.startsWith("The client tried to recover the files, but a error occured on solving path. " +
					"This message was ignored. Error cause: [") && msg.endsWith(" Client public key: [" + senderPublicKey + "].");
			
			int indexErrorCause = msg.indexOf("Error cause: [");
			String errorCauseMsg = msg.substring(indexErrorCause + 14, indexErrorCause + 28);
			return matches && errorCause.startsWith(errorCauseMsg);
		}

		return false;
	}

	public static String eqMatcher(String errorCause, String filePath, String senderPublicKey) {
		EasyMock.reportMatcher(new FileRecoverErrorMessageMatcher(errorCause, filePath, senderPublicKey));
		return null;
	}

}
