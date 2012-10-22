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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.statistics.beans.peer.Balance;
import org.ourgrid.common.statistics.beans.peer.BalanceValue;
import org.ourgrid.common.statistics.beans.peer.GridProcess;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.peer.business.controller.messages.AccountingMessages;
import org.ourgrid.peer.to.PeerBalance;
import org.ourgrid.reqtrace.Req;

/**
 * Manages accounting data
 */
@Req("REQ027")
public class AccountingDAO {
	
	public void addReplicaAccounting(GridProcessAccounting replicaAccounting, String dNdata) {
		updateGridProcessAccounting(dNdata, replicaAccounting);
	}
	
	private void updateGridProcessAccounting(String dNdata,
			GridProcessAccounting replicaAccounting) {
		
		long jobId = replicaAccounting.getJobId();
		int taskId = replicaAccounting.getTaskSequenceNumber();
		int processId = replicaAccounting.getGridProcessSequenceNumber();
		long requestId = replicaAccounting.getRequestId();

		Criteria criteria = HibernateUtil.getSession().createCriteria(
				GridProcess.class);

		criteria.add(Restrictions.eq("sequenceNumber", processId));
		Criteria taskCriteria = criteria.createCriteria("task");
		taskCriteria.add(Restrictions.eq("sequenceNumber", taskId));
		
		Criteria jobCriteria = taskCriteria.createCriteria("job");
		jobCriteria.add(Restrictions.eq("jobId", jobId));
		jobCriteria.add(Restrictions.eq("requestId", requestId));

		GridProcess gridProcess = (GridProcess) criteria.uniqueResult();
		gridProcess.setProviderDN(dNdata);
		gridProcess.setCpuConsumed(replicaAccounting.getAccountings().getCPUTime());
		gridProcess.setDataConsumed(replicaAccounting.getAccountings().getData());
		
		Session session = HibernateUtil.getSession();
		session.saveOrUpdate(gridProcess);
		session.flush();
	}
		
	public Map<String, List<GridProcess>> getProcessesOfRequest(long requestId) {
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(
				GridProcess.class);

		criteria.createCriteria("task").createCriteria("job").add(
				Restrictions.eq("requestId", requestId));
		
		@SuppressWarnings("unchecked")
		List<GridProcess> processes = criteria.list();
		Map<String, List<GridProcess>> processPerPeerDn = CommonUtils.createSerializableMap();
		
		for (GridProcess gridProcess : processes) {
			String providerDN = gridProcess.getProviderDN();
			List<GridProcess> gridProcesses = processPerPeerDn.get(providerDN);
			if (gridProcesses == null) {
				gridProcesses = new ArrayList<GridProcess>();
				processPerPeerDn.put(providerDN, gridProcesses);
			}
			gridProcesses.add(gridProcess);
		}
		
		return processPerPeerDn;
	}

	/**
	 * Returns te current balance of a remote peer
	 * @param remotePeerPublicKey The remote peer's public key
	 * @return The remote peer balance
	 */
	public PeerBalance getRemotePeerBalance(Peer localPeer, Peer remotePeer) {
		
		if (localPeer == null || remotePeer == null) {
			return null;
		}
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(Balance.class);
		criteria.createCriteria("self").add(Restrictions.eq("DNdata", localPeer.getDNdata()));
		criteria.createCriteria("other").add(Restrictions.eq("DNdata", remotePeer.getDNdata()));
		
//		criteria.add(Restrictions.eq("self.DNdata", localPeer.getDNdata()));
//		criteria.add(Restrictions.eq("other.DNdata", remotePeer.getDNdata()));
		criteria.addOrder(Order.desc("balanceTime"));
		
		List<Balance> balances = criteria.list();
		PeerBalance balanceReturn = null;
		
		if (balances != null && !balances.isEmpty()) {
			Balance balance = balances.get(0);
			List<BalanceValue> values = balance.getValues();
			
			if (values != null) {
				balanceReturn = fillBalanceValue(values);
			}	
		}
		
		return balanceReturn;
	}
	
