package org.ourgrid.broker.util;

public class RandomRequestIDGenerator implements RequestIDGenerator {

	private long nextid;
	
	public RandomRequestIDGenerator() {
		this.nextid = randomize(); 
	}

	/*
	 * (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.gridclient.RequestIDCreator#getNextRequestID()
	 */
	public long nextRequestID() {
		long returnValue = nextid;
		this.nextid = randomize();
		
		return returnValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.gridclient.RequestIDCreator#peekNextRequestID()
	 */
	public long getRequestID() {
		return nextid;
	}
	

	public static long randomize() {
		return ((long) (Math.random() * Long.MAX_VALUE - 1)) + 1;
	}
}