package org.ourgrid.acceptance.broker;

import org.ourgrid.acceptance.util.AcceptanceTestContainer;
import org.ourgrid.broker.BrokerServerModule;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 * Requirement 301
 */
public class BrokerAcceptanceTestComponent extends BrokerServerModule {

	public BrokerAcceptanceTestComponent(ModuleContext context)
			throws CommuneNetworkException, ProcessorStartException {
		super(context);
	}

	protected Module createContainer(String containerName,
			ModuleContext context) throws CommuneNetworkException, ProcessorStartException {
		return new AcceptanceTestContainer(containerName, context);
	}
}
