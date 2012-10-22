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
package org.ourgrid.common.ui;

import org.ourgrid.reqtrace.Req;


public class MainUtil {
	
	/**
	 * Discard the first parameter from this String[]. This parameter represents
	 * the Command from line command.
	 * 
	 * @param params An String array with all parameter that came from line
	 *        command, including the specified Broker Command.
	 * @return All other parameters except the Broker Command
	 */
	@Req({"REQ001", "REQ003", "REQ010"})
	public static String[ ] discardFirstParameter( String[ ] params ) {

		String[ ] parameters = new String[ params.length - 1 ];

		for ( int i = 1; i < params.length; i++ ) {
			parameters[i - 1] = params[i];
		}

		return parameters;
	}

}
