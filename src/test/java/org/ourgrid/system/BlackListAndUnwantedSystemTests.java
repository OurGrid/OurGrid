package org.ourgrid.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.ourgrid.broker.status.GridProcessStatusInfo;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.broker.status.TaskStatusInfo;
import org.ourgrid.broker.status.WorkerStatusInfo;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.util.TempFileManager;
import org.ourgrid.system.condition.BrokerJobRunningCondition;
import org.ourgrid.system.condition.BrokerJobStateCondition;
import org.ourgrid.system.condition.PeerHasTheWorkerInStateCondition;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.PeerUnit;
import org.ourgrid.system.units.UnitUtil;
import org.ourgrid.system.units.WorkerUnit;

public class BlackListAndUnwantedSystemTests extends AbstractSystemTest {
	
	@Test
	public void testBlackListWithApllicationError() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 10, 1, 2);
		unitManager.addUnit(brokerUnit);
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

//		brokerUnit.setMaxFails( 10 );
//		brokerUnit.setMaxBlFails( 1 );

//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		peerUnit.addUser(brokerUnit.getLogin());
		int jobid = brokerUnit.addJob( UnitUtil.buildAFailJobApplicationError() );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( job.getState(), GridProcessState.RUNNING );

		String type = job.getTaskByID( 1 ).getReplicaByID( 1 ).getReplicaResult().getExecutionError();
		assertEquals( GridProcessErrorTypes.APPLICATION_ERROR.toString(), type );

		Set<WorkerStatusInfo> workers = brokerUnit.getWorkersByJob().get( jobid );
		assertTrue( workers.isEmpty() );
	}
	
	/**
	 * When a sabotage was detected, the worker should be added to the the blacklist
	 * immediately.
	 */
	@Test
	public void testSabotageBlackList() throws Exception {
		
		/*
		 * added in 25/10/2007 @author Thiago Emmanuel Pereira, thiago.manel@gmail.com
		 */
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 2, 2);
		unitManager.addUnit(brokerUnit);
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

//		brokerUnit.setMaxFails( 1 );
//		brokerUnit.setMaxBlFails( 2 );
		//there is only one task in the job, but it will be sabotaged so the worker will be blacklisted
		//immediately. The maxBlFails does not matter. 

//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		peerUnit.addUser(brokerUnit.getLogin());
		final JobSpecification jobToAdd = UnitUtil.buildASabotagedJobExecution(2, true);
		int jobid = brokerUnit.addJob( jobToAdd);
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IN_USE ) );

		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( GridProcessState.RUNNING, job.getState() );

		//Only errors caused by the user increment the max fails 
		TaskStatusInfo task1 = job.getTaskByID( 1 );
		assertEquals( GridProcessState.RUNNING, task1.getState() );
		String type = task1.getReplicaByID( 1 ).getReplicaResult().getExecutionError();
		assertEquals( GridProcessErrorTypes.SABOTAGE_ERROR.toString(), type );
		
		TaskStatusInfo task2 = job.getTaskByID( 2 );
		assertEquals( GridProcessState.UNSTARTED, task2.getState() );

		Set<WorkerStatusInfo> workers = brokerUnit.getWorkersByJob().get( jobid );
		assertTrue( workers.isEmpty() );
		
	}
	
	@Test
	public void testBlackListWithSabotageDetection() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 10, 1, 2);
		unitManager.addUnit(brokerUnit);
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		peerUnit.initKeys();
		workerUnit.initKeys();
		brokerUnit.initKeys();

