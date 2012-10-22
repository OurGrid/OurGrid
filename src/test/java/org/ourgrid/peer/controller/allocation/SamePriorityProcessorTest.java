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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.worker.WorkerAcceptanceTestCase;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.controller.allocation.SamePriorityProcessor;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalAllocableWorker;
import org.ourgrid.peer.to.LocalConsumer;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.peer.to.PeerBalance;
import org.ourgrid.peer.to.Priority;
import org.ourgrid.peer.to.RemoteConsumer;
import org.ourgrid.peer.to.Request;
import org.ourgrid.peer.to.Priority.Range;
import org.ourgrid.worker.WorkerComponentContextFactory;

import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;

public class SamePriorityProcessorTest extends TestCase {
	
	protected WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(createComponentContext());
	
	protected TestContext createComponentContext() {
		return new TestContext(
				new WorkerComponentContextFactory(
						new PropertiesFileParser(WorkerAcceptanceTestCase.PROPERTIES_FILEPATH
						)).createContext());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * This method was created to catch a bug on SamePriorityProcessor class method process.
	 * Every time it was required to process a worker request on the same priority as mine, it used to
	 * calculate the number of workers deserved by each consumer in the priority by accepting the total number
	 * of workers as being all the workers signed on the provider. In some cases, this didn't allow workers
	 * to be swapped from consumer to consumer, even if the requesting consumer still deserved workers,
	 * because all the consumers in the range ended up with a negative worker deserving number.
	 * 
	 * The following piece of code was changed:
	 * 
	 * FROM => getPossibleWorkersToAllocate(consumer, workersInRange, totalAllocableWorkers, requirements);
	 * TO   => getPossibleWorkersToAllocate(consumer, workersInRange, workersInRange.size(), requirements);
	 * 
	 */
	public void testProcess_ConsideringOnlyWorkersInRange() {
		// Consumers initialization
		LocalConsumer localConsumer = new LocalConsumer();
		String localConsumerPubKey = "localConsumerPubKey";
		DeploymentID consumerID = new DeploymentID(new ContainerID("localConsumerName", "localConsumerServer", "localConsumerModule", 
				localConsumerPubKey), "localConsumerObjName");
		
		localConsumer.setConsumer(consumerID.getServiceID().toString(), consumerID.getPublicKey());
		
		RemoteConsumer remoteConsumer1 = new RemoteConsumer();
		remoteConsumer1.setPriority(Priority.UNKNOWN_PEER);
		String remoteConsumer1PubKey = "remConsumer1PubKey";
		
		DeploymentID remConsumerID = new DeploymentID(new ContainerID("remConsumer1Name", "remConsumer1Server", "remConsumer1Module", 
				remoteConsumer1PubKey), "remConsumer1ObjName");
		remoteConsumer1.setConsumer(remConsumerID.getServiceID().toString(), remoteConsumer1.getPublicKey());
		
		RemoteConsumer remoteConsumer2 = new RemoteConsumer();
		remoteConsumer2.setPriority(Priority.UNKNOWN_PEER);
		String remoteConsumer2PubKey = "remConsumer2PubKey";

		DeploymentID remConsumer2ID = new DeploymentID(new ContainerID("remConsumer2Name", "remConsumer2Server", "remConsumer2Module", 
				remoteConsumer2PubKey), "remConsumer@ObjName");
		remoteConsumer2.setConsumer(remConsumer2ID.getServiceID().toString(), remoteConsumer2.getPublicKey());
		
		RemoteConsumer trustedRemoteConsumer = new RemoteConsumer();
		trustedRemoteConsumer.setPriority(new Priority(Range.ALLOC_FOR_TRUST_COMMUNITY, 1));
		String trustedRemoteConsumerPubKey = "trustedRemConsumerPubKey";

		DeploymentID trustedRemConsumerID = new DeploymentID(new ContainerID("trustedRemConsumerName", "trustedRemConsumerServer",
				"trustedRemConsumerModule", trustedRemoteConsumerPubKey), "trustedRemConsumerObjName");
		trustedRemoteConsumer.setConsumer(trustedRemConsumerID.getServiceID().toString(), trustedRemoteConsumer.getPublicKey());
		
		// Worker Specs initialization
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

		// Allocable Workers initialization
		List<AllocableWorker> sameClientRange = new LinkedList<AllocableWorker>();
		List<AllocableWorker> workersToAllocate = new LinkedList<AllocableWorker>();
		
		String lwpAddress = new ServiceID(new ContainerID("peer", "server", PeerConstants.MODULE_NAME), 
				PeerConstants.LOCAL_WORKER_PROVIDER).toString();
		
		LocalWorker workerA = new LocalWorker(workerSpecA, "workerA@serverA");
		AllocableWorker allocA = new LocalAllocableWorker(workerA, lwpAddress, "CN=workerA");
		allocA.setStatus(LocalWorkerState.IN_USE);
				
		LocalWorker workerB = new LocalWorker(workerSpecB, "workerB@serverB");
		AllocableWorker allocB = new LocalAllocableWorker(workerB, lwpAddress, "CN=workerB");
		allocB.setStatus(LocalWorkerState.IN_USE);

		LocalWorker workerC = new LocalWorker(workerSpecC, "workerC@serverC");
		AllocableWorker allocC = new LocalAllocableWorker(workerC, lwpAddress, "CN=workerC");
		allocC.setStatus(LocalWorkerState.IN_USE);

		LocalWorker workerD = new LocalWorker(workerSpecD, "workerD@serverD");
		AllocableWorker allocD = new LocalAllocableWorker(workerD, lwpAddress, "CN=workerD");
		allocD.setStatus(LocalWorkerState.IN_USE);

		LocalWorker workerE = new LocalWorker(workerSpecE, "workerE@serverE");
		AllocableWorker allocE = new LocalAllocableWorker(workerE, lwpAddress, "CN=workerE");
		allocE.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocE);

		LocalWorker workerF = new LocalWorker(workerSpecF, "workerF@serverF");
		AllocableWorker allocF = new LocalAllocableWorker(workerF, lwpAddress, "CN=workerF");
		allocF.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocF);

		LocalWorker workerG = new LocalWorker(workerSpecG, "workerG@serverG");
		AllocableWorker allocG = new LocalAllocableWorker(workerG, lwpAddress, "CN=workerG");
		allocG.setStatus(LocalWorkerState.IN_USE);
		workersToAllocate.add(allocG);

