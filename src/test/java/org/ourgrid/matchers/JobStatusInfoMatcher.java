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
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.common.specification.job.JobSpecification;

public class JobStatusInfoMatcher  implements IArgumentMatcher {

	int jobId;
	JobSpecification jobSpec;
	
	
	public JobStatusInfoMatcher(int jobId) {
		this.jobId = jobId;
	}
	
	public void appendTo(StringBuffer arg0) {
		
	}

	public boolean matches(Object arg0) {
		if ( !(arg0 instanceof JobStatusInfo) ) {
			return false;
		}
		
		JobStatusInfo jobStats = (JobStatusInfo) arg0;
		
		jobSpec = jobStats.getJobSpec();
		
		if (this.jobSpec == null) {
			if (jobSpec != null) {
				return false;
			}
		} else {
			if (jobSpec == null) {
				return false;
			}
			
			if (!this.jobSpec.equals(jobStats.getJobSpec())) {
				return false;
			}
		}
		
		 
		return this.jobId == jobStats.getJobId();
	}
	
	public static JobStatusInfo eqMatcher(int jobId, JobSpecification jobSpec) {
		EasyMock.reportMatcher(new JobStatusInfoMatcher(jobId));
		return null;
	}

}
