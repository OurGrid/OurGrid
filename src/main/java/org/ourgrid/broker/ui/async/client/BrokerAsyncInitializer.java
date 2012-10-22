package org.ourgrid.broker.ui.async.client;

import org.ourgrid.broker.ui.async.model.BrokerAsyncUIModel;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class BrokerAsyncInitializer {
	
	private static BrokerAsyncInitializer instance;
	private BrokerAsyncApplicationClient componentClient;
	private BrokerAsyncUIModel model;
	
	
	private BrokerAsyncInitializer() {
	}
	
	public static BrokerAsyncInitializer getInstance() {
		if (instance == null) {
			instance = new BrokerAsyncInitializer();
		}
		return instance;
	}
	
	public void setModel(BrokerAsyncUIModel model) {
		this.model = model;
	}

	public BrokerAsyncApplicationClient initComponentClient(ModuleContext context, final BrokerAsyncUIModel model) 
		throws CommuneNetworkException, ProcessorStartException {
		
		stopComponentClient();
		
		this.model = model;
		this.componentClient = new BrokerAsyncApplicationClient(context, model, new ConnectionListener() {
			
			public void connectionFailed(Exception e) {
				model.brokerInitedFailed();
			}
			
			public void connected() {
				model.brokerStopped();
				
			}

			public void disconnected() {
				if(model.isBrokerEditing()){
					model.brokerEditing();
				} else {
					model.brokerInited();
				}
			}

			public void reconnected() {
				if(model.isBrokerUp()){
					model.brokerRestarted();
				}else{
					model.brokerStopped();
				}
			}

			public void reconnectedFailed() {
				model.brokerInitedFailed();
				
			}
		});
		return this.componentClient;
	}
	
	public void stopComponentClient() {
		if (this.componentClient != null) {
			try {
				this.componentClient.stop();
			} catch (CommuneNetworkException e) {
				e.printStackTrace();
			}
		}
	}

	public BrokerAsyncUIModel getModel() {
		if (this.model == null) {
			this.model = new BrokerAsyncUIModel();
		}
		
		return model;
	}

	public BrokerAsyncApplicationClient getComponentClient() {
		return componentClient;
	}
}
