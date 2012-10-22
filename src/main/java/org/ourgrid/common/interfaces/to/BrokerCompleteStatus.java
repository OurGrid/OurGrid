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
package org.ourgrid.common.interfaces.to;

import org.ourgrid.common.status.CompleteStatus;
import org.ourgrid.reqtrace.Req;

public class BrokerCompleteStatus extends CompleteStatus {

	private static final long serialVersionUID = 40L;

	private JobsPackage jobsPackage;

	private PeersPackage peersPackage;

	private WorkersPackage workersPackage;
	
	public BrokerCompleteStatus() {}

	@Req("REQ068a")
	public BrokerCompleteStatus( JobsPackage jp, PeersPackage pp, WorkersPackage wp, long up,
									String configuration ) {
		super( up, configuration );
		this.jobsPackage = jp;
		this.peersPackage = pp;
		this.workersPackage = wp;
	}

	public JobsPackage getJobsPackage() {
		return jobsPackage;
	}

	public WorkersPackage getWorkersPackage() {
		return workersPackage;
	}

	public PeersPackage getPeersPackage() {
		return peersPackage;
	}

	public void setJobsPackage(JobsPackage jobsPackage) {
		this.jobsPackage = jobsPackage;
	}

	public void setPeersPackage(PeersPackage peersPackage) {
		this.peersPackage = peersPackage;
	}

	public void setWorkersPackage(WorkersPackage workersPackage) {
		this.workersPackage = workersPackage;
	}
}
