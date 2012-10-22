package org.ourgrid.peer.controller.matcher.jdl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;

import org.glite.jdl.Jdl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ourgrid.common.spec.main.JDLTests;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.main.ClassAdSyntacticalAnalyzerStream;
import org.ourgrid.common.specification.main.JDLSemanticAnalyzer;
import org.ourgrid.common.specification.main.JDLTagsPublisher;
import org.ourgrid.common.specification.main.SDFClassAdsSyntacticalAnalyzer;
import org.ourgrid.common.specification.worker.WorkerSpecificationConstants;
import org.ourgrid.peer.business.controller.matcher.Matcher;
import org.ourgrid.peer.business.controller.matcher.MatcherImpl;

import condor.classad.AttrRef;
import condor.classad.ClassAdParser;
import condor.classad.Constant;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;

/**
 * This class contains unit tests for the peer matcher using JDL to describe jobs and ClassAds to describe
 * machines.
 * @author David Candeia Medeiros Maia
 *
 */
public class NewMatcherImplTest implements JDLTests{
	
	private static Matcher matcher;
	
	private static RecordExpr machine1;
	private static RecordExpr machine2;
	private static RecordExpr machine3;
	private static RecordExpr machine4;
	
	private static String diff_JDL;
	private static String echo_JDL;
	private static String java_io_JDL;
	private static String java_JDL;
	private static String java_output_JDL;
	private static String matcher1_JDL;
	private static String matcher2_JDL;

	@BeforeClass
	public static void setUp() throws Exception {
		
		//Loading tags
		JDLTagsPublisher.loadGLUETags(ACCEPTANCE_TEST_DIR+File.separator+"tags.conf");
		
		//Loading some jobs info
		diff_JDL = JDLSemanticAnalyzer.compileJDL(DIFF_JOB).get(0).getRequirements();
		echo_JDL = JDLSemanticAnalyzer.compileJDL(ECHO_JOB).get(0).getRequirements();
		java_io_JDL = JDLSemanticAnalyzer.compileJDL(JAVA_IO_JOB).get(0).getRequirements();
		java_JDL = JDLSemanticAnalyzer.compileJDL(JAVA_JOB).get(0).getRequirements();
		java_output_JDL = JDLSemanticAnalyzer.compileJDL(JAVA_OUTPUT_JOB).get(0).getRequirements();
		matcher1_JDL = JDLSemanticAnalyzer.compileJDL(MATCHER1_JOB).get(0).getRequirements();
		matcher2_JDL = JDLSemanticAnalyzer.compileJDL(MATCHER2_JOB).get(0).getRequirements();
		
		//Machine expressions
		matcher = new MatcherImpl();
		machine1 = new RecordExpr();
		machine2 = new RecordExpr();
		machine3 = new RecordExpr();
		machine4 = new RecordExpr();
		
		putAttribute(OurGridSpecificationConstants.USERNAME, "\"machine1\"", machine1);
		putAttribute(WorkerSpecificationConstants.OS, "\"linux\"", machine1);
		putAttribute(WorkerSpecificationConstants.SITE_NAME, "\"lsd\"", machine1);
		putAttribute(WorkerSpecificationConstants.MAIN_MEMORY, "200", machine1);
		putAttribute("SO", "\"bla\"", machine1);
		putAttribute(Jdl.REQUIREMENTS, "true", machine1);
		putAttribute(Jdl.RANK, "0", machine1);
		
		putAttribute(OurGridSpecificationConstants.USERNAME, "\"machineWithoutAttributes\"", machine2);
		putAttribute(Jdl.REQUIREMENTS, "True", machine2);
		putAttribute(Jdl.RANK, "0", machine2);
		
		putAttribute(OurGridSpecificationConstants.USERNAME, "\"machine3\"", machine3);
		putAttribute(WorkerSpecificationConstants.OS, "\"windows\"", machine3);
		putAttribute(WorkerSpecificationConstants.SITE_NAME, "\"puc\"", machine3);
		putAttribute(WorkerSpecificationConstants.MAIN_MEMORY, "50", machine3);
		putAttribute(Jdl.REQUIREMENTS, "True", machine3);
		putAttribute(Jdl.RANK, "0", machine3);
		
		putAttribute(OurGridSpecificationConstants.USERNAME, "\"machine4\"", machine4);
		putAttribute(WorkerSpecificationConstants.MAIN_MEMORY, "256", machine4);
		putAttribute(Jdl.REQUIREMENTS, "True", machine4);
		putAttribute(Jdl.RANK, "0", machine4);
	}
	
