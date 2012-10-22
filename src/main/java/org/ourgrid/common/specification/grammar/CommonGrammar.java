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
package org.ourgrid.common.specification.grammar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import org.ourgrid.common.specification.IgnoreCaseComparator;

/**
 * From Project: Caymman(DSC/UFCG) Description: This class represents a LL(1)
 * grammar.
 * 
 * @version 1.0 Created on Sep 25, 2003
 *          ************************************************************
 * @version 2.0 Updated on May 23, 2004
 */

public class CommonGrammar implements Grammar {

	/* The struct that will contain all the rules of the grammar. */
	private LinkedHashMap<Integer,Rule> rules;

	/* The struct that will contain all the symbols of the grammar. */
	private TreeMap<String,Symbol> symbols;

	/* The initial symbol of this grammar */
	private Symbol initialSymbol;

	/* The syntatic table used to the grammar. */
	private Rule[ ][ ] syntacticTable;

	/*
	 * A stack used to check if will happen infinit loop at follows search
	 * 
	 * @see this.follow(Symbol)
	 */
	private Stack<Rule> recursiveFollows;


	/**
	 * @see org.ourgrid.common.specification.grammar.Grammar
	 */
	public CommonGrammar() {

		rules = new LinkedHashMap<Integer,Rule>();
		symbols = new TreeMap<String,Symbol>( new IgnoreCaseComparator() );
		initialSymbol = null;
		syntacticTable = null;
		recursiveFollows = new Stack<Rule>();
	}


	/**
	 * @see org.ourgrid.common.specification.grammar.Grammar#addSymbol(Symbol)
	 */
	public void addSymbol( Symbol symbol ) {

		symbols.put( symbol.getValue(), symbol );
		if ( (initialSymbol == null) && (symbol.isNonTerminal()) ) {
			initialSymbol = symbol;
		}
	}


	/**
	 * @see org.ourgrid.common.specification.grammar.Grammar#addRule(Rule)
	 */
	public void addRule( Rule rule ) {

		rules.put( new Integer( rule.getID() ), rule );
		syntacticTable = null; // Invalidating syntacticTable
	}


	/**
	 * @see org.ourgrid.common.specification.grammar.Grammar#getSymbol(java.lang.String)
	 */
	public Symbol getSymbol( String symbolName ) {

		return (symbols.get( symbolName ));
	}


	/**
	 * @see org.ourgrid.common.specification.grammar.Grammar#getRule(int)
	 */
	public Rule getRule( int id ) {

		return rules.get( new Integer( id ) );
	}


	/**
	 * @see org.ourgrid.common.specification.grammar.Grammar#getRule(Symbol,Symbol)
	 */
	public Rule getRule( Symbol stackTop, Symbol nextSymbol ) {

		return this.syntacticTable[stackTop.getCode()][nextSymbol.getCode()];
	}


	/**
	 * @see org.ourgrid.common.specification.grammar.Grammar#getInitialSymbol()
	 */
	public Symbol getInitialSymbol() {

		return this.initialSymbol;
	}


	/**
	 * @see org.ourgrid.common.specification.grammar.Grammar#getEndOfSourceSymbol()
	 */
	public Symbol getEndOfSourceSymbol() {

		return Symbol.EOF;
	}


	/**
	 * The code of the last terminal symbol in the grammar.
	 * 
	 * @return The code of the last terminal.
	 */
	public int lastTerminal() {

		int biggerTerminalCode = 0;
		Iterator<Symbol> symbolsIt = symbols.values().iterator();
		while ( symbolsIt.hasNext() ) {
			Symbol symbol = symbolsIt.next();
			if ( (symbol.isTerminal()) && (symbol.getCode() > biggerTerminalCode) )
				biggerTerminalCode = symbol.getCode();
		}
		return biggerTerminalCode;
	}


	/**
	 * The code of the last non terminal symbol in the grammar.
	 * 
	 * @return The code of the last non Terminal.
	 */
	public int lastNonTerminal() {

		int biggerNonTerminalCode = 0;
		Iterator<Symbol> symbolsIt = symbols.values().iterator();
		while ( symbolsIt.hasNext() ) {
			Symbol symbol = symbolsIt.next();
			if ( (symbol.isNonTerminal()) && (symbol.getCode() > biggerNonTerminalCode) )
				biggerNonTerminalCode = symbol.getCode();
		}
		return biggerNonTerminalCode;
	}


	/**
	 * Gets all the rules of the grammar.
	 * 
	 * @return The iterator with all the rules of the grammar.
	 */
	public Iterator<Rule> getRules() {

		return rules.values().iterator();
	}


