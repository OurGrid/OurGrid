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
package org.ourgrid.common.specification.worker;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.ourgrid.common.specification.OurGridSpecification;
import org.ourgrid.common.specification.main.ClassAdSyntacticalAnalyzerStream;
import org.ourgrid.common.specification.main.SDFClassAdsSyntacticalAnalyzer;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.worker.WorkerConstants;

import condor.classad.AttrName;
import condor.classad.AttrRef;
import condor.classad.ClassAdParser;
import condor.classad.Constant;
import condor.classad.Expr;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;

/**
 * Entity that encapsulates all the information given by the user about a Worker
 * To provide the information, the user uses the Description Files that can be
 * compiled by CommonCompiler.
 */
public class WorkerSpecification extends OurGridSpecification {

	private static final long serialVersionUID = 40L;
	
	private transient RecordExpr record;//RecordExpr containing worker attributes

	/**
	 * Pairs of attribute=value provided by the users.
	 */
	protected Map<String, String> annotations;
	
	/**
	 * Default empty constructor
	 */
	public WorkerSpecification() {
		this( new TreeMap<String, String>());
	}

	/**
	 * Constructor using attributes from a map. Used with SDF.
	 * @param attributes A map of attributes
	 */
	public WorkerSpecification( Map<String,String> attributes ) {
		this(attributes, new TreeMap<String, String>());	
	}
	
	/**
	 * Constructor using attributes and annotations from maps. Used with SDF.
	 * @param attributes A map of attributes
	 */
	public WorkerSpecification( Map< String, String > attributes, Map< String, String > annotations ) {
		super( attributes );
		this.annotations = annotations;		
	}
	
	/**
	 * Default ClassAd constructor.
	 * @param attributes A {@link RecordExpr} containing the parsed ClassAd.
	 */
	public WorkerSpecification( RecordExpr attributes ) {
		this(attributes, new TreeMap<String, String>());	
	}
	
	/**
	 * Default ClassAd constructor.
	 * @param attributes A {@link RecordExpr} containing the parsed ClassAd.
	 * @param annotations A collection of annotations to do something we still don't know what it is.
	 */
	public WorkerSpecification( RecordExpr attributes, Map< String, String > annotations ) {
		super();
		this.annotations = annotations;
		record = attributes;
		setExpression( attributes.toString() );
	}


	/**
	 * Constructor receiving a string that represents a classad expression
	 * that describes the worker.
	 * @param expression The classad expression that describes a worker
	 */
	public WorkerSpecification( String expression ) {
		this.annotations = CommonUtils.createSerializableMap();
		setExpression( expression );
		checkRecordExpression();
		generateRecordExprString();
	}

	/**
	 * This method adds an annotation to the current worker
	 * @param property The attribute name
	 * @param value The attribute value
	 */
	public void addAnnotation( String property, String value ) {
		this.annotations.put( property, value );
	}


	/**
	 * Returns the value of an annotation given its attribute name.
	 * 
	 * @param attName the attribute name.
	 * @return the attribute value.
	 */
	public String getAnnotation( String attName ) {

		String value = this.annotations.get( attName );
		if ( value != null ) {
			return value;
		}
		return null;
	}

	/**
	 * Sets the annotations collection.
	 * 
	 * @param annotations annotations specified by pairs attribute=value.
	 */
	public void setAnnotations( Map<String, String> annotations ) {

		this.annotations = annotations;
	}

	/**
	 * Adds an annotations collection.
	 * 
	 * @param annotations annotations specified by pairs attribute=value.
	 */
	public void addAllAnnotations( Map<String, String> annotations ) {

		this.annotations.putAll(annotations);
	}
	
	/**
	 * Retrieves the map of annotations.
	 * 
	 * @return a map with all the attributes of this grid machine indexed by the
	 *         name of the attribute.
	 */
	public Map<String,String> getAnnotations() {

		return this.annotations;
	}
	
	/**
	 * @see OurGridSpecification#getModuleName
	 */
	@Override
	public String getModuleName() {

		return WorkerConstants.MODULE_NAME;
	}

