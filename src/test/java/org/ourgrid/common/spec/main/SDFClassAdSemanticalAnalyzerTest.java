package org.ourgrid.common.spec.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.SDFClassAdsSemanticAnalyzer;
import org.ourgrid.common.specification.worker.WorkerSpecification;

/**
 * This class contains some unit tests for the semantic actions performed while compiling
 * a SDF file.
 * @author David Candeia Medeiros Maia
 *
 */
public class SDFClassAdSemanticalAnalyzerTest {
	
	private static final String SDF_FILES_PATH = "test".concat( File.separator ).concat( "acceptance" ).concat( File.separator );
	private static final String MACHINE10_SDF = SDF_FILES_PATH.concat( "file10.classad" );
	private static final String MACHINE9_SDF = SDF_FILES_PATH.concat( "file9.classad" );
	private static final String MACHINE8_SDF = SDF_FILES_PATH.concat( "file8.classad" );
	private static final String MACHINE7_SDF = SDF_FILES_PATH.concat( "file7.classad" );
	private static final String MACHINE6_SDF = SDF_FILES_PATH.concat( "file6.classad" );
	private static final String MACHINE_REQ19_SDF = SDF_FILES_PATH.concat( "req19.classad" );
	private static final String MACHINE3_SDF = SDF_FILES_PATH.concat( "file3.classad" );
	private static final String MACHINE2_SDF = SDF_FILES_PATH.concat( "file2.classad" );
	private static final String MACHINE1_SDF = SDF_FILES_PATH.concat( "file1.classad" );

	@Test
	public void testAnalyzeSemanticValidationsWithGlobalNames() throws FileNotFoundException{
		try{
			List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile( MACHINE1_SDF );
			assertNotNull(specs);
			assertEquals(1, specs.size());
			
			//worker attributes
			WorkerSpecification workerSpec = specs.get(0);
			assertNotNull(workerSpec);
			assertEquals("testuser" , workerSpec.getAttribute(OurGridSpecificationConstants.USERNAME));
			assertEquals("testserver" , workerSpec.getAttribute(OurGridSpecificationConstants.SERVERNAME));
			assertEquals("linux" , workerSpec.getAttribute(WorkerSpecGlueConstants.OS));
			
			//Searching for inexistent attributes
			assertNull(workerSpec.getAttribute("workers"));
			assertNull(workerSpec.getAttribute("w1"));
			assertNull(workerSpec.getAttribute(WorkerSpecGlueConstants.CPU_VENDOR));
			assertNull(workerSpec.getAttribute(WorkerSpecGlueConstants.MACHINE_LOAD));
		}catch(CompilerException e){
			fail("A valid sdf file is being used! "+e.getMessage());
		}
	}
	
	@Test
	public void testAnalyzeSemanticValidationsWithLocalNames() throws FileNotFoundException{
		try{
			List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile( MACHINE2_SDF);
			assertNotNull(specs);
			assertEquals(2, specs.size());
			
			//First worker
			WorkerSpecification workerSpec = specs.get(0);
			assertNotNull(workerSpec);
			assertEquals("testuserB" , workerSpec.getAttribute(OurGridSpecificationConstants.USERNAME));
			assertEquals("testserver" , workerSpec.getAttribute(OurGridSpecificationConstants.SERVERNAME));
			assertEquals("128" , workerSpec.getAttribute(WorkerSpecGlueConstants.MAIN_MEMORY));
			assertEquals("linux" , workerSpec.getAttribute(WorkerSpecGlueConstants.OS));
			
			//Searching for inexistent attributes
			assertNull(workerSpec.getAttribute("workers"));
			assertNull(workerSpec.getAttribute("w1"));
			assertNull(workerSpec.getAttribute(WorkerSpecGlueConstants.CPU_MODEL));
			assertNull(workerSpec.getAttribute(WorkerSpecGlueConstants.SITE_NAME));
			
			//Second worker
			WorkerSpecification workerSpec2 = specs.get(1);
			assertNotNull(workerSpec2);
			assertEquals("testuserC" , workerSpec2.getAttribute(OurGridSpecificationConstants.USERNAME));
			assertEquals("testserver" , workerSpec2.getAttribute(OurGridSpecificationConstants.SERVERNAME));
			assertEquals("256" , workerSpec2.getAttribute(WorkerSpecGlueConstants.MAIN_MEMORY));
			assertEquals("windows" , workerSpec2.getAttribute(WorkerSpecGlueConstants.OS));
			
			//Searching for inexistent attributes
			assertNull(workerSpec.getAttribute("workers"));
			assertNull(workerSpec.getAttribute("w1"));
			assertNull(workerSpec.getAttribute(WorkerSpecGlueConstants.SITE_URL));
			assertNull(workerSpec.getAttribute(WorkerSpecGlueConstants.SITE_ID));
		}catch(CompilerException e){
			fail("A valid sdf file is being used!");
		}
	}
	
	@Test
	public void testAnalyzeSemanticValidationsWithOnlyLocalValues() throws FileNotFoundException{
		try{
			List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile(MACHINE3_SDF);
			assertNotNull(specs);
			assertEquals(1, specs.size());
			
			//First worker
			WorkerSpecification workerSpec = specs.get(0);
			assertNotNull(workerSpec);
			assertEquals("testuser" , workerSpec.getAttribute(OurGridSpecificationConstants.USERNAME));
			assertEquals("testserver" , workerSpec.getAttribute(OurGridSpecificationConstants.SERVERNAME));
			assertEquals("1024" , workerSpec.getAttribute(WorkerSpecGlueConstants.MAIN_MEMORY));
			assertEquals("linux" , workerSpec.getAttribute(WorkerSpecGlueConstants.OS));
			
			//Searching for inexistent attributes
			assertNull(workerSpec.getAttribute("workers"));
			assertNull(workerSpec.getAttribute("w1"));
			assertNull(workerSpec.getAttribute(WorkerSpecGlueConstants.DOMAIN_ID));
			assertNull(workerSpec.getAttribute(WorkerSpecGlueConstants.DOMAIN_WWW));
		}catch(CompilerException e){
			fail("A valid sdf file is being used!");
		}
	}
	
