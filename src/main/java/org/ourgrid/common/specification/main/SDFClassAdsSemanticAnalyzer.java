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
package org.ourgrid.common.specification.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.glite.jdl.Jdl;
import org.ourgrid.common.specification.CompilerMessages;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.semantic.exception.SemanticException;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecificationConstants;

import condor.classad.AttrName;
import condor.classad.AttrRef;
import condor.classad.Constant;
import condor.classad.Expr;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;

/**
 * This class is responsible for the semantic validations of SDF written in classAds
 * @author David Candeia Medeiros Maia
 */
public class SDFClassAdsSemanticAnalyzer {
	
	/**
	 * This method is responsible for requesting the syntactical and semantical analysis
	 * of a SDF file
	 * @param sourceFileName The SDF file
	 * @return A list containing specifications {@link WorkerSpecification} of defined workers
	 * @throws CompilerException Exception thrown if an error occurs during the compilation
	 */
	public static List<WorkerSpecification> compile(String sourceFileName ) throws CompilerException {
		RecordExpr expression = SDFClassAdsSyntacticalAnalyzer.compile( sourceFileName );
		return analyze( expression );
	}
	
	
	/**
	 * This method verifies if the workers list is defined in the SDF file
	 * @throws CompilerException
	 */
	private static void validateWorkersDeclaration(RecordExpr recordExpr) throws CompilerException {
		try{
			ListExpr result = (ListExpr) recordExpr.lookup(WorkerSpecificationConstants.WORKERS);
			if(result == null){
				throw new CompilerException(CompilerMessages.SEMANTIC_MISSING_ATTRIBUTE(WorkerSpecificationConstants.WORKERS));
			}

			//Iterating over declared workers
			Iterator iterator = result.iterator();
			if(!iterator.hasNext()){
				throw new CompilerException(CompilerMessages.SEMANTIC_MISSING_ATTRIBUTE(WorkerSpecificationConstants.WORKERS));
			}
			
		}catch(ClassCastException e){
			throw new CompilerException(CompilerMessages.SEMANTIC_MISSING_ATTRIBUTE(WorkerSpecificationConstants.WORKERS));
		}
	}
	
	/**
	 * This method verifies if an user and a server name were declared for a worker
	 * @param recordExpr The expression that should be verified
	 * @return True if both attributes were declared, false otherwise
	 */
	private static boolean verifyServerAndName(RecordExpr recordExpr) {
		Expr userName = recordExpr.lookup(OurGridSpecificationConstants.USERNAME);
		Expr serverName = recordExpr.lookup(OurGridSpecificationConstants.SERVERNAME);
		
		if(userName == null || serverName == null){
			return false;
		}
		
		if(userName.isConstant() && serverName.isConstant()){
			Constant userConst = (Constant) userName;
			Constant serverConst = (Constant) serverName;
			
			return userConst.stringValue().length() > 0 && serverConst.stringValue().length() > 0;
		}
		
		return false;
	}
	
	/**
	 * This method gathers general information and specific information for all workers and creates worker
	 * specifications {@link WorkerSpecification}
	 * @param recordExpr The expression containing all workers informations
	 * @return A list of worker specifications
	 * @throws CompilerException Exception thrown if an error occurs during the compilation
	 */
	private static List<WorkerSpecification> analyze(RecordExpr recordExpr) throws CompilerException {
		validateWorkersDeclaration(recordExpr);
		
		ListExpr result = (ListExpr) recordExpr.lookup(WorkerSpecificationConstants.WORKERS);
		List<WorkerSpecification> workersSpecs = new ArrayList<WorkerSpecification>();
		
		//General information for all workers
		RecordExpr generalData = obtainGeneralData(recordExpr, result);
		
		Iterator iterator = result.iterator();
		while(iterator.hasNext()){
			//Inserting general information
			RecordExpr workerSpecExpr = insertAllAttributes(generalData);
			
			//Getting workers specific descriptions
			AttrRef name = (AttrRef) iterator.next();
			RecordExpr workerSpecificExpr = (RecordExpr)recordExpr.lookup(name.name);
			
			if(workerSpecificExpr != null){
				Iterator workerSpecificIterator = workerSpecificExpr.attributes();
				while(workerSpecificIterator.hasNext()){
					AttrName attrName = (AttrName)workerSpecificIterator.next();
					Expr value = workerSpecificExpr.lookup(attrName);
					
					//Verifying type
					verifyType(attrName, value);
					
					workerSpecExpr.insertAttribute(attrName, value);
				}
			}
			
			if(!verifyServerAndName(workerSpecExpr)){
				throw new CompilerException(CompilerMessages.MISSING_ATTRIBUTE(OurGridSpecificationConstants.USERNAME+" or "+OurGridSpecificationConstants.SERVERNAME));
			}
			
			WorkerSpecification workerSpec = new WorkerSpecification(parseRequirements( workerSpecExpr ) );
			if ( workerSpec.isValid() ) {
				workersSpecs.add( workerSpec );
			} else {
				throw new SemanticException( CompilerMessages.BAD_WORKER_DEFINITION( workerSpec.getURL() ) );
			}
		}
		
		return workersSpecs;
	}
	
