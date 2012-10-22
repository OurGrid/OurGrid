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
package org.ourgrid.common.interfaces.status;

import java.io.Serializable;

public class ConsumerInfo implements Serializable {

	private static final long serialVersionUID = 40L;
	
	
	private int numberOfLocalWorkers;
	private String consumerIdentification;
	
	
	public ConsumerInfo(int numberOfLocalWorkers, String consumerIdentification) {
		this.numberOfLocalWorkers = numberOfLocalWorkers;
		this.consumerIdentification = consumerIdentification;
	}

	public String getConsumerIdentification() {
		return consumerIdentification;
	}
	
	public void setConsumerIdentification(String consumerIdentification) {
		this.consumerIdentification = consumerIdentification;
	}
	public int getNumberOfLocalWorkers() {
		return numberOfLocalWorkers;
	}
	
	public void setNumberOfLocalWorkers(int numberOfLocalWorkers) {
		this.numberOfLocalWorkers = numberOfLocalWorkers;
	}
	
	@Override
	public String toString() {
		return this.consumerIdentification;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((consumerIdentification == null) ? 0
						: consumerIdentification.hashCode());
		result = prime * result + numberOfLocalWorkers;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ConsumerInfo other = (ConsumerInfo) obj;
		if (consumerIdentification == null) {
			if (other.consumerIdentification != null)
				return false;
		} else if (!consumerIdentification.equals(other.consumerIdentification))
			return false;
		if (numberOfLocalWorkers != other.numberOfLocalWorkers)
			return false;
		return true;
	}
}
