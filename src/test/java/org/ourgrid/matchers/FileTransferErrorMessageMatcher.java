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

import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;

/**
 */

public class FileTransferErrorMessageMatcher implements IArgumentMatcher {

	private String errorCause;
	private String filePath;
	private long fileSize;
	private TransferHandle opHandle;
	private String senderPublicKey;
	
	public FileTransferErrorMessageMatcher(String errorCause, String filePath, long fileSize, 
			TransferHandle opHandle, String senderPublicKey) {
		
		this.errorCause = errorCause;
		this.filePath = filePath;
		this.fileSize = fileSize;
		this.opHandle = opHandle;
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
		
		String part = "The client tried to transfer the file, but error occured on solving path. " +
			"This message was ignored. Error cause: [" + errorCause + "]. File path: [" + filePath + "]. Size: " + fileSize +
			" bytes. Handle: " + opHandle + ". Client public key: [" + senderPublicKey + "].";
		
		if (errorCause != null) {
		
			if (!errorCause.startsWith("File path is not relative to")) {
				return msg.equals(part);
			} else {
				return msg.startsWith("The client tried to transfer the file, but error occured on solving path. " +
						"This message was ignored. Error cause: [") && msg.endsWith("]. File path: [" + filePath + "]. Size: " + fileSize +
			" bytes. Handle: " + opHandle + ". Client public key: [" + senderPublicKey + "]."); 
			}
		}
		

		return false;
	}

	public static String eqMatcher(String errorCause, String filePath, long fileSize, TransferHandle opHandle, String senderPublicKey) {
		EasyMock.reportMatcher(new FileTransferErrorMessageMatcher(errorCause, filePath, fileSize, opHandle, senderPublicKey));
		return null;
	}

}
