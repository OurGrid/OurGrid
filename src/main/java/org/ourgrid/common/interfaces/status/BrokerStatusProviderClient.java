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

import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.common.interfaces.to.BrokerCompleteStatus;
import org.ourgrid.common.interfaces.to.JobsPackage;

import br.edu.ufcg.lsd.commune.container.control.ModuleStatusProviderClient;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public interface BrokerStatusProviderClient extends ModuleStatusProviderClient {
	
	public static final String OBJECT_NAME = "SCHEDULER_STATUS_PROVIDER_CLIENT";

	public void hereIsCompleteStatus( ServiceID statusProviderServiceID, BrokerCompleteStatus status);
	
	public void hereIsCompleteJobsStatus( ServiceID statusProviderServiceID, JobsPackage jobsStatus);
	
	public void hereIsJobsStatus( ServiceID statusProviderServiceID, JobsPackage jobsStatus);

	public void hereIsPagedTasks(ServiceID serviceID, Integer jobId, Integer offset, List<TaskStatusInfo> pagedTasks);

	
}
