package org.ourgrid.acceptance.util.aggregator;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.AggregatorAcceptanceUtil;
import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.aggregator.communication.receiver.CommunityStatusProviderClientReceiver;
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.common.interfaces.CommunityStatusProvider;
import org.ourgrid.common.interfaces.CommunityStatusProviderClient;
import org.ourgrid.common.interfaces.control.AggregatorControl;
import org.ourgrid.common.interfaces.control.AggregatorControlClient;
import org.ourgrid.common.interfaces.control.DiscoveryServiceControlClient;
import org.ourgrid.matchers.ControlOperationResultMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleAlreadyStartedException;
import br.edu.ufcg.lsd.commune.container.control.ModuleNotStartedException;
import br.edu.ufcg.lsd.commune.container.control.ModuleStoppedException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class T_602_Util extends AggregatorAcceptanceUtil{
	
	private T_601_Util t_601_Util = new T_601_Util(context);
	
	public T_602_Util(ModuleContext context) {
		super(context);
	}

	public AggregatorComponent startAggregatorWithWorngPublicKey(String wrongPublicKey) 
		throws CommuneNetworkException, ProcessorStartException, InterruptedException {
		
		AggregatorComponent component = t_601_Util.createAggregatorComponent();
		return startAggregator(component, wrongPublicKey, false);
	}
	
	public AggregatorComponent startAggregator() 
		throws CommuneNetworkException, ProcessorStartException, InterruptedException {
		
		AggregatorComponent component = t_601_Util.createAggregatorComponent();
		return startAggregator(component, null, false);
	}
	
	public AggregatorComponent startAggregatorAgain(AggregatorComponent component)
		throws CommuneNetworkException, ProcessorStartException {
		
		return startAggregator(component, null, true);
	}
	
	public void stopAggregatorWithoutStartThisComponent() 
		throws CommuneNetworkException, ProcessorStartException, InterruptedException {
		
		AggregatorComponent component = t_601_Util.createAggregatorComponent();
		stopAggregator(component, null, false);
	}
	
	public void stopAggregatorAfterStart(AggregatorComponent component) 
	throws CommuneNetworkException, ProcessorStartException, InterruptedException {
	
		stopAggregator(component, null, true);
	}
	
	/**
	 * Stop Aggregator Component
	 * @param component
	 * @param senderPublicKey
	 * @param isStarted
	 */
	private void stopAggregator(AggregatorComponent component, String senderPublicKey,
			boolean isStarted) {
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		AggregatorControl aggControl = getAggregatorControl(component);
		ObjectDeployment aggOD = getAggregatorDeployment(component);
		
		AggregatorControlClient aggClientMock = EasyMock.createMock(AggregatorControlClient.class);
			
		if (!isStarted) {
			if(aggControl == null) {
				aggClientMock.operationSucceed(
						ControlOperationResultMatcher.eqType(ModuleStoppedException.class));
			} else {
				if (senderPublicKey == null) {
					senderPublicKey = aggOD.getDeploymentID().getPublicKey();
				}	
				aggClientMock.operationSucceed(
						ControlOperationResultMatcher.eqCauseType("Aggregator control was not started", ModuleNotStartedException.class));
			}
		}  else {
			if (senderPublicKey == null) {
				senderPublicKey = aggOD.getDeploymentID().getPublicKey();
				aggClientMock.operationSucceed(ControlOperationResultMatcher.noError());
			} else if(!aggOD.getDeploymentID().getPublicKey().equals(senderPublicKey)) {
				newLogger.warn("An unknown entity tried to stop the Aggregator. " +
						"Only the local modules can perform this operation. " +
						"Unknown entity public key: [" + senderPublicKey + "].");
			}
		}
		
		
		EasyMock.replay(newLogger);
		EasyMock.replay(aggClientMock);
	
		AcceptanceTestUtil.setExecutionContext(component, aggOD, senderPublicKey);
		aggControl.stop(false, false, aggClientMock);
		
		EasyMock.verify(aggClientMock);
		EasyMock.verify(newLogger);
		EasyMock.reset(newLogger);

	}
	
	/**
	 * Start Aggregator Component
	 * @param component
	 * @param senderPublicKey
	 * @param isAlreadyStarted
	 * @return
	 */
	private AggregatorComponent startAggregator(AggregatorComponent component, 
			String senderPublicKey, boolean isAlreadyStarted) {
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		AggregatorControl aggregatorControl = getAggregatorControl(component);		
		ObjectDeployment aggregatorOD = getAggregatorDeployment(component);
		
		DiscoveryServiceControlClient aggregatorClientMock = 
			EasyMock.createMock(DiscoveryServiceControlClient.class);
		
		if (senderPublicKey == null) {
			senderPublicKey = aggregatorOD.getDeploymentID().getPublicKey();
		}
		
		if (isAlreadyStarted) {
			aggregatorClientMock.operationSucceed(ControlOperationResultMatcher
									.eqType(ModuleAlreadyStartedException.class));
		} else {
			if (aggregatorOD.getDeploymentID().getPublicKey().equals(senderPublicKey)) {
				aggregatorClientMock.operationSucceed(ControlOperationResultMatcher.noError());
				newLogger.info("Aggregator has been successfully started.");
			} else {
				newLogger.warn("An unknown entity tried to start the Aggregator. " +
						"Only the local modules can perform this operation. " +
						"Unknown entity public key: [" + senderPublicKey + "].");
			}
		}
		
		EasyMock.replay(newLogger);
		EasyMock.replay(aggregatorClientMock);
		
		AcceptanceTestUtil.setExecutionContext(component, aggregatorOD, senderPublicKey);
		aggregatorControl.start(aggregatorClientMock);
		
		EasyMock.verify(aggregatorClientMock);
		EasyMock.verify(newLogger);
		
		EasyMock.reset(newLogger);
		
		return component;
		
	}
	
	public void CommunityStatusProviderIsDownWarning(AggregatorComponent component) throws 
					CommuneNetworkException, ProcessorStartException, InterruptedException{
		
		CommunityStatusProviderClient communityStatusProviderClient = (CommunityStatusProviderClient) 
				component.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME).getObject();
		CommunityStatusProviderIsDownFailureNotification(communityStatusProviderClient, component, true);
	}
	
	public void CommunityStatusProviderIsDownFailureNotification(
			CommunityStatusProviderClient communityStatusProviderClient, AggregatorComponent component,
			boolean communityIsDown) {
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		CommunityStatusProvider commStatusProvider = EasyMock.createMock(CommunityStatusProvider.class);
		ObjectDeployment aggregatorOD = component.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME);
		
		component.setLogger(newLogger);
		
		if(communityIsDown){
			newLogger.warn(AggregatorControlMessages.
					getCommunityStatusProviderIsDownWarningMessage());
		}else{
			newLogger.info(AggregatorControlMessages.
							getCommunityStatusProviderIsDownInfoMessage());
		}
		
		EasyMock.replay(commStatusProvider);
		EasyMock.replay(newLogger);
		
		AcceptanceTestUtil.publishTestObject(component, aggregatorOD.getDeploymentID(), commStatusProvider, CommunityStatusProvider.class);
		AcceptanceTestUtil.setExecutionContext(component, aggregatorOD, aggregatorOD.getDeploymentID());
		
		((CommunityStatusProviderClientReceiver) communityStatusProviderClient).doNotifyFailure(commStatusProvider, aggregatorOD.getDeploymentID());
		
		EasyMock.verify(newLogger);
		EasyMock.verify(commStatusProvider);
		
		EasyMock.reset(commStatusProvider);
		EasyMock.reset(newLogger);
	}
	



	public void CommunityStatusProviderIsDownWarningAgain(AggregatorComponent component) 
			throws CommuneNetworkException, ProcessorStartException, InterruptedException {
		CommunityStatusProviderIsDownWarning(component);
		CommunityStatusProviderIsDownWarning(component);
		
	}
	
}
