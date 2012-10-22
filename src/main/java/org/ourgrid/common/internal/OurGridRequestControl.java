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
package org.ourgrid.common.internal;

import java.util.List;
import java.util.Map;

import org.ourgrid.common.util.CommonUtils;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

/**
 * Requirement 301
 */
public abstract class OurGridRequestControl implements RequestControlIF {
	
	
	private static RequestControlIF instance;
	
	
	private Map<String, RequesterIF<?>> requesterMap;
	private ResponseControlIF responseControl;
	
	protected OurGridRequestControl() {
		this.requesterMap = CommonUtils.createSerializableMap();
		fillMap();
		this.responseControl = createResponseControl();
	}
	
	
	public static RequestControlIF getInstance() {
		return instance;
	}
	
	public static void setInstance(RequestControlIF instance) {
		OurGridRequestControl.instance = instance;
	}
	
	
	protected abstract void fillMap();
	
	protected abstract ResponseControlIF createResponseControl();
	
	
	protected void addRequester(String requestType, RequesterIF<?> requester) {
		this.requesterMap.put(requestType, requester);
	}
	
	protected RequesterIF<?> getRequester(String requestType) {
		return this.requesterMap.get(requestType);
	}
	
	@SuppressWarnings("unchecked")
	public <U extends IRequestTO> void execute(U request, ServiceManager serviceManager) {
		RequesterIF<U> requester = (RequesterIF<U>) getRequester(request.getRequestType());
		List<IResponseTO> responses = requester.execute(request);
		executeResponses(serviceManager, responses);
	}


	protected void executeResponses(ServiceManager serviceManager, List<IResponseTO> responses) {
		this.responseControl.execute(responses, serviceManager);
	}
}
