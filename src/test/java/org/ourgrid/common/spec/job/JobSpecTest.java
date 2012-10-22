package org.ourgrid.common.spec.job;

import org.junit.Test;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;


/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class JobSpecTest {
	
	/**
	 * 
	 */
	@Test(expected=AssertionError.class)
	public final void testJobSpecNullTasks() {

		TaskSpecification task = null;
		new JobSpecification( task, task );
	}
}