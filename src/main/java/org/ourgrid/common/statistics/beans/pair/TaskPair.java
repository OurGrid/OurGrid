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

import org.ourgrid.common.statistics.beans.aggregator.AG_GridProcess;
import org.ourgrid.common.statistics.beans.aggregator.AG_Job;
import org.ourgrid.common.statistics.beans.aggregator.AG_Task;
import org.ourgrid.common.statistics.beans.peer.Job;
import org.ourgrid.common.statistics.beans.peer.Task;
import org.ourgrid.peer.status.util.PeerHistoryStatusBuilderHelper;

public class TaskPair implements AGPair {

	private final Task task;
	private final AG_Task taskAg;
	
	public TaskPair(Task task, AG_Task taskAg) {
		this.task = task;
		this.taskAg = taskAg;
	}

	public void addAGChildren(Object children) {
		taskAg.getProcesses().add((AG_GridProcess) children);
	}

	public JobPair createParentPair() {
		return new JobPair(getParent(), PeerHistoryStatusBuilderHelper.convertJob(getParent()));
	}

	public AG_Task getAGObject() {
		return taskAg;
	}

	public Task getObject() {
		return task;
	}

	public Job getParent() {
		return task.getJob();
	}

	public void setAGParent(Object parent) {
		getAGObject().setJob((AG_Job) parent);
	}
	
}
