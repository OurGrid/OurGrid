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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.ourgrid.common.exception.InvalidExpressionException;
import org.ourgrid.common.exception.TooManyExpressionsException;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.common.util.StringUtil;

/**
 * This class translates the logical expression sintax used at requirements
 * field at JDF to Minimize library form and vice-versa.
 */
public class ExpressionTranslator {

	/**
	 * Initial expression key.
	 */
	public final int INITIAL_KEY = 'a';

	/**
	 * Map where the variables will be held.
	 */
	private Map<String,String> variablesMap;

	/**
	 * The key index.
	 */
	private char keyIndex;

	public ExpressionTranslator() { }

	/**
	 * Translates an expression in Broker JDF sintax compliance to Minimize
	 * library sintax.
	 * 
	 * @param brokerLogicalExpression An logical expression in Broker JDF sintax
	 * @return An equivalent expression in Minimize library sintax
	 */
	// TOO BIG .. REFACTOR
	public String translateToMinimizeLibraryForm( String brokerLogicalExpression ) throws InvalidExpressionException {

		// Initiating here forces the user to first call this method
		this.variablesMap = CommonUtils.createSerializableMap();
		this.keyIndex = INITIAL_KEY;

		List<Integer> parenthesisList = new LinkedList<Integer>();
		StringBuffer returnedExpression = new StringBuffer();
		StringTokenizer tokenizer = new StringTokenizer( brokerLogicalExpression, " " );

		while ( tokenizer.hasMoreTokens() ) {
			String token = tokenizer.nextToken();

			if ( isANDOperator( token ) ) {
				returnedExpression.append( "*" );

			} else if ( isOROperator( token ) ) {
				returnedExpression.append( "+" );

			} else if ( isNOTOperator( token ) ) {
				parenthesisList.add( Integer.valueOf( 0 ) );
				continue;

			} else if ( token.equals( "(" ) ) {
				returnedExpression.append( token );
				parenthesisList = incrementedParenthesisList( parenthesisList );

			} else if ( token.equals( ")" ) ) {
				returnedExpression.append( token );
				parenthesisList = decrementParenthesisList( parenthesisList );

			} else { // Is an expression
				try {
					token += tokenizer.nextToken() + tokenizer.nextToken();
				} catch ( NoSuchElementException e ) {
					throw new InvalidExpressionException( "Invalid expression." );
				}
				String key = getKeyByValue( variablesMap, token );
				if ( key == null ) {
					key = generateAKey() + "";
					variablesMap.put( key, token );
				}
				returnedExpression.append( key );
			}

			if ( itIsTimeToPutTheNotOperator( parenthesisList ) ) {
				returnedExpression.append( "'" );
			}
		}
		return returnedExpression.toString();
	}


	/**
	 * Returns the variable key by its value.
	 * 
	 * @param map The Map where the keys and variables are held.
	 * @param value The variable value.
	 * @return The key with the variable exists, <code>null</code> otherwise.
	 */
	private String getKeyByValue( Map<String,String> map, String value ) {

		Iterator<Map.Entry<String,String>> iterator = map.entrySet().iterator();
		while ( iterator.hasNext() ) {
			Map.Entry<String,String> entry = iterator.next();
			if ( entry.getValue().equals( value ) ) {
				return entry.getKey();
			}
		}
		return null;
	}


	/**
	 * Translates a logical expression written in Minimize syntax to the
	 * Broker's syntax
	 * 
	 * @param minimizeLogicaExpression The expression in Minimize's syntax.
	 * @return A logical expression in Broker's syntax.
	 * @throws InvalidExpressionException Case the translation in impossible.
	 */
	// TOO BIG .. REFACTOR
	public String translateToBrokerExpressionForm( String minimizeLogicaExpression ) throws InvalidExpressionException {

		// When the user tryies to converts to MG form and not convert to
		// minimize first.
		if ( variablesMap == null ) {
			throw new InvalidExpressionException( "It is necessary translate to Minimize form first" );
		}

		StringBuffer returnedExpression = new StringBuffer();
		List<Integer> notControlList = new LinkedList<Integer>();
		// iterates from end to begin to easy the treatment with not (')
		// operator.
		for ( int index = minimizeLogicaExpression.length() - 1; index >= 0; index-- ) {
			String character = minimizeLogicaExpression.charAt( index ) + "";

			if ( character.equals( " " ) ) {
				continue;

			} else if ( character.equals( "'" ) ) {
				notControlList.add( Integer.valueOf( 0 ) );
				continue;

			} else if ( character.equals( "(" ) ) {
				notControlList = decrementParenthesisList( notControlList );
				returnedExpression.insert( 0, " " + character );

			} else if ( character.equals( ")" ) ) {
				notControlList = incrementedParenthesisList( notControlList );
				returnedExpression.insert( 0, " " + character );

			} else if ( character.equals( "*" ) ) {
				returnedExpression.insert( 0, " AND" );

			} else if ( character.equals( "+" ) ) {
				returnedExpression.insert( 0, " OR" );

			} else {
				String expression = this.variablesMap.get( character );
				if ( expression == null ) {
					throw new InvalidExpressionException( "Invalid variables in minimized expression" );
				}
				returnedExpression.insert( 0, " " + StringUtil.insertSpaces( expression ) );
			}

			if ( itIsTimeToPutTheNotOperator( notControlList ) ) {
				// Only to remove the space before NOT operator if it comes at
				// first.
				returnedExpression.insert( 0, " NOT" );
			}

			// The strange AND case.
			if ( itIsTimeToPutAndOperator( index, minimizeLogicaExpression ) ) {
				returnedExpression.insert( 0, " AND" );
			}
		}
		return returnedExpression.delete( 0, 1 ).toString();
	}


