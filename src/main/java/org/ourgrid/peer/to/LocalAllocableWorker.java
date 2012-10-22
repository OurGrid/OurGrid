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

import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.response.WorkForBrokerResponseTO;
import org.ourgrid.peer.response.WorkForPeerResponseTO;
import org.ourgrid.reqtrace.Req;

/**
 * Local extension of an <code>AllocableWorker</code>
 */
@Req("REQ025")
public class LocalAllocableWorker extends AllocableWorker {

	private String localWorkerProviderAddress;
	private String workerManagementAddress;
	private LocalWorker localWorker;

	/**
	 * @param localWorker
	 * @param localWorkerProvider
	 */
	public LocalAllocableWorker(LocalWorker localWorker, String localWorkerProviderAddress, 
			String providerDN) {
		
		super();
		this.localWorkerProviderAddress = localWorkerProviderAddress;
		
		this.workerManagementAddress = localWorker.getWorkerManagementAddress();
		this.localWorker = localWorker;
		setStatus( LocalWorkerState.IDLE );
		setProviderCertificateDN(providerDN);
	}

	public String getWorkerUserAtServer() {
		return localWorker.getWorkerUserAtServer();
	}
	
	/**
	 * Gets the WorkerProvider responsible for this <code>AllocableWorker</code>
	 */
	@Override
	public String getProviderAddress() {
		return localWorkerProviderAddress;
	}
	
	@Override
	public void workForBroker(List<IResponseTO> responses) {
		WorkForBrokerResponseTO to = new WorkForBrokerResponseTO();
		to.setBrokerAddress(consumer.getConsumerAddress());
		to.setWorkerManagementAddress(workerManagementAddress);
		
		responses.add(to);
	}
	
	public String getWorkerAddress() {
		return this.workerManagementAddress;
	}
	
	/**
	 * @param remotePeerListener
	 * @param peerPubKey
	 */
	public void workForPeer(List<IResponseTO> responses, String peerPubKey){
		WorkForPeerResponseTO to = new WorkForPeerResponseTO();
		to.setPeerPublicKey(peerPubKey);
		to.setWorkerManagementAddress(workerManagementAddress);
		
		responses.add(to);
	}
	
	public void workForPeer(List<IResponseTO> responses, String peerPubKey, List<String> usersDN){
		WorkForPeerResponseTO to = new WorkForPeerResponseTO();
		to.setPeerPublicKey(peerPubKey);
		to.setUsersDN(usersDN);
		to.setWorkerManagementAddress(workerManagementAddress);
		
		responses.add(to);
	}
	
	@Override
	public LocalWorkerState getStatus() {
		return localWorker.getStatus();
	}
	
	@Override
	public void setStatus(LocalWorkerState state) {
		localWorker.setStatus(state);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.peer.to.AllocableWorker#isWorkerLocal()
	 */
	@Override
	public boolean isWorkerLocal() {
		return true;
	}

	@Override
	public WorkerSpecification getWorkerSpecification() {
		return localWorker.getWorkerSpecification();
	}

	@Override
	public Class<?> getMonitorableType() {
		return WorkerManagement.class;
	}

	@Override
	public String getMonitorName() {
		return PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME;
	}

	public LocalWorker getLocalWorker() {
		return localWorker;
	}

	@Override
	public void setProviderAddress(String providerAddress) {
		this.localWorkerProviderAddress = providerAddress;
	}

}
