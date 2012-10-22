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
package org.ourgrid.common.interfaces.status;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class RemoteWorkerInfo extends WorkerInfo {

	private static final long serialVersionUID = 40L;

	private String providerAddress;


	public RemoteWorkerInfo( WorkerSpecification workerSpec, String providerAddress, String consumerAddress ) {

		super( workerSpec, LocalWorkerState.IN_USE, consumerAddress );
		this.providerAddress = providerAddress == null ? "-" : providerAddress;
	}


	public String getProviderAddress() {

		return this.providerAddress;
	}


	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((this.providerAddress == null) ? 0 : this.providerAddress.hashCode());
		return result;
	}


	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( !super.equals( obj ) )
			return false;
		if ( !(obj instanceof RemoteWorkerInfo) )
			return false;
		final RemoteWorkerInfo other = (RemoteWorkerInfo) obj;

		if ( !(this.providerAddress == null ? other.providerAddress == null : this.providerAddress.equals( other.providerAddress )) )
			return false;
		return true;
	}
}
