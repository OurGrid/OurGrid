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

import org.ourgrid.common.statistics.beans.aggregator.AG_Attribute;
import org.ourgrid.common.statistics.beans.aggregator.AG_Peer;
import org.ourgrid.common.statistics.beans.aggregator.AG_Worker;
import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.beans.peer.Worker;
import org.ourgrid.peer.status.util.PeerHistoryStatusBuilderHelper;

public class WorkerPair implements AGPair {

	private final Worker worker;
	private final AG_Worker workerAg;
	
	public WorkerPair(Worker worker, AG_Worker workerAg) {
		this.worker = worker;
		this.workerAg = workerAg;
	}

	public void addAGChildren(Object children) {
		workerAg.getAttributes().add((AG_Attribute) children);
	}

	public PeerPair createParentPair() {
		return new PeerPair(getParent(), PeerHistoryStatusBuilderHelper.convertPeer(getParent()));
	}

	public AG_Worker getAGObject() {
		return workerAg;
	}

	public Worker getObject() {
		return worker;
	}

	public Peer getParent() {
		return worker.getPeer();
	}

	public void setAGParent(Object parent) {
		workerAg.setPeer((AG_Peer) parent);
	}

}
