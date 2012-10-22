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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.ourgrid.common.exception.InvalidIntervalModificationException;

/**
 * Defines a closed interval and permits that logical conjunctions alters the
 * interval .
 */
public class Interval {

	/**
	 * Defines the left margin at the closed interval
	 */
	private int leftMargin;

	/**
	 * Defines the right margin at the closed interval
	 */
	private int rightMargin;

	public static final String GREATER_EQUAL = ">=";

	public static final String LESS_EQUAL = "<=";

	public static final String EQUAL = "==";

	public static final String GREATER = ">";

	public static final String LESS = "<";

	public static final int UNDEFINED = -1;


	/**
	 * Constructs a Interval [ - infinity, infinity ]
	 */
	public Interval() {

		this.leftMargin = UNDEFINED;
		this.rightMargin = UNDEFINED;
	}


	/**
	 * Modifies the interval with the expression.
	 * 
	 * @param expression Sintax: "Operator number" Valid operators are: " <=",
	 *        ">=", "==", ">", " <"
	 */
	public void modifyInterval( String expression ) throws InvalidIntervalModificationException {

		StringTokenizer tokenizer = new StringTokenizer( expression, " " );
		String operator = null;
		int factor = 0;

		try {
			operator = tokenizer.nextToken();
			factor = Integer.parseInt( tokenizer.nextToken() );

			if ( operator.equals( GREATER ) ) {
				factor++;
				setLeftMargin( factor );
			} else if ( operator.equals( GREATER_EQUAL ) ) {
				setLeftMargin( factor );
			} else if ( operator.equals( LESS ) ) {
				factor--;
				setRightMargin( factor );
			} else if ( operator.equals( LESS_EQUAL ) ) {
				setRightMargin( factor );
			} else if ( operator.equals( EQUAL ) ) {
				// Can be right or left margin, both only stay undefined before
				// the first modification
				if ( getRightMargin() == UNDEFINED || isInInterval( factor ) ) {
					setLeftMargin( factor );
					setRightMargin( factor );
				} else {
					throw new InvalidIntervalModificationException( "Factor is not in the interval " + toString() );
				}
			} else {
				throw new InvalidIntervalModificationException( "Invalid operator " + operator );
			}

		} catch ( NoSuchElementException nse ) {
			throw new InvalidIntervalModificationException();
		} catch ( NumberFormatException nfe ) {
			throw new InvalidIntervalModificationException();
		}

	}


	/**
	 * Verifies a number is in the interval
	 * 
	 * @param factor The number
	 * @return If the number is in the interval
	 */
	public boolean isInInterval( int factor ) {

		return (factor <= getRightMargin()) && (factor >= getLeftMargin());
	}


	/**
	 * Sets the right margin of the interval.
	 * 
	 * @param factor The value of the new right margin.
	 * @throws InvalidIntervalModificationException Case the factor is invalid.
	 */
	private void setRightMargin( int factor ) throws InvalidIntervalModificationException {

		// Undefined case
		if ( factor >= 0 && getRightMargin() == UNDEFINED && getLeftMargin() == UNDEFINED ) {
			this.leftMargin = 0;
			this.rightMargin = factor;

			// Other cases
		} else if ( factor >= 0 && factor <= getRightMargin() && factor >= getLeftMargin() ) {
			this.rightMargin = factor;

		} else {
			throw new InvalidIntervalModificationException( "Could not set right margin( Factor: " + factor
					+ " -- Interval: " + toString() + " )" );
		}
	}


	/**
	 * Sets the left margin of the interval.
	 * 
	 * @param factor The value of the new left margin.
	 * @throws InvalidIntervalModificationException Case the factor is invalid.
	 */
	private void setLeftMargin( int factor ) throws InvalidIntervalModificationException {

		// Undefined Case
		if ( factor >= 0 && getLeftMargin() == UNDEFINED && getRightMargin() == UNDEFINED ) {
			this.rightMargin = Integer.MAX_VALUE;
			this.leftMargin = factor;

			// Other cases
		} else if ( factor >= 0 && factor >= getLeftMargin() && factor <= getRightMargin() ) {
			this.leftMargin = factor;

		} else {
			throw new InvalidIntervalModificationException( "Could not set left margin( Factor: " + factor
					+ " -- Interval: " + toString() + " )" );
		}
	}


	/**
	 * Returns the left margin of the interval.
	 * 
	 * @return Returns the leftMargin.
	 */
	public int getLeftMargin() {

		return leftMargin;
	}


	/**
	 * Returns the right margin of the interval.
	 * 
	 * @return Returns the rightMargin.
	 */
	public int getRightMargin() {

		return rightMargin;
	}


	/**
	 * Returns a String representation of the Interval.
	 * 
	 * @return A String representation of the Interval.
	 */
	@Override
	public String toString() {

		return "[" + getLeftMargin() + "," + getRightMargin() + "]";
	}


	/**
	 * Checks if the margins were not set yet.
	 * 
	 * @return <code>true</code> case the margins were not set yet.
	 *         <code>false</code> otherwise.
	 */
	public boolean isUndefiined() {

		return getLeftMargin() == UNDEFINED && getRightMargin() == UNDEFINED;
	}
}
