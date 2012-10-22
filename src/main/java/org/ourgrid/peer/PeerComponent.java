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
package org.ourgrid.peer;

import org.ourgrid.peer.communication.receiver.PeerComponentReceiver;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.ServerModule;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 * Perform the Peer component control actions.
 */
@Req("REQ010")
public class PeerComponent extends ServerModule {
	
	public PeerComponent(ModuleContext context) throws CommuneNetworkException, ProcessorStartException {
		super(PeerConstants.MODULE_NAME, context);
	}
	
	@Override
	protected ServerModuleManager createApplicationManager() {
		return new PeerComponentReceiver();
	}

}

