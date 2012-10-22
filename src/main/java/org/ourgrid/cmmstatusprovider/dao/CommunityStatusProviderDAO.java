/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of Commune. 
 *
 * Commune is free software: you can redistribute it and/or modify it under the
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
package org.ourgrid.cmmstatusprovider.dao;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.ourgrid.cmmstatusprovider.CommunityStatusProviderCallback;
import org.ourgrid.cmmstatusprovider.DiscoveryServiceStateListener;
import org.ourgrid.common.interfaces.status.DiscoveryServiceStatusProvider;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.util.CommonUtils;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.DAO;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * @author Windows XP
 *
 */
public class CommunityStatusProviderDAO extends DAO {

	private final Map<ServiceID, Set<CommunityStatusProviderCallback>> peersCallback;
	private final Map<ServiceID, Set<CommunityStatusProviderCallback>> dsCallback;
	private final Map<ServiceID, PeerStatusProvider> peerStatusProviders;
	private final Map<ServiceID, DiscoveryServiceStatusProvider> dsStatusProviders;
	
	private final Map<String, DiscoveryServiceStateListener> dsStateListeners;
	
	/**
	 * @param container
	 */
	public CommunityStatusProviderDAO(Module container) {
		super(container);
		this.peersCallback = CommonUtils.createSerializableMap();
		this.dsCallback = CommonUtils.createSerializableMap();
		this.peerStatusProviders = CommonUtils.createSerializableMap();
		this.dsStatusProviders = CommonUtils.createSerializableMap();
		this.dsStateListeners = CommonUtils.createSerializableMap();
	}
	
	public void addDiscoveryServiceStateListener(String userAtServer, DiscoveryServiceStateListener listener) {
		dsStateListeners.put(userAtServer, listener);
	}

	public DiscoveryServiceStateListener getDiscoveryServiceStateListener(String userAtServer) {
		return dsStateListeners.get(userAtServer);
	}

	/**
	 * @param serviceID 
	 * @param callback
	 */
	public void addPeerCallback(ServiceID serviceID, CommunityStatusProviderCallback callback) {
		Set<CommunityStatusProviderCallback> callbacks = this.peersCallback.get(serviceID);
		if (callbacks == null) {
			callbacks = new LinkedHashSet<CommunityStatusProviderCallback>();
			peersCallback.put(serviceID, callbacks);
		}
		callbacks.add(callback);
	}

	/**
	 * @param serviceID 
	 * @param callback
	 */
	public void addDsCallback(ServiceID serviceID, CommunityStatusProviderCallback callback) {
		Set<CommunityStatusProviderCallback> callbacks = this.dsCallback.get(serviceID);
		if (callbacks == null) {
			callbacks = new LinkedHashSet<CommunityStatusProviderCallback>();
			dsCallback.put(serviceID, callbacks);
		}
		callbacks.add(callback);
	}
	
	/**
	 * @return the peersCallback
	 */
	public Set<CommunityStatusProviderCallback> getPeersCallback(ServiceID providerServiceID) {
		return peersCallback.get(providerServiceID);
	}

	public void clearPeersCallback(ServiceID providerServiceID) {
		peersCallback.remove(providerServiceID);
	}
	
	/**
	 * @return the dsCallback
	 */
	public Set<CommunityStatusProviderCallback> getDsCallback(ServiceID dsServiceID) {
		return dsCallback.get(dsServiceID);
	}
	
	
	public void clearDsCallback(ServiceID dsServiceID) {
		dsCallback.remove(dsServiceID);
	}
	

	public void addPeerStatusProvider(ServiceID serviceID, PeerStatusProvider statusProvider) {
		peerStatusProviders.put(serviceID, statusProvider);
	}
	
	public PeerStatusProvider removePeerStatusProvider(ServiceID serviceID) {
		return peerStatusProviders.remove(serviceID);
	}
	
	public void addDSStatusProvider(ServiceID serviceID, DiscoveryServiceStatusProvider statusProvider) {
		dsStatusProviders.put(serviceID, statusProvider);
	}
	
	public DiscoveryServiceStatusProvider removeDSStatusProvider(ServiceID serviceID) {
		return dsStatusProviders.remove(serviceID);
	}
	
	public DiscoveryServiceStatusProvider getDSStatusProvider(ServiceID serviceID) {
		return dsStatusProviders.get(serviceID);
	}
	
	public PeerStatusProvider getPeerStatusProvider(ServiceID serviceID) {
		return peerStatusProviders.get(serviceID);
	}
}
