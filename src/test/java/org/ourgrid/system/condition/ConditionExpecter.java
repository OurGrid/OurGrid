package org.ourgrid.system.condition;

public class ConditionExpecter {

	/*
	 * This default values are suitable for all tests. Their values should not
	 * be decreased.
	 */
	private static final long DEFAULT_WAIT_TIME = 1000;

	private static final int DEFAULT_MAX_ROUNDS = 70;

	private static final long ASSERT_DEFAULT_WAIT_TIME = 1000;

	private static final int ASSERT_DEFAULT_MAX_ROUNDS = 5;

	private long defaultWaitTime;

	private int defaultMaxRounds;


	public ConditionExpecter() {

		this( DEFAULT_WAIT_TIME, DEFAULT_MAX_ROUNDS );
	}


	public ConditionExpecter( long timeOutInMillis, int maxRounds ) {

		this.defaultWaitTime = timeOutInMillis;
		this.defaultMaxRounds = maxRounds;
	}


	public synchronized void waitUntilConditionIsMet( Condition condition ) throws Exception {

		this.waitUntilConditionIsMet( condition, defaultWaitTime, defaultMaxRounds );
	}


	public synchronized void waitUntilConditionIsMet( Condition condition, long waitTime, int maxRounds )
		throws Exception {

		int rounds = 0;
		while ( !condition.isConditionMet() ) {
			wait( waitTime );

			System.out.println( "Testing: (" + condition.getClass().getSimpleName() + ")" + condition.detailMessage() );

			if ( maxRounds == rounds ) {
				throw new Exception( "Condition not met in maximum number of rounds: " + condition.detailMessage() );
			}

			rounds++;
		}

		System.out.println( "Condition met: " + condition.getClass().getSimpleName() );
	}


	public synchronized void assertCondition( Condition condition ) throws Exception {

		assertCondition( condition, ASSERT_DEFAULT_WAIT_TIME, ASSERT_DEFAULT_MAX_ROUNDS );
	}


	public synchronized void assertCondition( Condition condition, long waitTime, int maxRounds ) throws Exception {

		int rounds = 0;
		while ( condition.isConditionMet() ) {
			wait( waitTime );

			System.out
				.println( "Asserting: (" + condition.getClass().getSimpleName() + ")" + condition.detailMessage() );

			if ( maxRounds == rounds ) {
				return;
			}

			rounds++;
		}

		System.out.println( "Condition could not be asserted: " + condition.getClass().getSimpleName() );
		throw new Exception( "Condition not asserted during number of rounds: " + condition.detailMessage() );
	}


	public int getDefaultMaxRounds() {

		return defaultMaxRounds;
	}


	public void setDefaultMaxRounds( int maxRounds ) {

		this.defaultMaxRounds = maxRounds;
	}


	public long getDefaultWaitTime() {

		return defaultWaitTime;
	}


	public void setDefaultWaitTime( long waitTime ) {

		this.defaultWaitTime = waitTime;
	}
}
