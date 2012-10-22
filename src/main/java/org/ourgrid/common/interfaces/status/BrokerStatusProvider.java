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
package org.ourgrid.common.interfaces.status;

import java.util.List;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.container.control.ModuleStatusProvider;

/**
 * Interface that must be implemented by a entity that wants to provide information about 
 * Jobs executions. Through this interface, a client also can register itself as a listener 
 * of the Broker Status.  
 *
 */
@Remote
public interface BrokerStatusProvider extends ModuleStatusProvider {

	public static final String OBJECT_NAME = "STATUS_PROVIDER";

	/**
	 * Retrieves to  the callback client complete information about to the Broker:
	 * Jobs executions, Workers allocated and Peers connected.
	 * @param client The client that requested information
	 */
	public void getCompleteStatus(BrokerStatusProviderClient client);
	
	/**
	 * Retrieves to the callback client information about the jobs executions.
	 * @param client The client that requested the information.
	 * @param jobsIds The ids of jobs that will have their execution status listed.   
	 */
	public void getCompleteJobsStatus(BrokerStatusProviderClient client, List<Integer> jobsIds);
	
	public void getJobsStatus(BrokerStatusProviderClient client, List<Integer> jobsIds);
	
	public void getPagedTasks(BrokerStatusProviderClient client, Integer jobId, Integer offset, Integer pageSize);
	
	/**
	 * Adds the received client as a listener of events triggered by this broker.
	 * @param client The client that will be registered as a listener of the broker.
	 */
	public void registerAsListener( BrokerStatusProviderClient client );
}
