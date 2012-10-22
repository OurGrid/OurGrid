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
package org.ourgrid.acceptance.util.aggregator;

import java.io.File;

import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.aggregator.communication.receiver.AggregatorControlReceiver;

import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;


public class AggregatorTestComponent extends AggregatorComponent {
	
	private static final String SEP = File.separator;
	private static final String AGGREGATOR_TEST_DIR = "test" + SEP + "acceptance" + SEP + "aggregator";
	private static final String HIB_CFG_TEST_PATH = "aggregator-hibernate.cfg.xml";
	
	public AggregatorTestComponent(ModuleContext context)
			throws CommuneNetworkException, ProcessorStartException {
		super(context);
	}

	@Override
	protected ServerModuleManager createApplicationManager() {
		return new AggregatorControlReceiver(HIB_CFG_TEST_PATH);
	}
	
}
