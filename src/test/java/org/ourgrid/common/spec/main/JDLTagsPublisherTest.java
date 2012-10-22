package org.ourgrid.common.spec.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.JDLTagsPublisher;
import org.ourgrid.common.specification.main.SDFClassAdsSemanticAnalyzer;
import org.ourgrid.common.specification.worker.WorkerSpecification;

import condor.classad.RecordExpr;

/**
 * This class contains unit tests for the Publisher of worker specification attributes
 * according to GLUE schemas constants defined in the tags.conf file.
 * @author David Candeia Medeiros Maia
 *
 */
public class JDLTagsPublisherTest {
	
	private static final String SDF_FILES_PATH = "test".concat( File.separator ).concat( "acceptance" ).concat( File.separator );
	
	private static final String MACHINE1_SDF = SDF_FILES_PATH.concat( "file1.classad" );
	private static final String MACHINE2_SDF = SDF_FILES_PATH.concat( "file2.classad" );
	private static final String MACHINE3_SDF = SDF_FILES_PATH.concat( "file9.classad" );
	private static final String MACHINE4_SDF = SDF_FILES_PATH.concat( "publisher1.classad" );
	private static final String MACHINE5_SDF = SDF_FILES_PATH.concat( "publisher2.classad" );
	private static final String MACHINE6_SDF = SDF_FILES_PATH.concat( "publisher3.classad" );

	private static final String VALID_TAGS_FILE = SDF_FILES_PATH.concat( "tags.conf" );

	private static final String MISSING_TAGS_FILE = SDF_FILES_PATH.concat( "tags_invalid.conf" );
	
	private static final String WRONG_MAPPING_TAGS_FILE = SDF_FILES_PATH.concat( "tags_invalid2.conf" );
	private static final String WRONG_MAPPING_TAGS_FILE2 = SDF_FILES_PATH.concat( "tags_invalid3.conf" );
	private static final String WRONG_MAPPING_TAGS_FILE3 = SDF_FILES_PATH.concat( "tags_invalid4.conf" );
	
	public static final String OS_LINUX = "linux";
	public static final String OS_WINDOWS = "windows";
	public static final String OS_HPUX = "hpux";

	@Test(expected=NullPointerException.class)
	public void testLoadGlueTagsWithNullFilePath(){
		JDLTagsPublisher.loadGLUETags(null);
	}
	
	@Test(expected=RuntimeException.class)
	public void testLoadGlueTagsWithEmpytFilePath(){
		JDLTagsPublisher.loadGLUETags("");
	}
	
	@Test(expected=RuntimeException.class)
	public void testLoadGlueTagsWithInexistentFile(){
		JDLTagsPublisher.loadGLUETags(SDF_FILES_PATH.concat("inexistent_file.conf"));
	}
	
	@Test(expected=RuntimeException.class)
	public void testLoadGlueTagsWithMissingTagFile(){
		JDLTagsPublisher.loadGLUETags(MISSING_TAGS_FILE);
	}
	
	/**
	 * This method verifies that if the GLUE schema constant contains more 
	 * levels than the ones specified in the SDF an error should be thrown.
	 */
	@Test(expected=RuntimeException.class)
	public void testLoadGlueTagsWithWrongMappingTagFile(){
		JDLTagsPublisher.loadGLUETags(WRONG_MAPPING_TAGS_FILE);
	}
	
	/**
	 * This method verifies that if the SDF constant contains more 
	 * levels than the GLUE schema constant an error should be thrown.
	 */
	@Test(expected=RuntimeException.class)
	public void testLoadGlueTagsWithWrongMappingTagFile2(){
		JDLTagsPublisher.loadGLUETags(WRONG_MAPPING_TAGS_FILE2);
	}
	
	@Test(expected=RuntimeException.class)
	public void testLoadGlueTagsWithKeyWithoutValues(){
		JDLTagsPublisher.loadGLUETags(WRONG_MAPPING_TAGS_FILE3);
	}
	
