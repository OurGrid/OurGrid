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
package org.ourgrid.worker.business.requester;

import java.util.GregorianCalendar;

/**
 */
public class ScheduleTime  {
	
	public GregorianCalendar beginTime;
	
	public GregorianCalendar endTime;

	public ScheduleTime(GregorianCalendar beginTime, GregorianCalendar endTime) {
		
		this.beginTime = beginTime;
		this.endTime = endTime;
	}

	/**
	 * @return the beginTime
	 */
	public GregorianCalendar getBeginTime() {
		return beginTime;
	}

	/**
	 * @param beginTime the beginTime to set
	 */
	public void setBeginTime(GregorianCalendar beginTime) {
		this.beginTime = beginTime;
	}

	/**
	 * @return the endTime
	 */
	public GregorianCalendar getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(GregorianCalendar endTime) {
		this.endTime = endTime;
	}
	
	public boolean isActiveTime(GregorianCalendar time) {
		
		boolean afterInit = getHour(beginTime) < getHour(time) || ( getHour(time) == getHour(beginTime) &&
				getMinute(beginTime) <= getMinute(time));
		
		boolean beforeFinal = getHour(endTime) > getHour(time) || ( getHour(time) == getHour(endTime) &&
				getMinute(endTime) >= getMinute(time));
		
		return afterInit && beforeFinal;
	}
	
	private int getHour(GregorianCalendar time) {
		return time.get(GregorianCalendar.HOUR_OF_DAY);
	}
	
	private int getMinute(GregorianCalendar time) {
		return time.get(GregorianCalendar.MINUTE);
	}

	public boolean isValidInterval() {
		int beginHour = getHour(beginTime);
		int beginMin = getMinute(beginTime);

		int endHour = getHour(endTime);
		int endMin = getMinute(endTime);
		
		return beginHour < endHour || (beginHour == endHour && beginMin < endMin);
	}
	
}
