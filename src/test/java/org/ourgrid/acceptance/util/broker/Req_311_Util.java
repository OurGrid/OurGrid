package org.ourgrid.acceptance.util.broker;

import java.util.ArrayList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.business.dao.Request;
import org.ourgrid.broker.business.scheduler.workqueue.JobInfo;
import org.ourgrid.common.BrokerLoginResult;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.job.Job;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.matchers.RequestSpecMatcher;
import org.ourgrid.peer.PeerConstants;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_311_Util extends BrokerAcceptanceUtil {
	
	public Req_311_Util(ModuleContext context) {
		super(context);
	}
	
	public LocalWorkerProvider verifyLogin(BrokerServerModule component, String peerPublicKey, 
			boolean isAlreadyLogged, boolean hasError, String loginResultMessage, TestStub testStub) {
		return verifyLogin(component, peerPublicKey, isAlreadyLogged, hasError, loginResultMessage, testStub, null, null);
	}
	
	public LocalWorkerProvider verifyLogin(BrokerServerModule component, String peerPublicKey, 
			boolean isAlreadyLogged, boolean hasError, String loginResultMessage, TestStub testStub,
			List<JobSpecification> jobs, List<Integer> jobIDs) {
		
		CommuneLogger newLogger = component.getLogger();
		
		EasyMock.reset(newLogger);
		
		LocalWorkerProvider localWorkerProviderMock = (LocalWorkerProvider) testStub.getObject();
		
		BrokerLoginResult result = new BrokerLoginResult(loginResultMessage);
		component.setLogger(newLogger);
		EasyMock.reset(localWorkerProviderMock);
		
		LocalWorkerProviderClient localProviderClient = getLocalWorkerProviderClient(component);
		
		if (hasError) {
			if(isAlreadyLogged){
				newLogger.warn("The peer with public key [" + peerPublicKey + "] tried to send " +
						"a login response, but it is already logged.");
			} else if (peerPublicKey.equals("wrongPublicKey")){
				newLogger.warn("An unknown peer sent a login response. Peer public key: " +
						"[" + peerPublicKey + "]");
			} else if (peerPublicKey.equals("publicKey1")) {
				newLogger.warn("An error ocurred while logging in the peer with public key : " +
						"[" + peerPublicKey + "] - error message");
			}
		}
		
		String maxReplicas = component.getContext().getProperty(BrokerConfiguration.PROP_MAX_REPLICAS);
		String maxFails = component.getContext().getProperty(BrokerConfiguration.PROP_MAX_FAILS);
		
		Integer replicas = Integer.valueOf(maxReplicas);
		Integer fails = Integer.valueOf(maxFails);
		
		if (!hasError && jobs != null) {
			for (int i = 0; i < jobs.size(); i++) {
				Job job = JobInfo.getInstance().getJob(jobIDs.get(i));
				RequestSpecification requestSpec = createRequestSpec(job.getJobId(), job.getSpec(), replicas, fails);
				
				List<Request> requests = new ArrayList<Request>(job.getRequests());
				for (Request request : requests) {
					LocalWorkerProvider lwp = AcceptanceTestUtil.getStub(component, 
							new DeploymentID(request.getPeerID()), LocalWorkerProvider.class);
					
					if (lwp == localWorkerProviderMock) {
						requestSpec = request.getSpecification();
						break;
					}
				}
				
				localWorkerProviderMock.requestWorkers(RequestSpecMatcher.eqMatcher(requestSpec));
			}
		}
		
		EasyMock.replay(localWorkerProviderMock);
		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.notifyRecovery(component, testStub.getDeploymentID());
		
		if (peerPublicKey.equals("wrongPublicKey")){
			DeploymentID correctID = testStub.getDeploymentID();
			DeploymentID wrongID = new DeploymentID(new ContainerID(correctID.getUserName(), correctID.getServerName(), 
					PeerConstants.MODULE_NAME, peerPublicKey), 
					PeerConstants.LOCAL_WORKER_PROVIDER);
			AcceptanceTestUtil.publishTestObject(component, wrongID, localWorkerProviderMock, LocalWorkerProvider.class);
			
			AcceptanceTestUtil.setExecutionContext(component, getLocalWorkerProviderClientDeployment(component), testStub.getDeploymentID());
			localProviderClient.loginSucceed(localWorkerProviderMock, result);
			
		} else {
			testStub.getDeploymentID().setPublicKey(peerPublicKey);
			AcceptanceTestUtil.setExecutionContext(component, getLocalWorkerProviderClientDeployment(component), testStub.getDeploymentID());
			
			localProviderClient.loginSucceed(localWorkerProviderMock, result);
			EasyMock.verify(localWorkerProviderMock);
		}
		

		EasyMock.verify(newLogger);
		
		return localWorkerProviderMock;
	}

}