	@Test
	public void testSimpleAttributesSpecs() throws CompilerException{
		List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile( MACHINE1_SDF );
		assertNotNull(specs);
		
		JDLTagsPublisher.loadGLUETags(VALID_TAGS_FILE);
		WorkerSpecification workerSpec = specs.get(0);
		RecordExpr expr = JDLTagsPublisher.buildExprWithTagsToPublish( workerSpec.getExpression() );

		assertEquals("testserver", expr.lookup(OurGridSpecificationConstants.SERVERNAME).stringValue());
		assertEquals("testuser", expr.lookup(OurGridSpecificationConstants.USERNAME).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.OS).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_OPERATING_SYSTEM_NAME).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_OS_NAME).stringValue());
	}
	
	@Test
	public void testSimpleAttributesWithSpecificWorkersAttributes() throws CompilerException{
		List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile( MACHINE2_SDF );
		assertNotNull(specs);
		
		JDLTagsPublisher.loadGLUETags(VALID_TAGS_FILE);
		
		WorkerSpecification workerSpec = specs.get(0);
		RecordExpr expr = JDLTagsPublisher.buildExprWithTagsToPublish(workerSpec.getExpression());
		
		assertEquals("testserver", expr.lookup(OurGridSpecificationConstants.SERVERNAME).stringValue());
		assertEquals("testuserB", expr.lookup(OurGridSpecificationConstants.USERNAME).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.OS).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_OPERATING_SYSTEM_NAME).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_OS_NAME).stringValue());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.MAIN_MEMORY).toString());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_MAIN_MEMORY_SIZE).toString());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_MAIN_MEMORY_RAM_SIZE).toString());
		
		workerSpec = specs.get(1);
		expr = JDLTagsPublisher.buildExprWithTagsToPublish(workerSpec.getExpression());

		assertEquals("testserver", expr.lookup(OurGridSpecificationConstants.SERVERNAME).stringValue());
		assertEquals("testuserC", expr.lookup(OurGridSpecificationConstants.USERNAME).stringValue());
		assertEquals(OS_WINDOWS, expr.lookup(WorkerSpecGlueConstants.OS).stringValue());
		assertEquals(OS_WINDOWS, expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_OPERATING_SYSTEM_NAME).stringValue());
		assertEquals(OS_WINDOWS, expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_OS_NAME).stringValue());
		assertEquals("256", expr.lookup(WorkerSpecGlueConstants.MAIN_MEMORY).toString());
		assertEquals("256", expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_MAIN_MEMORY_SIZE).toString());
		assertEquals("256", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_MAIN_MEMORY_RAM_SIZE).toString());
	}
	
	@Test
	public void testDefaultStructuredAttributes() throws CompilerException{
		List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile( MACHINE3_SDF );
		assertNotNull(specs);
		
		JDLTagsPublisher.loadGLUETags(VALID_TAGS_FILE);

		WorkerSpecification workerSpec = specs.get(0);
		RecordExpr expr = JDLTagsPublisher.buildExprWithTagsToPublish(workerSpec.getExpression());

		assertEquals("testserver", expr.lookup(OurGridSpecificationConstants.SERVERNAME).stringValue());
		assertEquals("testuserB", expr.lookup(OurGridSpecificationConstants.USERNAME).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.OS).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_OPERATING_SYSTEM_NAME).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_OS_NAME).stringValue());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.MAIN_MEMORY).toString());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_MAIN_MEMORY_SIZE).toString());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_MAIN_MEMORY_RAM_SIZE).toString());
		assertEquals("{java,sql}", expr.lookup(WorkerSpecGlueConstants.SOFTWARE).toString());
		assertEquals("{java,sql}", expr.lookup("ApplicationEnvironment").toString());
		assertEquals("[name=\"Java\";version=\"1.6\"]", expr.lookup("java").toString());
		assertEquals("[name=\"SQL\";version=\"2.0\"]", expr.lookup("sql").toString());
		assertEquals("{\"Java\",\"SQL\"}", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_APPLICATION_SOFTWARE_RUNTIME_ENVIRONMENT).toString());
		
		workerSpec = specs.get(1);
		expr = JDLTagsPublisher.buildExprWithTagsToPublish(workerSpec.getExpression());
		
		assertEquals("testserver", expr.lookup(OurGridSpecificationConstants.SERVERNAME).stringValue());
		assertEquals("testuserC", expr.lookup(OurGridSpecificationConstants.USERNAME).stringValue());
		assertEquals(OS_WINDOWS, expr.lookup(WorkerSpecGlueConstants.OS).stringValue());
		assertEquals(OS_WINDOWS, expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_OPERATING_SYSTEM_NAME).stringValue());
		assertEquals(OS_WINDOWS, expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_OS_NAME).stringValue());
		assertEquals("true", expr.lookup(WorkerSpecGlueConstants.PREEMPTION_ENABLED).stringValue());
		assertEquals("true", expr.lookup(WorkerSpecGlueConstants.GLUE_CE_POLICY_PREEMPTION).stringValue());
		assertEquals("{java,sql}", expr.lookup(WorkerSpecGlueConstants.SOFTWARE).toString());
		assertEquals("{java,sql}", expr.lookup("ApplicationEnvironment").toString());
		assertEquals("[name=\"Java\";version=\"1.6\"]", expr.lookup("java").toString());
		assertEquals("[name=\"SQL\";version=\"2.0\"]", expr.lookup("sql").toString());
		assertEquals("{\"Java\",\"SQL\"}", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_APPLICATION_SOFTWARE_RUNTIME_ENVIRONMENT).toString());
	}
	
	@Test
	public void testSoftwareAsOneRefAndListOfConstants() throws CompilerException{
		List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile( MACHINE4_SDF );
		assertNotNull(specs);
		
		JDLTagsPublisher.loadGLUETags(VALID_TAGS_FILE);

		WorkerSpecification workerSpec = specs.get(0);
		RecordExpr expr = JDLTagsPublisher.buildExprWithTagsToPublish(workerSpec.getExpression());

		assertEquals("testserver", expr.lookup(OurGridSpecificationConstants.SERVERNAME).stringValue());
		assertEquals("testuserB", expr.lookup(OurGridSpecificationConstants.USERNAME).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.OS).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_OPERATING_SYSTEM_NAME).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_OS_NAME).stringValue());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.MAIN_MEMORY).toString());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_MAIN_MEMORY_SIZE).toString());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_MAIN_MEMORY_RAM_SIZE).toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("names").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("OtherNames").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("TestNames").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("DataNames").toString());
		assertEquals("java", expr.lookup(WorkerSpecGlueConstants.SOFTWARE).toString());
		assertEquals("java", expr.lookup("ApplicationEnvironment").toString());
		assertEquals("[name=\"Java\";version=\"1.6\"]", expr.lookup("java").toString());
		assertEquals("{\"Java\"}", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_APPLICATION_SOFTWARE_RUNTIME_ENVIRONMENT).toString());
		
		workerSpec = specs.get(1);
		expr = JDLTagsPublisher.buildExprWithTagsToPublish(workerSpec.getExpression());
		
		assertEquals("testserver", expr.lookup(OurGridSpecificationConstants.SERVERNAME).stringValue());
		assertEquals("testuserC", expr.lookup(OurGridSpecificationConstants.USERNAME).stringValue());
		assertEquals(OS_WINDOWS, expr.lookup(WorkerSpecGlueConstants.OS).stringValue());
		assertEquals(OS_WINDOWS, expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_OPERATING_SYSTEM_NAME).stringValue());
		assertEquals(OS_WINDOWS, expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_OS_NAME).stringValue());
		assertEquals("true", expr.lookup(WorkerSpecGlueConstants.PREEMPTION_ENABLED).toString());
		assertEquals("true", expr.lookup(WorkerSpecGlueConstants.GLUE_CE_POLICY_PREEMPTION).toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("names").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("OtherNames").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("TestNames").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("DataNames").toString());
		assertEquals("java", expr.lookup(WorkerSpecGlueConstants.SOFTWARE).toString());
		assertEquals("java", expr.lookup("ApplicationEnvironment").toString());
		assertEquals("[name=\"Java\";version=\"1.6\"]", expr.lookup("java").toString());
		assertEquals("{\"Java\"}", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_APPLICATION_SOFTWARE_RUNTIME_ENVIRONMENT).toString());
	}
	
	@Test
	public void testSingleSoftwareAsRecordExpr() throws CompilerException{
		List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile( MACHINE5_SDF );
		assertNotNull(specs);
		
		JDLTagsPublisher.loadGLUETags(VALID_TAGS_FILE);
		
		WorkerSpecification workerSpec = specs.get(0);
		RecordExpr expr = JDLTagsPublisher.buildExprWithTagsToPublish(workerSpec.getExpression());

		assertEquals("testserver", expr.lookup(OurGridSpecificationConstants.SERVERNAME).stringValue());
		assertEquals("testuserB", expr.lookup(OurGridSpecificationConstants.USERNAME).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.OS).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_OPERATING_SYSTEM_NAME).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_OS_NAME).stringValue());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.MAIN_MEMORY).toString());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_MAIN_MEMORY_SIZE).toString());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_MAIN_MEMORY_RAM_SIZE).toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("names").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("OtherNames").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("TestNames").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("DataNames").toString());
		assertEquals("[name=\"Java\";version=\"1.6\"]", expr.lookup(WorkerSpecGlueConstants.SOFTWARE).toString());
		assertEquals("[name=\"Java\";version=\"1.6\"]", expr.lookup("ApplicationEnvironment").toString());
		assertEquals("{\"Java\"}", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_APPLICATION_SOFTWARE_RUNTIME_ENVIRONMENT).toString());
		
		workerSpec = specs.get(1);
		expr = JDLTagsPublisher.buildExprWithTagsToPublish(workerSpec.getExpression());
		
		assertEquals("testserver", expr.lookup(OurGridSpecificationConstants.SERVERNAME).stringValue());
		assertEquals("testuserC", expr.lookup(OurGridSpecificationConstants.USERNAME).stringValue());
		assertEquals(OS_WINDOWS, expr.lookup(WorkerSpecGlueConstants.OS).stringValue());
		assertEquals(OS_WINDOWS, expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_OPERATING_SYSTEM_NAME).stringValue());
		assertEquals(OS_WINDOWS, expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_OS_NAME).stringValue());
		assertEquals("true", expr.lookup(WorkerSpecGlueConstants.PREEMPTION_ENABLED).toString());
		assertEquals("true", expr.lookup(WorkerSpecGlueConstants.GLUE_CE_POLICY_PREEMPTION).toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("names").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("OtherNames").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("TestNames").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("DataNames").toString());
		assertEquals("[name=\"Java\";version=\"1.6\"]", expr.lookup(WorkerSpecGlueConstants.SOFTWARE).toString());
		assertEquals("[name=\"Java\";version=\"1.6\"]", expr.lookup("ApplicationEnvironment").toString());
		assertEquals("{\"Java\"}", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_APPLICATION_SOFTWARE_RUNTIME_ENVIRONMENT).toString());
	}
	
	@Test
	public void testMultiLevelMapping() throws CompilerException{
		List<WorkerSpecification> specs = SDFClassAdsSemanticAnalyzer.compile( MACHINE6_SDF );
		assertNotNull(specs);
		
		JDLTagsPublisher.loadGLUETags(VALID_TAGS_FILE);

		WorkerSpecification workerSpec = specs.get(0);
		RecordExpr expr = JDLTagsPublisher.buildExprWithTagsToPublish(workerSpec.getExpression());

		assertEquals("testserver", expr.lookup(OurGridSpecificationConstants.SERVERNAME).stringValue());
		assertEquals("testuserB", expr.lookup(OurGridSpecificationConstants.USERNAME).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.OS).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_OPERATING_SYSTEM_NAME).stringValue());
		assertEquals(OS_LINUX, expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_OS_NAME).stringValue());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.MAIN_MEMORY).toString());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.EXECUTION_ENVIRONMENT_MAIN_MEMORY_SIZE).toString());
		assertEquals("128", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_MAIN_MEMORY_RAM_SIZE).toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("names").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("OtherNames").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("TestNames").toString());
		assertEquals("{\"name1\",\"names2\"}", expr.lookup("DataNames").toString());
		assertEquals("[name=\"Java\";data={d};d=[elements={a};a=[name=\"Test ...\";oname=\"Test ...\"];elements2={a}];version=\"1.6\";datas={d}]", expr.lookup(WorkerSpecGlueConstants.SOFTWARE).toString());
		assertEquals("[name=\"Java\";data={d};d=[elements={a};a=[name=\"Test ...\";oname=\"Test ...\"];elements2={a}];version=\"1.6\";datas={d}]", expr.lookup("ApplicationEnvironment").toString());
		assertEquals("{\"Java\"}", expr.lookup(WorkerSpecGlueConstants.GLUE_HOST_APPLICATION_SOFTWARE_RUNTIME_ENVIRONMENT).toString());
	}
}
