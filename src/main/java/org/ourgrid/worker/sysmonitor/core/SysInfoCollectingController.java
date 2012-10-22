/*
 * Copyright (C) 2009 Universidade Federal de Campina Grande
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

package org.ourgrid.worker.sysmonitor.core;
import java.util.Map;

import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.sysmonitor.interfaces.WorkerSysInfoCollector;

import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

/**
 * 
 * @author Di�genes
 *
 */
public class SysInfoCollectingController implements WorkerSysInfoCollector {
	
	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	/**
	 * @return the serviceManager
	 */
	protected ServiceManager getServiceManager() {
		return serviceManager;
	}
	
	/**
	 * Fills the {@link WorkerSpecification} object with current informations 
	 * about the underlying system. 
	 *
	 */
	public void metricsChanged(Map<String, String> metricsMap ) {
		WorkerSpecification spec = WorkerDAOFactory.getInstance().getWorkerSpecDAO().getWorkerSpec();
		spec.putAttributes(metricsMap);
	}
}
