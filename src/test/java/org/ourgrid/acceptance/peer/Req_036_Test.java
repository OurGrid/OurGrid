/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.acceptance.peer;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

@ReqTest(reqs="REQ036")
public class Req_036_Test extends PeerAcceptanceTestCase {

	private PeerComponent component;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
	private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
	private Req_036_Util req_036_Util = new Req_036_Util(getComponentContext());

	@Before
	public void setUp() throws Exception{
		super.setUp();
		component = req_010_Util.startPeer();
	}

	@After
	public void tearDown() throws Exception{
		req_010_Util.niceStopPeer(component);
		super.tearDown();
	}

	/**
	 * Verifies a peer without local Workers. 
	 * It must return an empty list of local Workers.
	 */
	@ReqTest(test="AT-036.1", reqs="REQ036")
	@Test public void test_AT_036_1_PeerWithoutLocalWorkers(){
		List<WorkerInfo> emptyList = new LinkedList<WorkerInfo>();
		req_036_Util.getLocalWorkersStatus(emptyList);
	}

	/**
	 * Verifies a peer with logged Workers. 
	 */
	@ReqTest(test="AT-036.2", reqs="REQ036")
	@Test public void test_AT_036_2_PeerWithSomeLocalWorkers(){

		//Create Worker specs
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec("U1", "S1");
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec("U2", "S2");

		List<WorkerSpecification> workers = 
				AcceptanceTestUtil.createList(workerSpecA, workerSpecB);

		//Workers login
		req_010_Util.workerLogin(component, workers);

		//Create expected result
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.OWNER, null);
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.OWNER, null);
		List<WorkerInfo> localWorkersInfo = 
				AcceptanceTestUtil.createList(workerInfoA, workerInfoB);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}

	/**
	 * Verifies a peer with logged Workers. 
	 */
	@ReqTest(test="AT-036.2", reqs="REQ036")
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_036_2_PeerWithSomeLocalWorkersWithJDL(){

		//Create Worker specs
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createClassAdWorkerSpec("U1", "S1", null, null);
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createClassAdWorkerSpec("U2", "S2", null, null);

		List<WorkerSpecification> workers = 
				AcceptanceTestUtil.createList(workerSpecA, workerSpecB);

		//Workers login
		req_010_Util.workerLogin(component, workers);

		//Create expected result
		WorkerInfo workerInfoA = new WorkerInfo(workerSpecA, LocalWorkerState.OWNER, null);
		WorkerInfo workerInfoB = new WorkerInfo(workerSpecB, LocalWorkerState.OWNER, null);
		List<WorkerInfo> localWorkersInfo = 
				AcceptanceTestUtil.createList(workerInfoA, workerInfoB);

		req_036_Util.getLocalWorkersStatus(localWorkersInfo);
	}
}