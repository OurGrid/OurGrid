package org.ourgrid.system;


public class StressSystemsTest extends AbstractSystemTest {

	public void testPutGet_ManyWorkers_ManyFiles_ManyTasks() throws Exception {

		//TODO Diogo
	/*	final int numberOfFiles = 20;
		final int fileSize = 1024 * 100;

		List<BrokerUnit> brokerUnits = unitManager.buildManyUnits( BrokerUnit.class, 5 );
		List<WorkerUnit> workerUnits = unitManager.buildManyUnits( WorkerUnit.class, 20 );
		for ( WorkerUnit workerUnit : workerUnits ) {
			workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
			workerUnit.setStorageRootPath( TEMP_TEST_DIR );
			workerUnit.initKeys();
		}

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		unitManager.startUnits( peerUnit );

		for ( BrokerUnit brokerUnit : brokerUnits ) {
			brokerUnit.setMaxFails( 1 );
			brokerUnit.setMaxReplicas( 1 );
			brokerUnit.setNumberOfReplicaExecutors( 10 );
			brokerUnit.initKeys();
			brokerUnit.setPeers( peerUnit );
		}

		peerUnit.setWorkers( workerUnits );

		File[ ] putFiles = new File[ numberOfFiles ];
		File[ ] getFiles = new File[ numberOfFiles ];
		String[ ] putSources = new String[ numberOfFiles ];
		String[ ] playpenDests = new String[ numberOfFiles ];
		String[ ] getDests = new String[ numberOfFiles ];
		for ( int i = 0; i < numberOfFiles; i++ ) {
			putFiles[i] = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "", tempFileDir,
				fileSize );
			putSources[i] = putFiles[i].getPath();
			playpenDests[i] = putFiles[i].getName();
			getDests[i] = putFiles[i].getPath() + "_get";
			getFiles[i] = new File( getDests[i] );
		}

		JobSpec jobToAdd = UnitUtil.buildASmallSleepJobWithPuts( 10, putSources, playpenDests, getDests );

		for ( BrokerUnit brokerUnit : brokerUnits ) {
			brokerUnit.addJob( jobToAdd );
			brokerUnit.addJob( jobToAdd );
		}

		for ( BrokerUnit brokerUnit : brokerUnits ) {
			conditionExpecter.waitUntilConditionIsMet( new BrokerAllJobsFinishedCondition( brokerUnit ), 1000, 10000 );
		}

		for ( int i = 0; i < numberOfFiles; i++ ) {
		assertTrue( getFiles[i].canRead() );
		assertEquals( new FileInfo( putFiles[i] ).getFileDigest(), new FileInfo( getFiles[i] ).getFileDigest() );
		}
	
		for ( int i = 0; i < numberOfFiles; i++ ) {
			assertTrue( getFiles[i].canRead() );
			assertEquals( JavaFileUtil.getDigestRepresentation(putFiles[i]), JavaFileUtil.getDigestRepresentation(getFiles[i]) );
		}*/
	}


	public void testExecuteJobsOnDistributedWorkers() throws Exception {

		//TODO Diogo
		/*final int numberOfFiles = 20;
		final int fileSize = 1024 * 1024;

		List<BrokerUnit> brokerUnits = unitManager.buildManyUnits( BrokerUnit.class, 5 );
		List<WorkerUnit> workerUnits = unitManager.buildManyUnits( WorkerUnit.class, 20, getAvailableWorkerMachines() );
		for ( WorkerUnit workerUnit : workerUnits ) {
			workerUnit.setPlaypenRootPath( TEMP_TEST_DIR );
			workerUnit.setStorageRootPath( TEMP_TEST_DIR );
		}

		unitManager.deployUnits( workerUnits );

		for ( WorkerUnit workerUnit : workerUnits ) {
			assertTrue( workerUnit.isRunning() );
		}

		PeerUnit peerUnit = unitManager.buildNewUnit( PeerUnit.class );
		unitManager.startUnits( peerUnit );

		for ( BrokerUnit brokerUnit : brokerUnits ) {
			brokerUnit.setMaxFails( 1 );
			brokerUnit.setMaxReplicas( 1 );
			brokerUnit.setNumberOfReplicaExecutors( 10 );
			brokerUnit.initKeys();
			brokerUnit.setPeers( peerUnit );
		}

		peerUnit.setWorkers( workerUnits );

		File[ ] putFiles = new File[ numberOfFiles ];
		File[ ] getFiles = new File[ numberOfFiles ];
		String[ ] putSources = new String[ numberOfFiles ];
		String[ ] playpenDests = new String[ numberOfFiles ];
		String[ ] getDests = new String[ numberOfFiles ];
		for ( int i = 0; i < numberOfFiles; i++ ) {
			putFiles[i] = TempFileManager.createTempFileWithBogusData( getClass().getSimpleName(), "", tempFileDir,
				fileSize );
			putSources[i] = putFiles[i].getPath();
			playpenDests[i] = putFiles[i].getName();
			getDests[i] = putFiles[i].getPath() + "_get";
			getFiles[i] = new File( getDests[i] );
		}

		JobSpec jobToAdd = UnitUtil.buildASmallSleepJobWithPuts( 10, putSources, playpenDests, getDests );

		for ( BrokerUnit brokerUnit : brokerUnits ) {
			brokerUnit.addJob( jobToAdd );
			brokerUnit.addJob( jobToAdd );
		}

		for ( BrokerUnit brokerUnit : brokerUnits ) {
			conditionExpecter.waitUntilConditionIsMet( new BrokerAllJobsFinishedCondition( brokerUnit ), 1000, 10000 );
		}

		for ( int i = 0; i < numberOfFiles; i++ ) {
			assertTrue( getFiles[i].canRead() );
			assertEquals( new FileInfo( putFiles[i] ).getFileDigest(), new FileInfo( getFiles[i] ).getFileDigest() );
		}
		
		for ( int i = 0; i < numberOfFiles; i++ ) {
			assertTrue( getFiles[i].canRead() );
			assertEquals( JavaFileUtil.getDigestRepresentation(putFiles[i]), JavaFileUtil.getDigestRepresentation(getFiles[i]) );
		}*/
	}
}
