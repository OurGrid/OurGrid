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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.ourgrid.peer.business.controller.matcher.Matcher;
import org.ourgrid.peer.business.controller.matcher.MatcherImpl;

public class MatcherTest extends TestCase {

	private Map<String,String> machine1 = new HashMap<String,String>();

	private Map<String,String> machine2 = new HashMap<String,String>();

	private Map<String,String> machine3 = new HashMap<String,String>();

	private Matcher matcher;


	/*
	 * Constructor
	 */
	public MatcherTest( String s ) {

		super( s );
		
		matcher = new MatcherImpl();
	}


	/*
	 * Initialization of the test
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {

		machine1.put( "username", "machine1" );
		machine1.put( "os", "linux" );
		machine1.put( "site", "lsd" );
		machine1.put( "ram", "200" );
		machine1.put( "SO", "bla" );

		machine2.put( "username", "machineWithoutAttributes" );

		machine3.put( "username", "machine3" );
		machine3.put( "os", "windows" );
		machine3.put( "site", "puc" );
		machine3.put( "mem", "50" );
	}


	/*
	 * Finishes the test
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}


	public void testMatchesWithNotDefinedAttributes() {

		String jobRequirement = "(site != lsd.ufcg.edu.br)";
		boolean matched = matcher.match( jobRequirement, machine2 );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "(site != lsd.ufcg.edu.br) && (os == linux)";
		matched = matcher.match( jobRequirement, machine2 );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "( site != lsd.ufcg.edu.br ) AND ( os == windows )";
		matched = matcher.match( jobRequirement, machine2 );
		assertFalse( matched );

		jobRequirement = "(site != lsd.ufcg.edu.br) || (os == windows)";
		matched = matcher.match( jobRequirement, machine2 );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "(site != lsd.ufcg.edu.br) OR ( os == windows)";
		matched = matcher.match( jobRequirement, machine2 );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "! (site != lsd.ufcg.edu.br)";
		matched = matcher.match( jobRequirement, machine2 );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "(mem < 200)";
		matched = matcher.match( jobRequirement, machine2 );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "(mem <= 200)";
		matched = matcher.match( jobRequirement, machine2 );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "(mem > 200)";
		matched = matcher.match( jobRequirement, machine2 );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "(mem >= 200)";
		matched = matcher.match( jobRequirement, machine2 );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "! (site != lsd.ufcg.edu.br) || (mem >= 200)";
		matched = matcher.match( jobRequirement, machine2 );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertFalse( matched );
	}


	public void testMatchesWithDefinedAndNotDefinedAttributes() {

		String jobRequirement = "(site != lsd.ufcg.edu.br)";
		boolean matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 + " ? ==> " + matched );
		assertTrue( matched );

		jobRequirement = "(site != lsd.ufcg.edu.br) && (so == linux)";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "( site == puc ) AND ( os == windows )";
		matched = matcher.match( jobRequirement, machine3 );
		assertTrue( matched );

		jobRequirement = "(site == puc) || (os == linux)";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 + " ? ==> " + matched );
		assertTrue( matched );

		jobRequirement = "(site == lsd) OR ( os == windows)";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 + " ? ==> " + matched );
		assertTrue( matched );

		jobRequirement = "! (caca != lsd)";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "(mem < 200) || ( caca = lsd)";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 + " ? ==> " + matched );
		assertTrue( matched );

		jobRequirement = "(mem <= 200)";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 + " ? ==> " + matched );
		assertTrue( matched );

		jobRequirement = "(mem > 200)";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "(mem >= 200)";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 + " ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "! (site != lsd.ufcg.edu.br) || (mem >= 200) || ( os == windows ) || ( caca == xpto )";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 + " ? ==> " + matched );
		assertTrue( matched );
	}


	public void testGoodMatches() {

		String jobRequirement = "os == linux";
		boolean matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "site == lsd";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "( site == lsd && os == linux )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "( site == xpto || os == linux )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "( site != xpto && os == linux )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "( ( site == ucsd || site == lsd ) && os == linux )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "( os == linux &&  ! ( site == ucsd ) )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "ram > 100";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "((site == ucsd|| site== lsd ) && ram >100 )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "( ram < 300 )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "( (site == ucsd || site == lsd )&& ( ram > 100 && ram< 300 ))";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "( ( site = ucsd || site = lsd ) && ram >= 200 )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "( ( site == ucsd || site == lsd ) && ram <= 200 )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );
		
		jobRequirement = "! ( ram>300)";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );
		
		jobRequirement = "!(ram = 300)";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );
		
		jobRequirement = "!( site == ucsd)";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );
		
		jobRequirement = "!( SO != bla)";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );
		
		jobRequirement = "!( os != linux)";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "not ( ram > 300 )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );
		
		jobRequirement = "not(ram>300)";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );
		
		jobRequirement = "not(ram = 300)";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );
		
		jobRequirement = "not( site == ucsd)";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );
		
		jobRequirement = "not( SO != bla)";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );
		
		jobRequirement = "not( os != linux)";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "( ( site == ucsd OR site == lsd ) AND ram <= 200 )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "( ( site = ucsd OR site = lsd ) AND ram <= 200 )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );

		jobRequirement = "( SO = bla )";
		matched = matcher.match( jobRequirement, machine1 );
		assertTrue( matched );
		
		jobRequirement = "(os == 1) || (ram >= 200)";
		matched = matcher.match( jobRequirement, machine1 );
		System.out.println( "test > " + jobRequirement + " matches " + machine1 +
		" ? ==> " + matched );
		assertTrue( matched );
		
		jobRequirement = "(os != 1) || (ram != 200)";
		matched = matcher.match( jobRequirement, machine1 );
		System.out.println( "test > " + jobRequirement + " matches " + machine1 +
		" ? ==> " + matched );
		assertTrue( matched );

		jobRequirement = "!(os = windows) or (site > 200)";
		matched = matcher.match( jobRequirement, machine1 );
		System.out.println( "test > " + jobRequirement + " matches " + machine1 +
		" ? ==> " + matched );
		assertTrue( matched );

		jobRequirement = "(mem = 50) || not(os = 2)";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 +
		" ? ==> " + matched );
		assertTrue( matched );

		jobRequirement = "(os != linux) OR (mem > windows)";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 +
		" ? ==> " + matched );
		assertTrue( matched );
		 
	}


	public void testBadMatches() {

		String jobRequirement = "!(site == lsd )";
		boolean matched = matcher.match( jobRequirement, machine1 );
		assertFalse( matched );

		jobRequirement = "( site == xpto && os = linux )";
		matched = matcher.match( jobRequirement, machine1 );
		assertFalse( matched );

		jobRequirement = "( site != xpto && os = windows )";
		matched = matcher.match( jobRequirement, machine1 );
		assertFalse( matched );

		jobRequirement = "( ram == 400 || os == windows )";
		matched = matcher.match( jobRequirement, machine1 );
		assertFalse( matched );

		jobRequirement = "( os == windows && ( site != ucsd ) )";
		matched = matcher.match( jobRequirement, machine1 );
		assertFalse( matched );

		jobRequirement = "( os == windows && ( site == noname ) )";
		matched = matcher.match( jobRequirement, machine1 );
		assertFalse( matched );

		jobRequirement = "( ram < 300 && site == noname )";
		matched = matcher.match( jobRequirement, machine1 );
		assertFalse( matched );

		jobRequirement = "( XPTO == bla )";
		matched = matcher.match( jobRequirement, machine1 );
		assertFalse( matched );

		jobRequirement = "( XPTO == bla ) && ( XPTO2 != ble )";
		matched = matcher.match( jobRequirement, machine1 );
		assertFalse( matched );

		jobRequirement = "( XPTO3 < 9876543210 )";
		matched = matcher.match( jobRequirement, machine1 );
		assertFalse( matched );
		
		jobRequirement = "(os == 1) && (ram >= 200)";
		matched = matcher.match( jobRequirement, machine1 );
		System.out.println( "test > " + jobRequirement + " matches " + machine1 +
		" ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "(site > 200) || (os == 50)";
		matched = matcher.match( jobRequirement, machine1 );
		System.out.println( "test > " + jobRequirement + " matches " + machine1 +
		" ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "(mem < 50) OR NoT(os != 20)";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 +
		" ? ==> " + matched );
		assertFalse( matched );

		jobRequirement = "!(os == linux) and (mem == windows)";
		matched = matcher.match( jobRequirement, machine3 );
		System.out.println( "test > " + jobRequirement + " matches " + machine3 +
		" ? ==> " + matched );
		assertFalse( matched );
	}


	public void testExceptionsMatches() {

		String jobRequirement;

		jobRequirement = "( ram >= linux && ( site == ucsd ) )";
		assertFalse( matcher.match( jobRequirement, machine1 ) );
	}

}
