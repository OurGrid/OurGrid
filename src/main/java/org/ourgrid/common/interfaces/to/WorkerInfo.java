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

import java.io.Serializable;

import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

/**
 * Stores information about a Worker
 */
@Req({"REQ36","REQ38a"})
public class WorkerInfo implements Serializable {

	private static final long serialVersionUID = 40L;

	private final WorkerSpecification workerSpec;

	private final LocalWorkerState workerStatus;

	private String consumerID;

	private String id;


	public WorkerInfo( WorkerSpecification workerSpec, LocalWorkerState workerStatus, String consumerID ) {

		this.workerSpec = workerSpec;
		this.workerStatus = workerStatus;
		this.id = DeploymentID.getLoginAndServer( workerSpec.getServiceID().toString() );
		this.consumerID = consumerID == null ? null : DeploymentID.getLoginAndServer(consumerID);
	}

	public WorkerInfo( WorkerSpecification workerSpec, LocalWorkerState workerStatus) {
		this (workerSpec, workerStatus, null);
	}

	
	public WorkerSpecification getWorkerSpec() {

		return this.workerSpec;
	}


	public String getId() {

		return this.id;
	}


	public LocalWorkerState getStatus() {

		return this.workerStatus;
	}


	public String getConsumerID() {

		return this.consumerID;
	}


	@Override
	public String toString() {

		return this.id == null	? "- Unknown name -"
								: this.id + "[ " + this.workerStatus + " ]\t " + 
								(this.consumerID == null	? "" : this.consumerID );
	}


	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((this.consumerID == null) ? 0 : this.consumerID.hashCode());
		result = PRIME * result + this.workerSpec.hashCode();
		result = PRIME * result + this.workerStatus.hashCode();
		return result;
	}


	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( !(obj instanceof WorkerInfo) )
			return false;
		final WorkerInfo other = (WorkerInfo) obj;

		if ( !this.workerSpec.equals( other.workerSpec ) )
			return false;
		if ( !this.workerStatus.equals( other.workerStatus ) )
			return false;
		if ( !(this.consumerID == null ? other.consumerID == null : this.consumerID.equals( other.consumerID )) )
			return false;
		return true;
	}
}
