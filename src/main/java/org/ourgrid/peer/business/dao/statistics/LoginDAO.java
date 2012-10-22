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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.ourgrid.common.BrokerLoginResult;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.beans.peer.Login;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.beans.peer.User;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.UsersDAO;
import org.ourgrid.peer.business.util.LoggerUtil;
import org.ourgrid.peer.to.PeerUser;

/**
 *
 */
public class LoginDAO extends EntityDAO {

	public void insert(List<IResponseTO> responses, Login login) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		session.save(login);
		session.flush();
		responses.add(LoggerUtil.leave());
	}

	public Login findCurrentLogin(List<IResponseTO> responses, User user) {
		responses.add(LoggerUtil.enter());
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(Login.class);
		criteria.add(Restrictions.eq("user", user));
		criteria.add(Restrictions.isNull("endTime"));
		Login login = (Login) criteria.uniqueResult();
		
		responses.add(LoggerUtil.leave());
		return login;
	}

	public void update(List<IResponseTO> responses, Login login) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		session.update(login);
		session.flush();
		responses.add(LoggerUtil.leave());
	}
	
	public void login(List<IResponseTO> responses, PeerUser peerUser, BrokerLoginResult loginResult, String myUserAtServer, String myCertSubjectDN,
			String description, String email, String label, String latitude, String longitude) {
		
		UsersDAO userDAO = PeerDAOFactory.getInstance().getUsersDAO();
		PeerDAO peerDAO = PeerDAOFactory.getInstance().getPeerDAO();
		
		String login = peerUser.getLogin();
		
		User user = userDAO.findByUserAtServer(responses, login);

		if (user == null) {
			Peer peer = peerDAO.updatePeer(responses, myUserAtServer, myCertSubjectDN, description, email, label, latitude, longitude);
			user = userDAO.insertUser(responses, login, peer);
			if (loginResult.hasAnErrorOcurred()) {
				user.setDeletionDate(user.getCreationDate());
			}
			userDAO.update(responses, user);
		}
		
		Login currentLogin = findCurrentLogin(responses, user);
		
		if (currentLogin != null && currentLogin.getEndTime() == null) {
			currentLogin.setEndTime(now());
			update(responses, currentLogin);
		}
		
		Login newLogin = new Login();
		newLogin.setUser(user);
		//user.getLogins().add(loginObj);
		newLogin.setLastModified(now());
		newLogin.setBeginTime(now());
		
		if (loginResult.hasAnErrorOcurred()) {
			newLogin.setLoginResult(loginResult.getErrorMessage());
			newLogin.setEndTime(now());
		} else {

			if (user.getPublicKey() == null) {
				user.setPublicKey(peerUser.getPublicKey());
			}

			newLogin.setLoginResult(BrokerLoginResult.OK);
		}

		insert(responses, newLogin);

	}
	
	public void localConsumerFailure(List<IResponseTO> responses, PeerUser user) {
		
		UsersDAO userDAO = PeerDAOFactory.getInstance().getUsersDAO();
		String loginStr = user.getLogin();
		
		User userObj = userDAO.findByUserAtServer(responses, loginStr);
		if (userObj == null) {
			//throw new CommuneRuntimeException("The user is not added: " + loginStr);
			throw new RuntimeException("The user is not added: " + loginStr);
		}

		Login login = findCurrentLogin(responses, userObj);
		
		if (login == null) {
			//throw new CommuneRuntimeException("The user is not logged: " + loginStr);
			throw new RuntimeException("The user is not logged: " + loginStr);
		}
		
		login.setEndTime(now());
		login.setLastModified(now());
		update(responses, login);
		
	}

	@SuppressWarnings("unchecked")
	public void clearLogins(List<IResponseTO> responses, Long lastUptime) {
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(Login.class);
		criteria.add(Restrictions.isNull("endTime"));
		List<Login> logins = criteria.list();
		
		for (Login login : logins) {
			login.setEndTime(lastUptime);
			login.setLastModified(lastUptime);
			update(responses, login);
		}
	}

}
