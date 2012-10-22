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

import org.ourgrid.common.statistics.beans.aggregator.AG_Job;
import org.ourgrid.common.statistics.beans.aggregator.AG_Login;
import org.ourgrid.common.statistics.beans.aggregator.AG_User;
import org.ourgrid.common.statistics.beans.peer.Login;
import org.ourgrid.common.statistics.beans.peer.User;
import org.ourgrid.peer.status.util.PeerHistoryStatusBuilderHelper;

public class LoginPair implements AGPair {

	private final Login login;
	private final AG_Login loginAg;
	
	public LoginPair(Login login, AG_Login loginAg) {
		this.login = login;
		this.loginAg = loginAg;
	}
	
	public void addAGChildren(Object children) {
		loginAg.getJobs().add((AG_Job) children);
	}

	public UserPair createParentPair() {
		return new UserPair(getParent(), PeerHistoryStatusBuilderHelper.convertUser(getParent()));
	}

	public AG_Login getAGObject() {
		return loginAg;
	}

	public Login getObject() {
		return login;
	}

	public User getParent() {
		return login.getUser();
	}

	public void setAGParent(Object parent) {
		loginAg.setUser((AG_User) parent);
	}

}
