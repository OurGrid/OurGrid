/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.peer.business.controller.allocation;

import java.util.List;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.controller.WorkerNotificationController;
import org.ourgrid.peer.business.controller.messages.WorkerMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.util.RequestUtils;
import org.ourgrid.peer.dao.AllocationDAO;
import org.ourgrid.peer.response.DisposeRemoteWorkerResponseTO;
import org.ourgrid.peer.response.StopWorkingResponseTO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalAllocableWorker;
import org.ourgrid.peer.to.LocalConsumer;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.peer.to.RemoteAllocableWorker;
import org.ourgrid.peer.to.Request;
import org.ourgrid.reqtrace.Req;

public class RedistributionController {

	private static RedistributionController instance = null;
	
	private RedistributionController() {}
	
	public static RedistributionController getInstance() {
		if (instance == null) {
			instance = new RedistributionController();
		}
		return instance;
	}
	
	/**
	 * Redistributes a local worker
	 * @param responses 
	 * @param allocableWorker
	 * @return true if the local worker was allocated, false if it's idle
	 */
	public void redistributeDeliveredWorker(List<IResponseTO> responses, LocalAllocableWorker allocableWorker, LocalWorker localWorker) {
		
		Request suitableRequestForWorker = DefaultAllocator.getInstance().getRequestForWorkerSpecification(
				allocableWorker.getWorkerSpecification());
		
		if(suitableRequestForWorker == null){
			StopWorkingResponseTO to = new StopWorkingResponseTO();
			to.setWmAddress(allocableWorker.getWorkerAddress());
			
			responses.add(to);
			return;
		}
		
		allocateRequestToIdleWorker(responses, allocableWorker, suitableRequestForWorker, localWorker);
	}

	public boolean redistributeIdleWorker(List<IResponseTO> responses, LocalWorker localWorker) {
		Request suitableRequestForWorker = DefaultAllocator.getInstance().getRequestForWorkerSpecification(
				localWorker.getWorkerSpecification());
		
		if(suitableRequestForWorker != null){
			AllocationDAO allocationDAO = PeerDAOFactory.getInstance().getAllocationDAO();
			AllocableWorker allocableWorker = allocationDAO.getAllocableWorker(localWorker.getPublicKey());
			allocateRequestToIdleWorker(responses, allocableWorker, suitableRequestForWorker, localWorker);
			return true;
		}
		
		return false;
	}

	/**
	 * Redistributes a remote worker
	 * @param provider
	 * @param worker
	 * @param workerSpec
	 * @return true if the local worker was allocated, false if it was 
	 * disposed to its provider
	 */
	public boolean redistributeRemoteWorker(List<IResponseTO> responses, RemoteAllocableWorker allocable) {
		
		String workerAddress = allocable.getWorkerAddress();
		String workerPublicKey = allocable.getWorkerPubKey();
		
		if (workerAddress == null || !isThereAtLeastOneRequestThatMatchesAndNeedMoreWorkers(
				PeerDAOFactory.getInstance().getRequestDAO().getRunningRequests(), allocable.getWorkerSpecification())) {
			
			String providerAddress = allocable.getProviderAddress();
			
			removeRemoteWorker(responses, workerAddress, providerAddress, workerPublicKey);
			disposeWorkerToRemoteProvider(responses, workerAddress, providerAddress, workerPublicKey);

			return false;
		}

		Request request = DefaultAllocator.getInstance().getRequestForWorkerSpecification(allocable.getWorkerSpecification());
		
		RemoteAllocableWorker rAlloca = 
			PeerDAOFactory.getInstance().getAllocationDAO().getRemoteAllocableWorker(workerPublicKey);
		
		LocalConsumer localConsumer = request.getConsumer();
		rAlloca.setConsumer(localConsumer);
		rAlloca.setRequest(request);
        request.addAllocableWorker(rAlloca);
		
		rAlloca.workForBroker(responses);

		long requestId = request.getSpecification().getRequestId();
		
		if(!request.needMoreWorkers()) {
			RequestUtils.cancelScheduledRequest(responses, requestId);
		}
		
		return true;
	}

	/**
	 * Redistributes an AllocableWorker
	 * @param allocable
	 */
	@Req({"REQ015", "REQ016"})
	public void redistributeWorker(List<IResponseTO> responses, AllocableWorker allocable) {
		
		RequestSpecification requestSpecification = allocable.getRequest().getSpecification();
		
		allocable.deallocate();
		
		if(allocable.isWorkerLocal()){
			
			LocalWorker localWorker = ((LocalAllocableWorker) allocable).getLocalWorker();

			redistributeLocalWorker(responses, localWorker, (LocalAllocableWorker) allocable);
			
		} else {
			RemoteAllocableWorker rAllocable = (RemoteAllocableWorker) allocable;
			redistributeRemoteWorker(responses, rAllocable);
		}
		
		Request request = PeerDAOFactory.getInstance().getRequestDAO().getRequest(
				requestSpecification.getRequestId());
		
		if (request != null && request.needMoreWorkers()) {
			scheduleRequest(responses, requestSpecification);
		}
	}
	