	/**
	 * This method inserts all attributes of a certain expression inside another expression
	 * @param generalData The expression which informations should be copied
	 * @return A new expression containing the information of the other expression
	 * @throws SemanticException Exception thrown if any attribute was declared wrongly
	 */
	private static RecordExpr insertAllAttributes(RecordExpr generalData) throws SemanticException {
		Iterator iterator = generalData.attributes();
		RecordExpr workerSpec = new RecordExpr();
		
		while(iterator.hasNext()){
			AttrName attrName = (AttrName)iterator.next();
			Expr value = generalData.lookup(attrName);
			
			//Verifying type
			verifyType(attrName, value);
			
			workerSpec.insertAttribute(attrName, value);
		}
		
		return workerSpec;
	}
	
	/**
	 * This method verifies if a certain attribute matches its predefined type
	 * @param attrName The attribute name
	 * @param value The attribute value
	 * @throws SemanticException Exception thrown if the match does not occur 
	 */
	private static void verifyType(AttrName attrName, Expr value) throws SemanticException {
		String attributeName = attrName.toString();
		String valueString = getStringValue(value);
		if(findInt(attributeName)){
			try{
				Integer.valueOf(valueString);
			}catch(NumberFormatException e){
				throw new SemanticException(CompilerMessages.INVALID_TYPE(attributeName, "integer"));
			}
		}else if(findLong(attributeName)){
			try{
				Long.valueOf(valueString);
			}catch(NumberFormatException e){
				throw new SemanticException(CompilerMessages.INVALID_TYPE(attributeName, "long"));
			}
		}else if(findDouble(attributeName)){
			try{
				Double.valueOf(valueString);
			}catch(NumberFormatException e){
				throw new SemanticException(CompilerMessages.INVALID_TYPE(attributeName, "double"));
			}
		}else if(findBool(attributeName)){
			if( !(valueString.equalsIgnoreCase("true") || valueString.equalsIgnoreCase("false")) ) {
				throw new SemanticException(CompilerMessages.INVALID_TYPE(attributeName, "boolean"));
			}
		}
	}

	private static String getStringValue(Expr value) {
		if(value.type == Expr.STRING){
			return value.stringValue();
		}else{
			return value.toString();
		}
	}

	/**
	 * This method retrieves general information for all workers
	 * @param recordExpr The expression containing the definition of all workers and all general information
	 * @param workersNames The workers defined
	 * @return The expression containing information defined for all workers
	 */
	private static RecordExpr obtainGeneralData(RecordExpr recordExpr, ListExpr workersNames) {
		RecordExpr generalExpr = new RecordExpr();
		
		Iterator attributes = recordExpr.attributes();
		while(attributes.hasNext()){
			AttrName attrName = (AttrName)attributes.next();
			if(!attrName.toString().equals(WorkerSpecificationConstants.WORKERS) && !workersNames.toString().contains(attrName.toString())){
				generalExpr.insertAttribute(attrName, recordExpr.lookup(attrName));
			}
		}
		
		return generalExpr;
	}
	
    /**
    * Check if the specified value could be of Boolean type
    * @param attrName the name of the attribute to be look for
    * @return true if the attribute type match, false otherwise*/
    private static boolean findBool(String attrName) {
        return findAttribute(attrName, WorkerSpecificationConstants.booleanAttributes);
    }

    /**
     * Check if the specified value could be of Integer type
     * @param attrName the name of the attribute to be look for
     * @return true if the attribute type match, false otherwise*/
     private static boolean findInt(String attrName) {
         return findAttribute(attrName, WorkerSpecificationConstants.integerAttributes);
     }

     /**
      * Check if the specified value could be of Integer type
      * @param attrName the name of the attribute to be look for
      * @return true if the attribute type match, false otherwise*/
      private static boolean findLong(String attrName) {
          return findAttribute(attrName, WorkerSpecificationConstants.longAttributes);
      }

    /**
    * Check if the specified value could be of String type
    * @param attrName the name of the attribute to be look for
    * @return true if the attribute type match, false otherwise*/
    private static boolean findString(String attrName) {
        return findAttribute(attrName, WorkerSpecificationConstants.stringAttributes);
    }

    /**
    * Check if the specified value could be of Double type
    * @param attrName the name of the attribute to be look for
    * @return true if the attribute type match, false otherwise*/
    private static boolean findDouble(String attrName) {
        return findAttribute(attrName, WorkerSpecificationConstants.doubleAttributes);
    }
    
    /**Check if the two strings are equals (case insensitive)*/
    private static boolean compare(String a, String b) {
        return a.equalsIgnoreCase(b);
    }
    
    /**
     * This method searches for a certain attribute name in a list of attributes names
     * @param attrName The searched attribute
     * @param list The list of attributes names
     * @return True if the searched attribute was found, false otherwise
     */
    private static boolean findAttribute(String attrName, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (compare(attrName, list[i])) {
                return true;
            }
        }

        return false;
    }
    
	/**
	 * This method verifies if the requirements and rank expressions were defined for a certain worker
	 * @param jobSpec The job specification {@link JobSpecification}
	 * @return A record expression containing all workers attributes
	 */
	private static RecordExpr parseRequirements( RecordExpr expr ) {
		
		Expr requirementsExpr = expr.lookup( Jdl.REQUIREMENTS );
		expr.insertAttribute( Jdl.REQUIREMENTS, requirementsExpr == null? Constant.TRUE : requirementsExpr);

		Expr rankExpr = expr.lookup( Jdl.RANK );
		expr.insertAttribute( Jdl.RANK, rankExpr == null? Constant.getInstance( 0 ) : rankExpr);
		
		return expr;
	}
}
