package org.ourgrid.acceptance.util.broker;

import java.util.ArrayList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.acceptance.util.LocalWorkerData;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.control.BrokerControl;
import org.ourgrid.common.interfaces.control.BrokerControlClient;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.matchers.ControlOperationResultMatcher;
import org.ourgrid.matchers.JobStatusInfoMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleNotStartedException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_305_Util  extends BrokerAcceptanceUtil {

	public Req_305_Util(ModuleContext context) {
		super(context);
	
	}
	
	public void cancelJob(boolean isBrokerStarted, boolean hasJobs, int jobID, BrokerServerModule component) {
		cancelJob(isBrokerStarted, hasJobs, jobID, component, null, null);
	}

	public void cancelJob(boolean isBrokerStarted, boolean hasJobWithSuchID, int jobID, BrokerServerModule component, TestJob testJob, 
			List<LocalWorkerProvider> peers) {
		cancelJob(isBrokerStarted, hasJobWithSuchID, false, jobID, component, testJob, peers, new ArrayList<LocalWorkerData>());
	}
	
	public void cancelJob(boolean isBrokerStarted, boolean hasJobWithSuchID, boolean scheduledJob, 
			int jobID, BrokerServerModule component, TestJob testJob, 
			List<LocalWorkerProvider> peers, List<LocalWorkerData> workersToDispose) {
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		BrokerControl brokerControl = getBrokerControl(component);
		BrokerControlClient brokerControlClientMock = EasyMock.createMock(BrokerControlClient.class);
		
		DeploymentID clientID = new DeploymentID(new ServiceID("a", "b", "c", "d"));
		AcceptanceTestUtil.publishTestObject(application, clientID, brokerControlClientMock, BrokerControlClient.class);
		
		ObjectDeployment brokerOD = getBrokerControlDeployment(component);
		
		component.setLogger(newLogger);
		
		newLogger.info("Trying to cancel a job.");
		
		if (isBrokerStarted) {
			if (hasJobWithSuchID) {
				brokerControlClientMock.operationSucceed(ControlOperationResultMatcher.noError());
				newLogger.debug("Job [" + jobID + "] was cancelled.");
				newLogger.info("Exiting application.");
			} else {	
				brokerControlClientMock.operationSucceed(ControlOperationResultMatcher.
						eqCauseType("Job [" + jobID + "] was not cancelled, there is no job with such id.", Exception.class));
				newLogger.warn("Job [" + jobID + "] was not cancelled, there is no job with such id.");
			}
			newLogger.info("Operation cancel job succeed.");
			
		} else {
			brokerControlClientMock.operationSucceed(
					ControlOperationResultMatcher.eqCauseType("Broker control was not started", ModuleNotStartedException.class));
			newLogger.error("Broker control was not started.");
		}
		
		for (LocalWorkerData worker : workersToDispose) {
			newLogger.debug("Worker dispose: " + worker.workerID.getServiceID());
			newLogger.info("Stub to be released: " + worker.workerID.getServiceID());
			
		}
		

		EasyMock.replay(newLogger);
		EasyMock.replay(brokerControlClientMock);
		
		if (peers != null) {
			for (LocalWorkerProvider lwp : peers) {
				EasyMock.reset(lwp);
				for (LocalWorkerData worker : workersToDispose) {
					if (lwp == worker.lwp) {
						lwp.disposeWorker(worker.workerID.getServiceID());
						break;
					}
				}
				
				lwp.hereIsJobStats(JobStatusInfoMatcher.eqMatcher(jobID, testJob.getJobSpec()));
				lwp.finishRequest(testJob.getRequestByPeer(application, lwp));
				
				if (workersToDispose.size() > 0 && scheduledJob) {
					lwp.reportReplicaAccounting((GridProcessAccounting) EasyMock.anyObject());
				}
				EasyMock.replay(lwp);
			}
		}
		AcceptanceTestUtil.setExecutionContext(component, brokerOD, brokerOD.getDeploymentID().getPublicKey());
		brokerControl.cancelJob(brokerControlClientMock, jobID);
		
		if (peers != null) {
			for (LocalWorkerProvider lwp : peers) {
				EasyMock.verify(lwp);
			}
		}
		
		EasyMock.verify(brokerControlClientMock);
		EasyMock.verify(newLogger);
		
		EasyMock.reset(newLogger);
		
		component.setLogger(oldLogger);
	}
}
