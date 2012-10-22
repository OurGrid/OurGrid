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
package org.ourgrid.peer.to;

import java.io.Serializable;
import java.util.Map;

import org.ourgrid.common.util.CommonUtils;

/**
 * The balance of a peer. This entity holds information
 * about CPU, data, brams donated by a remote peer for 
 * local consuming.
 */
public class PeerBalance implements Serializable {

	private Map<String, Double> balancesMap = CommonUtils.createSerializableMap();

	public static final String CPU_TIME = "CPU time";
	public static final String DATA 	= "Data";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PeerBalance() {
		this(0., 0.);
	}

	public PeerBalance(Double cpuTime, Double data) {
		this.balancesMap = CommonUtils.createSerializableMap();
		setCPUTime(cpuTime);
		setData(data);
	}
	
	public void setAttribute(String att, Double value) {
		balancesMap.put(att, value);
	}
	
	public Double getAttribute(String att) {
		return balancesMap.get(att);
	}
	
	public Map<String, Double> getBalances() {
		return balancesMap;
	}
	
	public Double getCPUTime() {
		return balancesMap.get(CPU_TIME);
	}
	
	public void setCPUTime(Double value) {
		balancesMap.put(CPU_TIME, value);
	}
	
	public Double getData() {
		return balancesMap.get(DATA);
	}
	
	public void setData(Double value) {
		balancesMap.put(DATA, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PeerBalance)) {
			return false;
		}
		return this.balancesMap.equals( ((PeerBalance)obj).balancesMap );
	}
	
	/**
	 * @return True if there is no data available (cpu == 0 && data == 0), 
	 * false in any other case
	 */
	public boolean isClear() {
		return getCPUTime() == 0. && getData() == 0.;
	}
	
	@Override
	public String toString() {
		return String.format("%1$-30s%2$-30s", "CPU Time: " + String.format("%(,.2f", getCPUTime()),
				"Data: " + String.format("%(,.0f", getData()));
	}
}
