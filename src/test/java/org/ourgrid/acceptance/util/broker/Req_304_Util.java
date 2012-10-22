package org.ourgrid.acceptance.util.broker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.broker.BrokerAcceptanceTestCase;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.business.scheduler.workqueue.JobInfo;
import org.ourgrid.broker.communication.receiver.LocalWorkerProviderClientReceiver;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.control.BrokerControl;
import org.ourgrid.common.interfaces.control.BrokerControlClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.job.Job;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.main.JDLSemanticAnalyzer;
import org.ourgrid.common.specification.peer.PeerSpecification;
import org.ourgrid.matchers.ControlOperationResultMatcher;
import org.ourgrid.matchers.JobAddedMatcher;
import org.ourgrid.matchers.RequestSpecMatcher;
import org.ourgrid.peer.PeerConstants;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleNotStartedException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_304_Util extends BrokerAcceptanceUtil {
	
	private BrokerAcceptanceUtil brokerAcceptanceUtil = new BrokerAcceptanceUtil(context);

	public Req_304_Util(ModuleContext context) {
		super(context);
	}
	
	public TestJob addJob(boolean isBrokerStarted, int jobId, BrokerServerModule component, String remoteExec,
			String jobLabel, String requirements, IOEntry entry, List<LocalWorkerProvider> peers) throws Exception {
		List<IOEntry> entries = new LinkedList<IOEntry>();
		entries.add(entry);
		return addJob(peers, isBrokerStarted, jobId, component, remoteExec, jobLabel, requirements, entries);
	}
	
	public TestJob addJob(List<LocalWorkerProvider> peers, boolean isBrokerStarted, int jobId, BrokerServerModule component, String remoteExec,
			String jobLabel, String requirements, List<IOEntry> entries) throws Exception {
		
		IOBlock initBlock = new IOBlock();
		for(IOEntry entry: entries){
			initBlock.putEntry(entry);
		}
		
		TaskSpecification taskSpec = new TaskSpecification(initBlock, remoteExec, null, null);
		
		List<TaskSpecification> taskSpecs = new ArrayList<TaskSpecification>();
		taskSpecs.add(taskSpec);
		JobSpecification jobSpec = new JobSpecification(jobLabel, requirements, taskSpecs);
		
		return addJob(isBrokerStarted, jobSpec, jobId, component, peers);
	}
	
	public TestJob addJob(boolean isBrokerStarted, int jobId, BrokerServerModule component, String jobLabel, 
			String requirements, TaskSpecification taskSpec, List<LocalWorkerProvider> peers) throws Exception {
		
		List<TaskSpecification> taskSpecs = new ArrayList<TaskSpecification>();
		taskSpecs.add(taskSpec);
		JobSpecification jobSpec = new JobSpecification(jobLabel, requirements, taskSpecs);
		
		return addJob(isBrokerStarted, jobSpec, jobId, component, peers);
	}
	
	public TestJob addJob(boolean isBrokerStarted, int jobId, BrokerServerModule component, String remoteExec,
			String jobLabel, String requirements, List<IOEntry> initEntries, List<IOEntry> finalEntries, List<LocalWorkerProvider> peers)
			throws Exception {
		
		IOBlock initBlock = new IOBlock();
		IOBlock finalBlock = new IOBlock();

		for(IOEntry entry: initEntries){
			initBlock.putEntry(entry);
		}
		
		for(IOEntry entry: finalEntries){
			finalBlock.putEntry(entry);
		}
		
		TaskSpecification taskSpec = new TaskSpecification(initBlock, remoteExec, finalBlock, "echo");
		taskSpec.setSourceDirPath(BrokerAcceptanceTestCase.BROKER_TEST_DIR);
		
		List<TaskSpecification> taskSpecs = new ArrayList<TaskSpecification>();
		taskSpecs.add(taskSpec);
		JobSpecification jobSpec = new JobSpecification(jobLabel, requirements, taskSpecs);
		
		return addJob(isBrokerStarted, jobSpec, jobId, component, peers);
	}
	
	public TestJob addJob(boolean isBrokerStarted, int jobId, BrokerServerModule component, String remoteExec, String jobLabel) throws Exception {
		TaskSpecification taskSpec = new TaskSpecification(null, remoteExec, null, null);
		List<TaskSpecification> taskSpecs = new ArrayList<TaskSpecification>();
		taskSpecs.add(taskSpec);
		
		
		JobSpecification jobSpec = new JobSpecification(jobLabel, "requirements", taskSpecs);
		
		return addJob(isBrokerStarted, jobSpec, jobId, component, null);
	}
	
	/**
	 * Using JDL
	 * 
	 * @param isBrokerStarted Is Broker started?
	 * @param jobId Job unique identification.
	 * @param component Broker component to invoke.
	 * @param jdl JDL expression 
	 * @return A test job structure.
	 * @throws Exception
	 */
	public TestJob addJob(boolean isBrokerStarted, int jobId, BrokerServerModule component, String jdl, List<LocalWorkerProvider> peers) throws Exception {
		
		JobSpecification jobSpec = JDLSemanticAnalyzer.compileJDL(jdl).get(0);
		
		return addJob(isBrokerStarted, jobSpec, jobId, component, peers);
	}
	
	public TestJob addJob(boolean isBrokerStarted, int jobId, BrokerServerModule component, String remoteExec, String jobLabel,
			List<LocalWorkerProvider> peers) throws Exception {
		TaskSpecification taskSpec = new TaskSpecification(null, remoteExec, null, null);
		List<TaskSpecification> taskSpecs = new ArrayList<TaskSpecification>();
		taskSpecs.add(taskSpec);
		
		JobSpecification jobSpec = new JobSpecification(jobLabel, "requirements", taskSpecs);
		
		return addJob(isBrokerStarted, jobSpec, jobId, component, peers);
	}
	
	
	public TestJob addJob(boolean isBrokerStarted, JobSpecification jobSpec, int jobID, BrokerServerModule component,
			List<LocalWorkerProvider> peers) throws Exception {
		
		CommuneLogger newLogger = component.getLogger();
		
		if (newLogger == null) {
			newLogger = EasyMock.createMock(CommuneLogger.class);
		}
		
		EasyMock.reset(newLogger);
		newLogger.info("Trying to add a job.");
		BrokerControl brokerControl = brokerAcceptanceUtil.getBrokerControl(component);
		ObjectDeployment brokerOD = brokerAcceptanceUtil.getBrokerControlDeployment(component);
		
		BrokerControlClient brokerControlClientMock = EasyMock.createMock(BrokerControlClient.class);
		
		DeploymentID clientID = new DeploymentID(new ServiceID("a", "b", "c", "d"));
		AcceptanceTestUtil.publishTestObject(application, clientID, brokerControlClientMock, BrokerControlClient.class);
		
		if (peers != null) {
			for (int i = 0; i < peers.size(); i++) {
				RequestSpecification requestSpec = new RequestSpecification(jobID, jobSpec, 0, "", 0, 0, 0);
				LocalWorkerProvider peer = peers.get(i);
				
				EasyMock.reset(peer);
				
				peer.requestWorkers(RequestSpecMatcher.eqMatcher(requestSpec));
				
				EasyMock.replay(peer);
			}
		}	
		
		component.setLogger(newLogger);

		if (isBrokerStarted) {			
			brokerControlClientMock.operationSucceed(JobAddedMatcher.eqMatcher(jobID));
			newLogger.debug("Job [" + jobID + "] was added, with " + 
					jobSpec.getTaskSpecs().size() + " tasks");
			newLogger.info("Operation add job succeed.");
			
					
		} else {
			brokerControlClientMock.operationSucceed(
					ControlOperationResultMatcher.eqCauseType("Broker control was not started", ModuleNotStartedException.class));
			newLogger.error("Broker control was not started.");
		}
		
		
		EasyMock.replay(newLogger);
		EasyMock.replay(brokerControlClientMock);
		
		AcceptanceTestUtil.setExecutionContext(component, brokerOD, brokerOD.getDeploymentID().getPublicKey());
		
		brokerControl.addJob(brokerControlClientMock, jobSpec);
		
		
		Job job = null;
		if (isBrokerStarted) 
			job = (Job) JobInfo.getInstance().getJob(jobID);
		
		
		EasyMock.verify(brokerControlClientMock);
		EasyMock.verify(newLogger);
		
		if (peers != null) {
			for (int i = 0; i < peers.size(); i++) {
				LocalWorkerProvider providerMock = peers.get(i);
				EasyMock.verify(providerMock);
			}	
		}	
		
		
		return new TestJob(job, jobSpec);
	}

	public DeploymentID createPeerDeploymentID(String peerPublicKey, PeerSpecification peerSpec) {
		String peerName = peerSpec.getAttribute(OurGridSpecificationConstants.ATT_USERNAME);
		String peerServer = peerSpec.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
		
		DeploymentID peerDeploymentID = new DeploymentID(new ContainerID(peerName, peerServer, PeerConstants.MODULE_NAME, peerPublicKey), 
				PeerConstants.LOCAL_WORKER_PROVIDER);
		
		return peerDeploymentID;
	}
	
	public void notifyPeerRecovery(PeerSpecification peerSpec, String peerPublicKey,
			BrokerServerModule component) {
		//Mock logger
		CommuneLogger newLogger = component.getLogger();
		EasyMock.reset(newLogger);
		
		ObjectDeployment brokerOD = brokerAcceptanceUtil.getBrokerControlDeployment(component);
		
		LocalWorkerProvider lwpMock = EasyMock.createMock(LocalWorkerProvider.class);
		DeploymentID deploymentID = createPeerDeploymentID(peerPublicKey, peerSpec);
		AcceptanceTestUtil.publishTestObject(application, deploymentID, lwpMock, LocalWorkerProvider.class);
		
	    newLogger.debug("Peer with deployment id: [" + deploymentID + "] is UP.");
	    
	    // Get peer bound object
		LocalWorkerProviderClientReceiver peerMonitor = getPeerMonitor(component);
		ObjectDeployment peerMonitorOD = getPeerMonitorDeployment(component);
		
		AcceptanceTestUtil.setExecutionContext(component, peerMonitorOD, brokerOD.getDeploymentID().getPublicKey());
		lwpMock.login(getLocalWorkerProviderClient(component));
	    
	    EasyMock.replay(lwpMock);
	    EasyMock.replay(newLogger);
	    
	    peerMonitor.doNotifyRecovery(lwpMock, deploymentID);
	    
	    EasyMock.verify(lwpMock);
	    EasyMock.verify(newLogger);
	}

	public TestJob addJDLJob(boolean isBrokerStarted, JobSpecification jobSpec, int jobID, BrokerServerModule component,
			List<LocalWorkerProvider> peers) throws Exception {
		
		CommuneLogger newLogger = component.getLogger();
		
		if (newLogger == null) {
			newLogger = EasyMock.createMock(CommuneLogger.class);
		}
		
		EasyMock.reset(newLogger);
		
		BrokerControl brokerControl = brokerAcceptanceUtil.getBrokerControl(component);
		ObjectDeployment brokerOD = brokerAcceptanceUtil.getBrokerControlDeployment(component);
		
		BrokerControlClient brokerControlClientMock = EasyMock.createMock(BrokerControlClient.class);
		
		DeploymentID clientID = new DeploymentID(new ServiceID("a", "b", "c", "d"));
		AcceptanceTestUtil.publishTestObject(application, clientID, brokerControlClientMock, BrokerControlClient.class);
		
		if (peers != null) {
			for (int i = 0; i < peers.size(); i++) {
				RequestSpecification requestSpec = new RequestSpecification(jobID, jobSpec, 0, jobSpec.getRequirements(), 0, 0, 0);
				LocalWorkerProvider peer = peers.get(i);
				
				EasyMock.reset(peer);
				
				peer.requestWorkers(RequestSpecMatcher.eqMatcher(requestSpec));
				
				EasyMock.replay(peer);
			}
		}	
		
		component.setLogger(newLogger);
	
		if (isBrokerStarted) {			
			brokerControlClientMock.operationSucceed(JobAddedMatcher.eqMatcher(jobID));
			newLogger.debug("Job [" + jobID + "] was added, with " + 
					jobSpec.getTaskSpecs().size() + " tasks");
					
		} else {
			brokerControlClientMock.operationSucceed(
					ControlOperationResultMatcher.eqCauseType("Broker control was not started", ModuleNotStartedException.class));
			newLogger.error("Broker control was not started.");
		}
	
		EasyMock.replay(newLogger);
		EasyMock.replay(brokerControlClientMock);
		
		AcceptanceTestUtil.setExecutionContext(component, brokerOD, brokerOD.getDeploymentID().getPublicKey());
		
		
		brokerControl.addJob(brokerControlClientMock, jobSpec);
		
		Job job = null;
		if (isBrokerStarted) 
			job = (Job) JobInfo.getInstance().getJob(jobID);
		
		
		EasyMock.verify(brokerControlClientMock);
		EasyMock.verify(newLogger);
		
		if (peers != null) {
			for (int i = 0; i < peers.size(); i++) {
				LocalWorkerProvider providerMock = peers.get(i);
				EasyMock.verify(providerMock);
			}	
		}	
		
		
		return new TestJob(job, jobSpec);
	}
}
