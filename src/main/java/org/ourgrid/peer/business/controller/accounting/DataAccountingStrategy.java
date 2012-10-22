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
import org.ourgrid.common.util.CommonUtils;

/**
 *
 */
public class DataAccountingStrategy implements AccountingStrategy {

	/* (non-Javadoc)
	 * @see org.ourgrid.refactoring.peer.controller.accounting.AccountingStrategy#evaluate(java.util.Map)
	 */
	public Map<String, Double> evaluate(Map<String, List<GridProcess>> accountings) {
		
		Map<String, Double> balances = CommonUtils.createSerializableMap();
		
		for (String providerDN : accountings.keySet()) {
			Double balance = 0.;
			
			for (GridProcess process : accountings.get(providerDN)) {
				balance += process.getDataConsumed();
			}
			
			balances.put(providerDN, balance);
		}
		
		return balances;
	}

}
