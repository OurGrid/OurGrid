/* Created at 13/12/2006 */

package org.ourgrid.system;

import static java.io.File.separator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.util.JavaFileUtil;
import org.ourgrid.common.util.TempFileManager;
import org.ourgrid.system.condition.BrokerHasAPeerInTheState;
import org.ourgrid.system.condition.BrokerJobFinishedCondition;
import org.ourgrid.system.condition.BrokerJobRunningCondition;
import org.ourgrid.system.condition.BrokerNumberOfWorkersCondition;
import org.ourgrid.system.condition.FileCanBeReadCondition;
import org.ourgrid.system.condition.PeerHasTheWorkerInStateCondition;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.PeerUnit;
import org.ourgrid.system.units.UnitUtil;
import org.ourgrid.system.units.WorkerUnit;

public class StoreSystemTest extends AbstractSystemTest {

	private static String storagePath = new File( TEMP_TEST_DIR ).getAbsolutePath();


	/**
	 * 1 mg 1 peer 1 worker store a file verifies if the file exists verifies if
	 * the file continue existing after worker shutdown
	 */
	@Test
	public void testStore() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( storagePath );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

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
		String[ ] storeSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() + "_remote" };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithStores( storeSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		Thread.sleep( 2000 );
		System.out.println( "===> job end <===" );

		File getFile = new File( getDests[0] );
		conditionExpecter.waitUntilConditionIsMet( new FileCanBeReadCondition( getFile ) );

		assertEquals( JavaFileUtil.getDigestRepresentation(getFile), JavaFileUtil.getDigestRepresentation(putSourceTempFile) );

		workerUnit.stop();
		System.out.println( "===> Worker stopped <===" );
		assertTrue( new File( storagePath + File.separator + putSourceTempFile.getName() + "_remote" ).exists() );
		System.out.println( "===> Test Finished Successfully <===" );
	}


	/**
	 * 1 mg 1 peer 1 worker store a file store other file with the same name as
	 * the first verifies if the digest is the same as the second verifies if
	 * the file continue existing after worker shutdown
	 */
	@Test
	public void testStore1() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( storagePath );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

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
		String[ ] playpenDests = { putSourceTempFile.getName() + "_remote" };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		// First job
		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithStores( putSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		String storedFilePath = storagePath + File.separator + putSourceTempFile.getName() + "_remote";
/*		FileInfo getFileInfo = new FileInfo( storedFilePath );
		FileInfo putFileInfo = new FileInfo( putSourceTempFile );
		assertEquals( putFileInfo.getFileDigest(), getFileInfo.getFileDigest() );*/
		
		assertEquals( JavaFileUtil.getDigestRepresentation(new File(storedFilePath)), 
				JavaFileUtil.getDigestRepresentation(putSourceTempFile) );

		// overidden the put file
		putSourceTempFile.delete();
		putSourceTempFile = new File( putSourceTempFile.getPath() );
		TempFileManager.createTempFileWithBogusData( putSourceTempFile.getName(), tempFileDir, 4096 * 20 );

		// Second job
		jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithStores( putSources, playpenDests, getDests ) );
		System.out.println( "===> second job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> second job end <===" );

/*		getFileInfo = new FileInfo( storedFilePath );
		putFileInfo = new FileInfo( putSourceTempFile );
		assertEquals( putFileInfo.getFileDigest(), getFileInfo.getFileDigest() );*/
		
		assertEquals( JavaFileUtil.getDigestRepresentation(new File(storedFilePath)), 
				JavaFileUtil.getDigestRepresentation(putSourceTempFile) );

		workerUnit.stop();
		System.out.println( "===> Worker stopped <===" );
		assertTrue( new File( storedFilePath ).exists() );
		System.out.println( "===> Test Finished Successfully <===" );
	}


	/**
	 * 1 mg 1 peer 1 worker store a file get the last modification time of the
	 * stored file store other file with the same name and the same content
	 * verifies if the time is the first, in other words, the file was not
	 * overriden verifies if the file continue existing after worker shutdown
	 */
	@Test
	public void testStore2() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( storagePath );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

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
		String[ ] playpenDests = { putSourceTempFile.getName() + "_remote" };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		// First job
		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithStores( putSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		String storedFilePath = storagePath + File.separator + putSourceTempFile.getName() + "_remote";
/*		FileInfo getFileInfo = new FileInfo( storedFilePath );
		FileInfo putFileInfo = new FileInfo( putSourceTempFile );
		assertEquals( putFileInfo.getFileDigest(), getFileInfo.getFileDigest() );*/
		
		assertEquals( JavaFileUtil.getDigestRepresentation(new File(storedFilePath)), 
				JavaFileUtil.getDigestRepresentation(putSourceTempFile) );

		File storedFile = new File( storedFilePath );
		long lastModification = storedFile.lastModified();

		// Second job
		jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithStores( putSources, playpenDests, getDests ) );
		System.out.println( "===> second job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> second job end <===" );

		File newStoredFile = new File( storedFilePath );
		assertEquals( lastModification, newStoredFile.lastModified() );

		workerUnit.stop();
		System.out.println( "===> Worker stopped <===" );
		assertTrue( new File( storedFilePath ).exists() );
		System.out.println( "===> Test Finished Successfully <===" );
	}


	/**
	 * 1 mg 1 peer 2 worker adds 2 jobs that stores 2 big files with same name
	 * and different content The result stored file must me the same digest as
	 * the second job big file
	 */
	@Test
	public void testStore3() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( storagePath );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

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
			tempFileDir, 1024 * 1024 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() + "_remote" };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithStores( putSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ), 300, 1000 );
		System.out.println( "===> job end <===" );

		File getFile = new File( getDests[0] );
		conditionExpecter.waitUntilConditionIsMet( new FileCanBeReadCondition( getFile ), 1000, 50 );
		System.out.println( "===> File can be read <===" );

		String storedFilePath = storagePath + File.separator + putSourceTempFile.getName() + "_remote";
