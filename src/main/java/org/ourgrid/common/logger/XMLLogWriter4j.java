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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.ourgrid.common.util.CommonUtils;


/**
 *
 * This class  is a consumer of log events. It's responsable to write the information existent into the each log event, in xml format, in a specified
 * file, with the specified layout.
 */
public class XMLLogWriter4j extends AppenderSkeleton {

	private Layout layout;
	private File logFile;	
	private BufferedWriter bw;
	private ArrayList<LoggingEvent> logBuffer = new ArrayList<LoggingEvent>();
	public static final int TIMEOUT = 10000;
	private Dispatcher dispatcher;
	/**
     * HashMap containing writers that had been instantiated
     */
    private static Map<String, XMLLogWriter4j> writers = CommonUtils.createSerializableMap();
	
    
	/**
	 * MyAppender Constructor
	 * @param layout the layout
	 * @param fileName the file to logging
	 */
	private XMLLogWriter4j(Layout layout, String fileName, boolean newFile) {
		
		this.layout = layout;		
		try {
			
			logFile = new File(fileName);
	        	if (newFile) {
	                logFile.delete();
	                logFile.createNewFile();
	            }
	        	
		} catch (Exception e) {
			
			System.err.println("Cannot create XML log file: \"" + this.logFile.getAbsolutePath() + "\"");
			
		}
		dispatcher = new Dispatcher(this);
		dispatcher.start();
		
		
	}
	
	/**
	 * Return an instance of XMLLogWriter4j. We have one instance per file.
	 * @param layout the layout to format log messages.
	 * @param fileName the file to write log.
	 * @param newFile Determines if an existent file must be overwrited.
	 * @return an instance of XMLLogWriter4j.
	 */
	public static XMLLogWriter4j getInstance(Layout layout, String fileName, boolean newFile) {
				
		File myFile =  new File(fileName);
		
		XMLLogWriter4j writer = writers.get(myFile.getAbsolutePath());
        if (writer == null) {
        	writer = new XMLLogWriter4j(layout, fileName, newFile);
            writers.put(myFile.getAbsolutePath(), writer);
        }
        return writer;		
	}
	
	/**
	 * Get all LoggingEvent buffered and write's it all on disk, in a log file. Then the log buffer is reseted.
	 *
	 */
	protected void writeBufferedLogs() {
		
		synchronized (logBuffer) {
		
			try {
				
				bw = new BufferedWriter(new FileWriter(logFile, true));
								
				while(!logBuffer.isEmpty()) {
					bw.write(layout.format( logBuffer.get(0)  ));
					logBuffer.remove(0);
				}
				
			} catch (IOException ioe) {
	
	            System.out.println("Cannot write to XML log file: \"" + this.logFile.getAbsolutePath() + "\"");
	
			} finally {
				
	            try {
	                bw.close();
	            } catch (IOException e) {
	                System.out.println("Cannot close to XML log file: \"" + this.logFile.getAbsolutePath() + "\"");
	            }            
	            
			}
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	protected void append(LoggingEvent event) {
		
		//set the Local Information of an event
	    event.getLocationInformation();
	    
		synchronized (logBuffer) {
			logBuffer.add(event);
			logBuffer.notifyAll();
		}		
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#close()
	 */
	public void close() {
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	public boolean requiresLayout() {
		return true;
	}

	/**
	 *
	 * @author augusto
	 *
	 * This class is a thread that make the MGLogWriter4j waits a sleep time to write buffered logs.
	 */
	private class Dispatcher extends Thread{

		private XMLLogWriter4j appender;
		
		/**
		 * @param appender
		 * @param logBuffer
		 */
		public Dispatcher(XMLLogWriter4j appender) {
			this.appender = appender;
			
			this.setDaemon(true);
			this.setName("Log Writer");			
		}
		
		public void run() {
			
			while (true) {
											
				try {
					
					Thread.sleep(XMLLogWriter4j.TIMEOUT);
					
				} catch (InterruptedException e) {					
					//does nothing
				}
				
				appender.writeBufferedLogs();
				
			}
			
		}
		
	}
	

}
