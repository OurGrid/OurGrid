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
import org.ourgrid.common.statistics.beans.peer.Command;
import org.ourgrid.common.statistics.beans.peer.GridProcess;
import org.ourgrid.peer.status.util.PeerHistoryStatusBuilderHelper;

/**
 *
 */
public class CommandPair implements AGPair {

	private final Command command;
	private final AG_Command commandAg;
	
	public CommandPair(Command command, AG_Command commandAg) {
		this.command = command;
		this.commandAg = commandAg;
	}

	public void addAGChildren(Object children) {
	}

	public GridProcessPair createParentPair() {
		return new GridProcessPair(getParent(), PeerHistoryStatusBuilderHelper.convertProcess(getParent()));
	}

	public AG_Command getAGObject() {
		return commandAg;
	}

	public Command getObject() {
		return command;
	}

	public GridProcess getParent() {
		return command.getProcess();
	}

	public void setAGParent(Object parent) {
		commandAg.setProcess((AG_GridProcess) parent);	
	}
	
}
