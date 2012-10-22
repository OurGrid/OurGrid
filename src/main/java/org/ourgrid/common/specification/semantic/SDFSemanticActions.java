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
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Stack;
//
//import org.ourgrid.common.specification.CompilerMessages;
//import org.ourgrid.common.specification.semantic.exception.SemanticException;
//import org.ourgrid.common.specification.syntactical.CommonSyntacticalAnalyzer;
//import org.ourgrid.common.specification.token.Token;
//import org.ourgrid.common.specification.worker.WorkerSpecification;
//import org.ourgrid.common.util.CommonUtils;
//
///**
// * This entity is the set of actions that the GRID grammar uses to build a
// * answer to the compilation of sources wrote in this language.
// */
//public class SDFSemanticActions implements SemanticActions {
//
//	private Stack<String> stack;
//
//	private Token actualToken;
//
//	private int mode = CommonSyntacticalAnalyzer.MODE_NORMAL;
//
//	private Map<String,String> defaultAttrib = null;
//
//	private boolean defaults;
//
//	private LinkedList<WorkerSpecification> workers = new LinkedList<WorkerSpecification>();
//
//	private WorkerSpecification actualWorker = null;
//
//	private List<WorkerSpecification> result;
//
//	private Map<String,String> defaultAnnotations;
//
//
//	/**
//	 * The Constructor
//	 */
//	public SDFSemanticActions() {
//
//		this.stack = new Stack<String>();
//	}
//
//
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
//	public int getOperationalMode() {
//
//		return this.mode;
//	}
//
//
//	public List<WorkerSpecification> getResult() {
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
//	 * This action: Inserts a new attribute at the actual worker specification
//	 * or at the default map depending of the type of the attribute (worker or
//	 * default).
//	 */
//	public void action4() {
//
//		String attValue = stack.pop();
//		String attName = stack.pop();
//		if ( defaults == false ) {
//			this.actualWorker.putAttribute( attName, attValue );
//		} else {
//			this.defaultAttrib.put( attName, attValue );
//		}
//	}
//
//
//	/**
//	 * This action: Initializes a new WorkerSpec as the actual Worker.
//	 */
//	public void action6() {
//
//		this.actualWorker = new WorkerSpecification();
//	}
//
//
//	/**
//	 * This action: Closes the actual Worker specification and inserts it at
//	 * worker list. The close process includes the insertion of the default
//	 * attributes at the actual Worker specification where each default
//	 * attribute is only inserted if the entry for a value does not exists.
//	 */
//	public void action8() throws SemanticException {
//		
//		for(String defAttName : defaultAttrib.keySet()) {
//			if ( !actualWorker.hasAttribute( defAttName ) ) {
//				actualWorker.putAttribute( defAttName, defaultAttrib.get( defAttName ) );
//			}
//		}
//
//		this.actualWorker.getAnnotations().putAll(defaultAnnotations);
//		
//		if ( actualWorker.isValid() ) {
//			this.workers.add( actualWorker );
//			this.actualWorker = null;
//		} else {
//			throw new SemanticException( CompilerMessages.BAD_WORKER_DEFINITION( actualWorker.getURL() ) );
//		}
//	}
//
//
//	/**
//	 * This action: Prepares the environment to read and indicates the existence of defaults attributes or annotations. 
//	 */
//	public void action9() {
//
//		this.defaultAttrib = CommonUtils.createSerializableMap();
//		this.defaultAnnotations = CommonUtils.createSerializableMap();
//		this.defaults = true;
//	}
//
//
//	/**
//	 * This action: Finishes the default attributes reading process.
//	 */
//	public void action10() {
//
//		this.defaults = false;
//	}
//
//
//	/**
//	 * This action: Sets the final result LIST object.
//	 */
//	public void action12() {
//
//		this.result = this.workers;
//	}
//
//	/**
//	 * This action: Sets the mode to NORMAL
//	 */
//	public void action14() {
//
//		this.mode = CommonSyntacticalAnalyzer.MODE_NORMAL;
//	}
//
//	
//	/**
//	 * This action: Inserts a new attribute at the actual worker specification
//	 * or at the default map depending of the type of the attribute (worker or
//	 * default).
//	 */
//	public void action15() {
//
//		String attValue = stack.pop();
//		String attName = stack.pop();
//		if ( defaults ) {
//			this.defaultAnnotations.put( attName, attValue );
//		} else {
//			this.actualWorker.getAnnotations().put( attName, attValue );
//		}
//	}
//
//	
//}
