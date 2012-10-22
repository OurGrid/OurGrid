package org.ourgrid.system;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.ourgrid.common.executor.Executor;
import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.executor.ExecutorFactory;
import org.ourgrid.common.executor.ExecutorHandle;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.exception.JobSpecificationException;
import org.ourgrid.common.specification.exception.TaskSpecificationException;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.JavaFileUtil;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public abstract class OurGridTestCase extends TestCase {

	public static final String SEP = File.separator;

	public static final boolean NICE = true;

	public static final boolean NOT_NICE = false;

	public static final String TEST_SPECS_DIR = "test" + SEP + "specs";

	public static final String RESOURCE_DIR = System.getProperty( "user.dir" ) + SEP + "test" + SEP + "resources";

	public static final String TEMP_TEST_DIR = RESOURCE_DIR + SEP + "tmp";

	public static final File testDir = new File( TEMP_TEST_DIR );

	public static DeploymentID sampleDeploymentID1_1;

	public static DeploymentID sampleDeploymentID1_2;

	public static DeploymentID sampleDeploymentID2_1;

	public static DeploymentID sampleDeploymentID3_1;

	public static DeploymentID sampleDeploymentID4_1;

	public static ServiceID sampleServiceID1;

	public static ServiceID sampleServiceID2;

	public static ServiceID sampleServiceID3;

	public static ServiceID sampleServiceID4;

	public static ServiceID sampleServiceID5;

	protected static List<DeploymentID> sampleDeploymentIDs;

	private IMocksControl mockControl;

	private IMocksControl niceControl;

	protected Worker mockWorker;

	protected WorkerSpecification remoteWorkerSpec1;

	protected WorkerSpecification remoteWorkerSpec2;

	protected WorkerSpecification remoteWorkerSpec3;

	protected WorkerSpecification remoteWorkerSpec4;

	protected WorkerSpecification workerSpec1;

	protected WorkerSpecification workerSpec2;

	protected WorkerSpecification workerSpec3;

	protected WorkerSpecification workerSpec4;

	protected WorkerSpecification workerSpec5;

	protected WorkerSpecification workerSpec6;

	protected WorkerSpecification workerSpec7;

	protected WorkerSpecification workerSpec8;

	protected WorkerSpecification workerSpec9;

	protected WorkerSpecification workerSpec10;

	//protected JICConfiguration conf;

	//protected JICFileTransferManager mockFileTransfer;

	public static final ServiceID fakeServiceID = new ServiceID( "fake", "fake", "fake", "fake");

	public static final DeploymentID fakeDeploymentID = new DeploymentID( fakeServiceID, 1 );


	@Override
	protected void setUp() throws Exception {

		super.setUp();

		createTempDirectory();

		activateMockControls();
		resetActiveMocks();

		createSampleDeploymentIDs();
		createSampleWorkerSpecs();

		//conf = JICConfiguration.getInstance( RESOURCE_DIR + SEP + "jic.properties" );

		/*
		 * Note: This parameters must be tuned for each specific machine. For
		 * less powerful machines it is likely that you have to increase these
		 * values. Tests that rely on the Failure Detector may fail without a
		 * reason if these times are too low.
		 */
		/*conf.setProperty( JICConfiguration.PROP_LOCAL_DETECTION_TIME, "3" );
		conf.setProperty( JICConfiguration.PROP_LOCAL_HEARTBEAT_DELAY, "1" );
		conf.setProperty( JICConfiguration.PROP_LAN_DETECTION_TIME, "5" );
		conf.setProperty( JICConfiguration.PROP_LAN_HEARTBEAT_DELAY, "2" );
		conf.setProperty( JICConfiguration.PROP_WAN_DETECTION_TIME, "5" );
		conf.setProperty( JICConfiguration.PROP_WAN_HEARTBEAT_DELAY, "2" );

		JICModuleFactory.destroy();
		// JICModuleFactory.buildInstance( ModuleType.SINGLEJVM );
		JICModuleFactory.buildInstance( ModuleType.ACCESSPOINT );

		mockFileTransfer = getFileTransferMock( NICE );*/

		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}


	@Override
	protected void tearDown() throws Exception {

		super.tearDown();

		destroyTempDirectory();

		// JICModuleFactory.destroy();

		resetActiveMocks();
	}


	private void createTempDirectory() {

		destroyTempDirectory();
		testDir.mkdir();
	}


	private void destroyTempDirectory() {

		if ( testDir != null && testDir.exists() ) {
			JavaFileUtil.deleteDir( testDir );
		}
	}


	private void createSampleDeploymentIDs() {

		final String location1 = "user1@jabber.org/MODULE1";
		final String objectname1 = "OBJECT1";
		sampleServiceID1 = new ServiceID( new ContainerID("user1", "jabber.org", "MODULE1"), objectname1 );
		sampleDeploymentID1_1 = new DeploymentID( location1, objectname1, 1 );
		sampleDeploymentID1_2 = new DeploymentID( location1, objectname1, 2 );

		final String location2 = "user2@jabber.org/MODULE1";
		final String objectname2 = "OBJECT2";
		sampleServiceID2 = new ServiceID( new ContainerID("user2", "jabber.org", "MODULE1"), objectname2 );
		sampleDeploymentID2_1 = new DeploymentID( location2, objectname2, 1 );

		final String location3 = "user3@jabber.org/MODULE1";
		final String objectname3 = "OBJECT3";
		sampleServiceID3 = new ServiceID( new ContainerID("user3", "jabber.org", "MODULE1"), objectname3 );
		sampleDeploymentID3_1 = new DeploymentID( location3, objectname3, 1 );

		final String location4 = "user4@jabber.org/MODULE1";
		final String objectname4 = "OBJECT4";
		sampleServiceID4 = new ServiceID( new ContainerID("user4", "jabber.org", "MODULE1"), objectname4 );
		sampleDeploymentID4_1 = new DeploymentID( location4, objectname4, 1 );

		sampleDeploymentIDs = new ArrayList<DeploymentID>();
		sampleDeploymentIDs.add( sampleDeploymentID1_1 );
		sampleDeploymentIDs.add( sampleDeploymentID2_1 );
		sampleDeploymentIDs.add( sampleDeploymentID3_1 );
		sampleDeploymentIDs.add( sampleDeploymentID4_1 );
	}


	protected void activateMockControls() {

		mockControl = EasyMock.createControl();
		niceControl = EasyMock.createNiceControl();
	}


	protected void makeMockControlStrict() {

		mockControl.checkOrder( true );
	}


/*	protected Module getModuleMock( boolean nice ) {

		return this.getMock( nice, Module.class );
	}


	protected FailureDetectorInterestManager getFailureDetectorMock( boolean nice ) {

		return this.getMock( nice, FailureDetectorInterestManager.class );
	}*/


	protected FileInfo getFileInfoMock( boolean nice ) {

		return this.getMock( nice, FileInfo.class );
	}


	/*protected ControlClient getControlClientMock( boolean nice ) {

		return this.getMock( nice, ControlClient.class );
	}*/


	protected <T> T getMock( boolean nice, Class<T> clazz ) {

		return nice ? niceControl.createMock( clazz ) : mockControl.createMock( clazz );
	}


	protected void replayActiveMocks() {

		mockControl.replay();
		niceControl.replay();
	}


	protected void verifyActiveMocks() {

		mockControl.verify();
	}


	protected void resetActiveMocks() {

		if ( mockControl != null ) {
			mockControl.reset();
		}

		if ( niceControl != null ) {
			niceControl.reset();
		}
	}


	protected Worker getWorkerMock( boolean nice ) {

		return getMock( nice, Worker.class );
	}


	/*protected WorkerClientImpl getWorkerClientMock( boolean nice ) {

		return getMock( nice, WorkerClientImpl.class );
	}


	protected JICFileTransferManager getFileTransferMock( boolean nice ) {

		return getMock( nice, JICFileTransferManager.class );
	}*/


	protected WorkerControlClient getWorkerControlClientMock( boolean nice ) {

		return getMock( nice, WorkerControlClient.class );
	}


	/*protected WorkerProvider getWorkerProviderMock( boolean nice ) {

		return getMock( nice, WorkerProvider.class );
	}


	protected WorkerProviderClient getWorkerProviderClientMock( boolean nice ) {

		return getMock( nice, WorkerProviderClient.class );
	}


	protected Arbitrator getArbitratorMock( boolean nice ) {

		return getMock( nice, Arbitrator.class );
	}*/


	protected void createSampleWorkerSpecs() {

		Map<String,String> spec1 = new HashMap<String,String>();
		spec1.put( OurGridSpecificationConstants.ATT_USERNAME, "worker1" );
		spec1.put( OurGridSpecificationConstants.ATT_SERVERNAME, "serverA" );
		spec1.put( OurGridSpecificationConstants.ATT_PROVIDER_PEER, "peer@serverA" );
		workerSpec1 = new WorkerSpecification( spec1 );

		Map<String,String> spec2 = new HashMap<String,String>();
		spec2.put( OurGridSpecificationConstants.ATT_USERNAME, "worker2" );
		spec2.put( OurGridSpecificationConstants.ATT_SERVERNAME, "serverA" );
		spec2.put( OurGridSpecificationConstants.ATT_PROVIDER_PEER, "peer@serverA" );
		workerSpec2 = new WorkerSpecification( spec2 );

		Map<String,String> spec3 = new HashMap<String,String>();
		spec3.put( OurGridSpecificationConstants.ATT_USERNAME, "worker3" );
		spec3.put( OurGridSpecificationConstants.ATT_SERVERNAME, "serverA" );
		spec3.put( OurGridSpecificationConstants.ATT_PROVIDER_PEER, "peer@serverA" );
		workerSpec3 = new WorkerSpecification( spec3 );

		Map<String,String> spec4 = new HashMap<String,String>();
		spec4.put( OurGridSpecificationConstants.ATT_USERNAME, "worker4" );
		spec4.put( OurGridSpecificationConstants.ATT_SERVERNAME, "serverA" );
		spec4.put( OurGridSpecificationConstants.ATT_PROVIDER_PEER, "peer@serverA" );
		workerSpec4 = new WorkerSpecification( spec4 );

		Map<String,String> spec5 = new HashMap<String,String>();
		spec5.put( OurGridSpecificationConstants.ATT_USERNAME, "worker5" );
		spec5.put( OurGridSpecificationConstants.ATT_SERVERNAME, "serverA" );
		workerSpec5 = new WorkerSpecification( spec5 );

		Map<String,String> spec6 = new HashMap<String,String>();
		spec6.put( OurGridSpecificationConstants.ATT_USERNAME, "worker6" );
		spec6.put( OurGridSpecificationConstants.ATT_SERVERNAME, "serverA" );
		spec6.put( OurGridSpecificationConstants.ATT_PROVIDER_PEER, "peer@serverA" );
		workerSpec6 = new WorkerSpecification( spec6 );

		Map<String,String> spec7 = new HashMap<String,String>();
		spec7.put( OurGridSpecificationConstants.ATT_USERNAME, "worker7" );
		spec7.put( OurGridSpecificationConstants.ATT_SERVERNAME, "serverA" );
		spec7.put( OurGridSpecificationConstants.ATT_PROVIDER_PEER, "peer@serverA" );
		workerSpec7 = new WorkerSpecification( spec7 );

		Map<String,String> spec8 = new HashMap<String,String>();
		spec8.put( OurGridSpecificationConstants.ATT_USERNAME, "worker8" );
		spec8.put( OurGridSpecificationConstants.ATT_SERVERNAME, "serverA" );
		spec8.put( OurGridSpecificationConstants.ATT_PROVIDER_PEER, "peer@serverA" );
		workerSpec8 = new WorkerSpecification( spec8 );

		Map<String,String> spec9 = new HashMap<String,String>();
		spec9.put( OurGridSpecificationConstants.ATT_USERNAME, "worker9" );
		spec9.put( OurGridSpecificationConstants.ATT_SERVERNAME, "serverA" );
		spec9.put( OurGridSpecificationConstants.ATT_PROVIDER_PEER, "peer@serverA" );
		workerSpec9 = new WorkerSpecification( spec9 );

		Map<String,String> spec10 = new HashMap<String,String>();
		spec10.put( OurGridSpecificationConstants.ATT_USERNAME, "worker10" );
		spec10.put( OurGridSpecificationConstants.ATT_SERVERNAME, "serverA" );
		spec10.put( OurGridSpecificationConstants.ATT_PROVIDER_PEER, "peer@serverA" );
		workerSpec10 = new WorkerSpecification( spec10 );
	}


	/*protected String getJabberServerName() {

		return JICConfiguration.getInstance().getProperty( JICConfiguration.PROP_JABBER_SERVERNAME );
	}


	protected String getJabberUserName() {

		return JICConfiguration.getInstance().getProperty( JICConfiguration.PROP_USERNAME );
	}*/


	public static void assertExecutable( String path ) throws Exception {

		CommuneLogger logger = EasyMock.createMock(CommuneLogger.class);
		final Executor executorInstance = new ExecutorFactory(logger).buildNewNativeExecutor();
		ExecutorHandle handle = executorInstance.execute( "/tmp", "'" + path + "'" );
		ExecutorResult result = executorInstance.getResult( handle );
		assertEquals( "File could not be executed", 0, result.getExitValue() );
	}


	public static boolean isFileExecutable( String path ) throws Exception {

		return (0 == executeFile( path ));
	}


	private static int executeFile( String path ) throws ExecutorException {

		final File f = new File( path );
		
		CommuneLogger logger = EasyMock.createMock(CommuneLogger.class);
		final Executor executorInstance = new ExecutorFactory(logger).buildNewNativeExecutor();
		ExecutorHandle handle = executorInstance.execute( f.getParent(), "'./" + f.getName() + "'" );
		ExecutorResult result = executorInstance.getResult( handle );
		final int exitValue = result.getExitValue();
		return exitValue;
	}


	protected static JobSpecification sampleJobSpec1( int numTasks ) throws TaskSpecificationException,
		JobSpecificationException {

		List<TaskSpecification> taskSpecList = new LinkedList<TaskSpecification>();

		for ( int i = 0; i < numTasks; i++ ) {
			TaskSpecification t = new TaskSpecification( new IOBlock(), "echo 0", new IOBlock(), "ls foo" );
			taskSpecList.add( t );
		}

		return new JobSpecification( "TestJob", "", taskSpecList );
	}


	/*protected static ExecutionError isErrorOfType( ExecutionErrorTypes expected ) {

		EasyMock.reportMatcher( new ErrorTypeMatcher( expected ) );
		return null;
	}


	protected static ReplicaExecutionResult resultHasErrorOfErrorOfType( ExecutionErrorTypes expected ) {

		EasyMock.reportMatcher( new ErrorTypeMatcher( expected ) );
		return null;
	}*/


	/**
	 * Guarantee that an object and all its fields recursively is serializable
	 * 
	 * @see StreamCloner
	 */
	/*public static void assertSerializes( Object obj ) {

		try {
			StreamCloner.clone( obj );
		} catch ( CloneNotSupportedException e ) {
			throw new AssertionError( obj.getClass() + " is not fully serializable: " + e.getMessage() );
		}
	}*/
}
