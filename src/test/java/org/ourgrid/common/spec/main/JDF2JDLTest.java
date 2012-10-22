package org.ourgrid.common.spec.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.naming.directory.InvalidAttributeValueException;

import org.junit.Test;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.JDF2JDL;

/**
 * Unit tests for JDF to JDL translator.
 *
 */
public class JDF2JDLTest {
	
	private static final String $JOB = "$JOB";
	private final String ECHO_JOB_AD = "[    nodes = {        [            Executable = \"echo\";            Arguments = \"Hello World\"        ],        [            Executable = \"echo\";            Arguments = \"Hello World\"        ],        [            Executable = \"echo\";            Arguments = \"Hello World\"        ],        [            Executable = \"echo\";            Arguments = \"Hello World\"        ]    };    Name = \"EchoJob\"]";
	private final String FACTORING_JOB_AD = "[    nodes = {        [            Executable = \"nice\";            Arguments = \"java -cp . Fat 3 261147332 6819792792357414911 output-$JOB.0\";            InputSandbox = {                \"/local/david/workspace_intevol/Ourgrid-NewArchitecture/test/resources/Fat.class\"            };            OutputSandbox = {                \"output-$JOB.0\"            };            OutputSandboxDestURI = {                \"/local/david/workspace_intevol/Ourgrid-NewArchitecture/test/resources/output-$JOB.0\"            }        ],        [            Executable = \"nice\";            Arguments = \"java -cp . Fat 261147332 522294661 6819792792357414911 output-$JOB.1\";            InputSandbox = {                \"/local/david/workspace_intevol/Ourgrid-NewArchitecture/test/resources/Fat.class\"            };            OutputSandbox = {                \"output-$JOB.1\"            };            OutputSandboxDestURI = {                \"/local/david/workspace_intevol/Ourgrid-NewArchitecture/test/resources/output-$JOB.1\"            }        ],        [            Executable = \"nice\";            Arguments = \"java -cp . Fat 522294661 783441990 6819792792357414911 output-$JOB.2\";            InputSandbox = {                \"/local/david/workspace_intevol/Ourgrid-NewArchitecture/test/resources/Fat.class\"            };            OutputSandbox = {                \"output-$JOB.2\"            };            OutputSandboxDestURI = {                \"/local/david/workspace_intevol/Ourgrid-NewArchitecture/test/resources/output-$JOB.2\"            }        ]    };    Name = \"FatoraJob\"]";
	private final String SDT_JOB_AD = "[    nodes = {        [            Executable = \"Fat.class\";            StdInput = \"input.dat\";            StdOutput = \"out.dat\";            StdError = \"err.dat\";            InputSandbox = {                \"/local/david/workspace_intevol/Ourgrid-NewArchitecture/test/jdl/Fat.class\",                \"/local/david/workspace_intevol/Ourgrid-NewArchitecture/test/jdl/input.dat\"            };            OutputSandbox = {                \"out.dat\"            };            OutputSandboxDestURI = {                \"/local/david/workspace_intevol/Ourgrid-NewArchitecture/test/jdl/out.dat\"            }        ]    };    Name = \"myjob2\"]";
	private final String SDT_JOB_AD_APPEND = "[    nodes = {        [            Executable = \"Fat.class\";            StdInput = \"input.dat\";            Arguments = \">> out.dat 2>> err.dat\";            InputSandbox = {                \"/local/david/workspace_intevol/Ourgrid-NewArchitecture/test/jdl/Fat.class\",                \"/local/david/workspace_intevol/Ourgrid-NewArchitecture/test/jdl/input.dat\"            };            OutputSandbox = {                \"out.dat\"            };            OutputSandboxDestURI = {                \"/local/david/workspace_intevol/Ourgrid-NewArchitecture/test/jdl/out.dat\"            }        ]    };    Name = \"myjob2\"]";
	
	private static int jobID = 0;
	
	//------ Used to change error stream ------
	private static PrintStream systemErr;
	private static PrintStream previousStream;
	protected static StringBuilder errBuilder = new StringBuilder();
	
	private void changeErrStream(){
		previousStream = System.err;
		systemErr = new PrintStream( new OutputStream() {
			
			@Override
			public void write( int b ) throws IOException {
		
				errBuilder.append( (char)b );
				
			}
		});
		
		System.setErr(systemErr);
	}
	
	private static void resetSystemErr() {
		System.setErr(previousStream);
		errBuilder = new StringBuilder();
	}
	
