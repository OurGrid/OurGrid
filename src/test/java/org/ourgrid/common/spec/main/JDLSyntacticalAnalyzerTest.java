package org.ourgrid.common.spec.main;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer;


/**
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public class JDLSyntacticalAnalyzerTest implements JDLTests{
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=NullPointerException.class)
	public final void testCompileNullJDL() throws CompilerException {

		JDLSyntacticalAnalyzer.compileJDL( null );
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileDiffJDL() throws CompilerException {

		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( DIFF_JOB ));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileJavaIOJDL() throws CompilerException {

		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( JAVA_IO_JOB ));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileJavaJDL() throws CompilerException {

		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( JAVA_JOB ));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileJavaOutputJDL() throws CompilerException {

		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( JAVA_OUTPUT_JOB ));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileDeprecatedUnsupportedCheckpointable() throws CompilerException {

		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( UNSUPPORTED_JOB_CHECKPOINTABLE ));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileUnsuportedCollection() throws CompilerException {
	
		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( JOB_COLLECTION ));
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileUnsuportedDAG() throws CompilerException {
	
		JDLSyntacticalAnalyzer.compileJDL( UNSUPPORTED_JOB_DAG );
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileUnsuportedInteractive() throws CompilerException {

		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( UNSUPPORTED_JOB_INTERACTIVE ));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileUnsuportedMPI() throws CompilerException {

		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( UNSUPPORTED_JOB_MPI ));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileUnsuportedMultiple() throws CompilerException {

		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( UNSUPPORTED_JOB_MULTIPLE ));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileUnsuportedParametric() throws CompilerException {

		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( JOB_PARAMETRIC ));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileUnsuportedParametricList() throws CompilerException {

		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( JOB_PARAMETRIC_LIST ));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileDeprecatedUnsuportedPartitionable() throws CompilerException {

		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( UNSUPPORTED_JOB_PARTITIONABLE));
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileMalformedJob1() throws CompilerException {

		JDLSyntacticalAnalyzer.compileJDL( WRONG_SYNTAX_JOB1 );
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileMalformedJob2() throws CompilerException {
	
		JDLSyntacticalAnalyzer.compileJDL( WRONG_SYNTAX_JOB2 );
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLSyntacticalAnalyzer#compileJDL(java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileSemanticallyMalformedJob1() throws CompilerException {
	
		assertNotNull(JDLSyntacticalAnalyzer.compileJDL( WRONG_SEMANTIC_JOB1));
	}
}
