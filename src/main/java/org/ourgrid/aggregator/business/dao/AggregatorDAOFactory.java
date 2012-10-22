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
package org.ourgrid.aggregator.business.dao;

/**
 * This class provides a Factory of AggregatorDAO object.
 *
 */
public class AggregatorDAOFactory {

	private static AggregatorDAOFactory daoFactory;

	private AggregatorDAO aggregatorDAO;
	
	private AggregatorDAOFactory() {}
		
	public static AggregatorDAOFactory getInstance() {
		if (daoFactory == null) {
			daoFactory = new AggregatorDAOFactory();
		}
		
		return daoFactory;
	}

	public AggregatorDAO getAggregatorDAO() {
		if (this.aggregatorDAO == null) {
			this.aggregatorDAO = new AggregatorDAO();
		}
		
		return this.aggregatorDAO;
	}
	
	/**
	 * Clean the persistence of memory.
	 */
	public void reset() {
		this.aggregatorDAO = null;
	}

}