	@Test
	public void testInvalidJDFFilePath(){
		changeErrStream();
		JDF2JDL.main(new String[]{"e"});
		assertTrue(errBuilder.toString().length() > 0);
		resetSystemErr();
		
		changeErrStream();
		JDF2JDL.main(new String[]{""});
		assertTrue(errBuilder.toString().length() > 0);
		resetSystemErr();
		
		//File without permission
		changeErrStream();
		JDF2JDL.main(new String[]{"test"+File.separator+"jdl"+File.separator+"job_without_permission.jdf"});
		assertTrue(errBuilder.toString().length() > 0);
		resetSystemErr();
		
		changeErrStream();
		JDF2JDL.main(new String[]{null});
		assertTrue(errBuilder.toString().length() > 0);
		resetSystemErr();
	}
	
	@Test
	public void testJDFWithInvalidSyntax(){
		changeErrStream();
		JDF2JDL.main(new String[]{"test"+File.separator+"jdl"+File.separator+"job_with_invalid_syntax.jdf"});
		assertTrue(errBuilder.toString().length() > 0);
		resetSystemErr();
	}
	
	@Test
	public void testJDFWithMultipleCommands(){
		changeErrStream();
		JDF2JDL.main(new String[]{"test"+File.separator+"jdl"+File.separator+"job_with_multiple_commands.jdf"});
		System.out.println(errBuilder.toString());
		assertTrue(errBuilder.toString().contains("more than one executable command"));
		resetSystemErr();
	}
	
	@Test
	public void testEchoJob() throws CompilerException, InvalidAttributeValueException, IOException{
		String jdlOutputFile = "test_out.jdl";
		
		changeErrStream();
		JDF2JDL.main(new String[]{"test"+File.separator+"resources"+File.separator+"EchoJob.jdf", jdlOutputFile});
		File file = new File(jdlOutputFile);
		assertTrue(file.exists());
		assertTrue(file.canRead());
		FileReader reader = new FileReader(file);
		BufferedReader r = new BufferedReader(reader);
		StringBuilder out = new StringBuilder();
		while(r.ready()){
			out.append(r.readLine());
		}
		assertEquals(0, errBuilder.toString().length());
		resetSystemErr();
		assertEquals(ECHO_JOB_AD, out.toString());
		
		file.delete();
		reader.close();
		
		jobID++;
	}
	
	@Test
	public void testJobWithMultipleArguments() throws CompilerException, InvalidAttributeValueException, IOException{
		String jdlOutputFile = "test_out.jdl";
		
		changeErrStream();
		JDF2JDL.main(new String[]{"test"+File.separator+"resources"+File.separator+"FatoraJob.jdf", jdlOutputFile});
		File file = new File(jdlOutputFile);
		assertTrue(file.exists());
		assertTrue(file.canRead());
		FileReader reader = new FileReader(file);
		BufferedReader r = new BufferedReader(reader);
		StringBuilder out = new StringBuilder();
		while(r.ready()){
			out.append(r.readLine());
		}
		
		assertEquals(0, errBuilder.toString().length());
		resetSystemErr();
		assertEquals(FACTORING_JOB_AD.replace($JOB, jobID+""), out.toString());
		jobID++;
		
		file.delete();
		reader.close();
	}
	
	@Test
	public void testJobWithStdInOutAndErr() throws CompilerException, InvalidAttributeValueException, IOException{
		String jdlOutputFile = "test_out.jdl";
		
		changeErrStream();
		JDF2JDL.main(new String[]{"test"+File.separator+"jdl"+File.separator+"job_with_std_devices.jdf", jdlOutputFile});
		File file = new File(jdlOutputFile);
		assertTrue(file.exists());
		assertTrue(file.canRead());
		FileReader reader = new FileReader(file);
		BufferedReader r = new BufferedReader(reader);
		StringBuilder out = new StringBuilder();
		while(r.ready()){
			out.append(r.readLine());
		}
		
		assertEquals(0, errBuilder.toString().length());
		resetSystemErr();
		assertEquals(SDT_JOB_AD, out.toString());
		jobID++;
		
		file.delete();
		reader.close();
	}
	
	@Test
	public void testJobWithStdInOutErrAppended() throws CompilerException, InvalidAttributeValueException, IOException{
		String jdlOutputFile = "test_out.jdl";
		
		changeErrStream();
		JDF2JDL.main(new String[]{"test"+File.separator+"jdl"+File.separator+"job_with_std_devices_append.jdf", jdlOutputFile});
		File file = new File(jdlOutputFile);
		assertTrue(file.exists());
		assertTrue(file.canRead());
		FileReader reader = new FileReader(file);
		BufferedReader r = new BufferedReader(reader);
		StringBuilder out = new StringBuilder();
		while(r.ready()){
			out.append(r.readLine());
		}

		assertEquals(0, errBuilder.toString().length());
		resetSystemErr();
		assertEquals(SDT_JOB_AD_APPEND, out.toString());
		jobID++;
		
		file.delete();
		reader.close();
	}
}
