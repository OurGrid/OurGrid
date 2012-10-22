package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.worker.business.controller.EnvironmentController;
import org.ourgrid.worker.business.controller.GridProcessError;
import org.ourgrid.worker.business.controller.WorkerController;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.exception.UnableToCreatePlaypenException;
import org.ourgrid.worker.business.exception.UnableToCreateStorageException;
import org.ourgrid.worker.business.exception.VOMSAuthorizationException;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.request.StartWorkRequestTO;
import org.ourgrid.worker.response.ErrorOcurredMessageHandleResponseTO;
import org.ourgrid.worker.response.WorkerIsReadyMessageHandleResponseTO;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;

public class StartWorkRequester implements RequesterIF<StartWorkRequestTO> {

	public List<IResponseTO> execute(StartWorkRequestTO request) {
		String brokerAddress = StringUtil.deploymentIDToAddress(request.getClientDeploymentID());
		String brokerPubKey = request.getBrokerPublicKey();
		String senderPubKey = request.getSenderPublicKey();
		X509CertPath senderCerthPath = request.getSenderCerthPath();
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		if (!senderPubKey.equals(workerStatusDAO.getConsumerPublicKey()) ||
				!senderPubKey.equals(brokerPubKey)) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.getUnknownClientSendsStartWorkMessage(brokerAddress, brokerPubKey), LoggerResponseTO.WARN));
			return responses;
		}
		
		//Worker is broker-authorized
		if (workerStatusDAO.isAuthorisedForBroker()) {
			
			if (!CertificationUtils.isCertificateValid(senderCerthPath)) {
				responses.add(new LoggerResponseTO(WorkerControllerMessages.getInvalidCertPathMessage(brokerAddress), LoggerResponseTO.WARN));
				responses.add(new ErrorOcurredMessageHandleResponseTO(new GridProcessError(
						new VOMSAuthorizationException(CertificationUtils.getCertSubjectDN(senderCerthPath)), 
						GridProcessErrorTypes.BROKER_ERROR), brokerAddress));
				
				return responses;
			}
			
			if(!workerStatusDAO.getUsersDN().isEmpty() && !workerStatusDAO.containsUsersDN(CertificationUtils.getCertSubjectDN(senderCerthPath))) {
				responses.add(new LoggerResponseTO(WorkerControllerMessages.getBrokerIsNotOnVomsListMessage(brokerAddress), LoggerResponseTO.WARN));
				responses.add(new ErrorOcurredMessageHandleResponseTO(new GridProcessError(
						new VOMSAuthorizationException(CertificationUtils.getCertSubjectDN(senderCerthPath)), 
						GridProcessErrorTypes.BROKER_ERROR), brokerAddress));
	
				return responses;
			}
			
			if(!CertificationUtils.isCertPathIssuedByCA(senderCerthPath, 
					workerStatusDAO.getCaCertificates())) {
				responses.add(new LoggerResponseTO(WorkerControllerMessages.getNonIssuedCertPathMessage(brokerAddress), LoggerResponseTO.WARN));
				responses.add(new ErrorOcurredMessageHandleResponseTO(new GridProcessError(
						new VOMSAuthorizationException(CertificationUtils.getCertSubjectDN(senderCerthPath)), 
						GridProcessErrorTypes.BROKER_ERROR), brokerAddress));
				
				return responses;
			}
			
		}
		
		if (workerStatusDAO.isAllocated()) {
			WorkerController.getInstance().cleanWorker(responses);
		}
		
		try {
			EnvironmentController.getInstance().mountPlaypen(request.getPlaypenRoot());
		} catch (UnableToCreatePlaypenException e) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.getPlaypenCreationErrorMessage(brokerAddress, e.getPlaypenPath()), LoggerResponseTO.ERROR));
			responses.add(new ErrorOcurredMessageHandleResponseTO(new GridProcessError(e, GridProcessErrorTypes.IO_ERROR), brokerAddress));
			WorkerDAOFactory.getInstance().getEnvironmentDAO().resetEnvVariables();
			
			return responses;
		}
		
		try {
			EnvironmentController.getInstance().mountStorage(
					workerStatusDAO.getConsumerPublicKey(), request.getStorageRoot());
		} catch (UnableToCreateStorageException e) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.getStorageCreationErrorMessage(brokerAddress, e.getStoragePath()), LoggerResponseTO.ERROR));
			responses.add(new ErrorOcurredMessageHandleResponseTO(new GridProcessError(e, GridProcessErrorTypes.IO_ERROR), brokerAddress));
			WorkerDAOFactory.getInstance().getEnvironmentDAO().resetEnvVariables();
			
			return responses;
		}
		
		if (workerStatusDAO.isAllocatedForRemotePeer()) {
			WorkerDAOFactory.getInstance().getWorkAccountingDAO().setCurrentWorkAccounting(
					new WorkAccounting(workerStatusDAO.getRemotePeerDN()));
		}
		
		workerStatusDAO.setConsumerAddress(brokerAddress);				
		workerStatusDAO.setConsumerDeploymentID(request.getClientDeploymentID());
		workerStatusDAO.setWorkingState(true);
		
		responses.add(new LoggerResponseTO(WorkerControllerMessages.getSuccessfulStartWorkMessage(brokerAddress), LoggerResponseTO.INFO));
		responses.add(new WorkerIsReadyMessageHandleResponseTO(workerStatusDAO.getConsumerAddress()));
		
		return responses;
	}
}
