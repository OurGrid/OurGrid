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
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 */
public class ScheduleTimeParser  {
	
	public String times;
	
	private static String TIME_DELIMITER = ",";
	
	private static String TIME_INTERVAL_DELIMITER = "-";
	
	private static String HOUR_MIN_DELIMITER = ":";
	
	private static String SCHEDULE_TIME_PATTERN = "^[0-9]{1,2}:[0-9]{2}-[0-9]{1,2}:[0-9]{2}(,[0-9]{1,2}:[0-9]{2}-[0-9]{1,2}:[0-9]{2})*$";

	private static int MAX_HOUR = 23;
	
	private static int MIN_HOUR = 0;
	
	private static int MAX_MINUTE = 60;
	
	private static int MIN_MINUTE = 0;
	
	public ScheduleTimeParser(String times) {
		this.times = times;
	}
	
	public List<ScheduleTime> parseScheduleTimes() {
		
		Pattern scheduleTimesPattern = Pattern.compile(SCHEDULE_TIME_PATTERN);
		Matcher scheduleTimesMatcher = scheduleTimesPattern.matcher(times);
		
		if (times.length() > 0 && !scheduleTimesMatcher.matches()) {
			throw new RuntimeException(
					"Idleness Detector Schedule Time property could not be loaded, because has syntactical errors.");
		}
		
		LinkedList<ScheduleTime> scheduleTimes = new LinkedList<ScheduleTime>();
		StringTokenizer timeTokenizer = new StringTokenizer(times, TIME_DELIMITER);
		
		while (timeTokenizer.hasMoreTokens()) {
			
			String timeToken = timeTokenizer.nextToken();
			StringTokenizer intervalTokenizer = new StringTokenizer(timeToken, TIME_INTERVAL_DELIMITER);
			
			String beginTime = intervalTokenizer.nextToken();
			GregorianCalendar beginDate = createDate(beginTime);
			
			String endTime = intervalTokenizer.nextToken();
			GregorianCalendar endDate = createDate(endTime);
			
			ScheduleTime scheduleTime = new ScheduleTime(beginDate, endDate);
			
			if (!scheduleTime.isValidInterval()) {
				throw new RuntimeException(
						"Idleness Detector Schedule Time property could not be loaded, because has invalid times.");
			}
			
			scheduleTimes.add(scheduleTime);
		}
		
		return scheduleTimes;
	}
	
	private static GregorianCalendar createDate(String time) {
		StringTokenizer hourMinTokenizer = new StringTokenizer(time, HOUR_MIN_DELIMITER);
		
		int hour = Integer.parseInt(hourMinTokenizer.nextToken());
		int min = Integer.parseInt(hourMinTokenizer.nextToken());
		
		boolean validTime =
			hour >= MIN_HOUR && hour <= MAX_HOUR && min >= MIN_MINUTE && min <= MAX_MINUTE;
			
		if (!validTime) {
			throw new RuntimeException(
			"Idleness Detector Schedule Time property could not be loaded, because has invalid times.");
		}
		
		return new GregorianCalendar(0, 0, 0, hour, min); 
	}
	

}