	private void removeRemoteWorker(List<IResponseTO> responses, String workerAddress,
			String providerAddress, String workerPublicKey) {
		WorkerControl.getInstance().removeRemoteWorker(responses, StringUtil.addressToUserAtServer(workerAddress));
		PeerDAOFactory.getInstance().getAllocationDAO().removeRemoteAllocableWorker(workerPublicKey);

		ReleaseResponseTO releaseTO = new ReleaseResponseTO();
		releaseTO.setStubAddress(workerAddress);
		
		responses.add(releaseTO);
	}
	
	private void disposeWorkerToRemoteProvider(List<IResponseTO> responses, String workerAddress,
			String providerAddress, String workerPublicKey) {
		boolean providerIsUp = providerAddress != null;

		if (providerIsUp) {
			responses.add(new LoggerResponseTO(WorkerMessages.getDisposingWorkerToRemoteProviderMessage(
					providerAddress, workerAddress), LoggerResponseTO.DEBUG));
			
			DisposeRemoteWorkerResponseTO to = new DisposeRemoteWorkerResponseTO();
			to.setProviderAddress(providerAddress);
			to.setWorkerAddress(workerAddress);
			to.setWorkerPublicKey(workerPublicKey);
			
			responses.add(to);
		}
	}
	
	private boolean isThereAtLeastOneRequestThatMatchesAndNeedMoreWorkers(
			List<Request> requests, WorkerSpecification spec) {
		
		for (Request request : requests) {
			if (Util.matchAndNeedWorkers(spec, request)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Req("REQ117")
	public LocalAllocableWorker createAllocableWorker(LocalWorker localWorker, String localWorkerProviderAddress, String myCertPathDN) {
		
		AllocationDAO allocationDAO = PeerDAOFactory.getInstance().getAllocationDAO();
		
		/*LocalWorkerProvider localWorkerProvider = (LocalWorkerProvider) serviceManager.getObjectDeployment(
				PeerConstants.LOCAL_WORKER_PROVIDER).getObject();
		
		String myCertPathDN = CertificationUtils.getCertSubjectDN(serviceManager.getMyCertPath());
		
		LocalAllocableWorker allocableWorker = new LocalAllocableWorker(localWorker,
				localWorkerProvider,myCertPathDN);*/
		
		LocalAllocableWorker allocableWorker = new LocalAllocableWorker(localWorker, localWorkerProviderAddress, myCertPathDN);
		
		allocationDAO.addLocalAllocableWorker(localWorker.getPublicKey(), allocableWorker);
		
		return allocableWorker;
	}
	
	@Req("REQ117")
	private void allocateRequestToIdleWorker(List<IResponseTO> responses, AllocableWorker allocableWorker, Request request, LocalWorker localWorker) {
		
		localWorker.setStatus(LocalWorkerState.IN_USE);
		
		WorkerControl.getInstance().statusChanged(responses, localWorker.getWorkerUserAtServer(), LocalWorkerState.IN_USE);
		
		LocalConsumer localConsumer = request.getConsumer();
		allocableWorker.setConsumer(localConsumer);
		allocableWorker.setRequest(request);
	    request.addAllocableWorker(allocableWorker);
	    allocableWorker.workForBroker(responses);
	    
	    if(!request.needMoreWorkers()){
			RequestUtils.cancelScheduledRequest(responses, request.getSpecification().getRequestId());
	    }
	}
	
	
	/**
	 * @param requestSpec
	 */
	private void scheduleRequest(List<IResponseTO> responses, RequestSpecification requestSpec) {
		RequestUtils.scheduleRequest(responses, requestSpec);
	}
	
	@Req("REQ015")
	private void redistributeLocalWorker(List<IResponseTO> responses, LocalWorker localWorker, LocalAllocableWorker allocableWorker) {
		
		redistributeDeliveredWorker(responses, allocableWorker, localWorker);
		
//		RegisterInterestResponseTO registerInterestResponse = new RegisterInterestResponseTO();
//		registerInterestResponse.setMonitorableAddress(allocableWorker.getWorkerAddress());
//		registerInterestResponse.setMonitorableType(WorkerManagement.class);
//		registerInterestResponse.setMonitorName(PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
//		
//		responses.add(registerInterestResponse);
	}
}