//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		peerUnit.addUser(brokerUnit.getLogin());
		int jobid = brokerUnit.addJob( UnitUtil.buildASabotagedJobExecution(1, true) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( job.getState(), GridProcessState.RUNNING );

		GridProcessStatusInfo replicaByID = job.getTaskByID( 1 ).getReplicaByID( 1 );
		String type = replicaByID.getReplicaResult().getExecutionError();
		assertEquals( GridProcessErrorTypes.SABOTAGE_ERROR.toString(), type );

		Set<WorkerStatusInfo> workers = brokerUnit.getWorkersByJob().get( jobid );
		assertTrue( workers.isEmpty() );
	}

	@Test
	public void testBlackListWithExecutionError() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 10, 1, 2);
		unitManager.addUnit(brokerUnit);
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		peerUnit.initKeys();
		workerUnit.initKeys();
		brokerUnit.initKeys();

//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		peerUnit.addUser(brokerUnit.getLogin());
		int jobid = brokerUnit.addJob( UnitUtil.buildAFailJobExecutionError() );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( job.getState(), GridProcessState.RUNNING );

		String type = job.getTaskByID( 1 ).getReplicaByID( 1 ).getReplicaResult().getExecutionError();
		assertEquals( GridProcessErrorTypes.EXECUTION_ERROR.toString(), type );

		Set<WorkerStatusInfo> workers = brokerUnit.getWorkersByJob().get( jobid );
		assertTrue( workers.isEmpty() );
	}

	@Test
	public void testBlackListWithIOError() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 10, 1, 2);
		unitManager.addUnit(brokerUnit);
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setStorageRootPath( NONWRITABLE_DIR.getAbsolutePath() );

		peerUnit.initKeys();
		workerUnit.initKeys();
		brokerUnit.initKeys();

//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		peerUnit.addUser(brokerUnit.getLogin());

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		int jobid = brokerUnit
			.addJob( UnitUtil.buildASmallSleepJobWithStores( putSources, playpenDests, getDests ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( job.getState(), GridProcessState.RUNNING );

		String type = job.getTaskByID( 1 ).getReplicaByID( 1 ).getReplicaResult().getExecutionError();
		assertEquals( GridProcessErrorTypes.IO_ERROR.toString(), type );

		Set<WorkerStatusInfo> workers = brokerUnit.getWorkersByJob().get( jobid );
		assertTrue( workers.isEmpty() );
	}

	@Test
	public void testBlackListNumTasksGreaterThanBLFails() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 1, 2);
		unitManager.addUnit(brokerUnit);
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		peerUnit.initKeys();
		workerUnit.initKeys();
		brokerUnit.initKeys();

//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		peerUnit.addUser(brokerUnit.getLogin());
		int jobid = brokerUnit.addJob( UnitUtil.buildAFailJobExecutionError( 2 ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobid ) );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		// Worker will be scheduled to one task only
		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( job.getState(), GridProcessState.RUNNING );

		TaskStatusInfo task1 = job.getTaskByID( 1 );
		assertEquals( GridProcessState.FAILED, task1.getState() );

		TaskStatusInfo task2 = job.getTaskByID( 2 );
		assertEquals( GridProcessState.UNSTARTED, task2.getState() );

		Set<WorkerStatusInfo> workers = brokerUnit.getWorkersByJob().get( jobid );
		assertTrue( workers.isEmpty() );
	}

	@Test
	public void testBlackListNumTasksLesserThanBLFails() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 3, 2);
		unitManager.addUnit(brokerUnit);
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );

		peerUnit.initKeys();
		workerUnit.initKeys();
		brokerUnit.initKeys();

