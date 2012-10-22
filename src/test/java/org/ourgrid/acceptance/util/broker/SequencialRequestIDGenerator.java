package org.ourgrid.acceptance.util.broker;

import org.ourgrid.broker.util.RequestIDGenerator;

public class SequencialRequestIDGenerator implements RequestIDGenerator {

	private static long nextid;
	
	public SequencialRequestIDGenerator() {
		nextid = 1L; 
	}

	/*
	 * (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.gridclient.RequestIDCreator#getNextRequestID()
	 */
	public long nextRequestID() {
		long returnValue = nextid;
		nextid = nextid++;
		
		return returnValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.ourgrid.broker.scheduler.gridclient.RequestIDCreator#peekNextRequestID()
	 */
	public long getRequestID() {
		return nextid;
	}
}