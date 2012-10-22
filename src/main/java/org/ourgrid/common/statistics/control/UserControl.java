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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.beans.peer.User;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.UsersDAO;
import org.ourgrid.peer.business.util.LoggerUtil;
import org.ourgrid.peer.to.PeerUser;

public class UserControl extends EntityControl {
	
	private static UserControl instance = null;
	
	public static UserControl getInstance() {
		if (instance == null) {
			instance = new UserControl();
		}
		return instance;
	}
	
	protected UserControl() {}
	
	public User addUser(List<IResponseTO> responses,String login, String myUserAtServer, 
			String myCertSubjectDN, String description, String email, 
			String label, String latitude, String longitude) throws Exception {
		
		responses.add(LoggerUtil.enter());
		HibernateUtil.beginTransaction();
		
		User user = null;
		try {
			UsersDAO dao = PeerDAOFactory.getInstance().getUsersDAO();

			user = dao.addUser(responses,login.toLowerCase(), myUserAtServer,  myCertSubjectDN,
					 description,  email,  label,  latitude,
					 longitude);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
			throw e;
		} finally {
			HibernateUtil.closeSession();
			responses.add(LoggerUtil.leave());
		}
		
		return user;
	}
	
	public User insertUser(List<IResponseTO> responses, String login, Peer peer) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		User user = null;
		try {
			user = getUsersDAO().insertUser(responses, login, peer);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
		
		return user;
	}

	public void removeUser(List<IResponseTO> responses,String login) throws Exception {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		try {
			UsersDAO dao = PeerDAOFactory.getInstance().getUsersDAO();
			dao.removeUser(responses, login.toLowerCase());
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
			throw e;
		} finally {
			HibernateUtil.closeSession();
			responses.add(LoggerUtil.leave());
		}
		
	}
	
	public void registerPublicKey(List<IResponseTO> responses, PeerUser user, String publicKey) throws IOException {
		
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		try {
			getUsersDAO().registerPublicKey(responses, user, publicKey);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}
		
		responses.add(LoggerUtil.leave());
	}
	
	public boolean userExists(List<IResponseTO> responses, String userPublicKey){
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		boolean exists = false;
		try {
			exists = PeerDAOFactory.getInstance().getUsersDAO().
				userExists(responses, userPublicKey);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		return exists;
	}
	
	public PeerUser getUser(List<IResponseTO> responses, String login) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		PeerUser user = null;
		try {
			user = getUsersDAO().getUser(responses, login);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		return user;
	}
	
	public PeerUser getUserByPublicKey(List<IResponseTO> responses, String userPublicKey) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
				
		PeerUser user = null;
		try {
			user = getUsersDAO().getUserByPublicKey(responses, userPublicKey);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		return user;
	}
	
	public Map<String,PeerUser> getUsers(List<IResponseTO> responses){
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		Map<String,PeerUser> users = new TreeMap<String, PeerUser>();
		try {
			users = getUsersDAO().getUsers(responses);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		return users;
	}
	
	private UsersDAO getUsersDAO() {
		return PeerDAOFactory.getInstance().getUsersDAO();
	}
}
