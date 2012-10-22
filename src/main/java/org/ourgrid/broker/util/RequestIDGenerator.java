package org.ourgrid.broker.util;

public interface RequestIDGenerator {

	/**
	 * Generates and return a new request ID
	 * 
	 * @return a newly generated request ID
	 */
	public long nextRequestID();


	/**
	 * Returns the latest generated request ID
	 * 
	 * @return the latest generated request ID
	 */
	public long getRequestID();

}
