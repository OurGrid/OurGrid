package org.ourgrid.system.units;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.system.AbstractSystemTest;

/**
 * @see UnitManagerTest
 */
public class UnitManager {

	private static final String TEST_DEPLOY_DIR_PREFIX = ".ourgrid-test";

	private static UnitManager instance;

	private List<Unit> units;
	
	public void addUnit(Unit unit) {
		this.units.add(unit);
	}


	private UnitManager() {

		this.units = new LinkedList<Unit>();
	}


	public static UnitManager getInstance() {

		if ( instance == null )
			instance = new UnitManager();
		return instance;
	}


	public <T extends Unit> T buildNewUnit( Class<T> unitType ) throws Exception {

		return this.buildNewUnit( unitType, null );
	}
	
	public <T extends Unit> T buildNewUnit( String propertiesFile, Class<T> unitType ) throws Exception {

		Constructor<T> constructor = unitType.getConstructor(String.class);
		T newUnit = constructor.newInstance(propertiesFile);
		units.add( newUnit );
		
		return newUnit;
	}


	public <T extends Unit> T buildNewUnit( Class<T> unitType, String machineToDeployUnit ) throws Exception {

		T newUnit = unitType.newInstance();
		units.add( newUnit );
		newUnit.setHostMachine( machineToDeployUnit );

		return newUnit;
	}


//	public <T extends Unit> T buildAndStartNewUnit( Class<T> unitType ) throws Exception {
//
//		T unit = buildNewUnit( unitType );
//		unit.initKeys();
//
//		return unit;
//	}


	public <T extends Unit> List<T> buildManyUnits( Class<T> unitType, int numberOfUnits ) throws Exception {

		return this.buildManyUnits( unitType, numberOfUnits, (List<String>) null );
	}


	public <T extends Unit> List<T> buildManyUnits( Class<T> unitType, int numberOfUnits, String hostMachine )
		throws Exception {

		ArrayList<String> machineList = new ArrayList<String>();
		machineList.add( hostMachine );
		return this.buildManyUnits( unitType, numberOfUnits, machineList );
	}


	public <T extends Unit> List<T> buildManyUnits( Class<T> unitType, int numberOfUnits, String... hostMachines )
		throws Exception {

		ArrayList<String> machineList = new ArrayList<String>();
		for ( String hostMachine : hostMachines ) {
			machineList.add( hostMachine );
		}
		return this.buildManyUnits( unitType, numberOfUnits, machineList );
	}


	public <T extends Unit> List<T> buildManyUnits( Class<T> unitType, int numberOfUnits, List<String> machineList )
		throws Exception {

		List<T> returnValue = new LinkedList<T>();

		Iterator<String> it = null;
		if ( machineList != null && !machineList.isEmpty() ) {
			it = machineList.iterator();
		}
		for ( int i = 0; i < numberOfUnits; i++ ) {
			T newUnit = unitType.newInstance();
			units.add( newUnit );
			String hostMachine = null;
			if ( it != null ) {
				if ( !it.hasNext() ) {
					it = machineList.iterator();
				}
				hostMachine = it.next();
			}
			newUnit.setHostMachine( hostMachine );
			returnValue.add( newUnit );
		}

		return returnValue;
	}


	public <T extends Unit> List<T> buildAndStartManyUnits( Class<T> unitType, int numberOfUnits ) throws Exception {

		List<T> createdUnits = buildManyUnits( unitType, numberOfUnits );

		startUnits( createdUnits );

		return createdUnits;
	}


	public void startUnits( List<? extends Unit> createdUnits ) throws Exception {

		for ( Unit t : createdUnits ) {
			t.initKeys();
		}
	}


	public void startUnits( Unit... createdUnits ) throws Exception {

		for ( Unit t : createdUnits ) {
			t.initKeys();
		}
	}


	public void deployUnits( List<? extends Unit> createdUnits ) throws Exception {

		this.deployUnits( createdUnits.toArray( new Unit[ 0 ] ) );
	}


	public void unDeployUnits( List<? extends Unit> createdUnits ) throws Exception {

		this.unDeployUnits( createdUnits.toArray( new Unit[ 0 ] ) );
	}


	public void deployUnits( Unit... createdUnits ) throws Exception {

		final List<WorkerSpecification> specs = getSpecsForDeployer( createdUnits );
		//TODO Deployer.runWorkerDeployer( "start", getDeployDirPrefix(), getRootDir(), true, specs );
	}


	public void unDeployUnits( Unit... createdUnits ) throws Exception {

		final List<WorkerSpecification> specs = getSpecsForDeployer( createdUnits );
		//TODO Deployer.runWorkerDeployer( "uninstall", getDeployDirPrefix(), getRootDir(), true, specs );
	}


	private List<WorkerSpecification> getSpecsForDeployer( Unit... createdUnits ) {

		List<WorkerSpecification> specs = new ArrayList<WorkerSpecification>();

		for ( Unit unit : createdUnits ) {
			if ( !(unit instanceof WorkerUnit) ) {
				throw new UnsupportedOperationException( "Remote deployment currently works for WorkerUnit only" );
			}
			WorkerUnit w = (WorkerUnit) unit;
			specs.add( w.getSpec() );
		}
		return specs;
	}


	public void destroyAllLivingUnits() throws Exception {

		boolean allStopped = true;
		List<Unit> unitsToUndeploy = new ArrayList<Unit>();
		for ( Unit unit : units ) {

			final String unitID = unit.getClass().getSimpleName() + " - " + unit.getJabberUserName() + "@"
					+ unit.getJabberServerHostname();

			if ( !unit.runningLocally() ) {
				unitsToUndeploy.add( unit );
			} else {
				if ( unit.isRunning() ) {

					System.out.println( "STOPPING: " + unitID );

					/*
					 * Catch Exception to guarantee it at least tries to stop
					 * all units
					 */
					try {
						unit.stop();
					} catch ( Exception e ) {
						System.err.println( "UNABLE TO STOP: " + unitID );
						e.printStackTrace();
						allStopped = false;
					}
				}

				System.out.println( "UNIT STOPPED: " + unitID );

				unit.cleanUp();
			}
		}
		if ( !unitsToUndeploy.isEmpty() ) {
			unDeployUnits( unitsToUndeploy );
		}
		if ( !allStopped ) {
			throw new Exception( "Could not stop all units" );
		}
		units.clear();
	}


	public static void destroy() {

		instance = null;
	}


	public String getDeployDirPrefix() {

		return TEST_DEPLOY_DIR_PREFIX;
	}


	public String getRootDir() {

		return AbstractSystemTest.ROOT_DIR;
	}
}
