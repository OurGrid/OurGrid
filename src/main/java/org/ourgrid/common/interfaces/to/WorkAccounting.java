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
package org.ourgrid.common.interfaces.to;

import org.ourgrid.peer.to.PeerBalance;
import org.ourgrid.reqtrace.Req;

@Req("REQ027")
public class WorkAccounting extends Accounting {
	
	private static final long serialVersionUID = 1L;
	private final String consumerDN;
	private long initCPUTime;

	public WorkAccounting(String consumerDN) {
		super();
		this.consumerDN = consumerDN;
		setAccounting(PeerBalance.CPU_TIME, 0D);
		setAccounting(PeerBalance.DATA, 0D);
	}
	
	public WorkAccounting(String consumerDN, PeerBalance balance) {
		super(balance);
		this.consumerDN = consumerDN;
	}

	public WorkAccounting(String consumerDN, double cpuTime, double data) {
		this(consumerDN);
		setAccounting(PeerBalance.CPU_TIME, cpuTime);
		setAccounting(PeerBalance.DATA, data);
	}
	
	public void incDataTransfered(long dataTransfered) {
		Double oldData = getAccountings().getAttribute(PeerBalance.DATA);
		oldData = oldData == null ? 0 : oldData;
		
		setAccounting(PeerBalance.DATA, dataTransfered + oldData);
	}
	
	public void startCPUTiming() {
		initCPUTime = System.currentTimeMillis();
	}
	
	public void stopCPUTiming() {
		if (initCPUTime == 0) {
			return;
		}
		setAccounting(PeerBalance.CPU_TIME, System.currentTimeMillis() - (double) initCPUTime);
	}

	public void restartWorkAccounting() {
		startCPUTiming();
		setAccountings(new PeerBalance());
	}
	
	public String getConsumerPeerDN() {
		return consumerDN;
	}

	public boolean isTimmingStarted() {
		return initCPUTime > 0;
	}
}