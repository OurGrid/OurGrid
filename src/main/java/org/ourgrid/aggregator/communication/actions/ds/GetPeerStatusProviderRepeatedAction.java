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
package org.ourgrid.aggregator.communication.actions.ds;

import java.io.Serializable;

import org.ourgrid.aggregator.request.GetPeerStatusProviderRepeatedActionRequestTO;
import org.ourgrid.common.internal.OurGridRequestControl;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;

/**
 * Class for actions to be determined in <code>Component</code> initialization.
 * A <code>GetPeerStatusProviderRepeatedAction</code> records a common behavior to be 
 * stored in the Component and to be scheduled with different parameters.
 */
public class GetPeerStatusProviderRepeatedAction implements RepeatedAction {
	
	/**
	 * {@inheritDoc}
	 */
	public void run(Serializable handler, ServiceManager serviceManager) {
		GetPeerStatusProviderRepeatedActionRequestTO requestTO = new GetPeerStatusProviderRepeatedActionRequestTO();
		OurGridRequestControl.getInstance().execute(requestTO, serviceManager);
	}
}