/*		FileInfo getFileInfo = new FileInfo( storedFilePath );
		FileInfo putFileInfo = new FileInfo( putSourceTempFile );
		assertEquals( putFileInfo.getFileDigest(), getFileInfo.getFileDigest() );*/
		
		assertEquals( JavaFileUtil.getDigestRepresentation(new File(storedFilePath)), 
				JavaFileUtil.getDigestRepresentation(putSourceTempFile) );

		// overidden the put file
		putSourceTempFile.delete();
		putSourceTempFile = new File( putSourceTempFile.getPath() );
		TempFileManager.createTempFileWithBogusData( putSourceTempFile.getName(), tempFileDir, 1024 * 1024 * 10 );

		// Second job
		jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithStores( putSources, playpenDests, getDests ) );
		System.out.println( "===> second job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> second job end <===" );

/*		getFileInfo = new FileInfo( storedFilePath );
		putFileInfo = new FileInfo( putSourceTempFile );
		assertEquals( putFileInfo.getFileDigest(), getFileInfo.getFileDigest() );*/
		
		assertEquals( JavaFileUtil.getDigestRepresentation(new File(storedFilePath)), 
				JavaFileUtil.getDigestRepresentation(putSourceTempFile) );

		workerUnit.stop();
		System.out.println( "===> Worker stopped <===" );
		assertTrue( new File( storedFilePath ).exists() );
		System.out.println( "===> Test Finished Successfully <===" );
	}

	@Test
	public void testStoreLock1() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 4, 1, 10, 1);
		unitManager.addUnit(brokerUnit);
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		WorkerUnit workerUnit2 = unitManager.buildNewUnit( WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );;
		WorkerUnit workerUnit3 = unitManager.buildNewUnit( WORKER3_PROPERTIES_FILENAME, WorkerUnit.class );
		WorkerUnit workerUnit4 = unitManager.buildNewUnit( WORKER4_PROPERTIES_FILENAME, WorkerUnit.class );;		
		
		
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( storagePath + separator + "1" );

		workerUnit2.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit2.setStorageRootPath( storagePath + separator + "2" );

		workerUnit3.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit3.setStorageRootPath( storagePath + separator + "3" );

		workerUnit4.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit4.setStorageRootPath( storagePath + separator + "4" );

		brokerUnit.initKeys();
		peerUnit.initKeys();

//		peerUnit.setWorkers( workerUnit, workerUnit2, workerUnit3, workerUnit4 );
		peerUnit.addUser(brokerUnit.getLogin());

		System.out.println( "===> Units OK <===" );

		File putSourceTempFile1 = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName() + "1-", "",
			tempFileDir, 1024 * 1024 * 1 );
		File putSourceTempFile2 = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName() + "2-", "",
			tempFileDir, 1024 * 1024 * 1 );
		String[ ] putSources = { putSourceTempFile1.getPath(), putSourceTempFile2.getPath() };
		String[ ] playpenDests = { putSourceTempFile1.getName() + "_remote", putSourceTempFile2.getName() + "_remote" };
		String[ ] getDests = { putSourceTempFile1.getPath() + "_get", putSourceTempFile2.getPath() + "_get" };

		JobSpecification sleepJob = UnitUtil.buildASleepJobWithStores( 1, putSources, playpenDests, playpenDests, getDests,
			10 );
		int jobID = brokerUnit.addJob( sleepJob );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobID ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerNumberOfWorkersCondition( brokerUnit, 4 ) );

		JobStatusInfo job = brokerUnit.getJob( jobID );

		assertTrue( job.getTaskByID( 1 ).getNumberOfRunningReplicas() == 4 );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );

		jobID = brokerUnit.addJob( sleepJob );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobID ) );
		conditionExpecter.waitUntilConditionIsMet( new BrokerNumberOfWorkersCondition( brokerUnit, 4 ) );

		job = brokerUnit.getJob( jobID );

		assertTrue( job.getTaskByID( 1 ).getNumberOfRunningReplicas() == 4 );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );

		System.out.println( "===> job end <===" );
	}
}
