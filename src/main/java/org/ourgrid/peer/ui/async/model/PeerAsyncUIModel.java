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
package org.ourgrid.peer.ui.async.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.ui.OurGridUIModel;
import org.ourgrid.common.ui.servicesetup.ServiceSetupStrategy;
import org.ourgrid.peer.PeerComponentContextFactory;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.status.PeerCompleteStatus;
import org.ourgrid.peer.ui.async.client.PeerAsyncInitializer;
import org.ourgrid.peer.ui.async.servicesetup.PeerServiceSetupFactory;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;

/**
 * This class represents image data which stores the informations about
 * the peer async ui. It notifies the PeerAsyncUIListeners when changes occur.
 */
public class PeerAsyncUIModel implements OurGridUIModel{

	public static final String PEER_PROPERTIES_FILE = PeerConfiguration.PROPERTIES_FILENAME;
	private final List<PeerAsyncUIListener> listeners;
	private Properties loadedProperties;
	private boolean isPeerUp;
	private ServiceSetupStrategy serviceSetup;
	private boolean peerToStartOnRecovery;
	private Future<?> statusFuture;
	private boolean isEditXMMPConf;
	
	
    private static final String IMAGES_PATH = "/resources/images/";
    
    public static final URL XMPP_ONLINE_IMAGE_PATH = PeerAsyncUIModel.class.
				getResource(IMAGES_PATH + "xmpp_online.gif");
    public static final URL XMPP_OFFLINE_IMAGE_PATH = PeerAsyncUIModel.class.
				getResource(IMAGES_PATH + "xmpp_offline.gif");
    public static final URL XMPP_CONTACTING_ICON_IMAGE_PATH = PeerAsyncUIModel.class.
    			getResource(IMAGES_PATH + "xmpp_contacting.gif");
    public static final URL XMPP_EDITING_ICON_IMAGE_PATH = PeerAsyncUIModel.class.
		getResource(IMAGES_PATH + "xmpp_editing.gif");
    
	/** Creates new form PeerAsyncUIModel */
	public PeerAsyncUIModel() {
		this.listeners = new LinkedList<PeerAsyncUIListener>();
		this.loadedProperties = new Properties();
		this.serviceSetup = new PeerServiceSetupFactory().createPeerServiceSetupStrategy();
		this.peerToStartOnRecovery = false;
		this.isPeerUp = false;
		this.isEditXMMPConf = false;
	}

	/**
	 * It notifies the listeners that the workers status has been changed.
	 * @param localWorkers The informations about the workers status.
	 */
	public void updateWorkersStatus(List<WorkerInfo> localWorkers) {
		for (PeerAsyncUIListener listener : listeners) {
			listener.updateWorkersStatus(localWorkers);
		}
	}

	/**
	 * It notifies the listeners that the users status has been changed.
	 * @param localWorkers The informations about the users status.
	 */
	public void updateUsersStatus(List<UserInfo> usersInfo) {
		for (PeerAsyncUIListener listener : listeners) {
			listener.updateUsersStatus(usersInfo);
		}
	}

	/**
	 * It notifies the listeners that the peer complete status has been changed.
	 * @param localWorkers The informations about the peer complete status.
	 */
	public void updateCompleteStatus(PeerCompleteStatus completeStatus) {
		for (PeerAsyncUIListener listener : listeners) {
			listener.updateCompleteStatus(completeStatus);
		}
	}

	/**
	 * Adds a new listeners to this model.
	 * @param listener The listener to be added.
	 */
	public void addListener(PeerAsyncUIListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException(
					"The PeerAsyncUIListener to be added must not be null");
		}
		
