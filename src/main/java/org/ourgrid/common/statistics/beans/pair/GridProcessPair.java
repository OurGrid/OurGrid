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

import org.ourgrid.common.statistics.beans.aggregator.AG_Command;
import org.ourgrid.common.statistics.beans.aggregator.AG_GridProcess;
import org.ourgrid.common.statistics.beans.aggregator.AG_Task;
import org.ourgrid.common.statistics.beans.peer.GridProcess;
import org.ourgrid.common.statistics.beans.peer.Task;
import org.ourgrid.peer.status.util.PeerHistoryStatusBuilderHelper;

/**
 *
 */
public class GridProcessPair implements AGPair {

	private final GridProcess process;
	private final AG_GridProcess processAg;
	
	public GridProcessPair(GridProcess process, AG_GridProcess processAg) {
		this.process = process;
		this.processAg = processAg;
	}

	public void addAGChildren(Object children) {
		processAg.getCommands().add((AG_Command) children);		
	}

	public AGPair createParentPair() {
		return new TaskPair(getParent(), PeerHistoryStatusBuilderHelper.convertTask(getParent()));
	}

	public AG_GridProcess getAGObject() {
		return processAg;
	}

	public GridProcess getObject() {
		return process;
	}

	public Task getParent() {
		return process.getTask();
	}

	public void setAGParent(Object parent) {
		getAGObject().setTask((AG_Task) parent);
	}
}
