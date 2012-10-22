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

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;

public class JobAddedMatcher  implements IArgumentMatcher {

	int jobId;
	
	public JobAddedMatcher(int jobId) {
		this.jobId = jobId;
	}
	
	public void appendTo(StringBuffer arg0) {
		
	}

	public boolean matches(Object arg0) {
		if ( !(arg0 instanceof ControlOperationResult) ) {
			return false;
		}
		
		ControlOperationResult result = (ControlOperationResult) arg0;
		
		Integer idJob = (Integer) result.getResult();
		
		return idJob != null && idJob.equals(this.jobId);
	}
	
	public static ControlOperationResult eqMatcher(int jobId) {
		EasyMock.reportMatcher(new JobAddedMatcher(jobId));
		return null;
	}

}
