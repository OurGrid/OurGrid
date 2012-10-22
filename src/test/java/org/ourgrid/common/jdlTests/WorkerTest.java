package org.ourgrid.common.jdlTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.main.DescriptionFileCompile;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecificationConstants;

/**
 * @author David Candeia
 *
 */
public class WorkerTest {

	private static final String TEST_FILES_PATH = "test" + File.separator + "specs" + File.separator + "semantic"
			+ File.separator;

	private static final String test_file1 = TEST_FILES_PATH + "GridPeerTest41.classad";
	
	private static final String test_file2 = TEST_FILES_PATH + "GridPeerTest1.classad";

	private static final String test_file3 = TEST_FILES_PATH + "GridPeerTest4.classad";

	private static final String TEST_FILES_PATH2 = "test" + File.separator + "acceptance" + File.separator;

//	private static final String test_file4 = TEST_FILES_PATH + File.separator + "file4.classad";

	@Test
	public void testWorker1() throws Exception {
		try {
			List<WorkerSpecification> work = DescriptionFileCompile.compileNewSDF(test_file1);
			fail("Should fail since the server name is not specified");
		} catch (Exception e) {
			// System.out.println("Error: " + e.getMessage());
			assertEquals(
					"Missing attribute \"username or servername\".",
					e.getMessage());

		}
	}

	@Test
	public void testWorker2() throws Exception {
		List<WorkerSpecification> workers = DescriptionFileCompile.compileNewSDF(test_file2);
		assertTrue(2 == workers.size());
	}

	@Test
	public void testWorker3() throws Exception {
		try {
			List<WorkerSpecification> workers = DescriptionFileCompile.compileNewSDF(test_file3);
			fail("Should fail since the server name is not specified");
		} catch (Exception e) {
			// System.out.println("Error: " + e.getMessage());
			assertEquals(
					"Missing attribute \"username or servername\".",
					e.getMessage());
		}
	}

