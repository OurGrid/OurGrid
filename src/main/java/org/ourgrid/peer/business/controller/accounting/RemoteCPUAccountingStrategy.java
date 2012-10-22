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
package org.ourgrid.peer.business.controller.accounting;

import java.util.List;
import java.util.Map;

import org.ourgrid.common.statistics.beans.peer.GridProcess;
import org.ourgrid.common.statistics.beans.status.ExecutionStatus;
import org.ourgrid.common.util.CommonUtils;

public class RemoteCPUAccountingStrategy implements AccountingStrategy {

	/* (non-Javadoc)
	 * @see org.ourgrid.refactoring.peer.controller.accounting.AccountingStrategy#evaluate(java.util.Map)
	 */
	public Map<String, Double> evaluate(Map<String, List<GridProcess>> processes) {
		
		Map<String, Double> cpuBalances = CommonUtils.createSerializableMap();
		
		double totalCpuTime = 0.;
		int receivedFavoursCount = 0;
		
		for (String providerDN : processes.keySet()) {
			double totalCpuPerPeer = 0.;
			
			double finishedReplicaCpuPerPeer = 0.;
			int finishedReplicasCount = 0;
			
			for (GridProcess process : processes.get(providerDN)) {
				Double cpuTime = process.getCpuConsumed();
				totalCpuPerPeer += cpuTime;
				
				if (process.getStatus().equals(ExecutionStatus.FINISHED)) {
					finishedReplicaCpuPerPeer += cpuTime;
					finishedReplicasCount++;
				}
				
			}
			
			totalCpuTime += finishedReplicaCpuPerPeer;
			receivedFavoursCount += finishedReplicasCount;
			
			double averageCpuPerPeer = 0;
			
			if (totalCpuPerPeer != 0) {
				double finishedReplicaCpuPerPeerAvg = finishedReplicasCount == 0 ? 0 : 
					finishedReplicaCpuPerPeer / finishedReplicasCount;
				
				averageCpuPerPeer = finishedReplicaCpuPerPeerAvg == 0 ? 0 : 
					totalCpuPerPeer / finishedReplicaCpuPerPeerAvg;
			}
			
			cpuBalances.put(providerDN, averageCpuPerPeer);
		}
		
		double totalAverage = totalCpuTime == 0 ? 0 : totalCpuTime / receivedFavoursCount;

		for (String providerDN : cpuBalances.keySet()) {
			cpuBalances.put(providerDN, cpuBalances.get(providerDN) * totalAverage);
		}
		
		return cpuBalances;
	}

}
