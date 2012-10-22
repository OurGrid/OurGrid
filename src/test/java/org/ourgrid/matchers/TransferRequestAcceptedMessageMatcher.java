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

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.easymock.IArgumentMatcher;
import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;

import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;

public class TransferRequestAcceptedMessageMatcher implements IArgumentMatcher {
	
	private final String directoryRoot;
	
	private final String filePath;
	
	private final String senderPubKey;

	private final TransferHandle handle;
	
	private final boolean toPlaypen;
	
	public TransferRequestAcceptedMessageMatcher(String directoryRoot, String filePath, TransferHandle handle,
			String senderPubKey, boolean toPlaypen) {
		this.directoryRoot = directoryRoot;
		this.filePath = filePath.replace('\\', '/');
		this.senderPubKey = senderPubKey;
		this.handle = handle;
		this.toPlaypen = toPlaypen;
	}

	public void appendTo(StringBuffer arg0) {
		
	}

	public boolean matches(Object arg0) {
		if(arg0.getClass() != String.class) {
			return false;
		}
		
		String anotherMessage = (String) arg0;
		
		anotherMessage = anotherMessage.replace('\\', '/');
		
		/*return anotherMessage.startsWith("A transfer request from the client was successfully accepted." +
				" Client public key: [" + senderPubKey + "]. File destination: [") &&
				anotherMessage.endsWith("Handle: " + handle + ".");*/
		
		String rootPath = new File(directoryRoot).getAbsolutePath();
		rootPath = rootPath.replace('\\', '/');
		Pattern pattern = Pattern.compile("A transfer request from the client was successfully accepted." +
				" Client public key: \\[" + senderPubKey + "\\]. File destination: \\[" + rootPath  +
				(toPlaypen? ".worker-.+" : "/" + WorkerAcceptanceUtil.generateHexadecimalCodedString(senderPubKey))
				+ "/" + filePath + "\\]. Handle: " + handle + ".");
		
		Matcher matcher = pattern.matcher(anotherMessage);
		
		return matcher.matches();
	}
	
	public static String eqMatcher(String playpenRoot, String filePath, TransferHandle handle,
			String senderPubKey, boolean toPlaypen) {
		EasyMock.reportMatcher(new TransferRequestAcceptedMessageMatcher(playpenRoot, filePath,
				handle, senderPubKey, toPlaypen));
		return null;
	}

}
