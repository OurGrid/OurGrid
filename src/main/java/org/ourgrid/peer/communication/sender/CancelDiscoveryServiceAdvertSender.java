package org.ourgrid.peer.communication.sender;

import java.util.concurrent.Future;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.communication.dao.DiscoveryServiceAdvertDAO;
import org.ourgrid.peer.response.CancelDiscoveryServiceAdvertResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class CancelDiscoveryServiceAdvertSender implements SenderIF<CancelDiscoveryServiceAdvertResponseTO> {

	public void execute(CancelDiscoveryServiceAdvertResponseTO response,
			ServiceManager manager) {
		Future<?> future = DiscoveryServiceAdvertDAO.getInstance().getAdvertActionFuture();
		if (future != null) {
			future.cancel(true);
		}
	}

}
