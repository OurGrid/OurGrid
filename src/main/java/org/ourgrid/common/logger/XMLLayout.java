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
package org.ourgrid.common.logger;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Class that determines the format of log messages
 */
public class XMLLayout extends Layout {

	public String format( LoggingEvent le ) {

		StringBuffer formattedXML = new StringBuffer();

		/** ********** setting date to string */
		Calendar date = new GregorianCalendar();
		date.setTimeInMillis( le.timeStamp );

		StringBuffer dateStr = new StringBuffer( 26 ); // size of string, to be
														// faster
		dateStr.append( date.get( Calendar.YEAR ) + "/" );
		dateStr.append( (date.get( Calendar.MONTH ) + 1) + "/" ); // 0 is
																	// January
		dateStr.append( date.get( Calendar.DAY_OF_MONTH ) + " " );
		dateStr.append( date.get( Calendar.HOUR_OF_DAY ) + ":" );
		dateStr.append( date.get( Calendar.MINUTE ) + ":" );
		dateStr.append( date.get( Calendar.SECOND ) + ":" );
		dateStr.append( date.get( Calendar.MILLISECOND ) );

		/** *********** setting layout of LoggingEvent */
		LocationInfo locInfo = le.getLocationInformation();

		if ( le != null ) {
			formattedXML.append( "<ENTRY" );
			formattedXML.append( " Type=\"" + le.getLevel() + "\"" );
			formattedXML.append( " Time=\"" + dateStr + "\"" );
			formattedXML.append( " Reference=\"" + locInfo.getClassName() + "." + locInfo.getMethodName() + ":"
					+ locInfo.getLineNumber() + "\"" );
			formattedXML.append( "><![CDATA[" );
			formattedXML.append( le.getMessage() );
			formattedXML.append( "]]></ENTRY>\n" );
		}

		return formattedXML.toString();
	}


	public boolean ignoresThrowable() {

		return true;
	}


	public void activateOptions() {

		// do nothing
	}

}
