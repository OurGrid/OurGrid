package org.ourgrid.acceptance.util.aggregator;

import org.ourgrid.acceptance.util.AggregatorAcceptanceUtil;
import org.ourgrid.aggregator.AggregatorComponent;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class T_601_Util extends AggregatorAcceptanceUtil{

	public T_601_Util(ModuleContext context) {
		super(context);
	}
	
	public AggregatorComponent createAggregatorComponent() throws CommuneNetworkException,
				ProcessorStartException, InterruptedException {
		
		AggregatorComponent component = new AggregatorTestComponent(context);
		application = component;
		
		Thread.sleep(2000);
		
		return component;
	}
}
