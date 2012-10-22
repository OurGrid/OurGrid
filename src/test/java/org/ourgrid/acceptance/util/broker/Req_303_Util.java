package org.ourgrid.acceptance.util.broker;

import java.util.Iterator;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.control.BrokerControl;
import org.ourgrid.common.interfaces.control.BrokerControlClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.matchers.ControlOperationResultMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleNotStartedException;
import br.edu.ufcg.lsd.commune.container.control.ModuleStoppedException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_303_Util extends BrokerAcceptanceUtil{

	public Req_303_Util(ModuleContext context) {
		super(context);

	}
	
	public void stopBroker(BrokerServerModule component, String senderPublicKey) {
		stopBroker(component, senderPublicKey, true);
	}
	
	public void stopBroker(BrokerServerModule component, boolean brokerStarted) {
		stopBroker(component, null, brokerStarted);
	}
	
	public void stopBroker(BrokerServerModule component, String senderPublicKey, 
			boolean isBrokerStarted) {
		stopBroker(component, senderPublicKey, isBrokerStarted, null, null);
	}
	
	public void stopBroker(BrokerServerModule component, String senderPublicKey, 
			boolean isBrokerStarted, List<TestJob> testJobs, List<LocalWorkerProvider> peers) {
		
		BrokerControl brokerControl = getBrokerControl(component);
		ObjectDeployment bcOD = getBrokerControlDeployment(component);
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		newLogger.info("Trying to stop broker component.");
		
		BrokerControlClient brokerControlClientMock = EasyMock.createMock(BrokerControlClient.class);
			
		if (!isBrokerStarted) {
			if(getBrokerControl(component) == null) {
				brokerControlClientMock.operationSucceed(
						ControlOperationResultMatcher.eqType(ModuleStoppedException.class));
			} else {
				senderPublicKey = bcOD.getDeploymentID().getPublicKey();
				brokerControlClientMock.operationSucceed(
						ControlOperationResultMatcher.eqCauseType("Broker control was not started", ModuleNotStartedException.class));
				newLogger.error("Broker control was not started.");
			}
		}  else {
			if (senderPublicKey == null) {
				senderPublicKey = bcOD.getDeploymentID().getPublicKey();
				brokerControlClientMock.operationSucceed(ControlOperationResultMatcher.noError());
				newLogger.info("Broker has been successfully shutdown.");
			} else if(!bcOD.getDeploymentID().getPublicKey().equals(senderPublicKey)) {
				newLogger.warn("An unknown entity tried to perform a control operation on the Broker. Only the local modules can perform this operation." +
						" Unknown entity public key: [" + senderPublicKey + "].");
			}
		}
		
		if (peers != null) {
			for (Iterator<LocalWorkerProvider> iterator = peers.iterator(); iterator.hasNext();) {
				EasyMock.reset(iterator.next());
			}
		}
		
		if (testJobs != null && peers != null) {
			for (TestJob testJob : testJobs) {
				for (LocalWorkerProvider lwp : peers) {
					RequestSpecification requestSpec = testJob.getRequestByPeer(application, lwp);
					lwp.finishRequest(requestSpec);
				}
			}
		}
		
		if (peers != null) {
			for (Iterator<LocalWorkerProvider> iterator = peers.iterator(); iterator.hasNext();) {
				EasyMock.replay(iterator.next());
			}
		}
				
		EasyMock.replay(newLogger);
		EasyMock.replay(brokerControlClientMock);
	
		AcceptanceTestUtil.setExecutionContext(component, bcOD, senderPublicKey);
		brokerControl.stop(false, false, brokerControlClientMock);
		
		if (peers != null) {
			for (LocalWorkerProvider lwp : peers) {
				EasyMock.verify(lwp);
			}
		}

		EasyMock.verify(brokerControlClientMock);
		EasyMock.verify(newLogger);
		component.setLogger(oldLogger);
	}
	
}
