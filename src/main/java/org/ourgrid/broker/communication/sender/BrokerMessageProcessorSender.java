package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.response.BrokerMessageProcessorResponseTO;
import org.ourgrid.common.interfaces.MessageProcessor;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class BrokerMessageProcessorSender implements SenderIF<BrokerMessageProcessorResponseTO> {

	public void execute(BrokerMessageProcessorResponseTO response,
			ServiceManager manager) {
		
		MessageProcessor<MessageHandle> processor = response.getProcessor();
		processor.process(response.getHandle(), manager);
	}

}
