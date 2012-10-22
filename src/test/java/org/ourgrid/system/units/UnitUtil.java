package org.ourgrid.system.units;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.ourgrid.common.specification.exception.TaskSpecificationException;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.worker.WorkerConstants;

public class UnitUtil {
	
	public static final String SEP = File.separator;
	
	public static final String RESOURCE_DIR = System.getProperty( "user.dir" ) + SEP + "test" + SEP + "resources";

	public static final String TEMP_TEST_DIR = RESOURCE_DIR + SEP + "tmp";
	
	public static JobSpecification buildAnEchoJob( String echo ) throws Exception {

		return buildAnEchoJob( 1, echo );
	}
	
	public static JobSpecification buildAnEchoJob( int tasks, String echo ) throws Exception {

		return buildAnEchoJobWithRequirements( tasks, echo, "" );
	}

	public static JobSpecification buildAnEchoJobWithRequirements( int tasks, String echo, String requirements )
		throws Exception {

		return createJob( tasks, "", "", new String[ 0 ], new String[ 0 ], new String[ 0 ], new String[ 0 ],
			new String[ 0 ], new String[ 0 ], requirements, "echo " + echo, null );
	}
	
	public static JobSpecification buildASleepJob( int sleep ) throws Exception {

		return buildASleepJob( 1, sleep );
	}
	
	public static JobSpecification buildASleepJob( int tasks, int sleep ) throws Exception {

		return buildASleepJobWithRequirements( tasks, sleep, "" );
	}


	public static JobSpecification buildASleepJobWithRequirements( int tasks, int sleep, String requirements ) throws Exception {

		return createJob( tasks, "", "", new String[ 0 ], new String[ 0 ], new String[ 0 ], new String[ 0 ],
				new String[ 0 ], new String[ 0 ], requirements, "sleep " + sleep, null );
	}

	private static JobSpecification createJob( int tasks, String operation, String envVar, String[ ] putCondition,
			String[ ] putSources, String[ ] playpenDests, String[ ] getCondition,
			String[ ] playpenGets, String[ ] getDests, String requirements, String command,
			String sabotageCheckCommand )

	throws Exception, TaskSpecificationException {

		if ( (putSources.length != playpenDests.length) || (putSources.length != putCondition.length)
				|| (playpenGets.length != getDests.length) || (playpenGets.length != getCondition.length) ) {
			throw new Exception( "wrong parameters" );
		}

		List<TaskSpecification> taskSpecs = new ArrayList<TaskSpecification>( tasks );
		for ( int i = 0; i < tasks; i++ ) {
			IOBlock initBlock = new IOBlock();
			for ( int j = 0; j < putSources.length; j++ ) {
				initBlock.putEntry( putCondition[j], new IOEntry( operation, putSources[j], "$" + envVar
						+ File.separator + playpenDests[j] ) );
			}

			IOBlock finalBlock = new IOBlock();
			for ( int j = 0; j < playpenGets.length; j++ ) {
				finalBlock.putEntry( getCondition[j], new IOEntry( "GET", "$" + envVar + File.separator
						+ playpenGets[j], getDests[j] ) );
			}

			TaskSpecification taskSpec = new TaskSpecification( initBlock, command, finalBlock, sabotageCheckCommand );
			taskSpec.setSourceDirPath( TEMP_TEST_DIR );
			taskSpecs.add( taskSpec );
		}
		final JobSpecification ret = new JobSpecification( "SimpleJobWith", requirements, taskSpecs );
		ret.setSourceDirPath( TEMP_TEST_DIR );
		return ret;
	}
	
	private static String[ ] createEmptyStringArray( int size ) {

		String[ ] strings = new String[ size ];
		for ( int i = 0; i < size; i++ ) {
			strings[i] = "";
		}
		return strings;
	}
	
	/**
	 * SMALL SLEEP JOB WITH STORES
	 */

	public static JobSpecification buildASmallSleepJobWithStores( int tasks, String[ ] storeSources, String[ ] playpenDests,
															String[ ] playpenGets, String[ ] getDests )
		throws Exception {

		return buildASleepJobWithStores( tasks, storeSources, playpenDests, playpenGets, getDests, 1 );
	}


	public static JobSpecification buildASleepJobWithStores( int tasks, String[ ] storeSources, String[ ] playpenDests,
													String[ ] playpenGets, String[ ] getDests, int sleepTime )
		throws Exception {

		return createJob( tasks, "STORE", WorkerConstants.ENV_STORAGE, createEmptyStringArray( storeSources.length ),
			storeSources, playpenDests, createEmptyStringArray( playpenGets.length ), playpenGets, getDests, "",
			"sleep " + sleepTime, null );
	}


	public static JobSpecification buildASmallSleepJobWithStores( String[ ] storeSources, String[ ] playpenDests,
															String[ ] playpenGets, String[ ] getDests )
		throws Exception {

		return buildASmallSleepJobWithStores( 1, storeSources, playpenDests, playpenGets, getDests );
	}


	public static JobSpecification buildAJobWithStores( int tasks, String requirements, String command, String putCondition[],
												String[ ] storeSources, String[ ] playpenDests,
												String[ ] getConditions, String[ ] playpenGets, String[ ] getDests )
		throws Exception {

		return createJob( tasks, "STORE", WorkerConstants.ENV_STORAGE, putCondition, storeSources, playpenDests,
			getConditions, playpenGets, getDests, requirements, command, null );
	}


