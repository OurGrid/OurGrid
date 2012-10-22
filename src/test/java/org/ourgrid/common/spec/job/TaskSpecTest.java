package org.ourgrid.common.spec.job;

import static org.junit.Assert.assertNull;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.ourgrid.common.specification.job.InputBlock;
import org.ourgrid.common.specification.job.OutputBlock;
import org.ourgrid.common.specification.job.TaskSpecification;


/**
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TaskSpecTest {
	
	/**
	 * Test method for {@link org.ourgrid.common.specification.job.TaskSpecification#TaskSpec(String, String, InputBlock, OutputBlock, String)}.
	 */
	@Test
	public final void testTaskSpecNullLabel() {
		InputBlock inputBlock = EasyMock.createStrictMock( InputBlock.class );
		OutputBlock outputBlock = EasyMock.createStrictMock( OutputBlock.class );
		new TaskSpecification("", "", inputBlock, outputBlock, "");
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.job.TaskSpecification#TaskSpec(String, String, InputBlock, OutputBlock, String)}.
	 */
	@Test(expected=AssertionError.class)
	public final void testTaskSpecNullExpression() {
		InputBlock inputBlock = EasyMock.createStrictMock( InputBlock.class );
		OutputBlock outputBlock = EasyMock.createStrictMock( OutputBlock.class );
		new TaskSpecification(null, "", inputBlock, outputBlock, "");
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.job.TaskSpecification#TaskSpec(String, String, InputBlock, OutputBlock, String)}.
	 */
	@Test(expected=AssertionError.class)
	public final void testTaskSpecNullExecutable() {
		InputBlock inputBlock = EasyMock.createStrictMock( InputBlock.class );
		OutputBlock outputBlock = EasyMock.createStrictMock( OutputBlock.class );
		new TaskSpecification("", null, inputBlock, outputBlock, "");
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.job.TaskSpecification#TaskSpec(String, String, InputBlock, OutputBlock, String)}.
	 */
	@Test(expected=AssertionError.class)
	public final void testTaskSpecNullInputBlock() {
		OutputBlock outputBlock = EasyMock.createStrictMock( OutputBlock.class );
		new TaskSpecification("", "", null, outputBlock, "");
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.job.TaskSpecification#TaskSpec(String, String, InputBlock, OutputBlock, String)}.
	 */
	@Test(expected=AssertionError.class)
	public final void testTaskSpecNullOutputBlock() {
		InputBlock inputBlock = EasyMock.createStrictMock( InputBlock.class );
		new TaskSpecification("", "", inputBlock, null, "");
	}

	/**
	 * Test method for {@link org.ourgrid.common.specification.job.TaskSpecification#TaskSpec(String, String, InputBlock, OutputBlock, String)}.
	 */
	@Test
	public final void testTaskSpecNullEpilogue() {
		InputBlock inputBlock = EasyMock.createStrictMock( InputBlock.class );
		OutputBlock outputBlock = EasyMock.createStrictMock( OutputBlock.class );
		assertNull( new TaskSpecification("", "", inputBlock, outputBlock, null).getSabotageCheck() );
	}
}
