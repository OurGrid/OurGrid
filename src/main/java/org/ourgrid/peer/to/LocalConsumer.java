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
package org.ourgrid.peer.to;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.ourgrid.common.interfaces.LocalWorkerProviderClient;

/**
 * @author
 * since 28/08/2007
 */
public class LocalConsumer extends Consumer {

    private Collection<Request> requests = new LinkedList<Request>();
    
    /* (non-Javadoc)
	 * @see org.ourgrid.peer.to.Consumer#getPriority()
	 */
	public Priority getPriority() {
		return Priority.LOCAL_CONSUMER;
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.peer.to.Consumer#isLocal()
	 */
	public boolean isLocal() {
		return true;
	}

   public void addRequest(Request request) {
        requests.add(request);
    }

	@Override
	public List<AllocableWorker> getAllocableWorkers() {
		
		List<AllocableWorker> result = new LinkedList<AllocableWorker>();
		
		for (Request request : requests) {
			result.addAll(request.getAllocableWorkers());
		}
		
		return result;
	}

	@Override
	public void removeAllocableWorker(AllocableWorker allocableWorker) {
		
		for (Request request : requests) {
			request.removeAllocableWorker(allocableWorker);
		}
	}

	@Override
	public Class<?> getConsumerType() {
		return LocalWorkerProviderClient.class;
	}

}