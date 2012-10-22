package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.peer.business.controller.WorkerNotificationController;
import org.ourgrid.peer.business.controller.messages.ConsumerMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.AllocationDAO;
import org.ourgrid.peer.request.RemoteDisposeWorkerRequestTO;
import org.ourgrid.peer.response.StopWorkingResponseTO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.peer.to.RemoteConsumer;

public class RemoteDisposeWorkerRequester implements RequesterIF<RemoteDisposeWorkerRequestTO> {

	public List<IResponseTO> execute(RemoteDisposeWorkerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		String consumerPublicKey = request.getConsumerPublicKey();

		RemoteConsumer remoteConsumer = PeerDAOFactory.getInstance().getConsumerDAO().getRemoteConsumer(consumerPublicKey);

		if (remoteConsumer == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					ConsumerMessages.getUnknownConsumerDisposingWorkerMessage(consumerPublicKey),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);

			return responses;
		}

		String remoteConsumerAddress = remoteConsumer.getConsumerAddress();
		String workerAddress = request.getWorkerAddress();

		if (workerAddress == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					ConsumerMessages.getRemoteConsumerDisposingNullWorkerMessage(remoteConsumerAddress),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);

			return responses;
		}

		String workerPublicKey = request.getWorkerPublicKey();

		LocalWorker localWorker = WorkerControl.getInstance().getLocalWorker(
				responses, request.getWorkerUserAtServer());

		if (localWorker != null && 
				(		localWorker.getStatus().equals(LocalWorkerState.OWNER) || 
						localWorker.getStatus().equals(LocalWorkerState.IDLE))) {

			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					ConsumerMessages.getRemoteConsumerDisposingNotAllocatedWorkerMessage(
							remoteConsumerAddress, workerAddress),
							LoggerResponseTO.WARN);
			responses.add(loggerResponse);

			return responses;
		}

		AllocationDAO allocationDAO = PeerDAOFactory.getInstance().getAllocationDAO();
		AllocableWorker allocable = allocationDAO.getAllocableWorker(workerPublicKey);

		if (allocable == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					ConsumerMessages.getRemoteConsumerDisposingUnknownWorkerMessage(remoteConsumerAddress),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);

			return responses;
		}

		if (!consumerPublicKey.equals(allocable.getConsumerPublicKey())) {

			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					ConsumerMessages.getRemoteConsumerDisposingNotAllocatedWorkerMessage(
							remoteConsumerAddress, workerAddress),
							LoggerResponseTO.WARN);
			responses.add(loggerResponse);

			return responses;
		}

		LoggerResponseTO loggerResponse = new LoggerResponseTO(
				ConsumerMessages.getRemoteClientDisposingWorkerMessage(
						remoteConsumerAddress, workerAddress),
						LoggerResponseTO.DEBUG);
		responses.add(loggerResponse);

		StopWorkingResponseTO stopWorkingResponse = new StopWorkingResponseTO();
		stopWorkingResponse.setWmAddress(allocable.getWorkerAddress());
		responses.add(stopWorkingResponse);

		//			Consumer consumer = allocable.getConsumer();
		allocable.deallocate();

		WorkerControl.getInstance().statusChanged(responses,
				allocable.getWorkerSpecification().getUserAndServer(), 
				LocalWorkerState.IDLE);

		if (remoteConsumer.getAllocableWorkers().isEmpty()) {
			PeerDAOFactory.getInstance().getConsumerDAO().removeRemoteConsumer(consumerPublicKey);

			//				ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			//				releaseTO.setStubAddress(consumer.getConsumerAddress());
			//				
			//				responses.add(releaseTO);
		}

		return responses;
	}


}
