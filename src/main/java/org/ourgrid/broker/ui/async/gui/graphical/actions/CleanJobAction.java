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
package org.ourgrid.broker.ui.async.gui.graphical.actions;

import java.awt.event.ActionEvent;

import org.ourgrid.broker.ui.async.client.BrokerAsyncInitializer;

public class CleanJobAction extends AbstractBrokerAction {

	private static final long serialVersionUID = 1L;

	private static final String ACTION_NAME = "Clean job";

	private final int jodID;

	public CleanJobAction( int jodID ) {
		this(getActionName(jodID), jodID);
	}
	
	public CleanJobAction(String title, int jodID ) {
		super(getActionName(jodID));
		this.jodID = jodID;
	}

	public void actionPerformed( ActionEvent e ) {
		BrokerAsyncInitializer.getInstance().getComponentClient().cleanJob(jodID);
	}

	public static String getActionName(int jodID) {
		return ACTION_NAME + " " + jodID;
	}
}
