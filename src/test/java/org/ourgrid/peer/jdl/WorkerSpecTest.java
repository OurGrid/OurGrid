package org.ourgrid.peer.jdl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecificationConstants;

import condor.classad.ClassAdParser;
import condor.classad.RecordExpr;


public class WorkerSpecTest {
	
	/**
	 * Testing different creations of a workerSpec.
	 */
	@Test
	public void testValidConstructors(){
		//Creating a workerspec without attributes
		WorkerSpecification spec = new WorkerSpecification();
		assertFalse(spec.hasAttribute(OurGridSpecificationConstants.USERNAME));
		
		//Creating a workerspec with attributes and without annotations
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		spec = new WorkerSpecification(expr);
		assertTrue(spec.hasAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
		assertTrue(spec.hasAttribute(WorkerSpecificationConstants.OS));
		assertTrue(spec.hasAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		assertTrue(spec.hasAttribute("Requirements"));
		assertTrue(spec.hasAttribute("Rank"));
		assertTrue(spec.hasAttribute("Owner"));
		
		//Creating a workerspec with attributes and annotations
		Map<String, String> annotations = new HashMap<String, String>();
		annotations.put("ann1", "testing...");
		
		spec = new WorkerSpecification(expr, annotations);
		assertEquals(1, spec.getAnnotations().size());
		assertTrue(spec.hasAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
		assertTrue(spec.hasAttribute(WorkerSpecificationConstants.OS));
		assertTrue(spec.hasAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		assertTrue(spec.hasAttribute("Requirements"));
		assertTrue(spec.hasAttribute("Rank"));
		assertTrue(spec.hasAttribute("Owner"));
	}

	/**
	 * Testing different invalid creations of a workerSpec.
	 */
	@Test
	public void testInvalidConstructors(){
		//Invalid record expressions
		RecordExpr expr = null;
		try{
			new WorkerSpecification(expr);
			fail("Invalid workerSpec creation!");
		}catch(RuntimeException e){
		}
		
		try{
			Map<String, String> annotations = new HashMap<String, String>();
			annotations.put("ann1", "testing...");
			new WorkerSpecification(expr, annotations);
			fail("Invalid workerSpec creation!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void testEqualsWithIdenticalSpecs(){
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		
		RecordExpr expr2 = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"Requirements = TRUE;" +
				"CPUPlatform = \"x86\"; " +
				"OS = \"Linux\"; " +
				"Rank = 0]").parse();
		
		WorkerSpecification spec1 = new WorkerSpecification(expr);
		WorkerSpecification spec2 = new WorkerSpecification(expr2);
		assertTrue(spec1.equals(spec2));
		assertTrue(spec1.equals(spec1));
		assertTrue(spec2.equals(spec2));
	}
	
	@Test
	public void testEqualsWithSimilarSpecs(){
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		
		RecordExpr expr2 = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"Requirements = TRUE;" +
				"OS = \"Linu\"; " +
				"CPUPlatform = \"x86\"; " +
				"Rank = 0]").parse();
		
		WorkerSpecification spec1 = new WorkerSpecification(expr);
		WorkerSpecification spec2 = new WorkerSpecification(expr2);
		assertFalse(spec1.equals(spec2));
		assertTrue(spec1.equals(spec1));
		assertTrue(spec2.equals(spec2));
		
		expr2 = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"Requirements = TRUE;" +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x6\"; " +
				"Rank = 0]").parse();
		
		spec1 = new WorkerSpecification(expr);
		spec2 = new WorkerSpecification(expr2);
		assertFalse(spec1.equals(spec2));
		assertTrue(spec1.equals(spec1));
		assertTrue(spec2.equals(spec2));
		
		expr2 = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"Requirements = FALSE;" +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Rank = 0]").parse();
		
		spec1 = new WorkerSpecification(expr);
		spec2 = new WorkerSpecification(expr2);
		assertFalse(spec1.equals(spec2));
		assertTrue(spec1.equals(spec1));
		assertTrue(spec2.equals(spec2));
	}
	
	@Test
	public void testGetExistentAttributes(){
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		
		WorkerSpecification spec = new WorkerSpecification(expr);
		assertEquals("Linux", spec.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("x86", spec.getAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		assertEquals("x86",spec.getAttribute("CPUPlaTform"));
		assertTrue("TRUE".equalsIgnoreCase(spec.getAttribute("Requirements")));
		assertTrue("TRUE".equalsIgnoreCase(spec.getAttribute("requirements")));
		assertEquals("0", spec.getAttribute("Rank"));
		assertEquals("Fubica", spec.getAttribute("Owner"));
		assertEquals("2048", spec.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
		assertEquals("2048",spec.getAttribute("mAinMeMory"));
	}
	
	@Test
	public void testGetInexistentAttributes(){
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		
		WorkerSpecification spec = new WorkerSpecification(expr);
		assertNull(spec.getAttribute("Op"));
		assertNull(spec.getAttribute("Sys"));
		assertNull(spec.getAttribute("Memor"));
		assertNull(spec.getAttribute("data"));
	}
	
	@Test
	public void testGetUserAndServerDefined(){
		//Existent attributes, but invalid to form a DeploymentID
		RecordExpr expr = (RecordExpr) new ClassAdParser("[username=\"Fubica\";servername = \"xmpp.ourgrid.org\"; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		WorkerSpecification spec = new WorkerSpecification(expr);
		assertEquals("xmpp.ourgrid.org", spec.getServer());
		assertEquals("Fubica", spec.getUser());
		assertEquals("Fubica@xmpp.ourgrid.org/WORKER/LOCAL_WORKER_MANAGEMENT", spec.getURL());
		assertEquals("Fubica@xmpp.ourgrid.org", spec.getUserAndServer());
		assertEquals("WORKER", spec.getModuleName());
		assertEquals("LOCAL_WORKER_MANAGEMENT", spec.getObjectName());
		
		assertTrue(spec.isValid());
		
		//Existent and valid attributes
		expr = (RecordExpr) new ClassAdParser("[username=Fubica;servername = xmpp.ourgrid.org; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		spec = new WorkerSpecification(expr);
		assertEquals("xmpp.ourgrid.org", spec.getServer());
		assertEquals("Fubica", spec.getUser());
		assertEquals("Fubica@xmpp.ourgrid.org/WORKER/LOCAL_WORKER_MANAGEMENT", spec.getURL());
		assertEquals("Fubica@xmpp.ourgrid.org", spec.getUserAndServer());
		assertEquals("WORKER", spec.getModuleName());
		assertEquals("LOCAL_WORKER_MANAGEMENT", spec.getObjectName());
		
		assertTrue(spec.isValid());
	}
	
	@Test
	public void testGetUserAndServerNotDefined(){
		//Server not defined
		RecordExpr expr = (RecordExpr) new ClassAdParser("[username=\"Fubica\";mainMemory = 1024; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		WorkerSpecification spec = new WorkerSpecification(expr);
		assertNull(spec.getServer());
		assertEquals("Fubica", spec.getUser());
		assertEquals("Fubica@null/WORKER/LOCAL_WORKER_MANAGEMENT", spec.getURL());
		assertEquals("Fubica@null", spec.getUserAndServer());
		assertEquals("WORKER", spec.getModuleName());
		assertEquals("LOCAL_WORKER_MANAGEMENT", spec.getObjectName());
		assertFalse(spec.isValid());
		
		//User not defined
		expr = (RecordExpr) new ClassAdParser("[servername=xmpp.ourgrid.org;mainMemory = 1024; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		spec = new WorkerSpecification(expr);
		assertEquals("xmpp.ourgrid.org", spec.getServer());
		assertNull(spec.getUser());
		assertEquals("null@xmpp.ourgrid.org/WORKER/LOCAL_WORKER_MANAGEMENT", spec.getURL());
		assertEquals("null@xmpp.ourgrid.org", spec.getUserAndServer());
		assertEquals("WORKER", spec.getModuleName());
		assertEquals("LOCAL_WORKER_MANAGEMENT", spec.getObjectName());
		assertFalse(spec.isValid());
	}
	
	@Test
	public void testHasAttributeWithExistentOnes(){
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		WorkerSpecification spec = new WorkerSpecification(expr);
		assertTrue(spec.hasAttribute("Owner"));
		assertTrue(spec.hasAttribute(WorkerSpecificationConstants.OS));
		assertTrue(spec.hasAttribute("oS"));
		assertTrue(spec.hasAttribute("Os"));
		assertTrue(spec.hasAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		assertTrue(spec.hasAttribute("reQUIREments"));
		assertTrue(spec.hasAttribute("rank"));
		assertTrue(spec.hasAttribute("Rank"));
	}
	
	@Test
	public void testHasAttributeWithInexistentOnes(){
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		
		WorkerSpecification spec = new WorkerSpecification(expr);
		assertFalse(spec.hasAttribute("Owne"));
		assertFalse(spec.hasAttribute("opys"));
		assertFalse(spec.hasAttribute("OpSy"));
		assertFalse(spec.hasAttribute("RCH"));
		assertFalse(spec.hasAttribute("reUIREments"));
		assertFalse(spec.hasAttribute("ran"));
		assertFalse(spec.hasAttribute(OurGridSpecificationConstants.USERNAME));
		assertFalse(spec.hasAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertFalse(spec.hasAttribute("data"));
	}
	
	@Test
	public void testPutAttributeInEmptySpec(){
		WorkerSpecification spec = new WorkerSpecification();
		spec.putAttribute("username", "user1");
		spec.putAttribute("servername", "server1");
		
		assertEquals("user1", spec.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("server1", spec.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		
		//New insertions
		spec.putAttribute("owner", "user1");
		spec.putAttribute("CPUPlatform", "X86");
		
		assertEquals("user1", spec.getAttribute("owner"));
		assertEquals("X86", spec.getAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
	}
	
	@Test
	public void testPutAttributeInNonEmptySpec(){
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		
		WorkerSpecification spec = new WorkerSpecification(expr);
		spec.putAttribute("username", "user1");
		spec.putAttribute("servername", "server1");
		
		assertEquals("Linux", spec.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("x86", spec.getAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		assertTrue("TRUE".equalsIgnoreCase(spec.getAttribute("Requirements")));
		assertEquals("0", spec.getAttribute("Rank"));
		assertEquals("Fubica", spec.getAttribute("Owner"));
		assertEquals("2048", spec.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
		assertEquals("user1", spec.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("server1", spec.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		
		//Defining an attribute that already existed
		spec.putAttribute(WorkerSpecificationConstants.OS, "windows");
		assertEquals("windows", spec.getAttribute(WorkerSpecificationConstants.OS));
		
		//Adding empty and null attributes
		spec.putAttribute("", "empty");
		try{
			spec.putAttribute(null, "null");
			fail("Null attributes are not allowed!");
		}catch(IllegalArgumentException e){
		}
		assertEquals("empty", spec.getAttribute(""));
		assertNull(spec.getAttribute(null));
	}
	
	@Test
	public void testPutAttributesInEmptySpec(){
		WorkerSpecification spec = new WorkerSpecification(new RecordExpr());
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
				"Rank = 0]").parse();
		
		spec.putAttributes(expr);
		assertEquals("Linux", spec.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("x86", spec.getAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		assertTrue("TRUE".equalsIgnoreCase(spec.getAttribute("Requirements")));
		assertEquals("0", spec.getAttribute("Rank"));
		assertEquals("Fubica", spec.getAttribute("Owner"));
		assertEquals("2048", spec.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
	}
	
	@Test
	public void testPutAttributesInNonEmptySpec(){
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
		"Rank = 0]").parse();
		WorkerSpecification spec = new WorkerSpecification(expr);
		
		assertEquals("Linux", spec.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("x86", spec.getAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		assertTrue("TRUE".equalsIgnoreCase(spec.getAttribute("Requirements")));
		assertEquals("0", spec.getAttribute("Rank"));
		assertEquals("Fubica", spec.getAttribute("Owner"));
		assertEquals("2048", spec.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
		
		//Inserting another expression with some common attributes
		RecordExpr expr2 = (RecordExpr) new ClassAdParser("[User=\"Fubica\"; " +
				"CPUClock = 2.33; " +
				"CPUPlatform = \"x64\"; " +
				"Requirements = False;" +
				"Rank = 0]").parse();
		spec.putAttributes(expr2);
		assertEquals("Linux", spec.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("x64", spec.getAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		assertTrue("False".equalsIgnoreCase(spec.getAttribute("Requirements")));
		assertEquals("0", spec.getAttribute("Rank"));
		assertEquals("Fubica", spec.getAttribute("Owner"));
		assertEquals("2048", spec.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
		assertEquals("Fubica", spec.getAttribute("User"));
		assertEquals("2.330000000000000E+00", spec.getAttribute(WorkerSpecificationConstants.CPU_CLOCK));
	}
	
	@Test
	public void testRemoveExistentAttributes(){
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
		"Rank = 0]").parse();
		WorkerSpecification spec = new WorkerSpecification(expr);
		
		assertEquals("Linux", spec.getAttribute(WorkerSpecificationConstants.OS));
		spec.removeAttribute(WorkerSpecificationConstants.OS);
		assertNull(spec.getAttribute(WorkerSpecificationConstants.OS));
		
		//removing another attribute
		assertEquals("x86", spec.getAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		spec.removeAttribute(WorkerSpecificationConstants.CPU_PLATFORM);
		assertNull(spec.getAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		
		//removing an attribute already removed
		assertNull(spec.getAttribute(WorkerSpecificationConstants.OS));
		spec.removeAttribute(WorkerSpecificationConstants.OS);
		assertNull(spec.getAttribute(WorkerSpecificationConstants.OS));
	}
	
	@Test
	public void testRemoveInexistentAttributes(){
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
		"Rank = 0]").parse();
		WorkerSpecification spec = new WorkerSpecification(expr);
		
		assertNull(spec.getAttribute(OurGridSpecificationConstants.USERNAME));
		spec.removeAttribute(OurGridSpecificationConstants.USERNAME);
		assertNull(spec.getAttribute(OurGridSpecificationConstants.USERNAME));
		
		//Removing a null attribute
		assertNull(spec.getAttribute(null));
		spec.removeAttribute(null);
		assertNull(spec.getAttribute(null));
		
		//Removing an empty attribute
		assertNull(spec.getAttribute(""));
		spec.removeAttribute("");
		assertNull(spec.getAttribute(""));
	}
	
	@Test
	public void testSetAttributes(){
		RecordExpr expr = (RecordExpr) new ClassAdParser("[Owner=\"Fubica\";mainMemory = 2048; " +
				"OS = \"Linux\"; " +
				"CPUPlatform = \"x86\"; " +
				"Requirements = TRUE;" +
		"Rank = 0]").parse();
		WorkerSpecification spec = new WorkerSpecification(expr);
		
		assertEquals("Linux", spec.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("x86", spec.getAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		assertTrue("TRUE".equalsIgnoreCase(spec.getAttribute("Requirements")));
		assertEquals("0", spec.getAttribute("Rank"));
		assertEquals("Fubica", spec.getAttribute("Owner"));
		assertEquals("2048", spec.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
		
		//Setting new attributes
		RecordExpr expr2 = (RecordExpr) new ClassAdParser("[User=\"Fubica\"; " +
				"CPUClock = 2.33; " +
				"CPUPlatform = \"x64\"; " +
				"Requirements = False;" +
				"Rank = 0]").parse();
		spec.setRecord(expr2);
		assertNull(spec.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("x64", spec.getAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		assertTrue("False".equalsIgnoreCase(spec.getAttribute("Requirements")));
		assertEquals("0", spec.getAttribute("Rank"));
		assertNull(spec.getAttribute("Owner"));
		assertNull(spec.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
		assertEquals("Fubica", spec.getAttribute("User"));
		assertEquals("2.330000000000000E+00", spec.getAttribute(WorkerSpecificationConstants.CPU_CLOCK));
		
		//Setting attributes in an empty spec
		spec = new WorkerSpecification();
		
		spec.setRecord(expr);
		assertEquals("Linux", spec.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("x86", spec.getAttribute(WorkerSpecificationConstants.CPU_PLATFORM));
		assertTrue("TRUE".equalsIgnoreCase(spec.getAttribute("Requirements")));
		assertEquals("0", spec.getAttribute("Rank"));
		assertEquals("Fubica", spec.getAttribute("Owner"));
		assertEquals("2048", spec.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
	}
}
