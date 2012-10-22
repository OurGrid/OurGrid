package org.ourgrid.broker.communication.processors;

import org.ourgrid.broker.communication.actions.HereIsWorkerSpecMessageHandle;
import org.ourgrid.broker.request.HereIsWorkerSpecProcessorRequestTO;
import org.ourgrid.common.interfaces.MessageProcessor;
import org.ourgrid.common.internal.OurGridRequestControl;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class HereIsWorkerSpecProcessor implements MessageProcessor<HereIsWorkerSpecMessageHandle>{

	public void process(HereIsWorkerSpecMessageHandle handle,
			ServiceManager serviceManager) {
		HereIsWorkerSpecProcessorRequestTO to = new HereIsWorkerSpecProcessorRequestTO();
		to.setWorkerAddress(serviceManager.getSenderServiceID().toString());
		to.setWorkerSpec(handle.getWorkerSpec());

		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	
}
