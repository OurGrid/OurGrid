package org.ourgrid.broker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.JobDAO;
import org.ourgrid.broker.business.dao.PeerEntry;
import org.ourgrid.broker.business.messages.LocalWorkerProviderClientMessages;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.request.LoginSucceedRequestTO;
import org.ourgrid.common.BrokerLoginResult;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;

public class LoginSucceedRequester implements RequesterIF<LoginSucceedRequestTO> {

	public List<IResponseTO> execute(LoginSucceedRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String senderPubKey = request.getSenderPublicKey();
		String peerDeploymentID = request.getPeerDeploymentID();
		String peerPublicKey = request.getPeerPublicKey();
		
		if (!senderPubKey.equals(peerPublicKey)) {
			responses.add(new LoggerResponseTO(LocalWorkerProviderClientMessages.getUnknownPeerSentALoginSucceedMessage(peerPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		PeerEntry peerEntry = BrokerDAOFactory.getInstance().getPeerDAO().getPeerEntry(
				request.getPeerAddress());
		
		if (peerEntry == null) {
			responses.add(new LoggerResponseTO(LocalWorkerProviderClientMessages.getUnknownPeerSentALoginSucceedMessage(peerPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (peerEntry.isDown()) {
			responses.add(new LoggerResponseTO(LocalWorkerProviderClientMessages.getPeerDownLoginSucceedMessage(peerPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (peerEntry.isLogged()) {
			responses.add(new LoggerResponseTO(LocalWorkerProviderClientMessages.getPeerLoggedLoginSucceedMessage(peerPublicKey), LoggerResponseTO.WARN));
			
			return responses; 
		}
		
		BrokerLoginResult result = request.getResult();
		
		if (result.hasAnErrorOcurred()) {
			responses.add(new LoggerResponseTO(LocalWorkerProviderClientMessages.getErrorOcurredWhileLoggingIn(
					result.getErrorMessage(), peerPublicKey), LoggerResponseTO.WARN));
			
			peerEntry.setAsNotLogged(result.getErrorMessage());
			
			return responses;
		}
		
		peerEntry.setAsLogged();
		
		
		JobDAO jobDAO = BrokerDAOFactory.getInstance().getJobDAO();
		
		for(SchedulerIF scheduler: jobDAO.getSchedulers()) {
			scheduler.loginSucceed(peerDeploymentID.toString(), responses);
		}
		
		return responses;
	}
}
