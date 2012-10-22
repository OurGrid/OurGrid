///*
// * Copyright (C) 2008 Universidade Federal de Campina Grande
// *  
// * This file is part of OurGrid. 
// *
// * OurGrid is free software: you can redistribute it and/or modify it under the
// * terms of the GNU Lesser General Public License as published by the Free 
// * Software Foundation, either version 3 of the License, or (at your option) 
// * any later version. 
// * 
// * This program is distributed in the hope that it will be useful, but WITHOUT 
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
// * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
// * for more details. 
// * 
// * You should have received a copy of the GNU Lesser General Public License 
// * along with this program. If not, see <http://www.gnu.org/licenses/>.
// * 
// */
//package org.ourgrid.common.specification.semantic;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Stack;
//
//import org.ourgrid.common.specification.CompilerMessages;
//import org.ourgrid.common.specification.OurGridSpecificationConstants;
//import org.ourgrid.common.specification.peer.PeerSpecification;
//import org.ourgrid.common.specification.semantic.exception.SemanticException;
//import org.ourgrid.common.specification.syntactical.CommonSyntacticalAnalyzer;
//import org.ourgrid.common.specification.token.Token;
//import org.ourgrid.common.util.CommonUtils;
//
///**
// * This entity is the set of actions that the GRID grammar uses to build a
// * answer to the compilation of sources wrote in this language. Created on Jul
// * 8, 2004
// */
//public class GDFSemanticActions implements SemanticActions {
//
//	private static transient final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
//			.getLogger( GDFSemanticActions.class );
//
//	private Stack<String> stack;
//
//	private Token actualToken;
//
//	private int mode = CommonSyntacticalAnalyzer.MODE_NORMAL;
//
//	private LinkedList<PeerSpecification> peers = new LinkedList<PeerSpecification>();
//
//	private PeerSpecification actualPeer = null;
//
//	private Map<String,String> actualPeerAttributes = CommonUtils.createSerializableMap();
//
//	private List<PeerSpecification> result;
//
//
//	/**
//	 * The Constructor
//	 */
//	public GDFSemanticActions() {
//
//		this.stack = new Stack<String>();
//	}
//
//
//	/**
//	 * @see org.ourgrid.common.specification.semantic.SemanticActions#performAction(java.lang.String,
//	 *      org.ourgrid.common.specification.token.Token)
//	 */
//	public void performAction( String action, Token token ) throws SemanticException {
//
//		this.actualToken = token;
//		try {
//			Class semantic = Class.forName( this.getClass().getName() );
//			Method method = semantic.getMethod( action );
//			method.invoke( this );
//		} catch ( NoSuchMethodException nsmex ) {
//			throw new SemanticException( CompilerMessages.SEMANTIC_ACTION_NOT_FOUND, nsmex );
//		} catch ( ClassNotFoundException cnfex ) {
//			throw new SemanticException( CompilerMessages.SEMANTIC_CLASS_NOT_FOUND, cnfex );
//		} catch ( InvocationTargetException itex ) {
//			if ( itex.getCause() instanceof SemanticException ) {
//				throw (SemanticException) itex.getCause();
//			}
//			throw new SemanticException( CompilerMessages.SEMANTIC_FATAL_ERROR(), itex.getCause() );
//		} catch ( IllegalAccessException iaex ) {
//			throw new SemanticException( CompilerMessages.SEMANTIC_FATAL_ILLEGAL_ACCESS, iaex );
//		}
//	}
//
//
//	/**
//	 * @see org.ourgrid.common.specification.semantic.SemanticActions#getOperationalMode()
//	 */
//	public int getOperationalMode() {
//
//		return this.mode;
//	}
//
//
//	/**
//	 * @see org.ourgrid.common.specification.semantic.SemanticActions#getResult()
//	 */
//	public List<PeerSpecification> getResult() {
//
//		return this.result;
//	}
//
//
//	/**
//	 * This action: Sets the read line mode
//	 */
//	public void action1() {
//
//		this.mode = CommonSyntacticalAnalyzer.MODE_READLINE;
//	}
//
//
//	/**
//	 * This action: Puts the value string for a attribute at the top of the
//	 * stack.
//	 * 
//	 * @throws SemanticException
//	 */
//	public void action2() throws SemanticException {
//
//		String tempAttValue = actualToken.getSymbol();
//		if ( tempAttValue.equals( "" ) ) {
//			throw new SemanticException( CompilerMessages.SEMANTIC_EMPTY_ATTRIBUTE_VALUE( stack.pop(), actualToken
//					.getLine() ) );
//		}
//		this.stack.push( tempAttValue );
//	}
//
//
//	/**
//	 * This action: Puts the name string for a attribute at the top of the
//	 * stack.
//	 */
//	public void action3() {
//
//		this.stack.push( actualToken.getSymbol() );
//	}
//
//
//	/**
//	 * This action: Inserts the actual peer specification at the list of peers.
//	 */
//	public void action5() {
//
//		this.peers.addLast( actualPeer );
//		this.actualPeer = null;
//	}
//
//
//	/**
//	 * This action: Insert the actualPeer attributes at the map.
//	 */
//	public void action7() {
//
//		String value = stack.pop();
//		String name = stack.pop();
//		String realName = "";
//		if ( name.equalsIgnoreCase( OurGridSpecificationConstants.ATT_USERNAME ) )
//			realName = OurGridSpecificationConstants.ATT_USERNAME;
//		else if ( name.equalsIgnoreCase( PeerSpecification.ATT_LABEL ) )
//			realName = PeerSpecification.ATT_LABEL;
//		else if ( name.equalsIgnoreCase( OurGridSpecificationConstants.ATT_SERVERNAME ) )
//			realName = OurGridSpecificationConstants.ATT_SERVERNAME;
//		else
//			realName = name;
//		actualPeerAttributes.put( realName, value );
//	}
//
//
//	/**
//	 * This action: Sets the final result LIST object.
//	 */
//	public void action12() {
//
//		this.result = this.peers;
//	}
//
//
//	/**
//	 * This action: Mount the actualPeer and inserts it at the list of peers.
//	 */
//	public void action13() throws SemanticException {
//
//		String userName = actualPeerAttributes.remove( OurGridSpecificationConstants.ATT_USERNAME );
//		String serverName = actualPeerAttributes.remove( OurGridSpecificationConstants.ATT_SERVERNAME );
//		String label = actualPeerAttributes.remove( PeerSpecification.ATT_LABEL );
//
//		this.actualPeer = new PeerSpecification();
//		this.actualPeer.putAttribute( OurGridSpecificationConstants.ATT_USERNAME, userName );
//		this.actualPeer.putAttribute( OurGridSpecificationConstants.ATT_SERVERNAME, serverName );
//		if ( !(this.actualPeer.isValid()) ) {
//			throw new SemanticException( CompilerMessages.BAD_PEER_DEFINITION_USERNAME_OR_SERVER_MISSING );
//		}
//		if ( label != null ) {
//			this.actualPeer.putAttribute( PeerSpecification.ATT_LABEL, label );
//		}
//
//		Iterator<String> it = actualPeerAttributes.keySet().iterator();
//		while ( it.hasNext() ) {
//			String key = it.next();
//			LOG.info( "Attribute " + key + " cannot be used on a peer definition and is been ignored." );
//		}
//		this.actualPeerAttributes = CommonUtils.createSerializableMap();
//	}
//
//
//	/**
//	 * This action: Sets the mode to NORMAL
//	 */
//	public void action14() {
//
//		this.mode = CommonSyntacticalAnalyzer.MODE_NORMAL;
//	}
//
//}