	@Req("{REQ035, REQ115}")
	public Map<String, PeerBalance> getBalances(Peer localPeer) {
		
		Criteria criteria = HibernateUtil.getSession().createCriteria(Balance.class);
		
		criteria.createCriteria("self").add(Restrictions.eq("DNdata", localPeer.getDNdata()));
		
		//criteria.add(Restrictions.eq("self.dNdata", localPeer.getdndata()));
		criteria.addOrder(Order.desc("balanceTime"));
		
		List<Balance> balances = criteria.list();
		
		//FIXME revise and improve filter
		List<Balance> latestBalances = findLatestBalances(balances);
		
		Map<String, PeerBalance> peerBalances = CommonUtils.createSerializableMap();
		
		if (!latestBalances.isEmpty()) {
			for (Iterator<Balance> iterator = latestBalances.iterator(); iterator.hasNext();) {
				Balance balance = (Balance) iterator.next();
				List<BalanceValue> values = balance.getValues();
				
				if (values != null && !values.isEmpty()) {
					PeerBalance pBalance = fillBalanceValue(values);
					Peer remotePeer = balance.getOther();
					
//					String[] splitAddress = remotePeer.getAddress().split("@");
//					String userName = splitAddress[0];
//					String serverName = splitAddress[1];
					
//					ServiceID serviceID = new ServiceID(userName, serverName, PeerConstants.MODULE_NAME,
//							PeerConstants.REMOTE_ACCESS_OBJECT_NAME);
					
					peerBalances.put(remotePeer.getDNdata(), pBalance);
				}
			}	
		}
		
		return peerBalances;
	}
	
	private PeerBalance fillBalanceValue(List<BalanceValue> values) {
		
		Double cpuTime = null;
		Double data = null;
		
		for (Iterator<BalanceValue> iterator = values.iterator(); iterator.hasNext();) {
			BalanceValue balanceValue = (BalanceValue) iterator.next();
			
			if (balanceValue.getProperty().equals(PeerBalance.CPU_TIME)) {
				cpuTime = Double.valueOf(balanceValue.getValue());
			} else if (balanceValue.getProperty().equals(PeerBalance.DATA)) {
				data = Double.valueOf(balanceValue.getValue());
			}
		}
		
		return new PeerBalance(cpuTime, data);
	}
	
	private List<Balance> findLatestBalances(List<Balance> balances) {
		
		List<String> remotePeers = new ArrayList<String>();
		List<Balance> balanceReturn = new ArrayList<Balance>();
		
		for (Iterator<Balance> iterator = balances.iterator(); iterator.hasNext();) {
			Balance balance = (Balance) iterator.next();
			
			if (!remotePeers.contains(balance.getOther().getAddress())) {
				balanceReturn.add(balance);
				remotePeers.add(balance.getOther().getAddress());
			}
		}
		
		return balanceReturn;
	}

	public void setRemotePeerBalance(List<IResponseTO> responses, Peer localPeer, Peer remotePeer, PeerBalance newBalance, 
			String remotePeerDN) {
		
		Session session = HibernateUtil.getSession();
		
		if (remotePeer == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					AccountingMessages.getNotReceivedRemoteWorkerProviderMessage(
					remotePeerDN), LoggerResponseTO.WARN);
			
			responses.add(loggerResponse);
			
			return;
		}
		
