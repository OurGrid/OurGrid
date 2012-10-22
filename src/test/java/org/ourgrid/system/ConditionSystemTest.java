/* Created at 15/12/2006 */

package org.ourgrid.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Test;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.util.JavaFileUtil;
import org.ourgrid.common.util.TempFileManager;
import org.ourgrid.system.condition.BrokerHasAPeerInTheState;
import org.ourgrid.system.condition.BrokerJobFinishedCondition;
import org.ourgrid.system.condition.BrokerJobStateCondition;
import org.ourgrid.system.condition.FileCanBeReadCondition;
import org.ourgrid.system.condition.PeerHasTheWorkerInStateCondition;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.PeerUnit;
import org.ourgrid.system.units.UnitUtil;
import org.ourgrid.system.units.WorkerUnit;

public class ConditionSystemTest extends AbstractSystemTest {

	/**
	 * 1 peer 1 worker (os=windows) 1 broker run a job that has 1 put with
	 * os==windows condition verifies if the file is there and has the same
	 * digest
	 */
	@Test
	public void testPutInOneWorker() throws Exception {

		System.out.println( "===> Test started <===" );
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 1);
		unitManager.addUnit(brokerUnit);
		System.out.println( "===> MGUnit created <===" );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		workerUnit.addProperty( "os", "windows" );
		System.out.println( "===> WorkerUnit created <===" );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		System.out.println( "===> PeerUnit created <===" );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );

