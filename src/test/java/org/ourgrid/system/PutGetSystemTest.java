/* Created at 12/12/2006 */

package org.ourgrid.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;

import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.junit.Test;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.util.JavaFileUtil;
import org.ourgrid.common.util.TempFileManager;
import org.ourgrid.system.condition.BrokerHasAPeerInTheState;
import org.ourgrid.system.condition.BrokerJobFinishedCondition;
import org.ourgrid.system.condition.BrokerJobRunningCondition;
import org.ourgrid.system.condition.BrokerJobStateCondition;
import org.ourgrid.system.condition.FileCanBeReadCondition;
import org.ourgrid.system.condition.FileIsExecutableCondition;
import org.ourgrid.system.condition.PeerHasTheWorkerInStateCondition;
import org.ourgrid.system.condition.PeerSetWorkersCondition;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.PeerUnit;
import org.ourgrid.system.units.UnitUtil;
import org.ourgrid.system.units.WorkerUnit;

public class PutGetSystemTest extends AbstractSystemTest {

	/**
	 * 1 peer 1 worker 1 broker run a job that has 1 put verifies if the file is
	 * there and has the same digest
	 */
	@Test
	public void testPutInOneWorker() throws Exception {

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 1 );
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );
		
		//brokerUnit.showGUI();

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.LOGGED ) );
		System.out.println( "===> Set peer <===" );

		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( putSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		File getFile = new File( getDests[0] );
		conditionExpecter.waitUntilConditionIsMet( new FileCanBeReadCondition( getFile ) );

	/*	FileInfo getFileInfo = new FileInfo( getFile );
		FileInfo putFileInfo = new FileInfo( putSourceTempFile );
		assertEquals( putFileInfo.getFileDigest(), getFileInfo.getFileDigest() );*/
		
		assertEquals( JavaFileUtil.getDigestRepresentation(getFile), 
				JavaFileUtil.getDigestRepresentation(putSourceTempFile) );

		workerUnit.stop();
		System.out.println( "===> Worker stopped <===" );
		assertFalse( new File( playpenDests[0] ).exists() );
	}

	@Test
	public void testPutZeroLengthFile() throws Exception {

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 0 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10 ,1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );
		
		//brokerUnit.showGUI();

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Set peer <===" );

		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( putSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		File getFile = new File( getDests[0] );
		conditionExpecter.waitUntilConditionIsMet( new FileCanBeReadCondition( getFile ) );

		assertEquals( 0, getFile.length() );

		/*	FileInfo getFileInfo = new FileInfo( getFile );
		FileInfo putFileInfo = new FileInfo( putSourceTempFile );
		assertEquals( putFileInfo.getFileDigest(), getFileInfo.getFileDigest() );*/
		
		assertEquals( JavaFileUtil.getDigestRepresentation(getFile), 
				JavaFileUtil.getDigestRepresentation(putSourceTempFile) );
	}


	/**
	 * 1 peer 1 worker 1 broker run a job that has 1 put verifies if the file is
	 * there and has the same digest
	 */
	@Test
	public void testPutInOneWorkerAndTestPermissions() throws Exception {

		File putSourceTempFile = TempFileManager.createTempFile( getClass().getSimpleName(), "", tempFileDir );

		FileWriter writer = new FileWriter( putSourceTempFile );
		writer.write( "exit 0" );
		writer.close();

		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );
		
		//brokerUnit.showGUI();

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Set peer <===" );

		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( 1, Integer.MAX_VALUE, putSources,
			playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobID ) );
		System.out.println( "===> job is running <===" );

		final String currentPlaypenDir = workerUnit.getCurrentPlaypenDir();
		File playpenDir = new File( currentPlaypenDir );
		File playpenFile = new File( playpenDir, playpenDests[0] );

		conditionExpecter.waitUntilConditionIsMet( new FileIsExecutableCondition( playpenFile ) );

		assertTrue( playpenFile.canWrite() );
		
		/*	FileInfo getFileInfo = new FileInfo( playpenFile );
		FileInfo putFileInfo = new FileInfo( putSourceTempFile );
		assertEquals( putFileInfo.getFileDigest(), getFileInfo.getFileDigest() );*/
		
		assertEquals( JavaFileUtil.getDigestRepresentation(playpenFile), 
				JavaFileUtil.getDigestRepresentation(putSourceTempFile) );

		workerUnit.stop();
		System.out.println( "===> Worker stopped <===" );
		assertFalse( new File( playpenDests[0] ).exists() );
	}


	/**
	 * 1 peer 1 worker 1 broker run a job that has 20 put files verifies if the
	 * files is there and if they have the same digest
	 */
	@Test
	public void testPutInOneWorker_ManyFiles() throws Exception {

		final int numberOFiles = 20;
		final int fileSize = 10 * 4096;

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );
		
		//brokerUnit.showGUI();

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Set peer <===" );

		File[ ] putFiles = new File[ numberOFiles ];
		File[ ] getFiles = new File[ numberOFiles ];
		String[ ] putSources = new String[ numberOFiles ];
		String[ ] playpenDests = new String[ numberOFiles ];
		String[ ] getDests = new String[ numberOFiles ];
		for ( int i = 0; i < numberOFiles; i++ ) {
			putFiles[i] = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "", tempFileDir,
				fileSize );
			putSources[i] = putFiles[i].getPath();
			playpenDests[i] = putFiles[i].getName();
			getDests[i] = putFiles[i].getPath() + "_get";
			getFiles[i] = new File( getDests[i] );
		}

		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( putSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		for ( int i = 0; i < numberOFiles; i++ ) {
			assertTrue( getFiles[i].canRead() );
			//assertEquals( new FileInfo( putFiles[i] ).getFileDigest(), new FileInfo( getFiles[i] ).getFileDigest() );
			
			assertEquals(JavaFileUtil.getDigestRepresentation(putFiles[i]), JavaFileUtil.getDigestRepresentation(getFiles[i]));
		}
	}


	/**
	 * 1 peer 3 workers 1 broker run 3 jobs with 1 tasks and each job put 1 file
	 * (different files) verifies if the files is there and if they have the
	 * same digest
	 */
	@Test
	public void testPutInThreeWorkers() throws Exception {

		final int numberOFiles = 20;
		final int fileSize = 10 * 4096;
		final int numberOfWorkers = 3;

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 10, 1, 1);
		unitManager.addUnit(brokerUnit);

		WorkerUnit workerUnit1 = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit1.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit1.setStorageRootPath( TEMP_TEST_DIR );

		WorkerUnit workerUnit2 = unitManager.buildNewUnit( WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		workerUnit2.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit2.setStorageRootPath( TEMP_TEST_DIR );

		WorkerUnit workerUnit3 = unitManager.buildNewUnit( WORKER3_PROPERTIES_FILENAME, WorkerUnit.class );
		workerUnit3.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit3.setStorageRootPath( TEMP_TEST_DIR );

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit1.initKeys();
		workerUnit2.initKeys();
		workerUnit3.initKeys();
		System.out.println( "===> Remote entities running (3 worker, 1 peer, 1 broker) <===" );
		
		//brokerUnit.showGUI();

//		peerUnit.setWorkers( workerUnit1, workerUnit2, workerUnit3 );
		conditionExpecter.waitUntilConditionIsMet( new PeerSetWorkersCondition( peerUnit, numberOfWorkers ) );
		System.out.println( "===> Set workers <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Set peer <===" );

		File[ ][ ] putFiles = new File[ numberOfWorkers ][ numberOFiles ];
		File[ ][ ] getFiles = new File[ numberOfWorkers ][ numberOFiles ];
		String[ ][ ] putSources = new String[ numberOfWorkers ][ numberOFiles ];
		String[ ][ ] playpenDests = new String[ numberOfWorkers ][ numberOFiles ];
		String[ ][ ] getDests = new String[ numberOfWorkers ][ numberOFiles ];

		for ( int j = 0; j < numberOfWorkers; j++ ) {
			for ( int i = 0; i < numberOFiles; i++ ) {
				putFiles[j][i] = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
					tempFileDir, fileSize );
				putSources[j][i] = putFiles[j][i].getPath();
				playpenDests[j][i] = putFiles[j][i].getName();
				getDests[j][i] = putFiles[j][i].getPath() + "_get";
				getFiles[j][i] = new File( getDests[j][i] );
			}
		}

		int[ ] jobID = new int[ numberOfWorkers ];
		for ( int j = 0; j < numberOfWorkers; j++ ) {
			jobID[j] = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( putSources[j], playpenDests[j],
				getDests[j] ) );
		}
		System.out.println( "===> " + numberOfWorkers + " job(s) added <===" );

		for ( int j = 0; j < numberOfWorkers; j++ ) {
			conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID[j] ) );
		}
		System.out.println( "===> " + numberOfWorkers + " job(s) end <===" );

		for ( int j = 0; j < numberOfWorkers; j++ ) {
			for ( int i = 0; i < numberOFiles; i++ ) {
				assertTrue( getFiles[j][i].canRead() );
				/*assertEquals( new FileInfo( putFiles[j][i] ).getFileDigest(), new FileInfo( getFiles[j][i] )
					.getFileDigest() );*/
				
				assertEquals(JavaFileUtil.getDigestRepresentation(putFiles[j][i]), JavaFileUtil.getDigestRepresentation(getFiles[j][i]));
			}
		}

	}


	/**
	 * Verification of override 1 peer 1 worker 1 broker run a job that has 20
	 * put files verifies if the files is there and if they have the same digest
	 * add other job that put the other file with the same name that the first
	 * job had put verifies if the files is there and if they have different
	 * digest (the second and the first) verifies if the files is there and if
	 * they have the same digest (the second)
	 */
	@Test
	public void testOverride() throws Exception {

		final int numberOFiles = 5;
		final int fileSize = 10 * 4096;

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 10, 1, 1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
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

		File[ ] putFiles = new File[ numberOFiles ];
		File[ ] getFiles = new File[ numberOFiles ];
		String[ ] putSources = new String[ numberOFiles ];
		String[ ] playpenDests = new String[ numberOFiles ];
		String[ ] getDests = new String[ numberOFiles ];
		for ( int i = 0; i < numberOFiles; i++ ) {
			putFiles[i] = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "", tempFileDir,
				fileSize );
			putSources[i] = putFiles[i].getPath();
			playpenDests[i] = putFiles[i].getName();
			getDests[i] = putFiles[i].getPath() + "_get";
			getFiles[i] = new File( getDests[i] );
		}

		int firstJobID = brokerUnit
			.addJob( UnitUtil.buildASmallSleepJobWithPuts( putSources, playpenDests, getDests ) );
		System.out.println( "===> first job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, firstJobID ) );
		System.out.println( "===> first job end <===" );

		File[ ] otherFilesToPut = new File[ numberOFiles ];
		String[ ] otherFilePathsToPut = new String[ numberOFiles ];
		for ( int i = 0; i < numberOFiles; i++ ) {
			otherFilesToPut[i] = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
				tempFileDir, fileSize * 2 );
			otherFilePathsToPut[i] = otherFilesToPut[i].getPath();
		}

		int secondJobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( otherFilePathsToPut, playpenDests,
			getDests ) );
		System.out.println( "===> second job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, secondJobID ) );
		System.out.println( "===> second job end <===" );

		for ( int i = 0; i < numberOFiles; i++ ) {
			assertTrue( getFiles[i].canRead() );
			//assertNotEquals( new FileInfo( putFiles[i] ).getFileDigest(), new FileInfo( getFiles[i] ).getFileDigest() );
			
			//assertNotEquals(JavaFileUtil.getDigestRepresentation(putFiles[i]), JavaFileUtil.getDigestRepresentation(getFiles[i]));
			
			/*assertEquals( new FileInfo( otherFilesToPut[i] ).getFileDigest(), new FileInfo( getFiles[i] )
				.getFileDigest() );*/
			
			assertEquals(JavaFileUtil.getDigestRepresentation(otherFilesToPut[i]), JavaFileUtil.getDigestRepresentation(getFiles[i]));
		}

	}


	/**
	 * 1 peer 1 worker 1 broker run a job with 1 task which tries to PUT an
	 * inexistent file verifies if the job fails
	 */
	@Test
	public void testPutInexistentFile() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 10, 1, 1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
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

		String crazyFile = "oxfordMytrociakslkrjiojkmflakd";
		String crazyFile2 = "oxfordMytrociakslkrjiojkmfla3589738974897kd";
		String[ ] putSources = { crazyFile, crazyFile2 };
		String[ ] playpenDests = { crazyFile, crazyFile2 };
		String[ ] getDests = { crazyFile + "_get", crazyFile2 + "_get" };

		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( putSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, jobID, GridProcessState.FAILED ) );
		System.out.println( "===> job failed <===" );
	}


	/**
	 * 1 peer 1 worker 1 broker run a job with 1 task which tries to GET an
	 * inexistent file verifies if the job fails
	 */
	@Test
	public void testGetInexistentFile() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 10, 1, 1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

		brokerUnit.setMaxFails( 1 );
		brokerUnit.setNumberOfReplicaExecutors( 1 );

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

		String[ ] putSources = { TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 ).getPath() };
		String[ ] playpenDests = { "a" };
		String[ ] playpenGets = { "blkadjalskdjalkdjopaeiejiorjo" };
		String[ ] getDests = { TEMP_TEST_DIR + File.separator + "a" };

		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( 1, putSources, playpenDests, playpenGets,
			getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, jobID, GridProcessState.FAILED ) );
		System.out.println( "===> job failed <===" );
	}


	/**
	 * 1 peer 2 worker 2 broker run 2 jobs with 1 task that do puts and gets and
	 * these must be correct
	 */
	@Test
	public void test2Brokers() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 10, 1, 1);
		unitManager.addUnit(brokerUnit);
		
		BrokerUnit otherBrokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 10, 1, 1);
		unitManager.addUnit(otherBrokerUnit);

		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );

		WorkerUnit otherWorkerUnit = unitManager.buildNewUnit( WORKER2_PROPERTIES_FILENAME, WorkerUnit.class );
		otherWorkerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		otherWorkerUnit.setStorageRootPath( TEMP_TEST_DIR );

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		otherWorkerUnit.initKeys();
		otherBrokerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );
		
		//brokerUnit.showGUI();
		//otherbrokerUnit.showGUI();

