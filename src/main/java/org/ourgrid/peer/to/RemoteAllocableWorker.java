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
package org.ourgrid.peer.to;

import java.util.List;

import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.response.RemoteWorkForBrokerResponseTO;

/**
 */
public class RemoteAllocableWorker extends AllocableWorker {

	private final String workerAddress;
	private final String workerClientAddress;
	private String providerAddress;
	private WorkerSpecification spec;
	private String workerPubKey;

	public void workForBroker(List<IResponseTO> responses) {
		RemoteWorkForBrokerResponseTO workForBroker = new RemoteWorkForBrokerResponseTO();
		workForBroker.setWorkerManagementAddress(getWorkerAddress());
		workForBroker.setWorkerManagementClientAddress(getWorkerClientAddress());
		workForBroker.setBrokerPublicKey(getConsumerPublicKey());
		
		responses.add(workForBroker);
	}
	
	public RemoteAllocableWorker(String workerAddress, String workerClientAddress, 
			String providerAddress, String providerDN, WorkerSpecification spec) {
		
		this.workerAddress = workerAddress;
		this.workerClientAddress = workerClientAddress;
		this.providerAddress = providerAddress;
		this.spec = spec;
		setProviderCertificateDN(providerDN);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.peer.to.AllocableWorker#getStatus()
	 */
	@Override
	public LocalWorkerState getStatus() {
		return LocalWorkerState.IN_USE;
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.peer.to.AllocableWorker#setStatus(org.ourgrid.common.interfaces.to.LocalWorkerState)
	 */
	@Override
	public void setStatus(LocalWorkerState state) {}

	/* (non-Javadoc)
	 * @see org.ourgrid.peer.to.AllocableWorker#isWorkerLocal()
	 */
	@Override
	public boolean isWorkerLocal() {
		return false;
	}

	@Override
	public WorkerSpecification getWorkerSpecification() {
		return spec;
	}
	
	public void setWorkerSpec(WorkerSpecification newSpec) {
		this.spec = newSpec;
	}

	@Override
	public Class<?> getMonitorableType() {
		return RemoteWorkerManagement.class;
	}

	@Override
	public String getMonitorName() {
		return PeerConstants.REMOTE_WORKER_MANAGEMENT_CLIENT;
	}

	public WorkerSpecification getSpec() {
		return spec;
	}

	public void setSpec(WorkerSpecification spec) {
		this.spec = spec;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public String getWorkerClientAddress() {
		return workerClientAddress;
	}

	public String getProviderAddress() {
		return providerAddress;
	}

	public String getWorkerPubKey() {
		return workerPubKey;
	}

	@Override
	public void setProviderAddress(String providerAddress) {
		this.providerAddress = providerAddress;
	}

	public void setWorkerPubKey(String workerPubKey) {
		this.workerPubKey = workerPubKey;
	}
	
}
