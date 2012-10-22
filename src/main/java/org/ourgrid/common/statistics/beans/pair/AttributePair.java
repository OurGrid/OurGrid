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
import org.ourgrid.common.statistics.beans.aggregator.AG_Worker;
import org.ourgrid.common.statistics.beans.peer.Attribute;
import org.ourgrid.common.statistics.beans.peer.Worker;
import org.ourgrid.peer.status.util.PeerHistoryStatusBuilderHelper;

public class AttributePair implements AGPair {

	private final Attribute attribute;

	private final AG_Attribute attributeAg;
	
	public AttributePair(Attribute attribute, AG_Attribute attributeAg) {
		this.attribute = attribute;
		this.attributeAg = attributeAg;
	}
	
	public void addAGChildren(Object children) {
		
	}

	public WorkerPair createParentPair() {
		return new WorkerPair(getParent(), PeerHistoryStatusBuilderHelper.convertWorker(getParent()));
	}

	public AG_Attribute getAGObject() {
		return attributeAg;
	}

	public Attribute getObject() {
		return attribute;
	}

	public Worker getParent() {
		return attribute.getWorker();
	}

	public void setAGParent(Object parent) {
		attributeAg.setWorker((AG_Worker) parent);
	}

}
