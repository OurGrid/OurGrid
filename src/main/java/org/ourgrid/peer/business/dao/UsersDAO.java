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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.beans.peer.User;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.peer.business.dao.statistics.EntityDAO;
import org.ourgrid.peer.business.dao.statistics.PeerDAO;
import org.ourgrid.peer.business.util.LoggerUtil;
import org.ourgrid.peer.to.PeerUser;
import org.ourgrid.peer.to.PeerUserReference;
import org.ourgrid.reqtrace.Req;

/**
 * Stores users info
 */
public class UsersDAO extends EntityDAO {

	private final Map<String, PeerUserReference> loggedUsersByPubKey = CommonUtils.createSerializableMap();
	
	/**
	 * Retrieves a map containing the registered users of the Peer
	 * @return A map linking login and PeerUser
	 */
	@Req({"REQ022", "REQ038a", "REQ106"})
	public Map<String,PeerUser> getUsers(List<IResponseTO> responses) {
		responses.add(LoggerUtil.enter());
		
		List<User> users = null;
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(User.class);
		criteria.add(Restrictions.isNull("deletionDate"));
		users = criteria.list();
			
		responses.add(LoggerUtil.leave());
		
		return fillPeerUsersMap(users);
		
	}
	
	private Map<String, PeerUser> fillPeerUsersMap(List<User> users) {
		
		Map<String, PeerUser> result = new TreeMap<String, PeerUser>();
		if (users != null) {
			
			for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
				User user = (User) iterator.next();
				
				result.put(user.getAddress(), parseToPeerUser(user));
			}
		}
		
		return result;
	}
	
	private PeerUser parseToPeerUser(User user){
		if(user == null){
			return null;
		}
		
		String[] splitedAddress = user.getAddress().split("@");
		String userName = splitedAddress[0];
		String server = splitedAddress[1];
		String publicKey = user.getPublicKey();
		boolean logged = loggedUsersByPubKey.get(publicKey) != null;
		return new PeerUser(userName, server, publicKey, logged);
	}
	
	/**
	 * Retrieves information about a user
	 * @param login The login of the user
	 * @return Information about the user
	 */
	@Req("REQ108")
	public PeerUser getUser(List<IResponseTO> responses, String login) {
		return parseToPeerUser(findByUserAtServer(responses, login));
	}
	
	/**
	 * Retrieves information about a user
	 * @param userPublicKey The public key of the user
	 * @return Information about the user
	 */
	@Req("REQ027")
	public PeerUser getUserByPublicKey(List<IResponseTO> responses, String userPublicKey) {
		if (userPublicKey == null) throw new IllegalArgumentException("Informed user public key is null");
		return parseToPeerUser(findByUserPublicKey(responses, userPublicKey));
	}
	
	public User findByUserPublicKey(List<IResponseTO> responses, String userPublicKey) {
		responses.add(LoggerUtil.enter());
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(User.class);
		criteria.add(Restrictions.eq("publicKey", userPublicKey));
		criteria.add(Restrictions.isNull("deletionDate"));
		User user = (User) criteria.uniqueResult();
		
		responses.add(LoggerUtil.leave());
		return user;
	}
	
	public User findByUserAtServer(List<IResponseTO> responses, String userAtServer) {
		responses.add(LoggerUtil.enter());
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(User.class);
		criteria.add(Restrictions.eq("address", userAtServer));
		criteria.add(Restrictions.isNull("deletionDate"));
		User user = (User) criteria.uniqueResult();
		
		responses.add(LoggerUtil.leave());
		return user;
	}
	
	public User findByID(List<IResponseTO> responses, long id) {
		responses.add(LoggerUtil.enter());
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(User.class);
		criteria.add(Restrictions.eq("id", id));
		User user = (User) criteria.uniqueResult();
		
		responses.add(LoggerUtil.leave());
		return user;
	}

	public void insert(List<IResponseTO> responses, User user) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		session.save(user);
		session.flush();
			
		responses.add(LoggerUtil.leave());
	}

	public void update(List<IResponseTO> responses, User user) {
		responses.add(LoggerUtil.enter());
		Session session = HibernateUtil.getSession();
		
		session.update(user);
		session.flush();
			
		responses.add(LoggerUtil.leave());		
	}
	
	/**
	 * @param userPublicKey
	 * @return
	 */
	public boolean userExists(List<IResponseTO> responses, String userPublicKey){
		return getUserByPublicKey(responses, userPublicKey) != null;
	}

	/**
	 * Registers a user's PublicKey.
	 * This method is called once, when the user first logins in the peer
	 * @param user The user that logged for the first time
	 * @param publicKey The PublicKey to be registered
	 * @throws IOException Throwed when there is a problem when trying to persist users file
	 */
	@Req("REQ108")
	public void registerPublicKey(List<IResponseTO> responses, PeerUser user, String publicKey) throws IOException {
		User peerUser = findByUserAtServer(responses, user.getLogin());
		peerUser.setPublicKey(publicKey);
		update(responses, peerUser);
	}

	@Req("REQ108")
	public void addLoggedUser(String lwpcPubKey, PeerUserReference workerProviderClient) {
		loggedUsersByPubKey.put(lwpcPubKey, workerProviderClient);
	}
	
	/**
	 * @param pubKey
	 * @return
	 */
	@Req("REQ022")
	public PeerUserReference removeLoggedUser(String pubKey){
		return loggedUsersByPubKey.remove(pubKey);
	}
	
	/**
	 * @param pubKey
	 * @return
	 */
	@Req("REQ112")
	public PeerUserReference getLoggedUser(String pubKey){
		return loggedUsersByPubKey.get(pubKey);
	}
	
	@Req({"REQ106", "REQ038a"})
	public boolean isLoggedUser(String pubKey){
		return loggedUsersByPubKey.containsKey(pubKey);
	}
	
	public User insertUser(List<IResponseTO> responses, String login, Peer peer) {
		User user = new User();
		user.setAddress(login);
		user.setCreationDate(now());
		user.setDeletionDate(null);
		user.setLastModified(now());
		user.setPeer(peer);
		user.setPublicKey("");
		
		insert(responses, user);
		return user;
	}

	public User addUser(List<IResponseTO> responses, String login, String myUserAtServer, String myCertSubjectDN,
			String description, String email, String label, String latitude,
			String longitude) {
		
		PeerDAO peerDao = PeerDAOFactory.getInstance().getPeerDAO();
			
		
		Peer peer = peerDao.updatePeer(responses,myUserAtServer,  myCertSubjectDN,
				 description,  email,  label,  latitude,
				 longitude);
		User user = findByUserAtServer(responses, login);
		
		if (user == null) {
			user = insertUser(responses, login, peer);
		} else {
			//throw new CommuneRuntimeException("The user is already added: " + login);
			throw new RuntimeException("The user is already added: " + login);
		}
			
		return user;
	}
	
	public void removeUser(List<IResponseTO> responses,String login) {
		responses.add(LoggerUtil.enter());
		
		User user = findByUserAtServer(responses, login);
		
		if (user == null) {
			//throw new CommuneRuntimeException("The user is not added: " + login);
			throw new RuntimeException("The user is not added: " + login);
		} else {
			user.setAddress(login);
			user.setDeletionDate(now());
			user.setLastModified(now());
			
			update(responses, user);
		}

		responses.add(LoggerUtil.leave());
	}
}
