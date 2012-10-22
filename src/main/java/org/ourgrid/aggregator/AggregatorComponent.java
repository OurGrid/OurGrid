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
package org.ourgrid.aggregator;

import org.ourgrid.aggregator.communication.receiver.AggregatorControlReceiver;

import br.edu.ufcg.lsd.commune.ServerModule;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 *
 */
public class AggregatorComponent extends ServerModule {
	
	/**
	 * Constructor of this class.
	 * @param context {@link ModuleContext}
	 * @throws CommuneNetworkException {@link CommuneNetworkException}
	 * @throws ProcessorStartException {@link ProcessorStartException}
	 */
	public AggregatorComponent(ModuleContext context) 
				throws CommuneNetworkException,	ProcessorStartException {
		super(AggregatorConstants.MODULE_NAME, context);
	}
	
	/**
	 * Create an AggregatorControlReceiver
	 * @return {@link AggregatorControlReceiver}
	 */
	@Override
	protected ServerModuleManager createApplicationManager() {
		return new AggregatorControlReceiver();
	}

}