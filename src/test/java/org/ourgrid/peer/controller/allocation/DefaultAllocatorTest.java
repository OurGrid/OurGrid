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
package org.ourgrid.peer.controller.allocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.controller.allocation.DefaultAllocator;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.Consumer;
import org.ourgrid.peer.to.LocalAllocableWorker;
import org.ourgrid.peer.to.LocalConsumer;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.peer.to.PeerBalance;
import org.ourgrid.peer.to.Priority;
import org.ourgrid.peer.to.RemoteConsumer;
import org.ourgrid.peer.to.Request;
import org.ourgrid.peer.to.Priority.Range;

import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class DefaultAllocatorTest extends TestCase {
	

	/**
	 * This method was created to catch a bug on DefaultAllocator class method getRangeBasedPriorityAllocation.
	 * In case there was a higher priority to be checked and the consumer still needed workers, the method
	 * treated as if that priority were a lower one.
	 * When there isn't any lower or equal priority compared to the consumer's, it must not search for
	 * workers anymore. It used to stop searching only either if there weren't any priorities left or the current
	 * searching consumer has the same priority as my consumer. 
	 * 
	 *  The following code was added in order to correct the bug.
	 *  
	 *  if(requestPriority.compareTo(currentPriorityRange) < 0) {
	 *		break;
	 *	}
	 */
	@Test
	public void testGetRangeBasedPriorityAllocation() {
		
		// Allocator initialization
		DefaultAllocator defaultAllocator = DefaultAllocator.getInstance();
		
		// Parameters initialization
		Consumer localConsumer = new LocalConsumer();
		String consumerPubKey = "consumerPubKey";
		DeploymentID consumerID = new DeploymentID(new ContainerID("consumerName", "consumerServer", "consumerModule", consumerPubKey),
			"consumerObjName");
		localConsumer.setConsumer(consumerID.getServiceID().toString(), consumerPubKey);
		
		Consumer remoteConsumer = new RemoteConsumer();
		String remoteConsumerPubKey = "remConsumerPubKey";
		
		DeploymentID remConsumerID = new DeploymentID(new ContainerID("remConsumerName", "remConsumerServer", "remConsumerModule", 
				remoteConsumerPubKey), "remConsumerObjName");
		remoteConsumer.setConsumer(remConsumerID.getServiceID().toString(), remoteConsumerPubKey);
			
		String requirements = "";
		Priority requestPriority = new Priority(Range.ALLOC_FOR_UNKNOWN_COMMUNITY);
		int requestNecessity = 10;
		
		String workerServerName = "xmpp.ourgrid.org";

		String workerAUserName = "workerA";
		WorkerSpecification workerSpecA = new WorkerSpecification();
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_MEM, "16");
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerAUserName);
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);
		
		String workerBUserName = "workerB";
		WorkerSpecification workerSpecB = new WorkerSpecification();
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_MEM, "32");
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerBUserName);
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerCUserName = "workerC";
		WorkerSpecification workerSpecC = new WorkerSpecification();
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_MEM, "64");
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerCUserName);
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerDUserName = "workerD";
		WorkerSpecification workerSpecD = new WorkerSpecification();
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_MEM, "128");
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerDUserName);
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerEUserName = "workerE";
		WorkerSpecification workerSpecE = new WorkerSpecification();
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_MEM, "256");
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerEUserName);
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerFUserName = "workerF";
		WorkerSpecification workerSpecF = new WorkerSpecification();
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_MEM, "512");
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerFUserName);
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerGUserName = "workerG";
		WorkerSpecification workerSpecG = new WorkerSpecification();
		workerSpecG.putAttribute(OurGridSpecificationConstants.ATT_MEM, "1024");
		workerSpecG.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerGUserName);
		workerSpecG.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerHUserName = "workerH";
		WorkerSpecification workerSpecH = new WorkerSpecification();
		workerSpecH.putAttribute(OurGridSpecificationConstants.ATT_MEM, "2048");
		workerSpecH.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerHUserName);
		workerSpecH.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		Collection<AllocableWorker> allAllocableWorkers = new LinkedList<AllocableWorker>();
		String lwpAddress = new ServiceID(new ContainerID("peer", "server", PeerConstants.MODULE_NAME), 
				PeerConstants.LOCAL_WORKER_PROVIDER).toString();
		
		LocalWorker workerA = new LocalWorker(workerSpecA, "workerA@serverA");
		AllocableWorker allocA = new LocalAllocableWorker(workerA, lwpAddress, "CN=workerA");
		allAllocableWorkers.add(allocA);
				
		LocalWorker workerB = new LocalWorker(workerSpecB, "workerB@serverB");
		AllocableWorker allocB = new LocalAllocableWorker(workerB, lwpAddress, "CN=workerB");
		allAllocableWorkers.add(allocB);

		LocalWorker workerC = new LocalWorker(workerSpecC, "workerC@serverC");
		AllocableWorker allocC = new LocalAllocableWorker(workerC, lwpAddress, "CN=workerC");
		allocC.setStatus(LocalWorkerState.IN_USE);
		
		Request requestC = new Request(new RequestSpecification(0, new JobSpecification("label"), 123, "", 5, 0, 0));
		requestC.addAllocableWorker(allocC);
		allocC.setRequest(requestC);
		allocC.setConsumer(localConsumer);
		((LocalConsumer)localConsumer).addRequest(requestC);		
		allAllocableWorkers.add(allocC);

		LocalWorker workerD = new LocalWorker(workerSpecD, "workerD@serverD");
		AllocableWorker allocD = new LocalAllocableWorker(workerD, lwpAddress, "CN=workerD");
		allAllocableWorkers.add(allocD);

		LocalWorker workerE = new LocalWorker(workerSpecE, "workerE@serverE");
		AllocableWorker allocE = new LocalAllocableWorker(workerE, lwpAddress, "CN=workerE");
		allAllocableWorkers.add(allocE);

		LocalWorker workerF = new LocalWorker(workerSpecF, "workerF@serverF");
		AllocableWorker allocF = new LocalAllocableWorker(workerF, lwpAddress, "CN=workerF");
		allAllocableWorkers.add(allocF);

		LocalWorker workerG = new LocalWorker(workerSpecG, "workerG@serverG");
		AllocableWorker allocG = new LocalAllocableWorker(workerG, lwpAddress, "CN=workerG");
		allAllocableWorkers.add(allocG);

		LocalWorker workerH = new LocalWorker(workerSpecH, "workerH@serverH");
		AllocableWorker allocH = new LocalAllocableWorker(workerH, lwpAddress, "CN=workerH");
		allAllocableWorkers.add(allocH);
		
		List<AllocableWorker> result = defaultAllocator.getRangeBasedPriorityAllocation(remoteConsumer,
				allAllocableWorkers, requirements, requestPriority, requestNecessity, 
				new HashMap<String, PeerBalance>(), requestC.getSpecification().getAnnotations());
		
		Collection<AllocableWorker> expectedResult = new LinkedList<AllocableWorker>();
		expectedResult.add(allocA);
		expectedResult.add(allocB);
		expectedResult.add(allocD);
		expectedResult.add(allocE);
		expectedResult.add(allocF);
		expectedResult.add(allocG);
		expectedResult.add(allocH);
				
		Assert.assertTrue(result.containsAll(expectedResult) && expectedResult.containsAll(result));
	}

}