//		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.LOGGED ) );
		System.out.println( "===> Set peer <===" );

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		String[ ] putConditions = { "os == windows" };
		String[ ] getConditions = { "" };
		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( 1, putConditions, putSources,
			playpenDests, getConditions, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		File getFile = new File( getDests[0] );
		conditionExpecter.waitUntilConditionIsMet( new FileCanBeReadCondition( getFile ) );
		
		assertEquals( JavaFileUtil.getDigestRepresentation(getFile), 
				JavaFileUtil.getDigestRepresentation(putSourceTempFile) );

		workerUnit.stop();
		System.out.println( "===> Worker stopped <===" );
		assertFalse( new File( playpenDests[0] ).exists() );
	}


	/**
	 * 1 peer 1 worker (os=linux) 1 broker run a job that has 1 put with
	 * os==windows condition Verifies that the file must not be there, in other
	 * words, an application error must occur. The job must fail
	 */
	@Test
	public void testPutInOneWorkerWithConditionThatDoesNotMatch() throws Exception {

		System.out.println( "===> Test started <===" );
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 1);
		unitManager.addUnit(brokerUnit);
		System.out.println( "===> MGUnit created <===" );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		workerUnit.addProperty( "os", "linux" );
		System.out.println( "===> WorkerUnit created <===" );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		System.out.println( "===> PeerUnit created <===" );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.LOGGED ) );
		System.out.println( "===> Set peer <===" );

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		String[ ] putConditions = { "os == windows" };
		String[ ] getConditions = { "" };
		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( 1, putConditions, putSources,
			playpenDests, getConditions, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, jobID, GridProcessState.FAILED ) );
		System.out.println( "===> job failed <===" );
	}


	/**
	 * 1 peer 1 worker (os=linux) 1 broker run a job that has 1 put with
	 * os!=linux condition Verifies that the file must not be there, in other
	 * words, an application error must occur. The job must fail
	 */
	@Test
	public void testPutInOneWorkerWithConditionThatDoesNotMatch1() throws Exception {

		System.out.println( "===> Test started <===" );
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 1);
		unitManager.addUnit(brokerUnit);
		System.out.println( "===> MGUnit created <===" );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		workerUnit.addProperty( "os", "linux" );
		System.out.println( "===> WorkerUnit created <===" );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		System.out.println( "===> PeerUnit created <===" );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Set peer <===" );

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		String[ ] putConditions = { "os != linux" };
		String[ ] getConditions = { "" };
		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( 1, putConditions, putSources,
			playpenDests, getConditions, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, jobID, GridProcessState.FAILED ) );
		System.out.println( "===> job failed <===" );
	}


	/**
	 * 1 peer 1 worker (no property) 1 broker run a job that has 1 put with
	 * os==linux condition Verifies that the file must not be there, in other
	 * words, an application error must occur. The job must fail
	 */
	@Test
	public void testPutInOneWorkerWithConditionThatDoesNotMatch2() throws Exception {

		System.out.println( "===> Test started <===" );
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1);
		unitManager.addUnit(brokerUnit);
		brokerUnit.setMaxFails( 1 );
		brokerUnit.setNumberOfReplicaExecutors( 1 );
		System.out.println( "===> MGUnit created <===" );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		System.out.println( "===> WorkerUnit created <===" );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		System.out.println( "===> PeerUnit created <===" );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Set peer <===" );

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		String[ ] putConditions = { "os != linux" };
		String[ ] getConditions = { "" };
		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( 1, putConditions, putSources,
			playpenDests, getConditions, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, jobID, GridProcessState.FAILED ) );
		System.out.println( "===> job failed <===" );
	}


	/**
	 * 1 peer 1 worker (os=windows) 1 broker run a job that has 1 put with
	 * os==windows condition and gets with os==windows verifies if the file is
	 * there and has the same digest
	 */
	@Test
	public void testGetWithConditionInOneWorker() throws Exception {

		System.out.println( "===> Test started <===" );
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1);
		unitManager.addUnit(brokerUnit);
		brokerUnit.setMaxFails( 1 );
		brokerUnit.setNumberOfReplicaExecutors( 1 );
		System.out.println( "===> MGUnit created <===" );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		workerUnit.addProperty( "os", "windows" );
		System.out.println( "===> WorkerUnit created <===" );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		System.out.println( "===> PeerUnit created <===" );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Set peer <===" );

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		String[ ] putConditions = { "" };
		String[ ] getConditions = { "os == windows" };
		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( 1, putConditions, putSources,
			playpenDests, getConditions, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		File getFile = new File( getDests[0] );
		conditionExpecter.waitUntilConditionIsMet( new FileCanBeReadCondition( getFile ) );

		/*FileInfo getFileInfo = new FileInfo( getFile );
		FileInfo putFileInfo = new FileInfo( putSourceTempFile );
		assertEquals( putFileInfo.getFileDigest(), getFileInfo.getFileDigest() );*/
		
		assertEquals( JavaFileUtil.getDigestRepresentation(getFile), 
				JavaFileUtil.getDigestRepresentation(putSourceTempFile) );

		workerUnit.stop();
		System.out.println( "===> Worker stopped <===" );
		assertFalse( new File( playpenDests[0] ).exists() );
	}


	/**
	 * 1 peer 1 worker (os=linux) 1 broker run a job that has 1 put with
	 * os==linux condition and get with os==windows The job must finish but the
	 * file must not be gotten
	 */
	@Test
	public void testGetInOneWorkerWithConditionThatDoesNotMatch() throws Exception {

		System.out.println( "===> Test started <===" );
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1);
		unitManager.addUnit(brokerUnit);
		brokerUnit.setMaxFails( 1 );
		brokerUnit.setNumberOfReplicaExecutors( 1 );
		System.out.println( "===> MGUnit created <===" );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		workerUnit.addProperty( "os", "linux" );
		System.out.println( "===> WorkerUnit created <===" );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		System.out.println( "===> PeerUnit created <===" );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Set peer <===" );

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		String[ ] putConditions = { "" };
		String[ ] getConditions = { "os == windows" };
		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( 1, putConditions, putSources,
			playpenDests, getConditions, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		File getFile = new File( getDests[0] );
		assertFalse( getFile.exists() );
		System.out.println( "===> assertion ok <===" );
	}


	/**
	 * 1 peer 1 worker (os=linux) 1 broker run a job that has 1 put with
	 * os==linux condition and get with os!=linux The job must finish but the
	 * file must not be gotten
	 */
	@Test
	public void testGetInOneWorkerWithConditionThatDoesNotMatch1() throws Exception {

		System.out.println( "===> Test started <===" );
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1);
		unitManager.addUnit(brokerUnit);
		brokerUnit.setMaxFails( 1 );
		brokerUnit.setNumberOfReplicaExecutors( 1 );
		System.out.println( "===> MGUnit created <===" );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		workerUnit.addProperty( "os", "linux" );
		System.out.println( "===> WorkerUnit created <===" );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		System.out.println( "===> PeerUnit created <===" );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Set peer <===" );

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		String[ ] putConditions = { "" };
		String[ ] getConditions = { "os != linux" };
		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( 1, putConditions, putSources,
			playpenDests, getConditions, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		File getFile = new File( getDests[0] );
		assertFalse( getFile.exists() );
		System.out.println( "===> assertion ok <===" );
	}


	/**
	 * 1 peer 1 worker (no property) 1 broker run a job that has 1 put with
	 * os==linux condition and get with os==linux The job must finish but the
	 * file must not be gotten
	 */
	@Test
	public void testGetInOneWorkerWithConditionThatDoesNotMatch2() throws Exception {

		System.out.println( "===> Test started <===" );
		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1);
		unitManager.addUnit(brokerUnit);
		brokerUnit.setMaxFails( 1 );
		brokerUnit.setNumberOfReplicaExecutors( 1 );
		System.out.println( "===> MGUnit created <===" );
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		System.out.println( "===> WorkerUnit created <===" );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		System.out.println( "===> PeerUnit created <===" );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Set peer <===" );

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		String[ ] putConditions = { "" };
		String[ ] getConditions = { "os == linux" };
		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( 1, putConditions, putSources,
			playpenDests, getConditions, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		File getFile = new File( getDests[0] );
		assertFalse( getFile.exists() );
		System.out.println( "===> assertion ok <===" );
	}
}
