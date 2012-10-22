package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.controller.allocation.DefaultAllocator;
import org.ourgrid.peer.business.controller.messages.RequestMessages;
import org.ourgrid.peer.business.controller.messages.VOMSMessages;
import org.ourgrid.peer.business.controller.voms.VomsAuthorisationStrategy;
import org.ourgrid.peer.business.controller.voms.VomsAuthorisationStrategy.VomsAuthorisationData;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.request.RemoteWorkerProviderRequestWorkersRequestTO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalAllocableWorker;
import org.ourgrid.peer.to.Priority;
import org.ourgrid.peer.to.RemoteConsumer;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.network.certification.CertificateCRLPair;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;

public class RemoteWorkerProviderRequestWorkersRequester implements RequesterIF<RemoteWorkerProviderRequestWorkersRequestTO> {
	
	public List<IResponseTO> execute(RemoteWorkerProviderRequestWorkersRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String senderPublicKey = request.getSenderPublicKey();
		
		RequestSpecification requestSpec = request.getRequestSpec();
		String remoteWorkerProviderClientAddress = request.getRemoteWorkerProviderClientAddress();
		
		if(senderPublicKey == null) {
			responses.add(new LoggerResponseTO(RequestMessages.getRequestWithNoPublicKeyMessage(requestSpec), LoggerResponseTO.WARN));
			
			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(remoteWorkerProviderClientAddress);
			
			responses.add(releaseTO);
			
			return responses;
		}
		
		if(remoteWorkerProviderClientAddress == null) {
			responses.add(new LoggerResponseTO(RequestMessages.getRequestWithNoClientMessage(requestSpec), LoggerResponseTO.WARN));
//			serviceManager.release(workerProviderClient);
			return responses;
		}
		
		if(requestSpec == null) {
			responses.add(new LoggerResponseTO(RequestMessages.getNullRequestSpecMessage(
					request.getRemoteWorkerProviderClientAddress()), LoggerResponseTO.WARN));
			
			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(remoteWorkerProviderClientAddress);
			
			responses.add(releaseTO);
			
			return responses;
		}
		
		if(requestSpec.getRequiredWorkers() < 1) {
			responses.add(new LoggerResponseTO(RequestMessages.getNonPositiveWorkerRequestMessage(request.getRemoteWorkerProviderClientAddress(),
					requestSpec.getRequestId()), LoggerResponseTO.WARN));
			
			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(remoteWorkerProviderClientAddress);
			
			responses.add(releaseTO);
			
			return responses;
		}
		
		
		//authentication
		Collection<CertificateCRLPair> requestingPeersCAsData = PeerDAOFactory.getInstance().getPeerCertificationDAO().getRequestingPeersCAsData();
		
		if(requestingPeersCAsData != null && !requestingPeersCAsData.isEmpty()) {
			
			if(!CertificationUtils.isCertificateValid(requestSpec.getRequesterCertPath())) {
//			if (!request.isCertificateValid()) {
				responses.add(new LoggerResponseTO(RequestMessages.getInvalidCertPathMessage(request.getRemoteWorkerProviderClientContainerID(),
						requestSpec.getRequestId()), LoggerResponseTO.WARN));
				
				
				ReleaseResponseTO releaseTO = new ReleaseResponseTO();
				releaseTO.setStubAddress(remoteWorkerProviderClientAddress);
				
				responses.add(releaseTO);
				
				return responses;
			}
			
			if(!CertificationUtils.isCertPathIssuedByCA(requestSpec.getRequesterCertPath(), requestingPeersCAsData)) {
			//if (!request.isCertPathIssuedByCA()) {
				responses.add(new LoggerResponseTO(RequestMessages.getNonIssuedCertPathMessage(request.getRemoteWorkerProviderClientContainerID(),
						requestSpec.getRequestId()), LoggerResponseTO.WARN));
				
				ReleaseResponseTO releaseTO = new ReleaseResponseTO();
				releaseTO.setStubAddress(remoteWorkerProviderClientAddress);
				
				responses.add(releaseTO);
				
				return responses;
			}
			
		}
		
		VomsAuthorisationData authorisationData = null;
		
		//authorization
		//if(serviceManager.getContainerContext().isEnabled(PeerConfiguration.PROP_USE_VOMS)) {
		if (request.useVomsAuth()) {
			VomsAuthorisationStrategy authStrategy = new VomsAuthorisationStrategy(request.getMyCertPath(), request.getMyPrivateKey());
			
			try {
				/*authorisationData = authStrategy.authorise(CertificationUtils.getCertSubjectDN(
						requestSpec.getRequesterCertPath()));*/
//				authorisationData = authStrategy.authorise(request.getProviderDN(), request.getVomsURLList());
				authorisationData = authStrategy.authorise(CertificationUtils.getCertSubjectDN(
						requestSpec.getRequesterCertPath()), request.getVomsURLList());
			} catch (Exception e) {
				responses.add(new LoggerResponseTO(VOMSMessages.getErrorOnConnectingToVOMSMessage(request.getRemoteWorkerProviderClientContainerID(),
						requestSpec.getRequestId(), e.getMessage()), LoggerResponseTO.WARN));
				
				ReleaseResponseTO releaseTO = new ReleaseResponseTO();
				releaseTO.setStubAddress(remoteWorkerProviderClientAddress);
				
				responses.add(releaseTO);
				
				return responses;
			}
			
			if (!authorisationData.isAuthorised()) {
				responses.add(new LoggerResponseTO(VOMSMessages.getNonAuthorisedConsumerMessage(request.getRemoteWorkerProviderClientContainerID(),
						requestSpec.getRequestId()), LoggerResponseTO.WARN));
				
				ReleaseResponseTO releaseTO = new ReleaseResponseTO();
				releaseTO.setStubAddress(remoteWorkerProviderClientAddress);
				
				responses.add(releaseTO);
				
				return responses;
			}
		}
		
		responses.add(new LoggerResponseTO(RequestMessages.getRequestWorkersMesasge(request.getRemoteWorkerProviderClientContainerID(),
				requestSpec), LoggerResponseTO.INFO));

		doRequest(responses, requestSpec, remoteWorkerProviderClientAddress, request.getRemoteWorkerProviderClientPublicKey(), 
				request.getConsumerDN(), authorisationData, 
				CertificationUtils.getCertSubjectDN(request.getMyCertPath()));
		
		return responses;
	}
	
