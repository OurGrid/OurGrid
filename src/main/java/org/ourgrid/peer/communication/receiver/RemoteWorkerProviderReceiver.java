package org.ourgrid.peer.communication.receiver;



import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.request.RemoteDisposeWorkerRequestTO;
import org.ourgrid.peer.request.RemoteWorkerProviderRequestWorkersRequestTO;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;

/**
 * Performs Remote Worker Provider Receiver actions
 */
@Req("REQ011")
public class RemoteWorkerProviderReceiver implements RemoteWorkerProvider{

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	
	@Req("REQ015")
	public void disposeWorker(ServiceID workerServiceID) {
		
		String consumerPublicKey = serviceManager.getSenderPublicKey();
		
		RemoteDisposeWorkerRequestTO to = new RemoteDisposeWorkerRequestTO();
		to.setConsumerPublicKey(consumerPublicKey);
		if (workerServiceID != null) {
			to.setWorkerAddress(workerServiceID.toString());
			to.setWorkerPublicKey(workerServiceID.getPublicKey());
			to.setWorkerUserAtServer(workerServiceID.getContainerID().getUserAtServer());
		}
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
		
	}

	/**
	 * Validates and executes a remote request.
	 * 
	 * @param workerProviderClient 
	 * @param requestSpec
	 * @param senderPublicKey
	 */
	@Req("REQ011")
	public void requestWorkers(
			@MonitoredBy(PeerConstants.REMOTE_WORKER_PROVIDER)
			RemoteWorkerProviderClient workerProviderClient, RequestSpecification requestSpec) {
		
		RemoteWorkerProviderRequestWorkersRequestTO to = new RemoteWorkerProviderRequestWorkersRequestTO();
		to.setSenderPublicKey(serviceManager.getSenderPublicKey());
		to.setRequestSpec(requestSpec);
		
		DeploymentID workerProviderClientID = serviceManager.getStubDeploymentID(workerProviderClient);
		
		if (workerProviderClientID != null) {
			to.setRemoteWorkerProviderClientAddress(workerProviderClientID.getServiceID().toString());
			to.setRemoteWorkerProviderClientContainerID(workerProviderClientID.getContainerID().toString());
			to.setRemoteWorkerProviderClientPublicKey(workerProviderClientID.getPublicKey());
		}
		
		to.setConsumerDN(CertificationUtils.getCertSubjectDN(serviceManager.getSenderCertPath()));
		to.setMyCertPath(serviceManager.getMyCertPath());
		to.setMyPrivateKey(serviceManager.getContainerContext().getProperty(SignatureProperties.PROP_PRIVATE_KEY));
		to.setVomsURLList(serviceManager.getContainerContext().parseStringListProperty(
				PeerConfiguration.PROP_VOMS_URL));
		to.setUseVomsAuth(serviceManager.getContainerContext().isEnabled(PeerConfiguration.PROP_USE_VOMS));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	@RecoveryNotification
	public void doNotifyRecovery(RemoteWorkerProviderClient monitorable, DeploymentID rwpcDID) {}

	/**
	 * Notifies that the {@link RemoteWorkerProviderClient} has failed
	 * @param monitorable The {@link RemoteWorkerProviderClient} that has failed.
	 * @param rwpcID The DeploymentID of the {@link RemoteWorkerProviderClient} that has failed.
	 */
	@FailureNotification
	public void doNotifyFailure(RemoteWorkerProviderClient monitorable, DeploymentID rwpcDID) {}
}