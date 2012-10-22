package org.ourgrid.acceptance.util;

import br.edu.ufcg.lsd.commune.network.NetworkBuilder;
import br.edu.ufcg.lsd.commune.network.connection.ConnectionProtocol;

/**
 * Requirement 301
 */
public class AcceptanceTestNetworkBuilder extends NetworkBuilder{

	@Override
	protected ConnectionProtocol createConnectionProtocol() {
		return null;
	}
	
}