	/**
	 * Executes a request. This method does not validate the request.
	 * @param requestSpec 
	 * @param workerProviderClient
	 * @param authorisationData 
	 */
	@Req("REQ011")
	private void doRequest(List<IResponseTO> responses, RequestSpecification requestSpec, String workerProviderClientAddress, 
			String workerProviderClientPublicKey, String consumerDN,
			VomsAuthorisationData authorisationData, String myCertSubjectDN) {
		
		/*DeploymentID workerProviderClientID = serviceManager.getStubDeploymentID(workerProviderClient);
		RemoteConsumer consumer = serviceManager.getDAO(ConsumerDAO.class).getRemoteConsumer(
				workerProviderClientID.getPublicKey());*/
		RemoteConsumer consumer = PeerDAOFactory.getInstance().getConsumerDAO().getRemoteConsumer(workerProviderClientPublicKey);
		
		if (consumer == null) {
			consumer = new RemoteConsumer();
			consumer.setConsumer(workerProviderClientAddress, workerProviderClientPublicKey);
			//consumer.setConsumerDN(CertificationUtils.getCertSubjectDN(serviceManager.getSenderCertPath()));
			consumer.setConsumerDN(consumerDN);
			
			Priority priority = createPriority(responses, workerProviderClientPublicKey);
			consumer.setPriority(priority);
			
			PeerDAOFactory.getInstance().getConsumerDAO().addRemoteConsumer(workerProviderClientPublicKey, consumer);
		}
		
		if (!workerProviderClientAddress.equals(consumer.getConsumerAddress())) {
			consumer.setConsumer(workerProviderClientAddress, workerProviderClientPublicKey);
		}
		
		List<AllocableWorker> allocableWbag = PeerDAOFactory.getInstance().getAllocationDAO().getAllAllocableWorkers();
		
		//try to serve the request
		List<AllocableWorker> allocation = DefaultAllocator.getInstance().
												getAllocableWorkersForRemoteRequest(
														responses, consumer, requestSpec, myCertSubjectDN, allocableWbag);
		
				
		if(! allocation.isEmpty()){
			RegisterInterestResponseTO to = new RegisterInterestResponseTO();
			to.setMonitorableAddress(workerProviderClientAddress);
			to.setMonitorableType(RemoteWorkerProviderClient.class);
			to.setMonitorName(PeerConstants.REMOTE_WORKER_PROVIDER);
			
			responses.add(to);
			
			for (AllocableWorker allocableWorker : allocation) {
//				dispatchAllocation(requestSpec.getRequestId(), workerProviderClient, 
//						consumer, allocableWorker, authorisationData);
				dispatchAllocation(responses, requestSpec.getRequestId(), workerProviderClientAddress, workerProviderClientPublicKey, 
						consumer, allocableWorker, authorisationData);
			}
			
		} else if (consumer.getAllocableWorkers().isEmpty()){
//			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
//			releaseTO.setStubAddress(workerProviderClientAddress);
//			
//			responses.add(releaseTO);
		}
		
	}
	
	/**
	 * @return
	 */
	private Priority createPriority(List<IResponseTO> responses, String peerPublicKey) {
		return PeerDAOFactory.getInstance().getTrustCommunitiesDAO().getPriority(responses, peerPublicKey);
	}
	
	/**
	 * @param requestID 
	 * @param workerProviderClient
	 * @param consumer
	 * @param allocableWorker
	 * @param authorisationData 
	 */
	private void dispatchAllocation(List<IResponseTO> responses, long requestID, String workerProviderClientAddress, String workerProviderClientPublicKey,
			RemoteConsumer consumer, AllocableWorker allocableWorker, VomsAuthorisationData authorisationData) {
		
		if(allocableWorker.getConsumer() != null) {//preemption
			//DeploymentID workerID = serviceManager.getStubDeploymentID(allocableWorker.getWorkerManagement());
			responses.add(new LoggerResponseTO(RequestMessages.getRequestPreemptionMessage(requestID, 
					allocableWorker.getWorkerAddress(), 
					allocableWorker.getConsumer().getConsumerAddress()), LoggerResponseTO.INFO));
		}
		
		allocableWorker.clear();
		allocableWorker.setStatus(LocalWorkerState.DONATED);
		
		WorkerControl.getInstance().statusChanged(responses, 
				StringUtil.addressToUserAtServer(allocableWorker.getWorkerAddress()), LocalWorkerState.DONATED);
		
		allocableWorker.setConsumer(consumer);
		consumer.addWorker(allocableWorker);
		LocalAllocableWorker localAllocableWorker = (LocalAllocableWorker)allocableWorker;
		
		if (authorisationData == null) {
			localAllocableWorker.workForPeer(responses, workerProviderClientPublicKey);
		} else {
			localAllocableWorker.workForPeer(responses, workerProviderClientPublicKey, 
					authorisationData.getUsersDN());
		}
		
	}
	
}
