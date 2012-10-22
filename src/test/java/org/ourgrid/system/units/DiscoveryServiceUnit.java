package org.ourgrid.system.units;

import static java.io.File.separator;

import java.util.Collection;

import org.ourgrid.common.interfaces.management.DiscoveryServiceManager;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceComponentContextFactory;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.ui.sync.DiscoveryServiceSyncComponentClient;
import org.ourgrid.discoveryservice.ui.sync.DiscoveryServiceSyncManagerClient;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncApplicationClient;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class DiscoveryServiceUnit extends AbstractUnit {

	private DiscoveryServiceSyncComponentClient uiManager;
	private static final String DS_PROPERTIES_FILENAME = "test" + separator +
		"system" + separator + "discoveryservice.properties";


	protected DiscoveryServiceUnit() throws Exception {

		super( DiscoveryServiceConstants.MODULE_NAME );
		getUIManager();
		this.uiManager.start();
	}

	@Override
	public SyncApplicationClient<DiscoveryServiceManager, DiscoveryServiceSyncManagerClient> getUIManager() throws Exception {
		if ( this.uiManager == null ) {
			
			ModuleContext context = createContext();
			this.uiManager = new DiscoveryServiceSyncComponentClient(context);
		}
		
		return this.uiManager;
	}

	public Collection<String> getConnectedPeers() throws Exception {

		final Collection<String> connectedPeers = this.uiManager.getDiscoveryServiceCompleteStatus().getConnectedPeers();
		return connectedPeers;
	}


	public void cleanUp() throws Exception {

	}


	@Override
	protected void deploy() {

		throw new UnsupportedOperationException( "Remove deployment does not work for this unit" );
	}

	@Override
	protected void createComponent() {
		ModuleContext context = createContext();
		
		try {
			new DiscoveryServiceComponent(context);
		} catch (CommuneNetworkException e) {
			e.printStackTrace();
		} catch (ProcessorStartException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected ModuleContext createContext() {
		DiscoveryServiceComponentContextFactory contextFactory = new DiscoveryServiceComponentContextFactory(
				new PropertiesFileParser(DS_PROPERTIES_FILENAME));
		return contextFactory.createContext();
	}
}
