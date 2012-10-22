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


public class PeerUserReference {

//	private LocalWorkerProviderClient client;
//	private DeploymentID deploymentId;
//
//	public PeerUserReference(LocalWorkerProviderClient client,
//			DeploymentID deploymentId) {
//		this.client = client;
//		this.deploymentId = deploymentId;
//	}
//
//	/**
//	 * @return the client
//	 */
//	public LocalWorkerProviderClient getClient() {
//		return client;
//	}
//
//	/**
//	 * @param client the client to set
//	 */
//	public void setClient(LocalWorkerProviderClient client) {
//		this.client = client;
//	}
//
//	/**
//	 * @return the deploymentId
//	 */
//	public DeploymentID getDeploymentId() {
//		return deploymentId;
//	}
//
//	/**
//	 * @param deploymentId the deploymentId to set
//	 */
//	public void setDeploymentId(DeploymentID deploymentId) {
//		this.deploymentId = deploymentId;
//	}
	
	private String workerProviderClientAddress;
	
	
	public PeerUserReference(String workerProviderClientAddress) {
		this.workerProviderClientAddress = workerProviderClientAddress;
	}
	

	public void setWorkerProviderClientAddress(
			String workerProviderClientAddress) {
		this.workerProviderClientAddress = workerProviderClientAddress;
	}

	public String getWorkerProviderClientAddress() {
		return workerProviderClientAddress;
	}
}
