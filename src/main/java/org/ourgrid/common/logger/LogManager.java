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

import static java.io.File.separator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * Class that reads log properties files and provides methods to change and read 
 * that properties.<p>
 * 
 * The changes are only written to the file when "save" is called
 *
 */
public class LogManager {

	private static final String VALUE_ATT = "value";
	private static final String APPENDER_NODE = "appender";
	private static final String PRIORITY_NODE = "priority";
	private static final String ROOT_NODE = "root";
	private static final String LOG_FILE_PARAM = "param";
	//private static final String OURGRID_LOG_LEVEL_PROPERTY_KEY = "log4j.logger.org.ourgrid";
	private static final String OURGRID_LOG_LEVEL_PROPERTY_SUFFIX = ", ourgrid";
	//private static final String OURGRID_LOG_FILE_PROPERTY_KEY = "log4j.appender.ourgrid.file";
	
	private Level logLevel;
	private File logPropertiesFile;
	private File logFile;
	private Document logDocFile;
	private Element priority;
	private Element fileParam;
	
	/**
	 * Creates a new LogManager with the properties value contained at a file
	 * @param logPropertiesFile the file to load the properties from
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public LogManager() throws FileNotFoundException, IOException {
		this.logPropertiesFile = getLogConfiguratonFile();
		loadLogPropertiesFile();
	}
	
	private File getLogConfiguratonFile() {
		String property = System.getProperty( "OGROOT" );
		property = property == null ? "." : property;
		
		property += separator +"log4j.cfg.xml";
		
		return new File(property);
	}


	/**
	 * Loads the properties from the file and overrides current values
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadLogPropertiesFile() throws IOException {
		//properties = new Properties();
		//properties.load(new FileReader(logPropertiesFile));
		
		loadData();
	}
	
	private void loadData() throws FileNotFoundException, IOException {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			docBuilder.setEntityResolver(new NoOpEntityResolver());
			logDocFile = docBuilder.parse(this.logPropertiesFile);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		
		Element log4j = logDocFile.getDocumentElement();
		Element root = (Element) log4j.getElementsByTagName(ROOT_NODE).item(0);
		
		this.priority = (Element) root.getElementsByTagName(PRIORITY_NODE).item(0);
		
		String levelStr = priority.getAttribute(VALUE_ATT);
		
		String[] tokens = levelStr.split(",");
		if (tokens.length == 0) {
			setLevel(Level.DEBUG);
		} else {
			setLevel(Level.toLevel(tokens[0]));
		}
		
		Element appender = (Element) log4j.getElementsByTagName(APPENDER_NODE).item(1);
		this.fileParam = (Element) appender.getElementsByTagName(LOG_FILE_PARAM).item(0);
		
		String fileStr = fileParam.getAttribute(VALUE_ATT);
		
		this.logFile = new File(fileStr);
	
		
		/*String prop = properties.getProperty(OURGRID_LOG_LEVEL_PROPERTY_KEY);
		String[] tokens = prop.split(",");
		if (tokens.length == 0) {
			setLevel(Level.DEBUG);
		} else {
			setLevel(Level.toLevel(tokens[0]));
		}
		
		logFile = new File(properties.getProperty(OURGRID_LOG_FILE_PROPERTY_KEY));*/
	}
	
	/**
	 * Saves the log properties to the log properties file
	 * @throws IOException
	 */
	public void save() throws IOException {
		//properties.store(new FileWriter(logPropertiesFile), null);
		XMLSerializer serializer = new XMLSerializer(new FileOutputStream(getLogConfiguratonFile()), new	
        OutputFormat(this.logDocFile, "iso-8859-1", true));
		serializer.serialize(this.logDocFile);
	}
	
	/**
	 * Sets log level
	 * @param lvl the new log level
	 */
	public void setLevel(Level lvl) {
		logLevel = lvl;
		priority.setAttribute(VALUE_ATT, logLevel.toString() + OURGRID_LOG_LEVEL_PROPERTY_SUFFIX);
		//properties.put(OURGRID_LOG_LEVEL_PROPERTY_KEY, logLevel.toString() + OURGRID_LOG_LEVEL_PROPERTY_SUFFIX);
	}
	
	/**
	 * @return log level of this manager
	 */
	public Level getLogLevel() {		
		return logLevel;
	}
	
	/**
	 * @return the log file of this manager
	 */
	public File getLogFile() {
		return logFile;
	}
	
	/**
	 * Changes the log file of this manager
	 * @param f the new log file
	 */
	public void setLogFile(File f) {
		logFile = f;
		fileParam.setAttribute(VALUE_ATT, f.toString());
		//properties.put(OURGRID_LOG_FILE_PROPERTY_KEY, f.toString());
	}
	
	
}
