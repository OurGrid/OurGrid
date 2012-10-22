package org.ourgrid.broker.controlws;

import java.io.File;

import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.BrokerComponentContextFactory;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class BrokerWSContextCreator {
	
	public static final String SEP = File.separator;
	public static final String BROKER_TEST_DIR = "resources" + SEP + "webservice" + SEP;
	public static final String PROPERTIES_FILENAME = BROKER_TEST_DIR + "broker.properties";
	
	public static BrokerControlWSFacade createWSFacade() {
	
		ModuleContext context = new BrokerComponentContextFactory(
				new PropertiesFileParser(PROPERTIES_FILENAME
				)).createContext();
		
		BrokerControlWSFacade facade = null;
		
		try {
			new BrokerServerModule(context);
			facade = new BrokerControlWSFacade(context);
		} catch (CommuneNetworkException e) {
			e.printStackTrace();
		} catch (ProcessorStartException e) {
			e.printStackTrace();
		}
		
		return facade;
	}
}
