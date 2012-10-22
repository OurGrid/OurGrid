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
package org.ourgrid.worker.ui.async.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.ourgrid.common.interfaces.status.WorkerCompleteStatus;
import org.ourgrid.common.ui.OurGridUIModel;
import org.ourgrid.common.ui.servicesetup.ServiceSetupStrategy;
import org.ourgrid.worker.WorkerComponentContextFactory;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.ui.async.client.WorkerAsyncInitializer;
import org.ourgrid.worker.ui.async.servicesetup.WorkerServiceSetupFactory;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;

/**
 * This class represents image data which stores the informations about
 * the worker async ui. It notifies the WorkerAsyncUIListeners when changes occur.
 */
public class WorkerAsyncUIModel implements OurGridUIModel {

	public static final String WORKER_PROPERTIES_FILE = WorkerConfiguration.PROPERTIES_FILENAME;
	private Properties loadedProperties;
	private final List<WorkerAsyncUIListener> listeners;
	private ServiceSetupStrategy serviceSetup;
	private boolean workerToStartOnRecovery;
	private Future<?> statusFuture;
	private boolean isWorkerUp;
	private boolean isEditXMMPConf;
	
	/** Creates new form WorkerAsyncUIModel */
	public WorkerAsyncUIModel() {
		this.loadedProperties = new Properties();
		this.listeners = new LinkedList<WorkerAsyncUIListener>();
		this.serviceSetup = new WorkerServiceSetupFactory().createPeerServiceSetupStrategy();
		this.isEditXMMPConf = false;
	}
	
	/**
	 * Adds a new listeners to this model.
	 * @param listener The listener to be added.
	 */
	public void addListener(WorkerAsyncUIListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException(
					"The WorkerAsyncUIListener to be added must not be null");
		}
		
		listeners.add(listener);
	}
	
	/**
	 * Loads the worker properties, obtaining them of the worker properties file.
	 */
	public void loadProperties() {
		ModuleContext context = new WorkerComponentContextFactory(
				new PropertiesFileParser(WORKER_PROPERTIES_FILE)).createContext(false);
		
		for (String propertyName : context.getPropertiesNames()) {
			loadedProperties.put(propertyName, context.getProperty(propertyName));
		}
	}
	
	/**
	 * Returns the value of the specified property.
	 */
	public String getProperty(String prop) {
		return (String) loadedProperties.get(prop);
	}
	
	/**
	 * Set a value to the specified worker property.
	 * @param property The property to be setted.
	 * @param value A value to the property.
	 */
	public void setProperty(String property, String value) {
		loadedProperties.put(property, value);
	}

	/**
	 * Save the worker properties.
	 * @throws IOException If there some problem to write on the worker properties file.
	 */
	public void saveProperties() throws FileNotFoundException, IOException {
		loadedProperties.store(new FileOutputStream(WORKER_PROPERTIES_FILE), null);
	}
	
	/**
	 * Notifies the listeners that the worker has been started.
	 */
	public void workerStarted() {
		isWorkerUp = true;
		for (WorkerAsyncUIListener listener : listeners) {
			listener.workerStarted();
		}
	}

	/**
	 * Notifies the listeners that the worker has been stopped.
	 */
	public void workerStopped() {
		isWorkerUp = false;
		for (WorkerAsyncUIListener listener : listeners) {
			listener.workerStopped();
		}
	}
	
	/**
	 * Notifies the listeners that the worker has been inited.
	 */
	public void workerInited() {
		isEditXMMPConf = false;
		for (WorkerAsyncUIListener listener : listeners) {
			listener.workerInited();
		}
		
	}
	
	/**
	 * Notifies the listeners that the worker has been inited.
	 */
	public void workerEditing() {
		isEditXMMPConf = true;
		for (WorkerAsyncUIListener listener : listeners) {
			listener.workerEditing();
		}
		
	}
	
	/**
	 * Notifies the listeners that the worker has not been inited.
	 */
	public void workerInitedFailed() {
		isEditXMMPConf = false;
		for (WorkerAsyncUIListener listener : listeners) {
			listener.workerInitedFailed();
		}
		
	}
	
	public void workerRestarted() {
		for (WorkerAsyncUIListener listener : listeners) {
			listener.workerRestarted();
		}
	}

	public void updateCompleteStatus(WorkerCompleteStatus completeStatus) {
		for (WorkerAsyncUIListener listener : listeners) {
			listener.updateCompleteStatus(completeStatus);
		}
	}

	public void workerPaused() {
		for (WorkerAsyncUIListener listener : listeners) {
			listener.workerPaused();
		}
		
	}

	public void workerResumed() {
		for (WorkerAsyncUIListener listener : listeners) {
			listener.workerResumed();
		}
		
	}

	/**
	 * @return the serviceSetup
	 */
	public ServiceSetupStrategy getServiceSetup() {
		return serviceSetup;
	}
	
	public void setWorkerStartOnRecovery(boolean workerToStartOnRecovery) {
		this.workerToStartOnRecovery = workerToStartOnRecovery;
	}

	/**
	 * @return the workerToStartOnRecovery
	 */
	public boolean isWorkerToStartOnRecovery() {
		return workerToStartOnRecovery;
	}

	public boolean isStatusFutureCancelled() {
		return statusFuture == null || 
			statusFuture.isCancelled() || 
			statusFuture.isDone();
	}

	public void setStatusFuture(Future<?> scheduledActionFuture) {
		this.statusFuture = scheduledActionFuture;
		
	}

	public void cancelStatusFuture() {
		if (statusFuture != null) {
			this.statusFuture.cancel(true);
			this.statusFuture = null;
		}
	}

	public boolean isWorkerUp() {
		return isWorkerUp;
	}

	public void restoreDefaultPropertiesValues() {
		Map<Object,Object> defaultProperties = new WorkerComponentContextFactory(null).getDefaultProperties();
		
		for (Object propertyName : defaultProperties.keySet()) {
			loadedProperties.put(propertyName, defaultProperties.get(propertyName));
		}
		
	}

	public void propertiesSaved() {
		isEditXMMPConf = false;
		WorkerComponentContextFactory contextFactory = new WorkerComponentContextFactory(
				new PropertiesFileParser(WORKER_PROPERTIES_FILE));
    	
    	ModuleContext context = contextFactory.createContext();
    	
		try {
			WorkerAsyncInitializer.getInstance().initComponentClient(context, this);
		} catch (Exception cre) {
			JOptionPane.showMessageDialog(null, cre.getMessage(), "Error on worker startup", JOptionPane.ERROR_MESSAGE);
			this.workerInited();
			return;
		}
	}

	public void editXMPPConf() {
		isEditXMMPConf = true;
		WorkerAsyncInitializer.getInstance().stopComponentClient();
	}

	public boolean isWorkerEditing() {
		return isEditXMMPConf;
	}


}
