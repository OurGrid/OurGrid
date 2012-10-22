package org.ourgrid.common.spec.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.JDLSemanticAnalyzer;



/**
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class JDLSemanticAnalyzerTest implements JDLTests {
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=NullPointerException.class)
	public final void testCompileNullJDL() throws CompilerException {

		JDLSemanticAnalyzer.compileJDL( null );
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileDeprecatedUnsupportedCheckpointable() throws CompilerException {

		JDLSemanticAnalyzer.compileJDL( UNSUPPORTED_JOB_CHECKPOINTABLE );
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileCollection() throws CompilerException {
	
		List<JobSpecification> jobSpecs = JDLSemanticAnalyzer.compileJDL( JOB_COLLECTION );
		
		assertNotNull(jobSpecs);
		assertFalse(jobSpecs.isEmpty());

		List<TaskSpecification> taskSpecs = jobSpecs.get(0).getTaskSpecs();
		assertNotNull(taskSpecs);
		assertFalse(taskSpecs.isEmpty());
		
		Iterator<TaskSpecification> tasksIterator = taskSpecs.iterator();
		
		while ( tasksIterator.hasNext() ) {
			TaskSpecification taskSpec = (TaskSpecification) tasksIterator.next();
			assertNotNull(taskSpec);
		}
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileCollectionWithParametric() throws CompilerException {
	
		JDLSemanticAnalyzer.compileJDL( JOB_COLLECTION_WITH_PARAMETRIC_NODE );
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileCollectionWithInvalidNode() throws CompilerException {
	
		JDLSemanticAnalyzer.compileJDL( JOB_COLLECTION_WITH_INVALID_NODE );
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileUnsuportedDAG() throws CompilerException {
	
		JDLSemanticAnalyzer.compileJDL( UNSUPPORTED_JOB_DAG );
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileUnsuportedInteractive() throws CompilerException {

		JDLSemanticAnalyzer.compileJDL( UNSUPPORTED_JOB_INTERACTIVE );
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileUnsuportedMPI() throws CompilerException {

		JDLSemanticAnalyzer.compileJDL( UNSUPPORTED_JOB_MPI );
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileUnsuportedMultiple() throws CompilerException {

		JDLSemanticAnalyzer.compileJDL( UNSUPPORTED_JOB_MULTIPLE );
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileParametric() throws CompilerException {
		
		List<JobSpecification> jobSpecs = JDLSemanticAnalyzer.compileJDL( JOB_PARAMETRIC );
		assertNotNull(jobSpecs);
		assertFalse(jobSpecs.isEmpty());
		
		JobSpecification jobSpec = jobSpecs.get(0);
		assertNotNull(jobSpec.getRequirements());
		assertFalse(jobSpec.getRequirements().length() == 0);
		
		List<TaskSpecification> taskSpecs = jobSpec.getTaskSpecs();
		assertNotNull(taskSpecs);
		assertFalse(taskSpecs.isEmpty());
		
		Iterator<TaskSpecification> tasksIterator = taskSpecs.iterator();
		
		int parameters = 10000;
		int parameterStart = 1000;
		int parameterStep = 10;

		assertTrue(tasksIterator.hasNext());
		assertEquals("Parametric Job", jobSpec.getLabel());
		for ( int p = parameterStart; p < parameters; p += parameterStep) {
			TaskSpecification taskSpec = tasksIterator.next();
			String pString = Integer.toString( p );
			assertEquals("cms_sim.exe < input".concat( pString ).concat( ".txt > myoutput" ).concat( pString ).concat( ".txt 2> myerror" ).concat( pString ).concat( ".txt" ), taskSpec.getRemoteExec());
			assertNull(taskSpec.getSabotageCheck());
			assertFalse(taskSpec.getExpression().length() == 0);
			List<IOEntry> entries = taskSpec.getInitBlock().getEntry("");
			assertEquals("PUT /home/cms/cms_sim.exe to cms_sim.exe", entries.get( 0 ).toString());
			assertEquals("PUT /home/cms/data/input".concat( pString ).concat( ".txt to input" ).concat( pString ).concat( ".txt" ), entries.get( 1 ).toString());
			entries = taskSpec.getFinalBlock().getEntry("");
			assertEquals("GET myoutput".concat( pString ).concat( ".txt to /tmp/myoutput" ).concat( pString ).concat( ".txt" ), entries.get( 0 ).toString());
			assertEquals("GET myerror".concat( pString ).concat( ".txt to /tmp/myerror" ).concat( pString ).concat( ".txt" ), entries.get( 1 ).toString());
		}
		assertFalse(tasksIterator.hasNext());
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileParametricList() throws CompilerException {

		List<JobSpecification> jobSpecs = JDLSemanticAnalyzer.compileJDL( JOB_PARAMETRIC_LIST );
		assertNotNull(jobSpecs);
		assertFalse(jobSpecs.isEmpty());

		JobSpecification jobSpec = jobSpecs.get(0);
		List<TaskSpecification> taskSpecs = jobSpec.getTaskSpecs();
		assertNotNull(taskSpecs);
		assertFalse(taskSpecs.isEmpty());
		
		Iterator<TaskSpecification> tasksIterator = taskSpecs.iterator();
		
		String[] parametersList = {"raw", "d0", "d1", "d2"};
		
		assertTrue(tasksIterator.hasNext());
		assertEquals("Parametric Job", jobSpec.getLabel());
		
		for ( String pString : parametersList ) {
			TaskSpecification taskSpec = tasksIterator.next();
			assertEquals("cms_sim.exe < input".concat( pString ).concat( ".txt > myoutput" ).concat( pString ).concat( ".txt 2> myerror" ).concat( pString ).concat( ".txt" ), taskSpec.getRemoteExec());
			assertNull(taskSpec.getSabotageCheck());
			assertFalse(taskSpec.getExpression().length() == 0);
			List<IOEntry> entries = taskSpec.getInitBlock().getEntry("");
			assertEquals("PUT /home/cms/cms_sim.exe to cms_sim.exe", entries.get( 0 ).toString());
			assertEquals("PUT /home/cms/data/input".concat( pString ).concat( ".txt to input" ).concat( pString ).concat( ".txt" ), entries.get( 1 ).toString());
			entries = taskSpec.getFinalBlock().getEntry("");
			assertEquals("GET myoutput".concat( pString ).concat( ".txt to /tmp/myoutput" ).concat( pString ).concat( ".txt" ), entries.get( 0 ).toString());
			assertEquals("GET myerror".concat( pString ).concat( ".txt to /tmp/myerror" ).concat( pString ).concat( ".txt" ), entries.get( 1 ).toString());
		}
		assertFalse(tasksIterator.hasNext());
		
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileDeprecatedUnsuportedPartitionable() throws CompilerException {

		JDLSemanticAnalyzer.compileJDL( UNSUPPORTED_JOB_PARTITIONABLE);
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 * @throws IOException 
	 */
	@Test
	public final void testCompileDiffJDL() throws CompilerException {

		List<JobSpecification> jobSpecs = JDLSemanticAnalyzer.compileJDL( DIFF_JOB );
		assertNotNull(jobSpecs);
		assertFalse(jobSpecs.isEmpty());
		String path = new File( BROKER_ACCEPTANCE_TEST_DIR ).getAbsolutePath();
		JobSpecification jobSpec = jobSpecs.get(0);
		List<TaskSpecification> taskSpecs = jobSpec.getTaskSpecs();
		TaskSpecification taskSpec = taskSpecs.get(0);
		assertEquals("Diff Job", jobSpec.getLabel());
		assertEquals("diff file1.txt file2.txt", taskSpec.getRemoteExec());
		assertNull(taskSpec.getSabotageCheck());
		assertFalse(taskSpec.getExpression().length() == 0);
		List<IOEntry> entries = taskSpec.getInitBlock().getEntry("");
		assertEquals("PUT " + path + "/file1.txt to file1.txt", entries.get( 0 ).toString());
		assertEquals("PUT " + path + "/file2.txt to file2.txt", entries.get( 1 ).toString());
		assertNull(taskSpec.getFinalBlock().getEntry(""));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileEchoJDL() throws CompilerException {

		List<JobSpecification> jobSpecs = JDLSemanticAnalyzer.compileJDL( ECHO_JOB );
		assertNotNull(jobSpecs);
		assertFalse(jobSpecs.isEmpty());
		
		JobSpecification jobSpec = jobSpecs.get(0);
		assertNotNull( jobSpec.getRequirements() );
		assertFalse( jobSpec.getRequirements().length() == 0 );
		
		List<TaskSpecification> taskSpecs = jobSpec.getTaskSpecs();
		TaskSpecification taskSpec = taskSpecs.get(0);
		assertEquals("Echo Job", jobSpec.getLabel());
		assertEquals("echo Hello World", taskSpec.getRemoteExec());
		assertNull(taskSpec.getSabotageCheck());
		assertFalse(taskSpec.getExpression().length() == 0);
		assertNull(taskSpec.getInitBlock().getEntry(""));
		assertNull(taskSpec.getFinalBlock().getEntry(""));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 * @throws IOException 
	 */
	@Test
	public final void testCompileJavaIOJDL() throws CompilerException, IOException {

		List<JobSpecification> jobSpecs = JDLSemanticAnalyzer.compileJDL( JAVA_IO_JOB );
		assertNotNull(jobSpecs);
		assertFalse(jobSpecs.isEmpty());
		String inputPath = new File(BROKER_ACCEPTANCE_TEST_DIR).getAbsolutePath();
		String outputPath = new File(JDL_TEST_DIR).getAbsolutePath();
		JobSpecification jobSpec = jobSpecs.get(0);
		List<TaskSpecification> taskSpecs = jobSpec.getTaskSpecs();
		TaskSpecification taskSpec = taskSpecs.get(0);
		assertEquals("Java IO Job", jobSpec.getLabel());
		assertEquals("java Class > remoteFile1.txt 2> remoteFile2.txt", taskSpec.getRemoteExec());
		assertEquals("echo", taskSpec.getSabotageCheck());
		assertFalse(taskSpec.getExpression().length() == 0);
		List<IOEntry> entries = taskSpec.getInitBlock().getEntry("");
		assertEquals("PUT " + inputPath + "/Class.class to Class.class", entries.get( 0 ).toString());
		entries = taskSpec.getFinalBlock().getEntry("");
		assertEquals("GET remoteFile1.txt to " + outputPath + "/remoteFile1.txt", entries.get( 0 ).toString());
		assertEquals("GET remoteFile2.txt to " + outputPath + "/remoteFile2.txt", entries.get( 1 ).toString());
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 * @throws IOException 
	 */
	@Test
	public final void testCompileJavaJDL() throws CompilerException, IOException {

		List<JobSpecification> jobSpecs = JDLSemanticAnalyzer.compileJDL( JAVA_JOB );
		assertNotNull(jobSpecs);
		assertFalse(jobSpecs.isEmpty());
		String path = new File(BROKER_ACCEPTANCE_TEST_DIR).getAbsolutePath();
		JobSpecification jobSpec = jobSpecs.get(0);
		List<TaskSpecification> taskSpecs = jobSpec.getTaskSpecs();
		TaskSpecification taskSpec = taskSpecs.get(0);
		assertEquals("Java Job", jobSpec.getLabel());
		assertEquals("java Class", taskSpec.getRemoteExec());
		assertNull(taskSpec.getSabotageCheck());
		assertFalse(taskSpec.getExpression().length() == 0);
		List<IOEntry> entries = taskSpec.getInitBlock().getEntry("");
		assertEquals("PUT " + path + "/Class.class to Class.class", entries.get( 0 ).toString());
		assertNull(taskSpec.getFinalBlock().getEntry(""));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 * @throws IOException 
	 */
	@Test
	public final void testCompileJavaOutputJDL() throws CompilerException, IOException {

		List<JobSpecification> jobSpecs = JDLSemanticAnalyzer.compileJDL( JAVA_OUTPUT_JOB );
		assertNotNull(jobSpecs);
		assertFalse(jobSpecs.isEmpty());
		String path = new File(BROKER_ACCEPTANCE_TEST_DIR).getAbsolutePath();
		JobSpecification jobSpec = jobSpecs.get(0);
		List<TaskSpecification> taskSpecs = jobSpec.getTaskSpecs();
		TaskSpecification taskSpec = taskSpecs.get(0);
		assertEquals("Java Job", jobSpec.getLabel());
		assertEquals("java Class > remoteFile1.txt", taskSpec.getRemoteExec());
		assertNull(taskSpec.getSabotageCheck());
		assertFalse(taskSpec.getExpression().length() == 0);
		List<IOEntry> entries = taskSpec.getInitBlock().getEntry("");
		assertEquals("PUT " + path + "/Class.class to Class.class", entries.get( 0 ).toString());
		entries = taskSpec.getFinalBlock().getEntry("");
		assertEquals("GET remoteFile1.txt to " + new File( "." ).getCanonicalPath() + "/test/jdl/remoteFile1.txt", entries.get( 0 ).toString());
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileMalformedJob1() throws CompilerException {
	
		JDLSemanticAnalyzer.compileJDL( WRONG_SYNTAX_JOB1 );
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileMalformedJob2() throws CompilerException {
	
		JDLSemanticAnalyzer.compileJDL( WRONG_SYNTAX_JOB2 );
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSemanticAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileSemanticallyMalformedJob1() throws CompilerException {
	
		JDLSemanticAnalyzer.compileJDL( WRONG_SEMANTIC_JOB1);
	}

}