	private static void putAttribute(String name, String value, RecordExpr record){
		if(value.contains(SDFClassAdsSyntacticalAnalyzer.LIST_SYMBOL)){
			record.insertAttribute(name, new ListExpr(Arrays.asList(value)));
		}else if(value.contains(SDFClassAdsSyntacticalAnalyzer.RECORD_SYMBOL)){
			inserRecordAttribute(name, value, record);
		}else{
			Constant constant = null;
			if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")){
				boolean realValue = Boolean.valueOf(value);
				constant = Constant.getInstance(realValue);
				record.insertAttribute(name, constant);
			}else if(!value.contains("\"")){
				try{
					Integer intValue = Integer.valueOf(value);
					constant = Constant.getInstance(intValue);
					record.insertAttribute(name, constant);
					return;
				}catch(Exception e){
				}
				
				try{
					Long longValue = Long.valueOf(value);
					constant = Constant.getInstance(longValue);
					record.insertAttribute(name, constant);
					return;
				}catch(Exception e){
				}
				
				try{
					Double doubleValue = Double.valueOf(value);
					constant = Constant.getInstance(doubleValue);
					record.insertAttribute(name, constant);
				}catch(Exception e){
					AttrRef ref = new AttrRef(value);
					record.insertAttribute(name, ref);
				}
			}else{
				constant = Constant.getInstance(value.replaceAll("\"", ""));
				record.insertAttribute(name, constant);
			}
		}
	}
	
	private static void inserRecordAttribute(String name, String value, RecordExpr record) {
		ClassAdSyntacticalAnalyzerStream.changeSystemErrToStream();
		RecordExpr expr = (RecordExpr) new ClassAdParser(value).parse();
		String errorMessage = ClassAdSyntacticalAnalyzerStream.getErrorMessage();
		if(errorMessage.length() > 0){
			throw new IllegalArgumentException("Mal formed attribute "+name+" "+errorMessage);
		}
		record.insertAttribute(name, expr);
		ClassAdSyntacticalAnalyzerStream.resetSystemErr();
	}
	
	/**
	 * Test method for {@link org.ourgrid.peer.controller.matcher.NewMatcherImpl#match(String, String)}.
	 */
	@Test(expected=AssertionError.class)
	public void testMatchNullJDLExpression() {
		matcher.match(null, "");
	}
	
	
	/**
	 * Test method for {@link org.ourgrid.peer.controller.matcher.NewMatcherImpl#match(String, String)}.
	 */
	@Test(expected=AssertionError.class)
	public void testMatchNullClassAdExpression() {
		matcher.match("", (String)null);
	}
	
	
	/**
	 * Test method for {@link org.ourgrid.peer.controller.matcher.NewMatcherImpl#match(String, String)}.
	 */
	@Test(expected=AssertionError.class)
	public void testMatchEmptyJDL() {
		matcher.match("", "[ username = \"username1;\" ]");
	}
	
	
	/**
	 * Test method for {@link org.ourgrid.peer.controller.matcher.NewMatcherImpl#match(String, String)}.
	 */
	@Test(expected=AssertionError.class)
	public void testMatchEmptyClassAd() {
		matcher.match("[ username = \"username1;\" ]", "");
	}


	/**
	 * Test method for {@link org.ourgrid.peer.controller.matcher.NewMatcherImpl#match(String, String)}.
	 */
	@Test(expected=AssertionError.class)
	public void testMatchEchoJob() {
		matcher.match("[ username = \"username1;\" ]", "");
	}
	
	@Test
	public void testMatchesWithNotDefinedAttributes() {

		String jobRequirement = "[Requirements = other.siteName != lsd.ufcg.edu.br;Rank = 0]";
		int matched = matcher.match( jobRequirement, machine2.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = other.siteName != lsd.ufcg.edu.br && other.os == linux;Rank = 0]";
		matched = matcher.match( jobRequirement, machine2.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = other.siteName != lsd.ufcg.edu.br && other.os == windows;Rank = 0]";
		matched = matcher.match( jobRequirement, machine2.toString() );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = other.siteName != lsd.ufcg.edu.br || other.os == window;Rank = 0]";
		matched = matcher.match( jobRequirement, machine2.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = other.siteName != lsd.ufcg.edu.br || other. os == windows;Rank = 0]";
		matched = matcher.match( jobRequirement, machine2.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = !other.siteName != lsd.ufcg.edu.br;Rank = 0]";
		matched = matcher.match( jobRequirement, machine2.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = other.mainMemory < 200;Rank = 0]";
		matched = matcher.match( jobRequirement, machine2.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertEquals(-1,matched );

		jobRequirement = "[Requirements = other.mainMemory <= 200;Rank = 0]";
		matched = matcher.match( jobRequirement, machine2.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = other.mainMemory > 200;Rank = 0]";
		matched = matcher.match( jobRequirement, machine2.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = other.mainMemory >= 200;Rank = 0]";
		matched = matcher.match( jobRequirement, machine2.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = !other.siteName != lsd.ufcg.edu.br || other.mainMemory >= 200;Rank = 0]";
		matched = matcher.match( jobRequirement, machine2.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertEquals(-1, matched );
		
		jobRequirement = "[Requirements = !siteName != lsd.ufcg.edu.br || mainMemory >= 200; Rank = 0]";
		matched = matcher.match( jobRequirement, machine2.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine2 + " ? ==> " + matched );
		assertEquals(-1, matched );
	}
	
	@Test
	public void testMatchesWithDefinedAndNotDefinedAttributes() {

		String jobRequirement = "[Requirements = other.siteName != \"lsd.ufcg.edu.br\"; Rank = 0]";
		int matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(0, matched );

		jobRequirement = "[Requirements = other.siteName != \"lsd.ufcg.edu.br\" && other.so == \"linux\";Rank = 0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = other.siteName == \"puc\" && other.OS == \"windows\";Rank = 0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		assertEquals(0, matched );

		jobRequirement = "[Requirements = other.siteName == \"puc\" || other.OS == \"linux\"; Rank = 0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(0, matched );

		jobRequirement = "[Requirements = other.siteName == \"lsd\" || other.OS == \"windows\"; Rank = 0;]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(0, matched );

		jobRequirement = "[Requirements = !(caca != \"lsd\"); Rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = other.mainMemory < 200 || other.caca == \"lsd\"; Rank = 0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(0, matched );

		jobRequirement = "[Requirements = other.mainMemory <= 200; Rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(0, matched );

		jobRequirement = "[Requirements = other.mainMemory > 200; Rank=0;]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = other.mainMemory >= 200; Rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = !(other.siteName != \"lsd.ufcg.edu.br\") || (other.mainMemory >= 200) || ( other.OS == \"windows\" ) || ( other.caca == \"xpto\"); Rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(0, matched );
	}
	
	@Test
	public void testMatchesWithDefinedAndNotDefinedAttributesAndAnyOtherReference() {

		String jobRequirement = "[Requirements = siteName != \"lsd.ufcg.edu.br\"; Rank = 0]";
		int matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = siteName != \"lsd.ufcg.edu.br\" && so == \"linux\";Rank = 0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = siteName == \"puc\" && OS == \"windows\";Rank = 0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = siteName == \"puc\" || OS == \"linux\"; Rank = 0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = siteName == \"lsd\" || OS == \"windows\"; Rank = 0;]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = !(caca != \"lsd\"); Rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = mainMemory < 200 || caca == \"lsd\"; Rank = 0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = mainMemory <= 200; Rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = mainMemory > 200; Rank=0;]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = mainMemory >= 200; Rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );

		jobRequirement = "[Requirements = !(siteName != \"lsd.ufcg.edu.br\") || (mainMemory >= 200) || ( OS == \"windows\" ) || ( caca == \"xpto\"); Rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() + " ? ==> " + matched );
		assertEquals(-1, matched );
	}
	
	/**
	 * This test contains some  positive matches tests. It also contains some cases
	 * in which different cases are used. 
	 */
	@Test
	public void testGoodMatches() {
		
		String jobRequirement = "[Requirements = other.os == \"linux\"; Rank=0]";
		int matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0, matched );

		jobRequirement = "[Requirements = other.siteName == \"lsd\"; Rank = 0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0, matched );
		
		jobRequirement = "[Requirements = other.SItename == \"lsd\" && other.os == \"linux\"; Rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = other.siteName == \"xpto\" || other.os == \"linux\"; rank=0;]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = other.sitename != \"xpto\" && other.os == \"linux\";rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = ( ( other.sitename == \"ucsd\" || other.sitename == \"lsd\" ) && other.os == \"linux\");rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = ( other.os == \"linux\" &&  !( other.sitename == \"ucsd\" ) ); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = other.mainMemory > 100; rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = ((other.siteName == \"ucsd\"|| other.sitename == \"lsd\" ) && other.mainMemory > 100 ); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = ( other.mainMemory < 300 ); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = ( (other.sitename == \"ucsd\" || other.sitename == \"lsd\" )&& ( other.mainmemory > 100 && other.mainmemory< 300 )); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = ( ( other.sitename == \"ucsd\" || other.sitename == \"lsd\" ) && other.mainmemory >= 200 ); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = ( ( other.sitename == \"ucsd\" || other.sitename == \"lsd\" ) && other.mainmemory <= 200 ); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements =  !( other.mainmemory>300); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements = !(other.mainmemory == 300); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements = !( other.sitename == \"ucsd\"); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements =  !( other.SO != \"bla\"); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements = !( other.os != \"linux\"); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements = !( other.mainmemory > 300 ); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements =!(other.mainmemory>300); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements =!(other.mainmemory == 300); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements =!( other.sitename == \"ucsd\"); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements =!( other.SO != \"bla\"); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements =!( other.os != \"linux\");rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );

		jobRequirement = "[requirements =( ( other.sitename == \"ucsd\" || other.sitename == \"lsd\" ) && other.mainmemory <= 200 ); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements =( ( other.sitename == \"ucsd\" || other.sitename == \"lsd\" ) && other.mainmemory <= 200 ); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = ( other.SO == \"bla\" );rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements = (other.os == \"blablabla\") || (other.mainmemory >= 200); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine1.toString() +
		" ? ==> " + matched );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements = (other.os != \"bla\") || (other.mainmemory != 200); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine1.toString() +
		" ? ==> " + matched );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = !(other.os == \"windows\") || (other.sitename >= \"200\"); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine1.toString() +
		" ? ==> " + matched );
		assertEquals(0,  matched );

		jobRequirement = "[requirements = (other.mainMemory == 50) || !(other.os == \"2\");rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(0,  matched );
		
		//Using inexistent ref windows 
		jobRequirement = "[requirements = (other.os != \"linux\") || (other.mainMemory > windows); rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(0,  matched );
		
		//Using existent ref val 
		jobRequirement = "[val=100000; requirements = (other.os != \"linux\") || (other.mainMemory > self.val); rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(0,  matched );
		
		//Using existent ref val without self
		jobRequirement = "[val=100000; requirements = (other.os != \"linux\") || (other.mainMemory > val); rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(0,  matched );
	}
	
	@Test
	public void testGoodMatchesFromFile(){
		int matched = matcher.match(diff_JDL, machine4.toString());
		assertEquals(0, matched);
		
		matched = matcher.match(echo_JDL, machine4.toString());
		assertEquals(0, matched);
		
		matched = matcher.match(java_io_JDL, machine4.toString());
		assertEquals(0, matched);
		
		matched = matcher.match(java_output_JDL, machine4.toString());
		assertEquals(0, matched);
		
		matched = matcher.match(java_JDL, machine4.toString());
		assertEquals(0, matched);
		
		matched = matcher.match(matcher1_JDL, machine1.toString());
		assertEquals(0, matched);
	}
	
	@Test
	public void testGoodMatchesWithDifferentRanks(){
		String jobRequirement = "[requirements = (other.mainMemory == 50) || !(other.os == \"2\");rank=0]";
		int matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(0,  matched );
		
		jobRequirement = "[requirements = (other.mainMemory == 50) || !(other.os == \"2\");rank=22220]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(22220,  matched );
		
		jobRequirement = "[requirements = (other.os == \"blablabla\") || (other.mainmemory >= 200); rank=-10000000]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine1.toString() +
		" ? ==> " + matched );
		assertEquals(-10000000,  matched );
		
		jobRequirement = "[requirements =( ( other.sitename == \"ucsd\" || other.sitename == \"lsd\" ) && other.mainmemory <= 200 ); rank=1000]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(1000,  matched );
		
		jobRequirement = "[requirements = !( other.os != \"linux\"); rank=10]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(10,  matched );
	}
	
	@Test
	public void testBadMatches() {

		String jobRequirement = "[requirements = !(other.siteName == \"lsd\" );rank=0]";
		int matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(-1, matched );

		jobRequirement = "[requirements = ( other.sitename == \"xpto\" && other.os == \"linux\" );rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(-1, matched );

		jobRequirement = "[requirements = ( other.sitename != \"xpto\" &&  other.os == \"windows\" );rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals( -1, matched );

		jobRequirement = "[requirements = ( other.mainmemory == 400 ||  other.os == \"windows\" );rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals( -1, matched );

		jobRequirement = "[requirements = ( other.os == \"windows\" && ( other.sitename != \"ucsd\" ) );rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals( -1, matched );

		jobRequirement = "[requirements = ( other.os == \"windows\" && ( other.sitename == \"noname\" ) );rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals( -1, matched );

		jobRequirement = "[requirements = ( other.mainMemory < 300 && other.sitename == \"noname\" );rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals( -1, matched );

		jobRequirement = "[requirements = ( other.XPTO == \"bla\" );rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals( -1, matched );

		jobRequirement = "[requirements = ( other.XPTO == \"bla\" ) && ( other.XPTO2 != \"ble\" );rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals( -1, matched );

		jobRequirement = "[requirements = ( other.XPTO3 < 9876543210 );rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals( -1, matched );
		
		jobRequirement = "[requirements = (other.os == \"1\") && (other.mainmemory >= 200);rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine1.toString() +
		" ? ==> " + matched );
		assertEquals( -1, matched );

		jobRequirement = "[requirements = (other.sitename <= \"200\") || (other.os == \"50\");rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine1.toString() +
		" ? ==> " + matched );
		assertEquals( -1, matched );

		jobRequirement = "[requirements = (other.mainmemory < 50) || !(other.os != \"20\");rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals( -1, matched );

		jobRequirement = "[requirements = !(other.os == \"linux\") && (other.mainmemory == windows);rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals( -1, matched );
		
		//Expressions containing AND, OR and NOT operators
		jobRequirement = "[val=100000; requirements = (other.os != \"linux\") OR (other.mainMemory > val); rank=0]";
		try{
			matched = matcher.match( jobRequirement, machine3.toString() );
			fail("Invalid job requirement: OR!");
		}catch(NullPointerException e){
		}
		
		jobRequirement = "[requirements = !(other.mainmemory == 300) AND other.mainmemory != 300; rank=0]";
		try{
			matched = matcher.match( jobRequirement, machine1.toString() );
			fail("Invalid job requirement: AND!");
		}catch(NullPointerException e){
		}
		
		jobRequirement = "[requirements = NOT(other.mainmemory == 300); rank=0]";
		matched = matcher.match( jobRequirement, machine1.toString() );
		assertEquals(-1, matched);
		
		//Using different types
		jobRequirement = "[val=100000; requirements = (other.os != 1) || (other.mainMemory > self.val); rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(-1,  matched );
		
		jobRequirement = "[val=100000; requirements = (other.requirements == 1); rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(-1,  matched );
		
		jobRequirement = "[val=100000; requirements = (other.requirements > \"zzz\"); rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(-1,  matched );
		
		//Invalid rank type
		jobRequirement = "[requirements = (other.mainMemory == 50) || !(other.os == \"2\");rank=22220.9]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(-1,  matched );
	}
	
	@Test
	public void testBadMatchesFromFile(){
		int matched = matcher.match(diff_JDL, machine1.toString());
		assertEquals(-1, matched);
		
		matched = matcher.match(java_io_JDL, machine1.toString());
		assertEquals(-1, matched);
		
		matched = matcher.match(java_output_JDL, machine1.toString());
		assertEquals(-1, matched);
		
		matched = matcher.match(java_JDL, machine1.toString());
		assertEquals(-1, matched);
		
		matched = matcher.match(matcher1_JDL, machine4.toString());
		assertEquals(-1, matched);
		
		matched = matcher.match(matcher2_JDL, machine4.toString());
		assertEquals(-1, matched);
		
		matched = matcher.match(matcher2_JDL, machine3.toString());
		assertEquals(-1, matched);
		
		matched = matcher.match(matcher2_JDL, machine2.toString());
		assertEquals(-1, matched);
		
		matched = matcher.match(matcher2_JDL, machine1.toString());
		assertEquals(-1, matched);
	}
	
	@Test
	public void testExceptionsMatches() {
		String jobRequirement;

		jobRequirement = "[requirements =( other.mainmemory >= \"linux\" && ( other.sitename == \"ucsd\" ) );rank=0]";
		assertEquals( -1, matcher.match( jobRequirement, machine1.toString() ) );
	}
	
	@Test
	public void testExpressionsWithoutReqOrRank(){
		//Positive match without rank
		String jobRequirement = "[val=100000; requirements = (other.os != \"linux\") || (other.mainMemory > val);]";
		int matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(-1,  matched );
		
		//Negative match without rank
		jobRequirement = "[val=100000; requirements = (other.requirements > \"zzz\");]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(-1,  matched );
		
		//Positive match without requirements
		jobRequirement = "[val=100000; rank=0]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(-1,  matched );
		
		//Negative match without requirements
		jobRequirement = "[val=100000; rank=10]";
		matched = matcher.match( jobRequirement, machine3.toString() );
		System.out.println( "test > " + jobRequirement + " matches " + machine3.toString() +
		" ? ==> " + matched );
		assertEquals(-1,  matched );
	}
}
