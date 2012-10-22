package org.ourgrid.acceptance.worker;

import org.ourgrid.acceptance.util.AcceptanceTestContainer;
import org.ourgrid.worker.WorkerComponent;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class WorkerAcceptanceTestComponent extends WorkerComponent{

	public WorkerAcceptanceTestComponent(ModuleContext context)
			throws CommuneNetworkException, ProcessorStartException {
		super(context);
	}

	protected Module createContainer(String containerName,
			ModuleContext context) throws CommuneNetworkException, ProcessorStartException {
		return new AcceptanceTestContainer(containerName, context);
	}
}
