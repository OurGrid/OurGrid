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

import org.ourgrid.common.statistics.beans.aggregator.AG_Worker;
import org.ourgrid.common.statistics.beans.aggregator.monitor.AG_WorkerStatusChange;
import org.ourgrid.common.statistics.beans.peer.Worker;
import org.ourgrid.common.statistics.beans.peer.monitor.WorkerStatusChange;
import org.ourgrid.peer.status.util.PeerHistoryStatusBuilderHelper;

public class WorkerStatusChangePair implements AGPair {

	public final WorkerStatusChange workersc;
	public final AG_WorkerStatusChange workerscAg;
	
	public WorkerStatusChangePair(WorkerStatusChange worker, AG_WorkerStatusChange workerAg) {
		this.workersc = worker;
		this.workerscAg = workerAg;
	}

	public void addAGChildren(Object children) {
		
	}

	public WorkerPair createParentPair() {
		return new WorkerPair(getParent(), PeerHistoryStatusBuilderHelper.convertWorker(getParent()));
	}

	public AG_WorkerStatusChange getAGObject() {
		return workerscAg;
	}

	public WorkerStatusChange getObject() {
		return workersc;
	}

	public Worker getParent() {
		return workersc.getWorker();
	}

	public void setAGParent(Object parent) {
		workerscAg.setWorker((AG_Worker) parent);
	}

}
