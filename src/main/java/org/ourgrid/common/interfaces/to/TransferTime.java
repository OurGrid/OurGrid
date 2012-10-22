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

package org.ourgrid.common.interfaces.to;

import java.io.Serializable;

public class TransferTime implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long initTime;
	private Long endTime;
	
	public TransferTime() {
		this.initTime = -1L;
		this.endTime = -1L;
	}

	public Long getInitTime() {
		return initTime;
	}

	public void setInitTime() {
		this.initTime = System.currentTimeMillis();
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime() {
		this.endTime = System.currentTimeMillis();
	}

	public void setInitTime(Long initTime) {
		this.initTime = initTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
}
