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
import org.ourgrid.common.statistics.beans.aggregator.AG_Task;
import org.ourgrid.common.statistics.beans.peer.Job;
import org.ourgrid.common.statistics.beans.peer.Login;
import org.ourgrid.peer.status.util.PeerHistoryStatusBuilderHelper;

public class JobPair implements AGPair {

	private final Job job;
	private final AG_Job jobAg;
	
	public JobPair(Job job, AG_Job jobAg) {
		this.job = job;
		this.jobAg = jobAg;
	}

	public void addAGChildren(Object children) {
		jobAg.getTasks().add((AG_Task) children);		
	}

	public AGPair createParentPair() {
		return new LoginPair(getParent(), PeerHistoryStatusBuilderHelper.convertLogin(getParent()));
	}

	public AG_Job getAGObject() {
		return jobAg;
	}

	public Job getObject() {
		return job;
	}

	public Login getParent() {
		return job.getLogin();
	}

	public void setAGParent(Object parent) {
		jobAg.setLogin((AG_Login) parent);
	}
	
}