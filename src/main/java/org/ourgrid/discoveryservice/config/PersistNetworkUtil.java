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
package org.ourgrid.discoveryservice.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.ourgrid.common.util.StringUtil;

import br.edu.ufcg.lsd.commune.context.ModuleContext;

public class PersistNetworkUtil {

	private String propertiesFileName;
	
	
	private static PersistNetworkUtil instance;
	
	
	private PersistNetworkUtil() {}
	
	
	public static PersistNetworkUtil getInstance() {
		if (instance == null) {
			instance = new PersistNetworkUtil();
		}
		
		return instance;
	}
	
	
	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}


	public void persistNetwork(Set<String> discoveryServicesAddresses, ModuleContext context) throws IOException {
		Properties props = new Properties();
		
		for (Entry<String, String> entry : context.getProperties().entrySet()) {
			props.put(entry.getKey(), entry.getValue());
		}
		
		props.put(DiscoveryServiceConfiguration.PROP_DS_NETWORK, StringUtil.concatAddresses(discoveryServicesAddresses));
		
		if (this.propertiesFileName == null) {
			this.propertiesFileName = DiscoveryServiceConfiguration.PROPERTIES_FILENAME;
		}

		FileOutputStream out = new FileOutputStream(propertiesFileName);
		
		props.store(out, null);
		
		out.flush();
		out.close();
	}



}
