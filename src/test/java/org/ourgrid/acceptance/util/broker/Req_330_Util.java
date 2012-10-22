package org.ourgrid.acceptance.util.broker;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.communication.receiver.LocalWorkerProviderClientReceiver;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.matchers.MessageStartsWithMatcher;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_330_Util extends BrokerAcceptanceUtil {

	public Req_330_Util(ModuleContext context) {
		super(context);
	}
	
	public DeploymentID createWorkerDeploymentID(String workerPublicKey, WorkerSpecification workerSpec) {
		String workerName = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_USERNAME);
		String workerServer = workerSpec.getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
		
		DeploymentID workerDeploymentID = new DeploymentID(new ContainerID(workerName, workerServer, WorkerConstants.MODULE_NAME, workerPublicKey), 
				WorkerConstants.WORKER);
		
		return workerDeploymentID;
	}
	
	public void notifyWorkerFailure(String workerPublicKey, WorkerSpecification workerSpec, BrokerServerModule component, 
			LocalWorkerProvider lwp, boolean isDisposed, DeploymentID deploymentID, boolean hasJobs, GridProcessHandle handle){
		
		//Mock logger
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		ObjectDeployment bcOD = getBrokerControlDeployment(component);
		
		Worker workerMock = EasyMock.createMock(Worker.class);
		
		EasyMock.reset(lwp);
		
		if(!isDisposed){
			lwp.disposeWorker(deploymentID.getServiceID());
			
			newLogger.debug("Worker dispose: " + deploymentID.getServiceID());
			
			if (hasJobs) {
				newLogger.debug(MessageStartsWithMatcher.eqMatcher(
						"Grid process FAILED " + handle + ". Job ended: false"));
			}

			newLogger.info("Stub to be released: " + deploymentID.getServiceID());
		}
		else{
			newLogger.warn("A peer notified a worker [" + deploymentID.getContainerID() + "] failure, " +
					"but it is not working to this broker.");
		}
	    
	    // Get peer bound object
	    LocalWorkerProviderClientReceiver workerMonitor = getWorkerMonitor(component);
	    ObjectDeployment wmOD = getWorkerMonitorDeployment(component);
		AcceptanceTestUtil.setExecutionContext(component, wmOD, bcOD.getDeploymentID());
		
	    EasyMock.replay(workerMock);
	    EasyMock.replay(newLogger);
	    
	    workerMonitor.doNotifyFailure(workerMock, deploymentID);
	    
	    EasyMock.verify(workerMock);
	    EasyMock.verify(newLogger);
	    
	    component.setLogger(oldLogger);
	}
	
	public void notifyWorkerFailure(String workerPublicKey, WorkerSpecification workerSpec, BrokerServerModule component, 
			LocalWorkerProvider lwp, boolean isDisposed, DeploymentID deploymentID){
		notifyWorkerFailure(workerPublicKey, workerSpec, component, lwp, isDisposed, deploymentID, false, null);
	}
	
	public void notifyWorkerRecovery(BrokerServerModule component, 
			DeploymentID deploymentID){
		
		ObjectDeployment bcOD = getBrokerControlDeployment(component);
		
		Worker workerMock = EasyMock.createMock(Worker.class);
		
	    // Get peer bound object
	    LocalWorkerProviderClientReceiver workerMonitor = getWorkerMonitor(component);
	    ObjectDeployment wmOD = getWorkerMonitorDeployment(component);
		AcceptanceTestUtil.setExecutionContext(component, wmOD, bcOD.getDeploymentID());
	    
	    EasyMock.replay(workerMock);
	    
	    workerMonitor.doNotifyRecovery(workerMock, deploymentID);
	    //AcceptanceTestUtil.notifyRecovery(component, deploymentID);
	    
	    EasyMock.verify(workerMock);
	}
}
