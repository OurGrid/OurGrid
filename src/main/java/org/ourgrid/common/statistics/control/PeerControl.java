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

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.beans.status.PeerStatus;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.statistics.PeerDAO;
import org.ourgrid.peer.business.util.LoggerUtil;


public class PeerControl {
	
	private static PeerControl instance = null;

	public static PeerControl getInstance() {
		if (instance == null) {
			instance = new PeerControl();
		}
		return instance;
	}
	
	public Peer updatePeer(List<IResponseTO> responses, String myUserAtServer, String myCertSubjectDN, String description, String email,
			String label, String latitude, String longitude) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		// Test if there isn't other entry with the same user@server
		Peer otherPeer = getPeerDAO().getPeer(responses, myUserAtServer);
		if (otherPeer != null) {
			if (!otherPeer.getDNdata().equals(myCertSubjectDN)) {
				RuntimeException exception = new RuntimeException("Database is corrupted, the Peer should be reinstalled.");

				responses.add(new LoggerResponseTO("Error while trying update the Peer. Duplicated address.",
						LoggerResponseTO.ERROR, exception));
				
				throw exception;
			}
		}
			
		Peer peer = null;
		try {
			peer = getPeerDAO().updatePeer(responses, myUserAtServer, myCertSubjectDN, description, email, label, latitude, longitude);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
		
		return peer;
	}
	
	public Peer getPeerByCommuneAddress(List<IResponseTO> responses, String peerUserAtServer) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		Peer peer = null;
		try {
			PeerDAO peerDao = PeerDAOFactory.getInstance().getPeerDAO();
			peer = peerDao.findUpPeerByID(responses, peerUserAtServer);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		
		return peer;
	}
	
	public Peer insertPeer(List<IResponseTO> responses, String peerUserAtServer, String certSubjectDN) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
			
		Peer peer = null;
		try {
			peer = PeerDAOFactory.getInstance().getPeerDAO()
						.insertPeer(responses, peerUserAtServer, certSubjectDN);		
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
		
		return peer;
	}
	
	public void updatePeerUptime(List<IResponseTO> responses, String myUserAtServer) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
			
		try {
			getPeerDAO().updatePeerUptime(responses, myUserAtServer);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
		
	}

	public Long getFirstDBUpdateDate(List<IResponseTO> responses) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
			
		Long firstDBUpdateDate = null;
		try {
			firstDBUpdateDate = getPeerDAO().getFirstDBUpdateDate();
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		
		return firstDBUpdateDate;
	}
	
	public void registerPeerStatusChange(List<IResponseTO> responses, String myUserAtServer) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		try {
			PeerDAO dao = getPeerDAO();
			Peer peer = dao.getPeer(responses, myUserAtServer);
			
			if (peer != null) {
				
				if (peer.getLastUpTime() != null) {
					dao.insertStatusChange(responses, peer, PeerStatus.DOWN, peer.getLastUpTime());
				}
				
				dao.insertStatusChange(responses, peer, PeerStatus.UP, System.currentTimeMillis());
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

	public void peerIsDown(List<IResponseTO> responses, String peerSubjectDN) {
		responses.add(LoggerUtil.enter());

		HibernateUtil.beginTransaction();

		try {
			PeerDAO dao = PeerDAOFactory.getInstance().getPeerDAO();
			Peer peer = dao.getPeerBySubjectDN(responses, peerSubjectDN);

			if (peer != null) {
				peer.setCurrentStatus(PeerStatus.DOWN);
				dao.update(responses, peer);
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

	private PeerDAO getPeerDAO() {
		return PeerDAOFactory.getInstance().getPeerDAO();
	}
}