	@Test
	public void testAnalyzeSemanticValidationsWithLocalAndGlobalValues() throws FileNotFoundException{
		try{
			List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile(MACHINE_REQ19_SDF);
			assertNotNull(specs);
			assertEquals(1, specs.size());
			
			//First worker
			WorkerSpecification workerSpec = specs.get(0);
			assertNotNull(workerSpec);
			assertEquals("workerA.ourgrid.org" , workerSpec.getAttribute(OurGridSpecificationConstants.USERNAME));
			assertEquals("xmpp.ourgrid.org" , workerSpec.getAttribute(OurGridSpecificationConstants.SERVERNAME));
			
			//Searching for inexistent attributes
			assertNull(workerSpec.getAttribute("workers"));
			assertNull(workerSpec.getAttribute("w1"));
			assertNull(workerSpec.getAttribute(WorkerSpecGlueConstants.OS_WORD_LENGTH));
			assertNull(workerSpec.getAttribute(WorkerSpecGlueConstants.VIRTUAL_MEMORY));
		}catch(CompilerException e){
			fail("A valid sdf file is being used!");
		}
	}
	
	@Test
	public void testAnalyzeSemanticValidationsWithIntegerInvalidType() throws FileNotFoundException{
		try{
			SDFClassAdsSemanticAnalyzer.compile(MACHINE6_SDF);
			fail("Invalid type for main memory field!");
		}catch(CompilerException e){
			assertTrue(e.getMessage().contains("integer"));
		}
	}
	
	@Test
	public void testAnalyzeSemanticValidationsWithDoubleInvalidType() throws FileNotFoundException{
		try{
			SDFClassAdsSemanticAnalyzer.compile(MACHINE7_SDF);
			fail("Invalid type for main memory field!");
		}catch(CompilerException e){
			assertTrue(e.getMessage().contains("double"));
		}
	}
	
	@Test
	public void testAnalyzeSemanticValidationsWithBooleanInvalidType() throws FileNotFoundException{
		try{
			SDFClassAdsSemanticAnalyzer.compile(MACHINE8_SDF);
			fail("Invalid type for main memory field!");
		}catch(CompilerException e){
			assertTrue(e.getMessage().contains("boolean"));
		}
	}
	
	@Test
	public void testFileUsingComplexAttributes() throws CompilerException, FileNotFoundException{
		try{
			List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile(MACHINE9_SDF);
			assertNotNull(specs);
			assertEquals(2, specs.size());
			
			//First worker
			WorkerSpecification workerSpec = specs.get(0);
			assertNotNull(workerSpec);
			assertEquals("testuserB" , workerSpec.getAttribute(OurGridSpecificationConstants.USERNAME));
			assertEquals("testserver" , workerSpec.getAttribute(OurGridSpecificationConstants.SERVERNAME));
			assertEquals("128" , workerSpec.getAttribute(WorkerSpecGlueConstants.MAIN_MEMORY));
			
			//Second worker
			workerSpec = specs.get(1);
			assertNotNull(workerSpec);
			assertEquals("testuserC" , workerSpec.getAttribute(OurGridSpecificationConstants.USERNAME));
			assertEquals("testserver" , workerSpec.getAttribute(OurGridSpecificationConstants.SERVERNAME));
			assertEquals("true" , workerSpec.getAttribute(WorkerSpecGlueConstants.PREEMPTION_ENABLED));
			assertEquals("windows" , workerSpec.getAttribute(WorkerSpecGlueConstants.OS));
		}catch(CompilerException e){
			fail("Valid sdf file with complex attributes!");
		}
	}
	
	@Test
	public void testFileRedirectingAttribute() throws CompilerException, FileNotFoundException{
		try{
			List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile(MACHINE10_SDF);
			assertNotNull(specs);
			assertEquals(2, specs.size());
			
			//First worker
			WorkerSpecification workerSpec = specs.get(0);
			assertNotNull(workerSpec);
			assertEquals("testuserB" , workerSpec.getAttribute(OurGridSpecificationConstants.USERNAME));
			assertEquals("testserver" , workerSpec.getAttribute(OurGridSpecificationConstants.SERVERNAME));
			assertEquals("128" , workerSpec.getAttribute(WorkerSpecGlueConstants.MAIN_MEMORY));
			assertEquals("java" , workerSpec.getAttribute(WorkerSpecGlueConstants.SOFTWARE));
			assertEquals("[name=\"Java\";version=\"1.6\"]" , workerSpec.getAttribute("java"));
			
			//Second worker
			workerSpec = specs.get(1);
			assertNotNull(workerSpec);
			assertEquals("testuserC" , workerSpec.getAttribute(OurGridSpecificationConstants.USERNAME));
			assertEquals("testserver" , workerSpec.getAttribute(OurGridSpecificationConstants.SERVERNAME));
			assertEquals("true" , workerSpec.getAttribute(WorkerSpecGlueConstants.PREEMPTION_ENABLED));
			assertEquals("windows" , workerSpec.getAttribute(WorkerSpecGlueConstants.OS));
			assertEquals("java" , workerSpec.getAttribute(WorkerSpecGlueConstants.SOFTWARE));
			assertEquals("[name=\"Java\";version=\"1.6\"]" , workerSpec.getAttribute("java"));
			
		}catch(CompilerException e){
			fail("Valid sdf file with complex attributes!");
		}
	}

}
