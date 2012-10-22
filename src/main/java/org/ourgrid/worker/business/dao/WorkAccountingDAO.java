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
package org.ourgrid.worker.business.dao;

import java.util.LinkedList;
import java.util.List;

import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.reqtrace.Req;

@Req("REQ122")
public class WorkAccountingDAO {
	
	private List<WorkAccounting> workAccountings;
	private WorkAccounting currentWorkAccounting;

	
	@Req("REQ123")	
	WorkAccountingDAO() {
		currentWorkAccounting = null;
		workAccountings = new LinkedList<WorkAccounting>();
	}
	
	
	public List<WorkAccounting> getWorkAccountings() {
		return workAccountings;
	}
	
	public void addWorkAccounting(WorkAccounting accounting) {
		workAccountings.add(accounting);
	}
	
	public void setCurrentWorkAccounting(WorkAccounting accounting) {
		currentWorkAccounting = accounting;
	}
	
	public WorkAccounting getCurrentWorkAccounting() {
		return currentWorkAccounting;
	}
	
	public void resetAccountings() {
		workAccountings = new LinkedList<WorkAccounting>();
	}

}
