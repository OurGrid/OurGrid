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
package org.ourgrid.common.exception;

import java.text.MessageFormat;

/**
 * Used to signalize that a part of the expression is syntactically malformed.
 * This expression is used at Broker to define the Job expressions to the match
 * moment. If the expressions passed by the compiler, it was already checked but
 * the user can use the Job definitions direct at the API.
 */
public class TokenErrorException extends OurgridException {

	private static final long serialVersionUID = 33L;

	/** The expression that is malformed */
	private String expression = "";

	/** The detailed problem at expression */
	private String detail = "";


	/**
	 * @param expression the malformed expression
	 * @param detail a message detail about the error
	 */
	public TokenErrorException(String expression, String detail) {
		super();
		this.expression = expression;
		this.detail = detail;
	}


	/**
	 * Returns the exception message.
	 * 
	 * @return The message
	 */
	public String getMessage() {
		Object params[] = { expression, detail };
		return MessageFormat.format(super.getMessage(), params);
	}

}
