package org.ourgrid.matchers;
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
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

public class WorkerProvidersMatcher implements IArgumentMatcher {
	
	private List<String> workerProviders;
	
	public WorkerProvidersMatcher(List<String> workerProviders) {
		this.workerProviders = workerProviders;
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#appendTo(java.lang.StringBuffer)
	 */
	public void appendTo(StringBuffer arg0) {
		
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#matches(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public boolean matches(Object arg0) {
		
		if (arg0 == null) {
			return false; 
		}

		if ( !(List.class.isInstance(arg0)) ) {
			return false;
		}
		
		List<String> otherWorkerProviders = (List<String>) arg0;
		
		if (workerProviders.size() != otherWorkerProviders.size()) {
			return false;
		}
		
		for (String deploymentID : workerProviders) {
			boolean hasEqual = false;
			for (String otherServiceID : otherWorkerProviders) {
				if (deploymentID.equals(otherServiceID))
					hasEqual = true;
			}
			
			if (!hasEqual) 
				return false;
		}
		
		return true;

	}

	public static List<String> eqMatcher(List<String> workerProviders) {
		EasyMock.reportMatcher(new WorkerProvidersMatcher(workerProviders));
		return null;
	}
}
