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

import org.ourgrid.common.exception.InvalidExpressionException;
import org.ourgrid.peer.business.controller.matcher.ExpressionTranslator;

/**
 * Test for expression translator class.
 */
public class ExpressionTranslatorTest extends TestCase {

	String minimizeExpression;

	String mgExpression;

	ExpressionTranslator translator;


	@Override
	protected void setUp() throws Exception {

		this.translator = new ExpressionTranslator();
	}


	public void testTranslateMGMinimize1() throws Exception {

		String[ ] mgExpression = { "os == linux", "os >= linux", "os <= linux", "os > linux", "os < linux" };
		String minimizeExpression = "a";
		for ( int i = 0; i < mgExpression.length; i++ ) {
			assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression[i] ) );
			assertEquals( mgExpression[i], translator.translateToBrokerExpressionForm( minimizeExpression ) );
		}

		String[ ] mgExpression1 = { "( os == linux )", "( os >= linux )", "( os <= linux )", "( os > linux )",
									"( os < linux )" };
		minimizeExpression = "(a)";
		for ( int i = 0; i < mgExpression.length; i++ ) {
			assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression1[i] ) );
			System.out.println( minimizeExpression );
			assertEquals( mgExpression1[i], translator.translateToBrokerExpressionForm( minimizeExpression ) );
		}
	}


	public void testTranslateMGMinimize2() throws Exception {

		String mgExpression = "os == linux || mem >= 500";
		String minimizeExpression = "a+b";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		mgExpression = "os == linux OR mem >= 500";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );

		mgExpression = "( os == linux ) OR ( mem >= 500 )";
		minimizeExpression = "(a)+(b)";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMinimize3() throws Exception {

		String mgExpression = "os == linux && num_procs < 5";
		String minimizeExpression = "a*b";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		mgExpression = "os == linux AND num_procs < 5";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );

		mgExpression = "( os == linux ) AND ( num_procs < 5 )";
		minimizeExpression = "(a)*(b)";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMinimize4() throws Exception {

		String mgExpression = "os == linux && num_procs < 5 || mem == 500 ";
		String minimizeExpression = "a*b+c";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		mgExpression = "os == linux AND num_procs < 5 OR mem == 500";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMinimize5() throws Exception {

		String mgExpression = "os == linux || num_procs < 5 AND mem == 500 ";
		String minimizeExpression = "a+b*c";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		mgExpression = "os == linux OR num_procs < 5 AND mem == 500";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMinimize6() throws Exception {

		String mgExpression = "( os == linux ) OR ( num_procs < 5 ) || ( mem == 500 )";
		String minimizeExpression = "(a)+(b)+(c)";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		mgExpression = "( os == linux ) OR ( num_procs < 5 ) OR ( mem == 500 )";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMinimize7() throws Exception {

		String mgExpression = "( ( ( os == linux ) AND ( num_procs < 5 ) ) || ( mem == 500 ) )";
		String minimizeExpression = "(((a)*(b))+(c))";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		mgExpression = "( ( ( os == linux ) AND ( num_procs < 5 ) ) OR ( mem == 500 ) )";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	// testes with not operator
	public void testTranslateMGMinimize8() throws Exception {

		String mgExpression = "NOT ( os == linux )";
		String minimizeExpression = "(a)'";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );

		mgExpression = "! ( os == linux )";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );
	}


	public void testTranslateMGMinimize9() throws Exception {

		String mgExpression = "NOT ( os == linux AND ( mem == 500 ) )";
		String minimizeExpression = "(a*(b))'";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );

		mgExpression = "NOT ( os == linux && mem == 500 )";
		minimizeExpression = "(a*b)'";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		mgExpression = "NOT ( os == linux AND mem == 500 )";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMinimize10() throws Exception {

		String mgExpression = "num_procs < 10 OR ! ( os == linux && ( mem == 500 ) ) ";
		String minimizeExpression = "a+(b*(c))'";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		mgExpression = "num_procs < 10 OR NOT ( os == linux AND ( mem == 500 ) )";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMinimize11() throws Exception {

		String mgExpression = "NOT ( num_procs < 10 OR ! ( os == linux && ( mem == 500 ) ) )";
		String minimizeExpression = "(a+(b*(c))')'";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		mgExpression = "NOT ( num_procs < 10 OR NOT ( os == linux AND ( mem == 500 ) ) )";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMinimize12() throws Exception {

		String mgExpression = "NOT num_procs < 10";
		String minimizeExpression = "a'";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMinimize13() throws Exception {

		String mgExpression = "NOT ( NOT num_procs < 10 )";
		String minimizeExpression = "(a')'";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMinimize14() throws Exception {

		String mgExpression = "( NOT num_procs < 10 )";
		String minimizeExpression = "(a')";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMinimize15() throws Exception {

		String mgExpression = "NOT c < d || NOT ( a == b OR NOT num_procs < 10 )";
		String minimizeExpression = "a'+(b+c')'";
		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );

		mgExpression = "NOT c < d OR NOT ( a == b OR NOT num_procs < 10 )";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMInimize16() throws Exception {

		String mgExpression = "os == linux AND os == linux";
		String minimizeExpression = "a*a";

		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateMGMInimize17() throws Exception {

		String mgExpression = "NOT os == linux AND os == linux";
		String minimizeExpression = "a'*a";

		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateBothWithMoreThan25DifferentExpressions() throws Exception {

		String mgExpression = "";
		String minimizeExpression = "";

		int init = 'a';
		for ( int k = 0; k < 26; k++ ) {
			mgExpression += "ram == " + k + "0 AND ";
			minimizeExpression += (char) (init + k) + "*";
		}
		init = 'A';
		int k = 0;
		for ( k = 0; k <= 9; k++ ) {
			mgExpression += "memory == " + k + "0 AND ";
			minimizeExpression += (char) (init + k) + "*";
		}
		mgExpression += "os == windows";
		minimizeExpression += (char) (init + k);

		assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}


	public void testTranslateBothWIthMoreThan46DifferentExpressions() throws Exception {

		String mgExpression = "";
		String minimizeExpression = "";

		int init = 'a';
		for ( int k = 0; k < 26; k++ ) {
			mgExpression += "ram == " + k + "0 AND ";
			minimizeExpression += (char) (init + k) + "*";
		}
		init = 'A';
		int k = 0;
		for ( k = 0; k < 27; k++ ) {
			mgExpression += "memory == " + k + "0 AND ";
			minimizeExpression += (char) (init + k) + "*";
		}
		try {
			assertEquals( minimizeExpression, translator.translateToMinimizeLibraryForm( mgExpression ) );
			fail( "An exception should be thrown here." );
		} catch ( Exception e ) {
			System.out.println( "Exception expected: " + e );
		}
	}


	public void testTranslateToMGWithouTranslateToMinimizeFirst() {

		String mgExpression = "NOT os == linux AND os == linux";
		String minimizeExpression = "a'*a";

		try {
			assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
			fail( "An exception should be thrown here." );
		} catch ( InvalidExpressionException ite ) {
			System.out.println( "Exception expected: " + ite );
		}
	}


	public void testTranslateToMGWithWrongExpression() throws Exception {

		String mgExpression = "NOT os == linux AND os == linux";
		translator.translateToMinimizeLibraryForm( mgExpression );
		try {
			// The original translation must return only one variable ("a")
			String minimizeExpression = "a'*b";
			translator.translateToBrokerExpressionForm( minimizeExpression );
			fail( "An exception should be thrown here." );
		} catch ( InvalidExpressionException ite ) {
			System.out.println( "Exception expected: " + ite );
		}
	}


	public void testEmptyExpression() throws InvalidExpressionException {

		assertEquals( "", translator.translateToMinimizeLibraryForm( "" ) );
		assertEquals( "", translator.translateToBrokerExpressionForm( "" ) );
	}


	public void testTheStrangeAndOperator() throws InvalidExpressionException {

		String mgExpression = "NOT os == linux AND os == windows";
		// bellow we get two variable expression ( a->os==linux and
		// b->os==windows)
		translator.translateToMinimizeLibraryForm( mgExpression );
		String minimizeExpression = "ab";
		mgExpression = "os == linux AND os == windows";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );

		minimizeExpression = "a'b";
		mgExpression = "NOT os == linux AND os == windows";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );

		minimizeExpression = "a(b)";
		mgExpression = "os == linux AND ( os == windows )";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );

		minimizeExpression = "a((b))";
		mgExpression = "os == linux AND ( ( os == windows ) )";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );

		minimizeExpression = "a'((b))";
		mgExpression = "NOT os == linux AND ( ( os == windows ) )";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );

		minimizeExpression = "(a)b";
		mgExpression = "( os == linux ) AND os == windows";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );

		minimizeExpression = "a*ba";
		mgExpression = "os == linux AND os == windows AND os == linux";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );

	}


	public void testNotAtMidle() throws InvalidExpressionException {

		String mgExpression = "NOT os == linux AND os == windows";
		// bellow we get two variable expression ( a->os==linux and
		// b->os==windows)
		translator.translateToMinimizeLibraryForm( mgExpression );

		String minimizeExpression = "ab'";
		mgExpression = "os == linux AND NOT os == windows";
		assertEquals( mgExpression, translator.translateToBrokerExpressionForm( minimizeExpression ) );
	}
}
