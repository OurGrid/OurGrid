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

import org.ourgrid.worker.business.requester.ScheduleTime;

/**
 * @author alan
 *
 */
public class IdlenessDetectorDAO {
	

	private long time;
	private long idlenessTime;
	private List<ScheduleTime> scheduleTimes;
	private boolean isActive;

	
	IdlenessDetectorDAO() {
		resetTime();
		isActive = false;
		idlenessTime = 0L;
		scheduleTimes = new LinkedList<ScheduleTime>();
	}
	
	
	public void incrementTime(long increment) {
		this.time = this.time + increment;
	}
	
	public void resetTime() {
		this.time = 0L;
	}
	
	public long getTime() {
		return this.time;
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the idlenessTime
	 */
	public long getIdlenessTime() {
		return idlenessTime;
	}

	/**
	 * @param idlenessTime the idlenessTime to set
	 */
	public void setIdlenessTime(long idlenessTime) {
		this.idlenessTime = idlenessTime;
	}

	/**
	 * @return the scheduleTimes
	 */
	public List<ScheduleTime> getScheduleTimes() {
		return scheduleTimes;
	}

	/**
	 * @param scheduleTimes the scheduleTimes to set
	 */
	public void setScheduleTimes(List<ScheduleTime> scheduleTimes) {
		this.scheduleTimes = scheduleTimes;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

}