	@Test
	public void testWorker4() throws Exception {
		List<WorkerSpecification> specs = DescriptionFileCompile.compileNewSDF("test" + File.separator +"acceptance" + File.separator +"file1.classad");

		assertTrue(1 == specs.size());
		WorkerSpecification workerSpecA = specs.get(0);

		assertEquals("testserver", workerSpecA.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("testuser", workerSpecA.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("linux", workerSpecA.getAttribute(WorkerSpecificationConstants.OS));

		specs = DescriptionFileCompile.compileNewSDF("test" + File.separator +"acceptance" + File.separator +"file2.classad");

		assertTrue(2 == specs.size());
		WorkerSpecification workerSpecB = specs.get(0);
		WorkerSpecification workerSpecC = specs.get(1);

		assertEquals("testserver", workerSpecB.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("testuserB", workerSpecB.getAttribute(OurGridSpecificationConstants.USERNAME));

		assertEquals("testserver", workerSpecC.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("testuserC", workerSpecC.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("windows", workerSpecC.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("256", workerSpecC.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));

		specs = DescriptionFileCompile.compileNewSDF("test" + File.separator +"acceptance" + File.separator +"file3.classad");

		assertTrue(1 == specs.size());
		WorkerSpecification workerSpecD = specs.get(0);

		assertEquals("testserver", workerSpecD.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("testuser", workerSpecD.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("linux", workerSpecD.getAttribute(WorkerSpecificationConstants.OS));
		assertEquals("1024", workerSpecD.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
	}

	@Test
	public void testWorker5() throws Exception {
		List<WorkerSpecification> specs = DescriptionFileCompile.compileNewSDF("test" + File.separator + "acceptance"
				+ File.separator + "req_111" + File.separator + "TA_111.1.classad");

		assertTrue(17 == specs.size());
		WorkerSpecification workerSpecB = specs.get(0);
		WorkerSpecification workerSpecC = specs.get(1);
		WorkerSpecification workerSpecR = specs.get(16);

		assertEquals("xmpp.ourgrid.org", workerSpecB.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("worker0", workerSpecB.getAttribute(OurGridSpecificationConstants.USERNAME));

		assertEquals("xmpp.ourgrid.org", workerSpecC.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("worker1", workerSpecC.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("256", workerSpecC.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));

		assertEquals("xmpp.ourgrid.org", workerSpecR.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("worker16", workerSpecR.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("1024", workerSpecR.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));

		specs = DescriptionFileCompile.compileNewSDF("test" + File.separator + "acceptance" + File.separator + "req_111"
				+ File.separator + "TA_111.4.classad");

		assertTrue(2 == specs.size());
		WorkerSpecification workerSpec1 = specs.get(0);
		WorkerSpecification workerSpec2 = specs.get(1);

		assertEquals("xmpp.ourgrid.org", workerSpec1.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("worker1", workerSpec1.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("512", workerSpec1.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));		
		assertTrue(workerSpec1.getAttribute(WorkerSpecificationConstants.OS) != null);
		assertTrue(workerSpec1.getAttribute(WorkerSpecificationConstants.OS).equalsIgnoreCase("linux"));
		assertEquals("{brams}", workerSpec1.getAttribute(WorkerSpecificationConstants.SOFTWARE).toString());
		assertEquals("[name=\"brams\"]", workerSpec1.getAttribute("brams").toString());

		assertEquals("xmpp.ourgrid.org", workerSpec2.getAttribute(OurGridSpecificationConstants.SERVERNAME));
		assertEquals("worker2", workerSpec2.getAttribute(OurGridSpecificationConstants.USERNAME));
		assertEquals("512", workerSpec2.getAttribute(WorkerSpecificationConstants.MAIN_MEMORY));
		assertTrue(workerSpec2.getAttribute(OurGridSpecificationConstants.ATT_OS).equalsIgnoreCase("linux"));
		assertEquals("{brams}", workerSpec1.getAttribute(WorkerSpecificationConstants.SOFTWARE).toString());
		assertEquals("[name=\"brams\"]", workerSpec1.getAttribute("brams").toString());

	}
	
//	@Test
//	public void testWorkerAddWithAnnotations() throws Exception {
//		List<WorkerSpec> specs = DescriptionFileCompile.compileNewSDF("test" + File.separator + "acceptance"
//				+ File.separator + "TA_ANN.1.sdf");
//
//		assertTrue(17 == specs.size());
//		WorkerSpec workerSpecB = specs.get(0);
//		WorkerSpec workerSpecC = specs.get(1);
//		WorkerSpec workerSpecR = specs.get(16);
//		
//		assertEquals("xmpp.ourgrid.org", workerSpecB.getAttribute(WorkerSpec.ATT_SERVERNAME));
//		assertEquals("worker0", workerSpecB.getAttribute(WorkerSpec.ATT_USERNAME));
//				
//		assertEquals("worker0", workerSpecB.getAnnotation(WorkerSpec.ATT_USERNAME));
//		assertEquals("tag1", workerSpecB.getAnnotation("tag1"));
//		assertEquals("tag2", workerSpecB.getAnnotation("tag2"));
//
//		
//		assertEquals("xmpp.ourgrid.org", workerSpecC.getAttribute(WorkerSpec.ATT_SERVERNAME));
//		assertEquals("worker1", workerSpecC.getAttribute(WorkerSpec.ATT_USERNAME));
//		assertEquals("256", workerSpecC.getAttribute(WorkerSpec.ATT_MEM));
//		
//		assertEquals("worker1", workerSpecC.getAnnotation(WorkerSpec.ATT_USERNAME));
//		assertEquals("tag1", workerSpecC.getAnnotation("tag1"));
//		assertEquals("tag2", workerSpecC.getAnnotation("tag2"));
//		assertEquals("256", workerSpecC.getAnnotation(WorkerSpec.ATT_MEM));
//
//		
//		assertEquals("xmpp.ourgrid.org", workerSpecR.getAttribute(WorkerSpec.ATT_SERVERNAME));
//		assertEquals("worker16", workerSpecR.getAttribute(WorkerSpec.ATT_USERNAME));
//		assertEquals("1024", workerSpecR.getAttribute(WorkerSpec.ATT_MEM));
//		
//		assertEquals("worker16", workerSpecR.getAnnotation(WorkerSpec.ATT_USERNAME));
//		assertEquals("tag1", workerSpecR.getAnnotation("tag1"));
//		assertEquals("tag2", workerSpecR.getAnnotation("tag2"));
//		assertEquals("1024", workerSpecR.getAnnotation(WorkerSpec.ATT_MEM));		
//	}	
	
}
