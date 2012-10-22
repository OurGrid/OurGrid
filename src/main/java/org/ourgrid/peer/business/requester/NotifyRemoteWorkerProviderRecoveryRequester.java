package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.statistics.control.PeerControl;
import org.ourgrid.peer.business.controller.messages.DiscoveryServiceClientMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.DiscoveryServiceClientDAO;
import org.ourgrid.peer.request.NotifyRemoteWorkerProviderRecoveryRequestTO;

import br.edu.ufcg.lsd.commune.network.certification.CertificateCRLPair;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;

public class NotifyRemoteWorkerProviderRecoveryRequester implements RequesterIF<NotifyRemoteWorkerProviderRecoveryRequestTO> {
	
	public List<IResponseTO> execute(NotifyRemoteWorkerProviderRecoveryRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (CertificationUtils.getCertSubjectDN(request.getMyCertPath()).equals(
				CertificationUtils.getCertSubjectDN(request.getRwpCertPath()))) {
			
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					DiscoveryServiceClientMessages.getSameCertificateDNMessage(request.getRwpAddress()), 
					LoggerResponseTO.DEBUG);
			responses.add(loggerResponse);
			
			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(request.getRwpAddress());
			
			responses.add(releaseTO);
			
			return responses;
		}
		
		Collection<CertificateCRLPair> receivedPeersCAsData = PeerDAOFactory.getInstance().
			getPeerCertificationDAO().getReceivedPeersCAsData();
		
		if (receivedPeersCAsData != null && !receivedPeersCAsData.isEmpty()) {
			
			if(!CertificationUtils.isCertificateValid(request.getRwpCertPath())) {
				
				LoggerResponseTO loggerResponse = new LoggerResponseTO(
						DiscoveryServiceClientMessages.getInvalidCertPathMessage(request.getRwpAddress()), 
						LoggerResponseTO.WARN);
				responses.add(loggerResponse);
				
				ReleaseResponseTO releaseTO = new ReleaseResponseTO();
				releaseTO.setStubAddress(request.getRwpAddress());
				
				responses.add(releaseTO);
				
				return responses;
			}
			
			if(!CertificationUtils.isCertPathIssuedByCA(request.getRwpCertPath(), receivedPeersCAsData)) {
				
				LoggerResponseTO loggerResponse = new LoggerResponseTO(
						DiscoveryServiceClientMessages.getNonIssuedCertPathMessage(request.getRwpAddress()), 
						LoggerResponseTO.WARN);
				responses.add(loggerResponse);
				
				ReleaseResponseTO releaseTO = new ReleaseResponseTO();
				releaseTO.setStubAddress(request.getRwpAddress());
				
				responses.add(releaseTO);
				
				return responses;
			}
			
		}
		
		DiscoveryServiceClientDAO dao = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
		dao.addRemoteWorkerProviderAddress(request.getRwpAddress());
		
		PeerControl.getInstance().insertPeer(responses, request.getRwpUserAtServer(), 
				CertificationUtils.getCertSubjectDN(request.getRwpCertPath()));
		
		return responses;
	}
	
}
