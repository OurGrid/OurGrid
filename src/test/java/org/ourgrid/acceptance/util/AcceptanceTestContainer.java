package org.ourgrid.acceptance.util;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.NetworkBuilder;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 * Requirement 301
 */
public class AcceptanceTestContainer extends Module {

	public AcceptanceTestContainer(String containerName,
			ModuleContext context) throws CommuneNetworkException, ProcessorStartException {
		super(containerName, context);
	}

	@Override
	public NetworkBuilder createNetworkBuilder() {
		networkBuilder = new AcceptanceTestNetworkBuilder();
		return networkBuilder;
	}
}
