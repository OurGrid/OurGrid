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

public class IncomingTransferFailedMessageMatcher implements IArgumentMatcher {
	
	private final long amountData;
	
	private final String filePath;
	
	private final String senderPublicKey;
	
	public IncomingTransferFailedMessageMatcher(String filePath, long amountData, String senderPublicKey) {
		this.filePath = filePath;
		this.amountData = amountData;
		this.senderPublicKey = senderPublicKey;
	}

	public void appendTo(StringBuffer arg0) {
		
	}

	public boolean matches(Object arg0) {
		if(arg0.getClass() != String.class) {
			return false;
		}
		
		String anotherMessage = (String) arg0;
		
		anotherMessage = anotherMessage.replace('\\', '/');
		
		boolean a = anotherMessage.startsWith("Error while trying to receive file from client. Reporting error to client." +
				" Client public key: [" + senderPublicKey + "]. Destination path: [");
		
		boolean b = anotherMessage.endsWith("Amount of data received: " + amountData + " bytes.");
		
		return anotherMessage.startsWith("Error while trying to receive file from client. Reporting error to client." +
				" Client public key: [" + senderPublicKey + "]. Destination path: [") &&
				anotherMessage.endsWith(" Amount of data received: " + amountData + " bytes.");
		
		/*Pattern pattern = Pattern.compile("Error while trying to receive file from client. Reporting error to client." +
				" Client public key: \\[" + senderPublicKey + "\\]. Destination path: \\[.+" + filePath + "\\]." +
						" Amount of data received: " + amountData + " bytes.");
		
		Matcher matcher = pattern.matcher(anotherMessage);
		
		return matcher.matches();*/
	}
	
	public static String eqMatcher(String filePath, long amountData, String senderPublicKey) {
		EasyMock.reportMatcher(new IncomingTransferFailedMessageMatcher(filePath, amountData, senderPublicKey));
		return null;
	}

}
