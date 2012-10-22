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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.peer.to.LocalConsumer;
import org.ourgrid.peer.to.Request;
import org.ourgrid.reqtrace.Req;


/**
 * Stores requests
 */
public class RequestDAO{

	private final Map<Long,Request> requests;
	private final Set<Long> scheduledRequestsIds;
	
	/**
	 * Create request manager.
	 */
	@Req("REQ010")
	public RequestDAO()  {
		this.requests = CommonUtils.createSerializableMap();
		this.scheduledRequestsIds = new LinkedHashSet<Long>();
	}

	public boolean removeScheduledRequest(Long requestId) {
		return scheduledRequestsIds.remove(requestId);
	}
	
	public boolean containsScheduledRequest(Long requestId) {
		return scheduledRequestsIds.contains(requestId);
	}
	
	public void addScheduledRequest(Long requestId) {
		scheduledRequestsIds.add(requestId);
	}
	
	/**
	 * @param workerProviderClient
	 * @param requestSpecification
	 * @return
	 */
	public Request createRequest(String lwpDID, String lwpPublicKey, 
			RequestSpecification requestSpecification, LocalConsumer localConsumer) {
        
		localConsumer.setConsumer(lwpDID, lwpPublicKey);
        
        Request request = new Request(requestSpecification);
        request.setConsumer(localConsumer);
        localConsumer.addRequest(request);
		
        long requestID = requestSpecification.getRequestId();
		this.requests.put(requestID, request);
		
		//TODO Remove
		//System.out.println("Request " + request);
		//System.out.println("RequestId " + requestID);
		//System.out.println("RequestSpec " + requestSpec);
		
		return request;
	}

	/**
	 * @param requestID
	 * @return The <code>Request</code> if the request exists, null otherwise.
	 */
	public Request getRequest(Long requestID) {
		return this.requests.get(requestID);
	}

	/**
	 * Verify if a request is running (created and not finished) in this peer.
	 * @param requestSpecification Request to be verified
	 * @return True if the request is running
	 */
	@Req("REQ027")
	public boolean isRunning(RequestSpecification requestSpecification) {
		
		if (requestSpecification == null) {
			return false;
		}
		
		Request request = this.requests.get(requestSpecification.getRequestId());
		return request != null;
	}

	/**
	 * Return a collection of Requests in a reverse order of creation
	 * @return
	 */
	@Req({"REQ018"})
	public List<Request> getRunningRequests() {
		LinkedList<Request> reverseOrderRequests = new LinkedList<Request>();
		
		// The newer requests must come first in the list
		for(Request request : this.requests.values()) {
			reverseOrderRequests.addFirst(request);
		}
		
		return reverseOrderRequests;
	}
	
	/**
	 * Remove a <code>Request</code> by request identification
	 * @param requestID
	 */
	public void removeRequest(Long requestID) {
		Request request = this.requests.remove(requestID);
		request.pause();
	}
		
}