	/**
	 * @see OurGridSpecification#getObjectName
	 */
	@Override
	public String getObjectName() {

		return WorkerConstants.LOCAL_WORKER_MANAGEMENT;
	}
	
	@Override
	public int hashCode(){
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((this.annotations == null) ? 0 : this.annotations.hashCode());
		return result;
	}
	
	
	@Override
	public String toString(){
		StringBuffer s = new StringBuffer(super.toString());
		
		s.append( "Annotations: " + this.annotations.size() ).append( " \t" );
		
		for (Entry<String,String> entry : this.annotations.entrySet()) {
			//s.append( entry.getKey() ).append( " : " ).append( entry.getValue() ).append( System.getProperty("line.separator") );
			s.append( entry.getKey() ).append( " : " ).append( entry.getValue() ).append( "; \t" );
		}
		s.append( System.getProperty("line.separator") );
		
		return s.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		
		if(usingClassAd()){
			if ( !(obj instanceof WorkerSpecification) )
				return false;

			final WorkerSpecification other = (WorkerSpecification) obj;
			
			if(other.usingClassAd()){
				return this.equalsAd( other );
			}
			return false;
		}
		
		if( !super.equals(obj))
			return false;

		if ( !(obj instanceof WorkerSpecification) )
			return false;

		final WorkerSpecification other = (WorkerSpecification) obj;
		
		return this.annotations.equals( other.annotations );						
	}
	
	/**
	 * This method verifies if two workers have the same record expression
	 * @param other Another worker specification
	 * @return True if the classad expressions are equal, false otherwise
	 */
	private boolean equalsAd(WorkerSpecification other){
		
		return this.record.sameAs(other.getRecord());						
	}
	
	/**
	 * This method checks if the current worker is being described by a 
	 * classad expression.
	 * @return True if the worker is described by a classad expression, false
	 * otherwise
	 */
	public boolean usingClassAd(){
		String expression = this.attributes.get( WorkerSpecificationConstants.EXPRESSION );

		boolean result = expression != null && !(expression.length() == 0);
		if(result){
			checkRecordExpression();
		}
		return result;
	}
	
	/**
	 * Check if the ClassAd record expression has been rebuilt since serialization. 
	 */
	private void checkRecordExpression() {

		String expression = this.attributes.get( WorkerSpecificationConstants.EXPRESSION );
		if(record == null){
			record = (RecordExpr) new ClassAdParser(expression).parse();
		}
	}
	
	/**
	 * @see OurGridSpecification#getAttribute(String)
	 */
	@Override
	public String getAttribute(String attName) {
		
		if(usingClassAd()){
			Expr value = this.record.lookup(attName);
			if(value != null){
				if(value.type == Expr.STRING){
					return value.stringValue();
				}else{
					return value.toString();
				}
			}
			return null;
		} 

		return super.getAttribute(attName);
	}
	
	/**
	 * This method retrieves the classad expression that describes
	 * the worker
	 * @return The classad expression
	 */
	public RecordExpr getRecord(){
		return this.record;
	}
	
	/**
	 * This method retrieves the classad expression string that describes
	 * the worker
	 * @return The classad expression string
	 */
	public String getExpression(){
		return this.attributes.get( WorkerSpecificationConstants.EXPRESSION );

	}
	
	/**
	 * This method changes the classad expression that describes the worker
	 * @param attributes The new classad expression
	 */
	public void setRecord( RecordExpr attributes ) {
		
		this.record = attributes;
		this.generateRecordExprString();
	}
	
	/**
	 * This method inserts a new attribute in the classad expression
	 * that describes the worker.
	 * @param name The attribute name
	 * @param value The attribute value
	 */
	private void inserRecordAttribute(String name, String value) {
		ClassAdSyntacticalAnalyzerStream.changeSystemErrToStream();
		RecordExpr expr = (RecordExpr) new ClassAdParser(value).parse();
		String errorMessage = ClassAdSyntacticalAnalyzerStream.getErrorMessage();
		if(errorMessage.length() > 0){
			throw new IllegalArgumentException("Mal formed attribute "+name+" "+errorMessage);
		}
		this.record.insertAttribute(name, expr);
		ClassAdSyntacticalAnalyzerStream.resetSystemErr();
	}
	
