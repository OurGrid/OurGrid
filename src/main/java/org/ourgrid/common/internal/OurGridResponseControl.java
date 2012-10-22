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

import org.ourgrid.common.internal.sender.CancelIncomingTransferSender;
import org.ourgrid.common.internal.sender.CancelOutgoingTransferSender;
import org.ourgrid.common.internal.sender.CreateRepeatedActionSender;
import org.ourgrid.common.internal.sender.DeployServiceSender;
import org.ourgrid.common.internal.sender.LoggerSender;
import org.ourgrid.common.internal.sender.RegisterInterestSender;
import org.ourgrid.common.internal.sender.ReleaseSender;
import org.ourgrid.common.internal.sender.UndeployServiceSender;
import org.ourgrid.common.util.CommonUtils;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

/**
 * Requirement 301
 */
public abstract class OurGridResponseControl implements ResponseControlIF {
	
	
	private Map<String, SenderIF<?>> senderExecutor;
	
	
	protected OurGridResponseControl() {
		fillMap();
	}
	
	
	protected void fillMap() {
		this.senderExecutor = CommonUtils.createSerializableMap();
		
		this.senderExecutor.put(OurGridResponseConstants.CANCEL_INCOMING_TRANSFER, new CancelIncomingTransferSender());
		this.senderExecutor.put(OurGridResponseConstants.CANCEL_OUTGOING_TRANSFER, new CancelOutgoingTransferSender());
		this.senderExecutor.put(OurGridResponseConstants.CREATE_REPEATED_ACTION, new CreateRepeatedActionSender());
		this.senderExecutor.put(OurGridResponseConstants.DEPLOY_SERVICE, new DeployServiceSender());
		this.senderExecutor.put(OurGridResponseConstants.LOGGER, new LoggerSender());
		this.senderExecutor.put(OurGridResponseConstants.REGISTER_INTEREST, new RegisterInterestSender());
		this.senderExecutor.put(OurGridResponseConstants.UNDEPLOY_SERVICE, new UndeployServiceSender());
		this.senderExecutor.put(OurGridResponseConstants.RELEASE, new ReleaseSender());
		
		addEntitySenders();
	}
	
	
	protected abstract void addEntitySenders();

	public void addSender(String responseType, SenderIF<?> executor) {
		this.senderExecutor.put(responseType, executor);
	}
	
	public SenderIF<?> getSender(String responseType) {
		return this.senderExecutor.get(responseType);
	}
	
	@SuppressWarnings("unchecked")
	public void execute(List<IResponseTO> responses, ServiceManager manager) {
		
		if (responses != null) {
			for (IResponseTO iResponseTO : responses) {
				SenderIF<IResponseTO> sender = (SenderIF<IResponseTO>) 
						getSender(iResponseTO.getResponseType());
				
				sender.execute(iResponseTO, manager);
			}
		}	
	}
}