		LocalWorker workerH = new LocalWorker(workerSpecH, "workerH@serverH");
		AllocableWorker allocH = new LocalAllocableWorker(workerH, lwpAddress, "CN=workerH");
		allocH.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocH);

		// Requests initialization
		Request localConsumerRequest = new Request(new RequestSpecification(0, new JobSpecification("label"), 123, "mem < 128", 2, 0, 0));
		Request remoteConsumer1Request = new Request(new RequestSpecification(0, new JobSpecification("label"), 456, "", 10, 0, 0));
		Request remoteConsumer2Request = new Request(new RequestSpecification(0, new JobSpecification("label"), 789, "mem > 512", 10, 0, 0));
		Request trustRemoteConsumerRequest = new Request(new RequestSpecification(0, new JobSpecification("label"), 012, "mem >= 256", 3, 0, 0));
		
		// Pointing allocables to requests and consumers and vice-versa
		localConsumerRequest.addAllocableWorker(allocC);
		allocC.setRequest(localConsumerRequest);
		allocC.setConsumer(localConsumer);
		localConsumer.addRequest(localConsumerRequest);
		
		remoteConsumer1Request.addAllocableWorker(allocA);
		remoteConsumer1.addWorker(allocA);
		allocA.setRequest(remoteConsumer1Request);
		allocA.setConsumer(remoteConsumer1);
		
		remoteConsumer1Request.addAllocableWorker(allocB);
		remoteConsumer1.addWorker(allocB);
		allocB.setRequest(remoteConsumer1Request);
		allocB.setConsumer(remoteConsumer1);
		
		remoteConsumer1Request.addAllocableWorker(allocD);
		remoteConsumer1.addWorker(allocD);
		allocD.setRequest(remoteConsumer1Request);
		allocD.setConsumer(remoteConsumer1);
		
		remoteConsumer2Request.addAllocableWorker(allocG);
		remoteConsumer2.addWorker(allocG);
		allocG.setRequest(remoteConsumer2Request);
		allocG.setConsumer(remoteConsumer2);
		
		trustRemoteConsumerRequest.addAllocableWorker(allocE);
		trustedRemoteConsumer.addWorker(allocE);
		allocE.setRequest(trustRemoteConsumerRequest);
		allocE.setConsumer(trustedRemoteConsumer);
		
		trustRemoteConsumerRequest.addAllocableWorker(allocF);
		trustedRemoteConsumer.addWorker(allocF);
		allocF.setRequest(trustRemoteConsumerRequest);
		allocF.setConsumer(trustedRemoteConsumer);
		
		trustRemoteConsumerRequest.addAllocableWorker(allocH);
		trustedRemoteConsumer.addWorker(allocH);
		allocH.setRequest(trustRemoteConsumerRequest);
		allocH.setConsumer(trustedRemoteConsumer);
		
		// New request is done from known remote consumer
		RemoteConsumer newTrustedRemoteConsumer = new RemoteConsumer();
		newTrustedRemoteConsumer.setPriority(new Priority(Range.ALLOC_FOR_TRUST_COMMUNITY, 1));
		String newTrustedRemoteConsumerPubKey = "newTrustedRemConsumerPubKey";

		DeploymentID newTrustedRemoteConsumerID = new DeploymentID(new ContainerID("newTrustedRemConsumerName", "newTrustedRemConsumerServer",
				"newTrustedRemConsumerModule", newTrustedRemoteConsumerPubKey), "newTrustedRemConsumerObjName");
		newTrustedRemoteConsumer.setConsumer(newTrustedRemoteConsumerID.getServiceID().toString(), 
				newTrustedRemoteConsumerPubKey);
		
		String requestRequirements = "mem > 512";
		int totalRequiredWorkers = 3;

		// Same priority processor initialization
		SamePriorityProcessor<AllocableWorker> samePriorityProc = new SamePriorityProcessor
				<AllocableWorker>(newTrustedRemoteConsumer, requestRequirements, 8, new HashMap<String, PeerBalance>(), new HashMap<String, String>() );
		
		// Processing workers from the same range
		samePriorityProc.process(totalRequiredWorkers - workersToAllocate.size(), sameClientRange,
				workersToAllocate);
		
		assertTrue(workersToAllocate.contains(allocH) && workersToAllocate.contains(allocG) && workersToAllocate.size() == 2);
		
	}
	
	/**
	 * This method was created to catch a bug on SamePriorityProcessor method process.
	 * The deserving workers algorithm didn't use to consider the workers chosen by other processors on its
	 * calculation. That is, no workers to be allocated were counted as already being on the client's range
	 * by the time the processor calculates the number of deserving workers by each consumer on the range.
	 * This caused errors on the allocation, sometimes even making the actual consumer get more workers than
	 * it was of its right (considering the redistribution on its range).
	 *
	 * The following piece of code was changed:
	 * 
	 * FROM => getPossibleWorkersToAllocate(consumer, workersInRange, workersInRange.size(), requirements);
	 * TO   => getPossibleWorkersToAllocate(consumer, workersInRange, workersInRange.size() + workersToAllocate.size(), requirements);
	 * 
	 */
	public void testProcess_ConsideringWorkersInRangeAndWorkersToBeAllocated() {
		// Consumers initialization
		LocalConsumer localConsumer1 = new LocalConsumer();
		String localConsumer1PubKey = "localConsumer1PubKey";
		
		DeploymentID localConsumer1ID = new DeploymentID(new ContainerID("localConsumer1Name", "localConsumer1Server",
				"localConsumer1Module", localConsumer1PubKey), "localConsumer1ObjName");

		localConsumer1.setConsumer(localConsumer1ID.getServiceID().toString(), localConsumer1PubKey);
		
		LocalConsumer localConsumer2 = new LocalConsumer();
		String localConsumer2PubKey = "localConsumer2PubKey";
		
		DeploymentID localConsumer2ID = new DeploymentID(new ContainerID("localConsumer2Name", "localConsumer2Server",
				"localConsumer2Module", localConsumer2PubKey), "localConsumer2ObjName");

		localConsumer2.setConsumer(localConsumer2ID.getServiceID().toString(), localConsumer2PubKey);
		
		RemoteConsumer trustedRemoteConsumer = new RemoteConsumer();
		trustedRemoteConsumer.setPriority(new Priority(Range.ALLOC_FOR_TRUST_COMMUNITY, 2));
		String trustedRemoteConsumerPubKey = "trustedRemConsumerPubKey";
		
		DeploymentID trustedRemoteConsumerID = new DeploymentID(new ContainerID("trustedRemConsumerName", "trustedRemConsumerServer",
				"trustedRemConsumerModule", trustedRemoteConsumerPubKey), "trustedRemConsumerObjName");

		trustedRemoteConsumer.setConsumer(trustedRemoteConsumerID.getServiceID().toString(), trustedRemoteConsumerPubKey);
		
		// Worker Specs initialization
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

		// Allocable Workers initialization
		List<AllocableWorker> sameClientRange = new LinkedList<AllocableWorker>();
		List<AllocableWorker> workersToAllocate = new LinkedList<AllocableWorker>();
		
		String lwpAddress = new ServiceID(new ContainerID("peer", "server", PeerConstants.MODULE_NAME), 
				PeerConstants.LOCAL_WORKER_PROVIDER).toString();
		
		LocalWorker workerA = new LocalWorker(workerSpecA, "workerA@serverA");
		AllocableWorker allocA = new LocalAllocableWorker(workerA, lwpAddress, "CN=workerA");
		allocA.setStatus(LocalWorkerState.IN_USE);
		workersToAllocate.add(allocA);
				
		LocalWorker workerB = new LocalWorker(workerSpecB, "workerB@serverB");
		AllocableWorker allocB = new LocalAllocableWorker(workerB, lwpAddress, "CN=workerB");
		allocB.setStatus(LocalWorkerState.IN_USE);
		workersToAllocate.add(allocB);

		LocalWorker workerC = new LocalWorker(workerSpecC, "workerC@serverC");
		AllocableWorker allocC = new LocalAllocableWorker(workerC, lwpAddress, "CN=workerC");
		allocC.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocC);
		
		LocalWorker workerD = new LocalWorker(workerSpecD, "workerD@serverD");
		AllocableWorker allocD = new LocalAllocableWorker(workerD, lwpAddress, "CN=workerD");
		allocD.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocD);

		LocalWorker workerE = new LocalWorker(workerSpecE, "workerE@serverE");
		AllocableWorker allocE = new LocalAllocableWorker(workerE, lwpAddress, "CN=workerE");
		allocE.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocE);

		LocalWorker workerF = new LocalWorker(workerSpecF, "workerF@serverF");
		AllocableWorker allocF = new LocalAllocableWorker(workerF, lwpAddress, "CN=workerF");
		allocF.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocF);

		LocalWorker workerG = new LocalWorker(workerSpecG, "workerG@serverG");
		AllocableWorker allocG = new LocalAllocableWorker(workerG, lwpAddress, "CN=workerG");
		allocG.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocG);

		LocalWorker workerH = new LocalWorker(workerSpecH, "workerH@serverH");
		AllocableWorker allocH = new LocalAllocableWorker(workerH, lwpAddress, "CN=workerH");
		allocH.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocH);

		// Requests initialization
		Request localConsumer1Request = new Request(new RequestSpecification(0, new JobSpecification("label"), 123, "mem < 128", 2, 0, 0));
		Request localConsumer2Request = new Request(new RequestSpecification(0, new JobSpecification("label"), 456, "mem > 32", 8, 0, 0));
		Request remoteConsumerRequest = new Request(new RequestSpecification(0, new JobSpecification("label"), 789, "", 3, 0, 0));

		localConsumer1.addRequest(localConsumer1Request);
		localConsumer2.addRequest(localConsumer2Request);
		
		// Pointing allocables to requests and consumers and vice-versa
		localConsumer1Request.addAllocableWorker(allocC);
		allocC.setRequest(localConsumer1Request);
		allocC.setConsumer(localConsumer1);

		localConsumer2Request.addAllocableWorker(allocD);
		allocD.setRequest(localConsumer2Request);
		allocD.setConsumer(localConsumer2);
		
		localConsumer2Request.addAllocableWorker(allocE);
		allocE.setRequest(localConsumer2Request);
		allocE.setConsumer(localConsumer2);
		
		localConsumer2Request.addAllocableWorker(allocF);
		allocF.setRequest(localConsumer2Request);
		allocF.setConsumer(localConsumer2);
		
		localConsumer2Request.addAllocableWorker(allocG);
		allocG.setRequest(localConsumer2Request);
		allocG.setConsumer(localConsumer2);
		
		localConsumer2Request.addAllocableWorker(allocH);
		allocH.setRequest(localConsumer2Request);
		allocH.setConsumer(localConsumer2);

		remoteConsumerRequest.addAllocableWorker(allocA);
		trustedRemoteConsumer.addWorker(allocA);
		allocA.setRequest(remoteConsumerRequest);
		allocA.setConsumer(trustedRemoteConsumer);
		
		remoteConsumerRequest.addAllocableWorker(allocB);
		trustedRemoteConsumer.addWorker(allocB);
		allocB.setRequest(remoteConsumerRequest);
		allocB.setConsumer(trustedRemoteConsumer);

		// New request is done from known remote consumer
		String requestRequirements = "";
		int totalRequiredWorkers = 10;

		// Same priority processor initialization
		SamePriorityProcessor<AllocableWorker> samePriorityProc = new SamePriorityProcessor
				<AllocableWorker>(localConsumer1, requestRequirements, 8, new HashMap<String, PeerBalance>(), new HashMap<String, String>());
		
		// Processing workers from the same range
		samePriorityProc.process(totalRequiredWorkers - workersToAllocate.size(), sameClientRange,
				workersToAllocate);
		
		assertTrue(workersToAllocate.contains(allocA) && workersToAllocate.contains(allocB) &&
				workersToAllocate.contains(allocH) && workersToAllocate.size() == 3);
		
	}
	
	/**
	 * This method was created to catch a bug on SamePriorityProcessor method process.
	 * The scenario established many local consumers, with one of them having too many workers. As one of the
	 * consumers has lots of workers, it gets to be over-balanced, and all of the other consumers get
	 * unbalanced. In case a new local consumer requests workers, it must only try to get workers from the
	 * over-balanced consumer.
	 * 
	 * This test assumes the over-balanced consumer does not have any matching worker, so the new local
	 * consumer won't get any workers when doing its request.
	 * 
	 */
	public void testProcess_ConsideringOverbalancedConsumerWithoutMatchingWorkers() {
		// Consumers initialization
		LocalConsumer localConsumer1 = new LocalConsumer();
		String localConsumer1PubKey = "localConsumer1PubKey";
		
		DeploymentID localConsumer1ID = new DeploymentID(new ContainerID("localConsumer1Name", "localConsumer1Server",
				"localConsumer1Module", localConsumer1PubKey), "localConsumer1ObjName");

		localConsumer1.setConsumer(localConsumer1ID.getServiceID().toString(), localConsumer1PubKey);
		
		LocalConsumer localConsumer2 = new LocalConsumer();
		String localConsumer2PubKey = "localConsumer2PubKey";
		
		DeploymentID localConsumer2ID = new DeploymentID(new ContainerID("localConsumer2Name", "localConsumer2Server",
				"localConsumer2Module", localConsumer2PubKey), "localConsumer2ObjName");

		localConsumer2.setConsumer(localConsumer2ID.getServiceID().toString(), localConsumer2PubKey);
		
		LocalConsumer localConsumer3 = new LocalConsumer();
		String localConsumer3PubKey = "localConsumer3PubKey";

		DeploymentID localConsumer3ID = new DeploymentID(new ContainerID("localConsumer3Name", "localConsumer3Server",
				"localConsumer3Module", localConsumer3PubKey), "localConsumer3ObjName");

		localConsumer3.setConsumer(localConsumer3ID.getServiceID().toString(), localConsumer3PubKey);
		
		// Worker Specs initialization
		String workerServerName = "xmpp.ourgrid.org";

		String workerAUserName = "workerA";
		WorkerSpecification workerSpecA = new WorkerSpecification();
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerAUserName);
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);
		
		String workerBUserName = "workerB";
		WorkerSpecification workerSpecB = new WorkerSpecification();
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerBUserName);
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerCUserName = "workerC";
		WorkerSpecification workerSpecC = new WorkerSpecification();
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerCUserName);
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerDUserName = "workerD";
		WorkerSpecification workerSpecD = new WorkerSpecification();
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerDUserName);
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerEUserName = "workerE";
		WorkerSpecification workerSpecE = new WorkerSpecification();
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerEUserName);
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerFUserName = "workerF";
		WorkerSpecification workerSpecF = new WorkerSpecification();
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerFUserName);
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerGUserName = "workerG";
		WorkerSpecification workerSpecG = new WorkerSpecification();
		workerSpecG.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecG.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerGUserName);
		workerSpecG.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerHUserName = "workerH";
		WorkerSpecification workerSpecH = new WorkerSpecification();
		workerSpecH.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecH.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerHUserName);
		workerSpecH.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);
		
		String workerIUserName = "workerI";
		WorkerSpecification workerSpecI = new WorkerSpecification();
		workerSpecI.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecI.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerIUserName);
		workerSpecI.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);
		
		String workerJUserName = "workerJ";
		WorkerSpecification workerSpecJ = new WorkerSpecification();
		workerSpecJ.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecJ.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerJUserName);
		workerSpecJ.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerKUserName = "workerK";
		WorkerSpecification workerSpecK = new WorkerSpecification();
		workerSpecK.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecK.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerKUserName);
		workerSpecK.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerLUserName = "workerL";
		WorkerSpecification workerSpecL = new WorkerSpecification();
		workerSpecL.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecL.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerLUserName);
		workerSpecL.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerMUserName = "workerM";
		WorkerSpecification workerSpecM = new WorkerSpecification();
		workerSpecM.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecM.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerMUserName);
		workerSpecM.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerNUserName = "workerN";
		WorkerSpecification workerSpecN = new WorkerSpecification();
		workerSpecN.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecN.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerNUserName);
		workerSpecN.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerOUserName = "workerO";
		WorkerSpecification workerSpecO = new WorkerSpecification();
		workerSpecO.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecO.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerOUserName);
		workerSpecO.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerPUserName = "workerP";
		WorkerSpecification workerSpecP = new WorkerSpecification();
		workerSpecP.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecP.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerPUserName);
		workerSpecP.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);
		
		String workerQUserName = "workerQ";
		WorkerSpecification workerSpecQ = new WorkerSpecification();
		workerSpecQ.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecQ.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerQUserName);
		workerSpecQ.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);
		
		String workerRUserName = "workerR";
		WorkerSpecification workerSpecR = new WorkerSpecification();
		workerSpecR.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecR.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerRUserName);
		workerSpecR.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerSUserName = "workerS";
		WorkerSpecification workerSpecS = new WorkerSpecification();
		workerSpecS.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecS.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerSUserName);
		workerSpecS.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerTUserName = "workerT";
		WorkerSpecification workerSpecT = new WorkerSpecification();
		workerSpecT.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecT.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerTUserName);
		workerSpecT.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerUUserName = "workerU";
		WorkerSpecification workerSpecU = new WorkerSpecification();
		workerSpecU.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_WINDOWS);
		workerSpecU.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerUUserName);
		workerSpecU.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerVUserName = "workerV";
		WorkerSpecification workerSpecV = new WorkerSpecification();
		workerSpecV.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_WINDOWS);
		workerSpecV.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerVUserName);
		workerSpecV.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerWUserName = "workerW";
		WorkerSpecification workerSpecW = new WorkerSpecification();
		workerSpecW.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_WINDOWS);
		workerSpecW.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerWUserName);
		workerSpecW.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerXUserName = "workerX";
		WorkerSpecification workerSpecX = new WorkerSpecification();
		workerSpecX.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_WINDOWS);
		workerSpecX.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerXUserName);
		workerSpecX.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		// Allocable Workers initialization
		List<AllocableWorker> sameClientRange = new LinkedList<AllocableWorker>();
		List<AllocableWorker> workersToAllocate = new LinkedList<AllocableWorker>();
		
		String lwpAddress = new ServiceID(new ContainerID("peer", "server", PeerConstants.MODULE_NAME), 
				PeerConstants.LOCAL_WORKER_PROVIDER).toString();
		
		LocalWorker workerA = new LocalWorker(workerSpecA, "workerA@serverA");
		AllocableWorker allocA = new LocalAllocableWorker(workerA, lwpAddress, "CN=workerA");
		allocA.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocA);

		LocalWorker workerB = new LocalWorker(workerSpecB, "workerB@serverB");
		AllocableWorker allocB = new LocalAllocableWorker(workerB, lwpAddress, "CN=workerB");
		allocB.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocB);
		
		LocalWorker workerC = new LocalWorker(workerSpecC, "workerC@serverC");
		AllocableWorker allocC = new LocalAllocableWorker(workerC, lwpAddress, "CN=workerC");
		allocC.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocC);

		LocalWorker workerD = new LocalWorker(workerSpecD, "workerD@serverC");
		AllocableWorker allocD = new LocalAllocableWorker(workerD, lwpAddress, "CN=workerD");
		allocD.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocD);
		
		LocalWorker workerE = new LocalWorker(workerSpecE, "workerE@serverE");
		AllocableWorker allocE = new LocalAllocableWorker(workerE, lwpAddress, "CN=workerE");
		allocE.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocE);

		LocalWorker workerF = new LocalWorker(workerSpecF, "workerF@serverF");
		AllocableWorker allocF = new LocalAllocableWorker(workerF, lwpAddress, "CN=workerF");
		allocF.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocF);
		
		LocalWorker workerG = new LocalWorker(workerSpecG, "workerG@serverG");
		AllocableWorker allocG = new LocalAllocableWorker(workerG, lwpAddress, "CN=workerG");
		allocG.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocG);

		LocalWorker workerH = new LocalWorker(workerSpecH, "workerH@serverH");
		AllocableWorker allocH = new LocalAllocableWorker(workerH, lwpAddress, "CN=workerH");
		allocH.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocH);
		
		LocalWorker workerI = new LocalWorker(workerSpecI, "workerI@serverI");
		AllocableWorker allocI = new LocalAllocableWorker(workerI, lwpAddress, "CN=workerI");
		allocI.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocI);

		LocalWorker workerJ = new LocalWorker(workerSpecJ, "workerJ@serverJ");
		AllocableWorker allocJ = new LocalAllocableWorker(workerJ, lwpAddress, "CN=workerJ");
		allocJ.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocJ);
		
		LocalWorker workerK = new LocalWorker(workerSpecK, "workerK@serverK");
		AllocableWorker allocK = new LocalAllocableWorker(workerK, lwpAddress, "CN=workerK");
		allocK.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocK);

		LocalWorker workerL = new LocalWorker(workerSpecL, "workerL@serverL");
		AllocableWorker allocL = new LocalAllocableWorker(workerL, lwpAddress, "CN=workerL");
		allocL.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocL);
		
		LocalWorker workerM = new LocalWorker(workerSpecM, "workerM@serverM");
		AllocableWorker allocM = new LocalAllocableWorker(workerM, lwpAddress, "CN=workerM");
		allocM.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocM);

		LocalWorker workerN = new LocalWorker(workerSpecN, "workerN@serverN");
		AllocableWorker allocN = new LocalAllocableWorker(workerN, lwpAddress, "CN=workerN");
		allocN.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocN);
		
		LocalWorker workerO = new LocalWorker(workerSpecO, "workerO@serverO");
		AllocableWorker allocO = new LocalAllocableWorker(workerO, lwpAddress, "CN=workerO");
		allocO.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocO);

		LocalWorker workerP = new LocalWorker(workerSpecP, "workerP@serverP");
		AllocableWorker allocP = new LocalAllocableWorker(workerP, lwpAddress, "CN=workerP");
		allocP.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocP);
		
		LocalWorker workerQ = new LocalWorker(workerSpecQ, "workerQ@serverQ");
		AllocableWorker allocQ = new LocalAllocableWorker(workerQ, lwpAddress, "CN=workerQ");
		allocQ.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocQ);

		LocalWorker workerR = new LocalWorker(workerSpecR, "workerR@serverR");
		AllocableWorker allocR = new LocalAllocableWorker(workerR, lwpAddress, "CN=workerR");
		allocR.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocR);
		
		LocalWorker workerS = new LocalWorker(workerSpecS, "workerS@serverS");
		AllocableWorker allocS = new LocalAllocableWorker(workerS, lwpAddress, "CN=workerS");
		allocS.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocS);

		LocalWorker workerT = new LocalWorker(workerSpecT, "workerT@serverT");
		AllocableWorker allocT = new LocalAllocableWorker(workerT, lwpAddress, "CN=workerT");
		allocT.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocT);
		
		LocalWorker workerU = new LocalWorker(workerSpecU, "workerU@serverU");
		AllocableWorker allocU = new LocalAllocableWorker(workerU, lwpAddress, "CN=workerU");
		allocU.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocU);

		LocalWorker workerV = new LocalWorker(workerSpecV, "workerV@serverV");
		AllocableWorker allocV = new LocalAllocableWorker(workerV, lwpAddress, "CN=workerV");
		allocV.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocV);
		
		LocalWorker workerW = new LocalWorker(workerSpecW, "workerW@serverW");
		AllocableWorker allocW = new LocalAllocableWorker(workerW, lwpAddress, "CN=workerW");
		allocW.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocW);

		LocalWorker workerX = new LocalWorker(workerSpecX, "workerX@serverX");
		AllocableWorker allocX = new LocalAllocableWorker(workerX, lwpAddress, "CN=workerX");
		allocX.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocX);
		
		// Requests initialization
		Request localConsumer1Request = new Request(new RequestSpecification(0, new JobSpecification("label"), 123, "os = linux", 20, 0, 0));
		Request localConsumer2Request = new Request(new RequestSpecification(0, new JobSpecification("label"), 456, "os = windows", 4, 0, 0));
		
		// Pointing allocables to requests and consumers and vice-versa
		localConsumer1Request.addAllocableWorker(allocA);
		allocA.setRequest(localConsumer1Request);
		allocA.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocB);
		allocB.setRequest(localConsumer1Request);
		allocB.setConsumer(localConsumer1);
				
		localConsumer1Request.addAllocableWorker(allocC);
		allocC.setRequest(localConsumer1Request);
		allocC.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocD);
		allocD.setRequest(localConsumer1Request);
		allocD.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocE);
		allocE.setRequest(localConsumer1Request);
		allocE.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocF);
		allocF.setRequest(localConsumer1Request);
		allocF.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocG);
		allocG.setRequest(localConsumer1Request);
		allocG.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocH);
		allocH.setRequest(localConsumer1Request);
		allocH.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocI);
		allocI.setRequest(localConsumer1Request);
		allocI.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocJ);
		allocJ.setRequest(localConsumer1Request);
		allocJ.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocK);
		allocK.setRequest(localConsumer1Request);
		allocK.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocL);
		allocL.setRequest(localConsumer1Request);
		allocL.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocM);
		allocM.setRequest(localConsumer1Request);
		allocM.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocN);
		allocN.setRequest(localConsumer1Request);
		allocN.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocO);
		allocO.setRequest(localConsumer1Request);
		allocO.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocP);
		allocP.setRequest(localConsumer1Request);
		allocP.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocQ);
		allocQ.setRequest(localConsumer1Request);
		allocQ.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocR);
		allocR.setRequest(localConsumer1Request);
		allocR.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocS);
		allocS.setRequest(localConsumer1Request);
		allocS.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocT);
		allocT.setRequest(localConsumer1Request);
		allocT.setConsumer(localConsumer1);
		
		localConsumer2Request.addAllocableWorker(allocU);
		allocU.setRequest(localConsumer2Request);
		allocU.setConsumer(localConsumer2);
		
		localConsumer2Request.addAllocableWorker(allocV);
		allocV.setRequest(localConsumer2Request);
		allocV.setConsumer(localConsumer2);
		
		localConsumer2Request.addAllocableWorker(allocW);
		allocW.setRequest(localConsumer2Request);
		allocW.setConsumer(localConsumer2);
		
		localConsumer2Request.addAllocableWorker(allocX);
		allocX.setRequest(localConsumer2Request);
		allocX.setConsumer(localConsumer2);
		
		// Pointing local consumers to requests
		localConsumer1.addRequest(localConsumer1Request);
		localConsumer2.addRequest(localConsumer2Request);
		
		// New request is done from local consumer
		String requestRequirements = "os = windows";
		int totalRequiredWorkers = 2;

		// Same priority processor initialization
		SamePriorityProcessor<AllocableWorker> samePriorityProc = new SamePriorityProcessor
				<AllocableWorker>(localConsumer3, requestRequirements, 24, new HashMap<String, PeerBalance>(), new HashMap<String, String>());
		
		// Processing workers from the same range
		samePriorityProc.process(totalRequiredWorkers - workersToAllocate.size(), sameClientRange,
				workersToAllocate);
		
		// The over-balanced consumer does not have any matching worker
		assertTrue(workersToAllocate.isEmpty());
		
	}
	
	/**
	 * This method was created to catch a bug on SamePriorityProcessor method process.
	 * The scenario established many local consumers, with one of them having too many workers. As one of the
	 * consumers has lots of workers, it gets to be over-balanced, and all of the other consumers get
	 * unbalanced. In case a new local consumer requests workers, it must only try to get workers from the
	 * over-balanced consumer.
	 * 
	 * This test assumes the over-balanced consumer has only one matching worker, so the new local
	 * consumer will get only that matching worker when doing its request.
	 * 
	 */
	public void testProcess_ConsideringOverbalancedConsumerWithOneMatchingWorker() {
		// Consumers initialization
		LocalConsumer localConsumer1 = new LocalConsumer();
		String localConsumer1PubKey = "localConsumer1PubKey";

		DeploymentID localConsumer1ID = new DeploymentID(new ContainerID("localConsumer1Name", "localConsumer1Server",
				"localConsumer1Module", localConsumer1PubKey), "localConsumer1ObjName");

		localConsumer1.setConsumer(localConsumer1ID.getServiceID().toString(), localConsumer1PubKey);
		
		LocalConsumer localConsumer2 = new LocalConsumer();
		String localConsumer2PubKey = "localConsumer2PubKey";
		
		DeploymentID localConsumer2ID = new DeploymentID(new ContainerID("localConsumer2Name", "localConsumer2Server",
				"localConsumer2Module", localConsumer2PubKey), "localConsumer2ObjName");

		localConsumer2.setConsumer(localConsumer2ID.getServiceID().toString(), localConsumer2PubKey);
		
		LocalConsumer localConsumer3 = new LocalConsumer();
		String localConsumer3PubKey = "localConsumer3PubKey";

		DeploymentID localConsumer3ID = new DeploymentID(new ContainerID("localConsumer3Name", "localConsumer3Server",
				"localConsumer3Module", localConsumer3PubKey), "localConsumer3ObjName");

		localConsumer3.setConsumer(localConsumer3ID.getServiceID().toString(), localConsumer3PubKey);
		
		// Worker Specs initialization
		String workerServerName = "xmpp.ourgrid.org";

		String workerAUserName = "workerA";
		WorkerSpecification workerSpecA = new WorkerSpecification();
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerAUserName);
		workerSpecA.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);
		
		String workerBUserName = "workerB";
		WorkerSpecification workerSpecB = new WorkerSpecification();
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerBUserName);
		workerSpecB.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerCUserName = "workerC";
		WorkerSpecification workerSpecC = new WorkerSpecification();
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerCUserName);
		workerSpecC.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerDUserName = "workerD";
		WorkerSpecification workerSpecD = new WorkerSpecification();
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerDUserName);
		workerSpecD.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerEUserName = "workerE";
		WorkerSpecification workerSpecE = new WorkerSpecification();
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerEUserName);
		workerSpecE.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerFUserName = "workerF";
		WorkerSpecification workerSpecF = new WorkerSpecification();
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerFUserName);
		workerSpecF.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerGUserName = "workerG";
		WorkerSpecification workerSpecG = new WorkerSpecification();
		workerSpecG.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecG.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerGUserName);
		workerSpecG.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerHUserName = "workerH";
		WorkerSpecification workerSpecH = new WorkerSpecification();
		workerSpecH.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecH.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerHUserName);
		workerSpecH.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);
		
		String workerIUserName = "workerI";
		WorkerSpecification workerSpecI = new WorkerSpecification();
		workerSpecI.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecI.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerIUserName);
		workerSpecI.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);
		
		String workerJUserName = "workerJ";
		WorkerSpecification workerSpecJ = new WorkerSpecification();
		workerSpecJ.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecJ.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerJUserName);
		workerSpecJ.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerKUserName = "workerK";
		WorkerSpecification workerSpecK = new WorkerSpecification();
		workerSpecK.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecK.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerKUserName);
		workerSpecK.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerLUserName = "workerL";
		WorkerSpecification workerSpecL = new WorkerSpecification();
		workerSpecL.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecL.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerLUserName);
		workerSpecL.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerMUserName = "workerM";
		WorkerSpecification workerSpecM = new WorkerSpecification();
		workerSpecM.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecM.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerMUserName);
		workerSpecM.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerNUserName = "workerN";
		WorkerSpecification workerSpecN = new WorkerSpecification();
		workerSpecN.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecN.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerNUserName);
		workerSpecN.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerOUserName = "workerO";
		WorkerSpecification workerSpecO = new WorkerSpecification();
		workerSpecO.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecO.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerOUserName);
		workerSpecO.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerPUserName = "workerP";
		WorkerSpecification workerSpecP = new WorkerSpecification();
		workerSpecP.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecP.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerPUserName);
		workerSpecP.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);
		
		String workerQUserName = "workerQ";
		WorkerSpecification workerSpecQ = new WorkerSpecification();
		workerSpecQ.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecQ.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerQUserName);
		workerSpecQ.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);
		
		String workerRUserName = "workerR";
		WorkerSpecification workerSpecR = new WorkerSpecification();
		workerSpecR.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecR.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerRUserName);
		workerSpecR.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerSUserName = "workerS";
		WorkerSpecification workerSpecS = new WorkerSpecification();
		workerSpecS.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_LINUX);
		workerSpecS.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerSUserName);
		workerSpecS.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerTUserName = "workerT";
		WorkerSpecification workerSpecT = new WorkerSpecification();
		workerSpecT.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_WINDOWS);
		workerSpecT.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerTUserName);
		workerSpecT.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerUUserName = "workerU";
		WorkerSpecification workerSpecU = new WorkerSpecification();
		workerSpecU.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_WINDOWS);
		workerSpecU.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerUUserName);
		workerSpecU.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerVUserName = "workerV";
		WorkerSpecification workerSpecV = new WorkerSpecification();
		workerSpecV.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_WINDOWS);
		workerSpecV.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerVUserName);
		workerSpecV.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerWUserName = "workerW";
		WorkerSpecification workerSpecW = new WorkerSpecification();
		workerSpecW.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_WINDOWS);
		workerSpecW.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerWUserName);
		workerSpecW.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		String workerXUserName = "workerX";
		WorkerSpecification workerSpecX = new WorkerSpecification();
		workerSpecX.putAttribute(OurGridSpecificationConstants.ATT_OS, OurGridSpecificationConstants.OS_WINDOWS);
		workerSpecX.putAttribute(OurGridSpecificationConstants.ATT_USERNAME, workerXUserName);
		workerSpecX.putAttribute(OurGridSpecificationConstants.ATT_SERVERNAME, workerServerName);

		// Allocable Workers initialization
		List<AllocableWorker> sameClientRange = new LinkedList<AllocableWorker>();
		List<AllocableWorker> workersToAllocate = new LinkedList<AllocableWorker>();
		String lwpAddress = new ServiceID(new ContainerID("peer", "server", PeerConstants.MODULE_NAME), 
				PeerConstants.LOCAL_WORKER_PROVIDER).toString();;
		
		LocalWorker workerA = new LocalWorker(workerSpecA, "workerA@serverA");
		AllocableWorker allocA = new LocalAllocableWorker(workerA, lwpAddress, "CN=workerA");
		allocA.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocA);

		LocalWorker workerB = new LocalWorker(workerSpecB, "workerB@serverB");
		AllocableWorker allocB = new LocalAllocableWorker(workerB, lwpAddress, "CN=workerB");
		allocB.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocB);
		
		LocalWorker workerC = new LocalWorker(workerSpecC, "workerC@serverC");
		AllocableWorker allocC = new LocalAllocableWorker(workerC, lwpAddress, "CN=workerC");
		allocC.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocC);

		LocalWorker workerD = new LocalWorker(workerSpecD, "workerD@serverC");
		AllocableWorker allocD = new LocalAllocableWorker(workerD, lwpAddress, "CN=workerD");
		allocD.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocD);
		
		LocalWorker workerE = new LocalWorker(workerSpecE, "workerE@serverE");
		AllocableWorker allocE = new LocalAllocableWorker(workerE, lwpAddress, "CN=workerE");
		allocE.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocE);

		LocalWorker workerF = new LocalWorker(workerSpecF, "workerF@serverF");
		AllocableWorker allocF = new LocalAllocableWorker(workerF, lwpAddress, "CN=workerF");
		allocF.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocF);
		
		LocalWorker workerG = new LocalWorker(workerSpecG, "workerG@serverG");
		AllocableWorker allocG = new LocalAllocableWorker(workerG, lwpAddress, "CN=workerG");
		allocG.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocG);

		LocalWorker workerH = new LocalWorker(workerSpecH, "workerH@serverH");
		AllocableWorker allocH = new LocalAllocableWorker(workerH, lwpAddress, "CN=workerH");
		allocH.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocH);
		
		LocalWorker workerI = new LocalWorker(workerSpecI, "workerI@serverI");
		AllocableWorker allocI = new LocalAllocableWorker(workerI, lwpAddress, "CN=workerI");
		allocI.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocI);

		LocalWorker workerJ = new LocalWorker(workerSpecJ, "workerJ@serverJ");
		AllocableWorker allocJ = new LocalAllocableWorker(workerJ, lwpAddress, "CN=workerJ");
		allocJ.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocJ);
		
		LocalWorker workerK = new LocalWorker(workerSpecK, "workerK@serverK");
		AllocableWorker allocK = new LocalAllocableWorker(workerK, lwpAddress, "CN=workerK");
		allocK.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocK);

		LocalWorker workerL = new LocalWorker(workerSpecL, "workerL@serverL");
		AllocableWorker allocL = new LocalAllocableWorker(workerL, lwpAddress, "CN=workerL");
		allocL.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocL);
		
		LocalWorker workerM = new LocalWorker(workerSpecM, "workerM@serverM");
		AllocableWorker allocM = new LocalAllocableWorker(workerM, lwpAddress, "CN=workerM");
		allocM.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocM);

		LocalWorker workerN = new LocalWorker(workerSpecN, "workerN@serverN");
		AllocableWorker allocN = new LocalAllocableWorker(workerN, lwpAddress, "CN=workerN");
		allocN.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocN);
		
		LocalWorker workerO = new LocalWorker(workerSpecO, "workerO@serverO");
		AllocableWorker allocO = new LocalAllocableWorker(workerO, lwpAddress, "CN=workerO");
		allocO.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocO);

		LocalWorker workerP = new LocalWorker(workerSpecP, "workerP@serverP");
		AllocableWorker allocP = new LocalAllocableWorker(workerP, lwpAddress, "CN=workerP");
		allocP.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocP);
		
		LocalWorker workerQ = new LocalWorker(workerSpecQ, "workerQ@serverQ");
		AllocableWorker allocQ = new LocalAllocableWorker(workerQ, lwpAddress, "CN=workerQ");
		allocQ.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocQ);

		LocalWorker workerR = new LocalWorker(workerSpecR, "workerR@serverR");
		AllocableWorker allocR = new LocalAllocableWorker(workerR, lwpAddress, "CN=workerR");
		allocR.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocR);
		
		LocalWorker workerS = new LocalWorker(workerSpecS, "workerS@serverS");
		AllocableWorker allocS = new LocalAllocableWorker(workerS, lwpAddress, "CN=workerS");
		allocS.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocS);

		LocalWorker workerT = new LocalWorker(workerSpecT, "workerT@serverT");
		AllocableWorker allocT = new LocalAllocableWorker(workerT, lwpAddress, "CN=workerT");
		allocT.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocT);
		
		LocalWorker workerU = new LocalWorker(workerSpecU, "workerU@serverU");
		AllocableWorker allocU = new LocalAllocableWorker(workerU, lwpAddress, "CN=workerU");
		allocU.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocU);

		LocalWorker workerV = new LocalWorker(workerSpecV, "workerV@serverV");
		AllocableWorker allocV = new LocalAllocableWorker(workerV, lwpAddress, "CN=workerV");
		allocV.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocV);
		
		LocalWorker workerW = new LocalWorker(workerSpecW, "workerW@serverW");
		AllocableWorker allocW = new LocalAllocableWorker(workerW, lwpAddress, "CN=workerW");
		allocW.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocW);

		LocalWorker workerX = new LocalWorker(workerSpecX, "workerX@serverX");
		AllocableWorker allocX = new LocalAllocableWorker(workerX, lwpAddress, "CN=workerX");
		allocX.setStatus(LocalWorkerState.IN_USE);
		sameClientRange.add(allocX);
		
		// Requests initialization
		Request localConsumer1Request = new Request(new RequestSpecification(0, new JobSpecification("label"), 123, "", 20, 0, 0));
		Request localConsumer2Request = new Request(new RequestSpecification(0, new JobSpecification("label"), 456, "os = windows", 4, 0, 0));
		
		// Pointing allocables to requests and consumers and vice-versa
		localConsumer1Request.addAllocableWorker(allocA);
		allocA.setRequest(localConsumer1Request);
		allocA.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocB);
		allocB.setRequest(localConsumer1Request);
		allocB.setConsumer(localConsumer1);
				
		localConsumer1Request.addAllocableWorker(allocC);
		allocC.setRequest(localConsumer1Request);
		allocC.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocD);
		allocD.setRequest(localConsumer1Request);
		allocD.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocE);
		allocE.setRequest(localConsumer1Request);
		allocE.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocF);
		allocF.setRequest(localConsumer1Request);
		allocF.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocG);
		allocG.setRequest(localConsumer1Request);
		allocG.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocH);
		allocH.setRequest(localConsumer1Request);
		allocH.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocI);
		allocI.setRequest(localConsumer1Request);
		allocI.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocJ);
		allocJ.setRequest(localConsumer1Request);
		allocJ.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocK);
		allocK.setRequest(localConsumer1Request);
		allocK.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocL);
		allocL.setRequest(localConsumer1Request);
		allocL.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocM);
		allocM.setRequest(localConsumer1Request);
		allocM.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocN);
		allocN.setRequest(localConsumer1Request);
		allocN.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocO);
		allocO.setRequest(localConsumer1Request);
		allocO.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocP);
		allocP.setRequest(localConsumer1Request);
		allocP.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocQ);
		allocQ.setRequest(localConsumer1Request);
		allocQ.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocR);
		allocR.setRequest(localConsumer1Request);
		allocR.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocS);
		allocS.setRequest(localConsumer1Request);
		allocS.setConsumer(localConsumer1);
		
		localConsumer1Request.addAllocableWorker(allocT);
		allocT.setRequest(localConsumer1Request);
		allocT.setConsumer(localConsumer1);
		
		localConsumer2Request.addAllocableWorker(allocU);
		allocU.setRequest(localConsumer2Request);
		allocU.setConsumer(localConsumer2);
		
		localConsumer2Request.addAllocableWorker(allocV);
		allocV.setRequest(localConsumer2Request);
		allocV.setConsumer(localConsumer2);
		
		localConsumer2Request.addAllocableWorker(allocW);
		allocW.setRequest(localConsumer2Request);
		allocW.setConsumer(localConsumer2);
		
		localConsumer2Request.addAllocableWorker(allocX);
		allocX.setRequest(localConsumer2Request);
		allocX.setConsumer(localConsumer2);
		
		// Pointing local consumers to requests
		localConsumer1.addRequest(localConsumer1Request);
		localConsumer2.addRequest(localConsumer2Request);
		
		// New request is done from local consumer
		String requestRequirements = "os = windows";
		int totalRequiredWorkers = 2;

		// Same priority processor initialization
		SamePriorityProcessor<AllocableWorker> samePriorityProc = new SamePriorityProcessor
				<AllocableWorker>(localConsumer3, requestRequirements, 24, new HashMap<String, PeerBalance>(), new HashMap<String, String>());
		
		// Processing workers from the same range
		samePriorityProc.process(totalRequiredWorkers - workersToAllocate.size(), sameClientRange,
				workersToAllocate);
		
		assertTrue(workersToAllocate.contains(allocT) && workersToAllocate.size() == 1);
		
	}

}
