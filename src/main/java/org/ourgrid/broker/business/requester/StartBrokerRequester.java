package org.ourgrid.broker.business.requester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.business.dao.BrokerDAOFactory;
import org.ourgrid.broker.business.dao.PeerDAO;
import org.ourgrid.broker.business.messages.BrokerControlMessages;
import org.ourgrid.broker.business.scheduler.SchedulerIF;
import org.ourgrid.broker.business.scheduler.workqueue.WorkQueueReplication;
import org.ourgrid.broker.communication.actions.SchedulerAction;
import org.ourgrid.broker.communication.receiver.LocalWorkerProviderClientReceiver;
import org.ourgrid.broker.communication.receiver.WorkerClientReceiver;
import org.ourgrid.broker.request.StartBrokerRequestTO;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.CreateMessageProcessorsResponseTO;
import org.ourgrid.common.internal.response.CreateRepeatedActionResponseTO;
import org.ourgrid.common.internal.response.DeployServiceResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.common.job.JobCounter;
import org.ourgrid.common.job.PersistentJobCounter;
import org.ourgrid.common.job.SimpleJobCounter;
import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.PeerConstants;

/**
 * Requirement 302
 */
public class StartBrokerRequester implements RequesterIF<StartBrokerRequestTO> {

	public List<IResponseTO> execute(StartBrokerRequestTO request) {
		BrokerDAOFactory.getInstance().reset();
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		createServices(responses);
		createRepeatedActions(responses);
		
		responses.add(new LoggerResponseTO(
				BrokerControlMessages.getTryingToStartBrokerMessage(), LoggerResponseTO.INFO));
		
		JobCounter jobCounter = createJobCounter(request);
		
		BrokerDAOFactory.getInstance().getJobCounterDAO().setJobCounter(jobCounter);
		
		initSchedulers(request);
		
		for(SchedulerIF scheduler: BrokerDAOFactory.getInstance().getJobDAO().getSchedulers()) {
			scheduler.start();	
		}
		
		setPeers(request.getPeersUserAtServer(), responses);
		
		responses.add(
				new LoggerResponseTO(
						BrokerControlMessages.getSuccessfullyStartedBrokerMessage(), LoggerResponseTO.INFO));
		
		return responses;
	}
	
	private void setPeers(List<String> peersUsersAtServer, List<IResponseTO> responses) {
		List<PeerSpecification> peersSpecs = new LinkedList<PeerSpecification>();
		
		responses.add(new LoggerResponseTO(
				"Trying to set peers " + peersUsersAtServer.toString(), 
				LoggerResponseTO.INFO));
		
		PeerDAO peerDAO = BrokerDAOFactory.getInstance().getPeerDAO();
		
		for (String peerUserAtServer : peersUsersAtServer) {
			String peerAddress = StringUtil.userAtServerToAddress(peerUserAtServer, 
					PeerConstants.MODULE_NAME, PeerConstants.LOCAL_WORKER_PROVIDER);
			
			RegisterInterestResponseTO to = new RegisterInterestResponseTO();
			to.setMonitorableAddress(peerAddress);
			to.setMonitorableType(LocalWorkerProvider.class);
			to.setMonitorName(BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);

			responses.add(to);
			
			PeerSpecification peerSpec = new PeerSpecification();
			peerSpec.setUserAtServer(peerUserAtServer);
			peersSpecs.add(peerSpec);
		}
				
		peerDAO.setPeers(peersSpecs);
		
		for(SchedulerIF scheduler: BrokerDAOFactory.getInstance().getJobDAO().getSchedulers()) {
			scheduler.setPeers(peersUsersAtServer.toArray(new String[]{}));
		}
		
	}

	protected void createServices(List<IResponseTO> responses) {
		DeployServiceResponseTO deployTO = new DeployServiceResponseTO();
		deployTO.setServiceName(BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);
		deployTO.setServiceClass(LocalWorkerProviderClientReceiver.class);
		responses.add(deployTO);
		
		deployTO = new DeployServiceResponseTO();
		deployTO.setServiceName(BrokerConstants.WORKER_CLIENT);
		deployTO.setServiceClass(WorkerClientReceiver.class);
		responses.add(deployTO);

		createMessageProcessors(responses);
	}
	
	private void createMessageProcessors(List<IResponseTO> responses) {
		responses.add(new CreateMessageProcessorsResponseTO());
	}
	
	private void createRepeatedActions(List<IResponseTO> responses) {
		CreateRepeatedActionResponseTO to = new CreateRepeatedActionResponseTO();
		to.setActionName(BrokerConstants.SCHEDULER_ACTION_NAME);
		to.setRepeatedAction(new SchedulerAction());
		
		responses.add(to);
	}
	
	/**
	 * Factory method for the JobCounter.
	 * @return JobCounter based on the Broker Context
	 */
	private JobCounter createJobCounter(StartBrokerRequestTO request) {
		
		if (request.isPersistentJobEnable()) {
			try {
				return new PersistentJobCounter(request.getJobCounterFilePath());
			} catch (IOException e) { }
		}
		return new SimpleJobCounter();
	}

	private void initSchedulers(StartBrokerRequestTO request) {
		
		String maxReplicas = request.getMaxReplicas();
		String maxFails = request.getMaxFails();
		String maxBlFails = request.getMaxBlackListFails();
		
		SchedulerIF scheduler = new WorkQueueReplication(Integer.valueOf(maxReplicas), 
				Integer.valueOf(maxFails), Integer.valueOf(maxBlFails));
		
		BrokerDAOFactory.getInstance().getJobDAO().addScheduler(scheduler);
	}
}