	/**
	 * This method creates a string value for the classad expression
	 * that describes the worker.
	 */
	private void generateRecordExprString(){
		setExpression( this.record.toString() );
	}
	
	/**
	 * @see OurGridSpecification#putAttribute(String, String)
	 */
	@Override
	public void putAttribute(String name, String value) {

		if(usingClassAd()){
			
			if(value.contains(SDFClassAdsSyntacticalAnalyzer.LIST_SYMBOL)){
				ListExpr listExpr = new ListExpr();
				this.record.insertAttribute(name, listExpr);
				String[ ] values = value.trim().split( "\\{" )[1].split( "\\}" )[0].split( "," );
				for ( String v : values ) {
					listExpr.add( parseAll( v ) );
				}
			}else if(value.contains(SDFClassAdsSyntacticalAnalyzer.RECORD_SYMBOL)){
				inserRecordAttribute(name, value);
			}else{
				Constant constant = parseConstant(value);
				this.record.insertAttribute(name, constant);
			}
			
			this.generateRecordExprString();
		}else{
			super.putAttribute(name, value);
		}
	}
	
	/**
	 * This method verifies if a certain attribute value is a classad constant
	 * @param value The attribute value
	 * @return True if the value is a constant, false otherwise
	 */
	private Constant parseConstant( String value ) {
		
		Constant primitiveValue = parsePrimitiveValue( value );
		return primitiveValue != null? primitiveValue : Constant.getInstance(value);
	}
	
	/**
	 * This method parses an attribute value according to classad syntax
	 * @param value The attribute value
	 * @return An expression {@link Expr} that represents the attribute
	 * value
	 */
	private Expr parseAll( String value ) {
		
		if(value.trim().startsWith( "\"" )){
			return Constant.getInstance(value);
		}
		
		Constant parsePrimitiveValue = parsePrimitiveValue( value );
		return parsePrimitiveValue != null? parsePrimitiveValue : new AttrRef(value);
	}

	/**
	 * This method verifies a certain attribute value is from a primitive
	 * type
	 * @param value The attribute value
	 * @return A constant {@link Constant} that represents the attribute value
	 */
	private Constant parsePrimitiveValue( String value ) {

		if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")){
			boolean booleanValue = Boolean.valueOf(value);
			return Constant.getInstance(booleanValue);
		}
		
		try{
			Integer intValue = Integer.valueOf(value);
			return Constant.getInstance(intValue);
		}catch(Exception e){
		}

		try{
			Long longValue = Long.valueOf(value);
			return Constant.getInstance(longValue);
		}catch(Exception e){
		}

		try{
			Double d = Double.valueOf(value);
			return Constant.getInstance(d);
		}catch(Exception e){
		}
		return null;
	}

	/**
	 * @see OurGridSpecification#hasAttribute(String)
	 */
	@Override
	public boolean hasAttribute(String name) {
		
		if(usingClassAd()){
			return this.record.lookup(name) != null;
		}
		return super.hasAttribute(name);
	}
	
	/**
	 * @see OurGridSpecification#removeAttribute(String)
	 */
	@Override
	public void removeAttribute(String key) {

		if( usingClassAd() ){
			if( this.record.removeAttribute(AttrName.fromString(key)) != null ){
				this.generateRecordExprString();
			}
		}else{
			super.removeAttribute(key);
		}
	}
	
	/**
	 * This method adds a set of attributes that describes the worker
	 * to the current worker classad expression
	 * @param expr An expression containing new attributes
	 */
	@SuppressWarnings("unchecked")
	public void putAttributes(RecordExpr expr){
		checkRecordExpression();
		
		Iterator<AttrName> iterator = expr.attributes();
		while (iterator.hasNext()) {
			AttrName name = iterator.next();
			this.record.insertAttribute(name, expr.lookup(name));
		}
	}

	/**
	 * @param expression the expression to set
	 */
	public void setExpression( String expression ) {
	
		this.attributes.put( WorkerSpecificationConstants.EXPRESSION, expression );
	}
	
	public void setAttributes( Map<String, String> att) {
		this.attributes = att;
	}
	
}

