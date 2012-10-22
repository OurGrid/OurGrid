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
import java.util.StringTokenizer;

import org.ourgrid.common.exception.TokenErrorException;
import org.ourgrid.common.specification.main.JDLTagsPublisher;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.common.util.StringUtil;

import condor.classad.ClassAd;
import condor.classad.ClassAdParser;
import condor.classad.Expr;
import condor.classad.RecordExpr;

/**
 * The Matcher is responsible for evaluate an expression (i.e, given a set of
 * attributes, verify if the expression matches with them)
 */

public class MatcherImpl implements Matcher {

	/** String Tokenizer (delimited by spaces) of the expression. */
	private StringTokenizer exprTokens;

	/** The current token of the expression. */
	private String currentExprToken = "";

	/** The expression to be evaluated. */
	private String expression;

	/** The attributes used to make a match with. */
	private Map<String,String> attributesMap;


	/**
	 * Sets the currentExprToken to be the next token of the expression
	 */
	private void getToken() {

		if ( exprTokens.hasMoreTokens() ) {
			currentExprToken = exprTokens.nextToken();
		} else {
			currentExprToken = "";
		}
	}


	/**
	 * Verifies if an expression matches with a set of attributes, in other
	 * words, evaluate an expression.
	 * 
	 * @param jobRequirement
	 * @param machineAtt
	 * @return <code>true</code> if the expression matches with the attributes
	 *         and <code>false</code> otherwise.
	 * @throws TokenErrorException If the expression is not valid
	 */
	public boolean match( String jobRequirement, Map<String,String> machineAtt ) {

		if (jobRequirement != null) {
			jobRequirement = jobRequirement.toLowerCase();
		}

		this.expression = StringUtil.insertSpaces( jobRequirement );
		
		Map<String, String> lowerAttributes = CommonUtils.createSerializableMap();
		for (Map.Entry<String, String> entry : machineAtt.entrySet()) {
			lowerAttributes.put(entry.getKey().toLowerCase(), entry.getValue().toLowerCase());
		}
		
		this.attributesMap = lowerAttributes;

		if ( this.expression == null ) {
			return true;
		}

		int result = ATT_TRUE;

		exprTokens = new StringTokenizer( expression );

		try {
			if ( exprTokens.countTokens() != 0 ) {
				getToken();
				result = evalExpr();
				if ( !currentExprToken.equals( "" ) ) {
					throw new TokenErrorException( expression, currentExprToken );
				}
			}
		} catch ( TokenErrorException e ) {
			return false;
		}

		return (result == ATT_FALSE || result == ATT_UNDEFINED) ? false : true;
	}


	private int threeValuedOR( int value1, int value2 ) {

		// If at least one sentence is TRUE, the result is TRUE
		if ( value1 == ATT_TRUE || value2 == ATT_TRUE ) {
			return ATT_TRUE;
		} else if ( value1 == ATT_FALSE && value2 == ATT_FALSE ) {
			// If both sentences are FALSE, the result is FALSE
			return ATT_FALSE;
		} else {
			// otherwise
			return ATT_UNDEFINED;
		}
	}


	private int threeValuedAND( int value1, int value2 ) {

		// If at least one sentence is FALSE, the result is FALSE
		if ( value1 == ATT_FALSE || value2 == ATT_FALSE ) {
			return ATT_FALSE;
		} else if ( value1 == ATT_TRUE && value2 == ATT_TRUE ) {
			// If both sentences are TRUE, the result is true
			return ATT_TRUE;
		} else {
			// otherwise
			return ATT_UNDEFINED;
		}
	}


	/**
	 * Evaluates the expression, making a match between the expression and the
	 * attributes
	 * 
	 * @return <code>true</code> if the result of the matche is true
	 *         <code>false</code> otherwise.
	 * @throws TokenErrorException If the expression is not valid
	 */
	private int evalExpr() throws TokenErrorException {

		int result = evalTerm();

		while ( !currentExprToken.equals( "" )
				&& (currentExprToken.equals( "||" ) || currentExprToken.equalsIgnoreCase( "OR" )) ) {
			getToken();
			int result2 = evalTerm();
			result = threeValuedOR( result, result2 );
		}

		return result;
	}


	/**
	 * Evaluates a term of the expression. It is used by the evalExpr to make
	 * the match
	 * 
	 * @return <code>true</code> if the term evaluate results in true and
	 *         <code>false</code> otherwise
	 * @throws TokenErrorException if the term is not valid
	 */
	private int evalTerm() throws TokenErrorException {

		int result = evalFactor();

		while ( !currentExprToken.equals( "" )
				&& (currentExprToken.equals( "&&" ) || currentExprToken.equalsIgnoreCase( "AND" )) ) {
			getToken();
			int result2 = evalFactor();
			result = threeValuedAND( result, result2 );
		}
		return result;
	}


