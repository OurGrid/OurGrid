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
import org.ourgrid.worker.communication.processors.handle.RemoteExecuteMessageHandle;

/**
 */

public class RemoteExecuteMessageHandleMatcher implements IArgumentMatcher {

	private RemoteExecuteMessageHandle remoteExecuteMessageHandle;

	/**
	 * @param remoteExecuteMessageHandle
	 */
	public RemoteExecuteMessageHandleMatcher(RemoteExecuteMessageHandle remoteExecuteMessageHandle) {
		this.remoteExecuteMessageHandle = remoteExecuteMessageHandle;
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

		if (!(RemoteExecuteMessageHandle.class.isInstance(arg0))) {
			return false;
		}

		if (arg0 == null) {
			return false;
		}

		RemoteExecuteMessageHandle other = (RemoteExecuteMessageHandle) arg0;

		if (this.remoteExecuteMessageHandle.getRequestID() != other.getRequestID()) {
			return false;
		}

		return true;
	}

	public static RemoteExecuteMessageHandle eqMatcher(RemoteExecuteMessageHandle remoteExecuteMessageHandle) {
		EasyMock.reportMatcher(new RemoteExecuteMessageHandleMatcher(remoteExecuteMessageHandle));
		return null;
	}

}
