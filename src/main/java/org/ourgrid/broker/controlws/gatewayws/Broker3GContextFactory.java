package org.ourgrid.broker.controlws.gatewayws;

import java.util.Map;

import org.ourgrid.common.OurGridContextFactory;

import br.edu.ufcg.lsd.commune.context.ContextParser;

public class Broker3GContextFactory extends OurGridContextFactory {

	public Broker3GContextFactory(ContextParser parser) {
		super(parser);
	}

	@Override
	public Map<Object, Object> getDefaultProperties() {
		Map<Object, Object> defaultProperties = super.getDefaultProperties();
		defaultProperties.put(Broker3GConstants.BROKER_3G_TRANSFERPORT_PROP, Broker3GConstants.BROKER_3G_TRANSFERPORT_DEF);
		defaultProperties.put(Broker3GConstants.BROKER_3G_TMPDIR_PROP, Broker3GConstants.JOBSDIR);
		
		return defaultProperties;
	}
	
}
