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
package org.ourgrid.peer.business.dao.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.statistics.beans.peer.Attribute;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.beans.peer.Worker;
import org.ourgrid.common.statistics.beans.peer.monitor.WorkerStatusChange;
import org.ourgrid.common.statistics.beans.status.WorkerStatus;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.peer.business.dao.LocalWorkersDAO;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.util.LoggerUtil;
import org.ourgrid.peer.to.LocalWorker;

/**
 * 
 */
public class WorkerDAO extends EntityDAO {

	public void insert(List<IResponseTO> responses, Worker worker) {
		responses.add(LoggerUtil.enter());
		worker.setAddress(worker.getAddress().toLowerCase());
		Session session = HibernateUtil.getSession();
		session.save(worker);
		session.flush();
		responses.add(LoggerUtil.leave());
	}

	public Worker findByID(List<IResponseTO> responses, long id) {
		responses.add(LoggerUtil.enter());
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(Worker.class);
		criteria.add(Restrictions.eq("id", id));
		Worker worker = (Worker) criteria.uniqueResult();
		
		responses.add(LoggerUtil.leave());
		return worker;
	}

	public Worker findByID(List<IResponseTO> responses, String address) {
		responses.add(LoggerUtil.enter());
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(Worker.class);
		criteria.add(Restrictions.eq("address", address));
		Worker worker = (Worker) criteria.uniqueResult();
		
		responses.add(LoggerUtil.leave());
		return worker;
	}
	
	public Collection<Worker> findAllActiveWorkers(List<IResponseTO> responses, String myUserAtServer) {
		responses.add(LoggerUtil.enter());
		
		Peer workerPeer = PeerDAOFactory.getInstance().getPeerDAO().findByID(responses, myUserAtServer);
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(Worker.class);
		criteria.add(Restrictions.isNull("endTime"));
		criteria.createCriteria("peer").add(Restrictions.eq("address", workerPeer.getAddress()));
		Collection<Worker> workers = criteria.list();
		
		for (Worker worker : workers) {
		
			String queryStr = "SELECT distinct a FROM Attribute a " +
					"WHERE a.worker.id = " + worker.getId() + " AND a.endTime = null";
			
			Query query = HibernateUtil.getSession().createQuery(queryStr);
			List<Attribute> attributes = query.list();
			worker.setAttributes(attributes);
		}	
		
		responses.add(LoggerUtil.leave());
		
		return workers;
	}
	
	public void updateWorkAccounting(List<IResponseTO> responses, String workerUserAtServer, double cpuTime, double dataStored) {

		Worker worker = findActiveWorker(responses, workerUserAtServer);
		
		double newCpuTime = worker.getCpuTime() + cpuTime;
		double newDataStored = worker.getDataStored() + dataStored;
		
		worker.setCpuTime(newCpuTime);
		worker.setDataStored(newDataStored);
		update(responses, worker);
	}
	
