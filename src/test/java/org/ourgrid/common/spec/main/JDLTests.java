package org.ourgrid.common.spec.main;

import java.io.File;



/**
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface JDLTests {

	static final String JDL_TEST_DIR = "test/jdl/";
	static final String ACCEPTANCE_TEST_DIR = "test/acceptance";
	static final String BROKER_ACCEPTANCE_TEST_DIR = ACCEPTANCE_TEST_DIR+File.separator+"broker"+File.separator;
	static final String DIFF_JOB = JDL_TEST_DIR.concat("diff_job.jdl");
	static final String MATCHER1_JOB = JDL_TEST_DIR.concat("matcher1.jdl");
	static final String MATCHER2_JOB = JDL_TEST_DIR.concat("matcher2.jdl");
	static final String ECHO_JOB = JDL_TEST_DIR.concat("echo_job.jdl");
	static final String JAVA_IO_JOB = JDL_TEST_DIR.concat("java_io_job.jdl");
	static final String JAVA_JOB = JDL_TEST_DIR.concat("java_job.jdl");
	static final String JAVA_OUTPUT_JOB = JDL_TEST_DIR.concat("java_output_job.jdl");
	static final String JOB_COLLECTION = JDL_TEST_DIR.concat("job_collection.jdl");
	static final String JOB_PARAMETRIC_LIST = JDL_TEST_DIR.concat("job_parametric_list.jdl");
	static final String JOB_PARAMETRIC = JDL_TEST_DIR.concat("job_parametric.jdl");
	static final String UNSUPPORTED_JOB_CHECKPOINTABLE = JDL_TEST_DIR.concat("unsupported_deprecated_job_checkpointable.jdl");
	static final String JOB_COLLECTION_WITH_PARAMETRIC_NODE = JDL_TEST_DIR.concat("job_collection_with_parametric.jdl");
	static final String JOB_COLLECTION_WITH_INVALID_NODE = JDL_TEST_DIR.concat("job_collection_with_invalid_nodes.jdl");
	static final String UNSUPPORTED_JOB_DAG = JDL_TEST_DIR.concat("unsupported_job_dag.jdl");
	static final String UNSUPPORTED_JOB_INTERACTIVE = JDL_TEST_DIR.concat("unsupported_job_interactive.jdl");
	static final String UNSUPPORTED_JOB_MPI = JDL_TEST_DIR.concat("unsupported_job_mpi.jdl");
	static final String UNSUPPORTED_JOB_MULTIPLE = JDL_TEST_DIR.concat("unsupported_job_multiple.jdl");
	static final String UNSUPPORTED_JOB_PARTITIONABLE = JDL_TEST_DIR.concat("unsupported_deprecated_job_partitionable.jdl");
	static final String WRONG_SYNTAX_JOB1 = JDL_TEST_DIR.concat("wrong_syntax_job1.jdl");
	static final String WRONG_SYNTAX_JOB2 = JDL_TEST_DIR.concat("wrong_syntax_job2.jdl");
	static final String WRONG_SEMANTIC_JOB1 = JDL_TEST_DIR.concat("wrong_semantic_job1.jdl");
}
