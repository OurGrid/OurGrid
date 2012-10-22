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
package org.ourgrid.peer.communication.dao;

import java.util.Map;
import java.util.concurrent.Future;

import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.reqtrace.Req;


/**
 * Stores requests
 */
public class ScheduledRequestDAO{

	private static ScheduledRequestDAO requestDAO;
	
	private final Map<Long,Future<?>> scheduledAdverts;
	
	
	public static ScheduledRequestDAO getInstance() {
		if (requestDAO == null)
			requestDAO = new ScheduledRequestDAO();
		return requestDAO;
	}
	
	/**
	 * Create request manager.
	 */
	@Req("REQ010")
	private ScheduledRequestDAO()  {
		this.scheduledAdverts = CommonUtils.createSerializableMap();
	}

	
	/**
	 * @param spec
	 * @return
	 */
	public boolean containsAScheduledRequest(Long requestId) {
		//FIXME: verificar hash code e equals
		return scheduledAdverts.containsKey(requestId);
	}
		
	@Req("REQ011")
	public void putScheduledRequest(Long requestId, Future<?> advertFuture) {
		scheduledAdverts.put(requestId, advertFuture);
	}
	
	@Req("REQ116")
	public Future<?> removeFuture(Long requestId) {
		return scheduledAdverts.remove(requestId);
	}

}