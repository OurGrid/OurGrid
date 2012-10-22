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
package org.ourgrid.peer.business.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.peer.to.LocalConsumer;
import org.ourgrid.peer.to.PeerUser;
import org.ourgrid.peer.to.RemoteConsumer;


/**
 * Stores consumers
 */
public class ConsumerDAO {

    private Map<String, LocalConsumer> localConsumers = CommonUtils.createSerializableMap();
    private Map<String, RemoteConsumer> remoteConsumers = CommonUtils.createSerializableMap();

    
	public LocalConsumer getLocalConsumer(String publicKey) {
        return localConsumers.get(publicKey);
	}
	
	public boolean isUserConsuming(String publicKey) {
		return localConsumers.containsKey(publicKey) || remoteConsumers.containsKey(publicKey);
	}
	
	public LocalConsumer createLocalConsumer(PeerUser user) {
	    String publicKey = user.getPublicKey();
        LocalConsumer consumer = getLocalConsumer(publicKey);
	    
	    if (consumer == null) {
	        consumer = new LocalConsumer();

	        localConsumers.put(publicKey, consumer);
	    }
	    
	    return consumer;
	}
	
    public RemoteConsumer getRemoteConsumer(String publicKey) {
        return publicKey != null ? remoteConsumers.get(publicKey) : null;
    }
    
	public void addRemoteConsumer(String consumerPublicKey, RemoteConsumer consumer) {
		remoteConsumers.put(consumerPublicKey, consumer);
	}

	public void removeLocalConsumer(String publicKey) {
		localConsumers.remove(publicKey);
	}

	public void removeRemoteConsumer(String publicKey) {
		remoteConsumers.remove(publicKey);
	}

	public List<LocalConsumer> getLocalConsumers() {
		return new ArrayList<LocalConsumer>(localConsumers.values());
	}
	
	public List<RemoteConsumer> getRemoteConsumers() {
		return new ArrayList<RemoteConsumer>(remoteConsumers.values());
	}
}