	public void update(List<IResponseTO> responses, Worker worker) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		session.update(worker);
		session.flush();
		responses.add(LoggerUtil.leave());
	}

	public void insert(List<IResponseTO> responses, Attribute attribute) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		session.save(attribute);
		session.flush();
		responses.add(LoggerUtil.leave());
	}

	public void update(List<IResponseTO> responses, Attribute attribute) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		session.update(attribute);
		session.flush();
		responses.add(LoggerUtil.leave());
	}

	@SuppressWarnings("unchecked")
	public WorkerStatusChange findCurrentStatus(List<IResponseTO> responses, Worker worker) {
		responses.add(LoggerUtil.enter());
		WorkerStatusChange wsc = null;
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(WorkerStatusChange.class);
		criteria.createCriteria("worker").add(Restrictions.eq("id", worker.getId()));
		criteria.addOrder(Order.desc("timeOfChange"));
		
		List<WorkerStatusChange> result = criteria.list();
		if(!result.isEmpty()){
			wsc = result.get(0);
		}
		
		responses.add(LoggerUtil.leave());
		return wsc;
	}

	public void insert(List<IResponseTO> responses, WorkerStatusChange statusChange) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		session.save(statusChange);
		session.flush();
		responses.add(LoggerUtil.leave());
	}

	@SuppressWarnings("unchecked")
	public Attribute findByWorkerAndProperty(List<IResponseTO> responses, Worker worker, String key) {
		responses.add(LoggerUtil.enter());
		Attribute att = null;
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(Attribute.class);
		criteria.createCriteria("worker").add(Restrictions.eq("id", worker.getId()));
		criteria.add(Restrictions.eq("property", key));
		
		List<Attribute> result = criteria.list();
		if(!result.isEmpty()){
			att = result.get(0);
		}
		
		responses.add(LoggerUtil.leave());
		return att;
	}

	public Worker findActiveWorker(List<IResponseTO> responses, String workerUserAtServer) {
		responses.add(LoggerUtil.enter());
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(Worker.class);
		criteria.add(Restrictions.eq("address", workerUserAtServer.toLowerCase()));
		criteria.add(Restrictions.isNull("endTime"));
		Worker worker = (Worker) criteria.uniqueResult();
		
		responses.add(LoggerUtil.leave());
		
		return worker;
		
	}
	
	public void statusChanged(List<IResponseTO> responses, String workerUserAtServer, LocalWorkerState workerStatus, String allocatedFor) {
		
		Worker worker = findActiveWorker(responses, workerUserAtServer);
		
		if (worker == null) {
			throw new RuntimeException("There is no worker " + workerUserAtServer);
		} 

		WorkerStatusChange statusChange = new WorkerStatusChange();
		statusChange.setLastModified(now());
		WorkerStatus status = null;
		
		if (LocalWorkerState.DONATED.equals(workerStatus)) {
			status = WorkerStatus.DONATED;
		} else if (LocalWorkerState.IDLE.equals(workerStatus)) {
			status = WorkerStatus.IDLE;
		} else if (LocalWorkerState.IN_USE.equals(workerStatus)) {
			status = WorkerStatus.IN_USE;
		} else if (LocalWorkerState.OWNER.equals(workerStatus)) {
			status = WorkerStatus.OWNER;
		} else if (LocalWorkerState.ERROR.equals(workerStatus)) {
			status = WorkerStatus.ERROR;
		}
		
		worker.setStatus(status);
		worker.setAllocatedFor(allocatedFor);
		worker.setLastModified(now());
		
		statusChange.setStatus(status);
		statusChange.setTimeOfChange(now());
		statusChange.setWorker(worker);
		
		update(responses, worker);
		insert(responses, statusChange);

	}
	
	public void statusChanged(List<IResponseTO> responses, String workerUserAtServer, LocalWorkerState workerStatus) {
		statusChanged(responses, workerUserAtServer, workerStatus, null);
	}
	
	public void addRemoteWorker(List<IResponseTO> responses, WorkerSpecification workerSpec, String providerDN, String consumerUserAtServer) {

		String address = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_USERNAME) + "@" + workerSpec.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
		Worker worker = findByID(responses, address);
		
		PeerDAO peerDAO = PeerDAOFactory.getInstance().getPeerDAO();
		Peer peer = peerDAO.getPeerBySubjectDN(responses, providerDN);
		
		boolean isWorkerNew = worker == null;
		
		if (isWorkerNew) {
			worker = new Worker();
			worker.setAddress(address);
		}
		
		worker.setLastModified(now());
		worker.setBeginTime(now());
		worker.setEndTime(null);
		worker.setPeer(peer);
		worker.setStatus(WorkerStatus.DONATED);
		worker.setAllocatedFor(consumerUserAtServer);
		
		if (isWorkerNew) {
			insert(responses, worker);
		} else {
			update(responses, worker);
		}
		
		addWorkerAttributes(responses, worker, workerSpec.getAttributes(), false);
		addWorkerAttributes(responses, worker, workerSpec.getAnnotations(), true);
	}

	public void addLocalWorker(List<IResponseTO> responses, LocalWorker localWorker, String myUserAtServer) {
		
		//PeerDAO peerDAO = getDAO(PeerDAO.class);
		
		String workerUserAtServer = localWorker.getWorkerUserAtServer();
		Worker worker = findActiveWorker(responses, workerUserAtServer);
		
		Peer peer = PeerDAOFactory.getInstance().getPeerDAO().findByID(responses, myUserAtServer);
		
		if (worker == null) {
			
			worker = new Worker();
			
			worker.setAddress(workerUserAtServer);
			worker.setLastModified(now());
			worker.setBeginTime(now());
			worker.setEndTime(null);
			
			worker.setPeer(peer);
			//peer.getWorkers().add(worker);
			
			insert(responses, worker);
		}
		
		addWorkerAttributes(responses, worker, localWorker.getAttributes(), false);
		addWorkerAttributes(responses, worker, localWorker.getAnnotations(), true);

		statusChanged(responses, workerUserAtServer, LocalWorkerState.OWNER);
	}
	
	private void addWorkerAttributes(List<IResponseTO> responses, Worker worker, Map<String, String> attributes, boolean isAnnotation) {
				
		for (String key : attributes.keySet()) {
			String value = attributes.get(key);
			
			Attribute attribute = findByWorkerAndProperty(responses, worker, key);
			
			if (attribute == null) { //new attribute?
				attribute = createAttribute(worker, key, value, isAnnotation);
				insert(responses, attribute);
			} else {
				if( !attribute.getIsAnnotation() && isAnnotation)
					continue; //An annotation cannot overwrite an attribute
				String oldValue = attribute.getValue();
				if (!oldValue.equals(value)) {
					attribute.setEndTime(now());
					attribute.setLastModified(now());
					update(responses, attribute);
					
					attribute = createAttribute(worker, key, value, isAnnotation);
					insert(responses, attribute);
				}
			}
		}
	}
	
	public void updateWorker(List<IResponseTO> responses, String workerUserAtServer, Map<String, String> currentAttributes, Map<String, String> currentAnnotations) {
		
		
		Worker worker = findActiveWorker(responses, workerUserAtServer);
		
		if (worker == null) {
			throw new RuntimeException("There is no worker " + workerUserAtServer);
		} 

		
		worker.setLastModified(now());

		updateAttributes(responses, currentAttributes, worker, false);
		updateAttributes(responses, currentAnnotations, worker, true);		
	}

	
	private void updateAttributes(List<IResponseTO> responses, Map<String, String> currentAttributes, Worker worker, boolean annotation) {
				
		List<String> newAttributes = new ArrayList<String>(currentAttributes.keySet());
		
		for (Attribute oldAttribute : worker.getAttributes()) {
			
			if(annotation && !oldAttribute.getIsAnnotation())
				continue; //an annotation cannot overwrite an attribute
			
			if (currentAttributes.containsKey(oldAttribute.getProperty())) {				
				oldAttribute.setValue(currentAttributes.get(oldAttribute.getProperty()));
				oldAttribute.setIsAnnotation( annotation );
				oldAttribute.setEndTime(null);
				newAttributes.remove(oldAttribute.getProperty());
				
			} else {
				oldAttribute.setEndTime(now());
			}
			
			oldAttribute.setLastModified(now());
			update(responses, oldAttribute);
		} //for
		
		for (String key : newAttributes) {			
			String value = currentAttributes.get(key);
			Attribute attribute = createAttribute(worker, key, value, annotation);			
			insert(responses, attribute);
		}
	}
	
	//TODO Must deal with annotations?
	public LocalWorker removeLocalWorker(List<IResponseTO> responses,String workerUserAtServer) {
		
		LocalWorkersDAO localWorkersDAO = PeerDAOFactory.getInstance().getLocalWorkersDAO();
		
		LocalWorker localWorker = localWorkersDAO.getLocalWorker(responses, workerUserAtServer);
		
		String address = workerUserAtServer;
		
		Worker worker = findActiveWorker(responses, address);
		
		if (worker == null) {
			throw new RuntimeException("There is no worker " + address);
		} 
		
		worker.setEndTime(now());
		worker.setLastModified(now());
		worker.setStatus(WorkerStatus.DISCONNECTED);
		worker.setAllocatedFor(null);
		
		WorkerStatusChange statusChange = new WorkerStatusChange();
		
		statusChange.setLastModified(now());
		statusChange.setStatus(WorkerStatus.DISCONNECTED);
		statusChange.setTimeOfChange(now());
		statusChange.setWorker(worker);

		insert(responses, statusChange);
		
		update(responses, worker);
		
		for (Attribute attribute : worker.getAttributes()) {
			attribute.setEndTime(now());
			attribute.setLastModified(now());
			update(responses, attribute);
		}
		
		return localWorker;
	}
	
	public void removeRemoteWorker(List<IResponseTO> responses, String remoteWorkerUserAtServer) {
		Worker remoteWorker = findActiveWorker(responses, remoteWorkerUserAtServer);
	
		if (remoteWorker != null) {
			
			remoteWorker.setEndTime(now());
			remoteWorker.setLastModified(now());
			remoteWorker.setStatus(WorkerStatus.DISCONNECTED);
			remoteWorker.setAllocatedFor(null);
			
			update(responses, remoteWorker);
			
		}
		
	}
}
