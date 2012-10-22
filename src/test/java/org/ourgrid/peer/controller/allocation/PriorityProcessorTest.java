package org.ourgrid.peer.controller.allocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.business.controller.allocation.AllocationInfo;
import org.ourgrid.peer.business.controller.allocation.PriorityProcessor;
import org.ourgrid.peer.business.controller.allocation.SamePriorityProcessor;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalAllocableWorker;


public class PriorityProcessorTest{
	
	private static final String RANK_BY_MEMORY = "[Requirements = true; rank = other.mainMemory]";
	private static final String RANK_EQUATION = "[Requirements = true; rank = ((other.mainMemory / 1024) + other.CPUClock)]";
	private static final String REQ_RANK_EQUATION = "[Requirements = other.OS != \"solaris\"; rank = ((other.mainMemory / 128) + (other.Owner == \"user3\"?10:0))]";
	
	private static final String EQUALS_RANK = "[Requirements = true; rank = 0]";
	
	private PriorityProcessor<AllocableWorker> priorityProcessor;
	
	@Category(JDLCompliantTest.class)
	@Test
	public void testGetMatchedAllocationsWithNoSpecs(){
		priorityProcessor = new SamePriorityProcessor<AllocableWorker>(null, RANK_BY_MEMORY, 0, null, null);
		
		AllocationInfo allAllocations = new AllocationInfo(1, null);
		
		List<AllocableWorker> matchedAllocations = priorityProcessor.getMatchedAllocations(allAllocations, RANK_BY_MEMORY);
		assertNotNull(matchedAllocations);
		assertEquals(0, matchedAllocations.size());
	}
	
