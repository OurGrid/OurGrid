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
package org.ourgrid.peer.dao.trust;

import java.util.List;

import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.reqtrace.Req;

/**
 * This class should not be modified without a strong reason. It is used
 * in serialisation code using the XStream framework. The name of instance
 * fields are maped into xml archive. Changes in this code must be reflected
 * in <code>TrustCommunitiesFileManipulator</code>.   
 */
@Req("REQ110")
public class TrustCommunitiesSack {

	private final List<TrustyCommunity> communities;
	
	/**
	 * @param comm
	 */
	public TrustCommunitiesSack(List<TrustyCommunity> comm){
		communities = comm;
	}

	/**
	 * @return the communities
	 */
	public List<TrustyCommunity> getCommunities() {
		return communities;
	}
}
