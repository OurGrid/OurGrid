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
package org.ourgrid.peer.business.dao;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecificationConstants;
import org.ourgrid.common.statistics.beans.peer.Attribute;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.beans.peer.Worker;
import org.ourgrid.common.statistics.beans.status.WorkerStatus;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.reqtrace.Req;

/**
 * Stores <code>LocalWorker</code>s, that is, workers that were set by the
 * 'setpeers' command
 */
public class LocalWorkersDAO {

	private static final long serialVersionUID = 1L;

	private final Map< String, String > workersPublicKeys = CommonUtils.createSerializableMap();

	/**
	 * Get the LocalWorker of a Worker
	 * 
	 * @param entityID The entityID of the Worker
	 * @return the LocalWorker
	 */
	@Req({"REQ025","REQ019"})
	public LocalWorker getLocalWorker(List<IResponseTO> responses, String workerUserAtServer) {
		
		Worker worker = PeerDAOFactory.getInstance().getWorkerDAO().findActiveWorker(responses, workerUserAtServer);
		
		if (worker == null) {
			return null;
		}
		
		Map< String, String > attributes = CommonUtils.createSerializableMap();
		Map< String, String > annotations = CommonUtils.createSerializableMap();

		for ( Attribute att : worker.getAttributes() ) {
			if ( att.getIsAnnotation() ) {
				annotations.put( att.getProperty(), att.getValue() );
			} else {
				attributes.put( att.getProperty(), att.getValue() );
			}
		}
		
		WorkerSpecification specification;
		
		String expressionValue = attributes.get( WorkerSpecificationConstants.EXPRESSION );
		if(expressionValue != null && !(expressionValue.length() == 0)){
			specification = new WorkerSpecification( expressionValue );
		}else{
			specification = new WorkerSpecification( attributes, annotations );
		}

		String workerPublicKey = workersPublicKeys.get( workerUserAtServer );
		
		LocalWorker localWorker = new LocalWorker(specification, workerUserAtServer, workerPublicKey);
		WorkerStatus currentStatus = worker.getStatus();

		if ( currentStatus != null ) {
			LocalWorkerState status = LocalWorkerState.parse( currentStatus );
			localWorker.setStatus( status );
		}
		
		return localWorker;
	}

	/**
	 * Get the LocalWorkers of this peer on the specified status
	 * @param status The status to filter the LocalWorkers
	 * @return All of the LocalWorkers in the specified status
	 */
	@Req("REQ036")
	public Collection<LocalWorker> getLocalWorkers(List<IResponseTO> responses,
			String peerUserAtServer, LocalWorkerState status) {
		Collection<LocalWorker> result = new LinkedList<LocalWorker>();
		
		Peer workerPeer = PeerDAOFactory.getInstance().getPeerDAO().findByID(responses, peerUserAtServer);
		
		Collection<Worker> allActiveWorkers = PeerDAOFactory.getInstance().getWorkerDAO().findAllActiveWorkers(
				responses, peerUserAtServer);
		for (Worker worker : allActiveWorkers) {
			WorkerStatus currentStatus = worker.getStatus();
			LocalWorkerState localState = LocalWorkerState.parse(currentStatus);
			if (localState.equals(status) && workerPeer.equals(worker.getPeer())) {
				result.add(getLocalWorker(responses, worker.getAddress()));
			}
			
		}
		
		return result;
	}
	
	/**
	 * Verifies if certain Worker is not already on this manager
	 * @param workerEntityID The worker entityID to be verified
	 * @return true if it's a new worker, false, otherwise
	 */
	@Req("REQ010")
	public boolean isNewWorker(List<IResponseTO> responses, String workerUserAtServer) {
		return getLocalWorker(responses, workerUserAtServer) == null;
	}
	

	/**
	 * Get the EntityIDs of all the workers stored on this manager
	 * @return The entityIDs of the store workers
	 */
	@Req("REQ010")
	public Collection<String> getLocalWorkersUserAtServer(List<IResponseTO> responses, String myUserAtServer){
		Collection<String> result = new LinkedList<String>();
		
		Peer workerPeer = PeerDAOFactory.getInstance().getPeerDAO().findByID(responses, myUserAtServer);
		
		Collection<Worker> allActiveWorkers = PeerDAOFactory.getInstance().getWorkerDAO().findAllActiveWorkers(responses, myUserAtServer);
		for (Worker worker : allActiveWorkers) {
			if (workerPeer.equals(worker.getPeer())) {
				//result.add(parseServiceID(worker.getAddress()));
				result.add(worker.getAddress());
			}	
		}
		
		return result;
	}
	
	public boolean isWorkerUp(String userAtServer) {
		return workersPublicKeys.get(userAtServer) != null;
	}

	public void workerIsUp(String userAtServer, String publicKey) {
		this.workersPublicKeys.put(userAtServer, publicKey);
	}

	public String workerIsDown(String userAtServer) {
		return this.workersPublicKeys.remove(userAtServer);
	}

}