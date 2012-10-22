/*
 * Copyright (C) 2011 Universidade Federal de Campina Grande
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
package org.ourgrid.aggregator.request;

import org.ourgrid.aggregator.business.requester.AggregatorRequestConstants;
import org.ourgrid.aggregator.communication.receiver.AggregatorControlReceiver;
import org.ourgrid.common.internal.IRequestTO;

import br.edu.ufcg.lsd.commune.context.ModuleContext;

/**
 * This class is a Transfer Object from one AggregatorControlReceiver{@link AggregatorControlReceiver}
 * to GerCompleteStatusResquester{@link GerCompleteStatusResquester}.
 */
public class GetCompleteStatusRequestTO implements IRequestTO {
	
	private String REQUEST_TYPE = AggregatorRequestConstants.GET_COMPLETE_STATUS.getString();
	
	private String clientAddress;
	
	private ModuleContext containerContext;
	
	private boolean canStatusBeUsed;
	
	private Long upTime;
	
	private String propConfDir;
	
	private String contextString;
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
		
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setCanStatusBeUsed(boolean canStatusBeUsed) {
		this.canStatusBeUsed = canStatusBeUsed;
	}
	
	public boolean getCanStatusBeUsed() {
		return canStatusBeUsed;
	}

	public void setUpTime(Long upTime) {
		this.upTime = upTime;
	}

	public Long getUpTime() {
		return upTime;
		
	}

	public void setContainerContext(ModuleContext containerContext) {
		this.containerContext = containerContext;
	}

	public ModuleContext getContainerContext() {
		return containerContext;
	}

	public void setPropConfDir(String propConfDir) {
		this.propConfDir = propConfDir;
	}

	public String getPropConfDir() {
		return propConfDir;
	}

	public void setContextString(String contextString) {
		this.contextString = contextString;
	}

	public String getContextString() {
		return contextString;
	}

}
