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

import java.io.Serializable;
import java.util.Map;

import org.ourgrid.broker.communication.operations.GetOperation;
import org.ourgrid.broker.communication.operations.InitOperation;

public class GridProcessPhasesData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long initBeginning;
	private Long initEnd;
	private Map<InitOperation, TransferTime> initOperations;
	private Long remoteBeginning;
	private Long remoteEnd;
	private Map<GetOperation, TransferTime> getOperations;
	private Long finalBeginning;
	private Long finalEnd;
	
	
	public GridProcessPhasesData() {}
	
	public GridProcessPhasesData(Long initBeginning, Long initEnd, Long remoteBeginning, Long remoteEnd,
		Long finalBeginning, Long finalEnd) {
		
		this.initBeginning = initBeginning;
		this.initEnd = initEnd;
		this.remoteBeginning = remoteBeginning;
		this.remoteEnd = remoteEnd;
		this.finalBeginning = finalBeginning;
		this.finalEnd = finalEnd;
	}
	
	
	public Long getInitBeginning() {
		return initBeginning;
	}
	
	public void setInitBeginning(Long initBeginning) {
		this.initBeginning = initBeginning;
	}
	
	public Long getInitEnd() {
		return initEnd;
	}
	
	public void setInitEnd(Long initEnd) {
		this.initEnd = initEnd;
	}
	public Long getRemoteBeginning() {
		return remoteBeginning;
	}
	
	public void setRemoteBeginning(Long remoteBeginning) {
		this.remoteBeginning = remoteBeginning;
	}
	
	public Long getRemoteEnd() {
		return remoteEnd;
	}
	
	public void setRemoteEnd(Long remoteEnd) {
		this.remoteEnd = remoteEnd;
	}
	
	public Long getFinalBeginning() {
		return finalBeginning;
	}
	
	public void setFinalBeginning(Long finalBeginning) {
		this.finalBeginning = finalBeginning;
	}
	
	public Long getFinalEnd() {
		return finalEnd;
	}
	
	public void setFinalEnd(Long finalEnd) {
		this.finalEnd = finalEnd;
	}
	
	public Map<InitOperation, TransferTime> getInitOperations() {
		return initOperations;
	}

	public void setInitOperations(Map<InitOperation, TransferTime> initOperations) {
		this.initOperations = initOperations;
	}

	public Map<GetOperation, TransferTime> getGetOperations() {
		return getOperations;
	}

	public void setGetOperations(Map<GetOperation, TransferTime> getOperations) {
		this.getOperations = getOperations;
	}

}
