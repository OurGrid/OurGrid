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
package org.ourgrid.common.job;

import java.io.Serializable;

import org.ourgrid.common.interfaces.Shutdownable;

/**
 * A <code>JobCounter</code> is used by the <code>JobManager</code> class to
 * determine how <code>Job</code> ids are set.
 * 
 * @see SimpleJobManager
 * 
 * Requirement 302
 */
public interface JobCounter extends Shutdownable, Serializable {

	/**
	 * Gets the id of the next job.
	 * 
	 * @return Id of the next job.
	 */
	public int nextJobId();


	/**
	 * Returns the latest generated job id
	 * 
	 * @return the latest generated job id
	 */
	public int getJobId();
}
