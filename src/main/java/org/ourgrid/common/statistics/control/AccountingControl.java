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
import java.util.Map;

import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.beans.peer.GridProcess;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.peer.business.dao.AccountingDAO;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.statistics.PeerDAO;
import org.ourgrid.peer.business.util.LoggerUtil;
import org.ourgrid.peer.to.PeerBalance;

public class AccountingControl extends EntityControl {
	
	private static AccountingControl instance = null;
	
	public static AccountingControl getInstance() {
		if (instance == null) {
			instance = new AccountingControl();
		}
		return instance;
	}
	
	protected AccountingControl() {}
	
	public void setRemotePeerBalance(List<IResponseTO> responses, String localPeerDN, String remotePeerDN, 
			PeerBalance balance) {
		
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		try {
			Peer localPeer = getPeerDAO().getPeerBySubjectDN(responses, localPeerDN);
			Peer remotePeer = getPeerDAO().getPeerBySubjectDN(responses, remotePeerDN);
			
			PeerDAOFactory.getInstance().getAccountingDAO().setRemotePeerBalance(
					responses, localPeer, remotePeer, balance, remotePeerDN);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			responses.add(LoggerUtil.rollbackException(e));
			HibernateUtil.rollbackTransaction();
		} finally {
			HibernateUtil.closeSession();
		}

		responses.add(LoggerUtil.leave());
	}
	
	public PeerBalance getRemotePeerBalance(List<IResponseTO> responses, String localPeerDN, String remotePeerDN) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		PeerBalance balance = new PeerBalance();
		try {
			Peer localPeer = getPeerDAO().getPeerBySubjectDN(responses, localPeerDN);
			Peer remotePeer = getPeerDAO().getPeerBySubjectDN(responses, remotePeerDN);
			
			balance = PeerDAOFactory.getInstance().getAccountingDAO().getRemotePeerBalance(
					localPeer, remotePeer);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		
		return balance;
	}
	
	public Map<String, PeerBalance> getBalances(List<IResponseTO> responses, String peerDNData) {
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();

		Map<String, PeerBalance> balances = CommonUtils.createSerializableMap();
		try {
			Peer localPeer = getPeerDAO().getPeerBySubjectDN(responses, peerDNData);
			balances = getAccountingDAO().getBalances(localPeer);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		
		return balances;
	}

	public void addReplicaAccounting(List<IResponseTO> responses, GridProcessAccounting replicaAccounting,
			String providerCertificateDN) {

		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		try {
			PeerDAOFactory.getInstance().getAccountingDAO().addReplicaAccounting(
					replicaAccounting, providerCertificateDN);
			
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
	
	private AccountingDAO getAccountingDAO() {
		return PeerDAOFactory.getInstance().getAccountingDAO();
	}

	public Map<String, List<GridProcess>> getProcessesOfRequest(
			List<IResponseTO> responses, long requestId) {
		
		responses.add(LoggerUtil.enter());
		
		HibernateUtil.beginTransaction();
		
		Map<String, List<GridProcess>> processes = CommonUtils.createSerializableMap();
		
		try {
			processes = getAccountingDAO().getProcessesOfRequest(requestId);
		} catch (Exception e) {
			responses.add(LoggerUtil.exception(e));
		}
		
		HibernateUtil.closeSession();
		responses.add(LoggerUtil.leave());
		
		return processes;
	}

}
