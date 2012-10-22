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
package org.ourgrid.discoveryservice;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;
import org.ourgrid.common.statistics.beans.status.PeerStatus;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.peer.business.util.LoggerUtil;

/**
 * @author alan
 *
 */
public class PeerStatusChangeUtil {

	public static void peerIsUp(String peerUserAtServer, 
			List<IResponseTO> responses) {
		registerPeerStatusChange(peerUserAtServer, PeerStatus.UP, 
				System.currentTimeMillis(), responses);
	}
	
	public static void peerIsDown(String peerUserAtServer, 
			List<IResponseTO> responses) {
		registerPeerStatusChange(peerUserAtServer, PeerStatus.DOWN, 
				System.currentTimeMillis(), responses);
	}
	
	@SuppressWarnings("unchecked")
	public static List<DS_PeerStatusChange> getPeerStatusChangesHistory(long since, 
			List<IResponseTO> responses) {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		List<DS_PeerStatusChange> allPeerStatusChange = null;
		try {
			allPeerStatusChange = new ArrayList<DS_PeerStatusChange>();
			
			Criteria criteria = HibernateUtil.getSession().createCriteria(DS_PeerStatusChange.class)
				.add(Restrictions.ge("timeOfChange", since))
				.addOrder(Order.asc("timeOfChange"));
			
			allPeerStatusChange.addAll(criteria.list());
			
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		
		return allPeerStatusChange;
	}
	
	public static void registerPeerStatusChange(String peerUserAtServer, 
			PeerStatus status, long timeOfChange, List<IResponseTO> responses) {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		try {
			DS_PeerStatusChange change = new DS_PeerStatusChange();

			change.setCurrentStatus(status);
			change.setLastModified(timeOfChange);
			change.setPeerAddress(peerUserAtServer);
			change.setTimeOfChange(timeOfChange);

			HibernateUtil.getSession().saveOrUpdate(change);

			HibernateUtil.commitTransaction();
		} catch (HibernateException e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
	}

	/**
	 * Requirement 502
	 * @param responses 
	 */
	@SuppressWarnings("unchecked")
	public static void killAllActivePeers(long timeOfChange, List<IResponseTO> responses) {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		try {
			Query peerQuery = HibernateUtil.getSession().createSQLQuery(
					"SELECT DISTINCT psc.peerAddress FROM peer_status_change psc");
			List<String> allPeers = peerQuery.list();
			
			for (String peerAddress : allPeers) {
				Query singlePeerQuery = HibernateUtil.getSession().createSQLQuery(
						"SELECT currentStatus FROM peer_status_change " +
						"WHERE peerAddress = '" + peerAddress + "' " +
						"ORDER BY timeOfChange DESC");
				singlePeerQuery.setMaxResults(1);
				
				String lastStatus = (String) singlePeerQuery.uniqueResult();
				
				if (PeerStatus.UP.toString().equals(lastStatus)) {
					DS_PeerStatusChange change = new DS_PeerStatusChange();
					change.setCurrentStatus(PeerStatus.DOWN);
					change.setLastModified(timeOfChange);
					change.setPeerAddress(peerAddress);
					change.setTimeOfChange(timeOfChange);
					
					HibernateUtil.getSession().save(change);
				}
			}
			
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
	}
	
}
