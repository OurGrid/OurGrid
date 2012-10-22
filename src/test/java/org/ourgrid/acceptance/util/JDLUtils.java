package org.ourgrid.acceptance.util;


/**
 * 
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface JDLUtils {
	
	public static final String JDL_DIR_PATH = "test/jdl";
	
	static String ECHO_JOB = JDL_DIR_PATH + "/echo_job.jdl";
	static String JAVA_JOB = JDL_DIR_PATH + "/java_job.jdl";
	static String JAVA_IO_JOB = JDL_DIR_PATH + "/java_io_job.jdl";
	static String JAVA_OUTPUT_JOB = JDL_DIR_PATH + "/java_output_job.jdl";
	static String DIFF_JOB = JDL_DIR_PATH + "/diff_job.jdl";
	
//	/**
//	 * Simple echo job with no input or output file nor requirements and rank expressions.
//	 */
//	static String ECHO_JOB = "[" +
//	"Name = \"Echo Job\";" +
//	"Type = \"Job\";" +
//	"JobType = \"normal\";" +
//	"Executable = \"echo\";" +
//	"Arguments = \"Hello World\";" +
//	"Requirements = true;" +
//	"Rank = 0;"+ 
//	"]";
//
//	/**
//	 * Java job with a one input file named Class.class and and a requirement to run on 
//	 * machines with more than 256MB of main memory.
//	 */
//	static String JAVA_JOB = "[" +
//	"Name = \"Java Job\";" +
//	"Type = \"Job\";" +
//	"JobType = \"normal\";" +
//	"InputSandbox = {\"Class.class\"};" +
//	"Executable = \"java\";" +
//	"Arguments = \"Class\";" +
//	"Requirements = other.GlueHostMainMemoryRAMSize == 256 || other.ExecutionEnvironmentMainMemorySize == 256;" +
//	"Rank = 0;"+ 
//	"]";
//
//	static String JAVA_IO_JOB = "[" +
//	"Name = \"Java IO Job\";" +
//	"Type = \"Job\";" +
//	"JobType = \"normal\";" +
//	"InputSandbox = { \"Class.class\" };" +
//	"OutputSandbox = { \"remoteFile1.txt\", \"remoteFile2.txt\" };" +
//	"Executable = \"java\";" +
//	"Arguments = \"Class\";" +
//	"StdOutput = \"remoteFile1.txt\";" +
//	"StdError = \"remoteFile2.txt\";" +
//	"Epilogue = \"echo\";" +
//	"Requirements = other.GlueHostMainMemoryRAMSize == 256 || other.ExecutionEnvironmentMainMemorySize == 256;" +
//	"Rank = 0;"+ 
//	"]";
//
//	static String JAVA_OUTPUT_JOB = "[" +
//	"Name = \"Java Job\";" +
//	"Type = \"Job\";" +
//	"JobType = \"normal\";" +
//	"InputSandbox = { \"Class.class\" };" +
//	"OutputSandbox = { \"remoteFile1.txt\" };" +
//	"Executable = \"java\";" +
//	"Arguments = \"Class \";" +
//	"StdOutput = \"remoteFile1.txt\";" +
//	"Requirements = other.GlueHostMainMemoryRAMSize == 256 || other.ExecutionEnvironmentMainMemorySize == 256;" +
//	"Rank = 0;"+ 
//	"]";
//
//	static String DIFF_JOB = "[" +
//	"Name = \"Java Job\";" +
//	"Type = \"Job\";" +
//	"JobType = \"normal\";" +
//	"InputSandbox = { \"file1.txt\", \"file2.txt\" };" +
//	"Executable = \"diff\";" +
//	"Arguments = \"file1.txt file2.txt\";" +
//	"Requirements = other.GlueHostMainMemoryRAMSize == 256 || other.ExecutionEnvironmentMainMemorySize == 256;" +
//	"Rank = 0;"+ 
//	"]";

}
