/*
 * Copyright (C) 2011 Universidade Federal de Campina Grande
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

package org.ourgrid.common.statistics.control;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.statistics.beans.peer.Worker;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.statistics.WorkerDAO;
import org.ourgrid.peer.business.util.LoggerUtil;
import org.ourgrid.peer.to.LocalWorker;

public class WorkerControl {
	
	private static WorkerControl instance = null;
	
	public static WorkerControl getInstance() {
		if (instance == null) {
			instance = new WorkerControl();
		}
		return instance;
	}
	
	public void statusChanged(List<IResponseTO> responses,
			String workerUserAtServer, LocalWorkerState workerStatus) {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		try {
			getWorkerDAO().statusChanged(responses, 
					workerUserAtServer, workerStatus);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());

	}
	
	public void statusChanged(List<IResponseTO> responses,
			String workerUserAtServer, LocalWorkerState workerStatus, String allocatedFor) {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		try {
			getWorkerDAO().statusChanged(responses, 
					workerUserAtServer, workerStatus, allocatedFor);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());

	}
	
	public void addLocalWorker(List<IResponseTO> responses, LocalWorker localWorker, String myUserAtServer) {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		try {
			getWorkerDAO().addLocalWorker(responses, localWorker, myUserAtServer);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}

		responses.add(LoggerUtil.leave());
	}
	
	public void addRemoteWorker(List<IResponseTO> responses,
			WorkerSpecification workerSpec, String providerDN, String consumerUserAtServer) {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		try {
			getWorkerDAO().addRemoteWorker(responses, workerSpec, providerDN, consumerUserAtServer);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}

		responses.add(LoggerUtil.leave());
	}
	

	public void updateWorker(List<IResponseTO> responses, String workerUserAtServer,
			Map<String, String> currentAttributes, Map<String, String> currentAnnotations) {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		try {
			getWorkerDAO().updateWorker(responses, workerUserAtServer, currentAttributes, currentAnnotations);
			
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
	}
	
	public LocalWorker getLocalWorker(List<IResponseTO> responses, String workerUserAtServer) {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		LocalWorker worker = null;
		
		try {
			worker = PeerDAOFactory.getInstance().getLocalWorkersDAO()
				.getLocalWorker(responses, workerUserAtServer);			
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}		
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());

		return worker;
	}
	
	public Collection<LocalWorker> getLocalWorkers(List<IResponseTO> responses, 
			String peerUserAtServer,LocalWorkerState status) {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		Collection<LocalWorker> workers = createCollection();
		
		try {
			workers = PeerDAOFactory.getInstance().getLocalWorkersDAO().
			getLocalWorkers(responses, peerUserAtServer, status);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());

		return workers;
	}
	
	public boolean isNewWorker(List<IResponseTO> responses, String workerAtServer) {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		boolean isNew = false;
		try {
			isNew = PeerDAOFactory.getInstance().getLocalWorkersDAO().isNewWorker(
					responses, workerAtServer);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		
		return isNew;
	}
	
	public Collection<String> getLocalWorkersUserAtServer(List<IResponseTO> responses, String myUserAtServer) {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		Collection<String> localWorkersAddress = createCollection();
		
		try {
			localWorkersAddress = PeerDAOFactory.getInstance().getLocalWorkersDAO()
					.getLocalWorkersUserAtServer(responses, myUserAtServer);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		
		return localWorkersAddress;
	}
	
	public LocalWorker removeLocalWorker(List<IResponseTO> responses, String localWorkerUserAtServer) {
		
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		LocalWorker localWorker = null;
		
		try {
			localWorker = PeerDAOFactory.getInstance().getWorkerDAO().removeLocalWorker(responses, 
					localWorkerUserAtServer);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
		return localWorker;
	}
	
	public void removeRemoteWorker(List<IResponseTO> responses, String remoteWorkerUserAtServer) {
		
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		try {
			getWorkerDAO().removeRemoteWorker(responses, remoteWorkerUserAtServer);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
	}
	
	public Collection<Worker> findAllActiveWorkers(List<IResponseTO> responses, String myUserAtServer) {
		
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		Collection<Worker> workers = createCollection();
		try {
			workers = getWorkerDAO().findAllActiveWorkers(responses, myUserAtServer);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());

		return workers;
	}
	
	public Worker findActiveWorker(List<IResponseTO> responses, String workerUserAtServer) {
		
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		Worker worker = null;
		try {
			worker = getWorkerDAO().findActiveWorker(responses, workerUserAtServer);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());

		return worker;
	}
	
	public void updateWorkAccounting(List<IResponseTO> responses, String workerUserAtServer, double cpuTime, double dataStored) {
		
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		try {
			getWorkerDAO().updateWorkAccounting(responses, workerUserAtServer, cpuTime, dataStored);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
	}
	
	private WorkerDAO getWorkerDAO() {
		return PeerDAOFactory.getInstance().getWorkerDAO();
	}
	
	private  <T> Collection<T> createCollection(){
		return new LinkedList<T>();
	}

}
