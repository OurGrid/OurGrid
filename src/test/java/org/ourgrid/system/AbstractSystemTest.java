package org.ourgrid.system;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.ourgrid.TestUtils;
import org.ourgrid.broker.status.GridProcessStatusInfo;
import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.util.JavaFileUtil;
import org.ourgrid.system.condition.ConditionExpecter;
import org.ourgrid.system.config.FakeConfiguration;
import org.ourgrid.system.units.UnitManager;

public abstract class AbstractSystemTest {

	protected UnitManager unitManager;

	protected ConditionExpecter conditionExpecter;

	private TestUtils testUtils;

	public static final String ROOT_DIR = ".";

	public static final String RESOURCE_DIR = "test" + separator + "resources";

	public static final String TEST_PROPERTIES = RESOURCE_DIR + separator + "test.properties";

	public static final String TEMP_TEST_DIR = RESOURCE_DIR + separator + "tmp";

	public static final File tempFileDir = new File( TEMP_TEST_DIR );

	public static final File NONWRITABLE_DIR = new File( TEMP_TEST_DIR + separator + UUID.randomUUID().toString() );

	public static final String ATT_REM_EXEC = "ssh -o StrictHostKeyChecking=no -x $machine $command";

	public static final String ATT_COPY_FROM = "scp -o StrictHostKeyChecking=no $machine:$remotefile $localfile";

	public static final String ATT_COPY_TO = "scp -o StrictHostKeyChecking=no $localfile $machine:$remotefile";

	protected static final String WORKER2_PROPERTIES_FILENAME =  "test" + separator + "system" + separator + "worker2.properties";

	protected static final String WORKER3_PROPERTIES_FILENAME =  "test" + separator + "system" + separator + "worker3.properties";
	
	protected static final String WORKER4_PROPERTIES_FILENAME =  "test" + separator + "system" + separator + "worker4.properties";
	
	protected static final String PEER2_PROPERTIES_FILENAME =  "test" + separator + "system" + separator + "peer2.properties";
	
	protected static final String PEER3_PROPERTIES_FILENAME =  "test" + separator + "system" + separator + "peer3.properties";
	
	protected static final String PEER4_PROPERTIES_FILENAME =  "test" + separator + "system" + separator + "peer4.properties";

	@Before
	public void setUp() throws Exception {
		
		cleanDB();
		
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();

		/*JICConfiguration.getInstance( RESOURCE_DIR + File.separator + "jic.properties" );
		JICConfiguration.getInstance().setProperty( JICConfiguration.PROP_WAN_HEARTBEAT_DELAY, "5" );
		JICConfiguration.getInstance().setProperty( JICConfiguration.PROP_WAN_DETECTION_TIME, "20" );
		JICConfiguration.getInstance().setProperty( JICConfiguration.PROP_LAN_HEARTBEAT_DELAY, "5" );
		JICConfiguration.getInstance().setProperty( JICConfiguration.PROP_LAN_DETECTION_TIME, "20" );
		JICConfiguration.getInstance().setProperty( JICConfiguration.PROP_LOCAL_DETECTION_TIME, "6" );
		JICConfiguration.getInstance().setProperty( JICConfiguration.PROP_LOCAL_HEARTBEAT_DELAY, "2" );*/

		createTempDirectory();

		Configuration.getInstance( FakeConfiguration.FAKE );

		//JICModuleFactory.destroy();
		//JICModuleFactory.buildInstance( JICModuleFactory.ModuleType.ACCESSPOINT );
		// JICModuleFactory.buildInstance( JICModuleFactory.ModuleType.SINGLEJVM
		// );
		UnitManager.destroy();
		this.conditionExpecter = new ConditionExpecter();
		this.unitManager = UnitManager.getInstance();

		testUtils = new TestUtils();
		testUtils.load();

		// FileTransferNegotiator.IBB_ONLY = true;

		// System.setOut(new PrintStream(new NullStream()));
	}


	private void cleanDB() {
    	try {  
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");  
  
            String database = "jdbc:derby:db/peer";  
            Connection con = DriverManager.getConnection( database ,"","");  
              
            Statement s = con.createStatement();  
  
            s.execute("DELETE FROM ATTRIBUTE");
            s.execute("DELETE FROM BALANCE_VALUE");
            s.execute("DELETE FROM BALANCE");
            s.execute("DELETE FROM COMMAND");
            s.execute("DELETE FROM EXECUTION");
            s.execute("DELETE FROM WORKER_STATUS_CHANGE");
            s.execute("DELETE FROM PEER_STATUS_CHANGE");
            s.execute("DELETE FROM TASK");
            s.execute("DELETE FROM JOB");
            s.execute("DELETE FROM LOGIN");
            s.execute("DELETE FROM T_USERS");
            s.execute("DELETE FROM WORKER");
            s.execute("DELETE FROM PEER");
            s.close();  
            con.close();  
            
        }  
        catch (Exception e) {  
            System.out.println("Error: " + e);  
        }    
	}


	protected String[ ] getAvailableWorkerMachines() {

		return testUtils.getProperty( "workers.list" ).split( "," );
	}


	@After
	public void tearDown() throws Exception {

		unitManager.destroyAllLivingUnits();
		UnitManager.destroy();
		//JICModuleFactory.destroy();
		//JICConfiguration.reset();

		destroyTempDirectory();

		this.unitManager = null;
		this.conditionExpecter = null;
		this.testUtils.reset();
		this.testUtils = null;

		System.gc();

		BasicConfigurator.resetConfiguration();
	}


	private void createTempDirectory() {

		destroyTempDirectory();
		tempFileDir.mkdir();

		NONWRITABLE_DIR.mkdir();
		NONWRITABLE_DIR.setReadOnly();
	}


	private void destroyTempDirectory() {

		if ( NONWRITABLE_DIR != null ) {
			JavaFileUtil.deleteDir( NONWRITABLE_DIR );
		}

		if ( tempFileDir != null && tempFileDir.exists() ) {
			JavaFileUtil.deleteDir( tempFileDir );
		}
	}


	protected static void assertAllReplicasFinished( List<GridProcessStatusInfo> replicas ) {

		for ( GridProcessStatusInfo replica : replicas ) {
			if ( !replica.getState().equals(GridProcessState.FINISHED) 
					&& !replica.getState().equals(GridProcessState.ABORTED) ) {
				throw new AssertionError( "Replica " + replica.getHandle() + " should have finished or aborted" );
			}
		}
	}

	private class NullStream extends OutputStream {

		@Override
		public void write( int b ) throws IOException {

		}

	}
}