//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		peerUnit.addUser(brokerUnit.getLogin());
		int jobid = brokerUnit.addJob( UnitUtil.buildAFailJobExecutionError( 2 ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, jobid,
			GridProcessState.FAILED ) );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );

		// Worker will be scheduled to both tasks
		JobStatusInfo job = brokerUnit.getJob( jobid );
		assertEquals( job.getState(), GridProcessState.FAILED );

		TaskStatusInfo task1 = job.getTaskByID( 1 );
		assertEquals( GridProcessState.FAILED, task1.getState() );

		TaskStatusInfo task2 = job.getTaskByID( 2 );
		assertEquals( GridProcessState.FAILED, task2.getState() );

		Set<WorkerStatusInfo> workers = brokerUnit.getWorkersByJob().get( jobid );
		assertNull( workers );
	}

	@Test
	public void testBlackListAfterReplicationDone() throws Exception {

		TaskSpecification taskSpec1 = new TaskSpecification( new IOBlock(), "sleep " + 2 + " && exit " + 1, new IOBlock(), null );
		TaskSpecification taskSpec2 = new TaskSpecification( new IOBlock(), "sleep " + 10 + " && exit " + 1, new IOBlock(), null );
		TaskSpecification taskSpec3 = new TaskSpecification( new IOBlock(), "sleep " + 30 + " && exit " + 1, new IOBlock(), null );

		LinkedList<TaskSpecification> specs = new LinkedList<TaskSpecification>();
		specs.add( taskSpec1 );
		specs.add( taskSpec2 );
		specs.add( taskSpec3 );

		JobSpecification eventualFailJob = new JobSpecification( "EventualFailJob", "", specs );

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 4, 100, 1);
		unitManager.addUnit(brokerUnit);
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		
		WorkerUnit workerUnit = unitManager.buildNewUnit(WorkerUnit.class);
		WorkerUnit workerUnit2 = unitManager.buildNewUnit(WORKER2_PROPERTIES_FILENAME, WorkerUnit.class);
		WorkerUnit workerUnit3 = unitManager.buildNewUnit(WORKER3_PROPERTIES_FILENAME, WorkerUnit.class);

		peerUnit.initKeys();
		brokerUnit.initKeys();
		workerUnit.initKeys();
		workerUnit2.initKeys();
		workerUnit3.initKeys();

//		peerUnit.setWorkers( workerUnit, workerUnit2, workerUnit3 );
		
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit2,
				LocalWorkerState.IDLE ) );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit3,
				LocalWorkerState.IDLE ) );

		peerUnit.addUser(brokerUnit.getLogin());

		int jobid = brokerUnit.addJob( eventualFailJob );
		
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
				LocalWorkerState.IDLE ), 1000, 50 );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit2,
				LocalWorkerState.IDLE ), 1000, 50 );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit3,
				LocalWorkerState.IDLE ), 1000, 50 );

		Map<Integer,Set<WorkerStatusInfo>> workersByJob = brokerUnit.getWorkersByJob();
		assertTrue( workersByJob.get( jobid ).isEmpty() );

		JobStatusInfo job = brokerUnit.getJob( jobid );
		conditionExpecter.waitUntilConditionIsMet(new BrokerJobRunningCondition(brokerUnit, jobid));
		assertEquals( GridProcessState.RUNNING, job.getState() );

		TaskStatusInfo task1 = job.getTaskByID( 1 );
		assertEquals( GridProcessState.RUNNING, task1.getState() );
		assertTrue( task1.getGridProcesses().size() == 3 );
		
		brokerUnit.showStatus();

		TaskStatusInfo task2 = job.getTaskByID( 2 );
		assertEquals( GridProcessState.RUNNING, task2.getState() );
		assertTrue( task2.getGridProcesses().size() == 2 );
		
		brokerUnit.showStatus();

		TaskStatusInfo task3 = job.getTaskByID( 3 );
		assertEquals( GridProcessState.RUNNING, task3.getState() );
		assertTrue( task3.getGridProcesses().size() == 1 );
		
		brokerUnit.showStatus();

		assertEquals( task1.getReplicaByID( 2 ).getWorkerInfo().getWorkerSpec(), task2.getReplicaByID( 1 ).getWorkerInfo().getWorkerSpec());
		assertEquals( task1.getReplicaByID( 3 ).getWorkerInfo().getWorkerSpec(), task3.getReplicaByID( 1 ).getWorkerInfo().getWorkerSpec() );
		assertEquals( task2.getReplicaByID( 2 ).getWorkerInfo().getWorkerSpec(), task3.getReplicaByID( 1 ).getWorkerInfo().getWorkerSpec() );
	}

}
