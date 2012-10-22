package org.ourgrid.common.spec.main;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.SDFClassAdsSyntacticalAnalyzer;

import condor.classad.ClassAdParser;
import condor.classad.RecordExpr;

/**
 * Some unit tests for the sdf syntactical verification using classAds
 * @author David Candeia Medeiros Maia
 *
 */
public class SDFClassAdSyntacticalAnalyzerTest {
	
	@Test
	public void testConstructorWithInvalidFile(){
		try{
			SDFClassAdsSyntacticalAnalyzer.compile("./invalidFilePath.sdf");
			fail("Invalid sdf file path!");
		}catch(CompilerException e){
		}
	}
	
	@Test
	public void testValidFile() throws CompilerException, FileNotFoundException{
		
		RecordExpr expectedExpr = (RecordExpr) new ClassAdParser(new FileInputStream(new File("test"+File.separator+"acceptance"+File.separator+"file1.classad"))).parse();
		RecordExpr recordExpr = SDFClassAdsSyntacticalAnalyzer.compile("test"+File.separator+"acceptance"+File.separator+"file1.classad");
		assertNotNull(expectedExpr);
		assertNotNull(recordExpr);
		assertTrue(expectedExpr.sameAs(recordExpr));
		
	}
	
	@Test
	public void testInvalidFileWithMissingAttribute() throws CompilerException, FileNotFoundException{
		
		try{
			SDFClassAdsSyntacticalAnalyzer.compile("test"+File.separator+"acceptance"+File.separator+"file4.classad");
			fail("Invalid sdf file!");
		}catch(CompilerException e){
			assertTrue(e.getMessage().contains("syntax error"));
		}
	}
	
	@Test
	public void testInvalidFileWithMissingToken() throws CompilerException, FileNotFoundException{

		try{
			SDFClassAdsSyntacticalAnalyzer.compile("test"+File.separator+"acceptance"+File.separator+"file5.classad");
			fail("Invalid sdf file!");
		}catch(CompilerException e){
			assertTrue(e.getMessage().contains("syntax error"));
		}
	}
	
	@Test
	public void testFileUsingComplexAttributes() throws CompilerException{
		try{
			SDFClassAdsSyntacticalAnalyzer.compile("test"+File.separator+"acceptance"+File.separator+"file9.classad");
		}catch(CompilerException e){
			fail("Valid sdf file!");
		}
	}
	
	@Test
	public void testFileRedirectingAttributes() throws CompilerException{
		try{
			SDFClassAdsSyntacticalAnalyzer.compile("test"+File.separator+"acceptance"+File.separator+"file10.classad");
		}catch(CompilerException e){
			fail("Valid sdf file!");
		}
	}
}