	/**
	 * Evaluates a factor of the expression. It is used by the evalTerm to make
	 * the match
	 * 
	 * @return <code>true</code> if the factor evaluate results in true and
	 *         <code>false</code> otherwise
	 * @throws TokenErrorException if the factor is not valid
	 */
	private int evalFactor() throws TokenErrorException {

		int result;
		boolean negate = false;

		if ( currentExprToken.equals( "!" ) || currentExprToken.equalsIgnoreCase( "NOT" ) ) {
			getToken();
			negate = true;
		}

		if ( currentExprToken.equals( "(" ) ) {
			getToken();
			result = evalExpr();
			if ( !currentExprToken.equals( ")" ) ) {
				throw new TokenErrorException( expression, currentExprToken );
			}
		} else {
			result = ATT_UNDEFINED;
			if ( !currentExprToken.equals( "" ) ) {
				String attName = currentExprToken;
				getToken();
				String operator = currentExprToken;
				getToken();
				String attValue = currentExprToken;
				String machineAttValue = this.attributesMap.get( attName );

				try {
					if ( machineAttValue != null )
						if ( operator.equals( "==" ) || operator.equals( "=" ) ) {
							result = executeEqualsOperation( machineAttValue, attValue );
						} else if ( operator.equals( "!=" ) ) {
							result = executeNotEqualsOperation( machineAttValue, attValue );
						} else if ( operator.equals( ">" ) ) {
							result = executeGreaterThenOperation( machineAttValue, attValue );
						} else if ( operator.equals( "<" ) ) {
							result = executeLessThenOperation( machineAttValue, attValue );
						} else if ( operator.equals( ">=" ) ) {
							result = executeGreaterOrEqualsThenOperation( machineAttValue, attValue );
						} else if ( operator.equals( "<=" ) ) {
							result = executeLessOrEqualsThenOperation( machineAttValue, attValue );
						} else {
							throw new TokenErrorException( expression, "Operator \"" + operator + "\" cannot be used." );
						}
				} catch (TokenErrorException tee) {
					result = ATT_FALSE;
				}
			}
		}
		getToken();

		if ( result == ATT_UNDEFINED ) {
			return ATT_UNDEFINED;
		} else if ( result == ATT_TRUE ) {
			return (negate ? ATT_FALSE : ATT_TRUE);
		} else
			// ATT_FALSE
			return (negate ? ATT_TRUE : ATT_FALSE);
	}


	/**
	 * Executes the operation ">" at two string arguments, but before, it tries
	 * to convert them to integer values and throws the exception if it could
	 * not be made.
	 * 
	 * @param machineAttValue The value to the attribute to be analyzed at the
	 *        machine.
	 * @param attValue The value to the attribute to be analyzed at the
	 *        expression.
	 * @return <code>true</code> if the first attribute is greater then the
	 *         second.
	 * @throws TokenErrorException If the attributes could not be converted to
	 *         integer values.
	 */
	private int executeGreaterThenOperation( String machineAttValue, String attValue ) throws TokenErrorException {

		if ( machineAttValue == null )
			return ATT_UNDEFINED;
		
		int attValueInt = testValueIsNumber( attValue, ">" );
		int machineAttValueInt = testValueIsNumber( machineAttValue, ">" );
		
		return (machineAttValueInt > attValueInt ? ATT_TRUE : ATT_FALSE);
	}


	/**
	 * Executes the operation ">=" at two string arguments, but before, it tries
	 * to convert them to integer values and throws the exception if it could
	 * not be made.
	 * 
	 * @param machineAttValue The value to the attribute to be analyzed at the
	 *        machine.
	 * @param attValue The value to the attribute to be analyzed at the
	 *        expression.
	 * @return <code>true</code> if the first attribute is greater or equals
	 *         then the second.
	 * @throws TokenErrorException If the attributes could not be converted to
	 *         integer values.
	 */
	private int executeGreaterOrEqualsThenOperation( String machineAttValue, String attValue )
		throws TokenErrorException {

		if ( machineAttValue == null )
			return ATT_UNDEFINED;

		int attValueInt = testValueIsNumber( attValue, ">=" );
		int machineAttValueInt = testValueIsNumber( machineAttValue, ">=" );

		return (machineAttValueInt >= attValueInt ? ATT_TRUE : ATT_FALSE);
	}


