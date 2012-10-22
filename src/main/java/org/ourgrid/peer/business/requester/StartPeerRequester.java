package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.DeployServiceResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.common.internal.response.ScheduleActionWithFixedDelayResponseTO;
import org.ourgrid.common.specification.main.JDLTagsPublisher;
import org.ourgrid.common.statistics.control.LoginControl;
import org.ourgrid.common.statistics.control.PeerControl;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.controller.accounting.AccountingConstants;
import org.ourgrid.peer.business.controller.actions.DelayedInterestRegistrationOnDiscoveryService;
import org.ourgrid.peer.business.controller.actions.DiscoveryServiceUpdateAction;
import org.ourgrid.peer.business.controller.actions.InvokeGarbageCollectorAction;
import org.ourgrid.peer.business.controller.actions.RequestWorkersAction;
import org.ourgrid.peer.business.controller.actions.SaveAccountingAction;
import org.ourgrid.peer.business.controller.actions.UpdatePeerUptimeAction;
import org.ourgrid.peer.business.controller.messages.PeerControlMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.communication.dao.PeerCertificationDAO;
import org.ourgrid.peer.communication.receiver.DiscoveryServiceClientReceiver;
import org.ourgrid.peer.communication.receiver.LocalWorkerProviderReceiver;
import org.ourgrid.peer.communication.receiver.RemoteWorkerManagementClientReceiver;
import org.ourgrid.peer.communication.receiver.RemoteWorkerProviderClientReceiver;
import org.ourgrid.peer.communication.receiver.RemoteWorkerProviderReceiver;
import org.ourgrid.peer.communication.receiver.WorkerManagementClientReceiver;
import org.ourgrid.peer.dao.DiscoveryServiceClientDAO;
import org.ourgrid.peer.request.StartPeerRequestTO;
import org.ourgrid.peer.response.AddActionForRepetitionResponseTO;

import br.edu.ufcg.lsd.commune.network.certification.CertificateCRLPair;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;

public class StartPeerRequester implements RequesterIF<StartPeerRequestTO> {
	
	private static final String CONF_XML_PATH = "peer-hibernate.cfg.xml";

	public List<IResponseTO> execute(StartPeerRequestTO request) {
		PeerDAOFactory.getInstance().reset();
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		HibernateUtil.setUp(CONF_XML_PATH);
		
		//Loading glue tags
		String glueTagsFilePath = request.getProperties().get(PeerConfiguration.PROP_TAGS_FILE_PATH);
		if(glueTagsFilePath != null && !(glueTagsFilePath.length() == 0)){
			JDLTagsPublisher.loadGLUETags(glueTagsFilePath);
		}
		
		createDAOs(request);
		createServices(responses);
		
		//TODO Discovery Service Client
		configRepetitionActions(responses);
		configBalanceRanking(responses, request.getFilePath());
		
		if (request.shouldJoinCommunity()) {
			
			DeployServiceResponseTO deployTO = new DeployServiceResponseTO();
			deployTO.setServiceClass(DiscoveryServiceClientReceiver.class);
			deployTO.setServiceName(PeerConstants.DS_CLIENT);
			responses.add(deployTO);
			
			String networkStr = request.getNetworkStr();
			
			if (networkStr == null || networkStr.equals("")) {
				throw new RuntimeException("Context could not be loaded. " +
						"If join community is set to 'yes', Discovery Service network property is required.");
			}
			
			List<String> dsAddresses = request.getDsAddress();
			
			DiscoveryServiceClientDAO discoveryServiceClientDAO = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
			
			for (String dsAddress : dsAddresses) {
				RegisterInterestResponseTO registerTO = new RegisterInterestResponseTO();
				registerTO.setMonitorName(PeerConstants.DS_CLIENT);
				registerTO.setMonitorableType(DiscoveryService.class);
				registerTO.setMonitorableAddress(dsAddress);
				
				responses.add(registerTO);
				
				discoveryServiceClientDAO.addDsAddress(dsAddress);
			}
			
			AddActionForRepetitionResponseTO dsUpdateActionTO = new AddActionForRepetitionResponseTO();
			dsUpdateActionTO.setActionClass(DiscoveryServiceUpdateAction.class);
			dsUpdateActionTO.setActionName(PeerConstants.DS_ACTION_NAME);
			
			responses.add(dsUpdateActionTO);
			
			AddActionForRepetitionResponseTO delayedInterestTO = new AddActionForRepetitionResponseTO();
			delayedInterestTO.setActionClass(DelayedInterestRegistrationOnDiscoveryService.class);
			delayedInterestTO.setActionName(PeerConstants.DELAYED_DS_INTEREST_ACTION_NAME);
			
			responses.add(delayedInterestTO);
			
		}

		PeerControl.getInstance().updatePeer(responses, request.getMyUserAtServer(), request.getMyCertSubjectDN(), request.getDescription(),
				request.getEmail(), request.getLabel(), request.getLatitude(), request.getLongitude());
		PeerControl.getInstance().registerPeerStatusChange(responses, request.getMyUserAtServer());
		LoginControl.getInstance().clearLogins(responses, request.getMyUserAtServer());
		
		ScheduleActionWithFixedDelayResponseTO peerUpdateTimeScheduleTO = new ScheduleActionWithFixedDelayResponseTO();
		peerUpdateTimeScheduleTO.setActionName(PeerConstants.UPDATE_PEER_UPTIME_ACTION_NAME);
		peerUpdateTimeScheduleTO.setDelay(PeerConstants.UPDATE_UPTIME_DELAY);
		peerUpdateTimeScheduleTO.setTimeUnit(TimeUnit.SECONDS);
		
		responses.add(peerUpdateTimeScheduleTO);
		
		ScheduleActionWithFixedDelayResponseTO invokeGarbageCollectorTO = new ScheduleActionWithFixedDelayResponseTO();
		invokeGarbageCollectorTO.setActionName(PeerConstants.INVOKE_GARBAGE_COLLECTOR_ACTION_NAME);
		invokeGarbageCollectorTO.setDelay(PeerConstants.INVOKE_GARBAGE_COLLECTOR_DELAY);
		invokeGarbageCollectorTO.setTimeUnit(TimeUnit.SECONDS);
		
		responses.add(invokeGarbageCollectorTO);
		
		responses.add(new LoggerResponseTO(PeerControlMessages.getSuccessfullyStartedPeerMessage(),
				LoggerResponseTO.INFO));
		
		return responses;
	}

	
	private void createDAOs(StartPeerRequestTO request) {
		PeerCertificationDAO peerCertificationDAO = PeerDAOFactory.getInstance().getPeerCertificationDAO();
		
		List<CertificateCRLPair> requestingCAsData = CertificationUtils.loadCAsData(
				request.getRequestingCACertificatePath());
		List<CertificateCRLPair> receivingCAsData = CertificationUtils.loadCAsData(
				request.getReceivingCACertificatePath());
		
		peerCertificationDAO.setReceivingCAsData(receivingCAsData);
		peerCertificationDAO.setRequestingCAsData(requestingCAsData);
		
		PeerDAOFactory.getInstance().getPeerPropertiesDAO().setRequestRepeatDelayInSeconds(
				Integer.valueOf(request.getProperties().get(PeerConfiguration.PROP_REPEAT_REQUEST_DELAY)));
	}


