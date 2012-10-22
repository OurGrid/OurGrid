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
package org.ourgrid.peer.business.controller.matcher;

import java.util.Map;

/**
 * 
 */
public interface Matcher {

	static final int ATT_FALSE = 0;

	static final int ATT_TRUE = 1;

	static final int ATT_UNDEFINED = 2;


	boolean match( String jobRequirement, Map<String,String> machineAtt );

	/**
	 * New match implementation. It is used to verify matching between ClassAd expressions.
	 * @param jdlExpression A JDL expression.
	 * @param machineClassAd A machine classAd specification.
	 * @return A integer specifying the rank of this machine according to what is specified 
	 * in the JDL or -1 in case there is no match.
	 */
	int match( String jdlExpression, String machineClassAd );
}
