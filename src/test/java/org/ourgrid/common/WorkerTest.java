package org.ourgrid.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.main.DescriptionFileCompile;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class WorkerTest {

	private static final String TEST_FILES_PATH = "test" + File.separator + "specs" + File.separator + "semantic"
			+ File.separator;

	private static final String test_file1 = TEST_FILES_PATH + "GridPeerTest41.sdf";

	private static final String test_file2 = TEST_FILES_PATH + "GridPeerTest1.sdf";

	private static final String test_file3 = TEST_FILES_PATH + "GridPeerTest4.sdf";

	private static final String TEST_FILES_PATH2 = "test" + File.separator + "acceptance" + File.separator;

	private static final String test_file4 = TEST_FILES_PATH + File.separator + "file4.sdf";

	@Test
	public void testWorker1() throws Exception {
		try {
			List<WorkerSpecification> work = new ArrayList<WorkerSpecification>();
			fail("Should fail since the server name is not specified");
		} catch (Exception e) {
			// System.out.println("Error: " + e.getMessage());
			assertEquals(
					"Bad Worker address definition: worker1@null/WORKER/LOCAL_WORKER_MANAGEMENT. Check if it has a valid address and the type attribute is defined",
					e.getMessage());

		}
	}

	@Test
	public void testWorker2() throws Exception {
		List<WorkerSpecification> workers = new ArrayList<WorkerSpecification>();
		assertTrue(2 == workers.size());
	}

	@Test
	public void testWorker3() throws Exception {
		try {
			List<WorkerSpecification> workers = new ArrayList<WorkerSpecification>();
			fail("Should fail since the server name is not specified");
		} catch (Exception e) {
			// System.out.println("Error: " + e.getMessage());
			assertEquals(
					"Bad Worker address definition: null@jabber.org/WORKER/LOCAL_WORKER_MANAGEMENT. Check if it has a valid address and the type attribute is defined",
					e.getMessage());
		}
	}

	@Test
	public void testWorker4() throws Exception {
		List<WorkerSpecification> specs = new ArrayList<WorkerSpecification>();

		assertTrue(1 == specs.size());
		WorkerSpecification workerSpecA = specs.get(0);

		assertEquals("testserver", workerSpecA.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		assertEquals("testuser", workerSpecA.getAttribute(OurGridSpecificationConstants.ATT_USERNAME));
		assertEquals("linux", workerSpecA.getAttribute(OurGridSpecificationConstants.ATT_OS));

		specs = new ArrayList<WorkerSpecification>();

		assertTrue(2 == specs.size());
		WorkerSpecification workerSpecB = specs.get(0);
		WorkerSpecification workerSpecC = specs.get(1);

		assertEquals("testserver", workerSpecB.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		assertEquals("testuserB", workerSpecB.getAttribute(OurGridSpecificationConstants.ATT_USERNAME));

		assertEquals("testserver", workerSpecC.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		assertEquals("testuserC", workerSpecC.getAttribute(OurGridSpecificationConstants.ATT_USERNAME));
		assertEquals("windows", workerSpecC.getAttribute(OurGridSpecificationConstants.ATT_OS));
		assertEquals("256", workerSpecC.getAttribute(OurGridSpecificationConstants.ATT_MEM));

		specs = new ArrayList<WorkerSpecification>();

		assertTrue(1 == specs.size());
		WorkerSpecification workerSpecD = specs.get(0);

		assertEquals("testserver", workerSpecD.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		assertEquals("testuser", workerSpecD.getAttribute(OurGridSpecificationConstants.ATT_USERNAME));
		assertEquals("linux", workerSpecD.getAttribute(OurGridSpecificationConstants.ATT_OS));
		assertEquals("1024", workerSpecD.getAttribute(OurGridSpecificationConstants.ATT_MEM));
	}

	@Test
	public void testWorker5() throws Exception {
		List<WorkerSpecification> specs = new ArrayList<WorkerSpecification>();

		assertTrue(17 == specs.size());
		WorkerSpecification workerSpecB = specs.get(0);
		WorkerSpecification workerSpecC = specs.get(1);
		WorkerSpecification workerSpecR = specs.get(16);

		assertEquals("xmpp.ourgrid.org", workerSpecB.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		assertEquals("worker0", workerSpecB.getAttribute(OurGridSpecificationConstants.ATT_USERNAME));

		assertEquals("xmpp.ourgrid.org", workerSpecC.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		assertEquals("worker1", workerSpecC.getAttribute(OurGridSpecificationConstants.ATT_USERNAME));
		assertEquals("256", workerSpecC.getAttribute(OurGridSpecificationConstants.ATT_MEM));

		assertEquals("xmpp.ourgrid.org", workerSpecR.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		assertEquals("worker16", workerSpecR.getAttribute(OurGridSpecificationConstants.ATT_USERNAME));
		assertEquals("1024", workerSpecR.getAttribute(OurGridSpecificationConstants.ATT_MEM));

		specs = new ArrayList<WorkerSpecification>();

		assertTrue(2 == specs.size());
		WorkerSpecification workerSpec1 = specs.get(0);
		WorkerSpecification workerSpec2 = specs.get(1);

		assertEquals("xmpp.ourgrid.org", workerSpec1.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		assertEquals("worker1", workerSpec1.getAttribute(OurGridSpecificationConstants.ATT_USERNAME));
		assertEquals("512", workerSpec1.getAttribute(OurGridSpecificationConstants.ATT_MEM));		
		assertTrue(workerSpec1.getAttribute(OurGridSpecificationConstants.ATT_OS) != null);
		assertTrue(workerSpec1.getAttribute(OurGridSpecificationConstants.ATT_OS).equalsIgnoreCase("linux"));
		assertEquals("brams", workerSpec1.getAttribute(OurGridSpecificationConstants.ATT_ENVIRONMENT));

		assertEquals("xmpp.ourgrid.org", workerSpec2.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		assertEquals("worker2", workerSpec2.getAttribute(OurGridSpecificationConstants.ATT_USERNAME));
		assertEquals("512", workerSpec2.getAttribute(OurGridSpecificationConstants.ATT_MEM));
		assertTrue(workerSpec2.getAttribute(OurGridSpecificationConstants.ATT_OS).equalsIgnoreCase("linux"));
		assertEquals("brams", workerSpec2.getAttribute(OurGridSpecificationConstants.ATT_ENVIRONMENT));

		}
	
	@Test
	public void testWorkerAddWithAnnotations() throws Exception {
		List<WorkerSpecification> specs = new ArrayList<WorkerSpecification>();

		assertTrue(17 == specs.size());
		WorkerSpecification workerSpecB = specs.get(0);
		WorkerSpecification workerSpecC = specs.get(1);
		WorkerSpecification workerSpecR = specs.get(16);
		
		assertEquals("xmpp.ourgrid.org", workerSpecB.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		assertEquals("worker0", workerSpecB.getAttribute(OurGridSpecificationConstants.ATT_USERNAME));
				
		assertEquals("worker0", workerSpecB.getAnnotation(OurGridSpecificationConstants.ATT_USERNAME));
		assertEquals("tag1", workerSpecB.getAnnotation("tag1"));
		assertEquals("tag2", workerSpecB.getAnnotation("tag2"));

		
		assertEquals("xmpp.ourgrid.org", workerSpecC.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		assertEquals("worker1", workerSpecC.getAttribute(OurGridSpecificationConstants.ATT_USERNAME));
		assertEquals("256", workerSpecC.getAttribute(OurGridSpecificationConstants.ATT_MEM));
		
		assertEquals("worker1", workerSpecC.getAnnotation(OurGridSpecificationConstants.ATT_USERNAME));
		assertEquals("tag1", workerSpecC.getAnnotation("tag1"));
		assertEquals("tag2", workerSpecC.getAnnotation("tag2"));
		assertEquals("256", workerSpecC.getAnnotation(OurGridSpecificationConstants.ATT_MEM));

		
		assertEquals("xmpp.ourgrid.org", workerSpecR.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME));
		assertEquals("worker16", workerSpecR.getAttribute(OurGridSpecificationConstants.ATT_USERNAME));
		assertEquals("1024", workerSpecR.getAttribute(OurGridSpecificationConstants.ATT_MEM));
		
		assertEquals("worker16", workerSpecR.getAnnotation(OurGridSpecificationConstants.ATT_USERNAME));
		assertEquals("tag1", workerSpecR.getAnnotation("tag1"));
		assertEquals("tag2", workerSpecR.getAnnotation("tag2"));
		assertEquals("1024", workerSpecR.getAnnotation(OurGridSpecificationConstants.ATT_MEM));		
	}	
	
}