	/**
	 * Executes the operation "<" at two string arguments, but before, it tries
	 * to convert them to integer values and throws the exception if it could
	 * not be made.
	 * 
	 * @param machineAttValue The value to the attribute to be analyzed at the
	 *        machine.
	 * @param attValue The value to the attribute to be analyzed at the
	 *        expression.
	 * @return <code>true</code> if the first attribute is less then the
	 *         second.
	 * @throws TokenErrorException If the attributes could not be converted to
	 *         integer values.
	 */
	private int executeLessThenOperation( String machineAttValue, String attValue ) throws TokenErrorException {

		if ( machineAttValue == null )
			return ATT_UNDEFINED;

		int attValueInt = testValueIsNumber( attValue, "<" );
		int machineAttValueInt = testValueIsNumber( machineAttValue, "<" );

		return (machineAttValueInt < attValueInt ? ATT_TRUE : ATT_FALSE);
	}


	/**
	 * Executes the operation "<=" at two string arguments, but before, it
	 * tries to convert them to integer values and throws the exception if it
	 * could not be made.
	 * 
	 * @param machineAttValue The value to the attribute to be analyzed at the
	 *        machine.
	 * @param attValue The value to the attribute to be analyzed at the
	 *        expression.
	 * @return <code>true</code> if the first attribute is less or equals then
	 *         the second.
	 * @throws TokenErrorException If the attributes could not be converted to
	 *         integer values.
	 */
	private int executeLessOrEqualsThenOperation( String machineAttValue, String attValue ) throws TokenErrorException {

		if ( machineAttValue == null )
			return ATT_UNDEFINED;

		int attValueInt = testValueIsNumber( attValue, "<=" );
		int machineAttValueInt = testValueIsNumber( machineAttValue, "<=" );

		return (machineAttValueInt <= attValueInt ? ATT_TRUE : ATT_FALSE);
	}


	/**
	 * Executes the operation equals at the two string arguments.
	 * 
	 * @param machineAttValue The value to the attribute to be analyzed at the
	 *        machine.
	 * @param attValue The value to the attribute to be analyzed at the
	 *        expression.
	 * @return <code>true</code> if the first attribute is equals to the
	 *         second. <code>false</code> if the the attributes are different
	 *         or if the machineAttValue is null - it means that the attribute
	 *         in question does not exists in machine.
	 */
	private int executeEqualsOperation( String machineAttValue, String attValue ) {

		if ( machineAttValue == null )
			return ATT_UNDEFINED;
		return (machineAttValue.equalsIgnoreCase( attValue ) ? ATT_TRUE : ATT_FALSE);
	}


	/**
	 * Executes the operation not equals at the two string arguments.
	 * 
	 * @param machineAttValue The value to the attribute to be analyzed at the
	 *        machine.
	 * @param attValue The value to the attribute to be analyzed at the
	 *        expression.
	 * @return <code>true</code> if the first attribute is not equals to the
	 *         second or if the machineAttValue is null - it means that the
	 *         attribute in question does not exists in machine.
	 *         <code>false</code> if the attributes are equals.
	 */
	private int executeNotEqualsOperation( String machineAttValue, String attValue ) {

		if ( machineAttValue == null )
			return ATT_UNDEFINED;
		return (machineAttValue.equalsIgnoreCase( attValue ) ? ATT_FALSE : ATT_TRUE);
	}


	/**
	 * Will try to return the integer value for a string attribute value.
	 * 
	 * @param attValue The string attribute to be converted to integer value.
	 * @param operator The symbol of the operator that will operate at the
	 *        attribute. It is used only to throws a better exception message if
	 *        it happens.
	 * @return The integer value for the string attribute.
	 * @throws TokenErrorException If the string value could not be converted to
	 *         a integer value.
	 */
	private int testValueIsNumber( String attValue, String operator ) throws TokenErrorException {

		int toReturn;
		try {
			toReturn = Integer.parseInt( attValue );
		} catch ( NumberFormatException nfex ) {
			throw new TokenErrorException( expression, "Value \"" + attValue
					+ "\" is not appliable for the operator \"" + operator + "\"" );
		}
		return toReturn;
	}
 
	/**
	 * {@inheritDoc}
	 */
	public int match(String jdlExpression, String machineClassAd) {
		assert jdlExpression != null: "JDL Expression must not be null";
		assert machineClassAd != null: "Machine ClassAd must not be null";
		assert !(jdlExpression.length() == 0): "JDL Expression must not be empty";
		assert !(machineClassAd.length() == 0): "Machine ClassAd must not be empty";
		
		Expr jdl = new ClassAdParser(jdlExpression).parse();
		RecordExpr classAd = JDLTagsPublisher.buildExprWithTagsToPublish( machineClassAd );
		
		int[] result = ClassAd.match(jdl, classAd);
		return result == null? -1 : result[0];
	}
}