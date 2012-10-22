/*
 * Copyright (c) 2002-2008 Universidade Federal de Campina Grande
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.ourgrid.acceptance.util.broker;

import org.ourgrid.acceptance.broker.BrokerAcceptanceTestComponent;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 * Requirement 301
 */
public class Req_301_Util extends BrokerAcceptanceUtil {

	public Req_301_Util(ModuleContext context) {
		super(context);
	}
	
	public BrokerServerModule createBrokerModule() throws CommuneNetworkException, ProcessorStartException, InterruptedException {
	    BrokerServerModule component = new BrokerAcceptanceTestComponent(context);
	    application = component;
	    
	    Thread.sleep(2000);
	    
	    return component;
	}
}