	@Category(JDLCompliantTest.class)
	@Test
	public void testGetMatchedAllocationsWithAnyValidMachine(){
		priorityProcessor = new SamePriorityProcessor<AllocableWorker>(null, RANK_BY_MEMORY, 0, null, null);
		
		LocalAllocableWorker worker1 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker2 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker3 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		
		WorkerSpecification spec1 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec2 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec3 = EasyMock.createStrictMock(WorkerSpecification.class);
		
		AllocationInfo allAllocations = new AllocationInfo(1, null);
		allAllocations.addAllocation(worker1);
		allAllocations.addAllocation(worker2);
		allAllocations.addAllocation(worker3);
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.usingClassAd()).andReturn(true).once();
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.getExpression()).andReturn("[mainMemory=512; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker2.getWorkerSpecification()).andReturn(spec2).once();
		EasyMock.expect(spec2.getExpression()).andReturn("[mainMemory=256; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker3.getWorkerSpecification()).andReturn(spec3).once();
		EasyMock.expect(spec3.getExpression()).andReturn("[mainMemory=1024; Requirements=TRUE; Rank=0]");
		
		EasyMock.replay(worker1);
		EasyMock.replay(worker2);
		EasyMock.replay(worker3);
		EasyMock.replay(spec1);
		EasyMock.replay(spec2);
		EasyMock.replay(spec3);
		
		List<AllocableWorker> matchedAllocations = priorityProcessor.getMatchedAllocations(allAllocations, RANK_BY_MEMORY);
		assertNotNull(matchedAllocations);
		assertEquals(3, matchedAllocations.size());
		assertEquals(worker3, matchedAllocations.get(0));
		assertEquals(worker1, matchedAllocations.get(1));
		assertEquals(worker2, matchedAllocations.get(2));
		
		EasyMock.verify(worker1);
		EasyMock.verify(worker2);
		EasyMock.verify(worker3);
		EasyMock.verify(spec1);
		EasyMock.verify(spec2);
		EasyMock.verify(spec3);
	}
	
	@Category(JDLCompliantTest.class)
	@Test
	public void testGetMatchedAllocationsRankingByMemoryValue(){
		priorityProcessor = new SamePriorityProcessor<AllocableWorker>(null, RANK_BY_MEMORY, 0, null, null);
		
		LocalAllocableWorker worker1 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker2 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker3 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		
		WorkerSpecification spec1 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec2 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec3 = EasyMock.createStrictMock(WorkerSpecification.class);
		
		AllocationInfo allAllocations = new AllocationInfo(1, null);
		allAllocations.addAllocation(worker1);
		allAllocations.addAllocation(worker2);
		allAllocations.addAllocation(worker3);
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.usingClassAd()).andReturn(true).once();
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.getExpression()).andReturn("[mainMemory=512; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker2.getWorkerSpecification()).andReturn(spec2).once();
		EasyMock.expect(spec2.getExpression()).andReturn("[mainMemory=256; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker3.getWorkerSpecification()).andReturn(spec3).once();
		EasyMock.expect(spec3.getExpression()).andReturn("[mainMemory=1024; Requirements=TRUE; Rank=0]");
		
		EasyMock.replay(worker1);
		EasyMock.replay(worker2);
		EasyMock.replay(worker3);
		EasyMock.replay(spec1);
		EasyMock.replay(spec2);
		EasyMock.replay(spec3);
		
		List<AllocableWorker> matchedAllocations = priorityProcessor.getMatchedAllocations(allAllocations, RANK_BY_MEMORY);
		assertNotNull(matchedAllocations);
		assertEquals(3, matchedAllocations.size());
		assertEquals(worker3, matchedAllocations.get(0));
		assertEquals(worker1, matchedAllocations.get(1));
		assertEquals(worker2, matchedAllocations.get(2));
		
		EasyMock.verify(worker1);
		EasyMock.verify(worker2);
		EasyMock.verify(worker3);
		EasyMock.verify(spec1);
		EasyMock.verify(spec2);
		EasyMock.verify(spec3);
	}
	
	@Category(JDLCompliantTest.class)
	@Test
	public void testGetOtherMatchedAllocationsRankingByMemoryValue(){
		priorityProcessor = new SamePriorityProcessor<AllocableWorker>(null, RANK_BY_MEMORY, 0, null, null);
		
		LocalAllocableWorker worker1 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker2 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker3 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		
		WorkerSpecification spec1 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec2 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec3 = EasyMock.createStrictMock(WorkerSpecification.class);
		
		AllocationInfo allAllocations = new AllocationInfo(1, null);
		allAllocations.addAllocation(worker1);
		allAllocations.addAllocation(worker2);
		allAllocations.addAllocation(worker3);
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.usingClassAd()).andReturn(true).once();
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.getExpression()).andReturn("[mainMemory=512; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker2.getWorkerSpecification()).andReturn(spec2).once();
		EasyMock.expect(spec2.getExpression()).andReturn("[mainMemory=256; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker3.getWorkerSpecification()).andReturn(spec3).once();
		EasyMock.expect(spec3.getExpression()).andReturn("[mainMemory=1024; Requirements=TRUE; Rank=0]");
		
		EasyMock.replay(worker1);
		EasyMock.replay(worker2);
		EasyMock.replay(worker3);
		EasyMock.replay(spec1);
		EasyMock.replay(spec2);
		EasyMock.replay(spec3);
		
		List<AllocationInfo> matchedAllocations = priorityProcessor.getMatchedAllocations(Arrays.asList(allAllocations));
		assertNotNull(matchedAllocations);
		assertEquals(1, matchedAllocations.size());
		assertEquals(allAllocations, matchedAllocations.get(0));
		
		EasyMock.verify(worker1);
		EasyMock.verify(worker2);
		EasyMock.verify(worker3);
		EasyMock.verify(spec1);
		EasyMock.verify(spec2);
		EasyMock.verify(spec3);
	}
	
	@Category(JDLCompliantTest.class)
	@Test
	public void testGetMatchedAllocationsRankingByMemoryAndCpuEquation(){
		priorityProcessor = new SamePriorityProcessor<AllocableWorker>(null, "", 0, null, null);
		
		LocalAllocableWorker worker1 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker2 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker3 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		
		WorkerSpecification spec1 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec2 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec3 = EasyMock.createStrictMock(WorkerSpecification.class);
		
		AllocationInfo allAllocations = new AllocationInfo(1, null);
		allAllocations.addAllocation(worker1);
		allAllocations.addAllocation(worker2);
		allAllocations.addAllocation(worker3);
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.usingClassAd()).andReturn(true).once();
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.getExpression()).andReturn("[CPUClock=3;mainMemory=512; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker2.getWorkerSpecification()).andReturn(spec2).once();
		EasyMock.expect(spec2.getExpression()).andReturn("[CPUClock=4;mainMemory=256; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker3.getWorkerSpecification()).andReturn(spec3).once();
		EasyMock.expect(spec3.getExpression()).andReturn("[CPUClock=1;mainMemory=1024; Requirements=TRUE; Rank=0]");
		
		EasyMock.replay(worker1);
		EasyMock.replay(worker2);
		EasyMock.replay(worker3);
		EasyMock.replay(spec1);
		EasyMock.replay(spec2);
		EasyMock.replay(spec3);
		
		List<AllocableWorker> matchedAllocations = priorityProcessor.getMatchedAllocations(allAllocations, RANK_EQUATION);
		assertNotNull(matchedAllocations);
		assertEquals(3, matchedAllocations.size());
		assertEquals(worker2, matchedAllocations.get(0));
		assertEquals(worker1, matchedAllocations.get(1));
		assertEquals(worker3, matchedAllocations.get(2));
		
		EasyMock.verify(worker1);
		EasyMock.verify(worker2);
		EasyMock.verify(worker3);
		EasyMock.verify(spec1);
		EasyMock.verify(spec2);
		EasyMock.verify(spec3);
	}
	
	@Category(JDLCompliantTest.class)
	@Test
	public void testGetMatchedAllocationsRankingByMemoryAndCpuEquationWithNotIntegerRankValue(){
		priorityProcessor = new SamePriorityProcessor<AllocableWorker>(null, "", 0, null, null);
		
		LocalAllocableWorker worker1 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker2 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker3 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		
		WorkerSpecification spec1 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec2 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec3 = EasyMock.createStrictMock(WorkerSpecification.class);
		
		AllocationInfo allAllocations = new AllocationInfo(1, null);
		allAllocations.addAllocation(worker1);
		allAllocations.addAllocation(worker2);
		allAllocations.addAllocation(worker3);
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.usingClassAd()).andReturn(true).once();
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.getExpression()).andReturn("[CPUClock=0.3;mainMemory=512; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker2.getWorkerSpecification()).andReturn(spec2).once();
		EasyMock.expect(spec2.getExpression()).andReturn("[CPUClock=0.4;mainMemory=256; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker3.getWorkerSpecification()).andReturn(spec3).once();
		EasyMock.expect(spec3.getExpression()).andReturn("[CPUClock=0.1;mainMemory=1024; Requirements=TRUE; Rank=0]");
		
		EasyMock.replay(worker1);
		EasyMock.replay(worker2);
		EasyMock.replay(worker3);
		EasyMock.replay(spec1);
		EasyMock.replay(spec2);
		EasyMock.replay(spec3);
		
		List<AllocableWorker> matchedAllocations = priorityProcessor.getMatchedAllocations(allAllocations, RANK_EQUATION);
		assertNotNull(matchedAllocations);
		assertEquals(0, matchedAllocations.size());

		EasyMock.verify(worker1);
		EasyMock.verify(worker2);
		EasyMock.verify(worker3);
		EasyMock.verify(spec1);
		EasyMock.verify(spec2);
		EasyMock.verify(spec3);
	}
	
	@Category(JDLCompliantTest.class)
	@Test
	public void testGetMatchedAllocationsWithRequirementsAndRank(){
		priorityProcessor = new SamePriorityProcessor<AllocableWorker>(null, "", 0, null, null);
		
		LocalAllocableWorker worker1 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker2 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker3 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		
		WorkerSpecification spec1 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec2 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec3 = EasyMock.createStrictMock(WorkerSpecification.class);
		
		AllocationInfo allAllocations = new AllocationInfo(1, null);
		allAllocations.addAllocation(worker1);
		allAllocations.addAllocation(worker2);
		allAllocations.addAllocation(worker3);
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.usingClassAd()).andReturn(true).once();
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.getExpression()).andReturn("[Owner=\"user1\"; OS=\"solaris\";mainMemory=512; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker2.getWorkerSpecification()).andReturn(spec2).once();
		EasyMock.expect(spec2.getExpression()).andReturn("[Owner=\"user3\"; OS=\"linux\"; mainMemory=256; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker3.getWorkerSpecification()).andReturn(spec3).once();
		EasyMock.expect(spec3.getExpression()).andReturn("[Owner=\"user2\"; OS=\"windows\"; mainMemory=1024; Requirements=TRUE; Rank=0]");
		
		EasyMock.replay(worker1);
		EasyMock.replay(worker2);
		EasyMock.replay(worker3);
		EasyMock.replay(spec1);
		EasyMock.replay(spec2);
		EasyMock.replay(spec3);
		
		List<AllocableWorker> matchedAllocations = priorityProcessor.getMatchedAllocations(allAllocations, REQ_RANK_EQUATION);
		assertNotNull(matchedAllocations);
		assertEquals(2, matchedAllocations.size());
		assertEquals(worker2, matchedAllocations.get(0));
		assertEquals(worker3, matchedAllocations.get(1));
		
		EasyMock.verify(worker1);
		EasyMock.verify(worker2);
		EasyMock.verify(worker3);
		EasyMock.verify(spec1);
		EasyMock.verify(spec2);
		EasyMock.verify(spec3);
	}
	
	@Category(JDLCompliantTest.class)
	@Test
	public void testGetMatchedAllocationsWithEqualsRank(){
		priorityProcessor = new SamePriorityProcessor<AllocableWorker>(null, EQUALS_RANK, 0, null, null);
		
		LocalAllocableWorker worker1 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker2 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		LocalAllocableWorker worker3 = EasyMock.createStrictMock(LocalAllocableWorker.class);
		
		WorkerSpecification spec1 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec2 = EasyMock.createStrictMock(WorkerSpecification.class);
		WorkerSpecification spec3 = EasyMock.createStrictMock(WorkerSpecification.class);
		
		AllocationInfo allAllocations = new AllocationInfo(1, null);
		allAllocations.addAllocation(worker1);
		allAllocations.addAllocation(worker2);
		allAllocations.addAllocation(worker3);
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.usingClassAd()).andReturn(true).once();
		
		EasyMock.expect(worker1.getWorkerSpecification()).andReturn(spec1).once();
		EasyMock.expect(spec1.getExpression()).andReturn("[mainMemory=512; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker2.getWorkerSpecification()).andReturn(spec2).once();
		EasyMock.expect(spec2.getExpression()).andReturn("[mainMemory=256; Requirements=TRUE; Rank=0]");
		EasyMock.expect(worker3.getWorkerSpecification()).andReturn(spec3).once();
		EasyMock.expect(spec3.getExpression()).andReturn("[mainMemory=1024; Requirements=TRUE; Rank=0]");
		
		EasyMock.replay(worker1);
		EasyMock.replay(worker2);
		EasyMock.replay(worker3);
		EasyMock.replay(spec1);
		EasyMock.replay(spec2);
		EasyMock.replay(spec3);
		
		List<AllocableWorker> matchedAllocations = priorityProcessor.getMatchedAllocations(allAllocations, EQUALS_RANK);
		assertNotNull(matchedAllocations);
		assertEquals(3, matchedAllocations.size());
		assertEquals(worker1, matchedAllocations.get(0));
		assertEquals(worker2, matchedAllocations.get(1));
		assertEquals(worker3, matchedAllocations.get(2));
		
		EasyMock.verify(worker1);
		EasyMock.verify(worker2);
		EasyMock.verify(worker3);
		EasyMock.verify(spec1);
		EasyMock.verify(spec2);
		EasyMock.verify(spec3);
	}

}
