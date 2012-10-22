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
package org.ourgrid.broker.communication.dao;

import java.util.concurrent.Future;

public class SchedulerFutureDAO {

	private static SchedulerFutureDAO instance;
	
	private Future<?> schedulerFuture;
	
	
	private SchedulerFutureDAO() {}
	
	
	public static SchedulerFutureDAO getInstance() {
		if (instance == null) {
			instance = new SchedulerFutureDAO();
		}
		
		return instance;
	}
	
	public void setSchedulerFuture(Future<?> schedulerFuture) {
		this.schedulerFuture = schedulerFuture;		
	}

	public void cancelSchedulerFuture() {
		if (schedulerFuture != null) {
			schedulerFuture.cancel(true);
		}	
	}
	
	public boolean isSchedulerActionActive() {
		return this.schedulerFuture != null && 
			!this.schedulerFuture.isCancelled() && 
			!this.schedulerFuture.isDone();
	}
}
