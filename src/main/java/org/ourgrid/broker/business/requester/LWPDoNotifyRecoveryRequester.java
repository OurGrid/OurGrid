package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.PeerEntry;
import org.ourgrid.broker.business.messages.BrokerControlMessages;
import org.ourgrid.broker.communication.receiver.LocalWorkerProviderClientReceiver;
import org.ourgrid.broker.request.LWPDoNotifyRecoveryRequestTO;
import org.ourgrid.broker.response.LoginResponseTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.DeployServiceResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;

public class LWPDoNotifyRecoveryRequester implements RequesterIF<LWPDoNotifyRecoveryRequestTO> {

	public List<IResponseTO> execute(LWPDoNotifyRecoveryRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		PeerEntry peerEntry = BrokerDAOFactory.getInstance().getPeerDAO().getPeerEntry(request.getPeerAddress());
		
		if (peerEntry == null) {
			responses.add(new LoggerResponseTO(BrokerControlMessages.getNoPeerWithSuchEntityIDMessage(request.getPeerID()), LoggerResponseTO.WARN));
			return responses;
		}
		
		peerEntry.setAsNotLogged(null);
		
		if (!request.isClientDeployed()) {
			DeployServiceResponseTO to = new DeployServiceResponseTO();
			to.setServiceClass(LocalWorkerProviderClientReceiver.class);
			to.setServiceName(BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);
			
			responses.add(to);
		}
		
		LoginResponseTO to = new LoginResponseTO();
		to.setStubAddress(request.getPeerAddress());
		
		responses.add(to);
		
		responses.add(new LoggerResponseTO(BrokerControlMessages.getPeerIsUpMessage(request.getPeerID()), LoggerResponseTO.DEBUG));
		
		return responses;
	}
}
