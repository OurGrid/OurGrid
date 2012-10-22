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
package org.ourgrid.common.statistics.beans.pair;

import org.ourgrid.common.statistics.beans.aggregator.AG_Login;
import org.ourgrid.common.statistics.beans.aggregator.AG_Peer;
import org.ourgrid.common.statistics.beans.aggregator.AG_User;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.beans.peer.User;
import org.ourgrid.peer.status.util.PeerHistoryStatusBuilderHelper;

public class UserPair implements AGPair {

	private final User user;
	private final AG_User userAg; 
	
	public UserPair(User user, AG_User userAg) {
		this.user = user;
		this.userAg = userAg;
	}

	public void addAGChildren(Object children) {
		userAg.getLogins().add((AG_Login) children);
	}

	public PeerPair createParentPair() {
		return new PeerPair(getParent(), PeerHistoryStatusBuilderHelper.convertPeer(getParent()));
	}

	public AG_User getAGObject() {
		return userAg;
	}

	public User getObject() {
		return user;
	}

	public Peer getParent() {
		return user.getPeer();
	}

	public void setAGParent(Object parent) {
		userAg.setPeer((AG_Peer) parent);
	}

}
