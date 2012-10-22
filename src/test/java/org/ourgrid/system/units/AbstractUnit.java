package org.ourgrid.system.units;

import java.security.KeyPair;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.ClientModule;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.InvalidIdentificationException;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;
import br.edu.ufcg.lsd.commune.network.signature.Util;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;

public abstract class AbstractUnit implements Unit {

	protected String jabberUser;

	private boolean isRunning = false;

	private String location;

	private String moduleName;

	private String publicKey;

	private String privateKey;

	private String hostMachine;
	
	private ModuleContext context;
	
	protected String propertiesFile;

	protected AbstractUnit( String moduleName) throws Exception {
		this.moduleName = moduleName;
	}
	
	protected AbstractUnit(String moduleName, String propertiesFile) throws Exception {
		this.propertiesFile = propertiesFile;
		this.moduleName = moduleName;
	}
	
	protected void contextCreated() throws Exception {
		setLocation();
		initKeys();
		createComponent();
	}
	
	protected void setLocation() {
		this.location = getJabberID( getJabberUserName(), getJabberServerHostname(), getModuleName());
	}
	
	protected abstract void createComponent();
	
	protected ModuleContext getContext() {
		return context;
	}

	protected void setContext(ModuleContext context) {
		this.context = context;
	}

	protected Map<String, String> getNewProperties() {
		Map<String, String> contextProperties = new LinkedHashMap<String, String>();
		contextProperties.put(XMPPProperties.PROP_USERNAME, jabberUser);
		contextProperties.put(XMPPProperties.PROP_PASSWORD, jabberUser);
		contextProperties.put(SignatureProperties.PROP_PUBLIC_KEY, publicKey);
		contextProperties.put(SignatureProperties.PROP_PRIVATE_KEY, privateKey);
		
		return contextProperties;
	}
	
	public void initKeys() throws Exception {
		genKeyPair();
		isRunning = true;
		System.out.println( "STARTING: " + this.toString() );
	}


	public boolean runningLocally() {

		return getHostMachine() == null;
	}


	protected abstract void deploy();


	private void genKeyPair() {

		KeyPair pair = Util.generateKeyPair();
		publicKey = Util.encodeArrayToBase64String( pair.getPublic().getEncoded() );
		privateKey = Util.encodeArrayToBase64String( pair.getPrivate().getEncoded() );
	}


	public abstract ClientModule getUIManager() throws Exception;


	public void stop() throws Exception {

		System.out.println( "AbstractUnit.stop(1)" );
		
		checkIfUnitIsRunning();
		System.out.println( "AbstractUnit.stop(2)" );
		getUIManager().stop();
		System.out.println( "AbstractUnit.stop(3)" );
		this.isRunning = false;
	}


	public void kill() throws Exception {

		checkIfUnitIsRunning();
		getUIManager().stop();
		this.isRunning = false;
	}


	public String getJabberServerHostname() {
		return getContext().getProperty(XMPPProperties.PROP_XMPP_SERVERNAME);
	}
	
	protected abstract ModuleContext createContext();


	public String getJabberUserName() {
		return getContext().getProperty(XMPPProperties.PROP_USERNAME);
	}


	public final String getLoginAndServer() {

		return jabberUser + "@" + getJabberServerHostname();
	}


	public void waitUntilWorkIsDone() throws Exception {

	}


	public boolean stillIdle() throws Exception {

		return false;
	}


	public final boolean isRunning() throws Exception {

		return runningLocally() ? this.isRunning : getUIManager().getContainerDAO().isStarted();
	}


	public final String getLocation() {

		return this.location;
	}


	public String getModuleName() {

		return this.moduleName;
	}


	protected void checkIfUnitIsRunning() throws Exception {

		if ( !isRunning() )
			throw new UnitException( "Unit is not running" );
	}


	protected void checkIfUnitIsStopped() throws Exception {

		if ( isRunning() )
			throw new UnitException( "Cannot set this property after unit was started" );
	}


	public void setHostMachine( String hostMachine ) throws Exception {

		this.hostMachine = hostMachine;
	}


	public String getHostMachine() {

		return this.hostMachine;
	}


	@Override
	public String toString() {

		return this.getClass().getSimpleName() + " - " + this.getJabberUserName() + "@"
				+ this.getJabberServerHostname() + ", machine: "
				+ (getHostMachine() == null ? "same jvm" : getHostMachine());
	}
	
	/**
	 * Returns the full Jabber ID based on the given data
	 * 
	 * @param login the user login
	 * @param server the server name
	 * @param resource the resource
	 * @return the full Jabber ID
	 */
	private String getJabberID( String login, String server, String resource ) {

		if ( login == null || server == null ) {
			throw new InvalidIdentificationException(
				"Undefined XMPP login or server name. Check the XMPP properties '" + XMPPProperties.PROP_USERNAME
						+ "' and '" + XMPPProperties.PROP_XMPP_SERVERNAME + "' on the configuration file" );
		}

		String fullID = login + "@" + server + (resource != null ? "/" + resource : "");

		if ( !validate( fullID ) ) {
			throw new InvalidIdentificationException( "Invalid XMPP identification: " + fullID );
		}

		return fullID;
	}
	
	/**
	 * Validates the given identification
	 * 
	 * @param fullID the full identification
	 * @returns true if the id is valid, false if it isn't.
	 */
	private boolean validate( String fullID ) {

		if ( fullID != null ) {
			Pattern p = Pattern.compile( "[a-zA-Z_0-9-\\.]+@[a-zA-Z_0-9-\\.]+[/a-zA-Z_0-9-\\.]*" );
			Matcher m = p.matcher( fullID );
			return m.matches();
		}
		return false;
	}
	
	public void setContextFilePath(String filePath) {
		propertiesFile = filePath;
		context = createContext();
	}

}