		listeners.add(listener);
	}
	
	/**
	 * Loads the peer properties, obtaining them of the peer properties file.
	 */
	public void loadProperties() {
		ModuleContext context = new PeerComponentContextFactory(
				new PropertiesFileParser(PEER_PROPERTIES_FILE)).createContext();
		
		for (String propertyName : context.getPropertiesNames()) {
			loadedProperties.put(propertyName, context.getProperty(propertyName));
		}
	}
	
	/**
	 * Set a value to the specified peer property.
	 * @param property The property to be setted.
	 * @param value A value to the property.
	 */
	public void setProperty(String property, String value) {
		loadedProperties.put(property, value);
	}
	
	/**
	 * Returns the value of the specified property.
	 */
	public String getProperty(String property) {
		return (String) loadedProperties.get(property);
	}
	
	/**
	 * Save the peer properties.
	 * @throws IOException If there some problem to write on the peer properties file.
	 */
	public void saveProperties() throws IOException {
		loadedProperties.store(new FileOutputStream(PEER_PROPERTIES_FILE), null);
	}

	/**
	 * Notifies the listeners that the peer has been started.
	 */
	public void peerStarted() {
		isPeerUp = true;
		for (PeerAsyncUIListener listener : listeners) {
			listener.peerStarted();
		}
	}
	
	public void peerRestarted() {
		for (PeerAsyncUIListener listener : listeners) {
			listener.peerRestarted();
		}
	}
	
	/**
	 * Notifies the listeners that the peer has been stopped.
	 */
	public void peerStopped() {
		isPeerUp = false;
		for (PeerAsyncUIListener listener : listeners) {
			listener.peerStopped();
		}
	}
	
	/**
	 * Notifies the listeners that the peer has been inited.
	 */
	public void peerInited() {
		isEditXMMPConf = false;
		for (PeerAsyncUIListener listener : listeners) {
			listener.peerInited();
		}
		
	}
	
	/**
	 */
	public void peerEditing() {
		isEditXMMPConf = true;
		for (PeerAsyncUIListener listener : listeners) {
			listener.peerEditing();
		}
		
	}
	
	/**
	 * Notifies the listeners that the peer has not been inited.
	 */
	public void peerInitedFailed() {
		isEditXMMPConf = false;
		for (PeerAsyncUIListener listener : listeners) {
			listener.peerInitedFailed();
		}
		
	}

	/**
	 * Verify if the peer is up.
	 * @return <code>true</code> if the peer is up.
	 */
	public boolean isPeerUp() {
		return isPeerUp;
	}

	/**
	 * @return the serviceSetup
	 */
	public ServiceSetupStrategy getServiceSetup() {
		return serviceSetup;
	}

	public void setPeerStartOnRecovery(boolean startPeer) {
		this.peerToStartOnRecovery = startPeer;
		
	}

	/**
	 * @return the peerToStartOnRecovery
	 */
	public boolean isPeerToStartOnRecovery() {
		return peerToStartOnRecovery;
	}

	public boolean isStatusFutureCancelled() {
		return statusFuture == null || statusFuture.isCancelled() || statusFuture.isDone();
	}

	public void setStatusFuture(Future<?> scheduledActionFuture) {
		this.statusFuture = scheduledActionFuture;
		
	}

	public void cancelStatusFuture() {
		if (statusFuture!= null) {
			this.statusFuture.cancel(true);
			this.statusFuture = null;
		}
	}

	public void restoreDefaultPropertiesValues() {
		Map<Object,Object> defaultProperties = new PeerComponentContextFactory(null).getDefaultProperties();
		
		for (Object propertyName : defaultProperties.keySet()) {
			loadedProperties.put(propertyName, defaultProperties.get(propertyName));
		}
	}

	public void propertiesSaved() {
		isEditXMMPConf = false;
		PeerComponentContextFactory contextFactory = new PeerComponentContextFactory(
				new PropertiesFileParser(PEER_PROPERTIES_FILE));
    	
    	ModuleContext context = contextFactory.createContext();
    	
		try {
			PeerAsyncInitializer.getInstance().initComponentClient(context, this);
		} catch (Exception cre) {
			JOptionPane.showMessageDialog(null, cre.getMessage(), "Error on peer startup", JOptionPane.ERROR_MESSAGE);
			this.peerInited();
			return;
		}
		
	}
	
	
	public void editXMPPConf() {
		isEditXMMPConf = true;
		PeerAsyncInitializer.getInstance().stopComponentClient();
	}

	public boolean isPeerEditing() {
		return isEditXMMPConf;
	}


}
