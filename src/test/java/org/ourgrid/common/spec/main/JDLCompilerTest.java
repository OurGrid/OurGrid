/**
 * 
 */
package org.ourgrid.common.spec.main;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.JDLCompiler;
import org.ourgrid.common.specification.main.CommonCompiler.FileType;


/**
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class JDLCompilerTest {

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLCompiler#compile(java.lang.String, java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=AssertionError.class)
	public final void testCompileNullFileName() throws CompilerException {

		new JDLCompiler().compile( null, FileType.JDL );
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLCompiler#compile(java.lang.String, java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=AssertionError.class)
	public final void testCompileNullFileType() throws CompilerException {

		new JDLCompiler().compile( "", null );
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLCompiler#compile(java.lang.String, java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test(expected=CompilerException.class)
	public final void testCompileUnknownFileType() throws CompilerException {

		new JDLCompiler().compile( "", FileType.JDL );
	}
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.main.JDLCompiler#compile(java.lang.String, java.lang.String)}.
	 * @throws CompilerException 
	 */
	@Test
	public final void testCompileJDLFileType() throws CompilerException {
		
		JDLCompiler compiler = new JDLCompiler();
		compiler.compile( "test/jdl/echo_job.jdl", FileType.JDL );
		List result = compiler.getResult();
		assertTrue( JobSpecification.class.isInstance( result.get( 0 ) ) );
	}
}
