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

import java.util.List;

import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.beans.peer.Job;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.util.LoggerUtil;
import org.ourgrid.peer.to.Request;

public class JobControl extends EntityControl {
	
	private static JobControl instance = null;
	
	public static JobControl getInstance() {
		if (instance == null) {
			instance = new JobControl();
		}
		return instance;
	}
	
	protected JobControl() {}
	
	public void addRequest(List<IResponseTO> responses, Request request) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();

		try {
			PeerDAOFactory.getInstance().getJobDAO().addRequest(responses, request);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
	}
	
	public void hereIsJobStats(List<IResponseTO> responses, JobStatusInfo jobStatusInfo, long requestId) {

		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		try {
			PeerDAOFactory.getInstance().getJobDAO().hereIsJobStats(responses, requestId, jobStatusInfo);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
	}	
	
	public void addProcessAccounting(List<IResponseTO> responses, GridProcessAccounting replicaAccounting) {

		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		try {
			PeerDAOFactory.getInstance().getJobDAO().addProcessAccounting(responses, replicaAccounting);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
	}	
	
	public void finishRequest(List<IResponseTO> responses, Request request, boolean clientFailure) {
		
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		try {
			PeerDAOFactory.getInstance().getJobDAO().finishRequest(responses, request, clientFailure);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
	}

	public Job findByRequestId(List<IResponseTO> responses, long requestId) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		Job job = null;
		try {
			job = PeerDAOFactory.getInstance().getJobDAO().findByRequestId(responses, requestId);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		
		return job;
	}
	
	

}