	private void configRepetitionActions(List<IResponseTO> responses) {
		AddActionForRepetitionResponseTO actionTO = new AddActionForRepetitionResponseTO();
		actionTO.setActionClass(RequestWorkersAction.class);
		actionTO.setActionName(PeerConstants.REQUEST_WORKERS_ACTION_NAME);
		
		responses.add(actionTO);
		
		actionTO = new AddActionForRepetitionResponseTO();
		actionTO.setActionClass(SaveAccountingAction.class);
		actionTO.setActionName(PeerConstants.SAVE_ACCOUNTING_ACTION_NAME);
		
		responses.add(actionTO);
		
		actionTO = new AddActionForRepetitionResponseTO();
		actionTO.setActionClass(UpdatePeerUptimeAction.class);
		actionTO.setActionName(PeerConstants.UPDATE_PEER_UPTIME_ACTION_NAME);
		
		responses.add(actionTO);
		
		actionTO = new AddActionForRepetitionResponseTO();
		actionTO.setActionClass(InvokeGarbageCollectorAction.class);
		actionTO.setActionName(PeerConstants.INVOKE_GARBAGE_COLLECTOR_ACTION_NAME);
		
		responses.add(actionTO);
	}
	
	/**
	 * Load balances from the ranking file and configure the timer to save
	 * the balances in the file periodically.
	 */
	private void configBalanceRanking(List<IResponseTO> responses, String filePath) {
		
		PeerDAOFactory.getInstance().getAccountingDAO().loadBalancesRanking(filePath);
		long frequence = AccountingConstants.RANKING_SAVING_FREQ;

		ScheduleActionWithFixedDelayResponseTO to = new ScheduleActionWithFixedDelayResponseTO();
		to.setActionName(PeerConstants.SAVE_ACCOUNTING_ACTION_NAME);
		to.setDelay(frequence);
		to.setTimeUnit(TimeUnit.SECONDS);
		
		responses.add(to);
	}
	
	private void createServices(List<IResponseTO> responses) {
		DeployServiceResponseTO to = new DeployServiceResponseTO();
		to.setServiceClass(WorkerManagementClientReceiver.class);
		to.setServiceName(PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
		responses.add(to);
		
		to = new DeployServiceResponseTO();
		to.setServiceClass(LocalWorkerProviderReceiver.class);
		to.setServiceName(PeerConstants.LOCAL_WORKER_PROVIDER);
		responses.add(to);
		
		to = new DeployServiceResponseTO();
		to.setServiceClass(RemoteWorkerProviderReceiver.class);
		to.setServiceName(PeerConstants.REMOTE_WORKER_PROVIDER);
		responses.add(to);
		
		to = new DeployServiceResponseTO();
		to.setServiceClass(RemoteWorkerProviderClientReceiver.class);
		to.setServiceName(PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		responses.add(to);
		
		to = new DeployServiceResponseTO();
		to.setServiceClass(RemoteWorkerManagementClientReceiver.class);
		to.setServiceName(PeerConstants.REMOTE_WORKER_MANAGEMENT_CLIENT);
		responses.add(to);
		
//		to = new DeployServiceResponseTO();
//		to.setServiceClass(DiscoveryServiceClientController.class);
//		to.setServiceName(PeerConstants.DS_CLIENT);
//		responses.add(to);
	}
	
}
