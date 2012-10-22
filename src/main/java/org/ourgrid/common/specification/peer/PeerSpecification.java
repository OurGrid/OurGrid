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
package org.ourgrid.common.specification.peer;

import java.util.Map;

import org.ourgrid.common.specification.OurGridSpecification;
import org.ourgrid.peer.PeerConstants;

public class PeerSpecification extends OurGridSpecification {

	public static final String ATT_LABEL = "label";

	private static final long serialVersionUID = 40L;


	public PeerSpecification() {

		super();
	}


	public PeerSpecification( Map<String,String> attributes ) {

		super( attributes );
	}


	@Override
	protected String getModuleName() {

		return PeerConstants.MODULE_NAME;
	}


	@Override
	protected String getObjectName() {

		return PeerConstants.LOCAL_WORKER_PROVIDER;
	}


	@Override
	public boolean isValid() {

		return super.isValid();
	}
}
