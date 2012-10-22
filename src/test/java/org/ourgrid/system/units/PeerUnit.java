package org.ourgrid.system.units;

import static java.io.File.separator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.status.LocalConsumerInfo;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerComponentContextFactory;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.to.PeerBalance;
import org.ourgrid.peer.ui.sync.PeerSyncApplicationClient;
import org.ourgrid.peer.ui.sync.command.PeerStatusCommand;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.ClientModule;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class PeerUnit extends AbstractUnit {

	private static final String INVALID_USER = UUID.randomUUID().toString();

	private static final String INVALID_SERVER = UUID.randomUUID().toString();
	
	public static final String SEP = File.separator;
	
	private double valueForCPU;

	private double valueForData;

	private String discoveryServiceUser;

	private String discoveryServiceServer;

	private boolean joinCommunity;

	private PeerSyncApplicationClient uiManager;
	
	private static final String PEER_PROPERTIES_FILENAME =  "test" + separator + "system" + separator + "peer.properties";

	
	public PeerUnit(String propertiesFile) throws Exception {
		super(PeerConstants.MODULE_NAME, propertiesFile);
		initData();
		this.propertiesFile = propertiesFile;
		getUIManager();
		this.uiManager.start();
	}
	
	protected PeerUnit() throws Exception {
		super( PeerConstants.MODULE_NAME );
		initData();
		getUIManager();
		this.uiManager.start();
	}
	
	private void initData() throws Exception {
		this.valueForCPU = 1d;
		this.valueForData = 1d;
		this.discoveryServiceUser = INVALID_USER;
		this.discoveryServiceServer = INVALID_SERVER;
		this.joinCommunity = false;
		setContext(createContext());
		contextCreated();
	}
	
	@Override
	public ClientModule getUIManager() throws Exception {
		if ( this.uiManager == null ) {
			
			ModuleContext context = getContext();
			this.uiManager = new PeerSyncApplicationClient(context);
		}
		
		return this.uiManager;
	}

	public void addUser( String login ) throws Exception {

		checkIfUnitIsRunning();
		
		uiManager.addUser(login);
	}


	public Collection<WorkerInfo> getLocalWorkerStatus() throws Exception {

		final Collection<WorkerInfo> localWorkersStatus = uiManager.getLocalWorkersStatus();

		return localWorkersStatus;
	}


	public Collection<RemoteWorkerInfo> getRemoteWorkerStatus() throws Exception {

		final Collection<RemoteWorkerInfo> remoteWorkersStatus = uiManager.getRemoteWorkersStatus();

		return remoteWorkersStatus;
	}


	public Collection<LocalConsumerInfo> getLocalConsumerStatus() throws Exception {

		final Collection<LocalConsumerInfo> localConsumerStatus = uiManager.getLocalConsumersStatus();

		return localConsumerStatus;
	}


	public Collection<ConsumerInfo> getRemoteConsumerStatus() throws Exception {

		final Collection<ConsumerInfo> remoteConsumerStatus = uiManager.getRemoteConsumersStatus();

		return remoteConsumerStatus;
	}


	public void setDiscoveryService( DiscoveryServiceUnit dsUnit ) throws Exception {

		checkIfUnitIsStopped();

		setDiscoveryServiceUser( dsUnit.getJabberUserName() );
		setDiscoveryServiceServer( dsUnit.getJabberServerHostname() );
	}


	public String getDiscoveryServiceServer() {

		return discoveryServiceServer;
	}


	private void setDiscoveryServiceServer( String discoveryServiceServer ) {

		this.discoveryServiceServer = discoveryServiceServer;
	}


	public String getDiscoveryServiceUser() {

		return discoveryServiceUser;
	}


	private void setDiscoveryServiceUser( String discoveryServiceUser ) {

		this.joinCommunity = true;
		this.discoveryServiceUser = discoveryServiceUser;
	}

	public double getValueForCPU() {

		return valueForCPU;
	}


	public void setValueForCPU( double valueForCPU ) throws Exception {

		checkIfUnitIsStopped();
		this.valueForCPU = valueForCPU;
	}


	public double getValueForData() {

		return valueForData;
	}


	public void setValueForData( double valueForData ) throws Exception {

		checkIfUnitIsStopped();
		this.valueForData = valueForData;
	}


	protected boolean isJoinCommunity() {

		return joinCommunity;
	}


	protected void setJoinCommunity( boolean joinCommunity ) throws Exception {

		checkIfUnitIsStopped();
		this.joinCommunity = joinCommunity;
	}

	public PeerBalance getBalanceForPeer( PeerUnit peerUnit ) throws Exception {

		checkIfUnitIsRunning();

		NetworkOfFavorsStatus networkOfFavorsStatus = uiManager.getNetworkOfFavorsStatus();

		System.err.println( "PeerUnit.getBalanceForPeer()" );
		System.err.println( networkOfFavorsStatus.getTable() );
		System.err.println( networkOfFavorsStatus.getTable().keySet().iterator().next() );

		return networkOfFavorsStatus.getBalance( peerUnit.getLocation() );
	}


//	public void setBalances( AccountingBalance... accountingBalances ) throws Exception {
//
//		checkIfUnitIsStopped();
//
//		HashMap<String,Balance> toSave = new HashMap<String,Balance>();
//
//		for ( AccountingBalance accountingBalance : accountingBalances ) {
//			toSave.put( accountingBalance.getPeerUnit().getLocation(), accountingBalance.getBalance() );
//		}
//
//		ObjectOutputStream objectOutputStream = null;
//		try {
//			objectOutputStream = new ObjectOutputStream( new FileOutputStream( nofRankingFile ) );
//			objectOutputStream.writeObject( toSave );
//		} finally {
//			if ( objectOutputStream != null )
//				objectOutputStream.close();
//		}
//	}


//	public String getCommunityObjtainerLocation() throws Exception {
//
//		return JIDUtil.getJabberID( getJabberUserName(), getJabberServerHostname(),
//			PeerConstants.COMMUNITY_OBTAINER_MODULE_NAME );
//	}


	@Override
	protected void deploy() {

		throw new UnsupportedOperationException( "Remote deployment does not work for this unit" );
	}
	
	
	public void showStatus() throws Exception {

		new PeerStatusCommand( uiManager ).run( new String[ ] {} );
	}

	@Override
	protected void createComponent() {
		ModuleContext context = getContext();
		
		try {
			new PeerComponent(context);
		} catch (CommuneNetworkException e) {
			e.printStackTrace();
		} catch (ProcessorStartException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected ModuleContext createContext() {
		String propertiesFile = PEER_PROPERTIES_FILENAME;
		if (this.propertiesFile != null) {
			propertiesFile = this.propertiesFile;
		}
		
		PeerComponentContextFactory contextFactory = new PeerComponentContextFactory(
				new PropertiesFileParser(propertiesFile));
		return contextFactory.createContext();
	}

	public void cleanUp() throws Exception {
		// TODO Auto-generated method stub
		
	}

	
}
