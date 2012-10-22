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

import java.util.List;
import java.util.TimeZone;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.beans.peer.monitor.LocalPeerStatusChange;
import org.ourgrid.common.statistics.beans.status.PeerStatus;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.peer.business.util.LoggerUtil;

/**
 * 
 */
public class PeerDAO extends EntityDAO {
	
	public Peer findByID(List<IResponseTO> responses, String address) {
		responses.add(LoggerUtil.enter());

		Criteria criteria = HibernateUtil.getSession().createCriteria(
				Peer.class);
		criteria.add(Restrictions.eq("address", address));
		Peer peer = (Peer) criteria.uniqueResult();

		responses.add(LoggerUtil.leave());
		return peer;
	}

	public Peer findUpPeerByID(List<IResponseTO> responses, String address) {
		responses.add(LoggerUtil.enter());

		Criteria criteria = HibernateUtil.getSession().createCriteria(
				Peer.class);
		criteria.add(Restrictions.eq("address", address));
		criteria.add(Restrictions.eq("currentStatus", PeerStatus.UP));
		Peer peer = (Peer) criteria.uniqueResult();

		responses.add(LoggerUtil.leave());
		return peer;
	}

	public Peer getPeerBySubjectDN(List<IResponseTO> responses, String peerDNData) {
		Criteria criteria = HibernateUtil.getSession().createCriteria(
				Peer.class);
		criteria.add(Restrictions.eq("DNdata", peerDNData));
		return (Peer) criteria.uniqueResult();
	}

	public void insert(List<IResponseTO> responses, Peer peer) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		session.save(peer);
		session.flush();
		responses.add(LoggerUtil.leave());
	}

	public void insertStatusChange(List<IResponseTO> responses, LocalPeerStatusChange statusChange) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		session.save(statusChange);
		session.flush();
		responses.add(LoggerUtil.leave());
	}

	public void update(List<IResponseTO> responses, Peer peer) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		session.update(peer);
		session.flush();
		responses.add(LoggerUtil.leave());
	}

	public void updatePeerUptime(List<IResponseTO> responses, String myUserAtServer) {

		Peer peer = findByID(responses, myUserAtServer);

		//FIXME check
		// Peer is not up, that's ok for now.
		if (peer == null) {
			return;
		}

		peer.setLastUpTime(now());

		update(responses, peer);
	}

	public Long getFirstDBUpdateDate() {

		Criteria criteria = HibernateUtil.getSession().createCriteria(
				Peer.class);
		criteria.setProjection(Projections.min("lastModified"));
		Long minPeer = (Long) criteria.uniqueResult();

		Criteria criteriaSC = HibernateUtil.getSession().createCriteria(
				LocalPeerStatusChange.class);
		criteriaSC.setProjection(Projections.min("lastModified"));
		Long minStatusChange = (Long) criteriaSC.uniqueResult();

		return Math.min(minPeer, minStatusChange);
	}

	public Long getPeerLastUptime(List<IResponseTO> responses, String myUserAtServer) {

		Peer peer = findByID(responses, myUserAtServer);

		// Peer is not up, that's ok for now.
		if (peer == null) {
			return null;
		}

		return peer.getLastUpTime();

	}

	public Peer updatePeer(List<IResponseTO> responses, String myUserAtServer, String myCertSubjectDN,
			String description, String email, String label, String latitude,
			String longitude) {

		/*
		 * PeerDAO peerDao = serviceManager.getDAO(PeerDAO.class); Peer peer =
		 * peerDao
		 * .getPeerBySubjectDN(CertificationUtils.getCertSubjectDN(serviceManager
		 * .getMyCertPath()));
		 */
		Peer peer = getPeerBySubjectDN(responses, myCertSubjectDN);

		if (peer == null) {
			peer = new Peer();
			//peer.setAddress(myUserAtServer);
			//peer.setDNdata(myCertSubjectDN);
			updatePeerObject(peer, myUserAtServer, myCertSubjectDN,
					description, email, label, latitude, longitude);

			insert(responses, peer);
		} else {

			String version = Configuration.VERSION.toString();

			// Test if at least one attribute was changed and reset the
			// lastModified field.
			if (peer.getDescription() == null
					|| !peer.getDescription().equals(description)
					|| peer.getEmail() == null
					|| !peer.getEmail().equals(email)
					|| peer.getLabel() == null
					|| !peer.getLabel().equals(label)
					|| peer.getLatitude() == null
					|| !peer.getLatitude().equals(latitude)
					|| peer.getLongitude() == null
					|| !peer.getLongitude().equals(longitude)
					|| peer.getVersion() == null
					|| !peer.getVersion().equals(version)
					|| peer.getDNdata() == null
					|| !peer.getDNdata().equals(myCertSubjectDN)
					|| peer.getAddress() == null
					|| !peer.getAddress().equals(myUserAtServer)) {

				updatePeerObject(peer, myUserAtServer, myCertSubjectDN,
						description, email, label, latitude, longitude);
			}

			update(responses, peer);
		}

		return peer;
	}

	private void updatePeerObject(Peer peer, String myUserAtServer,
			String myCertSubjectDN, String description, String email,
			String label, String latitude, String longitude) {
		peer.setDescription(description);
		peer.setEmail(email);
		peer.setLabel(label);
		peer.setLatitude(latitude);
		peer.setLongitude(longitude);
		peer.setDNdata(myCertSubjectDN);
		peer.setVersion(Configuration.VERSION.toString());
		peer.setTimezone(TimeZone.getDefault().getID());
		peer.setAddress(myUserAtServer);

		peer.setLastModified(now());
	}

	public Peer getPeer(List<IResponseTO> responses, String peerUserAtServer) {
		return findByID(responses, peerUserAtServer);
	}
	
	public void insertStatusChange(List<IResponseTO> responses, Peer peer, PeerStatus status,
			Long timeOfChange) {

		LocalPeerStatusChange toUpChange = new LocalPeerStatusChange();
		toUpChange.setCurrentStatus(status);
		toUpChange.setLastModified(now());
		toUpChange.setPeer(peer);
		toUpChange.setVersion(Configuration.VERSION.toString());
		toUpChange.setTimeOfChange(timeOfChange);

		insertStatusChange(responses, toUpChange);

	}

	public void remove(List<IResponseTO> responses, Peer peer) {

		responses.add(LoggerUtil.enter());

		Session session = HibernateUtil.getSession();
		session.delete(peer);
		session.flush();

		responses.add(LoggerUtil.leave());

	}
	
	public Peer insertPeer(List<IResponseTO> responses, String peerUserAtServer, String certSubjectDN){
		Peer peer = getPeerBySubjectDN(responses, certSubjectDN);
		
		boolean isNewPeer = (peer == null);
		
		if (isNewPeer) {
			peer = new Peer();
			peer.setDNdata(certSubjectDN);
		}
		
		peer.setAddress(peerUserAtServer);
		peer.setLastModified(System.currentTimeMillis());
		peer.setCurrentStatus(PeerStatus.UP);
		
		if (isNewPeer) {
			insert(responses, peer);
		} else {
			update(responses, peer);
		}
		
		return peer;
	}

}
