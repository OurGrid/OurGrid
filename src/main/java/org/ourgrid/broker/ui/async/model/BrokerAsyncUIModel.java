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
package org.ourgrid.broker.ui.async.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.ourgrid.broker.BrokerComponentContextFactory;
import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.broker.ui.async.client.BrokerAsyncInitializer;
import org.ourgrid.common.interfaces.to.BrokerCompleteStatus;
import org.ourgrid.common.ui.OurGridUIModel;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;

public class BrokerAsyncUIModel implements OurGridUIModel {

	public static final String BROKER_PROPERTIES_FILE = BrokerConfiguration.PROPERTIES_FILENAME;
	
	private boolean brokerToStartOnRecovery;
	private final List<BrokerAsyncUIListener> listeners;
	private boolean isBrokerUp;
	private Future<?> statusFuture;
	private List<String> jobHistory; //store jdf paths
	private Properties loadedProperties;

	private boolean isEditXMMPConf;

	public BrokerAsyncUIModel() {
		listeners = new LinkedList<BrokerAsyncUIListener>();
		this.loadedProperties = new Properties();
		statusFuture = null;
		this.brokerToStartOnRecovery = false;
		this.isBrokerUp = false;
		this.isEditXMMPConf = false;
		jobHistory = new LinkedList<String>();
	}
	
	public String getProperty(String prop) {
		return (String) loadedProperties.get(prop);
	}

	public void setBrokerStartOnRecovery(boolean start) {
		this.brokerToStartOnRecovery = start;
		
	}

	public void loadProperties() {
		ModuleContext context = new BrokerComponentContextFactory(
				new PropertiesFileParser(BROKER_PROPERTIES_FILE)).createContext();
		
		for (String propertyName : context.getPropertiesNames()) {
			loadedProperties.put(propertyName, context.getProperty(propertyName));
		}
	}

	public void addListener(BrokerAsyncUIListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException(
					"The BrokerAsyncUIListener to be added must not be null");
		}
		
		listeners.add(listener);
	}

	public boolean isBrokerToStartOnRecovery() {
		return this.brokerToStartOnRecovery;
	}

	public void brokerStarted() {
		isBrokerUp = true;
		for (BrokerAsyncUIListener listener : listeners) {
			listener.brokerStarted();
		}
		
	}
	
	public void brokerInited() {
		isEditXMMPConf = false;
		for (BrokerAsyncUIListener listener : listeners) {
			listener.brokerInited();
		}
		
	}
	
	public void brokerInitedFailed() {
		isEditXMMPConf = false;
		for (BrokerAsyncUIListener listener : listeners) {
			listener.brokerInitedFailed();
		}
		
	}
	
	public void brokerRestarted() {
		for (BrokerAsyncUIListener listener : listeners) {
			listener.brokerRestarted();
		}
	}


	public boolean isStatusFutureCancelled() {
		return statusFuture == null || statusFuture.isCancelled() || statusFuture.isDone();
	}

	public void setStatusFuture(Future<?> scheduledActionFuture) {
		this.statusFuture = scheduledActionFuture;
	}

	public void brokerStopped() {
		isBrokerUp = false;
		for (BrokerAsyncUIListener listener : listeners) {
			listener.brokerStopped();
		}
	}
	
	public void brokerEditing() {
		isEditXMMPConf = true;
		for (BrokerAsyncUIListener listener : listeners) {
			listener.brokerEditing();
		}
	}

	public void cancelStatusFuture() {
		if (statusFuture != null) {
			this.statusFuture.cancel(true);
			this.statusFuture = null;
		}
	}

	public boolean isBrokerUp() {
		return isBrokerUp;
	}

	public void addJobToHistory(String jdfPath) {
		jobHistory.add(0, jdfPath);
		fireJobHistoryUpdated();
	}

	public void cleanJobHistory() {
		jobHistory.clear();
		fireJobHistoryUpdated();
	}

	private void fireJobHistoryUpdated() {
		for (BrokerAsyncUIListener list : listeners) {
			list.jobHistoryUpdated(jobHistory);
		}
	}

	public void setProperty(String prop, String value) {
		loadedProperties.put(prop, value);
	}
	
	public void saveProperties() throws IOException {
		loadedProperties.store(new FileOutputStream(BROKER_PROPERTIES_FILE), null);
	}

	public void restoreDefaultPropertiesValues() {
		
		Map<Object,Object> defaultProperties = new BrokerComponentContextFactory(null).getDefaultProperties();
		
		for (Object propertyName : defaultProperties.keySet()) {
			loadedProperties.put(propertyName, defaultProperties.get(propertyName));
		}
	}

	public void hereIsCompleteStatus(BrokerCompleteStatus status) {
		for (BrokerAsyncUIListener list : listeners) {
			list.updateCompleteStatus(status);
		}
	}

	public void propertiesSaved() {
		isEditXMMPConf = false;
		BrokerComponentContextFactory contextFactory = new BrokerComponentContextFactory(
				new PropertiesFileParser(BROKER_PROPERTIES_FILE));
    	
    	ModuleContext context = contextFactory.createContext();
    	
		try {
			BrokerAsyncInitializer.getInstance().initComponentClient(context, this);
		} catch (Exception cre) {
			JOptionPane.showMessageDialog(null, cre.getMessage(), "Error on broker startup", JOptionPane.ERROR_MESSAGE);
			this.brokerInited();
			return;
		}
		
	}

	public void editXMPPConf() {
		isEditXMMPConf = true;
		BrokerAsyncInitializer.getInstance().stopComponentClient();
		
	}

	public boolean isBrokerEditing() {
		return isEditXMMPConf;
	}

}
