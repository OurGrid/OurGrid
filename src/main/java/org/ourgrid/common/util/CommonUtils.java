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
package org.ourgrid.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.ourgrid.reqtrace.Req;

public class CommonUtils {
	
	private static final transient Logger LOG = Logger.getLogger( CommonUtils.class );
	
	/**
	 * Loads properties from a persistent File
	 * @param propFile The File where the properties are stored
	 * @return a Properties object containing the persistent properties
	 */
	public static Properties loadProperties(File propFile) {
		Properties properties = new Properties();
		if(propFile.exists()){
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(propFile);
				properties.load(fileInputStream);
			} catch (IOException e) {
				LOG.warn(e);//If it is not possible to load the file, we're assuming there are no properties set
			} finally {
				try {
					fileInputStream.close();
				} catch (IOException e) {}
			}
		}
		return properties;
	}
	
	
	@Req("REQ108")
	public static boolean checkKey(String storedPublicKey, String keyToCompare) {
		if(storedPublicKey == null){
			return false;
		}
		return storedPublicKey.equals(keyToCompare);
	}

	/**
	 * Stores a Properties object on a persistent File
	 * @param properties The Properties object
	 * @param propFile The File where the Properties should be stored
	 * @param propertiesName The name of this properties (will be on the file header)
	 */
	public static void saveProperties(Properties properties, File propFile, String propertiesName) throws IOException {
		if(propFile.exists()){
			propFile.delete();
		}
		
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(propFile);
			properties.store(fileOutputStream, propertiesName + " properties file");
			
		} catch (IOException e) {
			throw e;
		} finally {
			if(fileOutputStream != null) {
				fileOutputStream.close();
			}
		}
	}
	
	public static <K, V> Map<K, V> createSerializableMap() {
		return new TreeMap<K, V>();
	}
	
	public static <K, V> Map<K, V> createMap() {
		return new LinkedHashMap<K, V>();
	}
	
	public static <V> Set<V> createSet() {
		return new LinkedHashSet<V>();
	}
}
