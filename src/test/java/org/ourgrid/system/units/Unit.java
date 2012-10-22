package org.ourgrid.system.units;


public interface Unit {

	/**
	 * Start this <code>Unit</code>.
	 */
	public void initKeys() throws Exception;


	/**
	 * Stop this <code>Unit</code>.
	 */
	public void stop() throws Exception;


	/**
	 * This method is used to delete enviroment files that the <code>Unit</code>
	 * uses.
	 */
	public void cleanUp() throws Exception;


	/**
	 * Verify if this <code>Unit</code> is running.
	 * 
	 * @return Returns true if this <code>Unit</code> is running.
	 * @throws Exception
	 */
	public boolean isRunning() throws Exception;


	/**
	 * Returns the jabber user name of this <code>Unit</code>.
	 * 
	 * @return The jabber user name of this <code>Unit</code>.
	 */
	public String getJabberUserName();


	/**
	 * Get's the jabber host of this functional unit.
	 * 
	 * @return Jabber Server's Hostname.
	 */
	public String getJabberServerHostname();


	/**
	 * Get's the unit location. This is a Jabber-ID.
	 * 
	 * @return JabberID
	 */
	public String getLocation();


	/**
	 * This method will block until this <code>Unit</code> has stopped all
	 * work.
	 * 
	 * @throws Exception
	 */
	public void waitUntilWorkIsDone() throws Exception;


	/**
	 * This method verifies if the functional test unit was not waken up since
	 * the last <code>waitUntilWorkIsDone()</code> call.
	 * 
	 * @return True if it hasnt.
	 * @throws Exception
	 */
	public boolean stillIdle() throws Exception;


	/**
	 * Kills this unit by calling <code>System.exit()</code>
	 * 
	 * @throws Exception
	 */
	public void kill() throws Exception;


	public void setHostMachine( String hostMachine ) throws Exception;


	public boolean runningLocally();
	
}
