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
package org.ourgrid.peer.controller.matcher;

import junit.framework.TestCase;

import org.ourgrid.common.exception.InvalidIntervalModificationException;
import org.ourgrid.peer.business.controller.matcher.Interval;

public class IntervalTest extends TestCase {

	Interval interval;


	/*
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {

		interval = new Interval();
	}


	public void testIntervalConstructor() {

		assertEquals( Interval.UNDEFINED, interval.getLeftMargin() );
		assertEquals( Interval.UNDEFINED, interval.getRightMargin() );
	}


	public void testInterval() throws Exception {

		interval.modifyInterval( ">= 5" );
		interval.modifyInterval( "<= 10" );
		assertEquals( 5, interval.getLeftMargin() );
		assertEquals( 10, interval.getRightMargin() );
	}


	public void testInterval2() throws Exception {

		interval.modifyInterval( "> 5" );
		interval.modifyInterval( "< 10" );
		assertEquals( 6, interval.getLeftMargin() );
		assertEquals( 9, interval.getRightMargin() );
	}


	public void testInterval3() throws Exception {

		interval.modifyInterval( ">= 5" );
		interval.modifyInterval( "== 5" );
		assertEquals( 5, interval.getLeftMargin() );
		assertEquals( 5, interval.getRightMargin() );
	}


	public void testInterval4() throws Exception {

		interval.modifyInterval( ">= 5" );
		interval.modifyInterval( "== 6" );
		assertEquals( 6, interval.getLeftMargin() );
		assertEquals( 6, interval.getRightMargin() );
	}


	public void testInterval5() throws Exception {

		interval.modifyInterval( ">= 5" );
		interval.modifyInterval( "<= 5" );
		assertEquals( 5, interval.getLeftMargin() );
		assertEquals( 5, interval.getRightMargin() );
	}


	public void testInterval6() throws Exception {

		interval.modifyInterval( ">= 5" );
		assertEquals( 5, interval.getLeftMargin() );
		assertEquals( Integer.MAX_VALUE, interval.getRightMargin() );
	}


	public void testInterval7() throws Exception {

		interval.modifyInterval( "<= 5" );
		assertEquals( 0, interval.getLeftMargin() );
		assertEquals( 5, interval.getRightMargin() );
	}


	public void testInterval8() throws Exception {

		try {
			interval.modifyInterval( "<= 5" );
			interval.modifyInterval( "> 5" );
			fail();
		} catch ( InvalidIntervalModificationException iime ) {
		}
	}


	public void testInterval9() throws Exception {

		try {
			interval.modifyInterval( "== 5" );
			interval.modifyInterval( "== 6" );
			System.out.println( interval );
			fail();
		} catch ( InvalidIntervalModificationException iime ) {
		}
	}


	public void testInterval10() throws Exception {

		interval.modifyInterval( "== 5" );
		interval.modifyInterval( "== 5" );
		assertEquals( 5, interval.getLeftMargin() );
		assertEquals( 5, interval.getRightMargin() );
	}


	public void testInterval11() throws Exception {

		try {
			interval.modifyInterval( "< 5" );
			interval.modifyInterval( "> 6" );
			fail();
		} catch ( InvalidIntervalModificationException iime ) {
		}
	}


	public void testInterval12() throws Exception {

		try {
			interval.modifyInterval( "<= 5" );
			interval.modifyInterval( ">= 6" );
			fail();
		} catch ( InvalidIntervalModificationException iime ) {
		}
	}


	public void testInterval13() throws Exception {

		try {
			interval.modifyInterval( ">= -1" );
			interval.modifyInterval( "<= 6" );
			fail();
		} catch ( InvalidIntervalModificationException iime ) {
		}
	}


	public void testInterval14() throws Exception {

		interval.modifyInterval( "> -1" );
		interval.modifyInterval( "< 5" );
		assertEquals( 0, interval.getLeftMargin() );
		assertEquals( 4, interval.getRightMargin() );
	}


	public void testInterval15() throws Exception {

		try {
			interval.modifyInterval( "> 5" );
			interval.modifyInterval( "== 3" );
			fail();
		} catch ( InvalidIntervalModificationException iime ) {
		}
	}


	public void testInterval16() throws Exception {

		try {
			interval.modifyInterval( "> a" );
			interval.modifyInterval( "== 3" );
			fail();
		} catch ( InvalidIntervalModificationException iime ) {
		}
	}


	public void testInterval17() throws Exception {

		try {
			interval.modifyInterval( ">" );
			fail();
		} catch ( InvalidIntervalModificationException iime ) {
		}
	}


	public void testInterval18() throws Exception {

		try {
			interval.modifyInterval( "3" );
			fail();
		} catch ( InvalidIntervalModificationException iime ) {
		}
	}


	public void testInterval19() throws Exception {

		interval.modifyInterval( ">= 10" );
		interval.modifyInterval( ">  20" );
		assertEquals( 21, interval.getLeftMargin() );
		assertEquals( Integer.MAX_VALUE, interval.getRightMargin() );
	}


	public void testInterval20() throws Exception {

		interval.modifyInterval( "<= 10" );
		interval.modifyInterval( "<  5" );
		assertEquals( 0, interval.getLeftMargin() );
		assertEquals( 4, interval.getRightMargin() );
	}
}
