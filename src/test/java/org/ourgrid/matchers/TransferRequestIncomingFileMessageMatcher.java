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

public class TransferRequestIncomingFileMessageMatcher implements IArgumentMatcher {
	
	private final String filePath;
	
	private final String senderPubKey;

	public TransferRequestIncomingFileMessageMatcher(String filePath, String senderPubKey) {
		this.filePath = filePath;
		this.senderPubKey = senderPubKey;
	}

	public void appendTo(StringBuffer arg0) {
		
	}

	public boolean matches(Object arg0) {
		if(arg0.getClass() != String.class) {
			return false;
		}
		
		String anotherMessage = (String) arg0;
		
		anotherMessage = anotherMessage.replace('\\', '/');
		
		return anotherMessage.startsWith("The client tried to transfer a file that is being received. This message was ignored." +
				" Client public key: [" + senderPubKey + "]. File destination:");
		
		/*Pattern pattern = Pattern.compile("The client tried to transfer a file that is being received. This message was ignored." +
				" Client public key: \\[" + senderPubKey + "\\]. File destination: \\[.+" + filePath + "\\].");
		
		Matcher matcher = pattern.matcher(anotherMessage);
		
		return matcher.matches();*/
	}
	
	public static String eqMatcher(String filePath, String senderPubKey) {
		EasyMock.reportMatcher(new TransferRequestIncomingFileMessageMatcher(filePath, senderPubKey));
		return null;
	}

}