		if (newBalance.isClear()) {
			Balance currentBalance = findBalanceByDate(new Date(), session, localPeer, remotePeer);
			
			if (currentBalance != null) {
				removeBalanceValues(session, currentBalance);
			}	
			
		} else {
			
			Balance currentBalance = findBalanceByDate(new Date(), session, localPeer, remotePeer);
			if (currentBalance == null) {
				
				Balance balance = new Balance();
				balance.setBalanceTime(new Date());
				balance.setLastModified(new Date().getTime());
				balance.setOther(remotePeer);
				balance.setSelf(localPeer);
				
				fillBalanceValues(balance, newBalance.getCPUTime().toString(), newBalance.getData().toString());
				session.save(balance);
				
			} else {
				
				if (currentBalance != null) {
					removeBalanceValues(session, currentBalance);
				}	
				
				fillBalanceValues(currentBalance, newBalance.getCPUTime().toString(), newBalance.getData().toString());
				session.update(currentBalance);
			}
		}
	}

	private void removeBalanceValues(Session session, Balance currentBalance) {
		List<BalanceValue> values = currentBalance.getValues();
		if (values != null) {
			for (BalanceValue balanceValue : values) {
				session.delete(balanceValue);
			}
		}
		
		currentBalance.setValues(null);
		session.update(currentBalance);
	}
	
	
	
	protected void fillBalanceValues(Balance balance, String cpuTime, String dataValue) {
		BalanceValue cpu = new BalanceValue();
		cpu.setBalance(balance);
		cpu.setProperty(PeerBalance.CPU_TIME);
		cpu.setValue(cpuTime);
		cpu.setLastModified(new Date().getTime());
		
		BalanceValue data = new BalanceValue();
		data.setBalance(balance);
		data.setProperty(PeerBalance.DATA);
		data.setValue(dataValue);
		data.setLastModified(new Date().getTime());
		
		balance.setValues(new ArrayList<BalanceValue>());
		balance.getValues().add(cpu);
		balance.getValues().add(data);
	}
	
	private Balance findBalanceByDate(Date date, Session session, Peer localPeer, Peer remotePeer) {
		
		Calendar todayInitDalendar = Calendar.getInstance();
		todayInitDalendar.set(Calendar.MILLISECOND, 0);
		todayInitDalendar.set(Calendar.HOUR_OF_DAY, 0);
		todayInitDalendar.set(Calendar.MINUTE, 0);
		todayInitDalendar.set(Calendar.SECOND, 0);
		Date todayInit = todayInitDalendar.getTime();
		
		Calendar todayEndCalendar = Calendar.getInstance();
		todayInitDalendar.set(Calendar.MILLISECOND, 0);
		todayInitDalendar.set(Calendar.HOUR_OF_DAY, 23);
		todayInitDalendar.set(Calendar.MINUTE, 59);
		todayInitDalendar.set(Calendar.SECOND, 59);
		Date todayEnd = todayEndCalendar.getTime();
		
		Criteria criteria = session.createCriteria(Balance.class);
		criteria.add(Restrictions.between("balanceTime", todayInit, todayEnd));
//		criteria.add(Restrictions.eq("self.DNdata", localPeer.getDNdata()));
//		criteria.add(Restrictions.eq("other.DNdata", remotePeer.getDNdata()));
		criteria.createCriteria("self").add(Restrictions.eq("DNdata", localPeer.getDNdata()));
		criteria.createCriteria("other").add(Restrictions.eq("DNdata", remotePeer.getDNdata()));
		
		
		return (Balance) criteria.uniqueResult();
	}

	/**
	 * Save the current peer balances in a file
	 * @param rankingFilePath The path of file where the balances will be saved.
	 */
	public void saveBalancesRanking(String rankingFilePath) {
		/*try {
			serializeObject(rankingFilePath, getBalances(), "the accounting ranking");
		} catch (IOException e) {
			getLog().debug("The ranking file '" + rankingFilePath + "' could not be saved.", e);
		}*/
	}
	
	/**
	 * Load the saved peer balances from a file
	 * @param filePath The path of file where the balances are saved.
	 */
	@SuppressWarnings("unchecked")
	public void loadBalancesRanking(String filePath) {
		/*try {
			balances = (Map<String, PeerBalance>) loadObject(filePath, "the accounting ranking");
		} catch (StreamCorruptedException e) {
			getLog().fatal("The Network of Favors ranking file [" + filePath + "] is corrupted. " +
					"The peer cannot be started.", e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			getLog().debug("The ranking file [" + filePath + "] could not be loaded. "
					+ "Using empty NoF data.", e);
			balances = new HashMap<String, PeerBalance>();
		} catch (ClassNotFoundException e) {
			getLog().fatal("The ranking file [" + filePath + "] could not be loaded. "
					+ "Unknown error.", e);
			throw new RuntimeException(e);
		}*/
	}
}
