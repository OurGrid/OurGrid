package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.statistics.control.UserControl;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.peer.business.controller.allocation.RedistributionController;
import org.ourgrid.peer.business.controller.messages.WorkerMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.AllocationDAO;
import org.ourgrid.peer.request.DisposeWorkerRequestTO;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.peer.to.PeerUser;
import org.ourgrid.peer.to.PeerUserReference;
import org.ourgrid.reqtrace.Req;

public class DisposeWorkerRequester implements RequesterIF<DisposeWorkerRequestTO> {

	public List<IResponseTO> execute(DisposeWorkerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		String brokerPublicKey = request.getBrokerPublicKey();

		String lwpcAddress = validateUser(responses, brokerPublicKey);
		if (lwpcAddress == null) {
			return responses;
		}

		String workerAddress = request.getWorkerAddress();

		AllocableWorker allocable = validateWorker(responses, workerAddress, 
				request.getWorkerUserAtServer(), request.getWorkerPublicKey(), 
				brokerPublicKey, lwpcAddress);
		if (allocable == null) {
			return responses;
		}

		LoggerResponseTO loggerResponse = new LoggerResponseTO(
				WorkerMessages.getDisposingWorkerMessage(
						allocable.getRequest().getSpecification().getRequestId(), lwpcAddress, workerAddress), 
						LoggerResponseTO.DEBUG);

		responses.add(loggerResponse);

		RedistributionController.getInstance().redistributeWorker(responses, allocable);

		return responses;
	}

	@Req("REQ015")
	private String validateUser(List<IResponseTO> responses, String brokerPublicKey) {
		PeerUser user = UserControl.getInstance().getUserByPublicKey(responses, brokerPublicKey);

		if (user == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					WorkerMessages.getUnknownConsumerDisposingWorkerMessage(brokerPublicKey),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);
			return null;
		}

		PeerUserReference loggedUser = PeerDAOFactory.getInstance().getUsersDAO().getLoggedUser(brokerPublicKey);
		if (loggedUser == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					WorkerMessages.getNotLoggedConsumerDisposingWorkerMessage(brokerPublicKey),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);

			return null;
		}

		String lwpcAddress = loggedUser.getWorkerProviderClientAddress();

		if (lwpcAddress == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					WorkerMessages.getNotLoggedUserDisposingWorkerMessage(brokerPublicKey),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);

			return null;
		}

		return lwpcAddress;
	}

	@Req("REQ015")
	private AllocableWorker validateWorker(List<IResponseTO> responses, String workerAddress, 
			String workerUserAtServer, String workerPublicKey,
			String brokerPublicKey, String lwpcAdress) {

		if (workerAddress == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					WorkerMessages.getNullWorkerDisposalMessage(lwpcAdress),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);

			return null;
		}

		LocalWorker localWorker = WorkerControl.getInstance().getLocalWorker(responses, workerUserAtServer);

		if (localWorker != null && 
				(		localWorker.getStatus().equals(LocalWorkerState.OWNER) || 
						localWorker.getStatus().equals(LocalWorkerState.IDLE))) {

			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					WorkerMessages.getConsumerDisposingNotAllocatedWorkerMessage(
							workerAddress, lwpcAdress),
							LoggerResponseTO.WARN);
			responses.add(loggerResponse);

			return null;
		}

		AllocationDAO allocationDAO = PeerDAOFactory.getInstance().getAllocationDAO();
		AllocableWorker allocable = allocationDAO.getAllocableWorker(workerPublicKey);

		if (allocable == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					WorkerMessages.getUnknownWorkerDisposalMessage(lwpcAdress),
					LoggerResponseTO.WARN);
			responses.add(loggerResponse);

			return null;
		}

		//		DeploymentID workerManagementID = serviceManager.getStubDeploymentID(allocable.getWorkerManagement());

		if (allocable.getConsumer() == null || allocable.getConsumer().getPublicKey() == null) {
			return null; //TODO
		}

		if (!allocable.getConsumer().getPublicKey().equals(brokerPublicKey)) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					WorkerMessages.getConsumerDisposingNotAllocatedWorkerMessage(
							workerAddress, lwpcAdress),
							LoggerResponseTO.WARN);
			responses.add(loggerResponse);

			return null;
		}

		return allocable;
	}

}