	/**
	 * Loads the internal syntactical table using the rules and symbols
	 * inserted. ATTENTION: Do not try to compile using this grammar without
	 * before call this method!!! Generally it will be used after finish to read
	 * the grammar description file.
	 * 
	 * @see org.ourgrid.common.specification.grammar.io.GrammarReader
	 */
	public void loadSyntacticTable() {

		if ( syntacticTable != null ) {
			return;
		}
		firstCache = new HashMap<Symbol,Set<Symbol>>();
		followCache = new HashMap<Symbol,Set<Symbol>>();
		syntacticTable = new Rule[ lastNonTerminal() + 1 ][ lastTerminal() + 1 ];
		for ( int i = 0; i < syntacticTable.length; i++ ) {
			for ( int j = 0; j < syntacticTable[i].length; j++ ) {
				syntacticTable[i][j] = null;
			}
		}

		Iterator<Rule> rulesIt = getRules();
		while ( rulesIt.hasNext() ) {
			Rule rule = rulesIt.next();
			Symbol head = rule.getHead();
			Iterator<Symbol> firstIt = first( rule.getBody() ).iterator();
			while ( firstIt.hasNext() ) {
				Symbol first = firstIt.next();
				if ( !first.isSemanticAction() ) {
					if ( !first.equals( Symbol.EMPTY ) ) {
						syntacticTable[head.getCode()][first.getCode()] = rule;
					} else {
						Iterator<Symbol> followIt = follow( head ).iterator();
						while ( followIt.hasNext() ) {
							Symbol follow = followIt.next();
							if ( !follow.equals( Symbol.EMPTY ) ) {
								if ( syntacticTable[head.getCode()][follow.getCode()] == null ) {
									syntacticTable[head.getCode()][follow.getCode()] = rule;
								}
							} else {
								syntacticTable[head.getCode()][Symbol.EOF.getCode()] = rule;
							}
						}
					}
				}
			}
		}

	}

	/*
	 * A map to caches follows of nonTerminals symbols
	 */
	private HashMap<Symbol,Set<Symbol>> followCache = new HashMap<Symbol,Set<Symbol>>();


	/*
	 * Search the follow symbols of a given symbol @param symbol the symbol to
	 * search the follow set @return a set with all the follows to the given
	 * symbol
	 */
	private Set<Symbol> follow( Symbol symbol ) {

		Set<Symbol> set = new HashSet<Symbol>();
		if ( symbol.isNonTerminal() ) {
			if ( followCache.get( symbol ) != null ) {
				return followCache.get( symbol );
			}
			if ( symbol.equals( getInitialSymbol() ) ) {
				set.add( Symbol.EMPTY );
			}
			Iterator<Rule> rulesIt = getRules();
			while ( rulesIt.hasNext() ) {
				Rule rule = rulesIt.next();
				for ( int i = 0; i < rule.getBody().length; i++ ) {
					if ( rule.getBody()[i].equals( symbol ) ) { // rule produces
																// symbol
						Symbol nextSymbol = getNextTerminalOrNonTerminal( rule, i );
						if ( (nextSymbol == null) && (!rule.getHead().equals( symbol ))
								&& (!recursiveFollows.contains( rule )) ) {
							// symbol is in the end of the rule and is different
							// from head
							recursiveFollows.push( rule );
							set.addAll( follow( rule.getHead() ) );
							recursiveFollows.pop();
						} else if ( nextSymbol != null ) {
							Iterator<Symbol> firstIt = first( nextSymbol ).iterator();
							while ( firstIt.hasNext() ) {
								Symbol first = firstIt.next();
								if ( !first.equals( Symbol.EMPTY ) ) {
									set.add( first );
								} else {
									set.addAll( follow( nextSymbol ) );
								}
							}
						}
					}
				}
			}
			followCache.put( symbol, set );
		}
		return set;
	}


	/*
	 * Will search for all the terminal and non terminal symbols inside the
	 * given rule body considering a pointer to begin. @param rule the rule
	 * where the symbols will be searched @param offset the actual position at
	 * the body that will begin the search process @return the symbol founded of
	 * null if any other terminal or non-terminal was found.
	 */
	private Symbol getNextTerminalOrNonTerminal( Rule rule, int offset ) {

		for ( int i = offset + 1; i < rule.getBody().length; i++ ) {
			if ( (rule.getBody()[i].isTerminal()) || (rule.getBody()[i].isNonTerminal()) ) {
				return rule.getBody()[i];
			}
		}
		return null;
	}

	/*
	 * A map to caches firsts of nonTerminals symbols
	 */
	private HashMap<Symbol,Set<Symbol>> firstCache = new HashMap<Symbol,Set<Symbol>>();


	/*
	 * Search the first set of symbols of a given symbol @param symbol the
	 * symbol to search the first set @return a set with all the firsts to the
	 * given symbol
	 */
	private Set<Symbol> first( Symbol symbol ) {

		Set<Symbol> set = new HashSet<Symbol>();
		if ( symbol.isTerminal() ) {
			set.add( symbol );
		} else if ( symbol.isNonTerminal() ) {
			if ( firstCache.get( symbol ) != null ) {
				return firstCache.get( symbol );
			}
			Iterator<Rule> rulesIt = getRules();
			while ( rulesIt.hasNext() ) {
				Rule rule = rulesIt.next();
				if ( rule.getHead().equals( symbol ) ) {
					set.addAll( first( rule.getBody() ) );
				}
			}
			firstCache.put( symbol, set );
		} else {
			set.add( Symbol.EMPTY );
		}
		return set;
	}


	/**
	 * Searchs the firsts set of the symbols into the given array
	 * 
	 * @param symbols the symbols to be searched the first symbols
	 * @return a set with the first symbols for the given arrays's symbols
	 */
	private Set<Symbol> first( Symbol[ ] symbols ) {

		Set<Symbol> set = new HashSet<Symbol>();
		boolean hasEmpty = true;
		for ( int i = 0; (hasEmpty) && (i < symbols.length); i++ ) {
			hasEmpty = false;
			Symbol nextSymbol = symbols[i];
			Iterator<Symbol> firstIt = first( nextSymbol ).iterator();
			while ( firstIt.hasNext() ) {
				Symbol first = firstIt.next();
				if ( !first.equals( Symbol.EMPTY ) ) {
					set.add( first );
				} else {
					hasEmpty = true;
				}
			}
		}
		if ( hasEmpty ) {
			set.add( Symbol.EMPTY );
		}
		return set;
	}

}
