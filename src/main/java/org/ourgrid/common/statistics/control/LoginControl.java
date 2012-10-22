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

import org.ourgrid.common.BrokerLoginResult;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.statistics.LoginDAO;
import org.ourgrid.peer.business.util.LoggerUtil;
import org.ourgrid.peer.to.PeerUser;

public class LoginControl {
	
	private static LoginControl instance = null;
	
	public static LoginControl getInstance() {
		if (instance == null) {
			instance = new LoginControl();
		}
		return instance;
	}
	
	public void login(List<IResponseTO> responses, PeerUser peerUser,  BrokerLoginResult loginResult,
			String myUserAtServer, String myCertSubjectDN, String description, String email,
				String label, String latitude, String longitude) {
		
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		try {
			getLoginDAO().login(responses, peerUser, loginResult, myUserAtServer,
					myCertSubjectDN, description, email, label, latitude, longitude);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
	}
	
	public void localConsumerFailure(List<IResponseTO> responses, PeerUser user) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();

		try {
			getLoginDAO().localConsumerFailure(responses, user);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
	}

	public void clearLogins(List<IResponseTO> responses, String myUserAtServer) {
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();

		try {
			Long peerLastUptime = PeerDAOFactory.getInstance().getPeerDAO()
					.getPeerLastUptime(responses, myUserAtServer);
			if (peerLastUptime == null) {
				peerLastUptime = System.currentTimeMillis();
			}
			getLoginDAO().clearLogins(responses, peerLastUptime);
			
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
		
	}
	
	private LoginDAO getLoginDAO() {
		return PeerDAOFactory.getInstance().getLoginDAO();
	}

}
