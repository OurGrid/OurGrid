/*
 * Copyright (C) 2011 Universidade Federal de Campina Grande
 *  
 * This file is part of Commune. 
 *
 * Commune is free software: you can redistribute it and/or modify it under the
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
package org.ourgrid.aggregator.communication.dao;

import java.util.concurrent.Future;

/**
 * This class provides a singleton of an AdvertActionFutureDAO.
 * He store Future {@link Future}
 *
 */
public class AdvertActionFutureDAO {

	private static AdvertActionFutureDAO advertActionFutureDao;
	
	private Future<?> advertActionFuture;
	
	private AdvertActionFutureDAO() {}
	
	public static AdvertActionFutureDAO getInstance(){
		if(advertActionFutureDao == null){
			advertActionFutureDao = new AdvertActionFutureDAO();
		}
	
		return advertActionFutureDao;
	}
	
	public Future<?> getAdvertActionFuture() {
		return this.advertActionFuture;
	}

	public void setAdvertActionFuture(Future<?> advertActionFuture) {
		this.advertActionFuture = advertActionFuture;
	}
	
}
