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
package org.ourgrid.broker.communication.operations;

import java.util.List;

import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.internal.IResponseTO;


/**
 * This type defines the interface of an operation. An operation is a step
 * defined in one of the task phases (init, remote or final).
 */
public interface Operation {

	void run(List<IResponseTO> responses) throws OperationException;


	/**
	 * Gets an identifier for the operation. Each operation have to define an
	 * attribute that uniquely identifies the operation within a replica.
	 * 
	 * @return an identifier for the operation.
	 */
	GenericTransferHandle getHandle();
}