	public static JobSpecification buildASmallSleepJobWithStores( String[ ] storeSources, String[ ] playpenDests,
															String[ ] getDests ) throws Exception {

		return buildASmallSleepJobWithStores( 1, storeSources, playpenDests, playpenDests, getDests );
	}
	
	/**
	 * SMALL SLEEP JOB WITH PUTS
	 */

	public static JobSpecification buildASmallSleepJobWithPuts( int tasks, String[ ] putSources, String[ ] playpenDests,
														String[ ] getDests ) throws Exception {

		return buildASleepJobWithPuts( tasks, 1, createEmptyStringArray( putSources.length ), putSources, playpenDests,
			createEmptyStringArray( putSources.length ), playpenDests, getDests );
	}


	public static JobSpecification buildASmallSleepJobWithPuts( int tasks, int sleep, String[ ] putSources,
														String[ ] playpenDests, String[ ] getDests ) throws Exception {

		return buildASleepJobWithPuts( tasks, sleep, createEmptyStringArray( putSources.length ), putSources,
			playpenDests, createEmptyStringArray( putSources.length ), playpenDests, getDests );
	}


	public static JobSpecification buildASmallSleepJobWithPuts( int tasks, String[ ] putSources, String[ ] playpenDests,
														String[ ] playpenGets, String[ ] getDests ) throws Exception {

		return buildASleepJobWithPuts( tasks, 1, createEmptyStringArray( putSources.length ), putSources, playpenDests,
			createEmptyStringArray( putSources.length ), playpenGets, getDests );
	}


	public static JobSpecification buildASmallSleepJobWithPuts( int tasks, int sleep, String[ ] putSources,
														String[ ] playpenDests, String[ ] playpenGets,
														String[ ] getDests ) throws Exception {

		return buildASleepJobWithPuts( tasks, sleep, createEmptyStringArray( putSources.length ), putSources,
			playpenDests, createEmptyStringArray( putSources.length ), playpenGets, getDests );
	}


	public static JobSpecification buildASmallSleepJobWithPuts( int tasks, String[ ] putConditions, String[ ] putSources,
														String[ ] playpenDests, String[ ] getConditions,
														String[ ] getDests ) throws Exception {

		return buildASleepJobWithPuts( tasks, 1, putConditions, putSources, playpenDests, getConditions, playpenDests,
			getDests );
	}


	public static JobSpecification buildASmallSleepJobWithPuts( String[ ] putSources, String[ ] playpenDests, String[ ] getDests )
		throws Exception {

		return buildASmallSleepJobWithPuts( 1, putSources, playpenDests, getDests );
	}


	public static JobSpecification buildASleepJobWithPuts( int tasks, int sleep, String[ ] putConditions, String[ ] putSources,
													String[ ] playpenDests, String[ ] getConditions,
													String[ ] playpenGets, String[ ] getDests ) throws Exception {

		return createJob( tasks, "PUT", WorkerConstants.ENV_PLAYPEN, putConditions, putSources, playpenDests,
			getConditions, playpenGets, getDests, "", "sleep " + sleep, null );
	}
	
	/**
	 * This job tries to put files on the persistent storage area. Such file
	 * transfers should be rejected by the Worker.
	 */
	public static JobSpecification buildAJobWithWrongPuts( int tasks, String[ ] putSources, String[ ] playpenDests )
		throws Exception {

		return createJob( tasks, "PUT", WorkerConstants.ENV_STORAGE, createEmptyStringArray( putSources.length ),
			putSources, playpenDests, new String[ 0 ], new String[ 0 ], new String[ 0 ], "", "exit 0", null );
	}

	public static JobSpecification buildAFailJobApplicationError() throws Exception {
		return buildAFailJobApplicationError( 1 );
	}
	
	public static JobSpecification buildAFailJobApplicationError( int tasks ) throws Exception {

		String putFile = UUID.randomUUID().toString();
		return createJob( tasks, "PUT", WorkerConstants.ENV_PLAYPEN, new String[ ] { "" }, new String[ ] { putFile },
			new String[ ] { putFile }, new String[ 0 ], new String[ 0 ], new String[ 0 ], "", "exit 0", null );
	}
	
	public static JobSpecification buildAFailJobExecutionError(int tasks ) throws  Exception {
		return createJob( tasks, "", "", new String[ 0 ], new String[ 0 ], new String[ 0 ], new String[ 0 ],
				new String[ 0 ], new String[ 0 ], "", "exit 1", null );
	}
	
	public static JobSpecification buildAFailJobExecutionError() throws Exception {

		return buildAFailJobExecutionError( 1 );
	}
	
	public static JobSpecification buildASabotagedJobExecution(int tasks, boolean wasSabotaged) throws Exception {
		String sabotageCheckCommand = (wasSabotaged) ? "exit 1" : "exit 0";

		return createJob( tasks, "", "", new String[ 0 ], new String[ 0 ], new String[ 0 ], new String[ 0 ],
			new String[ 0 ], new String[ 0 ], "", "exit 0", sabotageCheckCommand );
	}
}