//		peerUnit.setWorkers( workerUnit, otherWorkerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> One MG Set peer <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( otherBrokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Other MG Set peer <===" );

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };
		String[ ] otherPlaypenDests = { putSourceTempFile.getName() + "_1" };
		String[ ] otherGetDests = { putSourceTempFile.getPath() + "_get1" };

		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( putSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );

		int otherJobID = otherBrokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( putSources, otherPlaypenDests,
			otherGetDests ) );
		System.out.println( "===> other job added <===" );

		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( otherBrokerUnit, otherJobID ) );
		System.out.println( "===> other job end <===" );

		File getFile = new File( getDests[0] );
		conditionExpecter.waitUntilConditionIsMet( new FileCanBeReadCondition( getFile ) );
		assertEquals( JavaFileUtil.getDigestRepresentation(getFile), 
				JavaFileUtil.getDigestRepresentation(putSourceTempFile) );
		System.out.println( "===> one file could be read <===" );

		File otherGetFile = new File( otherGetDests[0] );
		conditionExpecter.waitUntilConditionIsMet( new FileCanBeReadCondition( otherGetFile ) );
		assertEquals( JavaFileUtil.getDigestRepresentation(putSourceTempFile), 
				JavaFileUtil.getDigestRepresentation(otherGetFile) );
		System.out.println( "===> other file could be read <===" );
	}

	@Test
	public void testBigFilePut() throws Exception {

		FileTransferNegotiator.IBB_ONLY = true;

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 10, 1, 1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );
		
		//brokerUnit.showGUI();

		//		peerUnit.setWorkers( workerUnit );
		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Set workers <===" );

		peerUnit.addUser(brokerUnit.getLogin());
		conditionExpecter.waitUntilConditionIsMet( new BrokerHasAPeerInTheState( brokerUnit, peerUnit, PeerTestState.UP ) );
		System.out.println( "===> Set peer <===" );

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 1024 * 1024 * 5 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( putSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( putSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( putSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobFinishedCondition( brokerUnit, jobID ) );
		System.out.println( "===> job end <===" );

		File getFile = new File( getDests[0] );
		conditionExpecter.waitUntilConditionIsMet( new FileCanBeReadCondition( getFile ) );
		
		/*	FileInfo getFileInfo = new FileInfo( getFile );
		FileInfo putFileInfo = new FileInfo( putSourceTempFile );
		assertEquals( putFileInfo.getFileDigest(), getFileInfo.getFileDigest() );*/
		
		assertEquals( JavaFileUtil.getDigestRepresentation(getFile), 
				JavaFileUtil.getDigestRepresentation(putSourceTempFile) );
	}

	@Test
	public void testBigFilePutAndCancelJob() throws Exception {

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 1, 10, 1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
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
			tempFileDir, 1024 * 1024 * 5 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };
		String[ ] getDests = { putSourceTempFile.getPath() + "_get" };

		int jobID = brokerUnit.addJob( UnitUtil.buildASmallSleepJobWithPuts( putSources, playpenDests, getDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobRunningCondition( brokerUnit, jobID ) );
		Thread.sleep( 1000 );
		brokerUnit.cancelJob( jobID );
		conditionExpecter
			.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, jobID, GridProcessState.CANCELLED ) );
		System.out.println( "===> job cancelled <===" );

		conditionExpecter.waitUntilConditionIsMet( new PeerHasTheWorkerInStateCondition( peerUnit, workerUnit,
			LocalWorkerState.IDLE ) );
		System.out.println( "===> Worker is idle <===" );

		File getFile = new File( getDests[0] );
		assertFalse( getFile.exists() );
	}


	/**
	 * Try to make a put on the storage directory. Transfer will be rejected
	 */
	@Test
	public void testWrongPutOnStorage() throws Exception {

		File putSourceTempFile = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "",
			tempFileDir, 4096 * 10 );
		String[ ] putSources = { putSourceTempFile.getPath() };
		String[ ] playpenDests = { putSourceTempFile.getName() };

		BrokerUnit brokerUnit = new BrokerUnit(BrokerUnit.BROKER_PROPERTIES_FILENAME, 1, 10, 1, 1);
		unitManager.addUnit(brokerUnit);
		WorkerUnit workerUnit = unitManager.buildNewUnit( WorkerUnit.class );
		workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
		workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );

		brokerUnit.initKeys();
		peerUnit.initKeys();
		workerUnit.initKeys();
		System.out.println( "===> Remote entities running (1 worker, 1 peer, 1 broker) <===" );

		//		peerUnit.setWorkers( workerUnit );

		peerUnit.addUser(brokerUnit.getLogin());

		int jobID = brokerUnit.addJob( UnitUtil.buildAJobWithWrongPuts( 1, putSources, playpenDests ) );
		System.out.println( "===> job added <===" );
		conditionExpecter.waitUntilConditionIsMet( new BrokerJobStateCondition( brokerUnit, jobID, GridProcessState.FAILED ) );
		System.out.println( "===> job end <===" );

		workerUnit.stop();
		System.out.println( "===> Worker stopped <===" );
		assertFalse( new File( playpenDests[0] ).exists() );
	}
}
