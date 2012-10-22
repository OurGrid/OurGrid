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
package org.ourgrid.aggregator.status;

import org.ourgrid.common.status.CompleteStatus;

import br.edu.ufcg.lsd.commune.identification.ServiceID;


/**
 * This class is used to create an AggregatorCompleteStatus object that
 * store the ServiceID {@link ServiceID}.
 */
public class AggregatorCompleteStatus extends CompleteStatus {

	private static final long serialVersionUID = 3406131311366758862L;

	private final String dsUserAtSever;

	/**
	 * The constructor of this class.
	 * @param dsID {@link ServiceID}
	 * @param upTime {@link Long}
	 * @param configuration {@link String}
	 */
	public AggregatorCompleteStatus(String dsUserAtSever, 
			long upTime, String configuration) {
		super(upTime, configuration);
		this.dsUserAtSever = dsUserAtSever;
	}
	
	
	public String getDiscoveryServiceUserAtSever() {
		return dsUserAtSever;
	}

}