	/**
	 * Checks if it is necessary put the AND operator.
	 * 
	 * @param index Where the AND should be put.
	 * @param minimizeLogicaExpression The expression where the AND operator
	 *        should be put.
	 * @return <code>true</code> case AND operator is necessary,
	 *         <code>false</code> otherwise.
	 */
	private boolean itIsTimeToPutAndOperator( int index, String minimizeLogicaExpression ) {

		char charNow = minimizeLogicaExpression.charAt( index );
		if ( ((charNow >= 'a' && charNow <= 'z') || (charNow >= 'A' && charNow <= 'Z') || charNow == '(') && index > 0 ) {
			int indexBefore = index - 1;
			char charBefore = minimizeLogicaExpression.charAt( indexBefore );
			return ((charBefore != '(') && charBefore != '*' && charBefore != '+' && charBefore != ' ');
		}
		return false;
	}


	/**
	 * Generates a new key for a variable.
	 * 
	 * @return A new key - a char.
	 * @throws TooManyExpressionsException As the Minimize Library works only
	 *         with alphabetical characters, and we use this library to get DNF,
	 *         we are restricted to use only 52 characters and, eventually, 52
	 *         different expressions at JDF's requirements.
	 */
	private char generateAKey() {

		if ( keyIndex > 'z' ) {
			keyIndex = 'A';
		} else if ( keyIndex == 'Z' + 1 ) {
			throw new TooManyExpressionsException();
		}
		return (keyIndex++);
	}


	/**
	 * Checks if <code>operator</code> is a NOT operator.
	 * 
	 * @param operator An operator.
	 * @return <code>true</code> case <code>operator</code> is equals to '!'
	 *         or 'NOT', <code>false</code> otherwise.
	 */
	private boolean isNOTOperator( String operator ) {

		return operator.equals( "!" ) || operator.equals( "NOT" );
	}


	/**
	 * Checks if <code>operator</code> is an AND operator.
	 * 
	 * @param operator An operator.
	 * @return <code>true</code> case <code>operator</code> is equals to
	 *         AND!' or '&&', <code>false</code> otherwise.
	 */
	private boolean isANDOperator( String operator ) {

		return operator.equals( "AND" ) || operator.equals( "&&" );
	}


	/**
	 * Checks if <code>operator</code> is a OR operator.
	 * 
	 * @param operator An operator.
	 * @return <code>true</code> case <code>operator</code> is equals to
	 *         'OR' or '||', <code>false</code> otherwise.
	 */
	private boolean isOROperator( String operator ) {

		return operator.equals( "OR" ) || operator.equals( "||" );
	}


	/**
	 * Increases all members of parenthesis list by one.
	 * 
	 * @param parenthesisList A List of Integers.
	 * @return The List with all members increased by one.
	 */
	private List<Integer> incrementedParenthesisList( List<Integer> parenthesisList ) {

		List<Integer> newParentList = new LinkedList<Integer>();
		Iterator<Integer> it = parenthesisList.listIterator();
		while ( it.hasNext() ) {
			Integer integer = it.next();
			newParentList.add( new Integer( integer.intValue() + 1 ) );
		}
		return newParentList;
	}


	/**
	 * Decreases all members of parenthesis list by one.
	 * 
	 * @param parenthesisList A List of Integers.
	 * @return The List with all members decreased by one.
	 */
	private List<Integer> decrementParenthesisList( List<Integer> parenthesisList ) {

		List<Integer> newParentList = new LinkedList<Integer>();
		Iterator<Integer> it = parenthesisList.listIterator();
		while ( it.hasNext() ) {
			Integer integer = it.next();
			newParentList.add( Integer.valueOf(integer.intValue() - 1 ) );
		}
		return newParentList;
	}


	/**
	 * Checks if it is necessary to put a NOT operator.
	 * 
	 * @param parenthesisList A List of Integers that has the notification if a
	 *        NOT operator is necessary or not.
	 * @return <code>true</code> case there's any Integer on the List equals
	 *         to zero, <code>false</code> otherwise.
	 */
	private boolean itIsTimeToPutTheNotOperator( List<Integer> parenthesisList ) {

		Iterator<Integer> it = parenthesisList.listIterator();
		while ( it.hasNext() ) {
			Integer integer = it.next();
			if ( integer.intValue() == 0 ) {
				it.remove();
				return true;
			}
		}
		return false;
	}

}
